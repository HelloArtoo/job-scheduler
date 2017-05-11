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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ElectionNodeHelperTest {

	private ElectionNodeHelper electionNode = new ElectionNodeHelper("test_job");

	//TRUE
	@Test
	public void assertIsSegmentTotalCountPath() {
		assertTrue(electionNode.isLeaderHostPath("/test_job/leader/election/host"));
	}

	//FALSE
	@Test
	public void assertIsNotSegmentTotalCountPath() {
		assertFalse(electionNode.isLeaderHostPath("/test_job/leader/election/host1"));
	}

}
