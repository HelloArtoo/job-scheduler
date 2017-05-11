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
import com.jd.framework.job.core.integrate.fixture.flow.TestStreamingFlowJob;
import com.jd.framework.job.core.integrate.utils.WaitingUtils;
import static org.junit.Assert.assertTrue;

public class StreamingFlowJobTest extends AbstractBaseStdJobAutoInitTest {

	public StreamingFlowJobTest() {
		super(TestStreamingFlowJob.class);
	}

	@Before
	@After
	public void reset() {
		TestStreamingFlowJob.reset();
	}

	@Override
	protected void setFactJobConfig(final FactJobConfiguration factJobConfig) {
		JobConfigUtils.setFieldValue(factJobConfig.getTypeConfig(), "streamingProcess", true);
	}

	@Test
	public void assertJobInit() {
		while (!TestStreamingFlowJob.isCompleted()) {
			WaitingUtils.waitingShortTime();
		}
		assertTrue(getRegCenter().isExisted("/" + getJobName() + "/execution"));
	}
}
