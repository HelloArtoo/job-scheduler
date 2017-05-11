/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.core.internal.helper;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ExecutionNodeHelperTest {

	private ExecutionNodeHelper executionNode = new ExecutionNodeHelper("test_job");

	@Test
	public void assertGetRunningNode() {
		assertThat(ExecutionNodeHelper.getRunningNode(0), is("execution/0/running"));
	}

	@Test
	public void assertGetCompletedNode() {
		assertThat(ExecutionNodeHelper.getCompletedNode(0), is("execution/0/completed"));
	}

	@Test
	public void assertGetLastBeginTimeNode() {
		assertThat(ExecutionNodeHelper.getLastBeginTimeNode(0), is("execution/0/lastBeginTime"));
	}

	@Test
	public void assertGetNextFireTimeNode() {
		assertThat(ExecutionNodeHelper.getNextFireTimeNode(0), is("execution/0/nextFireTime"));
	}

	@Test
	public void assertGetLastCompleteTimeNode() {
		assertThat(ExecutionNodeHelper.getLastCompleteTimeNode(0), is("execution/0/lastCompleteTime"));
	}

	@Test
	public void assertGetMisfireNode() {
		assertThat(ExecutionNodeHelper.getMisfireNode(0), is("execution/0/misfire"));
	}

	@Test
	public void assertGetItemWhenNotRunningItemPath() {
		assertNull(executionNode.getItemByRunningItemPath("/test_job/execution/0/completed"));
	}

	@Test
	public void assertGetItemByRunningItemPath() {
		assertThat(executionNode.getItemByRunningItemPath("/test_job/execution/0/running"), is(0));
	}

}
