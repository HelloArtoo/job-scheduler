/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.core.internal.listener.parent;

import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.framework.state.ConnectionStateListener;

import com.jd.framework.job.core.internal.helper.JobNodeStorageHelper;
import com.jd.framework.job.regcenter.api.CoordinatorRegistryCenter;

/**
 * 
 * 注册中心监听管理器父类
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */
public abstract class AbstractListenerManager {

	private final JobNodeStorageHelper jobNodeStorage;

	protected AbstractListenerManager(final CoordinatorRegistryCenter regCenter, final String jobName) {
		jobNodeStorage = new JobNodeStorageHelper(regCenter, jobName);
	}

	/**
	 * 开启监听器.
	 */
	public abstract void start();

	protected void addDataListener(final TreeCacheListener listener) {
		jobNodeStorage.addDataListener(listener);
	}

	protected void addConnectionStateListener(final ConnectionStateListener listener) {
		jobNodeStorage.addConnectionStateListener(listener);
	}
}
