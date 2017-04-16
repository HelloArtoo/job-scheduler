/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.core.internal.listener;

import java.util.List;

import com.jd.framework.job.core.api.listener.ScheduleJobListener;
import com.jd.framework.job.core.internal.listener.sub.ConfigListenerManager;
import com.jd.framework.job.core.internal.listener.sub.ElectionListenerManager;
import com.jd.framework.job.core.internal.listener.sub.ExecutionListenerManager;
import com.jd.framework.job.core.internal.listener.sub.FailoverListenerManager;
import com.jd.framework.job.core.internal.listener.sub.GuaranteeListenerManager;
import com.jd.framework.job.core.internal.listener.sub.JobOperationListenerManager;
import com.jd.framework.job.core.internal.listener.sub.SegmentListenerManager;
import com.jd.framework.job.regcenter.api.CoordinatorRegistryCenter;

/**
 * 
 * 注册中心的管理器统一服务中心
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */
public class ListenerManager {

	private final ElectionListenerManager electionListenerManager;

	private final SegmentListenerManager segmentListenerManager;

	private final ExecutionListenerManager executionListenerManager;

	private final FailoverListenerManager failoverListenerManager;

	private final JobOperationListenerManager jobOperationListenerManager;

	private final ConfigListenerManager configListenerManager;

	private final GuaranteeListenerManager guaranteeListenerManager;

	public ListenerManager(final CoordinatorRegistryCenter regCenter, final String jobName,
			final List<ScheduleJobListener> scheduleJobListener) {
		electionListenerManager = new ElectionListenerManager(regCenter, jobName);
		segmentListenerManager = new SegmentListenerManager(regCenter, jobName);
		executionListenerManager = new ExecutionListenerManager(regCenter, jobName);
		failoverListenerManager = new FailoverListenerManager(regCenter, jobName);
		jobOperationListenerManager = new JobOperationListenerManager(regCenter, jobName);
		configListenerManager = new ConfigListenerManager(regCenter, jobName);
		guaranteeListenerManager = new GuaranteeListenerManager(regCenter, jobName, scheduleJobListener);
	}

	/**
	 * 开启所有监听器.
	 */
	public void startAllListeners() {
		electionListenerManager.start();
		segmentListenerManager.start();
		executionListenerManager.start();
		failoverListenerManager.start();
		jobOperationListenerManager.start();
		configListenerManager.start();
		guaranteeListenerManager.start();
	}

	/**
	 * 设置当前分段总数.
	 * 
	 * @param currentSegmentTotalCount
	 *            当前分段总数
	 */
	public void setCurrentSegmentTotalCount(final int currentSegmentTotalCount) {
		segmentListenerManager.setCurrentSegmentTotalCount(currentSegmentTotalCount);
	}
}
