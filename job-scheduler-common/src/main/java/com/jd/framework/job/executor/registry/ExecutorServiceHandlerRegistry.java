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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import com.jd.framework.job.executor.handler.threadpool.ExecutorServiceHandler;

/**
 * 
 * 线程池服务器处理器注册表
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-15
 */
public class ExecutorServiceHandlerRegistry {

	/** 注册表 */
	private static final Map<String, ExecutorService> REGISTRY = new HashMap<>();

	/**
	 * 获取线程池服务.
	 * 
	 * @param jobName
	 *            作业名称
	 * @param executorServiceHandler
	 *            线程池服务处理器
	 * @return 线程池服务
	 */
	public static synchronized ExecutorService getExecutorServiceHandler(final String jobName,
			final ExecutorServiceHandler executorServiceHandler) {
		if (!REGISTRY.containsKey(jobName)) {
			REGISTRY.put(jobName, executorServiceHandler.createExecutorService(jobName));
		}
		return REGISTRY.get(jobName);
	}

	/**
	 * 从注册表中删除该作业线程池服务.
	 * 
	 * @param jobName
	 *            作业名称
	 */
	public static synchronized void remove(final String jobName) {
		REGISTRY.remove(jobName);
	}
}
