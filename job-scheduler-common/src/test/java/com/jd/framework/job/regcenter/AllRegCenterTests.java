/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */     
package com.jd.framework.job.regcenter;    

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.jd.framework.job.regcenter.conf.ZookeeperConfigurationTest;
import com.jd.framework.job.regcenter.exception.RegExceptionHandlerTest;
import com.jd.framework.job.regcenter.exception.RegExceptionTest;
import com.jd.framework.job.regcenter.zookeeper.ZookeeperElectionServiceTest;
import com.jd.framework.job.regcenter.zookeeper.ZookeeperRegistryCenter4AuthTest;
import com.jd.framework.job.regcenter.zookeeper.ZookeeperRegistryCenter4CacheQueryTest;
import com.jd.framework.job.regcenter.zookeeper.ZookeeperRegistryCenter4DirectQueryTest;
import com.jd.framework.job.regcenter.zookeeper.ZookeeperRegistryCenter4InitFailureTest;
import com.jd.framework.job.regcenter.zookeeper.ZookeeperRegistryCenter4MiscellaneousTest;
import com.jd.framework.job.regcenter.zookeeper.ZookeeperRegistryCenter4ModifyTest;
    
@RunWith(Suite.class)
@SuiteClasses({
        ZookeeperConfigurationTest.class, 
        RegExceptionHandlerTest.class, 
        RegExceptionTest.class, 
        ZookeeperElectionServiceTest.class, 
        ZookeeperRegistryCenter4AuthTest.class,
        ZookeeperRegistryCenter4CacheQueryTest.class,
        ZookeeperRegistryCenter4DirectQueryTest.class, 
        ZookeeperRegistryCenter4InitFailureTest.class,
        ZookeeperRegistryCenter4MiscellaneousTest.class,
        ZookeeperRegistryCenter4ModifyTest.class
    })
public class AllRegCenterTests {
	//REG CENTER TEST
}
  