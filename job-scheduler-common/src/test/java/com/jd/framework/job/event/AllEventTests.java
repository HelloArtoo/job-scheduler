/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.event;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.jd.framework.job.event.rdb.RdbJobEventConfigurationTest;
import com.jd.framework.job.event.rdb.RdbJobEventIdentityTest;
import com.jd.framework.job.event.rdb.RdbJobEventListenerTest;
import com.jd.framework.job.event.rdb.RdbJobEventSearchTest;
import com.jd.framework.job.event.rdb.RdbJobEventStorageTest;
import com.jd.framework.job.event.type.JobExecutionEventTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
		JobExecutionEventTest.class, 
		JobEventBusTest.class, 
		RdbJobEventIdentityTest.class,
		RdbJobEventConfigurationTest.class, 
		RdbJobEventListenerTest.class, 
		RdbJobEventStorageTest.class,
		RdbJobEventSearchTest.class })
public class AllEventTests {
	// DO NOTHING
}
