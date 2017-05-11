/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.core.integrate.std.simple;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jd.framework.job.core.integrate.AbstractBaseStdJobAutoInitTest;
import com.jd.framework.job.core.integrate.fixture.simple.TestSimpleScheduleJob;
import com.jd.framework.job.core.integrate.utils.WaitingUtils;

public class SimpleScheduleJobTest extends AbstractBaseStdJobAutoInitTest {

	public SimpleScheduleJobTest() {
		super(TestSimpleScheduleJob.class);
	}

	@Before
	@After
	public void reset() {
		TestSimpleScheduleJob.reset();
	}

	@Test
	public void assertJobInit() {
		while (!TestSimpleScheduleJob.isCompleted()) {
			WaitingUtils.waitingShortTime();
		}
		assertTrue(getRegCenter().isExisted("/" + getJobName() + "/execution"));
	}

}
