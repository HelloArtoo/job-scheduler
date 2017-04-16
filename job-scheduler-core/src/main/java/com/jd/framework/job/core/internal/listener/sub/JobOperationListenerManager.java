/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.core.internal.listener.sub;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent.Type;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;

import com.jd.framework.job.core.internal.executor.JobRegistry;
import com.jd.framework.job.core.internal.executor.quartz.JobScheduleController;
import com.jd.framework.job.core.internal.helper.ServerNodeHelper;
import com.jd.framework.job.core.internal.listener.parent.AbstractJobListener;
import com.jd.framework.job.core.internal.listener.parent.AbstractListenerManager;
import com.jd.framework.job.core.internal.service.ExecutionService;
import com.jd.framework.job.core.internal.service.SegmentService;
import com.jd.framework.job.core.internal.service.ServerService;
import com.jd.framework.job.regcenter.api.CoordinatorRegistryCenter;

/**
 * 
 * 作业操作监听管理器
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */
public class JobOperationListenerManager extends AbstractListenerManager {

	private final String jobName;

	private final ServerNodeHelper serverNode;

	private final ServerService serverService;

	private final SegmentService segmentService;

	private final ExecutionService executionService;

	public JobOperationListenerManager(final CoordinatorRegistryCenter regCenter, final String jobName) {
		super(regCenter, jobName);
		this.jobName = jobName;
		serverNode = new ServerNodeHelper(jobName);
		serverService = new ServerService(regCenter, jobName);
		segmentService = new SegmentService(regCenter, jobName);
		executionService = new ExecutionService(regCenter, jobName);
	}

	@Override
	public void start() {
		addConnectionStateListener(new ConnectionLostListener());
		addDataListener(new JobTriggerStatusJobListener());
		addDataListener(new JobPausedStatusJobListener());
		addDataListener(new JobShutdownStatusJobListener());
	}

	/**
	 * 
	 * 丢失连接监听器
	 * 
	 * @author Rong Hu
	 * @version 1.0, 2017-4-9
	 */
	class ConnectionLostListener implements ConnectionStateListener {

		@Override
		public void stateChanged(final CuratorFramework client, final ConnectionState newState) {
			JobScheduleController jobScheduleController = JobRegistry.getInstance().getJobScheduleController(jobName);
			if (ConnectionState.LOST == newState) {
				jobScheduleController.pauseJob();
			} else if (ConnectionState.RECONNECTED == newState) {
				serverService.persistServerOnline(serverService.isLocalhostServerEnabled());
				executionService.clearRunningInfo(segmentService.getLocalHostSegmentItems());
				if (!serverService.isJobPausedManually()) {
					jobScheduleController.resumeJob();
				}
			}
		}
	}

	/**
	 * 作业触发状态监听器
	 * 
	 * @author Rong Hu
	 * @version 1.0, 2017-4-9
	 */
	class JobTriggerStatusJobListener extends AbstractJobListener {

		@Override
		protected void dataChanged(final CuratorFramework client, final TreeCacheEvent event, final String path) {
			if (Type.NODE_ADDED != event.getType() || !serverNode.isLocalJobTriggerPath(path)) {
				return;
			}
			serverService.clearJobTriggerStatus();
			JobScheduleController jobScheduleController = JobRegistry.getInstance().getJobScheduleController(jobName);
			if (null == jobScheduleController) {
				return;
			}
			if (serverService.isLocalhostServerReady()) {
				jobScheduleController.triggerJob();
			}
		}
	}

	/**
	 * 
	 * 作业暂停状态监听器
	 * 
	 * @author Rong Hu
	 * @version 1.0, 2017-4-9
	 */
	class JobPausedStatusJobListener extends AbstractJobListener {

		@Override
		protected void dataChanged(final CuratorFramework client, final TreeCacheEvent event, final String path) {
			if (!serverNode.isLocalJobPausedPath(path)) {
				return;
			}
			JobScheduleController jobScheduleController = JobRegistry.getInstance().getJobScheduleController(jobName);
			if (null == jobScheduleController) {
				return;
			}
			if (Type.NODE_ADDED == event.getType()) {
				jobScheduleController.pauseJob();
			}
			if (Type.NODE_REMOVED == event.getType()) {
				jobScheduleController.resumeJob();
				serverService.clearJobPausedStatus();
			}
		}
	}

	/**
	 * 
	 * 作业关闭状态监听器
	 * 
	 * @author Rong Hu
	 * @version 1.0, 2017-4-9
	 */
	class JobShutdownStatusJobListener extends AbstractJobListener {

		@Override
		protected void dataChanged(final CuratorFramework client, final TreeCacheEvent event, final String path) {
			if (!serverNode.isLocalJobShutdownPath(path)) {
				return;
			}
			JobScheduleController jobScheduleController = JobRegistry.getInstance().getJobScheduleController(jobName);
			if (null != jobScheduleController && Type.NODE_ADDED == event.getType()) {
				jobScheduleController.shutdown();
				serverService.processServerShutdown();
			}
		}
	}

}
