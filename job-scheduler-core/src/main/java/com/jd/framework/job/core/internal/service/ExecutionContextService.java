/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.core.internal.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.common.base.Joiner;
import com.jd.framework.job.core.config.FactJobConfiguration;
import com.jd.framework.job.core.internal.helper.ExecutionNodeHelper;
import com.jd.framework.job.core.internal.helper.JobNodeStorageHelper;
import com.jd.framework.job.executor.context.SegmentContexts;
import com.jd.framework.job.regcenter.api.CoordinatorRegistryCenter;
import com.jd.framework.job.utils.env.LocalHostService;
import com.jd.framework.job.utils.segment.SegmentItemParameters;

/**
 * 
 * 作业运行时上下文
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */
public class ExecutionContextService {

	private final JobNodeStorageHelper jobNodeStorage;

	private final ConfigService configService;

	private final LocalHostService localHostService;

	public ExecutionContextService(final CoordinatorRegistryCenter regCenter, final String jobName) {
		jobNodeStorage = new JobNodeStorageHelper(regCenter, jobName);
		configService = new ConfigService(regCenter, jobName);
		localHostService = new LocalHostService();
	}

	/**
	 * 获取当前作业服务器分段上下文.
	 * 
	 * @param segmentItems
	 *            分段项
	 * @return 分段上下文
	 */
	public SegmentContexts getJobSegmentContext(final List<Integer> segmentItems) {
		FactJobConfiguration factJobConfig = configService.load(false);
		//remove running items
		removeRunningIfMonitorExecution(factJobConfig.isMonitorExecution(), segmentItems);
		if (segmentItems.isEmpty()) {
			return new SegmentContexts(buildTaskId(factJobConfig, segmentItems), factJobConfig.getJobName(),
					factJobConfig.getTypeConfig().getCoreConfig().getSegmentTotalCount(), factJobConfig.getTypeConfig()
							.getCoreConfig().getJobParameter(), Collections.<Integer, String> emptyMap());
		}

		Map<Integer, String> segmentItemParameterMap = new SegmentItemParameters(factJobConfig.getTypeConfig()
				.getCoreConfig().getSegmentItemParameters()).getMap();
		return new SegmentContexts(buildTaskId(factJobConfig, segmentItems), factJobConfig.getJobName(), factJobConfig
				.getTypeConfig().getCoreConfig().getSegmentTotalCount(), factJobConfig.getTypeConfig().getCoreConfig()
				.getJobParameter(), getAssignedSegmentItemParameterMap(segmentItems, segmentItemParameterMap));
	}

	private String buildTaskId(final FactJobConfiguration factJobConfig, final List<Integer> segmentItems) {
		return Joiner.on("@-@").join(factJobConfig.getJobName(), Joiner.on(",").join(segmentItems), "READY",
				localHostService.getIp(), UUID.randomUUID().toString());
	}

	/**
	 * 开启Monitor时，移除正在运行的的items
	 * 
	 * @param monitorExecution
	 * @param segmentItems
	 * @author Rong Hu
	 */
	private void removeRunningIfMonitorExecution(final boolean monitorExecution, final List<Integer> segmentItems) {
		if (!monitorExecution) {
			return;
		}
		List<Integer> runningSegmentItems = new ArrayList<>(segmentItems.size());
		for (int each : segmentItems) {
			if (isRunning(each)) {
				runningSegmentItems.add(each);
			}
		}
		segmentItems.removeAll(runningSegmentItems);
	}

	private boolean isRunning(final int segmentItem) {
		return jobNodeStorage.isJobNodeExisted(ExecutionNodeHelper.getRunningNode(segmentItem));
	}

	private Map<Integer, String> getAssignedSegmentItemParameterMap(final List<Integer> segmentItems,
			final Map<Integer, String> segmentItemParameterMap) {
		Map<Integer, String> result = new HashMap<>(segmentItemParameterMap.size(), 1);
		for (int each : segmentItems) {
			result.put(each, segmentItemParameterMap.get(each));
		}
		return result;
	}
}
