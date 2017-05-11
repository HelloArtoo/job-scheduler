/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.core.integrate.std.flow;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jd.framework.job.core.config.FactJobConfiguration;
import com.jd.framework.job.core.fixture.JobConfigUtils;
import com.jd.framework.job.core.integrate.AbstractBaseStdJobAutoInitTest;
import com.jd.framework.job.core.integrate.fixture.flow.TestOneOffFlowJob;
import com.jd.framework.job.core.integrate.utils.WaitingUtils;
import static org.junit.Assert.assertTrue;

public class OneOffFlowJobTest extends AbstractBaseStdJobAutoInitTest {

	public OneOffFlowJobTest() {
		super(TestOneOffFlowJob.class);
	}

	@Before
	@After
	public void reset() {
		TestOneOffFlowJob.reset();
	}

	@Override
	protected void setFactJobConfig(final FactJobConfiguration factJobConfig) {
		JobConfigUtils.setFieldValue(factJobConfig.getTypeConfig().getCoreConfig(), "misfire", false);
		JobConfigUtils.setFieldValue(factJobConfig.getTypeConfig(), "streamingProcess", false);
	}

	@Test
	public void assertJobInit() {
		while (!TestOneOffFlowJob.isCompleted()) {
			WaitingUtils.waitingShortTime();
		}
		assertTrue(getRegCenter().isExisted("/" + getJobName() + "/execution"));
	}
}
