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

public class FailoverNodeHelperTest {

	private FailoverNodeHelper failoverNode = new FailoverNodeHelper("test_job");

	@Test
	public void assertGetItemsNode() {
		assertThat(FailoverNodeHelper.getItemsNode(0), is("leader/failover/items/0"));
	}

	@Test
	public void assertGetExecutionFailoverNode() {
		assertThat(FailoverNodeHelper.getExecutionFailoverNode(0), is("execution/0/failover"));
	}

	@Test
	public void assertGetItemWhenNotExecutionFailoverPath() {
		assertNull(failoverNode.getItemByExecutionFailoverPath("/test_job/execution/0/completed"));
	}

	@Test
	public void assertGetItemByExecutionFailoverPath() {
		assertThat(failoverNode.getItemByExecutionFailoverPath("/test_job/execution/0/failover"), is(0));
	}

}
