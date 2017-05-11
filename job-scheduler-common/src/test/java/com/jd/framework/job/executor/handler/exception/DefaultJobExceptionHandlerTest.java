/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.executor.handler.exception;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.jd.framework.job.event.fixture.JobEventCaller;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DefaultJobExceptionHandlerTest {

	@Mock
	private JobEventCaller caller;

	@Test 
	public void assertHandleException() {
		new DefaultJobExceptionHandler().handleException("test_job", new RuntimeException("test"));
		verify(caller, atMost(1)).call();
	}
}
