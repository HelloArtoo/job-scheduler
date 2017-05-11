/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.executor.registry;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.RequiredArgsConstructor;

import org.junit.After;
import org.junit.Test;

import com.jd.framework.job.executor.handler.threadpool.DefaultExecutorServiceHandler;

public class ExecutorServiceHandlerRegistryTest {

	@After
	public void clear() {
		ExecutorServiceHandlerRegistry.remove("test_job");
	}

	@Test
	public void assertRemove() {
		ExecutorService actual = ExecutorServiceHandlerRegistry.getExecutorServiceHandler("test_job",
				new DefaultExecutorServiceHandler());
		ExecutorServiceHandlerRegistry.remove("test_job");
		assertThat(actual, not(ExecutorServiceHandlerRegistry.getExecutorServiceHandler("test_job",
				new DefaultExecutorServiceHandler())));
	}

	@Test
	public void assertGetExecutorServiceHandlerForSameThread() {
		assertThat(ExecutorServiceHandlerRegistry.getExecutorServiceHandler("test_job",
				new DefaultExecutorServiceHandler()), is(ExecutorServiceHandlerRegistry.getExecutorServiceHandler(
				"test_job", new DefaultExecutorServiceHandler())));
	}

	/**
	 * 并发测试
	 * 
	 * @throws InterruptedException
	 * @author Rong Hu
	 */
	@Test
	public void assertGetExecutorServiceHandlerForConcurrent() throws InterruptedException {
		int threadCount = 100;
		CyclicBarrier barrier = new CyclicBarrier(threadCount);
		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		CountDownLatch latch = new CountDownLatch(threadCount);
		Set<ExecutorService> set = new CopyOnWriteArraySet<>();
		for (int i = 0; i < threadCount; i++) {
			executorService.submit(new GetExecutorServiceHandlerTask(barrier, latch, set));
		}
		latch.await();
		assertThat(set.size(), is(1));
		assertThat(ExecutorServiceHandlerRegistry.getExecutorServiceHandler("test_job",
				new DefaultExecutorServiceHandler()), is(set.iterator().next()));
	}

	@RequiredArgsConstructor
	class GetExecutorServiceHandlerTask implements Runnable {

		private final CyclicBarrier barrier;

		private final CountDownLatch latch;

		private final Set<ExecutorService> set;

		@Override
		public void run() {
			try {
				barrier.await();
			} catch (final InterruptedException | BrokenBarrierException ex) {
				ex.printStackTrace();
			}
			set.add(ExecutorServiceHandlerRegistry.getExecutorServiceHandler("test_job",
					new DefaultExecutorServiceHandler()));
			latch.countDown();
		}
	}

}
