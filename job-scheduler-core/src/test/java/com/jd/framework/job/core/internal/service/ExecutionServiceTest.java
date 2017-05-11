/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */     
package com.jd.framework.job.core.internal.service;    

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.unitils.util.ReflectionUtils;

import com.jd.framework.job.config.core.JobCoreConfiguration;
import com.jd.framework.job.config.type.SimpleJobConfiguration;
import com.jd.framework.job.constant.job.ServerStatus;
import com.jd.framework.job.core.config.FactJobConfiguration;
import com.jd.framework.job.core.fixture.TestSimpleJob;
import com.jd.framework.job.core.internal.executor.JobRegistry;
import com.jd.framework.job.core.internal.executor.quartz.JobScheduleController;
import com.jd.framework.job.core.internal.helper.JobNodeStorageHelper;
import com.jd.framework.job.executor.context.SegmentContexts;
import com.jd.framework.job.utils.env.LocalHostService;
    
public class ExecutionServiceTest {

	@Mock
	private JobNodeStorageHelper jobNodeStorage;

	@Mock
	private LocalHostService localHostService;

	@Mock
	private ConfigService configService;

	@Mock
	private ServerService serverService;

	@Mock
	private LeaderElectionService leaderElectionService;

	@Mock
	private JobScheduleController jobScheduleController;

	private final ExecutionService executionService = new ExecutionService(null, "test_job");

	@Before
	public void setUp() throws NoSuchFieldException {
		MockitoAnnotations.initMocks(this);
		ReflectionUtils.setFieldValue(executionService, "jobNodeStorage", jobNodeStorage);
		ReflectionUtils.setFieldValue(executionService, "configService", configService);
		ReflectionUtils.setFieldValue(executionService, "serverService", serverService);
		ReflectionUtils.setFieldValue(executionService, "leaderElectionService", leaderElectionService);
		when(localHostService.getIp()).thenReturn("mockedIP");
		when(localHostService.getHostName()).thenReturn("mockedHostName");
	}

	@Test
	public void assertRegisterJobBeginWhenNotAssignAnyItem() {
		executionService.registerJobBegin(new SegmentContexts("fake_task_id", "test_job", 10, "", Collections
				.<Integer, String> emptyMap()));
		verify(configService, times(0)).load(true);
	}

	@Test
	public void assertRegisterJobBeginWhenNotMonitorExecution() {
		when(configService.load(true)).thenReturn(
				FactJobConfiguration
						.newBuilder(
								new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?",
										3).build(), TestSimpleJob.class.getCanonicalName())).monitorExecution(false)
						.build());
		executionService.registerJobBegin(getSegmentContext());
		verify(configService).load(true);
	}

	@Test
	public void assertRegisterJobBeginWithoutNextFireTime() {
		when(configService.load(true)).thenReturn(
				FactJobConfiguration
						.newBuilder(
								new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?",
										3).build(), TestSimpleJob.class.getCanonicalName())).monitorExecution(true)
						.build());
		when(jobScheduleController.getNextFireTime()).thenReturn(null);
		JobRegistry.getInstance().addJobScheduleController("test_job", jobScheduleController);
		executionService.registerJobBegin(getSegmentContext());
		verify(configService).load(true);
		verify(serverService).updateServerStatus(ServerStatus.RUNNING);
		verify(jobNodeStorage).fillEphemeralJobNode("execution/0/running", "");
		verify(jobNodeStorage).fillEphemeralJobNode("execution/1/running", "");
		verify(jobNodeStorage).fillEphemeralJobNode("execution/2/running", "");
		verify(jobNodeStorage).replaceJobNode(eq("execution/0/lastBeginTime"), anyLong());
		verify(jobNodeStorage).replaceJobNode(eq("execution/1/lastBeginTime"), anyLong());
		verify(jobNodeStorage).replaceJobNode(eq("execution/2/lastBeginTime"), anyLong());
	}

	@Test
	public void assertRegisterJobBeginWithNextFireTime() {
		when(configService.load(true)).thenReturn(
				FactJobConfiguration
						.newBuilder(
								new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?",
										3).build(), TestSimpleJob.class.getCanonicalName())).monitorExecution(true)
						.build());
		when(jobScheduleController.getNextFireTime()).thenReturn(new Date(0L));
		JobRegistry.getInstance().addJobScheduleController("test_job", jobScheduleController);
		executionService.registerJobBegin(getSegmentContext());
		verify(configService).load(true);
		verify(serverService).updateServerStatus(ServerStatus.RUNNING);
		verify(jobNodeStorage).fillEphemeralJobNode("execution/0/running", "");
		verify(jobNodeStorage).fillEphemeralJobNode("execution/1/running", "");
		verify(jobNodeStorage).fillEphemeralJobNode("execution/2/running", "");
		verify(jobNodeStorage).replaceJobNode(eq("execution/0/lastBeginTime"), anyLong());
		verify(jobNodeStorage).replaceJobNode(eq("execution/1/lastBeginTime"), anyLong());
		verify(jobNodeStorage).replaceJobNode(eq("execution/2/lastBeginTime"), anyLong());
		verify(jobNodeStorage).replaceJobNode("execution/0/nextFireTime", 0L);
		verify(jobNodeStorage).replaceJobNode("execution/1/nextFireTime", 0L);
		verify(jobNodeStorage).replaceJobNode("execution/2/nextFireTime", 0L);
	}

	@Test
	public void assertRegisterJobCompletedWhenNotMonitorExecution() {
		when(configService.load(true)).thenReturn(
				FactJobConfiguration
						.newBuilder(
								new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?",
										3).build(), TestSimpleJob.class.getCanonicalName())).monitorExecution(false)
						.build());
		executionService.registerJobCompleted(new SegmentContexts("fake_task_id", "test_job", 10, "", Collections
				.<Integer, String> emptyMap()));
		verify(configService).load(true);
		verify(serverService, times(0)).updateServerStatus(ServerStatus.READY);
	}

	@Test
	public void assertRegisterJobCompleted() {
		when(configService.load(true)).thenReturn(
				FactJobConfiguration
						.newBuilder(
								new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?",
										3).build(), TestSimpleJob.class.getCanonicalName())).monitorExecution(true)
						.build());
		executionService.registerJobCompleted(getSegmentContext());
		verify(serverService).updateServerStatus(ServerStatus.READY);
		verify(jobNodeStorage).createJobNodeIfNeeded("execution/0/completed");
		verify(jobNodeStorage).createJobNodeIfNeeded("execution/1/completed");
		verify(jobNodeStorage).createJobNodeIfNeeded("execution/2/completed");
		verify(jobNodeStorage).removeJobNodeIfExisted("execution/0/running");
		verify(jobNodeStorage).removeJobNodeIfExisted("execution/1/running");
		verify(jobNodeStorage).removeJobNodeIfExisted("execution/2/running");
		verify(jobNodeStorage).replaceJobNode(eq("execution/0/lastCompleteTime"), anyLong());
		verify(jobNodeStorage).replaceJobNode(eq("execution/1/lastCompleteTime"), anyLong());
		verify(jobNodeStorage).replaceJobNode(eq("execution/2/lastCompleteTime"), anyLong());
	}

	@Test
	public void assertCleanPreviousExecutionInfoWhenNotMonitorExecution() {
		when(jobNodeStorage.isJobNodeExisted("execution")).thenReturn(false);
		executionService.cleanPreviousExecutionInfo();
		verify(jobNodeStorage).isJobNodeExisted("execution");
		verify(leaderElectionService, times(0)).isLeader();
	}

	@Test
	public void assertCleanPreviousExecutionInfoWhenIsNotLeader() {
		when(jobNodeStorage.isJobNodeExisted("execution")).thenReturn(true);
		when(leaderElectionService.isLeader()).thenReturn(false);
		when(jobNodeStorage.isJobNodeExisted("leader/execution/cleaning")).thenReturn(true, false);
		executionService.cleanPreviousExecutionInfo();
		verify(jobNodeStorage).isJobNodeExisted("execution");
		verify(leaderElectionService).isLeader();
		verify(jobNodeStorage, times(2)).isJobNodeExisted("leader/execution/cleaning");
	}

	@Test
	public void assertCleanPreviousExecutionInfoWhenIsLeaderButNotNeedFixExecutionInfo() {
		when(jobNodeStorage.isJobNodeExisted("execution")).thenReturn(true);
		when(leaderElectionService.isLeader()).thenReturn(true);
		when(jobNodeStorage.getJobNodeChildrenKeys("execution")).thenReturn(Arrays.asList("0", "1", "2"));
		when(jobNodeStorage.isJobNodeExisted("leader/execution/necessary")).thenReturn(false);
		executionService.cleanPreviousExecutionInfo();
		verify(jobNodeStorage).isJobNodeExisted("execution");
		verify(leaderElectionService).isLeader();
		verify(jobNodeStorage).fillEphemeralJobNode("leader/execution/cleaning", "");
		verify(jobNodeStorage).getJobNodeChildrenKeys("execution");
		verify(jobNodeStorage).removeJobNodeIfExisted("execution/0/completed");
		verify(jobNodeStorage).removeJobNodeIfExisted("execution/1/completed");
		verify(jobNodeStorage).removeJobNodeIfExisted("execution/2/completed");
		verify(jobNodeStorage).isJobNodeExisted("leader/execution/necessary");
		verify(jobNodeStorage).removeJobNodeIfExisted("leader/execution/cleaning");
		verify(jobNodeStorage).isJobNodeExisted("leader/execution/cleaning");
	}

	@Test
	public void assertCleanPreviousExecutionInfoWhenNeedFixExecutionInfoForNewValuesGreater() {
		when(jobNodeStorage.isJobNodeExisted("execution")).thenReturn(true);
		when(leaderElectionService.isLeader()).thenReturn(true);
		when(jobNodeStorage.getJobNodeChildrenKeys("execution")).thenReturn(Arrays.asList("0", "1", "2"));
		when(jobNodeStorage.isJobNodeExisted("leader/execution/necessary")).thenReturn(true);
		when(configService.load(false)).thenReturn(
				FactJobConfiguration.newBuilder(
						new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 4)
								.build(), TestSimpleJob.class.getCanonicalName())).build());
		executionService.cleanPreviousExecutionInfo();
		verify(jobNodeStorage).isJobNodeExisted("execution");
		verify(leaderElectionService).isLeader();
		verify(jobNodeStorage).fillEphemeralJobNode("leader/execution/cleaning", "");
		verify(jobNodeStorage).getJobNodeChildrenKeys("execution");
		verify(jobNodeStorage).removeJobNodeIfExisted("execution/0/completed");
		verify(jobNodeStorage).removeJobNodeIfExisted("execution/1/completed");
		verify(jobNodeStorage).removeJobNodeIfExisted("execution/2/completed");
		verify(jobNodeStorage).isJobNodeExisted("leader/execution/necessary");
		verify(configService).load(false);
		verify(jobNodeStorage).createJobNodeIfNeeded("execution/3");
		verify(jobNodeStorage).removeJobNodeIfExisted("leader/execution/necessary");
		verify(jobNodeStorage).removeJobNodeIfExisted("leader/execution/cleaning");
		verify(jobNodeStorage).isJobNodeExisted("leader/execution/cleaning");
	}

	@Test
	public void assertCleanPreviousExecutionInfoWhenNeedFixExecutionInfoForNewValuesLess() {
		when(jobNodeStorage.isJobNodeExisted("execution")).thenReturn(true);
		when(leaderElectionService.isLeader()).thenReturn(true);
		when(jobNodeStorage.getJobNodeChildrenKeys("execution")).thenReturn(Arrays.asList("0", "1", "2"));
		when(jobNodeStorage.isJobNodeExisted("leader/execution/necessary")).thenReturn(true);
		when(configService.load(false)).thenReturn(
				FactJobConfiguration.newBuilder(
						new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 2)
								.build(), TestSimpleJob.class.getCanonicalName())).build());
		executionService.cleanPreviousExecutionInfo();
		verify(jobNodeStorage).isJobNodeExisted("execution");
		verify(leaderElectionService).isLeader();
		verify(jobNodeStorage).fillEphemeralJobNode("leader/execution/cleaning", "");
		verify(jobNodeStorage).getJobNodeChildrenKeys("execution");
		verify(jobNodeStorage).removeJobNodeIfExisted("execution/0/completed");
		verify(jobNodeStorage).removeJobNodeIfExisted("execution/1/completed");
		verify(jobNodeStorage).removeJobNodeIfExisted("execution/2/completed");
		verify(jobNodeStorage).isJobNodeExisted("leader/execution/necessary");
		verify(configService).load(false);
		verify(jobNodeStorage).removeJobNodeIfExisted("execution/2");
		verify(jobNodeStorage).removeJobNodeIfExisted("leader/execution/necessary");
		verify(jobNodeStorage).removeJobNodeIfExisted("leader/execution/cleaning");
		verify(jobNodeStorage).isJobNodeExisted("leader/execution/cleaning");
	}

	@Test
	public void assertCleanPreviousExecutionInfoWhenNeedFixExecutionInfoForNewValuesEqual() {
		when(jobNodeStorage.isJobNodeExisted("execution")).thenReturn(true);
		when(leaderElectionService.isLeader()).thenReturn(true);
		when(jobNodeStorage.getJobNodeChildrenKeys("execution")).thenReturn(Arrays.asList("0", "1", "2"));
		when(jobNodeStorage.isJobNodeExisted("leader/execution/necessary")).thenReturn(true);
		when(configService.load(false)).thenReturn(
				FactJobConfiguration.newBuilder(
						new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3)
								.build(), TestSimpleJob.class.getCanonicalName())).build());
		executionService.cleanPreviousExecutionInfo();
		verify(jobNodeStorage).isJobNodeExisted("execution");
		verify(leaderElectionService).isLeader();
		verify(jobNodeStorage).fillEphemeralJobNode("leader/execution/cleaning", "");
		verify(jobNodeStorage).getJobNodeChildrenKeys("execution");
		verify(jobNodeStorage).removeJobNodeIfExisted("execution/0/completed");
		verify(jobNodeStorage).removeJobNodeIfExisted("execution/1/completed");
		verify(jobNodeStorage).removeJobNodeIfExisted("execution/2/completed");
		verify(jobNodeStorage).isJobNodeExisted("leader/execution/necessary");
		verify(configService).load(false);
		verify(jobNodeStorage).removeJobNodeIfExisted("leader/execution/necessary");
		verify(jobNodeStorage).removeJobNodeIfExisted("leader/execution/cleaning");
		verify(jobNodeStorage).isJobNodeExisted("leader/execution/cleaning");
	}

	@Test
	public void assertSetNeedFixExecutionInfoFlag() {
		executionService.setNeedFixExecutionInfoFlag();
		verify(jobNodeStorage).createJobNodeIfNeeded("leader/execution/necessary");
	}

	@Test
	public void assertClearRunningInfo() {
		executionService.clearRunningInfo(Arrays.asList(0, 1, 2));
		verify(jobNodeStorage).removeJobNodeIfExisted("execution/0/running");
		verify(jobNodeStorage).removeJobNodeIfExisted("execution/1/running");
		verify(jobNodeStorage).removeJobNodeIfExisted("execution/2/running");
	}

	@Test
	public void assertMisfireIfNotNecessaryWhenNotMonitorExecution() {
		when(configService.load(true)).thenReturn(
				FactJobConfiguration
						.newBuilder(
								new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?",
										4).build(), TestSimpleJob.class.getCanonicalName())).monitorExecution(false)
						.build());
		assertFalse(executionService.misfireIfNecessary(Arrays.asList(0, 1, 2)));
		verify(configService).load(true);
	}

	@Test
	public void assertMisfireIfNotNecessary() {
		when(configService.load(true)).thenReturn(
				FactJobConfiguration
						.newBuilder(
								new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?",
										4).build(), TestSimpleJob.class.getCanonicalName())).monitorExecution(true)
						.build());
		when(jobNodeStorage.isJobNodeExisted("execution/0/running")).thenReturn(false);
		when(jobNodeStorage.isJobNodeExisted("execution/1/running")).thenReturn(false);
		when(jobNodeStorage.isJobNodeExisted("execution/2/running")).thenReturn(false);
		assertFalse(executionService.misfireIfNecessary(Arrays.asList(0, 1, 2)));
		verify(configService).load(true);
		verify(jobNodeStorage).isJobNodeExisted("execution/0/running");
		verify(jobNodeStorage).isJobNodeExisted("execution/1/running");
		verify(jobNodeStorage).isJobNodeExisted("execution/2/running");
	}

	@Test
	public void assertMisfireIfNecessary() {
		when(configService.load(true)).thenReturn(
				FactJobConfiguration
						.newBuilder(
								new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?",
										4).build(), TestSimpleJob.class.getCanonicalName())).monitorExecution(true)
						.build());
		when(jobNodeStorage.isJobNodeExisted("execution/0/running")).thenReturn(false);
		when(jobNodeStorage.isJobNodeExisted("execution/1/running")).thenReturn(true);
		assertTrue(executionService.misfireIfNecessary(Arrays.asList(0, 1, 2)));
		verify(configService, times(2)).load(true);
		verify(jobNodeStorage).isJobNodeExisted("execution/0/running");
		verify(jobNodeStorage).isJobNodeExisted("execution/1/running");
		verify(jobNodeStorage).createJobNodeIfNeeded("execution/0/misfire");
		verify(jobNodeStorage).createJobNodeIfNeeded("execution/1/misfire");
		verify(jobNodeStorage).createJobNodeIfNeeded("execution/2/misfire");
	}

	@Test
	public void assertSetMisfireWhenMonitorExecutionDisabled() {
		when(configService.load(true)).thenReturn(
				FactJobConfiguration
						.newBuilder(
								new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?",
										4).build(), TestSimpleJob.class.getCanonicalName())).monitorExecution(false)
						.build());
		executionService.setMisfire(Arrays.asList(0, 1, 2));
		verify(configService).load(true);
		verify(jobNodeStorage, times(0)).createJobNodeIfNeeded("execution/0/misfire");
		verify(jobNodeStorage, times(0)).createJobNodeIfNeeded("execution/1/misfire");
		verify(jobNodeStorage, times(0)).createJobNodeIfNeeded("execution/2/misfire");
	}

	@Test
	public void assertSetMisfire() {
		when(configService.load(true)).thenReturn(
				FactJobConfiguration
						.newBuilder(
								new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?",
										4).build(), TestSimpleJob.class.getCanonicalName())).monitorExecution(true)
						.build());
		executionService.setMisfire(Arrays.asList(0, 1, 2));
		verify(configService).load(true);
		verify(jobNodeStorage).createJobNodeIfNeeded("execution/0/misfire");
		verify(jobNodeStorage).createJobNodeIfNeeded("execution/1/misfire");
		verify(jobNodeStorage).createJobNodeIfNeeded("execution/2/misfire");
	}

	@Test
	public void assertGetMisfiredJobItems() {
		when(jobNodeStorage.isJobNodeExisted("execution/0/misfire")).thenReturn(true);
		when(jobNodeStorage.isJobNodeExisted("execution/1/misfire")).thenReturn(true);
		when(jobNodeStorage.isJobNodeExisted("execution/2/misfire")).thenReturn(false);
		assertThat(executionService.getMisfiredJobItems(Arrays.asList(0, 1, 2)), is(Arrays.asList(0, 1)));
		verify(jobNodeStorage).isJobNodeExisted("execution/0/misfire");
		verify(jobNodeStorage).isJobNodeExisted("execution/1/misfire");
		verify(jobNodeStorage).isJobNodeExisted("execution/2/misfire");
	}

	@Test
	public void assertClearMisfire() {
		executionService.clearMisfire(Arrays.asList(0, 1, 2));
		verify(jobNodeStorage).removeJobNodeIfExisted("execution/0/misfire");
		verify(jobNodeStorage).removeJobNodeIfExisted("execution/1/misfire");
		verify(jobNodeStorage).removeJobNodeIfExisted("execution/2/misfire");
	}

	@Test
	public void assertRemoveExecutionInfo() {
		executionService.removeExecutionInfo();
		verify(jobNodeStorage).removeJobNodeIfExisted("execution");
	}

	@Test
	public void assertIsCompleted() {
		when(jobNodeStorage.isJobNodeExisted("execution/0/completed")).thenReturn(true);
		assertTrue(executionService.isCompleted(0));
	}

	@Test
	public void assertNotHaveRunningItemsWhenJNotMonitorExecution() {
		when(configService.load(true)).thenReturn(
				FactJobConfiguration
						.newBuilder(
								new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?",
										4).build(), TestSimpleJob.class.getCanonicalName())).monitorExecution(false)
						.build());
		assertFalse(executionService.hasRunningItems(Arrays.asList(0, 1, 2)));
		verify(configService).load(true);
		verify(jobNodeStorage, times(0)).isJobNodeExisted("execution/0/running");
		verify(jobNodeStorage, times(0)).isJobNodeExisted("execution/1/running");
		verify(jobNodeStorage, times(0)).isJobNodeExisted("execution/2/running");
	}

	@Test
	public void assertHasRunningItems() {
		when(configService.load(true)).thenReturn(
				FactJobConfiguration
						.newBuilder(
								new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?",
										4).build(), TestSimpleJob.class.getCanonicalName())).monitorExecution(true)
						.build());
		when(jobNodeStorage.isJobNodeExisted("execution/0/running")).thenReturn(false);
		when(jobNodeStorage.isJobNodeExisted("execution/1/running")).thenReturn(true);
		assertTrue(executionService.hasRunningItems(Arrays.asList(0, 1, 2)));
		verify(configService).load(true);
		verify(jobNodeStorage).isJobNodeExisted("execution/0/running");
		verify(jobNodeStorage).isJobNodeExisted("execution/1/running");
	}

	@Test
	public void assertNotHaveRunningItems() {
		when(configService.load(true)).thenReturn(
				FactJobConfiguration
						.newBuilder(
								new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?",
										4).build(), TestSimpleJob.class.getCanonicalName())).monitorExecution(true)
						.build());
		when(jobNodeStorage.isJobNodeExisted("execution/0/running")).thenReturn(false);
		when(jobNodeStorage.isJobNodeExisted("execution/1/running")).thenReturn(false);
		when(jobNodeStorage.isJobNodeExisted("execution/2/running")).thenReturn(false);
		assertFalse(executionService.hasRunningItems(Arrays.asList(0, 1, 2)));
		verify(configService).load(true);
		verify(jobNodeStorage).isJobNodeExisted("execution/0/running");
		verify(jobNodeStorage).isJobNodeExisted("execution/1/running");
		verify(jobNodeStorage).isJobNodeExisted("execution/2/running");
	}

	@Test
	public void assertNotHaveRunningItemsWhenJNotMonitorExecutionForAll() {
		when(configService.load(true)).thenReturn(
				FactJobConfiguration
						.newBuilder(
								new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?",
										4).build(), TestSimpleJob.class.getCanonicalName())).monitorExecution(false)
						.build());
		when(jobNodeStorage.getJobNodeChildrenKeys("execution")).thenReturn(Arrays.asList("0", "1", "2"));
		assertFalse(executionService.hasRunningItems());
		verify(configService).load(true);
		verify(jobNodeStorage).getJobNodeChildrenKeys("execution");
	}

	@Test
	public void assertHasRunningItemsForAll() {
		when(configService.load(true)).thenReturn(
				FactJobConfiguration
						.newBuilder(
								new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?",
										4).build(), TestSimpleJob.class.getCanonicalName())).monitorExecution(true)
						.build());
		when(jobNodeStorage.getJobNodeChildrenKeys("execution")).thenReturn(Arrays.asList("0", "1", "2"));
		when(jobNodeStorage.isJobNodeExisted("execution/0/running")).thenReturn(false);
		when(jobNodeStorage.isJobNodeExisted("execution/1/running")).thenReturn(true);
		assertTrue(executionService.hasRunningItems());
		verify(configService).load(true);
		verify(jobNodeStorage).getJobNodeChildrenKeys("execution");
		verify(jobNodeStorage).isJobNodeExisted("execution/0/running");
		verify(jobNodeStorage).isJobNodeExisted("execution/1/running");
	}

	@Test
	public void assertNotHaveRunningItemsForAll() {
		when(configService.load(true)).thenReturn(
				FactJobConfiguration
						.newBuilder(
								new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?",
										4).build(), TestSimpleJob.class.getCanonicalName())).monitorExecution(true)
						.build());
		when(jobNodeStorage.getJobNodeChildrenKeys("execution")).thenReturn(Arrays.asList("0", "1", "2"));
		when(jobNodeStorage.isJobNodeExisted("execution/0/running")).thenReturn(false);
		when(jobNodeStorage.isJobNodeExisted("execution/1/running")).thenReturn(false);
		when(jobNodeStorage.isJobNodeExisted("execution/2/running")).thenReturn(false);
		assertFalse(executionService.hasRunningItems());
		verify(configService).load(true);
		verify(jobNodeStorage).getJobNodeChildrenKeys("execution");
		verify(jobNodeStorage).isJobNodeExisted("execution/0/running");
		verify(jobNodeStorage).isJobNodeExisted("execution/1/running");
		verify(jobNodeStorage).isJobNodeExisted("execution/2/running");
	}

	private SegmentContexts getSegmentContext() {
		Map<Integer, String> map = new HashMap<>(3, 1);
		map.put(0, "");
		map.put(1, "");
		map.put(2, "");
		return new SegmentContexts("fake_task_id", "test_job", 10, "", map);
	}

}
  