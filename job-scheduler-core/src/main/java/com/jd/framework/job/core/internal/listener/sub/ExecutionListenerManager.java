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
import com.jd.framework.job.core.internal.listener.parent.AbstractJobListener;
import com.jd.framework.job.core.internal.listener.parent.AbstractListenerManager;
import com.jd.framework.job.core.internal.service.ExecutionService;
import com.jd.framework.job.regcenter.api.CoordinatorRegistryCenter;

/**
 * 
 * 运行时状态监听器
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */
public class ExecutionListenerManager extends AbstractListenerManager {

	private final ExecutionService executionService;

	private final ConfigNodeHelper configNode;

	public ExecutionListenerManager(final CoordinatorRegistryCenter regCenter, final String jobName) {
		super(regCenter, jobName);
		executionService = new ExecutionService(regCenter, jobName);
		configNode = new ConfigNodeHelper(jobName);
	}

	@Override
	public void start() {
		addDataListener(new MonitorExecutionChangedJobListener());
	}

	/**
	 * 
	 * 执行变化监听器
	 * 
	 * @author Rong Hu
	 * @version 1.0, 2017-4-9
	 */
	class MonitorExecutionChangedJobListener extends AbstractJobListener {

		@Override
		protected void dataChanged(final CuratorFramework client, final TreeCacheEvent event, final String path) {
			if (configNode.isConfigPath(path) && Type.NODE_UPDATED == event.getType()
					&& !FactJobConfigGsonFactory.fromJson(new String(event.getData().getData())).isMonitorExecution()) {
				executionService.removeExecutionInfo();
			}
		}
	}

}
