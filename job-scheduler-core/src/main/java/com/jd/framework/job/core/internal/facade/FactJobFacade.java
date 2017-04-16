/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.core.internal.facade;

import java.util.Collection;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import com.google.common.base.Strings;
import com.jd.framework.job.config.type.FlowJobConfiguration;
import com.jd.framework.job.core.api.listener.ScheduleJobListener;
import com.jd.framework.job.core.config.FactJobConfiguration;
import com.jd.framework.job.core.internal.service.ConfigService;
import com.jd.framework.job.core.internal.service.ExecutionContextService;
import com.jd.framework.job.core.internal.service.ExecutionService;
import com.jd.framework.job.core.internal.service.FailoverService;
import com.jd.framework.job.core.internal.service.SegmentService;
import com.jd.framework.job.core.internal.service.ServerService;
import com.jd.framework.job.event.JobEventBus;
import com.jd.framework.job.event.type.JobExecutionEvent;
import com.jd.framework.job.event.type.JobStatusTraceEvent;
import com.jd.framework.job.event.type.JobStatusTraceEvent.Source;
import com.jd.framework.job.event.type.JobStatusTraceEvent.State;
import com.jd.framework.job.exception.JobExecutionEnvironmentException;
import com.jd.framework.job.executor.context.SegmentContexts;
import com.jd.framework.job.executor.context.TaskContext;
import com.jd.framework.job.executor.facade.JobFacade;
import com.jd.framework.job.regcenter.api.CoordinatorRegistryCenter;

/**
 * 
 * This class is used for ...
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */
@Slf4j
public class FactJobFacade implements JobFacade {

	private final ConfigService configService;

	private final SegmentService segmentService;

	private final ServerService serverService;

	private final ExecutionContextService executionContextService;

	private final ExecutionService executionService;

	private final FailoverService failoverService;

	private final List<ScheduleJobListener> scheduleJobListeners;

	private final JobEventBus jobEventBus;

	public FactJobFacade(final CoordinatorRegistryCenter regCenter, final String jobName,
			final List<ScheduleJobListener> scheduleJobListeners, final JobEventBus jobEventBus) {
		configService = new ConfigService(regCenter, jobName);
		segmentService = new SegmentService(regCenter, jobName);
		serverService = new ServerService(regCenter, jobName);
		executionContextService = new ExecutionContextService(regCenter, jobName);
		executionService = new ExecutionService(regCenter, jobName);
		failoverService = new FailoverService(regCenter, jobName);
		this.scheduleJobListeners = scheduleJobListeners;
		this.jobEventBus = jobEventBus;
	}

	@Override
	public FactJobConfiguration loadJobRootConfiguration(final boolean fromCache) {
		return configService.load(fromCache);
	}

	@Override
	public void checkJobExecutionEnvironment() throws JobExecutionEnvironmentException {
		configService.checkMaxTimeDiffSecondsTolerable();
	}

	@Override
	public void failoverIfNecessary() {
		if (configService.load(true).isFailover() && !serverService.isJobPausedManually()) {
			failoverService.failoverIfNecessary();
		}
	}

	@Override
	public void registerJobBegin(final SegmentContexts segmentContexts) {
		executionService.registerJobBegin(segmentContexts);
	}

	@Override
	public void registerJobCompleted(final SegmentContexts segmentContexts) {
		executionService.registerJobCompleted(segmentContexts);
		if (configService.load(true).isFailover()) {
			failoverService.updateFailoverComplete(segmentContexts.getSegmentItemParameters().keySet());
		}
	}

	public SegmentContexts getSegmentContexts() {
		boolean isFailover = configService.load(true).isFailover();
		if (isFailover) {
			List<Integer> failoverSegmentItems = failoverService.getLocalHostFailoverItems();
			if (!failoverSegmentItems.isEmpty()) {
				return executionContextService.getJobSegmentContext(failoverSegmentItems);
			}
		}
		segmentService.segmentIfNecessary();
		List<Integer> segmentItems = segmentService.getLocalHostSegmentItems();
		if (isFailover) {
			segmentItems.removeAll(failoverService.getLocalHostTakeOffItems());
		}
		return executionContextService.getJobSegmentContext(segmentItems);
	}

	@Override
	public boolean misfireIfNecessary(final Collection<Integer> segmentItems) {
		return executionService.misfireIfNecessary(segmentItems);
	}

	@Override
	public void clearMisfire(final Collection<Integer> segmentItems) {
		executionService.clearMisfire(segmentItems);
	}

	@Override
	public boolean isExecuteMisfired(final Collection<Integer> segmentItems) {
		return isEligibleForJobRunning() && configService.load(true).getTypeConfig().getCoreConfig().isMisfire()
				&& !executionService.getMisfiredJobItems(segmentItems).isEmpty();
	}

	@Override
	public boolean isEligibleForJobRunning() {
		FactJobConfiguration factJobConfig = configService.load(true);
		if (factJobConfig.getTypeConfig() instanceof FlowJobConfiguration) {
			return !serverService.isJobPausedManually() && !segmentService.isNeedSegment()
					&& ((FlowJobConfiguration) factJobConfig.getTypeConfig()).isStreamingProcess();
		}
		return !serverService.isJobPausedManually() && !segmentService.isNeedSegment();
	}

	@Override
	public boolean isNeedSegment() {
		return segmentService.isNeedSegment();
	}

	@Override
	public void cleanPreviousExecutionInfo() {
		executionService.cleanPreviousExecutionInfo();
	}

	@Override
	public void beforeJobExecuted(final SegmentContexts segmentContexts) {
		for (ScheduleJobListener each : scheduleJobListeners) {
			each.beforeJobExecuted(segmentContexts);
		}
	}

	@Override
	public void afterJobExecuted(final SegmentContexts segmentContexts) {
		for (ScheduleJobListener each : scheduleJobListeners) {
			each.afterJobExecuted(segmentContexts);
		}
	}

	@Override
	public void postJobExecutionEvent(final JobExecutionEvent jobExecutionEvent) {
		jobEventBus.post(jobExecutionEvent);
	}

	@Override
	public void postJobStatusTraceEvent(final String taskId, final State state, final String message) {
		TaskContext taskContext = TaskContext.from(taskId);
		jobEventBus.post(new JobStatusTraceEvent(taskContext.getMetaInfo().getJobName(), taskContext.getId(),
				taskContext.getSlaveId(), Source.LITE_EXECUTOR, taskContext.getType(), taskContext.getMetaInfo()
						.getSegmentItems().toString(), state, message));
		if (!Strings.isNullOrEmpty(message)) {
			log.trace(message);
		}
	}
}
