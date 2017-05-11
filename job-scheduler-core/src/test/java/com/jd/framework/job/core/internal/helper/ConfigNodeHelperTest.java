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

import static org.junit.Assert.*;

import org.junit.Test;

public class ConfigNodeHelperTest {

	private ConfigNodeHelper configNode = new ConfigNodeHelper("test_job");

	@Test
	public void assertIsConfigPath() {
		assertTrue(configNode.isConfigPath("/test_job/config"));
	}

}
