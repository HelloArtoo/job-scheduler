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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.curator.framework.api.transaction.CuratorTransactionFinal;

import com.jd.framework.job.core.api.strategy.JobSegmentStrategy;
import com.jd.framework.job.core.api.strategy.JobSegmentStrategyFactory;
import com.jd.framework.job.core.api.strategy.JobSegmentStrategyOption;
import com.jd.framework.job.core.config.FactJobConfiguration;
import com.jd.framework.job.core.internal.callback.TransactionExecutionCallback;
import com.jd.framework.job.core.internal.helper.JobNodePathHelper;
import com.jd.framework.job.core.internal.helper.JobNodeStorageHelper;
import com.jd.framework.job.core.internal.helper.SegmentNodeHelper;
import com.jd.framework.job.regcenter.api.CoordinatorRegistryCenter;
import com.jd.framework.job.utils.concurrent.BlockUtils;
import com.jd.framework.job.utils.env.LocalHostService;
import com.jd.framework.job.utils.segment.SegmentItems;

/**
 * 
 * 作业分段服务
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */
@Slf4j
public class SegmentService {
	private final String jobName;

	private final JobNodeStorageHelper jobNodeStorage;

	private final LocalHostService localHostService = new LocalHostService();

	private final LeaderElectionService leaderElectionService;

	private final ConfigService configService;

	private final ServerService serverService;

	private final ExecutionService executionService;

	private final JobNodePathHelper jobNodePath;

	public SegmentService(final CoordinatorRegistryCenter regCenter, final String jobName) {
		this.jobName = jobName;
		jobNodeStorage = new JobNodeStorageHelper(regCenter, jobName);
		leaderElectionService = new LeaderElectionService(regCenter, jobName);
		configService = new ConfigService(regCenter, jobName);
		serverService = new ServerService(regCenter, jobName);
		executionService = new ExecutionService(regCenter, jobName);
		jobNodePath = new JobNodePathHelper(jobName);
	}

	/**
	 * 设置需要重新分段的标记.
	 */
	public void setResegmentFlag() {
		jobNodeStorage.createJobNodeIfNeeded(SegmentNodeHelper.NECESSARY);
	}

	/**
	 * 判断是否需要重分段.
	 * 
	 * @return 是否需要重分段
	 */
	public boolean isNeedSegment() {
		return jobNodeStorage.isJobNodeExisted(SegmentNodeHelper.NECESSARY);
	}

	/**
	 * 如果需要分段且当前节点为主节点, 则作业分段. 如果当前无可用节点则不分段.
	 */
	public void segmentIfNecessary() {
		List<String> availableSegmentServers = serverService.getAvailableSegmentServers();
		if (availableSegmentServers.isEmpty()) {
			clearSegmentInfo();
			return;
		}
		if (!isNeedSegment()) {
			return;
		}
		if (!leaderElectionService.isLeader()) {
			blockUntilSegmentCompleted();
			return;
		}
		FactJobConfiguration factJobConfig = configService.load(false);
		if (factJobConfig.isMonitorExecution()) {
			waitingOtherJobCompleted();
		}
		log.debug("Job '{}' segment begin.", jobName);
		jobNodeStorage.fillEphemeralJobNode(SegmentNodeHelper.PROCESSING, "");
		clearSegmentInfo();
		JobSegmentStrategy jobSegmentStrategy = JobSegmentStrategyFactory.getStrategy(factJobConfig
				.getJobSegmentStrategyClass());
		JobSegmentStrategyOption option = new JobSegmentStrategyOption(jobName, factJobConfig.getTypeConfig()
				.getCoreConfig().getSegmentTotalCount());
		jobNodeStorage.executeInTransaction(new PersistSegmentInfoTransactionExecutionCallback(jobSegmentStrategy
				.segment(availableSegmentServers, option)));
		log.debug("Job '{}' segment complete.", jobName);
	}

	private void blockUntilSegmentCompleted() {
		while (!leaderElectionService.isLeader()
				&& (jobNodeStorage.isJobNodeExisted(SegmentNodeHelper.NECESSARY) || jobNodeStorage
						.isJobNodeExisted(SegmentNodeHelper.PROCESSING))) {
			log.debug("Job '{}' sleep short time until segment completed.", jobName);
			BlockUtils.waitingShortTime();
		}
	}

	private void waitingOtherJobCompleted() {
		while (executionService.hasRunningItems()) {
			log.debug("Job '{}' sleep short time until other job completed.", jobName);
			BlockUtils.waitingShortTime();
		}
	}

	private void clearSegmentInfo() {
		for (String each : serverService.getAllServers()) {
			jobNodeStorage.removeJobNodeIfExisted(SegmentNodeHelper.getSegmentNode(each));
		}
	}

	/**
	 * 获取运行在本作业服务器的分段序列号.
	 * 
	 * @return 运行在本作业服务器的分段序列号
	 */
	public List<Integer> getLocalHostSegmentItems() {
		String ip = localHostService.getIp();
		if (!jobNodeStorage.isJobNodeExisted(SegmentNodeHelper.getSegmentNode(ip))) {
			return Collections.emptyList();
		}
		return SegmentItems.toItemList(jobNodeStorage.getJobNodeDataDirectly(SegmentNodeHelper.getSegmentNode(ip)));
	}

	/**
	 * 查询是否存在没有运行状态并且含有分段节点的作业服务器.
	 * 
	 * @return 是否存在没有运行状态并且含有分段节点的作业服务器
	 */
	public boolean hasNotRunningSegmentNode() {
		for (String each : this.serverService.getAllServers()) {
			if (this.jobNodeStorage.isJobNodeExisted(SegmentNodeHelper.getSegmentNode(each))
					&& !this.serverService.hasStatusNode(each)) {
				return true;
			}
		}
		
		return false;
	}

	@RequiredArgsConstructor
	class PersistSegmentInfoTransactionExecutionCallback implements TransactionExecutionCallback {

		private final Map<String, List<Integer>> segmentItems;

		@Override
		public void execute(final CuratorTransactionFinal curatorTransactionFinal) throws Exception {
			for (Entry<String, List<Integer>> entry : segmentItems.entrySet()) {
				curatorTransactionFinal
						.create()
						.forPath(jobNodePath.getFullPath(SegmentNodeHelper.getSegmentNode(entry.getKey())),
								SegmentItems.toItemsString(entry.getValue()).getBytes()).and();
			}
			curatorTransactionFinal.delete().forPath(jobNodePath.getFullPath(SegmentNodeHelper.NECESSARY)).and();
			curatorTransactionFinal.delete().forPath(jobNodePath.getFullPath(SegmentNodeHelper.PROCESSING)).and();
		}
	}
}
