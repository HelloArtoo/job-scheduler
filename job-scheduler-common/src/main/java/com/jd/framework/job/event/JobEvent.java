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

/**
 * 
 * 作业事件
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */
public interface JobEvent {
	/**
	 * 获取作业名称.
	 * 
	 * @return 作业名称
	 */
	String getJobName();
}
