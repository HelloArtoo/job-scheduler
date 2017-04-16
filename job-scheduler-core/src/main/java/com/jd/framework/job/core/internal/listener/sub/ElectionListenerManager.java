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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent.Type;

import com.jd.framework.job.core.internal.helper.ElectionNodeHelper;
import com.jd.framework.job.core.internal.helper.ServerNodeHelper;
import com.jd.framework.job.core.internal.listener.parent.AbstractJobListener;
import com.jd.framework.job.core.internal.listener.parent.AbstractListenerManager;
import com.jd.framework.job.core.internal.service.LeaderElectionService;
import com.jd.framework.job.core.internal.service.ServerService;
import com.jd.framework.job.regcenter.api.CoordinatorRegistryCenter;

/**
 * 
 * 主节点选举监听管理器
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */
@Slf4j
public class ElectionListenerManager extends AbstractListenerManager {

	private final LeaderElectionService leaderElectionService;

	private final ServerService serverService;

	private final ElectionNodeHelper electionNode;

	private final ServerNodeHelper serverNode;

	public ElectionListenerManager(final CoordinatorRegistryCenter regCenter, final String jobName) {
		super(regCenter, jobName);
		leaderElectionService = new LeaderElectionService(regCenter, jobName);
		serverService = new ServerService(regCenter, jobName);
		electionNode = new ElectionNodeHelper(jobName);
		serverNode = new ServerNodeHelper(jobName);
	}

	@Override
	public void start() {
		addDataListener(new LeaderElectionJobListener());
	}

	class LeaderElectionJobListener extends AbstractJobListener {

		@Override
		protected void dataChanged(final CuratorFramework client, final TreeCacheEvent event, final String path) {
			EventHelper eventHelper = new EventHelper(path, event);
			if (eventHelper.isLeaderCrashedOrServerOn() && !leaderElectionService.hasLeader()
					&& !serverService.getAvailableServers().isEmpty()) {
				log.debug("Leader crashed, elect a new leader now.");
				leaderElectionService.leaderElection();
				log.debug("Leader election completed.");
				return;
			}
			if (eventHelper.isServerOff() && leaderElectionService.isLeader()) {
				leaderElectionService.removeLeader();
			}
		}

		@RequiredArgsConstructor
		final class EventHelper {

			private final String path;

			private final TreeCacheEvent event;

			boolean isLeaderCrashedOrServerOn() {
				return isLeaderCrashed() || isServerEnabled() || isServerResumed();
			}

			private boolean isLeaderCrashed() {
				return electionNode.isLeaderHostPath(path) && Type.NODE_REMOVED == event.getType();
			}

			private boolean isServerEnabled() {
				return serverNode.isLocalServerDisabledPath(path) && Type.NODE_REMOVED == event.getType();
			}

			private boolean isServerResumed() {
				return serverNode.isLocalJobPausedPath(path) && Type.NODE_REMOVED == event.getType();
			}

			boolean isServerOff() {
				return isServerDisabled() || isServerPaused() || isServerShutdown();
			}

			private boolean isServerDisabled() {
				return serverNode.isLocalServerDisabledPath(path) && Type.NODE_ADDED == event.getType();
			}

			private boolean isServerPaused() {
				return serverNode.isLocalJobPausedPath(path) && Type.NODE_ADDED == event.getType();
			}

			private boolean isServerShutdown() {
				return serverNode.isLocalJobShutdownPath(path) && Type.NODE_ADDED == event.getType();
			}
		}
	}

}
