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
import org.apache.curator.framework.state.ConnectionState;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.unitils.util.ReflectionUtils;

import com.jd.framework.job.core.internal.executor.JobRegistry;
import com.jd.framework.job.core.internal.executor.quartz.JobScheduleController;
import com.jd.framework.job.core.internal.helper.JobNodeStorageHelper;
import com.jd.framework.job.core.internal.listener.sub.JobOperationListenerManager.ConnectionLostListener;
import com.jd.framework.job.core.internal.listener.sub.JobOperationListenerManager.JobPausedStatusJobListener;
import com.jd.framework.job.core.internal.service.ExecutionService;
import com.jd.framework.job.core.internal.service.SegmentService;
import com.jd.framework.job.core.internal.service.ServerService;
import com.jd.framework.job.utils.env.LocalHostService;

public class JobOperationListenerManagerTest {

	@Mock
	private JobNodeStorageHelper jobNodeStorage;

	@Mock
	private ServerService serverService;

	@Mock
	private SegmentService segmentService;

	@Mock
	private ExecutionService executionService;

	@Mock
	private JobScheduleController jobScheduleController;

	private String ip = new LocalHostService().getIp();

	private final JobOperationListenerManager jobOperationListenerManager = new JobOperationListenerManager(null,
			"test_job");

	@Before
	public void setUp() throws NoSuchFieldException {
		MockitoAnnotations.initMocks(this);
		ReflectionUtils.setFieldValue(jobOperationListenerManager, "serverService", serverService);
		ReflectionUtils.setFieldValue(jobOperationListenerManager, "segmentService", segmentService);
		ReflectionUtils.setFieldValue(jobOperationListenerManager, "executionService", executionService);
		ReflectionUtils.setFieldValue(jobOperationListenerManager, jobOperationListenerManager.getClass()
				.getSuperclass().getDeclaredField("jobNodeStorage"), jobNodeStorage);
	}

	@Test
	public void assertStart() {
		jobOperationListenerManager.start();
		verify(jobNodeStorage).addConnectionStateListener(Matchers.<ConnectionLostListener> any());
		verify(jobNodeStorage, times(3)).addDataListener(Matchers.<JobPausedStatusJobListener> any());
	}

	@Test
	public void assertConnectionLostListenerWhenConnectionStateIsLost() {
		JobRegistry.getInstance().addJobScheduleController("test_job", jobScheduleController);
		jobOperationListenerManager.new ConnectionLostListener().stateChanged(null, ConnectionState.LOST);
		verify(jobScheduleController).pauseJob();
	}

	@Test
	public void assertConnectionLostListenerWhenConnectionStateIsReconnectedAndIsNotPausedManually() {
		JobRegistry.getInstance().addJobScheduleController("test_job", jobScheduleController);
		when(segmentService.getLocalHostSegmentItems()).thenReturn(Arrays.asList(0, 1));
		when(serverService.isLocalhostServerEnabled()).thenReturn(true);
		when(serverService.isJobPausedManually()).thenReturn(false);
		jobOperationListenerManager.new ConnectionLostListener().stateChanged(null, ConnectionState.RECONNECTED);
		verify(serverService).isLocalhostServerEnabled();
		verify(serverService).persistServerOnline(true);
		verify(executionService).clearRunningInfo(Arrays.asList(0, 1));
		verify(jobScheduleController).resumeJob();
	}

	@Test
	public void assertConnectionLostListenerWhenConnectionStateIsReconnectedAndIsPausedManually() {
		JobRegistry.getInstance().addJobScheduleController("test_job", jobScheduleController);
		when(segmentService.getLocalHostSegmentItems()).thenReturn(Arrays.asList(0, 1));
		when(serverService.isLocalhostServerEnabled()).thenReturn(true);
		when(serverService.isJobPausedManually()).thenReturn(true);
		jobOperationListenerManager.new ConnectionLostListener().stateChanged(null, ConnectionState.RECONNECTED);
		verify(serverService).isLocalhostServerEnabled();
		verify(serverService).persistServerOnline(true);
		verify(executionService).clearRunningInfo(Arrays.asList(0, 1));
		verify(jobScheduleController, times(0)).resumeJob();
	}

	@Test
	public void assertConnectionLostListenerWhenConnectionStateIsOther() {
		JobRegistry.getInstance().addJobScheduleController("test_job", jobScheduleController);
		jobOperationListenerManager.new ConnectionLostListener().stateChanged(null, ConnectionState.CONNECTED);
		verify(jobScheduleController, times(0)).pauseJob();
		verify(jobScheduleController, times(0)).resumeJob();
	}

	@Test
	public void assertJobTriggerStatusJobListenerWhenRemove() {
		jobOperationListenerManager.new JobTriggerStatusJobListener().dataChanged(null,
				new TreeCacheEvent(TreeCacheEvent.Type.NODE_REMOVED, new ChildData("/test_job/servers/" + ip
						+ "/trigger", null, "".getBytes())), "/test_job/servers/" + ip + "/trigger");
		verify(serverService, times(0)).clearJobTriggerStatus();
		verify(jobScheduleController, times(0)).triggerJob();
	}

	@Test
	public void assertJobTriggerStatusJobListenerWhenIsAddButNotLocalHostJobTriggerPath() {
		jobOperationListenerManager.new JobTriggerStatusJobListener().dataChanged(null, new TreeCacheEvent(
				TreeCacheEvent.Type.NODE_ADDED,
				new ChildData("/test_job/servers/" + ip + "/other", null, "".getBytes())), "/test_job/servers/" + ip
				+ "/other");
		verify(serverService, times(0)).clearJobTriggerStatus();
		verify(jobScheduleController, times(0)).triggerJob();
	}

	@Test
	public void assertJobTriggerStatusJobListenerWhenIsAddAndIsJobLocalHostTriggerPathButNoJobRegister() {
		jobOperationListenerManager.new JobTriggerStatusJobListener().dataChanged(null,
				new TreeCacheEvent(TreeCacheEvent.Type.NODE_ADDED, new ChildData(
						"/test_job/servers/" + ip + "/trigger", null, "".getBytes())), "/test_job/servers/" + ip
						+ "/trigger");
		verify(serverService).clearJobTriggerStatus();
		verify(jobScheduleController, times(0)).triggerJob();
	}

	@Test
	public void assertJobTriggerStatusJobListenerWhenIsAddAndIsJobLocalHostTriggerPathAndJobRegisterButServerIsNotReady() {
		JobRegistry.getInstance().addJobScheduleController("test_job", jobScheduleController);
		jobOperationListenerManager.new JobTriggerStatusJobListener().dataChanged(null,
				new TreeCacheEvent(TreeCacheEvent.Type.NODE_ADDED, new ChildData(
						"/test_job/servers/" + ip + "/trigger", null, "".getBytes())), "/test_job/servers/" + ip
						+ "/trigger");
		verify(serverService).clearJobTriggerStatus();
		verify(serverService).isLocalhostServerReady();
		verify(jobScheduleController, times(0)).triggerJob();
	}

	@Test
	public void assertJobTriggerStatusJobListenerWhenIsAddAndIsJobLocalHostTriggerPathAndJobRegisterAndServerIsReady() {
		JobRegistry.getInstance().addJobScheduleController("test_job", jobScheduleController);
		when(serverService.isLocalhostServerReady()).thenReturn(true);
		jobOperationListenerManager.new JobTriggerStatusJobListener().dataChanged(null,
				new TreeCacheEvent(TreeCacheEvent.Type.NODE_ADDED, new ChildData(
						"/test_job/servers/" + ip + "/trigger", null, "".getBytes())), "/test_job/servers/" + ip
						+ "/trigger");
		verify(serverService).isLocalhostServerReady();
		verify(jobScheduleController).triggerJob();
		verify(serverService).clearJobTriggerStatus();
	}

	@Test
	public void assertJobPausedStatusJobListenerWhenIsNotJobPausedPath() {
		jobOperationListenerManager.new JobPausedStatusJobListener().dataChanged(null,
				new TreeCacheEvent(TreeCacheEvent.Type.NODE_UPDATED, new ChildData(
						"/test_job/servers/" + ip + "/other", null, "".getBytes())), "/test_job/servers/" + ip
						+ "/other");
		verify(jobScheduleController, times(0)).pauseJob();
		verify(jobScheduleController, times(0)).resumeJob();
	}

	@Test
	public void assertJobPausedStatusJobListenerWhenIsJobPausedPathButJobIsNotExisted() {
		jobOperationListenerManager.new JobPausedStatusJobListener().dataChanged(null,
				new TreeCacheEvent(TreeCacheEvent.Type.NODE_ADDED, new ChildData("/test_job/servers/" + ip + "/paused",
						null, "".getBytes())), "/test_job/servers/" + ip + "/paused");
		verify(jobScheduleController, times(0)).pauseJob();
		verify(jobScheduleController, times(0)).resumeJob();
	}

	@Test
	public void assertJobPausedStatusJobListenerWhenIsJobPausedPathAndUpdate() {
		JobRegistry.getInstance().addJobScheduleController("test_job", jobScheduleController);
		jobOperationListenerManager.new JobPausedStatusJobListener().dataChanged(null,
				new TreeCacheEvent(TreeCacheEvent.Type.NODE_UPDATED, new ChildData("/test_job/servers/" + ip
						+ "/paused", null, "".getBytes())), "/test_job/servers/" + ip + "/paused");
		verify(jobScheduleController, times(0)).pauseJob();
		verify(jobScheduleController, times(0)).resumeJob();
	}

	@Test
	public void assertJobPausedStatusJobListenerWhenIsJobPausedPathAndAdd() {
		JobRegistry.getInstance().addJobScheduleController("test_job", jobScheduleController);
		jobOperationListenerManager.new JobPausedStatusJobListener().dataChanged(null,
				new TreeCacheEvent(TreeCacheEvent.Type.NODE_ADDED, new ChildData("/test_job/servers/" + ip + "/paused",
						null, "".getBytes())), "/test_job/servers/" + ip + "/paused");
		verify(jobScheduleController).pauseJob();
		verify(jobScheduleController, times(0)).resumeJob();
	}

	@Test
	public void assertJobPausedStatusJobListenerWhenIsJobPausedPathAndRemove() {
		JobRegistry.getInstance().addJobScheduleController("test_job", jobScheduleController);
		jobOperationListenerManager.new JobPausedStatusJobListener().dataChanged(null,
				new TreeCacheEvent(TreeCacheEvent.Type.NODE_REMOVED, new ChildData("/test_job/servers/" + ip
						+ "/paused", null, "".getBytes())), "/test_job/servers/" + ip + "/paused");
		verify(jobScheduleController, times(0)).pauseJob();
		verify(jobScheduleController).resumeJob();
		verify(serverService).clearJobPausedStatus();
	}

	@Test
	public void assertJobShutdownStatusJobListenerWhenIsNotJobShutdownPath() {
		jobOperationListenerManager.new JobShutdownStatusJobListener().dataChanged(null,
				new TreeCacheEvent(TreeCacheEvent.Type.NODE_UPDATED, new ChildData(
						"/test_job/servers/" + ip + "/other", null, "".getBytes())), "/test_job/servers/" + ip
						+ "/other");
		verify(jobScheduleController, times(0)).shutdown();
	}

	@Test
	public void assertJobShutdownStatusJobListenerWhenIsJobShutdownPathButJobIsNotExisted() {
		jobOperationListenerManager.new JobShutdownStatusJobListener().dataChanged(null,
				new TreeCacheEvent(TreeCacheEvent.Type.NODE_ADDED, new ChildData("/test_job/servers/" + ip
						+ "/shutdown", null, "".getBytes())), "/test_job/servers/" + ip + "/shutdown");
		verify(jobScheduleController, times(0)).shutdown();
	}

	@Test
	public void assertJobShutdownStatusJobListenerWhenIsJobShutdownPathAndUpdate() {
		JobRegistry.getInstance().addJobScheduleController("test_job", jobScheduleController);
		jobOperationListenerManager.new JobShutdownStatusJobListener().dataChanged(null,
				new TreeCacheEvent(TreeCacheEvent.Type.NODE_UPDATED, new ChildData("/test_job/servers/" + ip
						+ "/shutdown", null, "".getBytes())), "/test_job/servers/" + ip + "/shutdown");
		verify(jobScheduleController, times(0)).shutdown();
	}

	@Test
	public void assertJobShutdownStatusJobListenerWhenIsJobShutdownPathAndAdd() {
		JobRegistry.getInstance().addJobScheduleController("test_job", jobScheduleController);
		jobOperationListenerManager.new JobShutdownStatusJobListener().dataChanged(null,
				new TreeCacheEvent(TreeCacheEvent.Type.NODE_ADDED, new ChildData("/test_job/servers/" + ip
						+ "/shutdown", null, "".getBytes())), "/test_job/servers/" + ip + "/shutdown");
		verify(jobScheduleController).shutdown();
		verify(serverService).processServerShutdown();
	}
}
