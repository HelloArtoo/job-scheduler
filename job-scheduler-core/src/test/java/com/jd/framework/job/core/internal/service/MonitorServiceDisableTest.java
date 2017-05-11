/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.core.internal.service;

import java.io.IOException;

import org.junit.Test;

import com.jd.framework.job.core.fixture.TestSimpleJob;
import com.jd.framework.job.core.integrate.AbstractBaseStdJobTest;
import com.jd.framework.job.core.internal.utils.SocketUtils;

public class MonitorServiceDisableTest extends AbstractBaseStdJobTest {

	public MonitorServiceDisableTest() {
		super(TestSimpleJob.class, -1);
	}

	@Test(expected = IOException.class)
	public void assertMonitorWithDumpCommand() throws IOException {
		SocketUtils.sendCommand(MonitorService.DUMP_COMMAND, 9000);
	}
}
