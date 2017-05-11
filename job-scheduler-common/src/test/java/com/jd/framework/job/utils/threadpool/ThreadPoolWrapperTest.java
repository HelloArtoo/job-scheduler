/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.utils.threadpool;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import java.util.concurrent.ExecutorService;

import org.junit.Test;

import com.jd.framework.job.utils.concurrent.BlockUtils;

public class ThreadPoolWrapperTest {

	private ThreadPoolWrapper threadPoolWrapper;

	@Test
	public void assertCreateExecutorService() {
		threadPoolWrapper = new ThreadPoolWrapper("thread-pool-executor-test", 1);
		assertThat(threadPoolWrapper.getActiveThreadCount(), is(0));
		assertThat(threadPoolWrapper.getWorkQueueSize(), is(0));
		assertFalse(threadPoolWrapper.isShutdown());
		ExecutorService executorService = threadPoolWrapper.createExecutorService();
		executorService.submit(new FooTask());
		BlockUtils.waitingShortTime();
		assertThat(threadPoolWrapper.getActiveThreadCount(), is(1));
		assertThat(threadPoolWrapper.getWorkQueueSize(), is(0));
		assertFalse(threadPoolWrapper.isShutdown());
		executorService.submit(new FooTask());
		BlockUtils.waitingShortTime();
		assertThat(threadPoolWrapper.getActiveThreadCount(), is(1));
		assertThat(threadPoolWrapper.getWorkQueueSize(), is(1));
		assertFalse(threadPoolWrapper.isShutdown());
		executorService.shutdownNow();
		assertThat(threadPoolWrapper.getWorkQueueSize(), is(0));
		assertTrue(threadPoolWrapper.isShutdown());
	}

	class FooTask implements Runnable {

		@Override
		public void run() {
			BlockUtils.sleep(1000L);
		}
	}

}
