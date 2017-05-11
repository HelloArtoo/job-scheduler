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

import org.junit.Test;

import com.jd.framework.job.regcenter.ZookeeperRegistryCenter;
import com.jd.framework.job.regcenter.conf.ZookeeperConfiguration;
import com.jd.framework.job.regcenter.exception.RegException;

public class ZookeeperRegistryCenter4InitFailureTest {

	@Test(expected = RegException.class)
	public void assertInitFailure() {
		ZookeeperRegistryCenter zkRegCenter = new ZookeeperRegistryCenter(new ZookeeperConfiguration("localhost:1",
				ZookeeperRegistryCenter4InitFailureTest.class.getName()));
		zkRegCenter.init();
	}
}
