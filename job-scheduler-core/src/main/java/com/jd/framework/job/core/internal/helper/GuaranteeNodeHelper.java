/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.core.internal.helper;

import com.google.common.base.Joiner;

/**
 * 
 * 保证分布式任务全部开始和结束状态节点名称节点工具助手
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */
public final class GuaranteeNodeHelper {

	public static final String ROOT = "guarantee";

	public static final String STARTED_ROOT = ROOT + "/started";

	public static final String COMPLETED_ROOT = ROOT + "/completed";

	private final JobNodePathHelper jobNodePath;

	public GuaranteeNodeHelper(final String jobName) {
		jobNodePath = new JobNodePathHelper(jobName);
	}

	public static String getStartedNode(final int shardingItem) {
		return Joiner.on("/").join(STARTED_ROOT, shardingItem);
	}

	public static String getCompletedNode(final int shardingItem) {
		return Joiner.on("/").join(COMPLETED_ROOT, shardingItem);
	}

	public boolean isStartedRootNode(final String path) {
		return jobNodePath.getFullPath(STARTED_ROOT).equals(path);
	}

	public boolean isCompletedRootNode(final String path) {
		return jobNodePath.getFullPath(COMPLETED_ROOT).equals(path);
	}
}
