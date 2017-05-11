/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */     
package com.jd.framework.job.executor;    

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.jd.framework.job.executor.context.TaskContextTest;
import com.jd.framework.job.executor.factory.JobExecutorFactoryTest;
import com.jd.framework.job.executor.handler.JobPropertiesTest;
import com.jd.framework.job.executor.handler.exception.DefaultJobExceptionHandlerTest;
import com.jd.framework.job.executor.handler.threadpool.DefaultExecutorServiceHandlerTest;
import com.jd.framework.job.executor.registry.ExecutorServiceHandlerRegistryTest;
import com.jd.framework.job.executor.type.ExceptionJobExecutorTest;
import com.jd.framework.job.executor.type.FlowJobExecutorTest;
import com.jd.framework.job.executor.type.SimpleJobExecutorTest;
    
@RunWith(Suite.class)
@SuiteClasses({
		TaskContextTest.class,
        JobExecutorFactoryTest.class,
        DefaultJobExceptionHandlerTest.class, 
        DefaultExecutorServiceHandlerTest.class,
        JobPropertiesTest.class, 
        ExecutorServiceHandlerRegistryTest.class,
        ExceptionJobExecutorTest.class,
        FlowJobExecutorTest.class, 
        SimpleJobExecutorTest.class
    })
public class AllExecutorTests {
	//DO NOTHING
}
  