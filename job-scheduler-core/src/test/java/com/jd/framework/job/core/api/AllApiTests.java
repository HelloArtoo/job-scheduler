/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */     
package com.jd.framework.job.core.api;    

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.jd.framework.job.core.api.listener.AbstractOneOffJobListenerTest;
import com.jd.framework.job.core.api.operation.JobOperatorTest;
import com.jd.framework.job.core.api.strategy.JobSegmentStrategyFactoryTest;
import com.jd.framework.job.core.api.strategy.impl.AverageAllocationJobSegmentStrategyTest;
import com.jd.framework.job.core.api.strategy.impl.OdevitySortByNameJobSegmentStrategyTest;
import com.jd.framework.job.core.api.strategy.impl.RotateServerByNameJobSegmentStrategyTest;
    
@RunWith(Suite.class)
@SuiteClasses({
		AbstractOneOffJobListenerTest.class,
        JobSegmentStrategyFactoryTest.class, 
        AverageAllocationJobSegmentStrategyTest.class, 
        OdevitySortByNameJobSegmentStrategyTest.class, 
        RotateServerByNameJobSegmentStrategyTest.class,
        JobOperatorTest.class,
        JobSchedulerTest.class
    })
public class AllApiTests {

}
  