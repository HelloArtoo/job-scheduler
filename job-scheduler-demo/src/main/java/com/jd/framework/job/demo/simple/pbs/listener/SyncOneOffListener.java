/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.demo.simple.pbs.listener;

import com.jd.framework.job.core.api.listener.AbstractOneOffJobListener;
import com.jd.framework.job.executor.context.SegmentContexts;

public class SyncOneOffListener extends AbstractOneOffJobListener {
	private final long startedTimeoutMilliseconds;

	private final long completedTimeoutMilliseconds;

	public SyncOneOffListener(long startedTimeoutMilliseconds, long completedTimeoutMilliseconds) {
		super(startedTimeoutMilliseconds, completedTimeoutMilliseconds);
		this.startedTimeoutMilliseconds = startedTimeoutMilliseconds;
		this.completedTimeoutMilliseconds = completedTimeoutMilliseconds;
	}

	@Override
	public void doBeforeJobExecutedAtLastStarted(final SegmentContexts segmentContexts) {
		System.out.println("Job-Scheduler: doBeforeJobExecutedAtLastStarted:" + segmentContexts);

	}

	@Override
	public void doAfterJobExecutedAtLastCompleted(SegmentContexts segmentContexts) {
		System.out.println("Job-Scheduler: doAfterJobExecutedAtLastCompleted:" + startedTimeoutMilliseconds + ","
				+ completedTimeoutMilliseconds);

	}

}
