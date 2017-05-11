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
 * 选举服务工具助手（根节点）
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */
public final class ElectionNodeHelper {
	/**
	 * Job Scheduler 主服务器根节点.
	 */
	public static final String ROOT = "leader";

	public static final String ELECTION_ROOT = ROOT + "/election";

	public static final String LEADER_HOST = ELECTION_ROOT + "/host";

	public static final String LATCH = ELECTION_ROOT + "/latch";

	private final JobNodePathHelper jobNodePathHelper;

	public ElectionNodeHelper(final String jobName) {
		jobNodePathHelper = new JobNodePathHelper(jobName);
	}

	public boolean isLeaderHostPath(final String path) {
		return jobNodePathHelper.getFullPath(LEADER_HOST).equals(path);
	}
}
