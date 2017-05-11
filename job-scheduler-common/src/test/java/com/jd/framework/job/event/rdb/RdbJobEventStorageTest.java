/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.event.rdb;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbcp.BasicDataSource;
import org.junit.Before;
import org.junit.Test;

import com.jd.framework.job.constant.job.ExecutionType;
import com.jd.framework.job.event.type.JobExecutionEvent;
import com.jd.framework.job.event.type.JobExecutionEvent.ExecutionSource;
import com.jd.framework.job.event.type.JobStatusTraceEvent;
import com.jd.framework.job.event.type.JobStatusTraceEvent.Source;
import com.jd.framework.job.event.type.JobStatusTraceEvent.State;

public class RdbJobEventStorageTest {

	private RdbJobEventStorage storage;

	@Before
	public void setup() throws SQLException {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(org.h2.Driver.class.getName());
		dataSource.setUrl("jdbc:h2:mem:job_event_storage");
		dataSource.setUsername("sa");
		dataSource.setPassword("");
		storage = new RdbJobEventStorage(dataSource);
	}

	@Test
	public void assertAddJobExecutionEvent() throws SQLException {
		assertTrue(storage.addJobExecutionEvent(new JobExecutionEvent("fake_task_id", "test_job",
				ExecutionSource.NORMAL_TRIGGER, 0)));
	}

	@Test
	public void assertAddJobStatusTraceEvent() throws SQLException {
		assertTrue(storage.addJobStatusTraceEvent(new JobStatusTraceEvent("test_job", "fake_task_id", "fake_slave_id",
				Source.FACT_EXECUTOR, ExecutionType.READY, "0", State.TASK_RUNNING, "message is empty.")));
	}

	@Test
	public void assertAddJobStatusTraceEventWhenFailoverWithTaskStagingState() throws SQLException {
		JobStatusTraceEvent jobStatusTraceEvent = new JobStatusTraceEvent("test_job", "fake_failover_task_id",
				"fake_slave_id", Source.FACT_EXECUTOR, ExecutionType.FAILOVER, "0", State.TASK_STAGING,
				"message is empty.");
		jobStatusTraceEvent.setOriginalTaskId("original_fake_failover_task_id");
		assertThat(storage.getJobStatusTraceEvents("fake_failover_task_id").size(), is(0));
		storage.addJobStatusTraceEvent(jobStatusTraceEvent);
		assertThat(storage.getJobStatusTraceEvents("fake_failover_task_id").size(), is(1));
	}

	@Test
	public void assertAddJobStatusTraceEventWhenFailoverWithTaskFailedState() throws SQLException {
		JobStatusTraceEvent stagingJobStatusTraceEvent = new JobStatusTraceEvent("test_job",
				"fake_failed_failover_task_id", "fake_slave_id", Source.FACT_EXECUTOR, ExecutionType.FAILOVER, "0",
				State.TASK_STAGING, "message is empty.");
		stagingJobStatusTraceEvent.setOriginalTaskId("original_fake_failed_failover_task_id");
		storage.addJobStatusTraceEvent(stagingJobStatusTraceEvent);
		JobStatusTraceEvent failedJobStatusTraceEvent = new JobStatusTraceEvent("test_job",
				"fake_failed_failover_task_id", "fake_slave_id", Source.FACT_EXECUTOR, ExecutionType.FAILOVER, "0",
				State.TASK_FAILED, "message is empty.");
		storage.addJobStatusTraceEvent(failedJobStatusTraceEvent);
		List<JobStatusTraceEvent> jobStatusTraceEvents = storage
				.getJobStatusTraceEvents("fake_failed_failover_task_id");
		assertThat(jobStatusTraceEvents.size(), is(2));
		for (JobStatusTraceEvent jobStatusTraceEvent : jobStatusTraceEvents) {
			assertThat(jobStatusTraceEvent.getOriginalTaskId(), is("original_fake_failed_failover_task_id"));
		}
	}

	@Test
	public void assertUpdateJobExecutionEventWhenSuccess() throws SQLException {
		JobExecutionEvent startEvent = new JobExecutionEvent("fake_task_id", "test_job",
				ExecutionSource.NORMAL_TRIGGER, 0);
		assertTrue(storage.addJobExecutionEvent(startEvent));
		JobExecutionEvent successEvent = startEvent.executionSuccess();
		assertTrue(storage.addJobExecutionEvent(successEvent));
	}

	@Test
	public void assertUpdateJobExecutionEventWhenFailure() throws SQLException {
		JobExecutionEvent startEvent = new JobExecutionEvent("fake_task_id", "test_job",
				ExecutionSource.NORMAL_TRIGGER, 0);
		assertTrue(storage.addJobExecutionEvent(startEvent));
		JobExecutionEvent failureEvent = startEvent.executionFailure(new RuntimeException("failure"));
		assertTrue(storage.addJobExecutionEvent(failureEvent));
		assertThat(failureEvent.getFailureCause(), startsWith("java.lang.RuntimeException: failure"));
	}

	@Test
	public void assertUpdateJobExecutionEventWhenSuccessAndConflict() throws SQLException {
		JobExecutionEvent startEvent = new JobExecutionEvent("fake_task_id", "test_job",
				ExecutionSource.NORMAL_TRIGGER, 0);
		JobExecutionEvent successEvent = startEvent.executionSuccess();
		assertTrue(storage.addJobExecutionEvent(successEvent));
		assertFalse(storage.addJobExecutionEvent(startEvent));
	}

	@Test
	public void assertUpdateJobExecutionEventWhenFailureAndConflict() throws SQLException {
		JobExecutionEvent startEvent = new JobExecutionEvent("fake_task_id", "test_job",
				ExecutionSource.NORMAL_TRIGGER, 0);
		JobExecutionEvent failureEvent = startEvent.executionFailure(new RuntimeException("failure"));
		assertTrue(storage.addJobExecutionEvent(failureEvent));
		assertThat(failureEvent.getFailureCause(), startsWith("java.lang.RuntimeException: failure"));
		assertFalse(storage.addJobExecutionEvent(startEvent));
	}

	@Test
	public void assertUpdateJobExecutionEventWhenFailureAndMessageExceed() throws SQLException {
		JobExecutionEvent startEvent = new JobExecutionEvent("fake_task_id", "test_job",
				ExecutionSource.NORMAL_TRIGGER, 0);
		assertTrue(storage.addJobExecutionEvent(startEvent));
		StringBuilder failureMsg = new StringBuilder();
		for (int i = 0; i < 600; i++) {
			failureMsg.append(i);
		}
		JobExecutionEvent failEvent = startEvent.executionFailure(new RuntimeException("failure"
				+ failureMsg.toString()));
		assertTrue(storage.addJobExecutionEvent(failEvent));
		assertThat(failEvent.getFailureCause(), startsWith("java.lang.RuntimeException: failure"));
	}

	@Test
	public void assertFindJobExecutionEvent() throws SQLException {
		storage.addJobExecutionEvent(new JobExecutionEvent("fake_task_id", "test_job", ExecutionSource.NORMAL_TRIGGER,
				0));
	}
}
