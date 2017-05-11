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
import java.util.List;

import com.jd.framework.job.constant.job.ServerStatus;
import com.jd.framework.job.core.internal.helper.JobNodeStorageHelper;
import com.jd.framework.job.core.internal.helper.ServerNodeHelper;
import com.jd.framework.job.regcenter.api.CoordinatorRegistryCenter;
import com.jd.framework.job.utils.env.LocalHostService;

/**
 * 
 * 作业服务器节点服务
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */
public class ServerService {
	private final JobNodeStorageHelper jobNodeStorage;

	private final LocalHostService localHostService = new LocalHostService();

	public ServerService(final CoordinatorRegistryCenter regCenter, final String jobName) {
		jobNodeStorage = new JobNodeStorageHelper(regCenter, jobName);
	}

	/**
	 * 每次作业启动前清理上次运行状态.
	 */
	public void clearPreviousServerStatus() {
		jobNodeStorage.removeJobNodeIfExisted(ServerNodeHelper.getStatusNode(localHostService.getIp()));
		jobNodeStorage.removeJobNodeIfExisted(ServerNodeHelper.getShutdownNode(localHostService.getIp()));
	}

	/**
	 * 持久化作业服务器上线相关信息.
	 * 
	 * @param enabled
	 *            作业是否启用
	 */
	public void persistServerOnline(final boolean enabled) {
		jobNodeStorage.fillJobNode(ServerNodeHelper.getHostNameNode(localHostService.getIp()),
				localHostService.getHostName());
		if (enabled) {
			jobNodeStorage.removeJobNodeIfExisted(ServerNodeHelper.getDisabledNode(localHostService.getIp()));
		} else {
			jobNodeStorage.fillJobNode(ServerNodeHelper.getDisabledNode(localHostService.getIp()), "");
		}
		// 创建临时节点
		jobNodeStorage.fillEphemeralJobNode(ServerNodeHelper.getStatusNode(localHostService.getIp()),
				ServerStatus.READY);
		jobNodeStorage.removeJobNodeIfExisted(ServerNodeHelper.getShutdownNode(localHostService.getIp()));
	}

	/**
	 * 清除立刻执行作业的标记.
	 */
	public void clearJobTriggerStatus() {
		jobNodeStorage.removeJobNodeIfExisted(ServerNodeHelper.getTriggerNode(localHostService.getIp()));
	}

	/**
	 * 清除暂停作业的标记.
	 */
	public void clearJobPausedStatus() {
		jobNodeStorage.removeJobNodeIfExisted(ServerNodeHelper.getPausedNode(localHostService.getIp()));
	}

	/**
	 * 判断是否是手工暂停的作业.
	 * 
	 * @return 是否是手工暂停的作业
	 */
	public boolean isJobPausedManually() {
		return jobNodeStorage.isJobNodeExisted(ServerNodeHelper.getPausedNode(localHostService.getIp()));
	}

	/**
	 * 处理服务器关机的相关信息.
	 */
	public void processServerShutdown() {
		jobNodeStorage.removeJobNodeIfExisted(ServerNodeHelper.getStatusNode(localHostService.getIp()));
	}

	/**
	 * 在开始或结束执行作业时更新服务器状态.
	 * 
	 * @param status
	 *            服务器状态
	 */
	public void updateServerStatus(final ServerStatus status) {
		jobNodeStorage.updateJobNode(ServerNodeHelper.getStatusNode(localHostService.getIp()), status);
	}

	/**
	 * 删除服务器状态.
	 */
	public void removeServerStatus() {
		jobNodeStorage.removeJobNodeIfExisted(ServerNodeHelper.getStatusNode(localHostService.getIp()));
	}

	/**
	 * 获取所有的作业服务器列表.
	 * 
	 * @return 所有的作业服务器列表
	 */
	public List<String> getAllServers() {
		List<String> result = jobNodeStorage.getJobNodeChildrenKeys(ServerNodeHelper.ROOT);
		Collections.sort(result);
		return result;
	}

	/**
	 * 获取可分段的作业服务器列表.
	 * 
	 * @return 可分段的作业服务器列表
	 */
	public List<String> getAvailableSegmentServers() {
		List<String> servers = getAllServers();
		List<String> result = new ArrayList<>(servers.size());
		for (String each : servers) {
			if (isAvailableSegmentServer(each)) {
				result.add(each);
			}
		}
		return result;
	}

	private boolean isAvailableSegmentServer(final String ip) {
		return jobNodeStorage.isJobNodeExisted(ServerNodeHelper.getStatusNode(ip))
				&& !jobNodeStorage.isJobNodeExisted(ServerNodeHelper.getDisabledNode(ip))
				&& !jobNodeStorage.isJobNodeExisted(ServerNodeHelper.getShutdownNode(ip));
	}

	/**
	 * 获取可用的作业服务器列表.
	 * 
	 * @return 可用的作业服务器列表
	 */
	public List<String> getAvailableServers() {
		List<String> servers = getAllServers();
		List<String> result = new ArrayList<>(servers.size());
		for (String each : servers) {
			if (isAvailableServer(each)) {
				result.add(each);
			}
		}
		return result;
	}

	/**
	 * 判断作业服务器是否可用.
	 * 
	 * @param ip
	 *            作业服务器IP地址.
	 * @return 作业服务器是否可用
	 */
	public boolean isAvailableServer(final String ip) {
		return jobNodeStorage.isJobNodeExisted(ServerNodeHelper.getStatusNode(ip))
				&& !jobNodeStorage.isJobNodeExisted(ServerNodeHelper.getPausedNode(ip))
				&& !jobNodeStorage.isJobNodeExisted(ServerNodeHelper.getDisabledNode(ip))
				&& !jobNodeStorage.isJobNodeExisted(ServerNodeHelper.getShutdownNode(ip));
	}

	/**
	 * 判断当前服务器是否是等待执行的状态.
	 * 
	 * @return 当前服务器是否是等待执行的状态
	 */
	public boolean isLocalhostServerReady() {
		String ip = localHostService.getIp();
		return isAvailableServer(ip)
				&& ServerStatus.READY.name().equals(jobNodeStorage.getJobNodeData(ServerNodeHelper.getStatusNode(ip)));
	}

	/**
	 * 判断当前服务器是否是启用状态.
	 * 
	 * @return 当前服务器是否是启用状态
	 */
	public boolean isLocalhostServerEnabled() {
		return !jobNodeStorage.isJobNodeExisted(ServerNodeHelper.getDisabledNode(localHostService.getIp()));
	}

	/**
	 * 判断作业服务器是否存在status节点.
	 * 
	 * @param ip
	 *            作业服务器IP
	 * @return 作业服务器是否存在status节点
	 */
	public boolean hasStatusNode(final String ip) {
		return this.jobNodeStorage.isJobNodeExisted(ServerNodeHelper.getStatusNode(ip));
	}
}
