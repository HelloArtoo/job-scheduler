/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.regcenter.zookeeper;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;
import org.apache.zookeeper.KeeperException.NoAuthException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jd.framework.job.fixture.reg.EmbedTestingServer;
import com.jd.framework.job.regcenter.ZookeeperRegistryCenter;
import com.jd.framework.job.regcenter.conf.ZookeeperConfiguration;
import com.jd.framework.job.regcenter.util.ZookeeperRegistryCenterTestUtil;

public class ZookeeperRegistryCenter4AuthTest {

	private static final String NAME_SPACE = ZookeeperRegistryCenter4AuthTest.class.getName();

	private static final ZookeeperConfiguration ZOOKEEPER_CONFIGURATION = new ZookeeperConfiguration(
			EmbedTestingServer.getConnectionString(), NAME_SPACE);

	private static ZookeeperRegistryCenter zkRegCenter;

	@BeforeClass
	public static void setUp() {
		EmbedTestingServer.start();
		ZOOKEEPER_CONFIGURATION.setDigest("digest:password");
		ZOOKEEPER_CONFIGURATION.setSessionTimeoutMilliseconds(5000);
		ZOOKEEPER_CONFIGURATION.setConnectionTimeoutMilliseconds(5000);
		zkRegCenter = new ZookeeperRegistryCenter(ZOOKEEPER_CONFIGURATION);
		zkRegCenter.init();
		ZookeeperRegistryCenterTestUtil.persist(zkRegCenter);
	}

	@AfterClass
	public static void tearDown() {
		zkRegCenter.close();
	}

	@Test
	public void assertInitWithDigestSuccess() throws Exception {
		CuratorFramework client = CuratorFrameworkFactory.builder()
				.connectString(EmbedTestingServer.getConnectionString()).retryPolicy(new RetryOneTime(2000))
				.authorization("digest", "digest:password".getBytes()).build();
		client.start();
		client.blockUntilConnected();
		assertThat(
				client.getData()
						.forPath("/" + ZookeeperRegistryCenter4AuthTest.class.getName() + "/test/deep/nested"),
				is("deepNested".getBytes()));
	}

	@Test(expected = NoAuthException.class)
	public void assertInitWithDigestFailure() throws Exception {
		CuratorFramework client = CuratorFrameworkFactory.newClient(EmbedTestingServer.getConnectionString(),
				new RetryOneTime(2000));
		client.start();
		client.blockUntilConnected();
		client.getData().forPath("/" + ZookeeperRegistryCenter4AuthTest.class.getName() + "/test/deep/nested");
	}
}
