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

import static junit.framework.TestCase.assertNull;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jd.framework.job.fixture.reg.EmbedTestingServer;
import com.jd.framework.job.regcenter.ZookeeperRegistryCenter;
import com.jd.framework.job.regcenter.conf.ZookeeperConfiguration;
import com.jd.framework.job.regcenter.util.ZookeeperRegistryCenterTestUtil;

public class ZookeeperRegistryCenter4DirectQueryTest {

	private static final ZookeeperConfiguration ZOOKEEPER_CONFIGURATION = new ZookeeperConfiguration(
			EmbedTestingServer.getConnectionString(), ZookeeperRegistryCenter4DirectQueryTest.class.getName());

	private static ZookeeperRegistryCenter zkRegCenter;

	@BeforeClass
	public static void setUp() {
		EmbedTestingServer.start();
		ZOOKEEPER_CONFIGURATION.setConnectionTimeoutMilliseconds(30000);
		zkRegCenter = new ZookeeperRegistryCenter(ZOOKEEPER_CONFIGURATION);
		zkRegCenter.init();
		ZookeeperRegistryCenterTestUtil.persist(zkRegCenter);
		zkRegCenter.addCacheData("/other");
	}

	@AfterClass
	public static void tearDown() {
		zkRegCenter.close();
	}

	@Test
	public void assertGetFromServer() {
		assertThat(zkRegCenter.get("/test"), is("test"));
		assertThat(zkRegCenter.get("/test/deep/nested"), is("deepNested"));
	}

	@Test
	public void assertGetChildrenKeys() {
		assertThat(zkRegCenter.getChildrenKeys("/test"), is(Arrays.asList("deep", "child")));
		assertThat(zkRegCenter.getChildrenKeys("/test/deep"), is(Collections.singletonList("nested")));
		assertThat(zkRegCenter.getChildrenKeys("/test/child"), is(Collections.<String> emptyList()));
		assertThat(zkRegCenter.getChildrenKeys("/test/notExisted"), is(Collections.<String> emptyList()));
	}

	@Test
	public void assertGetNumChildren() {
		assertThat(zkRegCenter.getNumChildren("/test"), is(2));
		assertThat(zkRegCenter.getNumChildren("/test/deep"), is(1));
		assertThat(zkRegCenter.getNumChildren("/test/child"), is(0));
		assertThat(zkRegCenter.getNumChildren("/test/notExisted"), is(0));
	}

	@Test
	public void assertIsExisted() {
		assertTrue(zkRegCenter.isExisted("/test"));
		assertTrue(zkRegCenter.isExisted("/test/deep/nested"));
		assertFalse(zkRegCenter.isExisted("/notExisted"));
	}

	@Test
	public void assertGetRegistryCenterTime() {
		assertTrue(zkRegCenter.getRegistryCenterTime("/_systemTime/current") <= System.currentTimeMillis());
	}

	@Test
	public void assertGetWithoutNode() {
		assertNull(zkRegCenter.get("/notExisted"));
	}
}
