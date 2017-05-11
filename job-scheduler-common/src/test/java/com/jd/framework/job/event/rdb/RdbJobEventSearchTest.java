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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.dbcp.BasicDataSource;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jd.framework.job.constant.job.ExecutionType;
import com.jd.framework.job.event.rdb.RdbJobEventSearch.Condition;
import com.jd.framework.job.event.rdb.RdbJobEventSearch.Result;
import com.jd.framework.job.event.type.JobExecutionEvent;
import com.jd.framework.job.event.type.JobExecutionEvent.ExecutionSource;
import com.jd.framework.job.event.type.JobStatusTraceEvent;
import com.jd.framework.job.event.type.JobStatusTraceEvent.Source;
import com.jd.framework.job.event.type.JobStatusTraceEvent.State;

public class RdbJobEventSearchTest {

	private static RdbJobEventStorage storage;

	private static RdbJobEventSearch repository;

	@BeforeClass
	public static void setUpClass() throws SQLException {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(org.h2.Driver.class.getName());
		dataSource.setUrl("jdbc:h2:mem:");
		dataSource.setUsername("sa");
		dataSource.setPassword("");
		storage = new RdbJobEventStorage(dataSource);
		repository = new RdbJobEventSearch(dataSource);
		initStorage();
	}

	private static void initStorage() {
		for (int i = 1; i <= 500; i++) {
			JobExecutionEvent startEvent = new JobExecutionEvent("fake_task_id", "test_job_" + i,
					ExecutionSource.NORMAL_TRIGGER, 0);
			storage.addJobExecutionEvent(startEvent);
			if (i % 2 == 0) {
				JobExecutionEvent successEvent = startEvent.executionSuccess();
				storage.addJobExecutionEvent(successEvent);
			}
			storage.addJobStatusTraceEvent(new JobStatusTraceEvent("test_job_" + i, "fake_failed_failover_task_id",
					"fake_slave_id", Source.FACT_EXECUTOR, ExecutionType.FAILOVER, "0", State.TASK_FAILED,
					"message is empty."));
		}
	}

	/**
	 * 按PageSize Page查询
	 * 
	 * @author Rong Hu
	 */
	@Test
	public void assertFindJobExecutionEventsWithPageSizeAndNumber() {
		Result<JobExecutionEvent> result = repository.findJobExecutionEvents(new Condition(10, 1, null, null, null,
				null, null));
		assertThat(result.getTotal(), is(500));
		assertThat(result.getRows().size(), is(10));
		result = repository.findJobExecutionEvents(new Condition(50, 1, null, null, null, null, null));
		assertThat(result.getTotal(), is(500));
		assertThat(result.getRows().size(), is(50));
		result = repository.findJobExecutionEvents(new Condition(100, 5, null, null, null, null, null));
		assertThat(result.getTotal(), is(500));
		assertThat(result.getRows().size(), is(100));
		result = repository.findJobExecutionEvents(new Condition(100, 6, null, null, null, null, null));
		assertThat(result.getTotal(), is(500));
		assertThat(result.getRows().size(), is(0));
	}

	/**
	 * 异常PageSize和PageNum走默认值
	 * 
	 * @author Rong Hu
	 */
	@Test
	public void assertFindJobExecutionEventsWithErrorPageSizeAndNumber() {
		Result<JobExecutionEvent> result = repository.findJobExecutionEvents(new Condition(-1, -1, null, null, null,
				null, null));
		assertThat(result.getTotal(), is(500));
		assertThat(result.getRows().size(), is(10));
	}

	/**
	 * 排序
	 * 
	 * @author Rong Hu
	 */
	@Test
	public void assertFindJobExecutionEventsWithSort() {
		Result<JobExecutionEvent> result = repository.findJobExecutionEvents(new Condition(10, 1, "jobName", "ASC",
				null, null, null));
		assertThat(result.getTotal(), is(500));
		assertThat(result.getRows().size(), is(10));
		assertThat(result.getRows().get(0).getJobName(), is("test_job_1"));
		result = repository.findJobExecutionEvents(new Condition(10, 1, "jobName", "DESC", null, null, null));
		assertThat(result.getTotal(), is(500));
		assertThat(result.getRows().size(), is(10));
		assertThat(result.getRows().get(0).getJobName(), is("test_job_99"));
	}

	/**
	 * 错误sort
	 * 
	 * @author Rong Hu
	 */
	@Test
	public void assertFindJobExecutionEventsWithErrorSort() {
		Result<JobExecutionEvent> result = repository.findJobExecutionEvents(new Condition(10, 1, "jobName",
				"ERROR_SORT", null, null, null));
		assertThat(result.getTotal(), is(500));
		assertThat(result.getRows().size(), is(10));
		assertThat(result.getRows().get(0).getJobName(), is("test_job_1"));
		result = repository.findJobExecutionEvents(new Condition(10, 1, "notExistField", "ASC", null, null, null));
		assertThat(result.getTotal(), is(500));
		assertThat(result.getRows().size(), is(10));
	}

	/**
	 * 按时间查询
	 * 
	 * @author Rong Hu
	 */
	@Test
	public void assertFindJobExecutionEventsWithTime() {
		Date now = new Date();
		Date tenMinutesBefore = new Date(now.getTime() - 10 * 60 * 1000);
		// 十分钟前
		Result<JobExecutionEvent> result = repository.findJobExecutionEvents(new Condition(10, 1, null, null,
				tenMinutesBefore, null, null));
		assertThat(result.getTotal(), is(500));
		assertThat(result.getRows().size(), is(10));
		result = repository.findJobExecutionEvents(new Condition(10, 1, null, null, now, null, null));
		assertThat(result.getTotal(), is(0));
		assertThat(result.getRows().size(), is(0));
		result = repository.findJobExecutionEvents(new Condition(10, 1, null, null, null, tenMinutesBefore, null));
		assertThat(result.getTotal(), is(0));
		assertThat(result.getRows().size(), is(0));
		result = repository.findJobExecutionEvents(new Condition(10, 1, null, null, null, now, null));
		assertThat(result.getTotal(), is(500));
		assertThat(result.getRows().size(), is(10));
		result = repository.findJobExecutionEvents(new Condition(10, 1, null, null, tenMinutesBefore, now, null));
		assertThat(result.getTotal(), is(500));
		assertThat(result.getRows().size(), is(10));
	}

	/**
	 * where isSuccess = 1
	 * 
	 * @author Rong Hu
	 */
	@Test
	public void assertFindJobExecutionEventsWithFields() {
		Map<String, Object> fields = new HashMap<>();
		fields.put("isSuccess", "1");
		Result<JobExecutionEvent> result = repository.findJobExecutionEvents(new Condition(10, 1, null, null, null,
				null, fields));
		assertThat(result.getTotal(), is(250));
		assertThat(result.getRows().size(), is(10));
		fields.put("isSuccess", null);
		fields.put("jobName", "test_job_1");
		result = repository.findJobExecutionEvents(new Condition(10, 1, null, null, null, null, fields));
		assertThat(result.getTotal(), is(1));
		assertThat(result.getRows().size(), is(1));
	}

	/**
	 * 不存在的字段，不影响数据
	 * 
	 * @author Rong Hu
	 */
	@Test
	public void assertFindJobExecutionEventsWithErrorFields() {
		Map<String, Object> fields = new HashMap<>();
		fields.put("notExistField", "some value");
		Result<JobExecutionEvent> result = repository.findJobExecutionEvents(new Condition(10, 1, null, null, null,
				null, fields));
		assertThat(result.getTotal(), is(500));
		assertThat(result.getRows().size(), is(10));
	}

	/**
	 * JobStatusTraceEvents查找
	 * 
	 * @author Rong Hu
	 */
	@Test
	public void assertFindJobStatusTraceEventsWithPageSizeAndNumber() {
		Result<JobStatusTraceEvent> result = repository.findJobStatusTraceEvents(new Condition(10, 1, null, null, null,
				null, null));
		assertThat(result.getTotal(), is(500));
		assertThat(result.getRows().size(), is(10));
		result = repository.findJobStatusTraceEvents(new Condition(50, 1, null, null, null, null, null));
		assertThat(result.getTotal(), is(500));
		assertThat(result.getRows().size(), is(50));
		result = repository.findJobStatusTraceEvents(new Condition(100, 5, null, null, null, null, null));
		assertThat(result.getTotal(), is(500));
		assertThat(result.getRows().size(), is(100));
		result = repository.findJobStatusTraceEvents(new Condition(100, 6, null, null, null, null, null));
		assertThat(result.getTotal(), is(500));
		assertThat(result.getRows().size(), is(0));
	}

	/**
	 * -1
	 * 
	 * @author Rong Hu
	 */
	@Test
	public void assertFindJobStatusTraceEventsWithErrorPageSizeAndNumber() {
		Result<JobStatusTraceEvent> result = repository.findJobStatusTraceEvents(new Condition(-1, -1, null, null,
				null, null, null));
		assertThat(result.getTotal(), is(500));
		assertThat(result.getRows().size(), is(10));
	}

	/**
	 * 排序
	 * 
	 * @author Rong Hu
	 */
	@Test
	public void assertFindJobStatusTraceEventsWithSort() {
		Result<JobStatusTraceEvent> result = repository.findJobStatusTraceEvents(new Condition(10, 1, "jobName", "ASC",
				null, null, null));
		assertThat(result.getTotal(), is(500));
		assertThat(result.getRows().size(), is(10));
		assertThat(result.getRows().get(0).getJobName(), is("test_job_1"));
		result = repository.findJobStatusTraceEvents(new Condition(10, 1, "jobName", "DESC", null, null, null));
		assertThat(result.getTotal(), is(500));
		assertThat(result.getRows().size(), is(10));
		assertThat(result.getRows().get(0).getJobName(), is("test_job_99"));
	}

	/**
	 * 错误排序
	 * 
	 * @author Rong Hu
	 */
	@Test
	public void assertFindJobStatusTraceEventsWithErrorSort() {
		Result<JobStatusTraceEvent> result = repository.findJobStatusTraceEvents(new Condition(10, 1, "jobName",
				"ERROR_SORT", null, null, null));
		assertThat(result.getTotal(), is(500));
		assertThat(result.getRows().size(), is(10));
		assertThat(result.getRows().get(0).getJobName(), is("test_job_1"));
		result = repository.findJobStatusTraceEvents(new Condition(10, 1, "notExistField", "ASC", null, null, null));
		assertThat(result.getTotal(), is(500));
		assertThat(result.getRows().size(), is(10));
	}

	/**
	 * 按时间查询
	 * 
	 * @author Rong Hu
	 */
	@Test
	public void assertFindJobStatusTraceEventsWithTime() {
		Date now = new Date();
		Date tenMinutesBefore = new Date(now.getTime() - 10 * 60 * 1000);
		Result<JobStatusTraceEvent> result = repository.findJobStatusTraceEvents(new Condition(10, 1, null, null,
				tenMinutesBefore, null, null));
		assertThat(result.getTotal(), is(500));
		assertThat(result.getRows().size(), is(10));
		result = repository.findJobStatusTraceEvents(new Condition(10, 1, null, null, now, null, null));
		assertThat(result.getTotal(), is(0));
		assertThat(result.getRows().size(), is(0));
		result = repository.findJobStatusTraceEvents(new Condition(10, 1, null, null, null, tenMinutesBefore, null));
		assertThat(result.getTotal(), is(0));
		assertThat(result.getRows().size(), is(0));
		result = repository.findJobStatusTraceEvents(new Condition(10, 1, null, null, null, now, null));
		assertThat(result.getTotal(), is(500));
		assertThat(result.getRows().size(), is(10));
		result = repository.findJobStatusTraceEvents(new Condition(10, 1, null, null, tenMinutesBefore, now, null));
		assertThat(result.getTotal(), is(500));
		assertThat(result.getRows().size(), is(10));
	}

	/**
	 * where jobName = 'test_job_1'
	 * 
	 * @author Rong Hu
	 */
	@Test
	public void assertFindJobStatusTraceEventsWithFields() {
		Map<String, Object> fields = new HashMap<>();
		fields.put("jobName", "test_job_1");
		Result<JobStatusTraceEvent> result = repository.findJobStatusTraceEvents(new Condition(10, 1, null, null, null,
				null, fields));
		assertThat(result.getTotal(), is(1));
		assertThat(result.getRows().size(), is(1));
	}

	/**
	 * 不存在的列，返回全部
	 * 
	 * @author Rong Hu
	 */
	@Test
	public void assertFindJobStatusTraceEventsWithErrorFields() {
		Map<String, Object> fields = new HashMap<>();
		fields.put("notExistField", "some value");
		Result<JobStatusTraceEvent> result = repository.findJobStatusTraceEvents(new Condition(10, 1, null, null, null,
				null, fields));
		assertThat(result.getTotal(), is(500));
		assertThat(result.getRows().size(), is(10));
	}
}
