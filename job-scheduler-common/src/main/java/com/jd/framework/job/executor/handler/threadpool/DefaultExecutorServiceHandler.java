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

import java.util.concurrent.ExecutorService;

import com.jd.framework.job.utils.threadpool.ThreadPoolWrapper;

/**
 * 默认线程池处理器
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-4
 */
public final class DefaultExecutorServiceHandler implements
		ExecutorServiceHandler {

	/** 线程池大小 */
	private static final int THREAD_SIZE = Runtime.getRuntime()
			.availableProcessors() * 2;

	@Override
	public ExecutorService createExecutorService(final String jobName) {
		return new ThreadPoolWrapper("inner-job-" + jobName, THREAD_SIZE)
				.createExecutorService();
	}

}
