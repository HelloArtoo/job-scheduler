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

import org.junit.After;
import org.junit.Before;

import com.jd.framework.job.api.ScheduleJob;
import com.jd.framework.job.core.config.FactJobConfiguration;

public class AbstractBaseStdJobAutoInitTest extends AbstractBaseStdJobTest {

	protected AbstractBaseStdJobAutoInitTest(final Class<? extends ScheduleJob> scheduleJobClass) {
		super(scheduleJobClass, false);
	}

	protected void setFactJobConfig(final FactJobConfiguration factJobConfig) {
	}

	@Before
	public void autoJobInit() {
		setFactJobConfig(getFactJobConfig());
		initJob();
		assertRegCenterCommonInfoWithEnabled();
	}

	@After
	public void assertAfterJobRun() {
		assertRegCenterListenerInfo();
	}
}
