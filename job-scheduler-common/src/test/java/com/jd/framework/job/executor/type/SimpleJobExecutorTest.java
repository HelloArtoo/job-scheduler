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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.unitils.util.ReflectionUtils;

import com.jd.framework.job.event.type.JobStatusTraceEvent;
import com.jd.framework.job.event.type.JobStatusTraceEvent.State;
import com.jd.framework.job.exception.JobExecutionEnvironmentException;
import com.jd.framework.job.exception.JobSystemException;
import com.jd.framework.job.executor.AbstractJobExecutor;
import com.jd.framework.job.executor.context.SegmentContexts;
import com.jd.framework.job.executor.facade.JobFacade;
import com.jd.framework.job.executor.handler.exception.DefaultJobExceptionHandler;
import com.jd.framework.job.executor.handler.threadpool.DefaultExecutorServiceHandler;
import com.jd.framework.job.fixture.JobVerify;
import com.jd.framework.job.fixture.SegmentContextsBuilder;
import com.jd.framework.job.fixture.config.TestSimpleJobConfiguration;
import com.jd.framework.job.fixture.job.JobCaller;
import com.jd.framework.job.fixture.job.TestSimpleJob;

@RunWith(MockitoJUnitRunner.class)
public class SimpleJobExecutorTest {

	@Mock
	private JobCaller jobCaller;

	@Mock
	private JobFacade jobFacade;

	private SimpleJobExecutor simpleJobExecutor;

	@Before
	public void setUp() throws NoSuchFieldException {
		when(jobFacade.loadJobRootConfiguration(true)).thenReturn(new TestSimpleJobConfiguration());
		simpleJobExecutor = new SimpleJobExecutor(new TestSimpleJob(jobCaller), jobFacade);
	}

	@Test
	public void assertNewExecutorWithDefaultHandlers() throws NoSuchFieldException {
		when(jobFacade.loadJobRootConfiguration(true)).thenReturn(
				new TestSimpleJobConfiguration("ErrorHandler", Object.class.getName()));
		SimpleJobExecutor simpleJobExecutor = new SimpleJobExecutor(new TestSimpleJob(jobCaller), jobFacade);
		assertThat(
				ReflectionUtils.getFieldValue(simpleJobExecutor,
						AbstractJobExecutor.class.getDeclaredField("executorService")),
				instanceOf(new DefaultExecutorServiceHandler().createExecutorService("test_job").getClass()));
		assertThat(
				ReflectionUtils.getFieldValue(simpleJobExecutor,
						AbstractJobExecutor.class.getDeclaredField("jobExceptionHandler")),
				instanceOf(DefaultJobExceptionHandler.class));
	}

	/**
	 * 执行环境，检查与ZK服务器时间异常时不执行
	 * 
	 * @throws JobExecutionEnvironmentException
	 * @author Rong Hu
	 */
	@Test(expected = JobSystemException.class)
	public void assertExecuteWhenCheckMaxTimeDiffSecondsIntolerable() throws JobExecutionEnvironmentException {
		doThrow(JobExecutionEnvironmentException.class).when(jobFacade).checkJobExecutionEnvironment();
		try {
			simpleJobExecutor.execute();
		} finally {
			verify(jobFacade).checkJobExecutionEnvironment();
			// 没有执行execute
			verify(jobCaller, times(0)).execute();
		}
	}

	/**
	 * 前一个任务还在执行的时候不执行
	 * 
	 * @throws JobExecutionEnvironmentException
	 * @author Rong Hu
	 */
	@Test
	public void assertExecuteWhenPreviousJobStillRunning() throws JobExecutionEnvironmentException {
		SegmentContexts segmentContexts = new SegmentContexts("fake_task_id", "test_job", 10, "",
				Collections.<Integer, String> emptyMap());
		when(jobFacade.getSegmentContexts()).thenReturn(segmentContexts);
		when(jobFacade.misfireIfNecessary(segmentContexts.getSegmentItemParameters().keySet())).thenReturn(true);
		simpleJobExecutor.execute();
		verify(jobFacade).postJobStatusTraceEvent(segmentContexts.getTaskId(), State.TASK_STAGING,
				"Job 'test_job' execute begin.");
		verify(jobFacade)
				.postJobStatusTraceEvent(segmentContexts.getTaskId(), State.TASK_FINISHED,
						"Previous job 'test_job' - segmentItems '[]' is still running, misfired job will start after previous job completed.");
		verify(jobFacade).checkJobExecutionEnvironment();
		verify(jobFacade).getSegmentContexts();
		verify(jobFacade).misfireIfNecessary(segmentContexts.getSegmentItemParameters().keySet());
		verify(jobCaller, times(0)).execute();
	}

	/**
	 * SegmentItems空的时候不执行
	 * 
	 * @throws JobExecutionEnvironmentException
	 * @author Rong Hu
	 */
	@Test
	public void assertExecuteWhenSegmentItemsIsEmpty() throws JobExecutionEnvironmentException {
		SegmentContexts segmentContexts = new SegmentContexts("fake_task_id", "test_job", 10, "",
				Collections.<Integer, String> emptyMap());
		JobVerify.prepareForIsNotMisfire(jobFacade, segmentContexts);
		simpleJobExecutor.execute();
		verify(jobFacade).postJobStatusTraceEvent(segmentContexts.getTaskId(), State.TASK_STAGING,
				"Job 'test_job' execute begin.");
		verify(jobFacade).postJobStatusTraceEvent(segmentContexts.getTaskId(), State.TASK_FINISHED,
				"Segment item for job 'test_job' is empty.");
		verify(jobFacade).checkJobExecutionEnvironment();
		verify(jobFacade).getSegmentContexts();
		verify(jobFacade).misfireIfNecessary(segmentContexts.getSegmentItemParameters().keySet());
		verify(jobCaller, times(0)).execute();
	}

	/**
	 * segmentItemParameters size = 1
	 * 
	 * @throws JobExecutionEnvironmentException
	 * @author Rong Hu
	 */
	@Test(expected = JobSystemException.class)
	public void assertExecuteWhenRunOnceAndThrowExceptionForSingleSegmentItem() throws JobExecutionEnvironmentException {
		assertExecuteWhenRunOnceAndThrowException(SegmentContextsBuilder.getSingleSegmentContexts());
	}

	/**
	 * segmentItemParameters size > 1
	 * 
	 * @throws JobExecutionEnvironmentException
	 * @author Rong Hu
	 */
	@Test
	public void assertExecuteWhenRunOnceAndThrowExceptionForMultipleSegmentItems()
			throws JobExecutionEnvironmentException {
		assertExecuteWhenRunOnceAndThrowException(SegmentContextsBuilder.getMultipleSegmentContexts());
	}

	/**
	 * 任务异常时候
	 * 
	 * @param segmentContexts
	 * @throws JobExecutionEnvironmentException
	 * @author Rong Hu
	 */
	private void assertExecuteWhenRunOnceAndThrowException(final SegmentContexts segmentContexts)
			throws JobExecutionEnvironmentException {
		JobVerify.prepareForIsNotMisfire(jobFacade, segmentContexts);
		doThrow(RuntimeException.class).when(jobCaller).execute();
		try {
			simpleJobExecutor.execute();
		} finally {
			verify(jobFacade).postJobStatusTraceEvent(segmentContexts.getTaskId(), State.TASK_STAGING,
					"Job 'test_job' execute begin.");
			verify(jobFacade).postJobStatusTraceEvent(segmentContexts.getTaskId(), State.TASK_RUNNING, "");
			String errorMessage;
			String lineSeparator = System.getProperty("line.separator");
			if (1 == segmentContexts.getSegmentItemParameters().size()) {
				errorMessage = "{0=java.lang.RuntimeException" + lineSeparator + "}";
			} else {
				errorMessage = "{0=java.lang.RuntimeException" + lineSeparator + ", 1=java.lang.RuntimeException"
						+ lineSeparator + "}";
			}
			verify(jobFacade).postJobStatusTraceEvent(segmentContexts.getTaskId(), State.TASK_ERROR, errorMessage);
			verify(jobFacade).checkJobExecutionEnvironment();
			verify(jobFacade).getSegmentContexts();
			verify(jobFacade).misfireIfNecessary(segmentContexts.getSegmentItemParameters().keySet());
			verify(jobFacade).registerJobBegin(segmentContexts);
			verify(jobCaller, times(segmentContexts.getSegmentsSum())).execute();
			verify(jobFacade).registerJobCompleted(segmentContexts);
		}
	}

	/**
	 * 仅有一个SegmentItem执行成功后
	 * 
	 * @author Rong Hu
	 */
	@Test
	public void assertExecuteWhenRunOnceSuccessForSingleSegmentItems() {
		assertExecuteWhenRunOnceSuccess(SegmentContextsBuilder.getSingleSegmentContexts());
	}

	/**
	 * 多个SegmentItem执行成功后
	 * 
	 * @author Rong Hu
	 */
	@Test
	public void assertExecuteWhenRunOnceSuccessForMultipleSegmentItems() {
		assertExecuteWhenRunOnceSuccess(SegmentContextsBuilder.getMultipleSegmentContexts());
	}

	private void assertExecuteWhenRunOnceSuccess(final SegmentContexts segmentContexts) {
		JobVerify.prepareForIsNotMisfire(jobFacade, segmentContexts);
		simpleJobExecutor.execute();
		verify(jobFacade).postJobStatusTraceEvent(segmentContexts.getTaskId(), State.TASK_STAGING,
				"Job 'test_job' execute begin.");
		verify(jobFacade).postJobStatusTraceEvent(segmentContexts.getTaskId(), State.TASK_FINISHED, "");
		JobVerify.verifyForIsNotMisfire(jobFacade, segmentContexts);
		// 多次成功
		verify(jobCaller, times(segmentContexts.getSegmentsSum())).execute();
	}

	/**
	 * Misfire empty
	 * 
	 * @author Rong Hu
	 */
	@Test
	public void assertExecuteWhenRunOnceWithMisfireIsEmpty() {
		SegmentContexts segmentContexts = SegmentContextsBuilder.getMultipleSegmentContexts();
		when(jobFacade.getSegmentContexts()).thenReturn(segmentContexts);
		// 是否执行misFired
		when(jobFacade.isExecuteMisfired(segmentContexts.getSegmentItemParameters().keySet())).thenReturn(false);
		simpleJobExecutor.execute();
		JobVerify.verifyForIsNotMisfire(jobFacade, segmentContexts);
		verify(jobCaller, times(2)).execute();
	}

	/**
	 * isExecuteMisfired : false<br/>
	 * isEligibleForJobRunning : false
	 * 
	 * @author Rong Hu
	 */
	@Test
	public void assertExecuteWhenRunOnceWithMisfireIsNotEmptyButIsNotEligibleForJobRunning() {
		SegmentContexts segmentContexts = SegmentContextsBuilder.getMultipleSegmentContexts();
		when(jobFacade.getSegmentContexts()).thenReturn(segmentContexts);
		when(jobFacade.isExecuteMisfired(segmentContexts.getSegmentItemParameters().keySet())).thenReturn(false);
		when(jobFacade.isEligibleForJobRunning()).thenReturn(false);
		simpleJobExecutor.execute();
		JobVerify.verifyForIsNotMisfire(jobFacade, segmentContexts);
		verify(jobCaller, times(2)).execute();
		verify(jobFacade, times(0)).clearMisfire(segmentContexts.getSegmentItemParameters().keySet());
	}

	@Test
	public void assertExecuteWhenRunOnceWithMisfire() throws JobExecutionEnvironmentException {
		SegmentContexts segmentContexts = SegmentContextsBuilder.getMultipleSegmentContexts();
		when(jobFacade.getSegmentContexts()).thenReturn(segmentContexts);
		when(jobFacade.misfireIfNecessary(segmentContexts.getSegmentItemParameters().keySet())).thenReturn(false);
		// 两次，misFire一次，第二次成功
		when(jobFacade.isExecuteMisfired(segmentContexts.getSegmentItemParameters().keySet())).thenReturn(true, false);
		when(jobFacade.isNeedSegment()).thenReturn(false);
		simpleJobExecutor.execute();
		verify(jobFacade).postJobStatusTraceEvent(segmentContexts.getTaskId(), JobStatusTraceEvent.State.TASK_STAGING,
				"Job 'test_job' execute begin.");
		verify(jobFacade, times(2)).postJobStatusTraceEvent(segmentContexts.getTaskId(),
				JobStatusTraceEvent.State.TASK_RUNNING, "");
		verify(jobFacade).checkJobExecutionEnvironment();
		verify(jobFacade).getSegmentContexts();
		verify(jobFacade).misfireIfNecessary(segmentContexts.getSegmentItemParameters().keySet());
		verify(jobFacade, times(2)).registerJobBegin(segmentContexts);
		verify(jobCaller, times(4)).execute();
		verify(jobFacade, times(2)).registerJobCompleted(segmentContexts);
	}

	/**
	 * before listeners 报错不执行
	 * 
	 * @author Rong Hu
	 */
	@Test(expected = JobSystemException.class)
	public void assertBeforeJobExecutedFailure() {
		SegmentContexts segmentContexts = SegmentContextsBuilder.getMultipleSegmentContexts();
		when(jobFacade.getSegmentContexts()).thenReturn(segmentContexts);
		when(jobFacade.misfireIfNecessary(segmentContexts.getSegmentItemParameters().keySet())).thenReturn(false);
		doThrow(RuntimeException.class).when(jobFacade).beforeJobExecuted(segmentContexts);
		try {
			simpleJobExecutor.execute();
		} finally {
			verify(jobCaller, times(0)).execute();
		}
	}

	/**
	 * afterJobExecuted报错
	 * 
	 * @author Rong Hu
	 */

	@Test(expected = JobSystemException.class)
	public void assertAfterJobExecutedFailure() {
		SegmentContexts segmentContexts = SegmentContextsBuilder.getMultipleSegmentContexts();
		when(jobFacade.getSegmentContexts()).thenReturn(segmentContexts);
		when(jobFacade.misfireIfNecessary(segmentContexts.getSegmentItemParameters().keySet())).thenReturn(false);
		when(jobFacade.isExecuteMisfired(segmentContexts.getSegmentItemParameters().keySet())).thenReturn(false);
		doThrow(RuntimeException.class).when(jobFacade).afterJobExecuted(segmentContexts);
		try {
			simpleJobExecutor.execute();
		} finally {
			verify(jobCaller, times(2)).execute();
		}
	}

}
