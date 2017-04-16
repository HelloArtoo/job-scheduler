/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.core.internal.facade;

import java.util.List;

import com.jd.framework.job.core.api.listener.ScheduleJobListener;
import com.jd.framework.job.core.config.FactJobConfiguration;
import com.jd.framework.job.core.internal.executor.quartz.JobTriggerListener;
import com.jd.framework.job.core.internal.listener.ListenerManager;
import com.jd.framework.job.core.internal.service.ConfigService;
import com.jd.framework.job.core.internal.service.ExecutionService;
import com.jd.framework.job.core.internal.service.LeaderElectionService;
import com.jd.framework.job.core.internal.service.MonitorService;
import com.jd.framework.job.core.internal.service.ReconcileService;
import com.jd.framework.job.core.internal.service.SegmentService;
import com.jd.framework.job.core.internal.service.ServerService;
import com.jd.framework.job.regcenter.api.CoordinatorRegistryCenter;

/**
 * 
 * 服务门面类
 * <p>
 * 集成所需服务类，注册各式启动信息
 * </p>
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-6
 */
public class SchedulerFacade {

	private final ConfigService configService;

	private final LeaderElectionService leaderElectionService;

	private final ServerService serverService;

	private final SegmentService segmentService;

	private final ExecutionService executionService;

	private final MonitorService monitorService;

	private final ListenerManager listenerManager;

	private final ReconcileService reconcileService;

	public SchedulerFacade(final CoordinatorRegistryCenter regCenter, final String jobName,
			final List<ScheduleJobListener> scheduleJobListeners) {
		configService = new ConfigService(regCenter, jobName);
		leaderElectionService = new LeaderElectionService(regCenter, jobName);
		serverService = new ServerService(regCenter, jobName);
		segmentService = new SegmentService(regCenter, jobName);
		executionService = new ExecutionService(regCenter, jobName);
		monitorService = new MonitorService(regCenter, jobName);
		reconcileService = new ReconcileService(regCenter, jobName);
		listenerManager = new ListenerManager(regCenter, jobName, scheduleJobListeners);
	}

	/**
	 * 每次作业启动前清理上次运行状态.
	 */
	public void clearPreviousServerStatus() {
		serverService.clearPreviousServerStatus();
	}

	/**
	 * 注册Fact-Job启动信息.
	 * 
	 * @param liteJobConfig
	 *            作业配置
	 */
	public void registerStartUpInfo(final FactJobConfiguration factJobConfig) {
		listenerManager.startAllListeners();
		leaderElectionService.leaderForceElection();
		configService.persist(factJobConfig);
		serverService.persistServerOnline(!factJobConfig.isDisabled());
		serverService.clearJobPausedStatus();
		segmentService.setResegmentFlag();
		monitorService.listen();
		listenerManager.setCurrentSegmentTotalCount(configService.load(false).getTypeConfig().getCoreConfig()
				.getSegmentTotalCount());
		reconcileService.startAsync();
	}

	/**
	 * 释放作业占用的资源.
	 */
	public void releaseJobResource() {
		monitorService.close();
		serverService.removeServerStatus();
	}

	/**
	 * 读取作业配置.
	 * 
	 * @return 作业配置
	 */
	public FactJobConfiguration loadJobConfiguration() {
		return configService.load(false);
	}

	/**
	 * 获取作业触发监听器.
	 * 
	 * @return 作业触发监听器
	 */
	public JobTriggerListener newJobTriggerListener() {
		return new JobTriggerListener(executionService, segmentService);
	}
}
