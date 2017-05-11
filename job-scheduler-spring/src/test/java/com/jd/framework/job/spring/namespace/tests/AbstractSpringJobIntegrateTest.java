/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.spring.namespace.tests;

import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;

import lombok.RequiredArgsConstructor;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jd.framework.job.core.internal.executor.JobRegistry;
import com.jd.framework.job.regcenter.api.CoordinatorRegistryCenter;
import com.jd.framework.job.spring.fixture.job.TestSpringFlowJob;
import com.jd.framework.job.spring.fixture.job.TestSpringSimpleJob;
import com.jd.framework.job.spring.namespace.base.AbstractZookeeperJUnit4SpringContextTest;

@RequiredArgsConstructor
public abstract class AbstractSpringJobIntegrateTest extends AbstractZookeeperJUnit4SpringContextTest {

	private final String simpleJobName;

	private final String throughputFlowJobName;

	@Resource
	private CoordinatorRegistryCenter regCenter;

	@Before
	@After
	public void reset() {
		TestSpringSimpleJob.reset();
		TestSpringFlowJob.reset();
	}

	@After
	public void tearDown() {
		JobRegistry.getInstance().getJobScheduleController(simpleJobName).shutdown();
		JobRegistry.getInstance().getJobScheduleController(throughputFlowJobName).shutdown();
	}

	@Test
	public void assertSpringJobBean() {
		assertSimpleScheduleJobBean();
		assertThroughputTestSpringFlowJobBean();
	}

	private void assertSimpleScheduleJobBean() {
		while (!TestSpringSimpleJob.isCompleted()) {
			sleep(100L);
		}
		assertTrue(TestSpringSimpleJob.isCompleted());
		assertTrue(regCenter.isExisted("/" + simpleJobName + "/execution"));
	}

	private void assertThroughputTestSpringFlowJobBean() {
		while (!TestSpringFlowJob.isCompleted()) {
			sleep(100L);
		}
		assertTrue(TestSpringFlowJob.isCompleted());
		assertTrue(regCenter.isExisted("/" + throughputFlowJobName + "/execution"));
	}

	private static void sleep(final long millis) {
		try {
			Thread.sleep(millis);
		} catch (final InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}
}
