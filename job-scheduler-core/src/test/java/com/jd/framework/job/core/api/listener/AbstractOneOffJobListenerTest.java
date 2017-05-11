/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.core.api.listener;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.unitils.util.ReflectionUtils;

import com.google.common.collect.Sets;
import com.jd.framework.job.core.api.listener.fixture.ScheduleJobListenerCaller;
import com.jd.framework.job.core.api.listener.fixture.TestDistributeOnceJobListener;
import com.jd.framework.job.core.internal.service.GuaranteeService;
import com.jd.framework.job.exception.JobSystemException;
import com.jd.framework.job.executor.context.SegmentContexts;
import com.jd.framework.job.utils.env.TimeService;

public class AbstractOneOffJobListenerTest {

	@Mock
	private GuaranteeService guaranteeService;

	@Mock
	private TimeService timeService;

	@Mock
	private ScheduleJobListenerCaller factJobListenerCaller;

	private SegmentContexts segmentContexts;

	private TestDistributeOnceJobListener distributeOnceJobListener;

	@Before
	public void setUp() throws NoSuchFieldException {
		MockitoAnnotations.initMocks(this);
		distributeOnceJobListener = new TestDistributeOnceJobListener(factJobListenerCaller);
		ReflectionUtils.setFieldValue(distributeOnceJobListener,
				ReflectionUtils.getFieldWithName(AbstractOneOffJobListener.class, "guaranteeService", false),
				guaranteeService);
		ReflectionUtils.setFieldValue(distributeOnceJobListener,
				ReflectionUtils.getFieldWithName(AbstractOneOffJobListener.class, "timeService", false), timeService);
		Map<Integer, String> map = new HashMap<>(2, 1);
		map.put(0, "");
		map.put(1, "");
		segmentContexts = new SegmentContexts("fake_task_id", "test_job", 10, "", map);
	}

	@Test
	public void assertBeforeJobExecutedWhenIsAllStarted() {
		when(guaranteeService.isAllStarted()).thenReturn(true);
		distributeOnceJobListener.beforeJobExecuted(segmentContexts);
		verify(guaranteeService).registerStart(Sets.newHashSet(0, 1));
		verify(factJobListenerCaller).before();
		verify(guaranteeService).clearAllStartedInfo();
	}

	@Test
	public void assertBeforeJobExecutedWhenIsNotAllStartedAndNotTimeout() {
		when(guaranteeService.isAllStarted()).thenReturn(false);
		when(timeService.getCurrentMillis()).thenReturn(0L);
		distributeOnceJobListener.beforeJobExecuted(segmentContexts);
		verify(guaranteeService).registerStart(Sets.newHashSet(0, 1));
		verify(guaranteeService, times(0)).clearAllStartedInfo();
	}

	@Test(expected = JobSystemException.class)
	public void assertBeforeJobExecutedWhenIsNotAllStartedAndTimeout() {
		when(guaranteeService.isAllStarted()).thenReturn(false);
		when(timeService.getCurrentMillis()).thenReturn(0L, 2L);
		distributeOnceJobListener.beforeJobExecuted(segmentContexts);
		verify(guaranteeService).registerStart(Arrays.asList(0, 1));
		verify(guaranteeService, times(0)).clearAllStartedInfo();
	}

	@Test
	public void assertAfterJobExecutedWhenIsAllCompleted() {
		when(guaranteeService.isAllCompleted()).thenReturn(true);
		distributeOnceJobListener.afterJobExecuted(segmentContexts);
		verify(guaranteeService).registerComplete(Sets.newHashSet(0, 1));
		verify(factJobListenerCaller).after();
		verify(guaranteeService).clearAllCompletedInfo();
	}

	@Test
	public void assertAfterJobExecutedWhenIsAllCompletedAndNotTimeout() {
		when(guaranteeService.isAllCompleted()).thenReturn(false);
		when(timeService.getCurrentMillis()).thenReturn(0L);
		distributeOnceJobListener.afterJobExecuted(segmentContexts);
		verify(guaranteeService).registerComplete(Sets.newHashSet(0, 1));
		verify(guaranteeService, times(0)).clearAllCompletedInfo();
	}

	@Test(expected = JobSystemException.class)
	public void assertAfterJobExecutedWhenIsAllCompletedAndTimeout() {
		when(guaranteeService.isAllCompleted()).thenReturn(false);
		when(timeService.getCurrentMillis()).thenReturn(0L, 2L);
		distributeOnceJobListener.afterJobExecuted(segmentContexts);
		verify(guaranteeService).registerComplete(Arrays.asList(0, 1));
		verify(guaranteeService, times(0)).clearAllCompletedInfo();
	}

}
