/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */     
package com.jd.framework.job;    

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.jd.framework.job.api.AllApiTests;
import com.jd.framework.job.config.AllConfigTests;
import com.jd.framework.job.event.AllEventTests;
import com.jd.framework.job.exception.AllExceptionTests;
import com.jd.framework.job.executor.AllExecutorTests;
import com.jd.framework.job.regcenter.AllRegCenterTests;
import com.jd.framework.job.utils.AllUtilsTests;
    
/**
 * 
 * <pre>
 * 	单个单元测试请以Test*.java或*Test.java
 * 	suite class请统一以All*Tests.java命名
 *  严格遵守
 * </pre>
 *    
 * @author Rong Hu  
 * @version   
 *       1.0, 2017-5-1
 */
@RunWith(Suite.class)
@SuiteClasses({
        AllRegCenterTests.class,
        AllApiTests.class, 
        AllConfigTests.class, 
        AllExecutorTests.class, 
        AllEventTests.class, 
        AllExceptionTests.class, 
        AllUtilsTests.class
    })
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AllTests {
	//RUN THIS TEST ALL
}
  