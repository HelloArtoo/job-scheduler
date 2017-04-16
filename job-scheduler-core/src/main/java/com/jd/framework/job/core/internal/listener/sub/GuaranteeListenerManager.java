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

import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent.Type;

import com.jd.framework.job.core.api.listener.AbstractOneOffJobListener;
import com.jd.framework.job.core.api.listener.ScheduleJobListener;
import com.jd.framework.job.core.internal.helper.GuaranteeNodeHelper;
import com.jd.framework.job.core.internal.listener.parent.AbstractJobListener;
import com.jd.framework.job.core.internal.listener.parent.AbstractListenerManager;
import com.jd.framework.job.regcenter.api.CoordinatorRegistryCenter;

/**
 * 
 * 分布式服务监听管理器
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */
public class GuaranteeListenerManager extends AbstractListenerManager {

	private final GuaranteeNodeHelper guaranteeNode;

	private final List<ScheduleJobListener> scheduleJobListener;

	public GuaranteeListenerManager(final CoordinatorRegistryCenter regCenter, final String jobName,
			final List<ScheduleJobListener> scheduleJobListener) {
		super(regCenter, jobName);
		this.guaranteeNode = new GuaranteeNodeHelper(jobName);
		this.scheduleJobListener = scheduleJobListener;
	}

	@Override
	public void start() {
		addDataListener(new StartedNodeRemovedJobListener());
		addDataListener(new CompletedNodeRemovedJobListener());
	}

	class StartedNodeRemovedJobListener extends AbstractJobListener {

		@Override
		protected void dataChanged(final CuratorFramework client, final TreeCacheEvent event, final String path) {
			if (Type.NODE_REMOVED == event.getType() && guaranteeNode.isStartedRootNode(path)) {
				for (ScheduleJobListener each : scheduleJobListener) {
					if (each instanceof AbstractOneOffJobListener) {
						((AbstractOneOffJobListener) each).notifyWaitingTaskStart();
					}
				}
			}
		}
	}

	class CompletedNodeRemovedJobListener extends AbstractJobListener {

		@Override
		protected void dataChanged(final CuratorFramework client, final TreeCacheEvent event, final String path) {
			if (Type.NODE_REMOVED == event.getType() && guaranteeNode.isCompletedRootNode(path)) {
				for (ScheduleJobListener each : scheduleJobListener) {
					if (each instanceof AbstractOneOffJobListener) {
						((AbstractOneOffJobListener) each).notifyWaitingTaskComplete();
					}
				}
			}
		}
	}

}
