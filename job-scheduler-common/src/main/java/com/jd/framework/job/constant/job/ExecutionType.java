/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.constant.job;

/**
 * 
 * 执行类型枚举
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */
public enum ExecutionType {
	/**
	 * 准备执行的任务.
	 */
	READY,

	/**
	 * 失效转移的任务.
	 */
	FAILOVER
}
