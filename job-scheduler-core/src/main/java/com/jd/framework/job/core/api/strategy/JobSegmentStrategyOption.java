/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.core.api.strategy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 分段策略项
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */
@Getter
@RequiredArgsConstructor
public class JobSegmentStrategyOption {

	/**
	 * 作业名称.
	 */
	private final String jobName;

	/**
	 * 作业分段总数.
	 */
	private final int segmentTotalCount;
}
