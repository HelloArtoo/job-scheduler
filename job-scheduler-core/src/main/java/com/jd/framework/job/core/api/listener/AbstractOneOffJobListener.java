/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.core.api.listener;

import lombok.Setter;

import com.jd.framework.job.core.internal.service.GuaranteeService;
import com.jd.framework.job.exception.JobSystemException;
import com.jd.framework.job.executor.context.SegmentContexts;
import com.jd.framework.job.utils.env.TimeService;

/**
 * 
 * 监听器:在分布式作业中只执行一次
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */
public abstract class AbstractOneOffJobListener implements ScheduleJobListener {

	private final long startedTimeoutMilliseconds;

	private final Object startedWait = new Object();

	private final long completedTimeoutMilliseconds;

	private final Object completedWait = new Object();

	@Setter
	private GuaranteeService guaranteeService;

	private TimeService timeService = new TimeService();

	public AbstractOneOffJobListener(final long startedTimeoutMilliseconds, final long completedTimeoutMilliseconds) {
		if (startedTimeoutMilliseconds <= 0L) {
			this.startedTimeoutMilliseconds = Long.MAX_VALUE;
		} else {
			this.startedTimeoutMilliseconds = startedTimeoutMilliseconds;
		}
		if (completedTimeoutMilliseconds <= 0L) {
			this.completedTimeoutMilliseconds = Long.MAX_VALUE;
		} else {
			this.completedTimeoutMilliseconds = completedTimeoutMilliseconds;
		}
	}

	@Override
	public final void beforeJobExecuted(final SegmentContexts segmentContexts) {
		guaranteeService.registerStart(segmentContexts.getSegmentItemParameters().keySet());
		if (guaranteeService.isAllStarted()) {
			doBeforeJobExecutedAtLastStarted(segmentContexts);
			guaranteeService.clearAllStartedInfo();
			return;
		}
		long before = timeService.getCurrentMillis();
		try {
			synchronized (startedWait) {
				startedWait.wait(startedTimeoutMilliseconds);
			}
		} catch (final InterruptedException ex) {
			Thread.interrupted();
		}
		if (timeService.getCurrentMillis() - before >= startedTimeoutMilliseconds) {
			guaranteeService.clearAllStartedInfo();
			handleTimeout(startedTimeoutMilliseconds);
		}
	}

	@Override
	public final void afterJobExecuted(final SegmentContexts segmentContexts) {
		guaranteeService.registerComplete(segmentContexts.getSegmentItemParameters().keySet());
		if (guaranteeService.isAllCompleted()) {
			doAfterJobExecutedAtLastCompleted(segmentContexts);
			guaranteeService.clearAllCompletedInfo();
			return;
		}
		long before = timeService.getCurrentMillis();
		try {
			synchronized (completedWait) {
				completedWait.wait(completedTimeoutMilliseconds);
			}
		} catch (final InterruptedException ex) {
			Thread.interrupted();
		}
		if (timeService.getCurrentMillis() - before >= completedTimeoutMilliseconds) {
			guaranteeService.clearAllCompletedInfo();
			handleTimeout(completedTimeoutMilliseconds);
		}
	}

	private void handleTimeout(final long timeoutMilliseconds) {
		throw new JobSystemException("Job timeout. timeout mills is %s.", timeoutMilliseconds);
	}

	/**
	 * 分布式环境中最后一个作业执行前的执行的方法.
	 * 
	 * @param segmentContexts
	 *            分段上下文
	 */
	public abstract void doBeforeJobExecutedAtLastStarted(final SegmentContexts segmentContexts);

	/**
	 * 分布式环境中最后一个作业执行后的执行的方法.
	 * 
	 * @param segmentContexts
	 *            分段上下文
	 */
	public abstract void doAfterJobExecutedAtLastCompleted(final SegmentContexts segmentContexts);

	/**
	 * 通知任务开始.
	 */
	public void notifyWaitingTaskStart() {
		synchronized (startedWait) {
			startedWait.notifyAll();
		}
	}

	/**
	 * 通知任务结束.
	 */
	public void notifyWaitingTaskComplete() {
		synchronized (completedWait) {
			completedWait.notifyAll();
		}
	}
}
