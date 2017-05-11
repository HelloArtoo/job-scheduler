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
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.curator.framework.api.transaction.CuratorTransactionBridge;
import org.apache.curator.framework.api.transaction.CuratorTransactionFinal;
import org.apache.curator.framework.api.transaction.TransactionCreateBuilder;
import org.apache.curator.framework.api.transaction.TransactionDeleteBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.unitils.util.ReflectionUtils;

import com.jd.framework.job.config.core.JobCoreConfiguration;
import com.jd.framework.job.config.type.SimpleJobConfiguration;
import com.jd.framework.job.core.api.strategy.impl.AverageAllocationJobSegmentStrategy;
import com.jd.framework.job.core.config.FactJobConfiguration;
import com.jd.framework.job.core.fixture.TestSimpleJob;
import com.jd.framework.job.core.internal.callback.TransactionExecutionCallback;
import com.jd.framework.job.core.internal.helper.JobNodeStorageHelper;
import com.jd.framework.job.core.internal.helper.SegmentNodeHelper;
import com.jd.framework.job.utils.env.LocalHostService;

public class SegmentServiceTest {

	@Mock
	private JobNodeStorageHelper jobNodeStorage;

	@Mock
	private LocalHostService localHostService;

	@Mock
	private LeaderElectionService leaderElectionService;

	@Mock
	private ConfigService configService;

	@Mock
	private ExecutionService executionService;

	@Mock
	private ServerService serverService;

	private final SegmentService segmentService = new SegmentService(null, "test_job");

	@Before
	public void setUp() throws NoSuchFieldException {
		MockitoAnnotations.initMocks(this);
		ReflectionUtils.setFieldValue(segmentService, "jobNodeStorage", jobNodeStorage);
		ReflectionUtils.setFieldValue(segmentService, "localHostService", localHostService);
		ReflectionUtils.setFieldValue(segmentService, "leaderElectionService", leaderElectionService);
		ReflectionUtils.setFieldValue(segmentService, "configService", configService);
		ReflectionUtils.setFieldValue(segmentService, "executionService", executionService);
		ReflectionUtils.setFieldValue(segmentService, "serverService", serverService);
		when(localHostService.getIp()).thenReturn("mockedIP");
		when(localHostService.getHostName()).thenReturn("mockedHostName");
	}

	@Test
	public void assertSetResegmentFlag() {
		segmentService.setResegmentFlag();
		verify(jobNodeStorage).createJobNodeIfNeeded("leader/segment/necessary");
	}

	@Test
	public void assertIsNeedSegment() {
		when(jobNodeStorage.isJobNodeExisted("leader/segment/necessary")).thenReturn(true);
		assertTrue(segmentService.isNeedSegment());
		verify(jobNodeStorage).isJobNodeExisted("leader/segment/necessary");
	}

	@Test
	public void assertSegmentWhenUnnecessary() {
		when(serverService.getAvailableSegmentServers()).thenReturn(Collections.singletonList("mockedIP"));
		when(jobNodeStorage.isJobNodeExisted("leader/segment/necessary")).thenReturn(false);
		segmentService.segmentIfNecessary();
		verify(serverService).getAvailableSegmentServers();
		verify(jobNodeStorage).isJobNodeExisted("leader/segment/necessary");
	}

	@Test
	public void assertSegmentWithoutAvailableServers() {
		when(serverService.getAllServers()).thenReturn(Arrays.asList("ip1", "ip2"));
		when(serverService.getAvailableSegmentServers()).thenReturn(Collections.<String> emptyList());
		segmentService.segmentIfNecessary();
		verify(serverService).getAvailableSegmentServers();
		verify(serverService).getAllServers();
		verify(jobNodeStorage).removeJobNodeIfExisted("servers/ip1/segment");
		verify(jobNodeStorage).removeJobNodeIfExisted("servers/ip2/segment");
		verify(jobNodeStorage, times(0)).isJobNodeExisted("leader/segment/necessary");
	}

	@Test
	public void assertSegmentWhenIsNotLeaderAndIsSegmentProcessing() {
		when(serverService.getAvailableSegmentServers()).thenReturn(Collections.singletonList("mockedIP"));
		when(jobNodeStorage.isJobNodeExisted("leader/segment/necessary")).thenReturn(true, true, false, false);
		when(leaderElectionService.isLeader()).thenReturn(false);
		when(jobNodeStorage.isJobNodeExisted("leader/segment/processing")).thenReturn(true, false);
		segmentService.segmentIfNecessary();
		verify(serverService).getAvailableSegmentServers();
		verify(jobNodeStorage, times(4)).isJobNodeExisted("leader/segment/necessary");
		verify(jobNodeStorage, times(2)).isJobNodeExisted("leader/segment/processing");
	}

	@Test
	public void assertSegmentNecessaryWhenMonitorExecutionEnabled() {
		when(serverService.getAvailableSegmentServers()).thenReturn(Collections.singletonList("mockedIP"));
		when(jobNodeStorage.isJobNodeExisted("leader/segment/necessary")).thenReturn(true);
		when(leaderElectionService.isLeader()).thenReturn(true);
		when(configService.load(false)).thenReturn(
				FactJobConfiguration
						.newBuilder(
								new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?",
										3).build(), TestSimpleJob.class.getCanonicalName())).monitorExecution(true)
						.jobSegmentStrategyClass(AverageAllocationJobSegmentStrategy.class.getCanonicalName())
						.build());
		when(serverService.getAllServers()).thenReturn(Arrays.asList("ip1", "ip2"));
		when(executionService.hasRunningItems()).thenReturn(true, false);
		segmentService.segmentIfNecessary();
		verify(serverService).getAvailableSegmentServers();
		verify(jobNodeStorage).isJobNodeExisted("leader/segment/necessary");
		verify(leaderElectionService).isLeader();
		verify(configService).load(false);
		verify(executionService, times(2)).hasRunningItems();
		verify(jobNodeStorage).removeJobNodeIfExisted("servers/ip1/segment");
		verify(jobNodeStorage).removeJobNodeIfExisted("servers/ip2/segment");
		verify(jobNodeStorage).fillEphemeralJobNode("leader/segment/processing", "");
		verify(jobNodeStorage).executeInTransaction(any(TransactionExecutionCallback.class));
	}

	@Test
	public void assertSegmentNecessaryWhenMonitorExecutionDisabled() throws Exception {
		when(serverService.getAvailableSegmentServers()).thenReturn(Collections.singletonList("mockedIP"));
		when(jobNodeStorage.isJobNodeExisted("leader/segment/necessary")).thenReturn(true);
		when(leaderElectionService.isLeader()).thenReturn(true);
		when(configService.load(false)).thenReturn(
				FactJobConfiguration
						.newBuilder(
								new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?",
										3).build(), TestSimpleJob.class.getCanonicalName())).monitorExecution(false)
						.jobSegmentStrategyClass(AverageAllocationJobSegmentStrategy.class.getCanonicalName())
						.build());
		when(serverService.getAllServers()).thenReturn(Arrays.asList("ip1", "ip2"));
		segmentService.segmentIfNecessary();
		verify(serverService).getAvailableSegmentServers();
		verify(jobNodeStorage).isJobNodeExisted("leader/segment/necessary");
		verify(leaderElectionService).isLeader();
		verify(configService).load(false);
		verify(jobNodeStorage).removeJobNodeIfExisted("servers/ip1/segment");
		verify(jobNodeStorage).removeJobNodeIfExisted("servers/ip2/segment");
		verify(jobNodeStorage).fillEphemeralJobNode("leader/segment/processing", "");
		verify(jobNodeStorage).executeInTransaction(any(TransactionExecutionCallback.class));
	}

	@Test
	public void assertGetLocalHostSegmentItemsWhenNodeExisted() {
		when(jobNodeStorage.isJobNodeExisted("servers/mockedIP/segment")).thenReturn(true);
		when(jobNodeStorage.getJobNodeDataDirectly("servers/mockedIP/segment")).thenReturn("0,1,2");
		assertThat(segmentService.getLocalHostSegmentItems(), is(Arrays.asList(0, 1, 2)));
		verify(jobNodeStorage).isJobNodeExisted("servers/mockedIP/segment");
		verify(jobNodeStorage).getJobNodeDataDirectly("servers/mockedIP/segment");
	}

	@Test
	public void assertGetLocalHostSegmentWhenNodeNotExisted() {
		when(jobNodeStorage.isJobNodeExisted("servers/mockedIP/segment")).thenReturn(false);
		assertThat(segmentService.getLocalHostSegmentItems(), is(Collections.EMPTY_LIST));
		verify(jobNodeStorage).isJobNodeExisted("servers/mockedIP/segment");
	}

	@Test
	public void assertPersistSegmentInfoTransactionExecutionCallback() throws Exception {
		CuratorTransactionFinal curatorTransactionFinal = mock(CuratorTransactionFinal.class);
		TransactionCreateBuilder transactionCreateBuilder = mock(TransactionCreateBuilder.class);
		TransactionDeleteBuilder transactionDeleteBuilder = mock(TransactionDeleteBuilder.class);
		CuratorTransactionBridge curatorTransactionBridge = mock(CuratorTransactionBridge.class);
		when(curatorTransactionFinal.create()).thenReturn(transactionCreateBuilder);
		when(transactionCreateBuilder.forPath("/test_job/servers/host0/segment", "0,1,2".getBytes())).thenReturn(
				curatorTransactionBridge);
		when(curatorTransactionBridge.and()).thenReturn(curatorTransactionFinal);
		when(curatorTransactionFinal.delete()).thenReturn(transactionDeleteBuilder);
		when(transactionDeleteBuilder.forPath("/test_job/leader/segment/necessary")).thenReturn(
				curatorTransactionBridge);
		when(curatorTransactionBridge.and()).thenReturn(curatorTransactionFinal);
		when(curatorTransactionFinal.delete()).thenReturn(transactionDeleteBuilder);
		when(transactionDeleteBuilder.forPath("/test_job/leader/segment/processing")).thenReturn(
				curatorTransactionBridge);
		when(curatorTransactionBridge.and()).thenReturn(curatorTransactionFinal);
		Map<String, List<Integer>> segmentItems = new HashMap<>(1);
		segmentItems.put("host0", Arrays.asList(0, 1, 2));
		SegmentService.PersistSegmentInfoTransactionExecutionCallback actual = segmentService.new PersistSegmentInfoTransactionExecutionCallback(
				segmentItems);
		actual.execute(curatorTransactionFinal);
		verify(curatorTransactionFinal).create();
		verify(transactionCreateBuilder).forPath("/test_job/servers/host0/segment", "0,1,2".getBytes());
		verify(curatorTransactionFinal, times(2)).delete();
		verify(transactionDeleteBuilder).forPath("/test_job/leader/segment/necessary");
		verify(transactionDeleteBuilder).forPath("/test_job/leader/segment/processing");
		verify(curatorTransactionBridge, times(3)).and();
	}

	@Test
	public void assertNotRunningAndSegmentNodeExisted() throws NoSuchFieldException {
		when(jobNodeStorage.isJobNodeExisted(SegmentNodeHelper.getSegmentNode("ip3"))).thenReturn(true);
		when(serverService.hasStatusNode(SegmentNodeHelper.getSegmentNode("ip3"))).thenReturn(false);
		when(serverService.getAllServers()).thenReturn(Arrays.asList("ip1", "ip2", "ip3"));
		ReflectionUtils.setFieldValue(segmentService, "jobNodeStorage", jobNodeStorage);
		ReflectionUtils.setFieldValue(segmentService, "serverService", serverService);
		assertThat(segmentService.hasNotRunningSegmentNode(), is(true));
	}

}
