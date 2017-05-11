/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */     
package com.jd.framework.job.core;    

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.jd.framework.job.core.api.AllApiTests;
import com.jd.framework.job.core.config.AllConfigTests;
import com.jd.framework.job.core.integrate.AllIntegrateTests;
import com.jd.framework.job.core.internal.AllInternalTests;
    
@RunWith(Suite.class)
@SuiteClasses({
        AllApiTests.class, 
        AllConfigTests.class,
        AllInternalTests.class, 
        AllIntegrateTests.class
    })
public class AllCoreTests {

	//TEST THIS , TEST ALL
}
  