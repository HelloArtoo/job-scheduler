/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.core.internal.helper;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.jd.framework.job.utils.env.LocalHostService;

public class ServerNodeHelperTest {

	private LocalHostService localHostService = new LocalHostService();

	private ServerNodeHelper serverNode = new ServerNodeHelper("test_job");

	@Test
	public void assertGetHostNameNode() {
		assertThat(ServerNodeHelper.getHostNameNode("host0"), is("servers/host0/hostName"));
	}

	@Test
	public void assertGetStatusNode() {
		assertThat(ServerNodeHelper.getStatusNode("host0"), is("servers/host0/status"));
	}

	@Test
	public void assertGetTriggerNode() {
		assertThat(ServerNodeHelper.getTriggerNode("host0"), is("servers/host0/trigger"));
	}

	@Test
	public void assertGetDisabledNode() {
		assertThat(ServerNodeHelper.getDisabledNode("host0"), is("servers/host0/disabled"));
	}

	@Test
	public void assertPausedNode() {
		assertThat(ServerNodeHelper.getPausedNode("host0"), is("servers/host0/paused"));
	}

	@Test
	public void assertShutdownNode() {
		assertThat(ServerNodeHelper.getShutdownNode("host0"), is("servers/host0/shutdown"));
	}

	@Test
	public void assertIsLocalJobTriggerPath() {
		assertTrue(serverNode.isLocalJobTriggerPath("/test_job/servers/" + localHostService.getIp() + "/trigger"));
	}

	@Test
	public void assertIsLocalJobPausedPath() {
		assertTrue(serverNode.isLocalJobPausedPath("/test_job/servers/" + localHostService.getIp() + "/paused"));
	}

	@Test
	public void assertIsLocalJobShutdownPath() {
		assertTrue(serverNode.isLocalJobShutdownPath("/test_job/servers/" + localHostService.getIp() + "/shutdown"));
	}

	@Test
	public void assertIsLocalServerDisabledPath() {
		assertTrue(serverNode.isLocalServerDisabledPath("/test_job/servers/" + localHostService.getIp() + "/disabled"));
	}

	@Test
	public void assertIsServerStatusPath() {
		assertTrue(serverNode.isServerStatusPath("/test_job/servers/host0/status"));
		assertFalse(serverNode.isServerStatusPath("/otherJob/servers/host0/status"));
		assertFalse(serverNode.isServerStatusPath("/test_job/servers/host0/disabled"));
	}

	@Test
	public void assertIsServerDisabledPath() {
		assertTrue(serverNode.isServerDisabledPath("/test_job/servers/host0/disabled"));
		assertFalse(serverNode.isServerDisabledPath("/otherJob/servers/host0/status"));
		assertFalse(serverNode.isServerDisabledPath("/test_job/servers/host0/status"));
	}

	@Test
	public void assertIsServerShutdownPath() {
		assertTrue(serverNode.isServerShutdownPath("/test_job/servers/host0/shutdown"));
		assertFalse(serverNode.isServerShutdownPath("/otherJob/servers/host0/status"));
		assertFalse(serverNode.isServerShutdownPath("/test_job/servers/host0/status"));
	}

}
