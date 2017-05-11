/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.utils.env;

import static org.junit.Assert.*;

import org.junit.Test;

public class TimeServiceTest {

	private TimeService timeService = new TimeService();

	@Test
	public void assertGetCurrentMillis() throws Exception {
		assertTrue(timeService.getCurrentMillis() <= System.currentTimeMillis());
	}

}
