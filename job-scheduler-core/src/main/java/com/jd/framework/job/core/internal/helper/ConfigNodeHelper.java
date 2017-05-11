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
 * 根配置服务助手
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-6
 */
public final class ConfigNodeHelper {
	/** 配置服务根路径 */
	public static final String ROOT = "config";

	/** JobNode Helper */
	private final JobNodePathHelper jobNodePath;

	public ConfigNodeHelper(final String jobName) {
		jobNodePath = new JobNodePathHelper(jobName);
	}

	/**
	 * 判断是否为作业配置根路径.
	 * 
	 * @param path
	 *            节点路径
	 * @return 是否为作业配置根路径
	 */
	public boolean isConfigPath(final String path) {
		return jobNodePath.getConfigNodePath().equals(path);
	}
}
