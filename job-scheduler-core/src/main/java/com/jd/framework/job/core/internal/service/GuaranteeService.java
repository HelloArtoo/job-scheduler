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

import java.util.Collection;

import com.jd.framework.job.core.internal.helper.GuaranteeNodeHelper;
import com.jd.framework.job.core.internal.helper.JobNodeStorageHelper;
import com.jd.framework.job.regcenter.api.CoordinatorRegistryCenter;

/**
 * 保证分布式任务全部开始和结束状态的服务.
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */
public class GuaranteeService {

	private final JobNodeStorageHelper jobNodeStorage;

	private final ConfigService configService;

	public GuaranteeService(final CoordinatorRegistryCenter regCenter, final String jobName) {
		jobNodeStorage = new JobNodeStorageHelper(regCenter, jobName);
		configService = new ConfigService(regCenter, jobName);
	}

	/**
	 * 根据分段项注册任务开始运行.
	 * 
	 * @param SegmentItems
	 *            待注册的分段项
	 */
	public void registerStart(final Collection<Integer> SegmentItems) {
		for (int each : SegmentItems) {
			jobNodeStorage.createJobNodeIfNeeded(GuaranteeNodeHelper.getStartedNode(each));
		}
	}

	/**
	 * 判断是否所有的任务均启动完毕.
	 * 
	 * @return 是否所有的任务均启动完毕
	 */
	public boolean isAllStarted() {
		return jobNodeStorage.isJobNodeExisted(GuaranteeNodeHelper.STARTED_ROOT)
				&& configService.load(false).getTypeConfig().getCoreConfig().getSegmentTotalCount() == jobNodeStorage
						.getJobNodeChildrenKeys(GuaranteeNodeHelper.STARTED_ROOT).size();
	}

	/**
	 * 清理所有任务启动信息.
	 */
	public void clearAllStartedInfo() {
		jobNodeStorage.removeJobNodeIfExisted(GuaranteeNodeHelper.STARTED_ROOT);
	}

	/**
	 * 根据分段项注册任务完成运行.
	 * 
	 * @param SegmentItems
	 *            待注册的分段项
	 */
	public void registerComplete(final Collection<Integer> SegmentItems) {
		for (int each : SegmentItems) {
			jobNodeStorage.createJobNodeIfNeeded(GuaranteeNodeHelper.getCompletedNode(each));
		}
	}

	/**
	 * 判断是否所有的任务均执行完毕.
	 * 
	 * @return 是否所有的任务均执行完毕
	 */
	public boolean isAllCompleted() {
		return jobNodeStorage.isJobNodeExisted(GuaranteeNodeHelper.COMPLETED_ROOT)
				&& configService.load(false).getTypeConfig().getCoreConfig().getSegmentTotalCount() <= jobNodeStorage
						.getJobNodeChildrenKeys(GuaranteeNodeHelper.COMPLETED_ROOT).size();
	}

	/**
	 * 清理所有任务启动信息.
	 */
	public void clearAllCompletedInfo() {
		jobNodeStorage.removeJobNodeIfExisted(GuaranteeNodeHelper.COMPLETED_ROOT);
	}
}
