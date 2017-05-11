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
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jd.framework.job.fixture.reg.EmbedTestingServer;
import com.jd.framework.job.regcenter.ZookeeperRegistryCenter;
import com.jd.framework.job.regcenter.conf.ZookeeperConfiguration;
import com.jd.framework.job.regcenter.util.ZookeeperRegistryCenterTestUtil;

public class ZookeeperRegistryCenter4ModifyTest {

	private static final ZookeeperConfiguration ZOOKEEPER_CONFIGURATION = new ZookeeperConfiguration(
			EmbedTestingServer.getConnectionString(), ZookeeperRegistryCenter4ModifyTest.class.getName());

	private static ZookeeperRegistryCenter zkRegCenter;

	@BeforeClass
	public static void setUp() {
		EmbedTestingServer.start();
		zkRegCenter = new ZookeeperRegistryCenter(ZOOKEEPER_CONFIGURATION);
		ZOOKEEPER_CONFIGURATION.setConnectionTimeoutMilliseconds(30000);
		zkRegCenter.init();
		ZookeeperRegistryCenterTestUtil.persist(zkRegCenter);
	}

	@AfterClass
	public static void tearDown() {
		zkRegCenter.close();
	}

	@Test
	public void assertPersist() {
		zkRegCenter.persist("/test", "test_update");
		zkRegCenter.persist("/persist/new", "new_value");
		assertThat(zkRegCenter.get("/test"), is("test_update"));
		assertThat(zkRegCenter.get("/persist/new"), is("new_value"));
	}

	@Test
	public void assertUpdate() {
		zkRegCenter.persist("/update", "before_update");
		zkRegCenter.update("/update", "after_update");
		assertThat(zkRegCenter.getDirectly("/update"), is("after_update"));
	}

	@Test
	public void assertPersistEphemeral() throws Exception {
		zkRegCenter.persist("/persist", "persist_value");
		zkRegCenter.persistEphemeral("/ephemeral", "ephemeral_value");
		assertThat(zkRegCenter.get("/persist"), is("persist_value"));
		assertThat(zkRegCenter.get("/ephemeral"), is("ephemeral_value"));
		zkRegCenter.close();
		CuratorFramework client = CuratorFrameworkFactory.newClient(EmbedTestingServer.getConnectionString(),
				new RetryOneTime(2000));
		client.start();
		client.blockUntilConnected();
		// 持久化节点依然在
		assertThat(client.getData().forPath("/" + ZookeeperRegistryCenter4ModifyTest.class.getName() + "/persist"),
				is("persist_value".getBytes()));
		// 临时节点消失
		assertNull(client.checkExists()
				.forPath("/" + ZookeeperRegistryCenter4ModifyTest.class.getName() + "/ephemeral"));
		zkRegCenter.init();
	}

	@Test
	public void assertPersistSequential() throws Exception {
		assertThat(zkRegCenter.persistSequential("/sequential/test_sequential", "test_value"),
				startsWith("/sequential/test_sequential"));
		assertThat(zkRegCenter.persistSequential("/sequential/test_sequential", "test_value"),
				startsWith("/sequential/test_sequential"));
		CuratorFramework client = CuratorFrameworkFactory.newClient(EmbedTestingServer.getConnectionString(),
				new RetryOneTime(2000));
		client.start();
		client.blockUntilConnected();
		List<String> actual = client.getChildren().forPath(
				"/" + ZookeeperRegistryCenter4ModifyTest.class.getName() + "/sequential");
		assertThat(actual.size(), is(2));
		for (String each : actual) {
			System.out.println(each);
			assertThat(each, startsWith("test_sequential"));
			//System.out.println(zkRegCenter.get("/sequential/" + each));
			assertThat(zkRegCenter.get("/sequential/" + each), startsWith("test_value"));
		}
	}

	@Test
	public void assertPersistEphemeralSequential() throws Exception {
		zkRegCenter.persistEphemeralSequential("/sequential/test_ephemeral_sequential");
		zkRegCenter.persistEphemeralSequential("/sequential/test_ephemeral_sequential");
		CuratorFramework client = CuratorFrameworkFactory.newClient(EmbedTestingServer.getConnectionString(),
				new RetryOneTime(2000));
		client.start();
		client.blockUntilConnected();
		List<String> actual = client.getChildren().forPath(
				"/" + ZookeeperRegistryCenter4ModifyTest.class.getName() + "/sequential");
		assertThat(actual.size(), is(2));
		for (String each : actual) {
			assertThat(each, startsWith("test_ephemeral_sequential"));
		}
		zkRegCenter.close();
		actual = client.getChildren().forPath("/" + ZookeeperRegistryCenter4ModifyTest.class.getName() + "/sequential");
		assertTrue(actual.isEmpty());
		zkRegCenter.init();
	}

	@Test
	public void assertRemove() {
		zkRegCenter.remove("/test");
		assertFalse(zkRegCenter.isExisted("/test"));
	}
}
