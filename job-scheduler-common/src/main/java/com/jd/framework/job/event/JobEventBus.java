/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
/**
 * 
 */
package com.jd.framework.job.event;

import lombok.extern.slf4j.Slf4j;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.jd.framework.job.exception.JobEventListenerConfigException;
import com.jd.framework.job.utils.threadpool.ThreadPoolWrapper;

/**
 * 任务运行事件总线
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */
@Slf4j
public class JobEventBus {

	private final JobEventConfiguration jobEventConfig;

	private final ThreadPoolWrapper threadPoolWrapper;

	private final EventBus eventBus;

	private boolean isRegistered;

	public JobEventBus() {
		jobEventConfig = null;
		threadPoolWrapper = null;
		eventBus = null;
	}

	public JobEventBus(final JobEventConfiguration jobEventConfig) {
		this.jobEventConfig = jobEventConfig;
		threadPoolWrapper = new ThreadPoolWrapper("job-event", Runtime.getRuntime().availableProcessors() * 2);
		eventBus = new AsyncEventBus(threadPoolWrapper.createExecutorService());
		register();
	}

	private void register() {
		try {
			eventBus.register(jobEventConfig.createJobEventListener());
			isRegistered = true;
		} catch (final JobEventListenerConfigException ex) {
			log.error("Job-scheduler: create JobEventListener failure, error is: ", ex);
		}
	}

	/**
	 * 发布事件.
	 * 
	 * @param event
	 *            作业事件
	 */
	public void post(final JobEvent event) {
		if (isRegistered && !threadPoolWrapper.isShutdown()) {
			eventBus.post(event);
		}
	}

}
