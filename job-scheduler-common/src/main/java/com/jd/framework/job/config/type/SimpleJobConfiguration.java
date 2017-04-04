/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.config.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.jd.framework.job.config.JobTypeConfiguration;
import com.jd.framework.job.config.core.JobCoreConfiguration;
import com.jd.framework.job.constant.JobType;

/**
 * 
 * Simple Job 配置
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-4
 */
@RequiredArgsConstructor
@Getter
public final class SimpleJobConfiguration implements JobTypeConfiguration {

	/** 核心配置 */
	private final JobCoreConfiguration coreConfig;
	/** Job类型 */
	private final JobType jobType = JobType.SIMPLE;
	/** job对应的class */
	private final String jobClass;
}
