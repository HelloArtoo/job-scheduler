/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */     
package com.jd.framework.job.core.integrate;    

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.jd.framework.job.core.integrate.std.flow.OneOffFlowJobTest;
import com.jd.framework.job.core.integrate.std.flow.StreamingFlowJob4ExecuteExceptionTest;
import com.jd.framework.job.core.integrate.std.flow.StreamingFlowJob4ExecuteFailureTest;
import com.jd.framework.job.core.integrate.std.flow.StreamingFlowJob4ExecuteMultipleThreadsTest;
import com.jd.framework.job.core.integrate.std.flow.StreamingFlowJob4ExecuteNoMonitorTest;
import com.jd.framework.job.core.integrate.std.flow.StreamingFlowJobTest;
import com.jd.framework.job.core.integrate.std.simple.DisabledJobTest;
import com.jd.framework.job.core.integrate.std.simple.SimpleScheduleJobTest;
    
@RunWith(Suite.class)
@SuiteClasses({
        DisabledJobTest.class, 
        SimpleScheduleJobTest.class, 
        OneOffFlowJobTest.class, 
        StreamingFlowJob4ExecuteExceptionTest.class, 
        StreamingFlowJob4ExecuteFailureTest.class, 
        StreamingFlowJob4ExecuteMultipleThreadsTest.class, 
        StreamingFlowJob4ExecuteNoMonitorTest.class, 
        StreamingFlowJobTest.class
    })
public class AllIntegrateTests {
	//ALL INTEGRATE TESTS
}
  