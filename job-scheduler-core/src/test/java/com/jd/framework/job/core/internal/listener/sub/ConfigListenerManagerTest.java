/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.core.internal.listener.sub;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.unitils.util.ReflectionUtils;

import com.jd.framework.job.core.fixture.FactJsonConstants;
import com.jd.framework.job.core.internal.executor.JobRegistry;
import com.jd.framework.job.core.internal.executor.quartz.JobScheduleController;
import com.jd.framework.job.core.internal.helper.JobNodeStorageHelper;
import com.jd.framework.job.core.internal.listener.sub.ConfigListenerManager.CronSettingAndJobEventChangedJobListener;
import com.jd.framework.job.event.JobEventBus;

public class ConfigListenerManagerTest {

	@Mock
	private JobNodeStorageHelper jobNodeStorage;

	@Mock
	private JobScheduleController jobScheduleController;

	@Mock
	private JobEventBus jobEventBus;

	private final ConfigListenerManager configListenerManager = new ConfigListenerManager(null, "test_job");

	@Before
	public void setUp() throws NoSuchFieldException {
		MockitoAnnotations.initMocks(this);
		ReflectionUtils.setFieldValue(configListenerManager, configListenerManager.getClass().getSuperclass()
				.getDeclaredField("jobNodeStorage"), jobNodeStorage);
	}

	@Test
	public void assertStart() {
		configListenerManager.start();
		verify(jobNodeStorage).addDataListener(Matchers.<CronSettingAndJobEventChangedJobListener> any());
	}

	@Test
	public void assertCronSettingChangedJobListenerWhenIsNotCronPath() {
		configListenerManager.new CronSettingAndJobEventChangedJobListener().dataChanged(null, new TreeCacheEvent(
				TreeCacheEvent.Type.NODE_ADDED, new ChildData("/test_job/config/other", null, FactJsonConstants
						.getJobJson().getBytes())), "/test_job/config/other");
		verify(jobScheduleController, times(0)).rescheduleJob(Matchers.<String> any());
	}

	@Test
	public void assertCronSettingChangedJobListenerWhenIsCronPathButNotUpdate() {
		configListenerManager.new CronSettingAndJobEventChangedJobListener().dataChanged(null, new TreeCacheEvent(
				TreeCacheEvent.Type.NODE_ADDED, new ChildData("/test_job/config", null, FactJsonConstants.getJobJson()
						.getBytes())), "/test_job/config");
		verify(jobScheduleController, times(0)).rescheduleJob(Matchers.<String> any());
	}

	@Test
	public void assertCronSettingChangedJobListenerWhenIsCronPathAndUpdateButCannotFindJob() {
		configListenerManager.new CronSettingAndJobEventChangedJobListener().dataChanged(null, new TreeCacheEvent(
				TreeCacheEvent.Type.NODE_UPDATED, new ChildData("/test_job/config", null, FactJsonConstants
						.getJobJson().getBytes())), "/test_job/config");
		verify(jobScheduleController, times(0)).rescheduleJob(Matchers.<String> any());
	}

	@Test
	public void assertCronSettingChangedJobListenerWhenIsCronPathAndUpdateAndFindJob() {
		JobRegistry.getInstance().addJobScheduleController("test_job", jobScheduleController);
		configListenerManager.new CronSettingAndJobEventChangedJobListener().dataChanged(null, new TreeCacheEvent(
				TreeCacheEvent.Type.NODE_UPDATED, new ChildData("/test_job/config", null, FactJsonConstants
						.getJobJson().getBytes())), "/test_job/config");
		verify(jobScheduleController).rescheduleJob("0/1 * * * * ?");
	}

}
