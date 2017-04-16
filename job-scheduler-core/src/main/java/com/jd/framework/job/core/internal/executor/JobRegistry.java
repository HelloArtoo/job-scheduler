/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.core.internal.executor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import com.jd.framework.job.core.internal.executor.quartz.JobScheduleController;

/**
 * 
 * 作业注册表
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JobRegistry {

	private static volatile JobRegistry instance;

	private Map<String, JobScheduleController> schedulerMap = new ConcurrentHashMap<>();

	/**
	 * 获取作业注册表实例.
	 * 
	 * @return 作业注册表实例
	 */
	public static JobRegistry getInstance() {
		if (null == instance) {
			synchronized (JobRegistry.class) {
				if (null == instance) {
					instance = new JobRegistry();
				}
			}
		}
		return instance;
	}

	/**
	 * 添加作业调度控制器.
	 * 
	 * @param jobName
	 *            作业名称
	 * @param jobScheduleController
	 *            作业调度控制器
	 */
	public void addJobScheduleController(final String jobName, final JobScheduleController jobScheduleController) {
		schedulerMap.put(jobName, jobScheduleController);
	}

	/**
	 * 获取作业调度控制器.
	 * 
	 * @param jobName
	 *            作业名称
	 * @return 作业调度控制器
	 */
	public JobScheduleController getJobScheduleController(final String jobName) {
		return schedulerMap.get(jobName);
	}
}
