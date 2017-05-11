/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.core.internal.factory;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.jd.framework.job.config.core.JobCoreConfiguration;
import com.jd.framework.job.config.type.FlowJobConfiguration;
import com.jd.framework.job.config.type.SimpleJobConfiguration;
import com.jd.framework.job.constant.job.JobType;
import com.jd.framework.job.core.config.FactJobConfiguration;
import com.jd.framework.job.core.fixture.TestFlowJob;
import com.jd.framework.job.core.fixture.TestSimpleJob;
import com.jd.framework.job.executor.handler.JobProperties;
import com.jd.framework.job.executor.handler.exception.DefaultJobExceptionHandler;
import com.jd.framework.job.executor.handler.threadpool.DefaultExecutorServiceHandler;

public class FactJobConfigGsonFactoryTest {

	private static final String JOB_PROPS_JSON = "{\"job_exception_handler\":\""
			+ DefaultJobExceptionHandler.class.getCanonicalName() + "\"," + "\"executor_service_handler\":\""
			+ DefaultExecutorServiceHandler.class.getCanonicalName() + "\"}";

	private String simpleJobJson = "{\"jobName\":\"test_job\",\"jobClass\":\"com.jd.framework.job.core.fixture.TestSimpleJob\",\"jobType\":\"SIMPLE\",\"cron\":\"0/1 * * * * ?\","
			+ "\"segmentTotalCount\":3,\"segmentItemParameters\":\"\",\"jobParameter\":\"\",\"failover\":true,\"misfire\":false,\"description\":\"\","
			+ "\"jobProperties\":"
			+ JOB_PROPS_JSON
			+ ",\"monitorExecution\":false,\"maxTimeDiffSeconds\":1000,\"monitorPort\":8888,"
			+ "\"jobSegmentStrategyClass\":\"testClass\",\"disabled\":true,\"overwrite\":true,\"reconcileIntervalMinutes\":15}";

	private String flowJobJson = "{\"jobName\":\"test_job\",\"jobClass\":\"com.jd.framework.job.core.fixture.TestFlowJob\",\"jobType\":\"FLOW\",\"cron\":\"0/1 * * * * ?\","
			+ "\"segmentTotalCount\":3,\"segmentItemParameters\":\"\",\"jobParameter\":\"\",\"failover\":false,\"misfire\":true,\"description\":\"\","
			+ "\"jobProperties\":"
			+ JOB_PROPS_JSON
			+ ",\"streamingProcess\":true,"
			+ "\"monitorExecution\":true,\"maxTimeDiffSeconds\":-1,\"monitorPort\":-1,\"jobSegmentStrategyClass\":\"\",\"disabled\":false,\"overwrite\":false,\"reconcileIntervalMinutes\":-1}";

	@Test
	public void assertToJsonForSimpleJob() {
		FactJobConfiguration actual = FactJobConfiguration
				.newBuilder(
						new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3)
								.failover(true).misfire(false).build(), TestSimpleJob.class.getCanonicalName()))
				.monitorExecution(false).maxTimeDiffSeconds(1000).monitorPort(8888)
				.jobSegmentStrategyClass("testClass").disabled(true).overwrite(true).reconcileIntervalMinutes(15)
				.build();
		assertThat(FactJobConfigGsonFactory.toJson(actual), is(simpleJobJson));
	}

	@Test
	public void assertToJsonForFlowJob() {
		FactJobConfiguration actual = FactJobConfiguration.newBuilder(
				new FlowJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).build(),
						TestFlowJob.class.getCanonicalName(), true)).build();
		assertThat(FactJobConfigGsonFactory.toJson(actual), is(flowJobJson));
	}

	@Test
	public void assertFromJsonForSimpleJob() {
		FactJobConfiguration actual = FactJobConfigGsonFactory.fromJson(simpleJobJson);
		assertThat(actual.getJobName(), is("test_job"));
		assertThat(actual.getTypeConfig().getJobClass(), is(TestSimpleJob.class.getCanonicalName()));
		assertThat(actual.getTypeConfig().getJobType(), is(JobType.SIMPLE));
		assertThat(actual.getTypeConfig().getCoreConfig().getCron(), is("0/1 * * * * ?"));
		assertThat(actual.getTypeConfig().getCoreConfig().getSegmentTotalCount(), is(3));
		assertThat(actual.getTypeConfig().getCoreConfig().getSegmentItemParameters(), is(""));
		assertThat(actual.getTypeConfig().getCoreConfig().getJobParameter(), is(""));
		assertTrue(actual.getTypeConfig().getCoreConfig().isFailover());
		assertFalse(actual.getTypeConfig().getCoreConfig().isMisfire());
		assertThat(actual.getTypeConfig().getCoreConfig().getDescription(), is(""));
		assertThat(
				actual.getTypeConfig().getCoreConfig().getJobProperties()
						.get(JobProperties.JobPropertiesEnum.JOB_EXCEPTION_HANDLER),
				is(DefaultJobExceptionHandler.class.getCanonicalName()));
		assertThat(
				actual.getTypeConfig().getCoreConfig().getJobProperties()
						.get(JobProperties.JobPropertiesEnum.EXECUTOR_SERVICE_HANDLER),
				is(DefaultExecutorServiceHandler.class.getCanonicalName()));
		assertFalse(actual.isMonitorExecution());
		assertThat(actual.getMaxTimeDiffSeconds(), is(1000));
		assertThat(actual.getMonitorPort(), is(8888));
		assertThat(actual.getJobSegmentStrategyClass(), is("testClass"));
		assertThat(actual.getReconcileIntervalMinutes(), is(15));
		assertTrue(actual.isDisabled());
		assertTrue(actual.isOverwrite());
	}

	@Test
	public void assertFromJsonForFlowJob() {
		FactJobConfiguration actual = FactJobConfigGsonFactory.fromJson(flowJobJson);
		assertThat(actual.getJobName(), is("test_job"));
		assertThat(actual.getTypeConfig().getJobClass(), is(TestFlowJob.class.getCanonicalName()));
		assertThat(actual.getTypeConfig().getJobType(), is(JobType.FLOW));
		assertThat(actual.getTypeConfig().getCoreConfig().getCron(), is("0/1 * * * * ?"));
		assertThat(actual.getTypeConfig().getCoreConfig().getSegmentTotalCount(), is(3));
		assertThat(actual.getTypeConfig().getCoreConfig().getSegmentItemParameters(), is(""));
		assertThat(actual.getTypeConfig().getCoreConfig().getJobParameter(), is(""));
		assertFalse(actual.getTypeConfig().getCoreConfig().isFailover());
		assertTrue(actual.getTypeConfig().getCoreConfig().isMisfire());
		assertThat(actual.getTypeConfig().getCoreConfig().getDescription(), is(""));
		assertThat(
				actual.getTypeConfig().getCoreConfig().getJobProperties()
						.get(JobProperties.JobPropertiesEnum.JOB_EXCEPTION_HANDLER),
				is(DefaultJobExceptionHandler.class.getCanonicalName()));
		assertThat(
				actual.getTypeConfig().getCoreConfig().getJobProperties()
						.get(JobProperties.JobPropertiesEnum.EXECUTOR_SERVICE_HANDLER),
				is(DefaultExecutorServiceHandler.class.getCanonicalName()));
		assertTrue(actual.isMonitorExecution());
		assertThat(actual.getMaxTimeDiffSeconds(), is(-1));
		assertThat(actual.getMonitorPort(), is(-1));
		assertThat(actual.getJobSegmentStrategyClass(), is(""));
		assertFalse(actual.isDisabled());
		assertFalse(actual.isOverwrite());
		assertThat(actual.getReconcileIntervalMinutes(), is(-1));
		assertTrue(((FlowJobConfiguration) actual.getTypeConfig()).isStreamingProcess());
	}

}
