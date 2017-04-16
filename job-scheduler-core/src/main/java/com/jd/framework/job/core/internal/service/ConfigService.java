/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.core.internal.service;

import com.google.common.base.Optional;
import com.jd.framework.job.core.config.FactJobConfiguration;
import com.jd.framework.job.core.internal.factory.FactJobConfigGsonFactory;
import com.jd.framework.job.core.internal.helper.ConfigNodeHelper;
import com.jd.framework.job.core.internal.helper.JobNodeStorageHelper;
import com.jd.framework.job.exception.JobConfigurationException;
import com.jd.framework.job.exception.JobExecutionEnvironmentException;
import com.jd.framework.job.regcenter.api.CoordinatorRegistryCenter;

/**
 * 
 * 分布式作业服务提供
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-6
 */
public class ConfigService {

	private final JobNodeStorageHelper jobNodeStorageHelper;

	public ConfigService(final CoordinatorRegistryCenter regCenter, final String jobName) {
		jobNodeStorageHelper = new JobNodeStorageHelper(regCenter, jobName);
	}

	/**
	 * 读取作业配置
	 * 
	 * @param fromCache
	 * @return
	 * @author Rong Hu
	 */
	public FactJobConfiguration load(final boolean fromCache) {
		String result;
		if (fromCache) {
			result = jobNodeStorageHelper.getJobNodeData(ConfigNodeHelper.ROOT);
			if (null == result) {
				result = jobNodeStorageHelper.getJobNodeDataDirectly(ConfigNodeHelper.ROOT);
			}
		} else {
			result = jobNodeStorageHelper.getJobNodeDataDirectly(ConfigNodeHelper.ROOT);
		}
		return FactJobConfigGsonFactory.fromJson(result);
	}

	/**
	 * 持久化分布式作业配置信息.
	 * 
	 * @param factJobConfig
	 *            作业配置
	 */
	public void persist(final FactJobConfiguration factJobConfig) {
		checkConflictJob(factJobConfig);
		if (!jobNodeStorageHelper.isJobNodeExisted(ConfigNodeHelper.ROOT) || factJobConfig.isOverwrite()) {
			jobNodeStorageHelper.replaceJobNode(ConfigNodeHelper.ROOT, FactJobConfigGsonFactory.toJson(factJobConfig));
		}
	}

	private void checkConflictJob(final FactJobConfiguration factJobConfig) {
		Optional<FactJobConfiguration> factJobConfigFromZk = find();
		if (factJobConfigFromZk.isPresent()
				&& !factJobConfigFromZk.get().getTypeConfig().getJobClass()
						.equals(factJobConfig.getTypeConfig().getJobClass())) {
			throw new JobConfigurationException(
					"Job conflict with register center. The job '%s' in register center's class is '%s', your job class is '%s'",
					factJobConfig.getJobName(), factJobConfigFromZk.get().getTypeConfig().getJobClass(), factJobConfig
							.getTypeConfig().getJobClass());
		}
	}

	private Optional<FactJobConfiguration> find() {
		if (!jobNodeStorageHelper.isJobNodeExisted(ConfigNodeHelper.ROOT)) {
			return Optional.absent();
		}
		FactJobConfiguration result = FactJobConfigGsonFactory.fromJson(jobNodeStorageHelper
				.getJobNodeDataDirectly(ConfigNodeHelper.ROOT));
		if (null == result) {
			// TODO 应该删除整个job node,并非仅仅删除config node
			jobNodeStorageHelper.removeJobNodeIfExisted(ConfigNodeHelper.ROOT);
		}
		return Optional.fromNullable(result);
	}

	/**
	 * 检查本机与注册中心的时间误差秒数是否在允许范围.
	 * 
	 * @throws JobExecutionEnvironmentException
	 *             本机与注册中心的时间误差秒数不在允许范围所抛出的异常
	 */
	public void checkMaxTimeDiffSecondsTolerable() throws JobExecutionEnvironmentException {
		int maxTimeDiffSeconds = load(true).getMaxTimeDiffSeconds();
		if (-1 == maxTimeDiffSeconds) {
			return;
		}
		long timeDiff = Math.abs(System.currentTimeMillis() - jobNodeStorageHelper.getRegistryCenterTime());
		if (timeDiff > maxTimeDiffSeconds * 1000L) {
			throw new JobExecutionEnvironmentException(
					"Time different between job server and register center exceed '%s' seconds, max time different is '%s' seconds.",
					Long.valueOf(timeDiff / 1000).intValue(), maxTimeDiffSeconds);
		}
	}

}
