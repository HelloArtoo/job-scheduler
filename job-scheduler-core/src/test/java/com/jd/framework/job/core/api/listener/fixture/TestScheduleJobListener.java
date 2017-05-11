/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.core.api.listener.fixture;

import lombok.RequiredArgsConstructor;

import com.jd.framework.job.core.api.listener.ScheduleJobListener;
import com.jd.framework.job.executor.context.SegmentContexts;

@RequiredArgsConstructor
public class TestScheduleJobListener implements ScheduleJobListener {

	private final ScheduleJobListenerCaller caller;

	@Override
	public void beforeJobExecuted(final SegmentContexts segmentContexts) {
		caller.before();
	}

	@Override
	public void afterJobExecuted(final SegmentContexts segmentContexts) {
		caller.after();
	}
}
