/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.regcenter.util;

import com.jd.framework.job.regcenter.ZookeeperRegistryCenter;

public class ZookeeperRegistryCenterTestUtil {
	
	public static void persist(final ZookeeperRegistryCenter zookeeperRegistryCenter) {
		zookeeperRegistryCenter.persist("/test", "test");
		zookeeperRegistryCenter.persist("/test/deep/nested", "deepNested");
		zookeeperRegistryCenter.persist("/test/child", "child");
	}
}
