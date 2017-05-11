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
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.unitils.util.ReflectionUtils;

import com.jd.framework.job.config.core.JobCoreConfiguration;
import com.jd.framework.job.config.type.SimpleJobConfiguration;
import com.jd.framework.job.core.config.FactJobConfiguration;
import com.jd.framework.job.core.fixture.FactJsonConstants;
import com.jd.framework.job.core.fixture.TestSimpleJob;
import com.jd.framework.job.core.internal.helper.JobNodeStorageHelper;
import com.jd.framework.job.core.internal.listener.parent.AbstractJobListener;
import com.jd.framework.job.core.internal.service.ConfigService;
import com.jd.framework.job.core.internal.service.ExecutionService;
import com.jd.framework.job.core.internal.service.FailoverService;
import com.jd.framework.job.core.internal.service.SegmentService;

public class FailoverListenerManagerTest {
	@Mock
	private JobNodeStorageHelper jobNodeStorage;

	@Mock
	private ConfigService configService;

	@Mock
	private ExecutionService executionService;

	@Mock
	private SegmentService segmentService;

	@Mock
	private FailoverService failoverService;

	private final FailoverListenerManager failoverListenerManager = new FailoverListenerManager(null, "test_job");

	@Before
	public void setUp() throws NoSuchFieldException {
		MockitoAnnotations.initMocks(this);
		ReflectionUtils.setFieldValue(failoverListenerManager, failoverListenerManager.getClass().getSuperclass()
				.getDeclaredField("jobNodeStorage"), jobNodeStorage);
		ReflectionUtils.setFieldValue(failoverListenerManager, "configService", configService);
		ReflectionUtils.setFieldValue(failoverListenerManager, "executionService", executionService);
		ReflectionUtils.setFieldValue(failoverListenerManager, "segmentService", segmentService);
		ReflectionUtils.setFieldValue(failoverListenerManager, "failoverService", failoverService);
	}

	@Test
	public void assertStart() {
		failoverListenerManager.start();
		verify(jobNodeStorage, times(3)).addDataListener(Matchers.<AbstractJobListener> any());
	}

	@Test
	public void assertJobCrashedJobListenerWhenIsNotRunningItemPath() {
		failoverListenerManager.new JobCrashedJobListener().dataChanged(null, new TreeCacheEvent(
				TreeCacheEvent.Type.NODE_ADDED, new ChildData("/test_job/execution/0/other", null, "".getBytes())),
				"/test_job/execution/0/other");
		verify(failoverService, times(0)).setCrashedFailoverFlag(0);
	}

	@Test
	public void assertJobCrashedJobListenerWhenIsRunningItemPathButNotRemove() {
		failoverListenerManager.new JobCrashedJobListener().dataChanged(null, new TreeCacheEvent(
				TreeCacheEvent.Type.NODE_ADDED, new ChildData("/test_job/execution/0/running", null, "".getBytes())),
				"/test_job/execution/0/running");
		verify(failoverService, times(0)).setCrashedFailoverFlag(0);
	}

	@Test
	public void assertJobCrashedJobListenerWhenIsRunningItemPathAndRemoveButItemCompleted() {
		when(executionService.isCompleted(0)).thenReturn(true);
		failoverListenerManager.new JobCrashedJobListener().dataChanged(null, new TreeCacheEvent(
				TreeCacheEvent.Type.NODE_REMOVED, new ChildData("/test_job/execution/0/running", null, "".getBytes())),
				"/test_job/execution/0/running");
		verify(executionService).isCompleted(0);
		verify(failoverService, times(0)).setCrashedFailoverFlag(0);
	}

	@Test
	public void assertJobCrashedJobListenerWhenIsRunningItemPathAndRemoveAndItemNotCompletedButDisableFailover() {
		when(executionService.isCompleted(0)).thenReturn(false);
		when(configService.load(true)).thenReturn(
				FactJobConfiguration.newBuilder(
						new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3)
								.failover(false).build(), TestSimpleJob.class.getCanonicalName())).build());
		failoverListenerManager.new JobCrashedJobListener().dataChanged(null, new TreeCacheEvent(
				TreeCacheEvent.Type.NODE_REMOVED, new ChildData("/test_job/execution/0/running", null, "".getBytes())),
				"/test_job/execution/0/running");
		verify(executionService).isCompleted(0);
		verify(configService).load(true);
		verify(failoverService, times(0)).setCrashedFailoverFlag(0);
	}

	@Test
	public void assertJobCrashedJobListenerWhenIsRunningItemPathAndRemoveAndItemNotCompletedAndEnableFailoverButHasRunningItems() {
		when(executionService.isCompleted(0)).thenReturn(false);
		when(configService.load(true)).thenReturn(
				FactJobConfiguration
						.newBuilder(
								new SimpleJobConfiguration(JobCoreConfiguration
										.newBuilder("test_job", "0/1 * * * * ?", 3).failover(true).build(),
										TestSimpleJob.class.getCanonicalName())).monitorExecution(true).build());
		when(segmentService.getLocalHostSegmentItems()).thenReturn(Arrays.asList(1, 2));
		when(executionService.hasRunningItems(Arrays.asList(1, 2))).thenReturn(true);
		failoverListenerManager.new JobCrashedJobListener().dataChanged(null, new TreeCacheEvent(
				TreeCacheEvent.Type.NODE_REMOVED, new ChildData("/test_job/execution/0/running", null, "".getBytes())),
				"/test_job/execution/0/running");
		verify(executionService).isCompleted(0);
		verify(configService).load(true);
		verify(failoverService).setCrashedFailoverFlag(0);
		verify(segmentService).getLocalHostSegmentItems();
		verify(executionService).hasRunningItems(Arrays.asList(1, 2));
		verify(failoverService, times(0)).failoverIfNecessary();
	}

	@Test
	public void assertJobCrashedJobListenerWhenIsRunningItemPathAndRemoveAndItemNotCompletedAndEnableFailoverAndHasNotRunningItems() {
		when(executionService.isCompleted(0)).thenReturn(false);
		when(configService.load(true)).thenReturn(
				FactJobConfiguration
						.newBuilder(
								new SimpleJobConfiguration(JobCoreConfiguration
										.newBuilder("test_job", "0/1 * * * * ?", 3).failover(true).build(),
										TestSimpleJob.class.getCanonicalName())).monitorExecution(true).build());
		when(segmentService.getLocalHostSegmentItems()).thenReturn(Arrays.asList(1, 2));
		when(executionService.hasRunningItems(Arrays.asList(1, 2))).thenReturn(false);
		failoverListenerManager.new JobCrashedJobListener().dataChanged(null, new TreeCacheEvent(
				TreeCacheEvent.Type.NODE_REMOVED, new ChildData("/test_job/execution/0/running", null, "".getBytes())),
				"/test_job/execution/0/running");
		verify(executionService).isCompleted(0);
		verify(configService).load(true);
		verify(failoverService).setCrashedFailoverFlag(0);
		verify(segmentService).getLocalHostSegmentItems();
		verify(executionService).hasRunningItems(Arrays.asList(1, 2));
		verify(failoverService).failoverIfNecessary();
	}

	@Test
	public void assertFailoverJobCrashedJobListenerWhenIsNotRunningItemPath() {
		failoverListenerManager.new FailoverJobCrashedJobListener().dataChanged(null, new TreeCacheEvent(
				TreeCacheEvent.Type.NODE_ADDED, new ChildData("/test_job/execution/0/other", null, "".getBytes())),
				"/test_job/execution/0/other");
		verify(failoverService, times(0)).setCrashedFailoverFlag(0);
	}

	@Test
	public void assertFailoverJobCrashedJobListenerWhenIsRunningItemPathButNotRemove() {
		failoverListenerManager.new FailoverJobCrashedJobListener().dataChanged(null, new TreeCacheEvent(
				TreeCacheEvent.Type.NODE_ADDED, new ChildData("/test_job/execution/0/failover", null, "".getBytes())),
				"/test_job/execution/0/failover");
		verify(failoverService, times(0)).setCrashedFailoverFlag(0);
	}

	@Test
	public void assertFailoverJobCrashedJobListenerWhenIsRunningItemPathAndRemoveButItemCompleted() {
		when(executionService.isCompleted(0)).thenReturn(true);
		failoverListenerManager.new FailoverJobCrashedJobListener().dataChanged(null,
				new TreeCacheEvent(TreeCacheEvent.Type.NODE_REMOVED, new ChildData("/test_job/execution/0/failover",
						null, "".getBytes())), "/test_job/execution/0/failover");
		verify(executionService).isCompleted(0);
		verify(failoverService, times(0)).setCrashedFailoverFlag(0);
	}

	@Test
	public void assertFailoverJobCrashedJobListenerWhenIsRunningItemPathAndRemoveAndItemNotCompletedButDisableFailover() {
		when(executionService.isCompleted(0)).thenReturn(false);
		when(configService.load(true)).thenReturn(
				FactJobConfiguration.newBuilder(
						new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3)
								.failover(false).build(), TestSimpleJob.class.getCanonicalName())).build());
		failoverListenerManager.new FailoverJobCrashedJobListener().dataChanged(null,
				new TreeCacheEvent(TreeCacheEvent.Type.NODE_REMOVED, new ChildData("/test_job/execution/0/failover",
						null, "".getBytes())), "/test_job/execution/0/failover");
		verify(executionService).isCompleted(0);
		verify(configService).load(true);
		verify(failoverService, times(0)).setCrashedFailoverFlag(0);
	}

	@Test
	public void assertFailoverJobCrashedJobListenerWhenIsRunningItemPathAndRemoveAndItemNotCompletedAndEnableFailoverButHasRunningItems() {
		when(executionService.isCompleted(0)).thenReturn(false);
		when(configService.load(true)).thenReturn(
				FactJobConfiguration
						.newBuilder(
								new SimpleJobConfiguration(JobCoreConfiguration
										.newBuilder("test_job", "0/1 * * * * ?", 3).failover(true).build(),
										TestSimpleJob.class.getCanonicalName())).monitorExecution(true).build());
		when(segmentService.getLocalHostSegmentItems()).thenReturn(Arrays.asList(1, 2));
		when(executionService.hasRunningItems(Arrays.asList(1, 2))).thenReturn(true);
		failoverListenerManager.new FailoverJobCrashedJobListener().dataChanged(null,
				new TreeCacheEvent(TreeCacheEvent.Type.NODE_REMOVED, new ChildData("/test_job/execution/0/failover",
						null, "".getBytes())), "/test_job/execution/0/failover");
		verify(executionService).isCompleted(0);
		verify(configService).load(true);
		verify(failoverService).setCrashedFailoverFlag(0);
		verify(segmentService).getLocalHostSegmentItems();
		verify(executionService).hasRunningItems(Arrays.asList(1, 2));
		verify(failoverService, times(0)).failoverIfNecessary();
	}

	@Test
	public void assertFailoverJobCrashedJobListenerWhenIsRunningItemPathAndRemoveAndItemNotCompletedAndEnableFailoverAndHasNotRunningItems() {
		when(executionService.isCompleted(0)).thenReturn(false);
		when(configService.load(true)).thenReturn(
				FactJobConfiguration
						.newBuilder(
								new SimpleJobConfiguration(JobCoreConfiguration
										.newBuilder("test_job", "0/1 * * * * ?", 3).failover(true).build(),
										TestSimpleJob.class.getCanonicalName())).monitorExecution(true).build());
		when(segmentService.getLocalHostSegmentItems()).thenReturn(Arrays.asList(1, 2));
		when(executionService.hasRunningItems(Arrays.asList(1, 2))).thenReturn(false);
		failoverListenerManager.new FailoverJobCrashedJobListener().dataChanged(null,
				new TreeCacheEvent(TreeCacheEvent.Type.NODE_REMOVED, new ChildData("/test_job/execution/0/failover",
						null, "".getBytes())), "/test_job/execution/0/failover");
		verify(executionService).isCompleted(0);
		verify(configService).load(true);
		verify(failoverService).setCrashedFailoverFlag(0);
		verify(segmentService).getLocalHostSegmentItems();
		verify(executionService).hasRunningItems(Arrays.asList(1, 2));
		verify(failoverService).failoverIfNecessary();
	}

	@Test
	public void assertFailoverSettingsChangedJobListenerWhenIsNotFailoverPath() {
		failoverListenerManager.new FailoverSettingsChangedJobListener().dataChanged(null, new TreeCacheEvent(
				TreeCacheEvent.Type.NODE_ADDED, new ChildData("/test_job/config/other", null, FactJsonConstants
						.getJobJson().getBytes())), "/test_job/config/other");
		verify(failoverService, times(0)).removeFailoverInfo();
	}

	@Test
	public void assertFailoverSettingsChangedJobListenerWhenIsFailoverPathButNotUpdate() {
		failoverListenerManager.new FailoverSettingsChangedJobListener().dataChanged(null, new TreeCacheEvent(
				TreeCacheEvent.Type.NODE_ADDED, new ChildData("/test_job/config/failover", null, "".getBytes())),
				"/test_job/config/failover");
		verify(failoverService, times(0)).removeFailoverInfo();
	}

	@Test
	public void assertFailoverSettingsChangedJobListenerWhenIsFailoverPathAndUpdateButEnableFailover() {
		failoverListenerManager.new FailoverSettingsChangedJobListener().dataChanged(null, new TreeCacheEvent(
				TreeCacheEvent.Type.NODE_UPDATED, new ChildData("/test_job/config", null, FactJsonConstants
						.getJobJson().getBytes())), "/test_job/config");
		verify(failoverService, times(0)).removeFailoverInfo();
	}

	@Test
	public void assertFailoverSettingsChangedJobListenerWhenIsFailoverPathAndUpdateButDisableFailover() {
		failoverListenerManager.new FailoverSettingsChangedJobListener().dataChanged(null,
				new TreeCacheEvent(TreeCacheEvent.Type.NODE_UPDATED, new ChildData("/test_job/config", null,
						FactJsonConstants.getJobJson(false).getBytes())), "/test_job/config");
		verify(failoverService).removeFailoverInfo();
	}

}
