/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.config;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.jd.framework.job.config.core.JobCoreConfigurationTest;

/**
 * 
 * 请在SuiteClasses中增加需要测试的类
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-16
 */
@RunWith(Suite.class)
@SuiteClasses(JobCoreConfigurationTest.class)
public class AllConfigTests {
	// DO NOTHING
}
