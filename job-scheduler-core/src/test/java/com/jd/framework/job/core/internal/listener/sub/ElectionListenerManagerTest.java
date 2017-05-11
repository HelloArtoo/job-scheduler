/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.core.internal.listener.sub;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.unitils.util.ReflectionUtils;

import com.jd.framework.job.core.internal.helper.JobNodeStorageHelper;
import com.jd.framework.job.core.internal.helper.ServerNodeHelper;
import com.jd.framework.job.core.internal.listener.sub.ElectionListenerManager.LeaderElectionJobListener;
import com.jd.framework.job.core.internal.service.LeaderElectionService;
import com.jd.framework.job.core.internal.service.ServerService;

public class ElectionListenerManagerTest {

	@Mock
	private JobNodeStorageHelper jobNodeStorage;

	@Mock
	private ServerNodeHelper serverNode;

	@Mock
	private LeaderElectionService leaderElectionService;

	@Mock
	private ServerService serverService;

	private final ElectionListenerManager electionListenerManager = new ElectionListenerManager(null, "test_job");

	@Before
	public void setUp() throws NoSuchFieldException {
		MockitoAnnotations.initMocks(this);
		ReflectionUtils.setFieldValue(electionListenerManager, electionListenerManager.getClass().getSuperclass()
				.getDeclaredField("jobNodeStorage"), jobNodeStorage);
		ReflectionUtils.setFieldValue(electionListenerManager, "serverNode", serverNode);
		ReflectionUtils.setFieldValue(electionListenerManager, "leaderElectionService", leaderElectionService);
		ReflectionUtils.setFieldValue(electionListenerManager, "serverService", serverService);
	}

	@Test
	public void assertStart() {
		electionListenerManager.start();
		verify(jobNodeStorage).addDataListener(Matchers.<LeaderElectionJobListener> any());
	}

	@Test
	public void assertLeaderElectionJobListenerWhenIsNotLeaderHostPath() {
		electionListenerManager.new LeaderElectionJobListener().dataChanged(null,
				new TreeCacheEvent(TreeCacheEvent.Type.NODE_ADDED, new ChildData("/test_job/leader/election/other",
						null, "localhost".getBytes())), "/test_job/leader/election/other");
		verify(leaderElectionService, times(0)).leaderElection();
	}

	@Test
	public void assertLeaderElectionJobListenerWhenIsLeaderHostPathButNotRemove() {
		electionListenerManager.new LeaderElectionJobListener().dataChanged(null,
				new TreeCacheEvent(TreeCacheEvent.Type.NODE_ADDED, new ChildData("/test_job/leader/election/host",
						null, "localhost".getBytes())), "/test_job/leader/election/host");
		verify(leaderElectionService, times(0)).leaderElection();
	}

	@Test
	public void assertLeaderElectionJobListenerWhenIsLeaderHostPathAndIsRemoveAndIsLeader() {
		when(leaderElectionService.hasLeader()).thenReturn(true);
		electionListenerManager.new LeaderElectionJobListener().dataChanged(null,
				new TreeCacheEvent(TreeCacheEvent.Type.NODE_REMOVED, new ChildData("/test_job/leader/election/host",
						null, "localhost".getBytes())), "/test_job/leader/election/host");
		verify(leaderElectionService).hasLeader();
		verify(leaderElectionService, times(0)).leaderElection();
	}

	@Test
	public void assertLeaderElectionJobListenerWhenIsLeaderHostPathAndIsRemoveAndIsNotLeaderWithAvailableServers() {
		when(leaderElectionService.hasLeader()).thenReturn(false);
		when(serverService.getAvailableServers()).thenReturn(Collections.singletonList("localhost"));
		electionListenerManager.new LeaderElectionJobListener().dataChanged(null,
				new TreeCacheEvent(TreeCacheEvent.Type.NODE_REMOVED, new ChildData("/test_job/leader/election/host",
						null, "localhost".getBytes())), "/test_job/leader/election/host");
		verify(leaderElectionService).hasLeader();
		verify(serverService).getAvailableServers();
		verify(leaderElectionService).leaderElection();
	}

	@Test
	public void assertLeaderElectionJobListenerWhenIsLeaderHostPathAndIsRemoveAndIsNotLeaderWithoutAvailableServers() {
		when(leaderElectionService.hasLeader()).thenReturn(false);
		when(serverService.getAvailableServers()).thenReturn(Collections.<String> emptyList());
		electionListenerManager.new LeaderElectionJobListener().dataChanged(null,
				new TreeCacheEvent(TreeCacheEvent.Type.NODE_REMOVED, new ChildData("/test_job/leader/election/host",
						null, "localhost".getBytes())), "/test_job/leader/election/host");
		verify(leaderElectionService).hasLeader();
		verify(serverService).getAvailableServers();
		verify(leaderElectionService, times(0)).leaderElection();
	}

	@Test
	public void assertLeaderElectionJobListenerWhenJobDisabledAndIsNotLeader() {
		when(leaderElectionService.isLeader()).thenReturn(false);
		when(serverNode.isLocalJobPausedPath("/test_job/server/mockedIP/disabled")).thenReturn(true);
		electionListenerManager.new LeaderElectionJobListener().dataChanged(null,
				new TreeCacheEvent(TreeCacheEvent.Type.NODE_ADDED, new ChildData("/test_job/server/mockedIP/disabled",
						null, "localhost".getBytes())), "/test_job/server/mockedIP/disabled");
		verify(leaderElectionService, times(0)).removeLeader();
	}

	@Test
	public void assertLeaderElectionJobListenerWhenJobShutdownAndIsLeader() {
		when(leaderElectionService.isLeader()).thenReturn(true);
		when(serverNode.isLocalJobPausedPath("/test_job/server/mockedIP/shutdown")).thenReturn(true);
		electionListenerManager.new LeaderElectionJobListener().dataChanged(null,
				new TreeCacheEvent(TreeCacheEvent.Type.NODE_ADDED, new ChildData("/test_job/server/mockedIP/shutdown",
						null, "localhost".getBytes())), "/test_job/server/mockedIP/shutdown");
		verify(leaderElectionService).removeLeader();
	}

}
