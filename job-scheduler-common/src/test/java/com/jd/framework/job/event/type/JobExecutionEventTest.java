/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.event.type;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.core.Is.is;

import org.junit.Test;

public class JobExecutionEventTest {

	private final String taskId = "test_task_id";
	private final String jobName = "test_job";
	private final int item = 0;

	@Test
	public void assertNewJobExecutionEvent() {
		JobExecutionEvent actual = new JobExecutionEvent(taskId, jobName,
				JobExecutionEvent.ExecutionSource.NORMAL_TRIGGER, item);
		assertThat(actual.getJobName(), is("test_job"));
		assertThat(actual.getSource(), is(JobExecutionEvent.ExecutionSource.NORMAL_TRIGGER));
		assertThat(actual.getSegmentItem(), is(0));
		assertNotNull(actual.getHostname());
		assertNotNull(actual.getStartTime());
		assertNull(actual.getCompleteTime());
		assertFalse(actual.isSuccess());
		assertThat(actual.getFailureCause(), is(""));
		System.out.println("IP:" + actual.getIp());
		System.out.println("Host:" + actual.getHostname());
	}

	@Test
	public void assertExecutionSuccess() {
		JobExecutionEvent startEvent = new JobExecutionEvent(taskId, jobName,
				JobExecutionEvent.ExecutionSource.NORMAL_TRIGGER, item);
		JobExecutionEvent successEvent = startEvent.executionSuccess();
		assertNotNull(successEvent.getCompleteTime());
		assertTrue(successEvent.isSuccess());
	}

	@Test
	public void assertExecutionFailure() {
		JobExecutionEvent startEvent = new JobExecutionEvent(taskId, jobName,
				JobExecutionEvent.ExecutionSource.NORMAL_TRIGGER, item);
		JobExecutionEvent failureEvent = startEvent.executionFailure(new RuntimeException("failure"));
		assertNotNull(failureEvent.getCompleteTime());
		assertFalse(failureEvent.isSuccess());
		assertThat(failureEvent.getFailureCause(), startsWith("java.lang.RuntimeException: failure"));
	}

}
