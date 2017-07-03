/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.executor;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import com.jd.framework.job.api.SegmentContext;
import com.jd.framework.job.config.JobRootConfiguration;
import com.jd.framework.job.event.type.JobExecutionEvent;
import com.jd.framework.job.event.type.JobStatusTraceEvent.State;
import com.jd.framework.job.exception.JobExecutionEnvironmentException;
import com.jd.framework.job.exception.JobSystemException;
import com.jd.framework.job.executor.context.SegmentContexts;
import com.jd.framework.job.executor.facade.JobFacade;
import com.jd.framework.job.executor.handler.JobProperties;
import com.jd.framework.job.executor.handler.exception.JobExceptionHandler;
import com.jd.framework.job.executor.handler.threadpool.ExecutorServiceHandler;
import com.jd.framework.job.executor.registry.ExecutorServiceHandlerRegistry;
import com.jd.framework.job.utils.exception.ExceptionUtil;

/**
 * 
 * 核心作业执行逻辑，核心执行器
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-15
 */
@Slf4j
public abstract class AbstractJobExecutor {

	@Getter(AccessLevel.PROTECTED)
	private final JobFacade jobFacade;

	@Getter(AccessLevel.PROTECTED)
	private final JobRootConfiguration jobRootConfig;

	private final String jobName;

	private final ExecutorService executorService;

	private final JobExceptionHandler jobExceptionHandler;

	private final Map<Integer, String> itemErrorMessages;

	protected AbstractJobExecutor(final JobFacade jobFacade) {
		this.jobFacade = jobFacade;
		jobRootConfig = jobFacade.loadJobRootConfiguration(true);
		jobName = jobRootConfig.getTypeConfig().getCoreConfig().getJobName();
		executorService = ExecutorServiceHandlerRegistry.getExecutorServiceHandler(jobName,
				(ExecutorServiceHandler) getHandler(JobProperties.JobPropertiesEnum.EXECUTOR_SERVICE_HANDLER));
		jobExceptionHandler = (JobExceptionHandler) getHandler(JobProperties.JobPropertiesEnum.JOB_EXCEPTION_HANDLER);
		itemErrorMessages = new ConcurrentHashMap<>(jobRootConfig.getTypeConfig().getCoreConfig()
				.getSegmentTotalCount(), 1);
	}

	/**
	 * 执行作业.
	 */
	public final void execute() {
		try {
			jobFacade.checkJobExecutionEnvironment();
		} catch (final JobExecutionEnvironmentException cause) {
			jobExceptionHandler.handleException(jobName, cause);
		}
		SegmentContexts segmentContexts = jobFacade.getSegmentContexts();
		if (segmentContexts.isAllowSendJobEvent()) {
			jobFacade.postJobStatusTraceEvent(segmentContexts.getTaskId(), State.TASK_STAGING,
					String.format("Job '%s' execute begin.", jobName));
		}
		if (jobFacade.misfireIfNecessary(segmentContexts.getSegmentItemParameters().keySet())) {
			if (segmentContexts.isAllowSendJobEvent()) {
				jobFacade
						.postJobStatusTraceEvent(
								segmentContexts.getTaskId(),
								State.TASK_FINISHED,
								String.format(
										"Previous job '%s' - segmentItems '%s' is still running, misfired job will start after previous job completed.",
										jobName, segmentContexts.getSegmentItemParameters().keySet()));
			}
			return;
		}
		jobFacade.cleanPreviousExecutionInfo();
		try {
			jobFacade.beforeJobExecuted(segmentContexts);
			// CHECKSTYLE:OFF
		} catch (final Throwable cause) {
			// CHECKSTYLE:ON
			jobExceptionHandler.handleException(jobName, cause);
		}
		execute(segmentContexts, JobExecutionEvent.ExecutionSource.NORMAL_TRIGGER);
		// 执行错过的任务
		while (jobFacade.isExecuteMisfired(segmentContexts.getSegmentItemParameters().keySet())) {
			jobFacade.clearMisfire(segmentContexts.getSegmentItemParameters().keySet());
			execute(segmentContexts, JobExecutionEvent.ExecutionSource.MISFIRE);
		}
		jobFacade.failoverIfNecessary();
		try {
			jobFacade.afterJobExecuted(segmentContexts);
			// CHECKSTYLE:OFF
		} catch (final Throwable cause) {
			// CHECKSTYLE:ON
			jobExceptionHandler.handleException(jobName, cause);
		}
	}

	private void execute(final SegmentContexts segmentContexts, final JobExecutionEvent.ExecutionSource executionSource) {
		if (segmentContexts.getSegmentItemParameters().isEmpty()) {
			if (segmentContexts.isAllowSendJobEvent()) {
				jobFacade.postJobStatusTraceEvent(segmentContexts.getTaskId(), State.TASK_FINISHED,
						String.format("Segment item for job '%s' is empty.", jobName));
			}
			return;
		}
		jobFacade.registerJobBegin(segmentContexts);
		String taskId = segmentContexts.getTaskId();
		if (segmentContexts.isAllowSendJobEvent()) {
			jobFacade.postJobStatusTraceEvent(taskId, State.TASK_RUNNING, "");
		}
		try {
			process(segmentContexts, executionSource);
		} finally {
			// TODO 考虑增加作业失败的状态，并且考虑如何处理作业失败的整体回路
			jobFacade.registerJobCompleted(segmentContexts);
			if (itemErrorMessages.isEmpty()) {
				if (segmentContexts.isAllowSendJobEvent()) {
					jobFacade.postJobStatusTraceEvent(taskId, State.TASK_FINISHED, "");
				}
			} else {
				if (segmentContexts.isAllowSendJobEvent()) {
					jobFacade.postJobStatusTraceEvent(taskId, State.TASK_ERROR, itemErrorMessages.toString());
				}
			}
		}
	}

	private void process(final SegmentContexts segmentContexts, final JobExecutionEvent.ExecutionSource executionSource) {
		Collection<Integer> items = segmentContexts.getSegmentItemParameters().keySet();
		if (1 == items.size()) {
			int item = segmentContexts.getSegmentItemParameters().keySet().iterator().next();
			JobExecutionEvent jobExecutionEvent = new JobExecutionEvent(segmentContexts.getTaskId(), jobName,
					executionSource, item);
			process(segmentContexts, item, jobExecutionEvent);
			return;
		}
		final CountDownLatch latch = new CountDownLatch(items.size());
		for (final int each : items) {
			final JobExecutionEvent jobExecutionEvent = new JobExecutionEvent(segmentContexts.getTaskId(), jobName,
					executionSource, each);
			if (executorService.isShutdown()) {
				return;
			}
			executorService.submit(new Runnable() {

				@Override
				public void run() {
					try {
						process(segmentContexts, each, jobExecutionEvent);
					} finally {
						latch.countDown();
					}
				}
			});
		}
		try {
			latch.await();
		} catch (final InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}

	private void process(final SegmentContexts segmentContexts, final int item, final JobExecutionEvent startEvent) {
		if (segmentContexts.isAllowSendJobEvent()) {
			jobFacade.postJobExecutionEvent(startEvent);
		}
		log.trace("Job '{}' executing, item is: '{}'.", jobName, item);
		JobExecutionEvent completeEvent = null;
		try {
			process(new SegmentContext(segmentContexts, item));
			completeEvent = startEvent.executionSuccess();
			log.trace("Job '{}' executed, item is: '{}'.", jobName, item);
			// CHECKSTYLE:OFF
		} catch (final Throwable cause) {
			// CHECKSTYLE:ON
			completeEvent = startEvent.executionFailure(cause);
			itemErrorMessages.put(item, ExceptionUtil.transform(cause));
			jobExceptionHandler.handleException(jobName, cause);
		} finally {
			if (segmentContexts.isAllowSendJobEvent()) {
				jobFacade.postJobExecutionEvent(completeEvent);
			}
		}
	}

	private Object getHandler(final JobProperties.JobPropertiesEnum jobPropertiesEnum) {
		String handlerClassName = jobRootConfig.getTypeConfig().getCoreConfig().getJobProperties()
				.get(jobPropertiesEnum);
		try {
			Class<?> handlerClass = Class.forName(handlerClassName);
			if (jobPropertiesEnum.getClassType().isAssignableFrom(handlerClass)) {
				return handlerClass.newInstance();
			}
			return getDefaultHandler(jobPropertiesEnum, handlerClassName);
		} catch (final ReflectiveOperationException ex) {
			return getDefaultHandler(jobPropertiesEnum, handlerClassName);
		}
	}

	private Object getDefaultHandler(final JobProperties.JobPropertiesEnum jobPropertiesEnum,
			final String handlerClassName) {
		log.warn("Cannot instantiation class '{}', use default '{}' class.", handlerClassName,
				jobPropertiesEnum.getKey());
		try {
			return Class.forName(jobPropertiesEnum.getDefaultValue()).newInstance();
		} catch (final ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			throw new JobSystemException(e);
		}
	}

	/**
	 * 子类实现具体处理逻辑
	 * 
	 * @param segmentContext 分段上下文
	 */
	protected abstract void process(SegmentContext segmentContext);
}
