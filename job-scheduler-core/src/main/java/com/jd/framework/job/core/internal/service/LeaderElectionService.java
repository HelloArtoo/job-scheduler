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

import com.jd.framework.job.core.internal.callback.LeaderExecutionCallback;
import com.jd.framework.job.core.internal.helper.ElectionNodeHelper;
import com.jd.framework.job.core.internal.helper.JobNodeStorageHelper;
import com.jd.framework.job.regcenter.api.CoordinatorRegistryCenter;
import com.jd.framework.job.utils.concurrent.BlockUtils;
import com.jd.framework.job.utils.env.LocalHostService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 选举主节点的服务
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */
@Slf4j
public class LeaderElectionService {

	private final LocalHostService localHostService = new LocalHostService();

	private final ServerService serverService;

	private final JobNodeStorageHelper jobNodeStorageHelper;

	public LeaderElectionService(final CoordinatorRegistryCenter regCenter, final String jobName) {
		jobNodeStorageHelper = new JobNodeStorageHelper(regCenter, jobName);
		serverService = new ServerService(regCenter, jobName);
	}

	/**
	 * 强制选举主节点.
	 */
	public void leaderForceElection() {
		jobNodeStorageHelper.executeInLeader(ElectionNodeHelper.LATCH, new LeaderElectionExecutionCallback(true));
	}

	/**
	 * 选举主节点.
	 */
	public void leaderElection() {
		jobNodeStorageHelper.executeInLeader(ElectionNodeHelper.LATCH, new LeaderElectionExecutionCallback(false));
	}

	/**
	 * 判断当前节点是否是主节点.
	 * 
	 * <p>
	 * 如果主节点正在选举中而导致取不到主节点, 则阻塞至主节点选举完成再返回.
	 * </p>
	 * 
	 * @return 当前节点是否是主节点
	 */
	public Boolean isLeader() {
		String localHostIp = localHostService.getIp();
		while (!hasLeader() && !serverService.getAvailableServers().isEmpty()) {
			log.info("Leader node is electing, waiting for {} ms", 100);
			BlockUtils.waitingShortTime();
			leaderElection();
		}
		return localHostIp.equals(jobNodeStorageHelper.getJobNodeData(ElectionNodeHelper.LEADER_HOST));
	}

	/**
	 * 判断是否已经有主节点.
	 * 
	 * <p>
	 * 仅为选举监听使用. 程序中其他地方判断是否有主节点应使用{@code isLeader() }方法.
	 * </p>
	 * 
	 * @return 是否已经有主节点
	 */
	public boolean hasLeader() {
		return jobNodeStorageHelper.isJobNodeExisted(ElectionNodeHelper.LEADER_HOST);
	}

	/**
	 * 删除主节点供重新选举.
	 */
	public void removeLeader() {
		jobNodeStorageHelper.removeJobNodeIfExisted(ElectionNodeHelper.LEADER_HOST);
	}

	@RequiredArgsConstructor
	class LeaderElectionExecutionCallback implements LeaderExecutionCallback {

		/** 是否强制选举主节点 */
		private final boolean isForceElect;

		@Override
		public void execute() {
			if (!jobNodeStorageHelper.isJobNodeExisted(ElectionNodeHelper.LEADER_HOST)
					&& (isForceElect || serverService.isAvailableServer(localHostService.getIp()))) {
				// 强制写入主节点数据
				jobNodeStorageHelper.fillEphemeralJobNode(ElectionNodeHelper.LEADER_HOST, localHostService.getIp());
			}
		}
	}
}
