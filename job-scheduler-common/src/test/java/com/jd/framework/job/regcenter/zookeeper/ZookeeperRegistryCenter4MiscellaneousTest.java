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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jd.framework.job.fixture.reg.EmbedTestingServer;
import com.jd.framework.job.regcenter.ZookeeperRegistryCenter;
import com.jd.framework.job.regcenter.conf.ZookeeperConfiguration;

public class ZookeeperRegistryCenter4MiscellaneousTest {

	private static final ZookeeperConfiguration ZOOKEEPER_CONFIGURATION = new ZookeeperConfiguration(
			EmbedTestingServer.getConnectionString(), ZookeeperRegistryCenter4MiscellaneousTest.class.getName());

	private static ZookeeperRegistryCenter zkRegCenter;

	@BeforeClass
	public static void setUp() {
		EmbedTestingServer.start();
		ZOOKEEPER_CONFIGURATION.setConnectionTimeoutMilliseconds(30000);
		zkRegCenter = new ZookeeperRegistryCenter(ZOOKEEPER_CONFIGURATION);
		zkRegCenter.init();
		zkRegCenter.addCacheData("/test");
	}

	@AfterClass
	public static void tearDown() {
		zkRegCenter.close();
	}

	@Test
	public void assertGetRawClient() {
		assertThat(zkRegCenter.getRawClient(), instanceOf(CuratorFramework.class));
		assertThat(((CuratorFramework) zkRegCenter.getRawClient()).getNamespace(),
				is(ZookeeperRegistryCenter4MiscellaneousTest.class.getName()));
	}

	@Test
	public void assertGetRawCache() {
		assertThat(zkRegCenter.getRawCache("/test"), instanceOf(TreeCache.class));
	}

	@Test
	public void assertGetZkConfig() {
		ZookeeperRegistryCenter zkRegCenter = new ZookeeperRegistryCenter(ZOOKEEPER_CONFIGURATION);
		assertThat(zkRegCenter.getZkConfig(), is(ZOOKEEPER_CONFIGURATION));
	}
}
