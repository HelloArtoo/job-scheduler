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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.unitils.util.ReflectionUtils;

import com.jd.framework.job.core.internal.helper.JobNodeStorageHelper;
import com.jd.framework.job.core.internal.service.LeaderElectionService.LeaderElectionExecutionCallback;
import com.jd.framework.job.utils.env.LocalHostService;

public class LeaderElectionServiceTest {

	@Mock
	private JobNodeStorageHelper jobNodeStorage;

	@Mock
	private LocalHostService localHostService;

	@Mock
	private ServerService serverService;

	private final LeaderElectionService leaderElectionService = new LeaderElectionService(null, "test_job");

	@Before
	public void setUp() throws NoSuchFieldException {
		MockitoAnnotations.initMocks(this);
		ReflectionUtils.setFieldValue(leaderElectionService, "jobNodeStorage", jobNodeStorage);
		ReflectionUtils.setFieldValue(leaderElectionService, "localHostService", localHostService);
		ReflectionUtils.setFieldValue(leaderElectionService, "serverService", serverService);
		when(localHostService.getIp()).thenReturn("mockedIP");
		when(localHostService.getHostName()).thenReturn("mockedHostName");
	}

	@Test
	public void assertLeaderForceElection() {
		leaderElectionService.leaderForceElection();
		verify(jobNodeStorage).executeInLeader(eq("leader/election/latch"),
				Matchers.<LeaderElectionExecutionCallback> any());
	}

	@Test
	public void assertLeaderElection() {
		leaderElectionService.leaderElection();
		verify(jobNodeStorage).executeInLeader(eq("leader/election/latch"),
				Matchers.<LeaderElectionExecutionCallback> any());
	}

	@Test
	public void assertLeaderElectionExecutionCallbackWithLeader() {
		when(jobNodeStorage.isJobNodeExisted("leader/election/host")).thenReturn(true);
		leaderElectionService.new LeaderElectionExecutionCallback(false).execute();
		verify(jobNodeStorage).isJobNodeExisted("leader/election/host");
		verify(jobNodeStorage, times(0)).fillEphemeralJobNode("leader/election/host", "mockedIP");
	}

	@Test
	public void assertLeaderElectionExecutionCallbackWithoutLeaderAndIsAvailableServer() {
		when(jobNodeStorage.isJobNodeExisted("leader/election/host")).thenReturn(false);
		when(serverService.isAvailableServer("mockedIP")).thenReturn(true);
		leaderElectionService.new LeaderElectionExecutionCallback(false).execute();
		verify(jobNodeStorage).isJobNodeExisted("leader/election/host");
		verify(jobNodeStorage).fillEphemeralJobNode("leader/election/host", "mockedIP");
	}

	@Test
	public void assertLeaderElectionExecutionCallbackWithoutLeaderAndIsNotAvailableServer() {
		when(jobNodeStorage.isJobNodeExisted("leader/election/host")).thenReturn(false);
		when(serverService.isAvailableServer("mockedIP")).thenReturn(false);
		leaderElectionService.new LeaderElectionExecutionCallback(false).execute();
		verify(jobNodeStorage).isJobNodeExisted("leader/election/host");
		verify(jobNodeStorage, times(0)).fillEphemeralJobNode("leader/election/host", "mockedIP");
	}

	@Test
	public void assertLeaderForceElectionExecutionCallbackWithoutLeaderAndIsNotAvailableServer() {
		when(jobNodeStorage.isJobNodeExisted("leader/election/host")).thenReturn(false);
		when(serverService.isAvailableServer("mockedIP")).thenReturn(false);
		leaderElectionService.new LeaderElectionExecutionCallback(true).execute();
		verify(jobNodeStorage).isJobNodeExisted("leader/election/host");
		verify(jobNodeStorage).fillEphemeralJobNode("leader/election/host", "mockedIP");
	}

	@Test
	public void assertIsLeader() {
		when(jobNodeStorage.isJobNodeExisted("leader/election/host")).thenReturn(false, true);
		when(serverService.getAvailableServers()).thenReturn(Collections.singletonList("mockedIP"));
		when(jobNodeStorage.getJobNodeData("leader/election/host")).thenReturn("mockedIP");
		assertTrue(leaderElectionService.isLeader());
	}

	@Test
	public void assertIsNotLeader() {
		when(jobNodeStorage.isJobNodeExisted("leader/election/host")).thenReturn(false);
		when(serverService.getAvailableServers()).thenReturn(Collections.<String> emptyList());
		assertFalse(leaderElectionService.isLeader());
	}

	@Test
	public void assertHasLeader() {
		when(jobNodeStorage.isJobNodeExisted("leader/election/host")).thenReturn(true);
		assertTrue(leaderElectionService.hasLeader());
	}

	@Test
	public void assertRemoveLeader() {
		leaderElectionService.removeLeader();
		verify(jobNodeStorage).removeJobNodeIfExisted("leader/election/host");
	}

}
