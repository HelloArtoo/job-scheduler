/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.event.fixture;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.jd.framework.job.event.JobEventListener;
import com.jd.framework.job.event.type.JobExecutionEvent;
import com.jd.framework.job.event.type.JobStatusTraceEvent;

@RequiredArgsConstructor
public final class TestJobEventListener extends TestJobEventIdentity implements JobEventListener {

	@Getter
	private static volatile boolean executionEventCalled;

	private final JobEventCaller jobEventCaller;

	@Override
	@Subscribe
	@AllowConcurrentEvents
	public void listen(JobExecutionEvent jobExecutionEvent) {
		jobEventCaller.call();
		executionEventCalled = true;
	}

	@Override
	@Subscribe
	@AllowConcurrentEvents
	public void listen(JobStatusTraceEvent jobStatusTraceEvent) {
		jobEventCaller.call();
	}

	public static void reset() {
		executionEventCalled = false;
	}
}
