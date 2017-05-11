/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.spring.fixture.handler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.jd.framework.job.executor.handler.threadpool.ExecutorServiceHandler;

public class TestSimpleExecutorServiceHandler implements ExecutorServiceHandler {

	@Override
	public ExecutorService createExecutorService(String jobName) {
		return Executors.newFixedThreadPool(1);
	}

}
