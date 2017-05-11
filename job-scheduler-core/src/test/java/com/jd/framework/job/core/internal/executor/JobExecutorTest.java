/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.core.internal.executor;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.quartz.SchedulerException;
import org.unitils.util.ReflectionUtils;

import com.jd.framework.job.core.api.listener.AbstractOneOffJobListener;
import com.jd.framework.job.core.api.listener.fixture.ScheduleJobListenerCaller;
import com.jd.framework.job.core.api.listener.fixture.TestDistributeOnceJobListener;
import com.jd.framework.job.core.api.listener.fixture.TestScheduleJobListener;
import com.jd.framework.job.core.config.FactJobConfiguration;
import com.jd.framework.job.core.fixture.JobConfigUtils;
import com.jd.framework.job.core.internal.facade.SchedulerFacade;
import com.jd.framework.job.regcenter.api.CoordinatorRegistryCenter;

public class JobExecutorTest {

	@Mock
	private CoordinatorRegistryCenter regCenter;

	@Mock
	private SchedulerFacade schedulerFacade;

	@Mock
	private ScheduleJobListenerCaller caller;

	private final FactJobConfiguration factJobConfig = JobConfigUtils.createSimpleFactJobConfiguration();

	private JobExecutor jobExecutor = new JobExecutor(regCenter, factJobConfig);

	@Before
	public void initMocks() throws NoSuchFieldException {
		MockitoAnnotations.initMocks(this);
		ReflectionUtils.setFieldValue(jobExecutor, "regCenter", regCenter);
		ReflectionUtils.setFieldValue(jobExecutor, "schedulerFacade", schedulerFacade);
	}

	@Test
	public void assertNew() throws NoSuchFieldException {
		TestDistributeOnceJobListener testDistributeOnceJobListener = new TestDistributeOnceJobListener(caller);
		assertNull(ReflectionUtils.getFieldValue(testDistributeOnceJobListener,
				ReflectionUtils.getFieldWithName(AbstractOneOffJobListener.class, "guaranteeService", false)));
		new JobExecutor(null, factJobConfig, new TestScheduleJobListener(caller), testDistributeOnceJobListener);
		assertNotNull(ReflectionUtils.getFieldValue(testDistributeOnceJobListener,
				ReflectionUtils.getFieldWithName(AbstractOneOffJobListener.class, "guaranteeService", false)));
	}

	@Test
	public void assertInit() throws NoSuchFieldException, SchedulerException {
		jobExecutor.init();
		verify(schedulerFacade).clearPreviousServerStatus();
		verify(regCenter).addCacheData("/test_job");
		verify(schedulerFacade).registerStartUpInfo(factJobConfig);
	}

}
