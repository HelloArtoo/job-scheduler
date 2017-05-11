/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.core.internal.listener;

import static org.mockito.Mockito.verify;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.unitils.util.ReflectionUtils;

import com.jd.framework.job.core.api.listener.ScheduleJobListener;
import com.jd.framework.job.core.internal.listener.sub.ConfigListenerManager;
import com.jd.framework.job.core.internal.listener.sub.ElectionListenerManager;
import com.jd.framework.job.core.internal.listener.sub.ExecutionListenerManager;
import com.jd.framework.job.core.internal.listener.sub.FailoverListenerManager;
import com.jd.framework.job.core.internal.listener.sub.GuaranteeListenerManager;
import com.jd.framework.job.core.internal.listener.sub.JobOperationListenerManager;
import com.jd.framework.job.core.internal.listener.sub.SegmentListenerManager;

public class ListenerManagerTest {

	@Mock
	private ElectionListenerManager electionListenerManager;

	@Mock
	private SegmentListenerManager segmentListenerManager;

	@Mock
	private ExecutionListenerManager executionListenerManager;

	@Mock
	private FailoverListenerManager failoverListenerManager;

	@Mock
	private JobOperationListenerManager jobOperationListenerManager;

	@Mock
	private ConfigListenerManager configListenerManager;

	@Mock
	private GuaranteeListenerManager guaranteeListenerManager;

	private final ListenerManager listenerManager = new ListenerManager(null, "test_job",
			Collections.<ScheduleJobListener> emptyList());

	@Before
	public void setUp() throws NoSuchFieldException {
		MockitoAnnotations.initMocks(this);
		ReflectionUtils.setFieldValue(listenerManager, "electionListenerManager", electionListenerManager);
		ReflectionUtils.setFieldValue(listenerManager, "segmentListenerManager", segmentListenerManager);
		ReflectionUtils.setFieldValue(listenerManager, "executionListenerManager", executionListenerManager);
		ReflectionUtils.setFieldValue(listenerManager, "failoverListenerManager", failoverListenerManager);
		ReflectionUtils.setFieldValue(listenerManager, "jobOperationListenerManager", jobOperationListenerManager);
		ReflectionUtils.setFieldValue(listenerManager, "configListenerManager", configListenerManager);
		ReflectionUtils.setFieldValue(listenerManager, "guaranteeListenerManager", guaranteeListenerManager);
	}

	@Test
	public void assertStartAllListeners() {
		listenerManager.startAllListeners();
		verify(electionListenerManager).start();
		verify(segmentListenerManager).start();
		verify(executionListenerManager).start();
		verify(failoverListenerManager).start();
		verify(jobOperationListenerManager).start();
		verify(configListenerManager).start();
		verify(guaranteeListenerManager).start();
	}

	@Test
	public void assertSetCurrentSegmentTotalCount() {
		listenerManager.setCurrentSegmentTotalCount(10);
		verify(segmentListenerManager).setCurrentSegmentTotalCount(10);
	}

}
