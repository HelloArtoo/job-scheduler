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

import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.unitils.util.ReflectionUtils;

import com.jd.framework.job.constant.job.ExecutionType;
import com.jd.framework.job.event.JobEventBus;
import com.jd.framework.job.event.type.JobExecutionEvent;
import com.jd.framework.job.event.type.JobStatusTraceEvent;
import com.jd.framework.job.event.type.JobStatusTraceEvent.Source;
import com.jd.framework.job.event.type.JobStatusTraceEvent.State;
import com.jd.framework.job.exception.JobEventListenerConfigException;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RdbJobEventListenerTest {

	private static final String JOB_NAME = "test_rdb_event_listener";

	@Mock
	private RdbJobEventConfiguration rdbJobEventConfiguration;

	@Mock
	private RdbJobEventStorage repository;

	private JobEventBus jobEventBus;

	@Before
	public void setUp() throws JobEventListenerConfigException, SQLException, NoSuchFieldException {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(org.h2.Driver.class.getName());
		dataSource.setUrl("jdbc:h2:mem:job_event_storage");
		dataSource.setUsername("sa");
		dataSource.setPassword("");
		RdbJobEventListener jobEventRdbListener = new RdbJobEventListener(dataSource);
		ReflectionUtils.setFieldValue(jobEventRdbListener, "repository", repository);
		when(rdbJobEventConfiguration.createJobEventListener()).thenReturn(jobEventRdbListener);
		jobEventBus = new JobEventBus(rdbJobEventConfiguration);
	}

	@Test
	public void assertPostJobExecutionEvent() {
		JobExecutionEvent jobExecutionEvent = new JobExecutionEvent("test_task_id", JOB_NAME,
				JobExecutionEvent.ExecutionSource.NORMAL_TRIGGER, 0);
		jobEventBus.post(jobExecutionEvent);
		verify(repository, atMost(1)).addJobExecutionEvent(jobExecutionEvent);
	}

	@Test
	public void assertPostJobStatusTraceEvent() {
		JobStatusTraceEvent jobStatusTraceEvent = new JobStatusTraceEvent(JOB_NAME, "test_task_id", "fake_slave_id",
				Source.LITE_EXECUTOR, ExecutionType.READY, "0", State.TASK_RUNNING, "message is empty.");
		jobEventBus.post(jobStatusTraceEvent);
		verify(repository, atMost(1)).addJobStatusTraceEvent(jobStatusTraceEvent);
	}
}
