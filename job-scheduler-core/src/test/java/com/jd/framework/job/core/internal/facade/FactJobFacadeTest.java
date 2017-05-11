/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.core.internal.facade;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.unitils.util.ReflectionUtils;

import com.google.common.collect.Lists;
import com.jd.framework.job.config.core.JobCoreConfiguration;
import com.jd.framework.job.config.type.FlowJobConfiguration;
import com.jd.framework.job.config.type.SimpleJobConfiguration;
import com.jd.framework.job.core.api.listener.ScheduleJobListener;
import com.jd.framework.job.core.api.listener.fixture.ScheduleJobListenerCaller;
import com.jd.framework.job.core.api.listener.fixture.TestScheduleJobListener;
import com.jd.framework.job.core.config.FactJobConfiguration;
import com.jd.framework.job.core.fixture.TestFlowJob;
import com.jd.framework.job.core.fixture.TestSimpleJob;
import com.jd.framework.job.core.internal.service.ConfigService;
import com.jd.framework.job.core.internal.service.ExecutionContextService;
import com.jd.framework.job.core.internal.service.ExecutionService;
import com.jd.framework.job.core.internal.service.FailoverService;
import com.jd.framework.job.core.internal.service.SegmentService;
import com.jd.framework.job.core.internal.service.ServerService;
import com.jd.framework.job.event.JobEventBus;
import com.jd.framework.job.exception.JobExecutionEnvironmentException;
import com.jd.framework.job.executor.context.SegmentContexts;

public class FactJobFacadeTest {

	@Mock
	private ConfigService configService;

	@Mock
	private ServerService serverService;

	@Mock
	private SegmentService segmentService;

	@Mock
	private ExecutionContextService executionContextService;

	@Mock
	private ExecutionService executionService;

	@Mock
	private FailoverService failoverService;

	@Mock
	private JobEventBus eventBus;

	@Mock
	private ScheduleJobListenerCaller caller;

	private FactJobFacade factJobFacade;

	@Before
	public void setUp() throws NoSuchFieldException {
		MockitoAnnotations.initMocks(this);
		factJobFacade = new FactJobFacade(null, "test_job",
				Collections.<ScheduleJobListener> singletonList(new TestScheduleJobListener(caller)), eventBus);
		ReflectionUtils.setFieldValue(factJobFacade, "configService", configService);
		ReflectionUtils.setFieldValue(factJobFacade, "serverService", serverService);
		ReflectionUtils.setFieldValue(factJobFacade, "segmentService", segmentService);
		ReflectionUtils.setFieldValue(factJobFacade, "executionContextService", executionContextService);
		ReflectionUtils.setFieldValue(factJobFacade, "executionService", executionService);
		ReflectionUtils.setFieldValue(factJobFacade, "failoverService", failoverService);
	}

	@Test
	public void assertLoad() {
		FactJobConfiguration expected = FactJobConfiguration.newBuilder(null).build();
		when(configService.load(true)).thenReturn(expected);
		assertThat(factJobFacade.loadJobRootConfiguration(true), is(expected));
	}

	@Test
	public void assertCheckMaxTimeDiffSecondsTolerable() throws JobExecutionEnvironmentException {
		factJobFacade.checkJobExecutionEnvironment();
		verify(configService).checkMaxTimeDiffSecondsTolerable();
	}

	@Test
	public void assertFailoverIfUnnecessary() {
		when(configService.load(true)).thenReturn(
				FactJobConfiguration.newBuilder(
						new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3)
								.failover(false).build(), TestSimpleJob.class.getCanonicalName())).build());
		factJobFacade.failoverIfNecessary();
		verify(failoverService, times(0)).failoverIfNecessary();
	}

	@Test
	public void assertFailoverIfNecessaryButIsPaused() {
		when(configService.load(true)).thenReturn(
				FactJobConfiguration
						.newBuilder(
								new SimpleJobConfiguration(JobCoreConfiguration
										.newBuilder("test_job", "0/1 * * * * ?", 3).failover(true).build(),
										TestSimpleJob.class.getCanonicalName())).monitorExecution(true).build());
		when(serverService.isJobPausedManually()).thenReturn(true);
		factJobFacade.failoverIfNecessary();
		verify(failoverService, times(0)).failoverIfNecessary();
	}

	@Test
	public void assertFailoverIfNecessary() {
		when(configService.load(true)).thenReturn(
				FactJobConfiguration
						.newBuilder(
								new SimpleJobConfiguration(JobCoreConfiguration
										.newBuilder("test_job", "0/1 * * * * ?", 3).failover(true).build(),
										TestSimpleJob.class.getCanonicalName())).monitorExecution(true).build());
		when(serverService.isJobPausedManually()).thenReturn(false);
		factJobFacade.failoverIfNecessary();
		verify(failoverService).failoverIfNecessary();
	}

	@Test
	public void assertRegisterJobBegin() {
		SegmentContexts segmentContexts = new SegmentContexts("fake_task_id", "test_job", 10, "",
				Collections.<Integer, String> emptyMap());
		factJobFacade.registerJobBegin(segmentContexts);
		verify(executionService).registerJobBegin(segmentContexts);
	}

	@Test
	public void assertRegisterJobCompletedWhenFailoverDisabled() {
		SegmentContexts segmentContexts = new SegmentContexts("fake_task_id", "test_job", 10, "",
				Collections.<Integer, String> emptyMap());
		when(configService.load(true)).thenReturn(
				FactJobConfiguration.newBuilder(
						new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3)
								.failover(false).build(), TestSimpleJob.class.getCanonicalName())).build());
		factJobFacade.registerJobCompleted(segmentContexts);
		verify(executionService).registerJobCompleted(segmentContexts);
		verify(failoverService, times(0)).updateFailoverComplete(segmentContexts.getSegmentItemParameters().keySet());
	}

	@Test
	public void assertRegisterJobCompletedWhenFailoverEnabled() {
		SegmentContexts segmentContexts = new SegmentContexts("fake_task_id", "test_job", 10, "",
				Collections.<Integer, String> emptyMap());
		when(configService.load(true)).thenReturn(
				FactJobConfiguration
						.newBuilder(
								new SimpleJobConfiguration(JobCoreConfiguration
										.newBuilder("test_job", "0/1 * * * * ?", 3).failover(true).build(),
										TestSimpleJob.class.getCanonicalName())).monitorExecution(true).build());
		factJobFacade.registerJobCompleted(segmentContexts);
		verify(executionService).registerJobCompleted(segmentContexts);
		verify(failoverService).updateFailoverComplete(segmentContexts.getSegmentItemParameters().keySet());
	}

	@Test
	public void assertGetSegmentContextWhenIsFailoverEnableAndFailover() {
		SegmentContexts segmentContexts = new SegmentContexts("fake_task_id", "test_job", 10, "",
				Collections.<Integer, String> emptyMap());
		when(configService.load(true)).thenReturn(
				FactJobConfiguration
						.newBuilder(
								new SimpleJobConfiguration(JobCoreConfiguration
										.newBuilder("test_job", "0/1 * * * * ?", 3).failover(true).build(),
										TestSimpleJob.class.getCanonicalName())).monitorExecution(true).build());
		when(failoverService.getLocalHostFailoverItems()).thenReturn(Collections.singletonList(1));
		when(executionContextService.getJobSegmentContext(Collections.singletonList(1))).thenReturn(segmentContexts);
		assertThat(factJobFacade.getSegmentContexts(), is(segmentContexts));
		verify(segmentService, times(0)).segmentIfNecessary();
	}

	@Test
	public void assertGetSegmentContextWhenIsFailoverEnableAndNotFailover() {
		SegmentContexts segmentContexts = new SegmentContexts("fake_task_id", "test_job", 10, "",
				Collections.<Integer, String> emptyMap());
		when(configService.load(true)).thenReturn(
				FactJobConfiguration
						.newBuilder(
								new SimpleJobConfiguration(JobCoreConfiguration
										.newBuilder("test_job", "0/1 * * * * ?", 3).failover(true).build(),
										TestSimpleJob.class.getCanonicalName())).monitorExecution(true).build());
		when(failoverService.getLocalHostFailoverItems()).thenReturn(Collections.<Integer> emptyList());
		when(segmentService.getLocalHostSegmentItems()).thenReturn(Lists.newArrayList(0, 1));
		when(failoverService.getLocalHostTakeOffItems()).thenReturn(Collections.singletonList(0));
		when(executionContextService.getJobSegmentContext(Collections.singletonList(1))).thenReturn(segmentContexts);
		assertThat(factJobFacade.getSegmentContexts(), is(segmentContexts));
		verify(segmentService).segmentIfNecessary();
	}

	@Test
	public void assertGetSegmentContextWhenIsFailoverDisable() {
		SegmentContexts segmentContexts = new SegmentContexts("fake_task_id", "test_job", 10, "",
				Collections.<Integer, String> emptyMap());
		when(configService.load(true)).thenReturn(
				FactJobConfiguration.newBuilder(
						new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3)
								.failover(false).build(), TestSimpleJob.class.getCanonicalName())).build());
		when(segmentService.getLocalHostSegmentItems()).thenReturn(Lists.newArrayList(0, 1));
		when(executionContextService.getJobSegmentContext(Lists.newArrayList(0, 1))).thenReturn(segmentContexts);
		assertThat(factJobFacade.getSegmentContexts(), is(segmentContexts));
		verify(segmentService).segmentIfNecessary();
	}

	@Test
	public void assertMisfireIfNecessary() {
		when(executionService.misfireIfNecessary(Arrays.asList(0, 1))).thenReturn(true);
		assertThat(factJobFacade.misfireIfNecessary(Arrays.asList(0, 1)), is(true));
	}

	@Test
	public void assertClearMisfire() {
		factJobFacade.clearMisfire(Arrays.asList(0, 1));
		verify(executionService).clearMisfire(Arrays.asList(0, 1));
	}

	@Test
	public void assertIsNeedSegment() {
		when(segmentService.isNeedSegment()).thenReturn(true);
		assertThat(factJobFacade.isNeedSegment(), is(true));
	}

	@Test
	public void assertCleanPreviousExecutionInfo() {
		factJobFacade.cleanPreviousExecutionInfo();
		verify(executionService).cleanPreviousExecutionInfo();
	}

	@Test
	public void assertBeforeJobExecuted() {
		factJobFacade.beforeJobExecuted(new SegmentContexts("fake_task_id", "test_job", 10, "", Collections
				.<Integer, String> emptyMap()));
		verify(caller).before();
	}

	@Test
	public void assertAfterJobExecuted() {
		factJobFacade.afterJobExecuted(new SegmentContexts("fake_task_id", "test_job", 10, "", Collections
				.<Integer, String> emptyMap()));
		verify(caller).after();
	}

	@Test
	public void assertNotEligibleForJobRunningWhenJobPausedManually() {
		when(configService.load(true)).thenReturn(
				FactJobConfiguration.newBuilder(
						new FlowJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3)
								.build(), TestFlowJob.class.getCanonicalName(), true)).build());
		when(serverService.isJobPausedManually()).thenReturn(true);
		assertThat(factJobFacade.isEligibleForJobRunning(), is(false));
		verify(serverService).isJobPausedManually();
	}

	@Test
	public void assertNotEligibleForJobRunningWhenNeedSegment() {
		when(configService.load(true)).thenReturn(
				FactJobConfiguration.newBuilder(
						new FlowJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3)
								.build(), TestFlowJob.class.getCanonicalName(), true)).build());
		when(segmentService.isNeedSegment()).thenReturn(true);
		assertThat(factJobFacade.isEligibleForJobRunning(), is(false));
		verify(segmentService).isNeedSegment();
	}

	@Test
	public void assertNotEligibleForJobRunningWhenUnStreamingProcess() {
		when(configService.load(true)).thenReturn(
				FactJobConfiguration.newBuilder(
						new FlowJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3)
								.build(), TestFlowJob.class.getCanonicalName(), false)).build());
		assertThat(factJobFacade.isEligibleForJobRunning(), is(false));
		verify(configService).load(true);
	}

	@Test
	public void assertEligibleForJobRunningWhenNotJobPausedManuallyAndNotNeedSegmentAndStreamingProcess() {
		when(serverService.isJobPausedManually()).thenReturn(false);
		when(segmentService.isNeedSegment()).thenReturn(false);
		when(configService.load(true)).thenReturn(
				FactJobConfiguration.newBuilder(
						new FlowJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3)
								.build(), TestFlowJob.class.getCanonicalName(), true)).build());
		assertThat(factJobFacade.isEligibleForJobRunning(), is(true));
		verify(serverService).isJobPausedManually();
		verify(segmentService).isNeedSegment();
		verify(configService).load(true);
	}

	@Test
	public void assertPostJobExecutionEvent() {
		factJobFacade.postJobExecutionEvent(null);
		verify(eventBus).post(null);
	}

}
