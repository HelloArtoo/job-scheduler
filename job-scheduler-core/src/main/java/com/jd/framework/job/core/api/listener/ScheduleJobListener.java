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

import com.jd.framework.job.executor.context.SegmentContexts;

/**
 * 
 * 作业监听接口
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */
public interface ScheduleJobListener {
	/**
	 * 作业执行前的执行的方法.
	 * 
	 * @param segmentContexts
	 *            分段上下文
	 */
	void beforeJobExecuted(final SegmentContexts segmentContexts);

	/**
	 * 作业执行后的执行的方法.
	 * 
	 * @param segmentContexts
	 *            分段上下文
	 */
	void afterJobExecuted(final SegmentContexts segmentContexts);
}
