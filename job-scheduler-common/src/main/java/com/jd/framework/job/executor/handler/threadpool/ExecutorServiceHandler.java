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

/**
 * 
 * 线程池服务处理器.
 * <p>
 * 用于作业内部的线程池处理数据使用. 目前仅用于 {@link com.jd.framework.job.constant.job.JobType} Flow类型.
 * </p>
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-4
 */
public interface ExecutorServiceHandler {

	/**
	 * 创建线程池服务对象.
	 * 
	 * @param jobName
	 *            作业名
	 * 
	 * @return 线程池服务对象
	 */
	ExecutorService createExecutorService(final String jobName);
}
