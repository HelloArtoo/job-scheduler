/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */     
package com.jd.framework.job.exception;    

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
    
@RunWith(Suite.class)
@SuiteClasses({
        JobConfigurationExceptionTest.class, 
        JobExecutionEnvironmentExceptionTest.class, 
        JobSystemExceptionTest.class,
        JobEventListenerConfigExceptionTest.class,
    })
public class AllExceptionTests {
	//DO NOTHING
}
  