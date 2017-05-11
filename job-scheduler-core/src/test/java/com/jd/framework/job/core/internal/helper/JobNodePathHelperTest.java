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
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class JobNodePathHelperTest {

	private JobNodePathHelper jobNodePath = new JobNodePathHelper("test_job");

	@Test
	public void assertGetFullPath() {
		assertThat(jobNodePath.getFullPath("node"), is("/test_job/node"));
	}

	@Test
	public void assertGetServerNodePath() {
		assertThat(jobNodePath.getServerNodePath(), is("/test_job/servers"));
	}

	@Test
	public void assertGetServerNodePathForServerIp() {
		assertThat(jobNodePath.getServerNodePath("ip0"), is("/test_job/servers/ip0"));
	}

	@Test
	public void assertGetServerNodePathForServerIpAndNameNode() {
		assertThat(jobNodePath.getServerNodePath("ip0", "node"), is("/test_job/servers/ip0/node"));
	}

	@Test
	public void assertGetExecutionNodePath() {
		assertThat(jobNodePath.getExecutionNodePath(), is("/test_job/execution"));
	}

	@Test
	public void assertGetExecutionNodePathWihItemAndNode() {
		assertThat(jobNodePath.getExecutionNodePath("0", "running"), is("/test_job/execution/0/running"));
	}

	@Test
	public void assertGetLeaderIpNodePath() {
		assertThat(jobNodePath.getLeaderHostNodePath(), is("/test_job/leader/election/host"));
	}

}
