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

import com.jd.framework.job.core.api.listener.ScheduleJobListener;
import com.jd.framework.job.executor.context.SegmentContexts;

public class SyncListener implements ScheduleJobListener {

	@Override
	public void beforeJobExecuted(SegmentContexts segmentContexts) {
		System.out.println("Job-Scheduler: beforeJobExecuted:" + segmentContexts.getJobName());
	}

	@Override
	public void afterJobExecuted(SegmentContexts segmentContexts) {
		System.out.println("Job-Scheduler: afterJobExecuted:" + segmentContexts.getJobName());
	}

}
