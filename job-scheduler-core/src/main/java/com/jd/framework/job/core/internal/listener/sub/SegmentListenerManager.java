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

import lombok.Setter;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent.Type;

import com.jd.framework.job.core.internal.factory.FactJobConfigGsonFactory;
import com.jd.framework.job.core.internal.helper.ConfigNodeHelper;
import com.jd.framework.job.core.internal.helper.ServerNodeHelper;
import com.jd.framework.job.core.internal.listener.parent.AbstractJobListener;
import com.jd.framework.job.core.internal.listener.parent.AbstractListenerManager;
import com.jd.framework.job.core.internal.service.ExecutionService;
import com.jd.framework.job.core.internal.service.SegmentService;
import com.jd.framework.job.regcenter.api.CoordinatorRegistryCenter;

/**
 * 
 * 作业分段监听管理器
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */
public class SegmentListenerManager extends AbstractListenerManager {

	private final SegmentService segmentService;

	private final ExecutionService executionService;

	private final ConfigNodeHelper configNode;

	private final ServerNodeHelper serverNode;

	@Setter
	private int currentSegmentTotalCount;

	public SegmentListenerManager(final CoordinatorRegistryCenter regCenter, final String jobName) {
		super(regCenter, jobName);
		segmentService = new SegmentService(regCenter, jobName);
		executionService = new ExecutionService(regCenter, jobName);
		configNode = new ConfigNodeHelper(jobName);
		serverNode = new ServerNodeHelper(jobName);
	}

	@Override
	public void start() {
		addDataListener(new SegmentTotalCountChangedJobListener());
		addDataListener(new ListenServersChangedJobListener());
	}

	/**
	 * 
	 * 分段数变化监听器
	 * 
	 * @author Rong Hu
	 * @version 1.0, 2017-4-9
	 */
	class SegmentTotalCountChangedJobListener extends AbstractJobListener {

		@Override
		protected void dataChanged(final CuratorFramework client, final TreeCacheEvent event, final String path) {
			if (configNode.isConfigPath(path) && 0 != currentSegmentTotalCount) {
				int newSegmentTotalCount = FactJobConfigGsonFactory.fromJson(new String(event.getData().getData()))
						.getTypeConfig().getCoreConfig().getSegmentTotalCount();
				if (newSegmentTotalCount != currentSegmentTotalCount) {
					segmentService.setResegmentFlag();
					executionService.setNeedFixExecutionInfoFlag();
					currentSegmentTotalCount = newSegmentTotalCount;
				}
			}
		}
	}

	/**
	 * 
	 * 做业服务器监听器
	 * 
	 * @author Rong Hu
	 * @version 1.0, 2017-4-9
	 */
	class ListenServersChangedJobListener extends AbstractJobListener {

		@Override
		protected void dataChanged(final CuratorFramework client, final TreeCacheEvent event, final String path) {
			if (isServersCrashed(event, path) || serverNode.isServerDisabledPath(path)
					|| serverNode.isServerShutdownPath(path)) {
				segmentService.setResegmentFlag();
			}
		}

		private boolean isServersCrashed(final TreeCacheEvent event, final String path) {
			return serverNode.isServerStatusPath(path) && Type.NODE_UPDATED != event.getType();
		}
	}
}
