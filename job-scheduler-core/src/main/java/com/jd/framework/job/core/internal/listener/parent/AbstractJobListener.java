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

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;

/**
 * 注册中心作业监听器抽象
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */
public abstract class AbstractJobListener implements TreeCacheListener {
	@Override
	public final void childEvent(final CuratorFramework client, final TreeCacheEvent event) throws Exception {
		String path = null == event.getData() ? "" : event.getData().getPath();
		if (path.isEmpty()) {
			return;
		}
		dataChanged(client, event, path);
	}

	protected abstract void dataChanged(final CuratorFramework client, final TreeCacheEvent event, final String path);
}
