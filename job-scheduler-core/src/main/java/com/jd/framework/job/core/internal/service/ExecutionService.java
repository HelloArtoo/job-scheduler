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

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.jd.framework.job.constant.job.ServerStatus;
import com.jd.framework.job.core.internal.executor.JobRegistry;
import com.jd.framework.job.core.internal.executor.quartz.JobScheduleController;
import com.jd.framework.job.core.internal.helper.ExecutionNodeHelper;
import com.jd.framework.job.core.internal.helper.JobNodeStorageHelper;
import com.jd.framework.job.executor.context.SegmentContexts;
import com.jd.framework.job.regcenter.api.CoordinatorRegistryCenter;
import com.jd.framework.job.utils.concurrent.BlockUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 
 * 作业执行基础服务
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */
public class ExecutionService {

	private final String jobName;

	private final JobNodeStorageHelper jobNodeStorageHelper;

	private final ConfigService configService;

	private final ServerService serverService;

	private final LeaderElectionService leaderElectionService;

	public ExecutionService(final CoordinatorRegistryCenter regCenter, final String jobName) {
		this.jobName = jobName;
		jobNodeStorageHelper = new JobNodeStorageHelper(regCenter, jobName);
		configService = new ConfigService(regCenter, jobName);
		serverService = new ServerService(regCenter, jobName);
		leaderElectionService = new LeaderElectionService(regCenter, jobName);
	}

	/**
	 * 注册作业启动信息.
	 * 
	 * @param segmentContexts
	 *            分段上下文
	 */
	public void registerJobBegin(final SegmentContexts segmentContexts) {
		if (!segmentContexts.getSegmentItemParameters().isEmpty() && configService.load(true).isMonitorExecution()) {
			serverService.updateServerStatus(ServerStatus.RUNNING);
			for (int each : segmentContexts.getSegmentItemParameters().keySet()) {
				jobNodeStorageHelper.fillEphemeralJobNode(ExecutionNodeHelper.getRunningNode(each), "");
				jobNodeStorageHelper.replaceJobNode(ExecutionNodeHelper.getLastBeginTimeNode(each),
						System.currentTimeMillis());
				// 从注册表中获取作业控制器
				JobScheduleController jobScheduleController = JobRegistry.getInstance().getJobScheduleController(
						jobName);
				if (null == jobScheduleController) {
					continue;
				}
				Date nextFireTime = jobScheduleController.getNextFireTime();
				if (null != nextFireTime) {
					jobNodeStorageHelper.replaceJobNode(ExecutionNodeHelper.getNextFireTimeNode(each),
							nextFireTime.getTime());
				}
			}
		}
	}

	/**
	 * 清理作业上次运行时信息. 只会在主节点进行.
	 */
	public void cleanPreviousExecutionInfo() {
		if (!jobNodeStorageHelper.isJobNodeExisted(ExecutionNodeHelper.ROOT)) {
			return;
		}
		if (leaderElectionService.isLeader()) {
			jobNodeStorageHelper.fillEphemeralJobNode(ExecutionNodeHelper.CLEANING, "");
			List<Integer> items = this.getAllItems();
			for (int each : items) {
				jobNodeStorageHelper.removeJobNodeIfExisted(ExecutionNodeHelper.getCompletedNode(each));
			}
			if (jobNodeStorageHelper.isJobNodeExisted(ExecutionNodeHelper.NECESSARY)) {
				fixExecutionInfo(items);
			}
			jobNodeStorageHelper.removeJobNodeIfExisted(ExecutionNodeHelper.CLEANING);
		}
		while (jobNodeStorageHelper.isJobNodeExisted(ExecutionNodeHelper.CLEANING)) {
			BlockUtils.waitingShortTime();
		}
	}

	private void fixExecutionInfo(final List<Integer> items) {
		int newSegmentTotalCount = configService.load(false).getTypeConfig().getCoreConfig().getSegmentTotalCount();
		int currentSegmentTotalCount = items.size();
		if (newSegmentTotalCount > currentSegmentTotalCount) {
			for (int i = currentSegmentTotalCount; i < newSegmentTotalCount; i++) {
				jobNodeStorageHelper.createJobNodeIfNeeded(ExecutionNodeHelper.ROOT + "/" + i);
			}
		} else if (newSegmentTotalCount < currentSegmentTotalCount) {
			for (int i = newSegmentTotalCount; i < currentSegmentTotalCount; i++) {
				jobNodeStorageHelper.removeJobNodeIfExisted(ExecutionNodeHelper.ROOT + "/" + i);
			}
		}
		jobNodeStorageHelper.removeJobNodeIfExisted(ExecutionNodeHelper.NECESSARY);
	}

	/**
	 * 注册作业完成信息.
	 * 
	 * @param segmentContexts
	 *            分段上下文
	 */
	public void registerJobCompleted(final SegmentContexts segmentContexts) {
		if (!configService.load(true).isMonitorExecution()) {
			return;
		}
		serverService.updateServerStatus(ServerStatus.READY);
		for (int each : segmentContexts.getSegmentItemParameters().keySet()) {
			jobNodeStorageHelper.createJobNodeIfNeeded(ExecutionNodeHelper.getCompletedNode(each));
			jobNodeStorageHelper.removeJobNodeIfExisted(ExecutionNodeHelper.getRunningNode(each));
			jobNodeStorageHelper.replaceJobNode(ExecutionNodeHelper.getLastCompleteTimeNode(each),
					System.currentTimeMillis());
		}
	}

	/**
	 * 设置修复运行时分段信息标记的状态标志位.
	 */
	public void setNeedFixExecutionInfoFlag() {
		jobNodeStorageHelper.createJobNodeIfNeeded(ExecutionNodeHelper.NECESSARY);
	}

	/**
	 * 清除分配分段序列号的运行状态.
	 * 
	 * <p>
	 * 用于作业服务器恢复连接注册中心而重新上线的场景, 先清理上次运行时信息.
	 * </p>
	 * 
	 * @param items
	 *            需要清理的分段项列表
	 */
	public void clearRunningInfo(final List<Integer> items) {
		for (int each : items) {
			jobNodeStorageHelper.removeJobNodeIfExisted(ExecutionNodeHelper.getRunningNode(each));
		}
	}

	/**
	 * 如果满足条件，设置任务被错过执行的标记.
	 * 
	 * @param items
	 *            需要设置错过执行的任务分段项
	 * @return 是否满足misfire条件
	 */
	public boolean misfireIfNecessary(final Collection<Integer> items) {
		if (hasRunningItems(items)) {
			setMisfire(items);
			return true;
		}
		return false;
	}

	/**
	 * 设置任务被错过执行的标记.
	 * 
	 * @param items
	 *            需要设置错过执行的任务分段项
	 */
	public void setMisfire(final Collection<Integer> items) {
		if (!configService.load(true).isMonitorExecution()) {
			return;
		}
		for (int each : items) {
			jobNodeStorageHelper.createJobNodeIfNeeded(ExecutionNodeHelper.getMisfireNode(each));
		}
	}

	/**
	 * 获取标记被错过执行的任务分段项.
	 * 
	 * @param items
	 *            需要获取标记被错过执行的任务分段项
	 * @return 标记被错过执行的任务分段项
	 */
	public List<Integer> getMisfiredJobItems(final Collection<Integer> items) {
		List<Integer> result = new ArrayList<>(items.size());
		for (int each : items) {
			if (jobNodeStorageHelper.isJobNodeExisted(ExecutionNodeHelper.getMisfireNode(each))) {
				result.add(each);
			}
		}
		return result;
	}

	/**
	 * 清除任务被错过执行的标记.
	 * 
	 * @param items
	 *            需要清除错过执行的任务分段项
	 */
	public void clearMisfire(final Collection<Integer> items) {
		for (int each : items) {
			jobNodeStorageHelper.removeJobNodeIfExisted(ExecutionNodeHelper.getMisfireNode(each));
		}
	}

	/**
	 * 删除作业执行时信息.
	 */
	public void removeExecutionInfo() {
		jobNodeStorageHelper.removeJobNodeIfExisted(ExecutionNodeHelper.ROOT);
	}

	/**
	 * 判断该分段是否已完成.
	 * 
	 * @param item
	 *            运行中的分段路径
	 * @return 该分段是否已完成
	 */
	public boolean isCompleted(final int item) {
		return jobNodeStorageHelper.isJobNodeExisted(ExecutionNodeHelper.getCompletedNode(item));
	}

	/**
	 * 判断分段项中是否还有执行中的作业.
	 * 
	 * @param items
	 *            需要判断的分段项列表
	 * @return 分段项中是否还有执行中的作业
	 */
	public boolean hasRunningItems(final Collection<Integer> items) {
		if (!configService.load(true).isMonitorExecution()) {
			return false;
		}
		for (int each : items) {
			if (jobNodeStorageHelper.isJobNodeExisted(ExecutionNodeHelper.getRunningNode(each))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断是否还有执行中的作业.
	 * 
	 * @return 是否还有执行中的作业
	 */
	public boolean hasRunningItems() {
		return hasRunningItems(getAllItems());
	}

	private List<Integer> getAllItems() {
		return Lists.transform(jobNodeStorageHelper.getJobNodeChildrenKeys(ExecutionNodeHelper.ROOT),
				new Function<String, Integer>() {

					@Override
					public Integer apply(final String input) {
						return Integer.parseInt(input);
					}
				});
	}
}
