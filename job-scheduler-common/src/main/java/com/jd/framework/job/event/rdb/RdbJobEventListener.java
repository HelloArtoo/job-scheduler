/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.event.rdb;

import java.sql.SQLException;

import javax.sql.DataSource;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.jd.framework.job.event.JobEventListener;
import com.jd.framework.job.event.type.JobExecutionEvent;
import com.jd.framework.job.event.type.JobStatusTraceEvent;

/**
 * 
 * RDB事件监听
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-15
 */
public final class RdbJobEventListener extends RdbJobEventIdentity implements JobEventListener {

	private final RdbJobEventStorage repository;

	public RdbJobEventListener(final DataSource dataSource) throws SQLException {
		repository = new RdbJobEventStorage(dataSource);
	}

	/**
	 * 执行跟踪
	 */
	@Override
	@Subscribe
	@AllowConcurrentEvents
	public void listen(final JobExecutionEvent jobExecutionEvent) {
		repository.addJobExecutionEvent(jobExecutionEvent);
	}

	/**
	 * 运行痕迹跟踪
	 */
	@Override
	@Subscribe
	@AllowConcurrentEvents
	public void listen(final JobStatusTraceEvent jobStatusTraceEvent) {
		repository.addJobStatusTraceEvent(jobStatusTraceEvent);
	}

}
