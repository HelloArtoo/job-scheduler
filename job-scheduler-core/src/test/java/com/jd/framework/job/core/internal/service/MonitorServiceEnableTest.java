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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.junit.Test;

import com.jd.framework.job.core.fixture.TestSimpleJob;
import com.jd.framework.job.core.integrate.AbstractBaseStdJobTest;
import com.jd.framework.job.core.internal.utils.SocketUtils;

public class MonitorServiceEnableTest  extends AbstractBaseStdJobTest{
	
	private static final int MONITOR_PORT = 9000;

	public MonitorServiceEnableTest() {
		super(TestSimpleJob.class, MONITOR_PORT);
	}

	@Test
	public void assertMonitorWithCommand() throws IOException {
		initJob();
		assertNotNull(SocketUtils.sendCommand(MonitorService.DUMP_COMMAND, MONITOR_PORT));
		assertNull(SocketUtils.sendCommand("unknown_command", MONITOR_PORT));
	}

}
