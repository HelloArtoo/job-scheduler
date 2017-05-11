/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.core.internal.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.unitils.util.ReflectionUtils;

import com.google.common.collect.Lists;
import com.jd.framework.job.config.core.JobCoreConfiguration;
import com.jd.framework.job.config.type.FlowJobConfiguration;
import com.jd.framework.job.core.config.FactJobConfiguration;
import com.jd.framework.job.core.fixture.TestFlowJob;
import com.jd.framework.job.core.internal.helper.JobNodeStorageHelper;
import com.jd.framework.job.executor.context.SegmentContexts;
import com.jd.framework.job.utils.env.LocalHostService;

public class ExecutionContextServiceTest {
	
	@Mock
	private JobNodeStorageHelper jobNodeStorage;

	@Mock
	private LocalHostService localHostService;

	@Mock
	private ConfigService configService;

	private final ExecutionContextService executionContextService = new ExecutionContextService(null, "test_job");

	@Before
	public void setUp() throws NoSuchFieldException {
		MockitoAnnotations.initMocks(this);
		ReflectionUtils.setFieldValue(executionContextService, "jobNodeStorage", jobNodeStorage);
		ReflectionUtils.setFieldValue(executionContextService, "configService", configService);
		when(localHostService.getIp()).thenReturn("mockedIP");
		when(localHostService.getHostName()).thenReturn("mockedHostName");
	}

	@Test
	public void assertGetSegmentContextWhenNotAssignSegmentItem() {
		when(configService.load(false)).thenReturn(
				FactJobConfiguration
						.newBuilder(
								new FlowJobConfiguration(JobCoreConfiguration
										.newBuilder("test_job", "0/1 * * * * ?", 3).build(), TestFlowJob.class
										.getCanonicalName(), true)).monitorExecution(false).build());
		SegmentContexts segmentContexts = executionContextService.getJobSegmentContext(Collections
				.<Integer> emptyList());
		assertTrue(segmentContexts.getTaskId().startsWith("test_job@-@@-@READY@-@"));
		assertThat(segmentContexts.getSegmentsSum(), is(3));
		verify(configService).load(false);
	}

	@Test
	public void assertGetSegmentContextWhenAssignSegmentItems() {
		when(configService.load(false)).thenReturn(
				FactJobConfiguration
						.newBuilder(
								new FlowJobConfiguration(JobCoreConfiguration
										.newBuilder("test_job", "0/1 * * * * ?", 3)
										.segmentItemParameters("0=A,1=B,2=C").build(), TestFlowJob.class
										.getCanonicalName(), true)).monitorExecution(false).build());
		Map<Integer, String> map = new HashMap<>(3);
		map.put(0, "A");
		map.put(1, "B");
		SegmentContexts expected = new SegmentContexts("fake_task_id", "test_job", 3, "", map);
		assertSegmentContext(executionContextService.getJobSegmentContext(Arrays.asList(0, 1)), expected);
		verify(configService).load(false);
	}

	@Test
	public void assertGetSegmentContextWhenHasRunningItems() {
		when(configService.load(false)).thenReturn(
				FactJobConfiguration
						.newBuilder(
								new FlowJobConfiguration(JobCoreConfiguration
										.newBuilder("test_job", "0/1 * * * * ?", 3)
										.segmentItemParameters("0=A,1=B,2=C").build(), TestFlowJob.class
										.getCanonicalName(), true)).monitorExecution(true).build());
		when(jobNodeStorage.isJobNodeExisted("execution/0/running")).thenReturn(false);
		when(jobNodeStorage.isJobNodeExisted("execution/1/running")).thenReturn(true);
		Map<Integer, String> map = new HashMap<>(1, 1);
		map.put(0, "A");
		SegmentContexts expected = new SegmentContexts("fake_task_id", "test_job", 3, "", map);
		assertSegmentContext(executionContextService.getJobSegmentContext(Lists.newArrayList(0, 1)), expected);
		verify(configService).load(false);
		verify(jobNodeStorage).isJobNodeExisted("execution/0/running");
		verify(jobNodeStorage).isJobNodeExisted("execution/1/running");
	}

	private void assertSegmentContext(final SegmentContexts actual, final SegmentContexts expected) {
		assertThat(actual.getJobName(), is(expected.getJobName()));
		assertThat(actual.getSegmentsSum(), is(expected.getSegmentsSum()));
		assertThat(actual.getJobParameter(), is(expected.getJobParameter()));
		assertThat(actual.getSegmentItemParameters().size(), is(expected.getSegmentItemParameters().size()));
		for (int i = 0; i < expected.getSegmentItemParameters().size(); i++) {
			assertThat(actual.getSegmentItemParameters().get(i), is(expected.getSegmentItemParameters().get(i)));
		}
	}
}
