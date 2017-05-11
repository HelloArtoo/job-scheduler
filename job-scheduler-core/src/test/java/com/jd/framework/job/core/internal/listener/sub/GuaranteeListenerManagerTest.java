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

import java.util.Arrays;

import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.unitils.util.ReflectionUtils;

import com.jd.framework.job.core.api.listener.AbstractOneOffJobListener;
import com.jd.framework.job.core.api.listener.ScheduleJobListener;
import com.jd.framework.job.core.internal.helper.JobNodeStorageHelper;
import com.jd.framework.job.core.internal.listener.parent.AbstractJobListener;

public class GuaranteeListenerManagerTest {

	@Mock
	private JobNodeStorageHelper jobNodeStorage;

	@Mock
	private ScheduleJobListener scheduleJobListener;

	@Mock
	private AbstractOneOffJobListener oneOffJobListener;

	private GuaranteeListenerManager guaranteeListenerManager;

	@Before
	public void setUp() throws NoSuchFieldException {
		MockitoAnnotations.initMocks(this);
		guaranteeListenerManager = new GuaranteeListenerManager(null, "test_job", Arrays.asList(scheduleJobListener,
				oneOffJobListener));
		ReflectionUtils.setFieldValue(guaranteeListenerManager, guaranteeListenerManager.getClass().getSuperclass()
				.getDeclaredField("jobNodeStorage"), jobNodeStorage);
	}

	@Test
	public void assertStart() {
		guaranteeListenerManager.start();
		verify(jobNodeStorage, times(2)).addDataListener(Matchers.<AbstractJobListener> any());
	}

	@Test
	public void assertStartedNodeRemovedJobListenerWhenIsNotRemoved() {
		guaranteeListenerManager.new StartedNodeRemovedJobListener().dataChanged(null, new TreeCacheEvent(
				TreeCacheEvent.Type.NODE_UPDATED, new ChildData("/test_job/guarantee/started", null, "".getBytes())),
				"/test_job/guarantee/started");
		verify(oneOffJobListener, times(0)).notifyWaitingTaskStart();
	}

	@Test
	public void assertStartedNodeRemovedJobListenerWhenIsNotStartedNode() {
		guaranteeListenerManager.new StartedNodeRemovedJobListener().dataChanged(null, new TreeCacheEvent(
				TreeCacheEvent.Type.NODE_REMOVED, new ChildData("/other_job/guarantee/started", null, "".getBytes())),
				"/other_job/guarantee/started");
		verify(oneOffJobListener, times(0)).notifyWaitingTaskStart();
	}

	@Test
	public void assertStartedNodeRemovedJobListenerWhenIsRemovedAndStartedNode() {
		guaranteeListenerManager.new StartedNodeRemovedJobListener().dataChanged(null, new TreeCacheEvent(
				TreeCacheEvent.Type.NODE_REMOVED, new ChildData("/test_job/guarantee/started", null, "".getBytes())),
				"/test_job/guarantee/started");
		verify(oneOffJobListener).notifyWaitingTaskStart();
	}

	@Test
	public void assertCompletedNodeRemovedJobListenerWhenIsNotRemoved() {
		guaranteeListenerManager.new CompletedNodeRemovedJobListener().dataChanged(null, new TreeCacheEvent(
				TreeCacheEvent.Type.NODE_UPDATED, new ChildData("/test_job/guarantee/completed", null, "".getBytes())),
				"/test_job/guarantee/completed");
		verify(oneOffJobListener, times(0)).notifyWaitingTaskStart();
	}

	@Test
	public void assertCompletedNodeRemovedJobListenerWhenIsNotCompletedNode() {
		guaranteeListenerManager.new CompletedNodeRemovedJobListener().dataChanged(null,
				new TreeCacheEvent(TreeCacheEvent.Type.NODE_REMOVED, new ChildData("/other_job/guarantee/completed",
						null, "".getBytes())), "/other_job/guarantee/completed");
		verify(oneOffJobListener, times(0)).notifyWaitingTaskStart();
	}

	@Test
	public void assertCompletedNodeRemovedJobListenerWhenIsRemovedAndCompletedNode() {
		guaranteeListenerManager.new CompletedNodeRemovedJobListener().dataChanged(null, new TreeCacheEvent(
				TreeCacheEvent.Type.NODE_REMOVED, new ChildData("/test_job/guarantee/completed", null, "".getBytes())),
				"/test_job/guarantee/completed");
		verify(oneOffJobListener).notifyWaitingTaskComplete();
	}
}
