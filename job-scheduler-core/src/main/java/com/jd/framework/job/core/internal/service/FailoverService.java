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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import com.jd.framework.job.core.internal.callback.LeaderExecutionCallback;
import com.jd.framework.job.core.internal.executor.JobRegistry;
import com.jd.framework.job.core.internal.helper.ExecutionNodeHelper;
import com.jd.framework.job.core.internal.helper.FailoverNodeHelper;
import com.jd.framework.job.core.internal.helper.JobNodeStorageHelper;
import com.jd.framework.job.regcenter.api.CoordinatorRegistryCenter;
import com.jd.framework.job.utils.env.LocalHostService;

/**
 * 
 * 失效转移服务
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */
@Slf4j
public class FailoverService {

	private final String jobName;

	private final LocalHostService localHostService = new LocalHostService();

	private final JobNodeStorageHelper jobNodeStorage;

	private final ServerService serverService;

	private final SegmentService segmentService;

	public FailoverService(final CoordinatorRegistryCenter regCenter, final String jobName) {
		this.jobName = jobName;
		jobNodeStorage = new JobNodeStorageHelper(regCenter, jobName);
		serverService = new ServerService(regCenter, jobName);
		segmentService = new SegmentService(regCenter, jobName);
	}

	/**
	 * 设置失效的分片项标记.
	 * 
	 * @param item
	 *            崩溃的作业项
	 */
	public void setCrashedFailoverFlag(final int item) {
		if (!isFailoverAssigned(item)) {
			jobNodeStorage.createJobNodeIfNeeded(FailoverNodeHelper.getItemsNode(item));
		}
	}

	private boolean isFailoverAssigned(final Integer item) {
		return jobNodeStorage.isJobNodeExisted(FailoverNodeHelper.getExecutionFailoverNode(item));
	}

	/**
	 * 如果需要失效转移, 则设置作业失效转移.
	 */
	public void failoverIfNecessary() {
		if (needFailover()) {
			jobNodeStorage.executeInLeader(FailoverNodeHelper.LATCH, new FailoverLeaderExecutionCallback());
		}
	}

	private boolean needFailover() {
		return jobNodeStorage.isJobNodeExisted(FailoverNodeHelper.ITEMS_ROOT)
				&& !jobNodeStorage.getJobNodeChildrenKeys(FailoverNodeHelper.ITEMS_ROOT).isEmpty()
				&& serverService.isLocalhostServerReady();
	}

	/**
	 * 更新执行完毕失效转移的分片项状态.
	 * 
	 * @param items
	 *            执行完毕失效转移的分片项集合
	 */
	public void updateFailoverComplete(final Collection<Integer> items) {
		for (int each : items) {
			jobNodeStorage.removeJobNodeIfExisted(FailoverNodeHelper.getExecutionFailoverNode(each));
		}
	}

	/**
	 * 获取运行在本作业服务器的失效转移序列号.
	 * 
	 * @return 运行在本作业服务器的失效转移序列号
	 */
	public List<Integer> getLocalHostFailoverItems() {
		List<String> items = jobNodeStorage.getJobNodeChildrenKeys(ExecutionNodeHelper.ROOT);
		List<Integer> result = new ArrayList<>(items.size());
		String ip = localHostService.getIp();
		for (String each : items) {
			int item = Integer.parseInt(each);
			String node = FailoverNodeHelper.getExecutionFailoverNode(item);
			if (jobNodeStorage.isJobNodeExisted(node) && ip.equals(jobNodeStorage.getJobNodeDataDirectly(node))) {
				result.add(item);
			}
		}
		Collections.sort(result);
		return result;
	}

	/**
	 * 获取运行在本作业服务器的被失效转移的序列号.
	 * 
	 * @return 运行在本作业服务器的被失效转移的序列号
	 */
	public List<Integer> getLocalHostTakeOffItems() {
		List<Integer> segmentItems = segmentService.getLocalHostSegmentItems();
		List<Integer> result = new ArrayList<>(segmentItems.size());
		for (int each : segmentItems) {
			if (jobNodeStorage.isJobNodeExisted(FailoverNodeHelper.getExecutionFailoverNode(each))) {
				result.add(each);
			}
		}
		return result;
	}

	/**
	 * 删除作业失效转移信息.
	 */
	public void removeFailoverInfo() {
		for (String each : jobNodeStorage.getJobNodeChildrenKeys(ExecutionNodeHelper.ROOT)) {
			jobNodeStorage.removeJobNodeIfExisted(FailoverNodeHelper.getExecutionFailoverNode(Integer.parseInt(each)));
		}
	}

	/**
	 * 
	 * 失效转移回调
	 * 
	 * @author Rong Hu
	 * @version 1.0, 2017-4-9
	 */
	class FailoverLeaderExecutionCallback implements LeaderExecutionCallback {

		@Override
		public void execute() {
			if (!needFailover()) {
				return;
			}
			int crashedItem = Integer.parseInt(jobNodeStorage.getJobNodeChildrenKeys(FailoverNodeHelper.ITEMS_ROOT)
					.get(0));
			log.debug("Failover job '{}' begin, crashed item '{}'", jobName, crashedItem);
			jobNodeStorage.fillEphemeralJobNode(FailoverNodeHelper.getExecutionFailoverNode(crashedItem),
					localHostService.getIp());
			jobNodeStorage.removeJobNodeIfExisted(FailoverNodeHelper.getItemsNode(crashedItem));
			// TODO 不应使用triggerJob, 而是使用executor统一调度
			JobRegistry.getInstance().getJobScheduleController(jobName).triggerJob();
		}
	}
}
