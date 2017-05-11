/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */     
package com.jd.framework.job.spring.namespace;    

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.jd.framework.job.spring.namespace.tests.NamespaceWithEventTraceRdbTest;
import com.jd.framework.job.spring.namespace.tests.NamespaceWithJobPropertiesTest;
import com.jd.framework.job.spring.namespace.tests.NamespaceWithListenerAndCglibTest;
import com.jd.framework.job.spring.namespace.tests.NamespaceWithListenerAndJdkDynamicProxyTest;
import com.jd.framework.job.spring.namespace.tests.NamespaceWithListenerTest;
import com.jd.framework.job.spring.namespace.tests.NamespaceWithoutListenerTest;
    
@RunWith(Suite.class)
@SuiteClasses({
        NamespaceWithoutListenerTest.class,
        NamespaceWithJobPropertiesTest.class,
        NamespaceWithListenerTest.class,
        NamespaceWithListenerAndJdkDynamicProxyTest.class,
        NamespaceWithListenerAndCglibTest.class,
        NamespaceWithEventTraceRdbTest.class
    })
public class AllNamespaceTests {
	//ALL NAMESPACE TESTS
}
  