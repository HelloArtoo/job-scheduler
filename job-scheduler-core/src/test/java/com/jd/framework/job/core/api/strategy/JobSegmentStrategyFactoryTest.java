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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.jd.framework.job.core.api.strategy.fixture.InvalidJobSegmentStrategy;
import com.jd.framework.job.core.api.strategy.impl.AverageAllocationJobSegmentStrategy;
import com.jd.framework.job.exception.JobConfigurationException;

public class JobSegmentStrategyFactoryTest {

	/**
	 * 默认
	 *   
	 * @author Rong Hu
	 */
	@Test
	public void assertGetDefaultStrategy() {
		assertThat(JobSegmentStrategyFactory.getStrategy(null), instanceOf(AverageAllocationJobSegmentStrategy.class));
	}

	@Test(expected = JobConfigurationException.class)
	public void assertGetStrategyFailureWhenClassNotFound() {
		JobSegmentStrategyFactory.getStrategy("NotClass");
	}

	@Test(expected = JobConfigurationException.class)
	public void assertGetStrategyFailureWhenNotStrategyClass() {
		JobSegmentStrategyFactory.getStrategy(Object.class.getName());
	}

	@Test(expected = JobConfigurationException.class)
	public void assertGetStrategyFailureWhenStrategyClassInvalid() {
		JobSegmentStrategyFactory.getStrategy(InvalidJobSegmentStrategy.class.getName());
	}

	@Test
	public void assertGetStrategySuccess() {
		assertThat(JobSegmentStrategyFactory.getStrategy(AverageAllocationJobSegmentStrategy.class.getName()),
				instanceOf(AverageAllocationJobSegmentStrategy.class));
	}

}
