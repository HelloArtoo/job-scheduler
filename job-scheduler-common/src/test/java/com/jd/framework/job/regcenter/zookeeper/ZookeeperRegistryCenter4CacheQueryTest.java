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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jd.framework.job.fixture.reg.EmbedTestingServer;
import com.jd.framework.job.regcenter.ZookeeperRegistryCenter;
import com.jd.framework.job.regcenter.conf.ZookeeperConfiguration;
import com.jd.framework.job.regcenter.util.ZookeeperRegistryCenterTestUtil;

public class ZookeeperRegistryCenter4CacheQueryTest {

	private static final ZookeeperConfiguration ZOOKEEPER_CONFIGURATION = new ZookeeperConfiguration(
			EmbedTestingServer.getConnectionString(), ZookeeperRegistryCenter4CacheQueryTest.class.getName());

	private static ZookeeperRegistryCenter zkRegCenter;

	@BeforeClass
	public static void setUp() {
		EmbedTestingServer.start();
		zkRegCenter = new ZookeeperRegistryCenter(ZOOKEEPER_CONFIGURATION);
		ZOOKEEPER_CONFIGURATION.setConnectionTimeoutMilliseconds(30000);
		zkRegCenter.init();
		ZookeeperRegistryCenterTestUtil.persist(zkRegCenter);
		zkRegCenter.addCacheData("/test");
	}

	@AfterClass
	public static void tearDown() {
		zkRegCenter.close();
	}

	@Test
	public void assertGetWithoutValue() {
		assertNull(zkRegCenter.get("/test/null"));
	}

	@Test
	public void assertGetFromCache() {
		assertThat(zkRegCenter.get("/test"), is("test"));
		assertThat(zkRegCenter.get("/test/deep/nested"), is("deepNested"));
	}

}
