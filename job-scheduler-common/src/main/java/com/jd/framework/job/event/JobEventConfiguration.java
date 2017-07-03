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

import com.jd.framework.job.exception.JobEventListenerConfigException;

/**
 * 
 * 作业事件标识接口
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */
public interface JobEventConfiguration extends JobEventIdentity {

	/**
	 * 创建作业事件监听器.
	 * 
	 * @return 作业事件监听器.
	 * @throws JobEventListenerConfigException
	 *             作业事件监听器配置异常
	 */
	JobEventListener createJobEventListener() throws JobEventListenerConfigException;
}
