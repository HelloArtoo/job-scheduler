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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jd.framework.job.core.integrate.AbstractBaseStdJobTest;
import com.jd.framework.job.core.integrate.fixture.simple.TestSimpleScheduleJob;

public class DisabledJobTest extends AbstractBaseStdJobTest {

	public DisabledJobTest() {
		super(TestSimpleScheduleJob.class, true);
	}

	@Before
	@After
	public void reset() {
		TestSimpleScheduleJob.reset();
	}

	@Test
	public void assertJobInit() {
		initJob();
		assertRegCenterCommonInfoWithDisabled();
	}
}
