/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.event;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.jd.framework.job.event.type.JobExecutionEvent;
import com.jd.framework.job.event.type.JobStatusTraceEvent;

/**
 * 
 * 作业事件监听
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */
public interface JobEventListener extends JobEventIdentity {

	/**
	 * 作业执行事件监听执行.
	 * 
	 * @param jobExecutionEvent
	 *            作业执行事件
	 */
	@Subscribe
	@AllowConcurrentEvents
	void listen(JobExecutionEvent jobExecutionEvent);

	/**
	 * 作业状态痕迹事件监听执行.
	 * 
	 * @param jobStatusTraceEvent
	 *            作业状态痕迹事件
	 */
	@Subscribe
	@AllowConcurrentEvents
	void listen(JobStatusTraceEvent jobStatusTraceEvent);
}
