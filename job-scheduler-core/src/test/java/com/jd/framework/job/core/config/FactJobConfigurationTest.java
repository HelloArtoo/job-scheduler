/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.core.config;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.jd.framework.job.config.core.JobCoreConfiguration;
import com.jd.framework.job.config.type.SimpleJobConfiguration;
import com.jd.framework.job.core.fixture.TestSimpleJob;

public class FactJobConfigurationTest {

	@Test
	public void assertBuildAllProperties() {
		FactJobConfiguration actual = FactJobConfiguration
				.newBuilder(
						new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3)
								.build(), TestSimpleJob.class.getCanonicalName())).monitorExecution(false)
				.maxTimeDiffSeconds(1000).monitorPort(8888).jobSegmentStrategyClass("testClass").disabled(true)
				.overwrite(true).reconcileIntervalMinutes(60).build();
		assertFalse(actual.isMonitorExecution());
		assertThat(actual.getMaxTimeDiffSeconds(), is(1000));
		assertThat(actual.getMonitorPort(), is(8888));
		assertThat(actual.getJobSegmentStrategyClass(), is("testClass"));
		assertTrue(actual.isDisabled());
		assertTrue(actual.isOverwrite());
		assertThat(actual.getReconcileIntervalMinutes(), is(60));
	}

	@Test
	public void assertBuildRequiredProperties() {
		FactJobConfiguration actual = FactJobConfiguration.newBuilder(
				new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).build(),
						TestSimpleJob.class.getCanonicalName())).build();
		assertTrue(actual.isMonitorExecution());
		assertThat(actual.getMaxTimeDiffSeconds(), is(-1));
		assertThat(actual.getMonitorPort(), is(-1));
		assertThat(actual.getJobSegmentStrategyClass(), is(""));
		assertFalse(actual.isDisabled());
		assertFalse(actual.isOverwrite());
	}

	@Test
	public void assertBuildWhenOptionalParametersIsNull() {
		assertThat(
				FactJobConfiguration
						.newBuilder(
								new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?",
										3).build(), TestSimpleJob.class.getCanonicalName()))
						.jobSegmentStrategyClass(null).build().getJobSegmentStrategyClass(), is(""));
	}

	/**
	 * monitor false,那么isFailover false
	 * 
	 * @author Rong Hu
	 */
	@Test
	public void assertIsNotFailoverWhenNotMonitorExecution() {
		assertFalse(FactJobConfiguration
				.newBuilder(
						new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3)
								.failover(true).build(), TestSimpleJob.class.getCanonicalName()))
				.monitorExecution(false).build().isFailover());
	}

	@Test
	public void assertIsNotFailoverWhenMonitorExecution() {
		assertFalse(FactJobConfiguration
				.newBuilder(
						new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3)
								.failover(false).build(), TestSimpleJob.class.getCanonicalName()))
				.monitorExecution(true).build().isFailover());
	}

	@Test
	public void assertIsFailover() {
		assertTrue(FactJobConfiguration
				.newBuilder(
						new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3)
								.failover(true).build(), TestSimpleJob.class.getCanonicalName()))
				.monitorExecution(true).build().isFailover());
	}
}
