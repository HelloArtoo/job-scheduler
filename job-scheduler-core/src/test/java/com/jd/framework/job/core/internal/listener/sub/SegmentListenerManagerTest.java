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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
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
import com.jd.framework.job.core.internal.listener.parent.AbstractJobListener;
import com.jd.framework.job.core.internal.service.ExecutionService;
import com.jd.framework.job.core.internal.service.SegmentService;
import com.jd.framework.job.utils.env.LocalHostService;

public class SegmentListenerManagerTest {

	@Mock
	private JobNodeStorageHelper jobNodeStorage;

	@Mock
	private SegmentService segmentService;

	@Mock
	private ExecutionService executionService;

	private String ip = new LocalHostService().getIp();

	private final SegmentListenerManager segmentListenerManager = new SegmentListenerManager(null, "test_job");

	@Before
	public void setUp() throws NoSuchFieldException {
		MockitoAnnotations.initMocks(this);
		ReflectionUtils.setFieldValue(segmentListenerManager, segmentListenerManager.getClass().getSuperclass()
				.getDeclaredField("jobNodeStorage"), jobNodeStorage);
		ReflectionUtils.setFieldValue(segmentListenerManager, "segmentService", segmentService);
		ReflectionUtils.setFieldValue(segmentListenerManager, "executionService", executionService);
	}

	@Test
	public void assertStart() {
		segmentListenerManager.start();
		verify(jobNodeStorage, times(2)).addDataListener(Matchers.<AbstractJobListener> any());
	}

	@Test
	public void assertSegmentTotalCountChangedJobListenerWhenIsNotConfigPath() {
		segmentListenerManager.new SegmentTotalCountChangedJobListener().dataChanged(null, new TreeCacheEvent(
				TreeCacheEvent.Type.NODE_ADDED, new ChildData("/test_job/config/other", null, "".getBytes())),
				"/test_job/config/other");
		verify(segmentService, times(0)).setResegmentFlag();
	}

	@Test
	public void assertSegmentTotalCountChangedJobListenerWhenIsConfigPathButCurrentSegmentTotalCountIsZero() {
		segmentListenerManager.new SegmentTotalCountChangedJobListener().dataChanged(null, new TreeCacheEvent(
				TreeCacheEvent.Type.NODE_ADDED, new ChildData("/test_job/config", null, FactJsonConstants.getJobJson()
						.getBytes())), "/test_job/config");
		verify(segmentService, times(0)).setResegmentFlag();
		verify(executionService, times(0)).setNeedFixExecutionInfoFlag();
	}

	@Test
	public void assertSegmentTotalCountChangedJobListenerWhenIsConfigPathAndCurrentSegmentTotalCountIsEqualToNewSegmentTotalCount() {
		segmentListenerManager.setCurrentSegmentTotalCount(3);
		segmentListenerManager.new SegmentTotalCountChangedJobListener().dataChanged(null, new TreeCacheEvent(
				TreeCacheEvent.Type.NODE_ADDED, new ChildData("/test_job/config", null, FactJsonConstants.getJobJson()
						.getBytes())), "/test_job/config");
		verify(segmentService, times(0)).setResegmentFlag();
		verify(executionService, times(0)).setNeedFixExecutionInfoFlag();
	}

	@Test
	public void assertSegmentTotalCountChangedJobListenerWhenIsConfigPathAndCurrentSegmentTotalCountIsNotEqualToNewSegmentTotalCount()
			throws NoSuchFieldException {
		segmentListenerManager.setCurrentSegmentTotalCount(5);
		segmentListenerManager.new SegmentTotalCountChangedJobListener().dataChanged(null, new TreeCacheEvent(
				TreeCacheEvent.Type.NODE_ADDED, new ChildData("/test_job/config", null, FactJsonConstants.getJobJson()
						.getBytes())), "/test_job/config");
		assertThat(
				(Integer) ReflectionUtils.getFieldValue(segmentListenerManager,
						SegmentListenerManager.class.getDeclaredField("currentSegmentTotalCount")), is(3));
		verify(segmentService).setResegmentFlag();
		verify(executionService).setNeedFixExecutionInfoFlag();
	}

	@Test
	public void assertListenServersChangedJobListenerWhenIsNotServerStatusPath() {
		segmentListenerManager.new ListenServersChangedJobListener().dataChanged(null, new TreeCacheEvent(
				TreeCacheEvent.Type.NODE_ADDED,
				new ChildData("/test_job/servers/" + ip + "/other", null, "".getBytes())), "/test_job/servers/" + ip
				+ "/other");
		verify(segmentService, times(0)).setResegmentFlag();
	}

	@Test
	public void assertListenServersChangedJobListenerWhenIsServerStatusPathButUpdate() {
		segmentListenerManager.new ListenServersChangedJobListener().dataChanged(null,
				new TreeCacheEvent(TreeCacheEvent.Type.NODE_UPDATED, new ChildData("/test_job/servers/" + ip
						+ "/status", null, "".getBytes())), "/test_job/servers/" + ip + "/status");
		verify(segmentService, times(0)).setResegmentFlag();
	}

	@Test
	public void assertListenServersChangedJobListenerWhenIsServerStatusPathAndAdd() {
		segmentListenerManager.new ListenServersChangedJobListener().dataChanged(null,
				new TreeCacheEvent(TreeCacheEvent.Type.NODE_ADDED, new ChildData("/test_job/servers/" + ip + "/status",
						null, "".getBytes())), "/test_job/servers/" + ip + "/status");
		verify(segmentService).setResegmentFlag();
	}

	@Test
	public void assertListenServersChangedJobListenerWhenIsServerStatusPathButUpdateAndIsServerDisabledPath() {
		segmentListenerManager.new ListenServersChangedJobListener().dataChanged(null,
				new TreeCacheEvent(TreeCacheEvent.Type.NODE_UPDATED, new ChildData("/test_job/servers/" + ip
						+ "/disabled", null, "".getBytes())), "/test_job/servers/" + ip + "/disabled");
		verify(segmentService).setResegmentFlag();
	}

}
