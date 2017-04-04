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

/**
 * 
 * 获取作业配置信息
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-4
 */
public interface JobRootConfiguration {

	/**
	 * 获取作业类型配置.
	 * 
	 * @return 作业类型配置
	 */
	JobTypeConfiguration getTypeConfig();
}
