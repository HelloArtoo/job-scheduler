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

import com.google.common.util.concurrent.AbstractScheduledService;
import com.jd.framework.job.core.config.FactJobConfiguration;
import com.jd.framework.job.regcenter.api.CoordinatorRegistryCenter;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.TimeUnit;

/**
 * 
 * 修复服务器状态不一致服务
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */
@Slf4j
public class ReconcileService extends AbstractScheduledService {
	private long lastReconcileTime;

	private final ConfigService configService;

	private final SegmentService segmentService;

	private final LeaderElectionService leaderElectionService;

	public ReconcileService(final CoordinatorRegistryCenter regCenter, final String jobName) {
		lastReconcileTime = System.currentTimeMillis();
		configService = new ConfigService(regCenter, jobName);
		segmentService = new SegmentService(regCenter, jobName);
		leaderElectionService = new LeaderElectionService(regCenter, jobName);
	}

	@Override
	protected void runOneIteration() throws Exception {
		FactJobConfiguration config = configService.load(true);
		int reconcileIntervalMinutes = null == config || config.getReconcileIntervalMinutes() <= 0 ? -1 : config
				.getReconcileIntervalMinutes();
		if (reconcileIntervalMinutes > 0
				&& (System.currentTimeMillis() - lastReconcileTime >= reconcileIntervalMinutes * 60 * 1000)) {
			lastReconcileTime = System.currentTimeMillis();
			if (leaderElectionService.isLeader() && !segmentService.isNeedSegment()
					&& segmentService.hasNotRunningSegmentNode()) {
				log.warn("Elastic Job: job status node has inconsistent value,start reconciling...");
				// 打标，重新分配
				segmentService.setResegmentFlag();
			}
		}
	}

	@Override
	protected Scheduler scheduler() {
		return Scheduler.newFixedDelaySchedule(0, 1, TimeUnit.MINUTES);
	}
}
