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

/**
 * 
 * 失效转移节点工具助手
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */
public final class FailoverNodeHelper {

	public static final String FAILOVER = "failover";

	public static final String LEADER_ROOT = ElectionNodeHelper.ROOT + "/" + FAILOVER;

	public static final String ITEMS_ROOT = LEADER_ROOT + "/items";

	public static final String ITEMS = ITEMS_ROOT + "/%s";

	public static final String LATCH = LEADER_ROOT + "/latch";

	private static final String EXECUTION_FAILOVER = ExecutionNodeHelper.ROOT + "/%s/" + FAILOVER;

	private final JobNodePathHelper jobNodePath;

	public FailoverNodeHelper(final String jobName) {
		jobNodePath = new JobNodePathHelper(jobName);
	}

	public static String getItemsNode(final int item) {
		return String.format(ITEMS, item);
	}

	public static String getExecutionFailoverNode(final int item) {
		return String.format(EXECUTION_FAILOVER, item);
	}

	/**
	 * 根据失效转移执行路径获取分段项.
	 * 
	 * @param path
	 *            失效转移执行路径
	 * @return 分段项, 不是失效转移执行路径获则返回null
	 */
	public Integer getItemByExecutionFailoverPath(final String path) {
		if (!isFailoverPath(path)) {
			return null;
		}
		return Integer.parseInt(path.substring(jobNodePath.getFullPath(ExecutionNodeHelper.ROOT).length() + 1,
				path.lastIndexOf(FailoverNodeHelper.FAILOVER) - 1));
	}

	private boolean isFailoverPath(final String path) {
		return path.startsWith(jobNodePath.getFullPath(ExecutionNodeHelper.ROOT))
				&& path.endsWith(FailoverNodeHelper.FAILOVER);
	}
}
