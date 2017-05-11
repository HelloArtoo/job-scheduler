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
import com.jd.framework.job.core.internal.helper.JobNodeStorageHelper;
import com.jd.framework.job.core.internal.listener.sub.ExecutionListenerManager.MonitorExecutionChangedJobListener;
import com.jd.framework.job.core.internal.service.ExecutionService;

public class ExecutionListenerManagerTest {

	@Mock
	private JobNodeStorageHelper jobNodeStorage;

	@Mock
	private ExecutionService executionService;

	private final ExecutionListenerManager executionListenerManager = new ExecutionListenerManager(null, "test_job");

	@Before
	public void setUp() throws NoSuchFieldException {
		MockitoAnnotations.initMocks(this);
		ReflectionUtils.setFieldValue(executionListenerManager, executionListenerManager.getClass().getSuperclass()
				.getDeclaredField("jobNodeStorage"), jobNodeStorage);
		ReflectionUtils.setFieldValue(executionListenerManager, "executionService", executionService);
	}

	@Test
	public void assertStart() {
		executionListenerManager.start();
		verify(jobNodeStorage).addDataListener(Matchers.<MonitorExecutionChangedJobListener> any());
	}

	@Test
	public void assertMonitorExecutionChangedJobListenerWhenIsNotMonitorExecutionPath() {
		executionListenerManager.new MonitorExecutionChangedJobListener().dataChanged(null, new TreeCacheEvent(
				TreeCacheEvent.Type.NODE_ADDED, new ChildData("/test_job/config/other", null, FactJsonConstants
						.getJobJson().getBytes())), "/test_job/config/other");
		verify(executionService, times(0)).removeExecutionInfo();
	}

	@Test
	public void assertMonitorExecutionChangedJobListenerWhenIsMonitorExecutionPathButNotUpdate() {
		executionListenerManager.new MonitorExecutionChangedJobListener().dataChanged(null, new TreeCacheEvent(
				TreeCacheEvent.Type.NODE_ADDED, new ChildData("/test_job/config", null, FactJsonConstants.getJobJson()
						.getBytes())), "/test_job/config");
		verify(executionService, times(0)).removeExecutionInfo();
	}

	@Test
	public void assertMonitorExecutionChangedJobListenerWhenIsMonitorExecutionPathAndUpdateButEnable() {
		executionListenerManager.new MonitorExecutionChangedJobListener().dataChanged(null, new TreeCacheEvent(
				TreeCacheEvent.Type.NODE_UPDATED, new ChildData("/test_job/config", null, FactJsonConstants
						.getJobJson().getBytes())), "/test_job/config");
		verify(executionService, times(0)).removeExecutionInfo();
	}

	@Test
	public void assertMonitorExecutionChangedJobListenerWhenIsMonitorExecutionPathAndUpdateButDisable() {
		executionListenerManager.new MonitorExecutionChangedJobListener().dataChanged(null,
				new TreeCacheEvent(TreeCacheEvent.Type.NODE_UPDATED, new ChildData("/test_job/config", null,
						FactJsonConstants.getJobJson(false).getBytes())), "/test_job/config");
		verify(executionService).removeExecutionInfo();
	}

}
