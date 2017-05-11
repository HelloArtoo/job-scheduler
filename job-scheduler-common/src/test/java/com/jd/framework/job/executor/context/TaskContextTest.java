/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.executor.context;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertThat;

import org.hamcrest.core.Is;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.jd.framework.job.constant.job.ExecutionType;
import com.jd.framework.job.fixture.context.TaskNode;

public class TaskContextTest {

	@Test
	public void assertNew() {
		TaskContext actual = new TaskContext("test_job", Lists.newArrayList(0), ExecutionType.READY, "slave-S0");
		assertThat(actual.getMetaInfo().getJobName(), is("test_job"));
		assertThat(actual.getMetaInfo().getSegmentItems().get(0), is(0));
		assertThat(actual.getType(), is(ExecutionType.READY));
		assertThat(actual.getSlaveId(), is("slave-S0"));
		assertThat(
				actual.getId(),
				startsWith(TaskNode.builder().build().getTaskNodeValue()
						.substring(0, TaskNode.builder().build().getTaskNodeValue().length() - 1)));
	}

	@Test
	public void assertNewWithoutSlaveId() {
		TaskContext actual = new TaskContext("test_job", Lists.newArrayList(0), ExecutionType.READY);
		assertThat(actual.getSlaveId(), is("unassigned-slave"));
	}

	@Test
	public void assertGetMetaInfo() {
		TaskContext actual = new TaskContext("test_job", Lists.newArrayList(0), ExecutionType.READY, "slave-S0");
		assertThat(actual.getMetaInfo().toString(), is("test_job@-@0"));
	}

	@Test
	public void assertTaskContextFrom() {
		TaskContext actual = TaskContext.from(TaskNode.builder().build().getTaskNodeValue());
		assertThat(actual.getId(), Is.is(TaskNode.builder().build().getTaskNodeValue()));
		assertThat(actual.getMetaInfo().getJobName(), is("test_job"));
		assertThat(actual.getMetaInfo().getSegmentItems().get(0), is(0));
		assertThat(actual.getType(), is(ExecutionType.READY));
		assertThat(actual.getSlaveId(), is("slave-S0"));
	}

	@Test
	public void assertMetaInfoFromWithMetaInfo() {
		TaskContext.MetaInfo actual = TaskContext.MetaInfo.from("test_job@-@1");
		assertThat(actual.getJobName(), is("test_job"));
		assertThat(actual.getSegmentItems().get(0), is(1));
	}

	@Test
	public void assertMetaInfoFromWithTaskId() {
		TaskContext.MetaInfo actual = TaskContext.MetaInfo.from("test_job@-@1@-@READY@-@unassigned-slave@-@0");
		assertThat(actual.getJobName(), is("test_job"));
		assertThat(actual.getSegmentItems().get(0), is(1));
	}

	@Test
	public void assertMetaInfoFromWithMetaInfoWithoutSegmentItems() {
		TaskContext.MetaInfo actual = TaskContext.MetaInfo.from("test_job@-@");
		assertThat(actual.getJobName(), is("test_job"));
		assertTrue(actual.getSegmentItems().isEmpty());
	}

	@Test
	public void assertMetaInfoFromWithTaskIdWithoutSegmentItems() {
		TaskContext.MetaInfo actual = TaskContext.MetaInfo.from("test_job@-@@-@READY@-@unassigned-slave@-@0");
		assertThat(actual.getJobName(), is("test_job"));
		assertTrue(actual.getSegmentItems().isEmpty());
	}

	@Test
	public void assertGetIdForUnassignedSlave() {
		assertThat(TaskContext.getIdForUnassignedSlave("test_job@-@0@-@READY@-@slave-S0@-@0"),
				is("test_job@-@0@-@READY@-@unassigned-slave@-@0"));
	}

	@Test
	public void assertGetTaskName() {
		TaskContext actual = TaskContext.from(TaskNode.builder().build().getTaskNodeValue());
		assertThat(actual.getTaskName(), is("test_job@-@0@-@READY@-@slave-S0"));
	}

	@Test
	public void assertGetExecutorId() {
		TaskContext actual = TaskContext.from(TaskNode.builder().build().getTaskNodeValue());
		assertThat(actual.getExecutorId("app"), is("app@-@slave-S0"));
	}

	@Test
	public void assertSetSlaveId() {
		TaskContext actual = new TaskContext("test_job", Lists.newArrayList(0), ExecutionType.READY, "slave-S0");
		assertThat(actual.getSlaveId(), is("slave-S0"));
		actual.setSlaveId("slave-S1");
		assertThat(actual.getSlaveId(), is("slave-S1"));
	}

	@Test
	public void assertSetIdle() {
		TaskContext actual = new TaskContext("test_job", Lists.newArrayList(0), ExecutionType.READY, "slave-S0");
		assertFalse(actual.isIdle());
		actual.setIdle(true);
		assertTrue(actual.isIdle());
	}

}
