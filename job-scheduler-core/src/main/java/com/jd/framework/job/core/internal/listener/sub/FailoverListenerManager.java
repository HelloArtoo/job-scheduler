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

import com.jd.framework.job.core.internal.factory.FactJobConfigGsonFactory;
import com.jd.framework.job.core.internal.helper.ConfigNodeHelper;
import com.jd.framework.job.core.internal.helper.ExecutionNodeHelper;
import com.jd.framework.job.core.internal.helper.FailoverNodeHelper;
import com.jd.framework.job.core.internal.listener.parent.AbstractJobListener;
import com.jd.framework.job.core.internal.listener.parent.AbstractListenerManager;
import com.jd.framework.job.core.internal.service.ConfigService;
import com.jd.framework.job.core.internal.service.ExecutionService;
import com.jd.framework.job.core.internal.service.FailoverService;
import com.jd.framework.job.core.internal.service.SegmentService;
import com.jd.framework.job.regcenter.api.CoordinatorRegistryCenter;

/**
 * 
 * 失效转移监听管理器
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */
public class FailoverListenerManager extends AbstractListenerManager {

	private final ConfigService configService;

	private final ExecutionService executionService;

	private final SegmentService segmentService;

	private final FailoverService failoverService;

	private final ConfigNodeHelper configNode;

	private final ExecutionNodeHelper executionNode;

	private final FailoverNodeHelper failoverNode;

	public FailoverListenerManager(final CoordinatorRegistryCenter regCenter, final String jobName) {
		super(regCenter, jobName);
		configService = new ConfigService(regCenter, jobName);
		executionService = new ExecutionService(regCenter, jobName);
		segmentService = new SegmentService(regCenter, jobName);
		failoverService = new FailoverService(regCenter, jobName);
		configNode = new ConfigNodeHelper(jobName);
		executionNode = new ExecutionNodeHelper(jobName);
		failoverNode = new FailoverNodeHelper(jobName);
	}

	@Override
	public void start() {
		addDataListener(new JobCrashedJobListener());
		addDataListener(new FailoverJobCrashedJobListener());
		addDataListener(new FailoverSettingsChangedJobListener());
	}

	private void failover(final Integer item, final TreeCacheEvent event) {
		if (!isJobCrashAndNeedFailover(item, event)) {
			return;
		}
		failoverService.setCrashedFailoverFlag(item);
		if (!executionService.hasRunningItems(segmentService.getLocalHostSegmentItems())) {
			failoverService.failoverIfNecessary();
		}
	}

	private boolean isJobCrashAndNeedFailover(final Integer item, final TreeCacheEvent event) {
		return null != item && Type.NODE_REMOVED == event.getType() && !executionService.isCompleted(item)
				&& configService.load(true).isFailover();
	}

	class JobCrashedJobListener extends AbstractJobListener {

		@Override
		protected void dataChanged(final CuratorFramework client, final TreeCacheEvent event, final String path) {
			failover(executionNode.getItemByRunningItemPath(path), event);
		}
	}

	/**
	 * 
	 * 作业失败监听器
	 * 
	 * @author Rong Hu
	 * @version 1.0, 2017-4-9
	 */
	class FailoverJobCrashedJobListener extends AbstractJobListener {

		@Override
		protected void dataChanged(final CuratorFramework client, final TreeCacheEvent event, final String path) {
			failover(failoverNode.getItemByExecutionFailoverPath(path), event);
		}
	}

	/**
	 * 
	 * 作业配置更新监听器
	 * 
	 * @author Rong Hu
	 * @version 1.0, 2017-4-9
	 */
	class FailoverSettingsChangedJobListener extends AbstractJobListener {

		@Override
		protected void dataChanged(final CuratorFramework client, final TreeCacheEvent event, final String path) {
			if (configNode.isConfigPath(path) && Type.NODE_UPDATED == event.getType()
					&& !FactJobConfigGsonFactory.fromJson(new String(event.getData().getData())).isFailover()) {
				failoverService.removeFailoverInfo();
			}
		}
	}

}
