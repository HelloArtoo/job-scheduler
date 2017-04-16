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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import com.google.common.base.Strings;
import com.jd.framework.job.core.api.strategy.impl.AverageAllocationJobSegmentStrategy;
import com.jd.framework.job.exception.JobConfigurationException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JobSegmentStrategyFactory {

	/**
	 * 获取 作业分片策略实例.
	 * 
	 * @param jobSegmentStrategyClassName
	 *            作业分片策略类名
	 * @return 作业分片策略实例
	 */
	public static JobSegmentStrategy getStrategy(final String jobSegmentStrategyClassName) {
		if (Strings.isNullOrEmpty(jobSegmentStrategyClassName)) {
			return new AverageAllocationJobSegmentStrategy();
		}
		try {
			Class<?> jobSegmentStrategyClass = Class.forName(jobSegmentStrategyClassName);
			if (!JobSegmentStrategy.class.isAssignableFrom(jobSegmentStrategyClass)) {
				throw new JobConfigurationException("Class '%s' is not job strategy class", jobSegmentStrategyClassName);
			}
			return (JobSegmentStrategy) jobSegmentStrategyClass.newInstance();
		} catch (final ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
			throw new JobConfigurationException("Segment strategy class '%s' config error, message details are '%s'",
					jobSegmentStrategyClassName, ex.getMessage());
		}
	}
}
