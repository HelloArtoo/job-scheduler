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

import com.jd.framework.job.core.api.listener.AbstractOneOffJobListener;
import com.jd.framework.job.executor.context.SegmentContexts;

public class TestDistributeOnceJobListener extends AbstractOneOffJobListener {

	private final ScheduleJobListenerCaller caller;

	public TestDistributeOnceJobListener(final ScheduleJobListenerCaller caller) {
		super(1L, 1L);
		this.caller = caller;
	}

	@Override
	public void doBeforeJobExecutedAtLastStarted(final SegmentContexts segmentContexts) {
		caller.before();
	}

	@Override
	public void doAfterJobExecutedAtLastCompleted(final SegmentContexts segmentContexts) {
		caller.after();
	}

}
