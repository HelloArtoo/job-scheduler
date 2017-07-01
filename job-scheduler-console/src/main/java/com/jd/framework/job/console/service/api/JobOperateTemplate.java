/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.console.service.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import lombok.RequiredArgsConstructor;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.jd.framework.job.core.internal.helper.JobNodePathHelper;
import com.jd.framework.job.regcenter.api.CoordinatorRegistryCenter;

@RequiredArgsConstructor
public final class JobOperateTemplate {

	private final CoordinatorRegistryCenter regCenter;

	/**
	 * 作业操作.
	 * 
	 * @param jobName
	 *            作业名称
	 * @param serverIp
	 *            作业服务器IP地址
	 * @param callback
	 *            作业操作的回调方法
	 * @return 操作失败的作业服务器IP地址列表(作业维度操作)或作业名称列表(IP维度操作)
	 */
	public Collection<String> operate(final Optional<String> jobName, final Optional<String> serverIp,
			final JobOperateCallback callback) {
		Preconditions.checkArgument(jobName.isPresent() || serverIp.isPresent(),
				"At least indicate jobName or serverIp.");
		Collection<String> result;
		if (jobName.isPresent() && serverIp.isPresent()) {
			boolean isSuccess = callback.operate(jobName.get(), serverIp.get());
			if (!isSuccess) {
				result = new ArrayList<>(1);
				result.add(serverIp.get());
			} else {
				result = Collections.emptyList();
			}
		} else if (jobName.isPresent()) {
			JobNodePathHelper jobNodePath = new JobNodePathHelper(jobName.get());
			List<String> ipList = regCenter.getChildrenKeys(jobNodePath.getServerNodePath());
			result = new ArrayList<>(ipList.size());
			for (String each : ipList) {
				boolean isSuccess = callback.operate(jobName.get(), each);
				if (!isSuccess) {
					result.add(each);
				}
			}
		} else {
			List<String> jobNames = regCenter.getChildrenKeys("/");
			result = new ArrayList<>(jobNames.size());
			for (String each : jobNames) {
				boolean isSuccess = callback.operate(each, serverIp.get());
				if (!isSuccess) {
					result.add(each);
				}
			}
		}
		return result;
	}
}
