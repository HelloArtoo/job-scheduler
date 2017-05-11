/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.executor.handler.threadpool;

import static org.junit.Assert.*;

import java.util.concurrent.ExecutorService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.jd.framework.job.utils.threadpool.ThreadPoolWrapper;
import static org.mockito.Mockito.when;
import static org.hamcrest.CoreMatchers.instanceOf;

@RunWith(MockitoJUnitRunner.class)
public class DefaultExecutorServiceHandlerTest {

	@Mock
	private ExecutorServiceHandler defaultExecutorServiceHandler;

	private final String jobName = "testJob";
	private final int threadSize = Runtime.getRuntime().availableProcessors() * 2;

	@Test
	public void test() {
		ThreadPoolWrapper wrapper = new ThreadPoolWrapper("scheduler-inner-job-" + jobName, threadSize);
		when(defaultExecutorServiceHandler.createExecutorService(jobName)).thenReturn(wrapper.createExecutorService());
		assertThat(defaultExecutorServiceHandler.createExecutorService(jobName), instanceOf(ExecutorService.class));
	}
}
