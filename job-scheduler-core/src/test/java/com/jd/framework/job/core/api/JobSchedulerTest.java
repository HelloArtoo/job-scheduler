/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.core.api;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.unitils.util.ReflectionUtils;

import com.jd.framework.job.config.core.JobCoreConfiguration;
import com.jd.framework.job.config.type.SimpleJobConfiguration;
import com.jd.framework.job.core.api.listener.fixture.ScheduleJobListenerCaller;
import com.jd.framework.job.core.config.FactJobConfiguration;
import com.jd.framework.job.core.fixture.JobConfigurationUtils;
import com.jd.framework.job.core.fixture.TestSimpleJob;
import com.jd.framework.job.core.internal.executor.JobExecutor;
import com.jd.framework.job.core.internal.executor.JobRegistry;
import com.jd.framework.job.core.internal.executor.quartz.JobScheduleController;
import com.jd.framework.job.core.internal.executor.quartz.JobTriggerListener;
import com.jd.framework.job.core.internal.facade.SchedulerFacade;
import com.jd.framework.job.regcenter.api.CoordinatorRegistryCenter;

/**
 * 
 * 基于quartz的scheduler任务
 * 
 * @author Rong Hu
 * @version 1.0, 2017-5-1
 */
public class JobSchedulerTest {

	@Mock
	private CoordinatorRegistryCenter regCenter;

	@Mock
	private JobExecutor jobExecutor;

	@Mock
	private SchedulerFacade schedulerFacade;

	@Mock
	private ScheduleJobListenerCaller caller;

	private final FactJobConfiguration factJobConfig = JobConfigurationUtils.createSimpleFactJobConfiguration();

	private JobScheduler jobScheduler = new JobScheduler(regCenter, factJobConfig);

	@Before
	public void initMocks() throws NoSuchFieldException {
		MockitoAnnotations.initMocks(this);
		ReflectionUtils.setFieldValue(jobScheduler, "jobExecutor", jobExecutor);
		when(jobExecutor.getSchedulerFacade()).thenReturn(schedulerFacade);
	}

	@Test
	public void assertInitIfIsMisfire() throws NoSuchFieldException, SchedulerException {
		mockInit(true);
		jobScheduler.init();
		assertInit();
	}

	@Test
	public void assertInitIfIsNotMisfire() throws NoSuchFieldException, SchedulerException {
		mockInit(false);
		jobScheduler.init();
		assertInit();
	}

	@Test
	public void assertShutdown() {
		mockInit(true);
		jobScheduler.init();
		jobScheduler.shutdown();
	}

	private void mockInit(final boolean isMisfire) {
		when(schedulerFacade.newJobTriggerListener()).thenReturn(new JobTriggerListener(null, null));
		when(schedulerFacade.loadJobConfiguration()).thenReturn(
				FactJobConfiguration.newBuilder(
						new SimpleJobConfiguration(JobCoreConfiguration
								.newBuilder("test_job", "* * 0/10 * * ? 2050", 3).misfire(isMisfire).build(),
								TestSimpleJob.class.getCanonicalName())).build());
	}

	private void assertInit() throws NoSuchFieldException, SchedulerException {
		verify(jobExecutor).init();
		Scheduler scheduler = ReflectionUtils.getFieldValue(
				JobRegistry.getInstance().getJobScheduleController("test_job"),
				JobScheduleController.class.getDeclaredField("scheduler"));
		assertThat(scheduler.getListenerManager().getTriggerListeners().size(), is(1));
		assertThat(scheduler.getListenerManager().getTriggerListeners().get(0), instanceOf(JobTriggerListener.class));
		assertTrue(scheduler.isStarted());
		verify(schedulerFacade).newJobTriggerListener();
	}
}
