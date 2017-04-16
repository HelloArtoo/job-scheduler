/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.exception.handler;

import org.apache.zookeeper.KeeperException.ConnectionLossException;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.junit.Ignore;
import org.junit.Test;

import com.jd.framework.job.exception.RegException;

public class RegExceptionHandlerTest {

	@Test
	@Ignore
	// TODO throw InterruptedException will cause zookeeper TestingServer break.
	// Ignore first, fix it later.
	public void assertHandleExceptionWithInterruptedException() {
		RegExceptionHandler.handleException(new InterruptedException());
	}

	@Test(expected = RegException.class)
	public void assertHandleExceptionWithOtherException() {
		RegExceptionHandler.handleException(new RuntimeException());
	}

	@Test
	public void assertHandleExceptionWithConnectionLossException() {
		RegExceptionHandler.handleException(new ConnectionLossException());
	}

	@Test
	public void assertHandleExceptionWithNoNodeException() {
		RegExceptionHandler.handleException(new NoNodeException());
	}

	@Test
	public void assertHandleExceptionWithNoNodeExistsException() {
		RegExceptionHandler.handleException(new NodeExistsException());
	}

	@Test
	public void assertHandleExceptionWithCausedConnectionLossException() {
		RegExceptionHandler.handleException(new RuntimeException(new ConnectionLossException()));
	}

	@Test
	public void assertHandleExceptionWithCausedNoNodeException() {
		RegExceptionHandler.handleException(new RuntimeException(new NoNodeException()));
	}

	@Test
	public void assertHandleExceptionWithCausedNoNodeExistsException() {
		RegExceptionHandler.handleException(new RuntimeException(new NodeExistsException()));
	}

}
