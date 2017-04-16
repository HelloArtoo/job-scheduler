/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.config.core;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.jd.framework.job.executor.handler.JobProperties.JobPropertiesEnum;
import com.jd.framework.job.executor.handler.exception.DefaultJobExceptionHandler;
import com.jd.framework.job.fixture.handler.IgnoreExceptionHandler;

public class JobCoreConfigurationTest {

	private final String jobName = "test_job";
	private final String cron = "0/1 * * * * ?";
	private final int segmentTotalCount = 3;

	@Test(expected = IllegalArgumentException.class)
	public void assertBuildWhenSegmentTotalIsNegative() {
		JobCoreConfiguration.newBuilder(jobName, cron, -3).build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void assertBuildWhenCronIsNull() {
		JobCoreConfiguration.newBuilder(jobName, null, segmentTotalCount).build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void assertBuildWhenJobNameIsNull() {
		JobCoreConfiguration.newBuilder(null, cron, segmentTotalCount).build();
	}

	@Test
	public void assertBuildOptionalNull() {
		JobCoreConfiguration actual = JobCoreConfiguration.newBuilder(jobName, cron, segmentTotalCount)
				.segmentItemParameters(null).jobParameter(null).description(null).build();
		assertRequiredProperties(actual);
		assertDefaultValues(actual);
	}

	@Test
	public void assertBuildRequired() {
		JobCoreConfiguration actual = JobCoreConfiguration.newBuilder(jobName, cron, segmentTotalCount).build();
		assertRequiredProperties(actual);
		assertDefaultValues(actual);
	}

	@Test
	public void assertBuildAll() {
		JobCoreConfiguration actual = JobCoreConfiguration
				.newBuilder(jobName, cron, segmentTotalCount)
				.segmentItemParameters("0=北京,1=上海,2=广州")
				.jobParameter("param")
				.failover(true)
				.misfire(true)
				.description("desc")
				.jobProperties(JobPropertiesEnum.JOB_EXCEPTION_HANDLER.getKey(), IgnoreExceptionHandler.class.getName())
				.build();

		assertRequiredProperties(actual);

		assertEquals("0=北京,1=上海,2=广州", actual.getSegmentItemParameters());
		assertEquals("param", actual.getJobParameter());
		assertEquals("desc", actual.getDescription());
		assertEquals(true, actual.isFailover());
		assertEquals(true, actual.isMisfire());
		assertEquals(IgnoreExceptionHandler.class.getName(),
				actual.getJobProperties().get(JobPropertiesEnum.JOB_EXCEPTION_HANDLER));
	}

	/**
	 * 必填项检查
	 * 
	 * @param actual
	 * @author Rong Hu
	 */
	private void assertRequiredProperties(final JobCoreConfiguration actual) {
		assertEquals("test_job", actual.getJobName());
		assertEquals("0/1 * * * * ?", actual.getCron());
		assertEquals(3, actual.getSegmentTotalCount());
	}

	/**
	 * default
	 * 
	 * @param actual
	 * @author Rong Hu
	 */
	private void assertDefaultValues(final JobCoreConfiguration actual) {
		assertEquals("", actual.getSegmentItemParameters());
		assertEquals("", actual.getJobParameter());
		assertEquals(false, actual.isFailover());
		assertEquals(true, actual.isMisfire());
		assertEquals("", actual.getDescription());
		assertEquals(DefaultJobExceptionHandler.class.getName(),
				actual.getJobProperties().get(JobPropertiesEnum.JOB_EXCEPTION_HANDLER));
	}
}
