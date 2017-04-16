/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.config;

import com.jd.framework.job.config.core.JobCoreConfiguration;
import com.jd.framework.job.constant.job.JobType;

/**
 * 
 * 作业类型配置信息
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-4
 */
public interface JobTypeConfiguration {

	/**
	 * 获取作业类型.
	 * 
	 * @return 作业类型
	 */
	JobType getJobType();

	/**
	 * 获取作业实现类名称.
	 * 
	 * @return 作业实现类名称
	 */
	String getJobClass();

	/**
	 * 获取作业核心配置.
	 * 
	 * @return 作业核心配置
	 */
	JobCoreConfiguration getCoreConfig();
}
