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
 * 作业事件标识
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */
public interface JobEventIdentity {
	/**
	 * 获取作业事件标识.
	 * 
	 * @return 作业事件标识
	 */
	String getIdentity();
}
