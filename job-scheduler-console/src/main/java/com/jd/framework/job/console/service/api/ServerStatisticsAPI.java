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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;

import com.jd.framework.job.console.domain.job.ServerBriefInfo;
import com.jd.framework.job.console.domain.job.ServerInfo;
import com.jd.framework.job.core.internal.helper.JobNodePathHelper;
import com.jd.framework.job.regcenter.api.CoordinatorRegistryCenter;

@RequiredArgsConstructor
public final class ServerStatisticsAPI {

	private final CoordinatorRegistryCenter regCenter;

	/**
	 * 获取所有作业服务器简明信息.
	 * 
	 * @return 作业服务器简明信息集合
	 */
	public Collection<ServerBriefInfo> getAllServersBriefInfo() {
		Map<String, String> serverHostMap = new HashMap<>();
		Collection<String> aliveServers = new ArrayList<>();
		Collection<String> crashedServers = new ArrayList<>();
		List<String> jobs = regCenter.getChildrenKeys("/");
		for (String jobName : jobs) {
			JobNodePathHelper jobNodePath = new JobNodePathHelper(jobName);
			List<String> servers = regCenter.getChildrenKeys(jobNodePath.getServerNodePath());
			for (String server : servers) {
				serverHostMap.put(server, regCenter.get(jobNodePath.getServerNodePath(server, "hostName")));
				if (!regCenter.isExisted(jobNodePath.getServerNodePath(server, "shutdown"))
						&& regCenter.isExisted(jobNodePath.getServerNodePath(server, "status"))) {
					aliveServers.add(server);
				} else {
					crashedServers.add(server);
				}
			}
		}
		List<ServerBriefInfo> result = new ArrayList<>(serverHostMap.size());
		for (Map.Entry<String, String> entry : serverHostMap.entrySet()) {
			result.add(getServerBriefInfo(aliveServers, crashedServers, entry.getKey(), entry.getValue()));
		}
		Collections.sort(result);
		return result;
	}

	/**
	 * 获取作业服务器部署的作业.
	 * 
	 * @param serverIp
	 *            作业服务器IP
	 * @return 作业服务器部署的作业
	 */
	public Collection<ServerInfo> getJobs(final String serverIp) {
		List<String> jobs = regCenter.getChildrenKeys("/");
		Collection<ServerInfo> result = new ArrayList<>(jobs.size());
		for (String each : jobs) {
			JobNodePathHelper jobNodePath = new JobNodePathHelper(each);
			if (regCenter.isExisted(jobNodePath.getServerNodePath(serverIp))) {
				result.add(getJob(serverIp, each));
			}
		}
		return result;
	}

	private ServerBriefInfo getServerBriefInfo(final Collection<String> aliveServers,
			final Collection<String> crashedServers, final String serverIp, final String hostName) {
		ServerBriefInfo result = new ServerBriefInfo();
		result.setServerIp(serverIp);
		result.setServerHostName(hostName);
		result.setStatus(ServerBriefInfo.ServerBriefStatus.getServerBriefStatus(aliveServers, crashedServers, serverIp));
		return result;
	}

	private ServerInfo getJob(final String serverIp, final String jobName) {
		ServerInfo result = new ServerInfo();
		JobNodePathHelper jobNodePath = new JobNodePathHelper(jobName);
		result.setJobName(jobName);
		result.setIp(serverIp);
		result.setHostName(regCenter.get(jobNodePath.getServerNodePath(serverIp, "hostName")));
		result.setSegment(regCenter.get(jobNodePath.getServerNodePath(serverIp, "segment")));
		String status = regCenter.get(jobNodePath.getServerNodePath(serverIp, "status"));
		boolean disabled = regCenter.isExisted(jobNodePath.getServerNodePath(serverIp, "disabled"));
		boolean paused = regCenter.isExisted(jobNodePath.getServerNodePath(serverIp, "paused"));
		boolean shutdown = regCenter.isExisted(jobNodePath.getServerNodePath(serverIp, "shutdown"));
		result.setStatus(ServerInfo.ServerStatus.getServerStatus(status, disabled, paused, shutdown));
		return result;
	}
}
