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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import com.google.common.base.Joiner;
import com.google.common.util.concurrent.MoreExecutors;

/**
 * 
 * 线程池服务对象包装类
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-4
 */
public class ThreadPoolWrapper {
	private final ThreadPoolExecutor threadPoolExecutor;
	private final BlockingQueue<Runnable> workQueue;

	public ThreadPoolWrapper(final String namingPattern, final int threadSize) {
		workQueue = new LinkedBlockingQueue<>();
		threadPoolExecutor = new ThreadPoolExecutor(threadSize, threadSize, 5L,
				TimeUnit.MINUTES, workQueue,
				new BasicThreadFactory.Builder().namingPattern(
						Joiner.on("-").join(namingPattern, "%s")).build());
		threadPoolExecutor.allowCoreThreadTimeOut(true);
	}

	/**
	 * 创建线程池服务对象.
	 * 
	 * @return 线程池服务对象
	 */
	public ExecutorService createExecutorService() {
		return MoreExecutors.listeningDecorator(MoreExecutors
				.getExitingExecutorService(threadPoolExecutor));
	}

	public boolean isShutdown() {
		return threadPoolExecutor.isShutdown();
	}

	/**
	 * 获取当前活跃的线程数.
	 * 
	 * @return 当前活跃的线程数
	 */
	public int getActiveThreadCount() {
		return threadPoolExecutor.getActiveCount();
	}

	/**
	 * 获取待执行任务数量.
	 * 
	 * @return 待执行任务数量
	 */
	public int getWorkQueueSize() {
		return workQueue.size();
	}
}
