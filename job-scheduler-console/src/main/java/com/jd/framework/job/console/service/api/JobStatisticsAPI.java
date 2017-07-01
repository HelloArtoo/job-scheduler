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
import java.util.Date;
import java.util.List;

import lombok.RequiredArgsConstructor;

import com.jd.framework.job.console.domain.job.ExecutionInfo;
import com.jd.framework.job.console.domain.job.JobBriefInfo;
import com.jd.framework.job.console.domain.job.ServerInfo;
import com.jd.framework.job.core.config.FactJobConfiguration;
import com.jd.framework.job.core.internal.factory.FactJobConfigGsonFactory;
import com.jd.framework.job.core.internal.helper.JobNodePathHelper;
import com.jd.framework.job.regcenter.api.CoordinatorRegistryCenter;

/**
 * 
 * 作业状态展示的实现类.
 * 
 * @author Rong Hu
 * @version 1.0, 2017-7-1
 */
@RequiredArgsConstructor
public final class JobStatisticsAPI {

	private final CoordinatorRegistryCenter regCenter;

	/**
	 * 获取作业概览信息.
	 * 
	 * @param jobName
	 *            作业名称
	 * @return 作业概览信息.
	 */
	public JobBriefInfo getJobBriefInfo(final String jobName) {
		JobNodePathHelper jobNodePath = new JobNodePathHelper(jobName);
		JobBriefInfo result = new JobBriefInfo();
		result.setJobName(jobName);
		String factJobConfigJson = regCenter.get(jobNodePath.getConfigNodePath());
		if (null == factJobConfigJson) {
			return null;
		}
		FactJobConfiguration factJobConfig = FactJobConfigGsonFactory.fromJson(factJobConfigJson);
		result.setJobType(factJobConfig.getTypeConfig().getJobType().name());
		result.setDescription(factJobConfig.getTypeConfig().getCoreConfig().getDescription());
		result.setStatus(getJobStatus(jobName));
		result.setCron(factJobConfig.getTypeConfig().getCoreConfig().getCron());
		return result;
	}

	/**
	 * 获取所有作业简明信息.
	 * 
	 * @return 作业简明信息集合.
	 */
	public Collection<JobBriefInfo> getAllJobsBriefInfo() {
		List<String> jobNames = regCenter.getChildrenKeys("/");
		List<JobBriefInfo> result = new ArrayList<>(jobNames.size());
		for (String each : jobNames) {
			JobBriefInfo jobBriefInfo = getJobBriefInfo(each);
			if (null != jobBriefInfo) {
				result.add(jobBriefInfo);
			}
		}
		Collections.sort(result);
		return result;
	}

	/**
	 * 获取执行作业的服务器.
	 * 
	 * @param jobName
	 *            作业名称
	 * @return 作业的服务器集合
	 */
	public Collection<ServerInfo> getServers(final String jobName) {
		JobNodePathHelper jobNodePath = new JobNodePathHelper(jobName);
		List<String> serverIps = regCenter.getChildrenKeys(jobNodePath.getServerNodePath());
		Collection<ServerInfo> result = new ArrayList<>(serverIps.size());
		for (String each : serverIps) {
			result.add(getJobServer(jobName, each));
		}
		return result;
	}

	/**
	 * 获取作业运行时信息.
	 * 
	 * @param jobName
	 *            作业名称
	 * @return 作业运行时信息集合
	 */
	public Collection<ExecutionInfo> getExecutionInfo(final String jobName) {
		String executionRootPath = new JobNodePathHelper(jobName).getExecutionNodePath();
		if (!regCenter.isExisted(executionRootPath)) {
			return Collections.emptyList();
		}
		List<String> items = regCenter.getChildrenKeys(executionRootPath);
		List<ExecutionInfo> result = new ArrayList<>(items.size());
		for (String each : items) {
			result.add(getExecutionInfo(jobName, each));
		}
		Collections.sort(result);
		return result;
	}

	private ServerInfo getJobServer(final String jobName, final String serverIp) {
		ServerInfo result = new ServerInfo();
		JobNodePathHelper jobNodePath = new JobNodePathHelper(jobName);
		result.setJobName(jobName);
		result.setIp(serverIp);
		result.setHostName(regCenter.get(jobNodePath.getServerNodePath(serverIp, "hostName")));
		result.setSegment(regCenter.get(jobNodePath.getServerNodePath(serverIp, "segment")));
		result.setStatus(getServerStatus(jobName, serverIp));
		return result;
	}

	private ServerInfo.ServerStatus getServerStatus(final String jobName, final String serverIp) {
		JobNodePathHelper jobNodePath = new JobNodePathHelper(jobName);
		String status = regCenter.get(jobNodePath.getServerNodePath(serverIp, "status"));
		boolean disabled = regCenter.isExisted(jobNodePath.getServerNodePath(serverIp, "disabled"));
		boolean paused = regCenter.isExisted(jobNodePath.getServerNodePath(serverIp, "paused"));
		boolean shutdown = regCenter.isExisted(jobNodePath.getServerNodePath(serverIp, "shutdown"));
		return ServerInfo.ServerStatus.getServerStatus(status, disabled, paused, shutdown);
	}

	private ExecutionInfo getExecutionInfo(final String jobName, final String item) {
		ExecutionInfo result = new ExecutionInfo();
		result.setItem(Integer.parseInt(item));
		JobNodePathHelper jobNodePath = new JobNodePathHelper(jobName);
		boolean running = regCenter.isExisted(jobNodePath.getExecutionNodePath(item, "running"));
		boolean completed = regCenter.isExisted(jobNodePath.getExecutionNodePath(item, "completed"));
		result.setStatus(ExecutionInfo.ExecutionStatus.getExecutionStatus(running, completed));
		if (regCenter.isExisted(jobNodePath.getExecutionNodePath(item, "failover"))) {
			result.setFailoverIp(regCenter.get(jobNodePath.getExecutionNodePath(item, "failover")));
		}
		String lastBeginTime = regCenter.get(jobNodePath.getExecutionNodePath(item, "lastBeginTime"));
		result.setLastBeginTime(null == lastBeginTime ? null : new Date(Long.parseLong(lastBeginTime)));
		String nextFireTime = regCenter.get(jobNodePath.getExecutionNodePath(item, "nextFireTime"));
		result.setNextFireTime(null == nextFireTime ? null : new Date(Long.parseLong(nextFireTime)));
		String lastCompleteTime = regCenter.get(jobNodePath.getExecutionNodePath(item, "lastCompleteTime"));
		result.setLastCompleteTime(null == lastCompleteTime ? null : new Date(Long.parseLong(lastCompleteTime)));
		return result;
	}

	private JobBriefInfo.JobStatus getJobStatus(final String jobName) {
		JobNodePathHelper jobNodePath = new JobNodePathHelper(jobName);
		List<String> servers = regCenter.getChildrenKeys(jobNodePath.getServerNodePath());
		int okCount = 0;
		int crashedCount = 0;
		int disabledCount = 0;
		for (String each : servers) {
			switch (getServerStatus(jobName, each)) {
			case READY:
			case RUNNING:
				okCount++;
				break;
			case DISABLED:
			case PAUSED:
				disabledCount++;
				break;
			case CRASHED:
			case SHUTDOWN:
				crashedCount++;
				break;
			default:
				break;
			}
		}
		return JobBriefInfo.JobStatus.getJobStatus(okCount, crashedCount, disabledCount, servers.size());
	}
}
