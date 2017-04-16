/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.core.internal.executor.quartz;

import lombok.RequiredArgsConstructor;

import org.quartz.Trigger;
import org.quartz.listeners.TriggerListenerSupport;

import com.jd.framework.job.core.internal.service.ExecutionService;
import com.jd.framework.job.core.internal.service.SegmentService;

/**
 * 
 * 作业出发监听,Quartz
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */
@RequiredArgsConstructor
public final class JobTriggerListener extends TriggerListenerSupport {

	private final ExecutionService executionService;

	private final SegmentService segmentService;

	@Override
	public String getName() {
		return "JobTriggerListener";
	}

	@Override
	public void triggerMisfired(final Trigger trigger) {
		executionService.setMisfire(segmentService.getLocalHostSegmentItems());
	}
}
