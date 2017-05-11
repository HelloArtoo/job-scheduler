/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.executor.type;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.jd.framework.job.executor.context.SegmentContexts;
import com.jd.framework.job.executor.facade.JobFacade;
import com.jd.framework.job.fixture.JobVerify;
import com.jd.framework.job.fixture.SegmentContextsBuilder;
import com.jd.framework.job.fixture.config.TestFlowJobConfiguration;
import com.jd.framework.job.fixture.job.JobCaller;
import com.jd.framework.job.fixture.job.TestFlowJob;

/**
 * 
 * FLOW JOB TEST CASE
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-25
 */
@RunWith(MockitoJUnitRunner.class)
public class FlowJobExecutorTest {

	@Mock
	private JobCaller jobCaller;

	@Mock
	private JobFacade jobFacade;

	private SegmentContexts segmentContexts;

	private FlowJobExecutor flowJobExecutor;

	@After
	public void tearDown() throws NoSuchFieldException {
		verify(jobFacade).loadJobRootConfiguration(true);
		JobVerify.verifyForIsNotMisfire(jobFacade, segmentContexts);
	}

	/**
	 * fetch数据空或者size为0时不执行
	 * 
	 * @author Rong Hu
	 */
	@Test
	public void assertExecuteWhenFetchDataIsNullAndEmpty() {
		setUp(true, SegmentContextsBuilder.getMultipleSegmentContexts());
		when(jobCaller.fetchData(0)).thenReturn(null);
		when(jobCaller.fetchData(1)).thenReturn(Collections.emptyList());
		flowJobExecutor.execute();
		verify(jobCaller).fetchData(0);
		verify(jobCaller).fetchData(1);
		verify(jobCaller, times(0)).processData(any());
	}

	@Test
	public void assertExecuteWhenFetchDataIsNotEmptyForUnStreamingProcessAndSingleSegmentItem() {
		setUp(false, SegmentContextsBuilder.getSingleSegmentContexts());
		doThrow(new IllegalStateException()).when(jobCaller).fetchData(0);
		flowJobExecutor.execute();
		verify(jobCaller).fetchData(0);
		verify(jobCaller, times(0)).processData(any());
	}

	@Test
	public void assertExecuteWhenFetchDataIsNotEmptyForUnStreamingProcessAndMultipleSegmentItems() {
		setUp(false, SegmentContextsBuilder.getMultipleSegmentContexts());
		when(jobCaller.fetchData(0)).thenReturn(Arrays.<Object> asList(1, 2));
		when(jobCaller.fetchData(1)).thenReturn(Arrays.<Object> asList(3, 4));
		doThrow(new IllegalStateException()).when(jobCaller).processData(4);
		flowJobExecutor.execute();
		verify(jobCaller).fetchData(0);
		verify(jobCaller).fetchData(1);
		verify(jobCaller).processData(1);
		verify(jobCaller).processData(2);
		verify(jobCaller).processData(3);
		verify(jobCaller).processData(4);
	}

	/**
	 * fetch到2次数据的时候
	 * 
	 * @author Rong Hu
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void assertExecuteWhenFetchDataIsNotEmptyForStreamingProcessAndSingleSegmentItem() {
		setUp(true, SegmentContextsBuilder.getSingleSegmentContexts());
		when(jobCaller.fetchData(0)).thenReturn(Collections.<Object> singletonList(1), Collections.emptyList());
		when(jobFacade.isEligibleForJobRunning()).thenReturn(true);
		flowJobExecutor.execute();
		verify(jobCaller, times(2)).fetchData(0);
		verify(jobCaller).processData(1);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void assertExecuteWhenFetchDataIsNotEmptyForStreamingProcessAndMultipleSegmentItems() {
		setUp(true, SegmentContextsBuilder.getMultipleSegmentContexts());
		when(jobCaller.fetchData(0)).thenReturn(Collections.<Object> singletonList(1), Collections.emptyList());
		when(jobCaller.fetchData(1)).thenReturn(Collections.<Object> singletonList(2), Collections.emptyList());
		when(jobFacade.isEligibleForJobRunning()).thenReturn(true);
		flowJobExecutor.execute();
		verify(jobCaller, times(2)).fetchData(0);
		verify(jobCaller, times(2)).fetchData(1);
		verify(jobCaller).processData(1);
		verify(jobCaller).processData(2);
	}

	/**
	 * 中间一个异常后面不执行
	 * 
	 * @author Rong Hu
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void assertExecuteWhenFetchDataIsNotEmptyAndProcessFailureWithExceptionForStreamingProcess() {
		setUp(true, SegmentContextsBuilder.getMultipleSegmentContexts());
		when(jobCaller.fetchData(0)).thenReturn(Collections.<Object> singletonList(1), Collections.emptyList());
		when(jobCaller.fetchData(1)).thenReturn(Arrays.<Object> asList(2, 3), Collections.emptyList());
		when(jobFacade.isEligibleForJobRunning()).thenReturn(true);
		doThrow(new IllegalStateException()).when(jobCaller).processData(2);
		flowJobExecutor.execute();
		verify(jobCaller, times(2)).fetchData(0);
		verify(jobCaller, times(1)).fetchData(1);
		verify(jobCaller).processData(1);
		verify(jobCaller).processData(2);
		verify(jobCaller, times(0)).processData(3);
	}

	/**
	 * 最后一个异常
	 * 
	 * @author Rong Hu
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void assertExecuteWhenFetchDataIsNotEmptyAndIsEligibleForJobRunningForStreamingProcess() {
		setUp(true, SegmentContextsBuilder.getMultipleSegmentContexts());
		when(jobFacade.isEligibleForJobRunning()).thenReturn(true);
		when(jobCaller.fetchData(0)).thenReturn(Arrays.<Object> asList(1, 2), Collections.emptyList());
		when(jobCaller.fetchData(1)).thenReturn(Arrays.<Object> asList(3, 4), Collections.emptyList());
		doThrow(new IllegalStateException()).when(jobCaller).processData(4);
		flowJobExecutor.execute();
		verify(jobCaller, times(2)).fetchData(0);
		verify(jobCaller, times(1)).fetchData(1);
		verify(jobCaller).processData(1);
		verify(jobCaller).processData(2);
		verify(jobCaller).processData(3);
		verify(jobCaller).processData(4);
	}

	@Test
	public void assertExecuteWhenFetchDataIsNotEmptyAndIsNotEligibleForJobRunningForStreamingProcess() {
		setUp(true, SegmentContextsBuilder.getMultipleSegmentContexts());
		when(jobFacade.isEligibleForJobRunning()).thenReturn(false);
		when(jobCaller.fetchData(0)).thenReturn(Arrays.<Object> asList(1, 2));
		when(jobCaller.fetchData(1)).thenReturn(Arrays.<Object> asList(3, 4));
		doThrow(new IllegalStateException()).when(jobCaller).processData(4);
		flowJobExecutor.execute();
		verify(jobCaller).fetchData(0);
		verify(jobCaller).fetchData(1);
		verify(jobCaller).processData(1);
		verify(jobCaller).processData(2);
		verify(jobCaller).processData(3);
		verify(jobCaller).processData(4);
	}

	private void setUp(final boolean isStreamingProcess, final SegmentContexts segmentContexts) {
		this.segmentContexts = segmentContexts;
		when(jobFacade.loadJobRootConfiguration(true)).thenReturn(new TestFlowJobConfiguration(isStreamingProcess));
		when(jobFacade.getSegmentContexts()).thenReturn(segmentContexts);
		flowJobExecutor = new FlowJobExecutor(new TestFlowJob(jobCaller), jobFacade);
		JobVerify.prepareForIsNotMisfire(jobFacade, segmentContexts);
	}

}
