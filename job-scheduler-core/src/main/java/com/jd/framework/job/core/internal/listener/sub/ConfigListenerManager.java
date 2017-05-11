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

import com.jd.framework.job.core.config.FactJobConfiguration;
import com.jd.framework.job.core.internal.executor.JobRegistry;
import com.jd.framework.job.core.internal.executor.quartz.JobScheduleController;
import com.jd.framework.job.core.internal.factory.FactJobConfigGsonFactory;
import com.jd.framework.job.core.internal.helper.ConfigNodeHelper;
import com.jd.framework.job.core.internal.listener.parent.AbstractJobListener;
import com.jd.framework.job.core.internal.listener.parent.AbstractListenerManager;
import com.jd.framework.job.regcenter.api.CoordinatorRegistryCenter;

/**
 * 
 * 作业配置监听管理器
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */
public class ConfigListenerManager extends AbstractListenerManager {

	private final ConfigNodeHelper configNode;

	private final String jobName;

	public ConfigListenerManager(final CoordinatorRegistryCenter regCenter, final String jobName) {
		super(regCenter, jobName);
		this.jobName = jobName;
		configNode = new ConfigNodeHelper(jobName);
	}

	@Override
	public void start() {
		addDataListener(new CronSettingAndJobEventChangedJobListener());
	}

	/**
	 * 
	 * 作业配置监听器
	 * 
	 * @author Rong Hu
	 * @version 1.0, 2017-4-9
	 */
	class CronSettingAndJobEventChangedJobListener extends AbstractJobListener {

		/**
		 * 重新调度作业
		 */
		@Override
		protected void dataChanged(final CuratorFramework client, final TreeCacheEvent event, final String path) {
			if (configNode.isConfigPath(path) && Type.NODE_UPDATED == event.getType()) {
				JobScheduleController jobScheduler = JobRegistry.getInstance().getJobScheduleController(jobName);
				if (null == jobScheduler) {
					return;
				}
				FactJobConfiguration factJobConfiguration = FactJobConfigGsonFactory.fromJson(new String(event
						.getData().getData()));
				jobScheduler.rescheduleJob(factJobConfiguration.getTypeConfig().getCoreConfig().getCron());
			}
		}
	}

}
