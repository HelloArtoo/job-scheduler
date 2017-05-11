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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.unitils.util.ReflectionUtils;

import com.jd.framework.job.constant.job.ServerStatus;
import com.jd.framework.job.core.internal.helper.JobNodeStorageHelper;
import com.jd.framework.job.core.internal.helper.ServerNodeHelper;
import com.jd.framework.job.utils.env.LocalHostService;

public class ServerServiceTest {

	@Mock
	private JobNodeStorageHelper jobNodeStorage;

	@Mock
	private LocalHostService localHostService;

	private final ServerService serverService = new ServerService(null, "test_job");

	@Before
	public void setUp() throws NoSuchFieldException {
		MockitoAnnotations.initMocks(this);
		ReflectionUtils.setFieldValue(serverService, "jobNodeStorage", jobNodeStorage);
		ReflectionUtils.setFieldValue(serverService, "localHostService", localHostService);
		when(localHostService.getIp()).thenReturn("mockedIP");
		when(localHostService.getHostName()).thenReturn("mockedHostName");
	}

	@Test
	public void assertClearPreviousServerStatus() {
		serverService.clearPreviousServerStatus();
		verify(jobNodeStorage).removeJobNodeIfExisted(ServerNodeHelper.getStatusNode("mockedIP"));
		verify(jobNodeStorage).removeJobNodeIfExisted(ServerNodeHelper.getShutdownNode("mockedIP"));
	}

	@Test
	public void assertPersistServerOnlineForDisabledServerWithLeaderElecting() {
		serverService.persistServerOnline(false);
		verify(jobNodeStorage).fillJobNode("servers/mockedIP/hostName", "mockedHostName");
		verify(localHostService, times(4)).getIp();
		verify(localHostService).getHostName();
		verify(jobNodeStorage).fillJobNode("servers/mockedIP/disabled", "");
		verify(jobNodeStorage).fillEphemeralJobNode("servers/mockedIP/status", ServerStatus.READY);
		verify(jobNodeStorage).removeJobNodeIfExisted("servers/mockedIP/shutdown");
	}

	@Test
	public void assertPersistServerOnlineForEnabledServer() {
		serverService.persistServerOnline(true);
		verify(jobNodeStorage).fillJobNode("servers/mockedIP/hostName", "mockedHostName");
		verify(localHostService, times(4)).getIp();
		verify(localHostService).getHostName();
		verify(jobNodeStorage).removeJobNodeIfExisted("servers/mockedIP/disabled");
		verify(jobNodeStorage).fillEphemeralJobNode("servers/mockedIP/status", ServerStatus.READY);
	}

	@Test
	public void assertClearJobTriggerStatus() {
		serverService.clearJobTriggerStatus();
		verify(jobNodeStorage).removeJobNodeIfExisted("servers/mockedIP/trigger");
	}

	@Test
	public void assertClearJobPausedStatus() {
		serverService.clearJobPausedStatus();
		verify(jobNodeStorage).removeJobNodeIfExisted("servers/mockedIP/paused");
	}

	@Test
	public void assertIsJobPausedManually() {
		when(jobNodeStorage.isJobNodeExisted("servers/mockedIP/paused")).thenReturn(true);
		assertTrue(serverService.isJobPausedManually());
		verify(jobNodeStorage).isJobNodeExisted("servers/mockedIP/paused");
	}

	@Test
	public void assertProcessServerShutdown() {
		serverService.processServerShutdown();
		verify(jobNodeStorage).removeJobNodeIfExisted("servers/mockedIP/status");
	}

	@Test
	public void assertUpdateServerStatus() {
		serverService.updateServerStatus(ServerStatus.RUNNING);
		verify(jobNodeStorage).updateJobNode("servers/mockedIP/status", ServerStatus.RUNNING);
	}

	@Test
	public void assertRemoveServerStatus() {
		serverService.removeServerStatus();
		verify(jobNodeStorage).removeJobNodeIfExisted("servers/mockedIP/status");
	}

	@Test
	public void assertGetAllServers() {
		when(jobNodeStorage.getJobNodeChildrenKeys("servers")).thenReturn(
				Arrays.asList("host0", "host2", "host1", "host3"));
		assertThat(serverService.getAllServers(), is(Arrays.asList("host0", "host1", "host2", "host3")));
		verify(jobNodeStorage).getJobNodeChildrenKeys("servers");
	}

	@Test
	public void assertGetAvailableSegmentServers() {
		when(jobNodeStorage.getJobNodeChildrenKeys("servers")).thenReturn(
				Arrays.asList("host0", "host2", "host1", "host3", "host4"));
		when(jobNodeStorage.isJobNodeExisted("servers/host0/status")).thenReturn(true);
		when(jobNodeStorage.isJobNodeExisted("servers/host0/disabled")).thenReturn(false);
		when(jobNodeStorage.isJobNodeExisted("servers/host1/status")).thenReturn(true);
		when(jobNodeStorage.isJobNodeExisted("servers/host1/disabled")).thenReturn(true);
		when(jobNodeStorage.isJobNodeExisted("servers/host2/status")).thenReturn(false);
		when(jobNodeStorage.isJobNodeExisted("servers/host3/status")).thenReturn(true);
		when(jobNodeStorage.isJobNodeExisted("servers/host3/disabled")).thenReturn(false);
		when(jobNodeStorage.isJobNodeExisted("servers/host4/status")).thenReturn(true);
		when(jobNodeStorage.isJobNodeExisted("servers/host4/paused")).thenReturn(true);
		assertThat(serverService.getAvailableSegmentServers(), is(Arrays.asList("host0", "host3", "host4")));
		verify(jobNodeStorage).getJobNodeChildrenKeys("servers");
		verify(jobNodeStorage).isJobNodeExisted("servers/host0/status");
		verify(jobNodeStorage).isJobNodeExisted("servers/host0/disabled");
		verify(jobNodeStorage).isJobNodeExisted("servers/host1/status");
		verify(jobNodeStorage).isJobNodeExisted("servers/host1/disabled");
		verify(jobNodeStorage).isJobNodeExisted("servers/host2/status");
		verify(jobNodeStorage).isJobNodeExisted("servers/host3/status");
		verify(jobNodeStorage).isJobNodeExisted("servers/host3/disabled");
		verify(jobNodeStorage).isJobNodeExisted("servers/host4/status");
		verify(jobNodeStorage).isJobNodeExisted("servers/host4/disabled");
	}

	@Test
	public void assertGetAvailableServers() {
		when(jobNodeStorage.getJobNodeChildrenKeys("servers")).thenReturn(
				Arrays.asList("host0", "host2", "host1", "host3", "host4"));
		when(jobNodeStorage.isJobNodeExisted("servers/host0/status")).thenReturn(true);
		when(jobNodeStorage.isJobNodeExisted("servers/host0/disabled")).thenReturn(false);
		when(jobNodeStorage.isJobNodeExisted("servers/host1/status")).thenReturn(true);
		when(jobNodeStorage.isJobNodeExisted("servers/host1/disabled")).thenReturn(true);
		when(jobNodeStorage.isJobNodeExisted("servers/host2/status")).thenReturn(false);
		when(jobNodeStorage.isJobNodeExisted("servers/host3/status")).thenReturn(true);
		when(jobNodeStorage.isJobNodeExisted("servers/host3/disabled")).thenReturn(false);
		when(jobNodeStorage.isJobNodeExisted("servers/host4/status")).thenReturn(true);
		when(jobNodeStorage.isJobNodeExisted("servers/host4/paused")).thenReturn(true);
		assertThat(serverService.getAvailableServers(), is(Arrays.asList("host0", "host3")));
		verify(jobNodeStorage).getJobNodeChildrenKeys("servers");
		verify(jobNodeStorage).isJobNodeExisted("servers/host0/status");
		verify(jobNodeStorage).isJobNodeExisted("servers/host0/disabled");
		verify(jobNodeStorage).isJobNodeExisted("servers/host1/status");
		verify(jobNodeStorage).isJobNodeExisted("servers/host1/disabled");
		verify(jobNodeStorage).isJobNodeExisted("servers/host2/status");
		verify(jobNodeStorage).isJobNodeExisted("servers/host3/status");
		verify(jobNodeStorage).isJobNodeExisted("servers/host3/disabled");
		verify(jobNodeStorage).isJobNodeExisted("servers/host4/status");
		verify(jobNodeStorage).isJobNodeExisted("servers/host4/paused");
	}

	@Test
	public void assertIsLocalhostServerReadyWhenServerCrashed() {
		when(jobNodeStorage.isJobNodeExisted("servers/mockedIP/status")).thenReturn(false);
		assertFalse(serverService.isLocalhostServerReady());
		verify(localHostService).getIp();
		verify(jobNodeStorage).isJobNodeExisted("servers/mockedIP/status");
	}

	@Test
	public void assertIsLocalhostServerReadyWhenServerPaused() {
		when(jobNodeStorage.isJobNodeExisted("servers/mockedIP/status")).thenReturn(true);
		when(jobNodeStorage.isJobNodeExisted("servers/mockedIP/paused")).thenReturn(true);
		assertFalse(serverService.isLocalhostServerReady());
		verify(localHostService).getIp();
		verify(jobNodeStorage).isJobNodeExisted("servers/mockedIP/paused");
	}

	@Test
	public void assertIsLocalhostServerReadyWhenServerDisabled() {
		when(jobNodeStorage.isJobNodeExisted("servers/mockedIP/status")).thenReturn(true);
		when(jobNodeStorage.isJobNodeExisted("servers/mockedIP/paused")).thenReturn(false);
		when(jobNodeStorage.isJobNodeExisted("servers/mockedIP/disabled")).thenReturn(true);
		assertFalse(serverService.isLocalhostServerReady());
		verify(localHostService).getIp();
		verify(jobNodeStorage).isJobNodeExisted("servers/mockedIP/paused");
		verify(jobNodeStorage).isJobNodeExisted("servers/mockedIP/disabled");
	}

	@Test
	public void assertIsLocalhostServerReadyWhenServerShutdown() {
		when(jobNodeStorage.isJobNodeExisted("servers/mockedIP/status")).thenReturn(true);
		when(jobNodeStorage.isJobNodeExisted("servers/mockedIP/paused")).thenReturn(false);
		when(jobNodeStorage.isJobNodeExisted("servers/mockedIP/disabled")).thenReturn(false);
		when(jobNodeStorage.isJobNodeExisted("servers/mockedIP/shutdown")).thenReturn(true);
		assertFalse(serverService.isLocalhostServerReady());
		verify(localHostService).getIp();
		verify(jobNodeStorage).isJobNodeExisted("servers/mockedIP/paused");
		verify(jobNodeStorage).isJobNodeExisted("servers/mockedIP/disabled");
		verify(jobNodeStorage).isJobNodeExisted("servers/mockedIP/shutdown");
	}

	@Test
	public void assertIsLocalhostServerReadyWhenServerRunning() {
		when(jobNodeStorage.isJobNodeExisted("servers/mockedIP/status")).thenReturn(true);
		when(jobNodeStorage.isJobNodeExisted("servers/mockedIP/paused")).thenReturn(false);
		when(jobNodeStorage.isJobNodeExisted("servers/mockedIP/disabled")).thenReturn(false);
		when(jobNodeStorage.isJobNodeExisted("servers/mockedIP/shutdown")).thenReturn(false);
		when(jobNodeStorage.isJobNodeExisted("servers/mockedIP/status")).thenReturn(true);
		when(jobNodeStorage.getJobNodeData("servers/mockedIP/status")).thenReturn("RUNNING");
		assertFalse(serverService.isLocalhostServerReady());
		verify(jobNodeStorage).isJobNodeExisted("servers/mockedIP/status");
		verify(localHostService).getIp();
		verify(jobNodeStorage).isJobNodeExisted("servers/mockedIP/paused");
		verify(jobNodeStorage).isJobNodeExisted("servers/mockedIP/disabled");
		verify(jobNodeStorage).isJobNodeExisted("servers/mockedIP/shutdown");
		verify(jobNodeStorage).getJobNodeData("servers/mockedIP/status");
	}

	@Test
	public void assertIsLocalhostServerReadyWhenServerReady() {
		when(jobNodeStorage.isJobNodeExisted("servers/mockedIP/status")).thenReturn(true);
		when(jobNodeStorage.isJobNodeExisted("servers/mockedIP/paused")).thenReturn(false);
		when(jobNodeStorage.isJobNodeExisted("servers/mockedIP/disabled")).thenReturn(false);
		when(jobNodeStorage.isJobNodeExisted("servers/mockedIP/shutdown")).thenReturn(false);
		when(jobNodeStorage.isJobNodeExisted("servers/mockedIP/status")).thenReturn(true);
		when(jobNodeStorage.getJobNodeData("servers/mockedIP/status")).thenReturn("READY");
		assertTrue(serverService.isLocalhostServerReady());
		verify(localHostService).getIp();
		verify(jobNodeStorage).isJobNodeExisted("servers/mockedIP/status");
		verify(jobNodeStorage).isJobNodeExisted("servers/mockedIP/paused");
		verify(jobNodeStorage).isJobNodeExisted("servers/mockedIP/disabled");
		verify(jobNodeStorage).isJobNodeExisted("servers/mockedIP/shutdown");
		verify(jobNodeStorage).getJobNodeData("servers/mockedIP/status");
	}

	@Test
	public void assertIsLocalhostServerEnabled() {
		when(jobNodeStorage.isJobNodeExisted("servers/mockedIP/disabled")).thenReturn(false);
		assertTrue(serverService.isLocalhostServerEnabled());
		verify(localHostService).getIp();
		verify(jobNodeStorage).isJobNodeExisted("servers/mockedIP/disabled");
	}

	@Test
	public void assertHasStatusNode() {
		when(jobNodeStorage.isJobNodeExisted(ServerNodeHelper.getStatusNode("IP"))).thenReturn(true);
		serverService.hasStatusNode("IP");
		verify(jobNodeStorage).isJobNodeExisted(ServerNodeHelper.getStatusNode("IP"));
	}
}
