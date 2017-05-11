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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class GuaranteeNodeHelperTest {

	private GuaranteeNodeHelper guaranteeNode = new GuaranteeNodeHelper("test_job");

	@Test
	public void assertGetStartedNode() {
		assertThat(GuaranteeNodeHelper.getStartedNode(1), is("guarantee/started/1"));
	}

	@Test
	public void assertGetCompletedNode() {
		assertThat(GuaranteeNodeHelper.getCompletedNode(1), is("guarantee/completed/1"));
	}

	@Test
	public void assertIsStartedRootNode() {
		assertTrue(guaranteeNode.isStartedRootNode("/test_job/guarantee/started"));
	}

	@Test
	public void assertIsNotStartedRootNode() {
		assertFalse(guaranteeNode.isStartedRootNode("/otherJob/guarantee/started"));
	}

	@Test
	public void assertIsCompletedRootNode() {
		assertTrue(guaranteeNode.isCompletedRootNode("/test_job/guarantee/completed"));
	}

	@Test
	public void assertIsNotCompletedRootNode() {
		assertFalse(guaranteeNode.isCompletedRootNode("/otherJob/guarantee/completed"));
	}

}
