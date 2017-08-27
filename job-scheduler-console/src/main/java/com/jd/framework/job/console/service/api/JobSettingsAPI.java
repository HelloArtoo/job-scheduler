/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.console.service.api;

import lombok.RequiredArgsConstructor;

import com.jd.framework.job.config.type.FlowJobConfiguration;
import com.jd.framework.job.console.domain.job.JobSettings;
import com.jd.framework.job.constant.job.JobType;
import com.jd.framework.job.core.config.FactJobConfiguration;
import com.jd.framework.job.core.internal.factory.FactJobConfigGsonFactory;
import com.jd.framework.job.core.internal.helper.JobNodePathHelper;
import com.jd.framework.job.executor.handler.JobProperties.JobPropertiesEnum;
import com.jd.framework.job.regcenter.api.CoordinatorRegistryCenter;

/**
 * 
 * 作业配置
 * 
 * @author Rong Hu
 * @version 1.0, 2017-7-1
 */
@RequiredArgsConstructor
public final class JobSettingsAPI {

	private final CoordinatorRegistryCenter regCenter;

	/**
	 * 获取作业设置.
	 * 
	 * @param jobName
	 *            作业名称
	 * @return 作业设置对象
	 */
	public JobSettings getJobSettings(final String jobName) {
		JobSettings result = new JobSettings();
		JobNodePathHelper jobNodePath = new JobNodePathHelper(jobName);
		FactJobConfiguration factJobConfig = FactJobConfigGsonFactory
				.fromJson(regCenter.get(jobNodePath.getConfigNodePath()));
		String jobType = factJobConfig.getTypeConfig().getJobType().name();
		buildSimpleJobSettings(jobName, result, factJobConfig);
		if (JobType.FLOW.name().equals(jobType)) {
			buildFlowJobSettings(result, (FlowJobConfiguration) factJobConfig.getTypeConfig());
		}

		return result;
	}

	/**
	 * 更新作业设置.
	 * 
	 * @param jobSettings
	 *            作业设置对象
	 */
	public void updateJobSettings(final JobSettings jobSettings) {
		JobNodePathHelper jobNodePath = new JobNodePathHelper(jobSettings.getJobName());
		regCenter.update(jobNodePath.getConfigNodePath(), FactJobConfigGsonFactory.toJsonForObject(jobSettings));
	}

	private void buildSimpleJobSettings(final String jobName, final JobSettings result,
			final FactJobConfiguration factJobConfig) {
		result.setJobName(jobName);
		result.setJobType(factJobConfig.getTypeConfig().getJobType().name());
		result.setJobClass(factJobConfig.getTypeConfig().getJobClass());
		result.setSegmentTotalCount(factJobConfig.getTypeConfig().getCoreConfig().getSegmentTotalCount());
		result.setCron(factJobConfig.getTypeConfig().getCoreConfig().getCron());
		result.setSegmentItemParameters(factJobConfig.getTypeConfig().getCoreConfig().getSegmentItemParameters());
		result.setJobParameter(factJobConfig.getTypeConfig().getCoreConfig().getJobParameter());
		result.setMonitorExecution(factJobConfig.isMonitorExecution());
		result.setMaxTimeDiffSeconds(factJobConfig.getMaxTimeDiffSeconds());
		result.setMonitorPort(factJobConfig.getMonitorPort());
		result.setFailover(factJobConfig.getTypeConfig().getCoreConfig().isFailover());
		result.setMisfire(factJobConfig.getTypeConfig().getCoreConfig().isMisfire());
		result.setJobSegmentStrategyClass(factJobConfig.getJobSegmentStrategyClass());
		result.setDescription(factJobConfig.getTypeConfig().getCoreConfig().getDescription());
		result.setReconcileIntervalMinutes(factJobConfig.getReconcileIntervalMinutes());
		result.getJobProperties().put(JobPropertiesEnum.EXECUTOR_SERVICE_HANDLER.getKey(), factJobConfig.getTypeConfig()
				.getCoreConfig().getJobProperties().get(JobPropertiesEnum.EXECUTOR_SERVICE_HANDLER));
		result.getJobProperties().put(JobPropertiesEnum.JOB_EXCEPTION_HANDLER.getKey(), factJobConfig.getTypeConfig()
				.getCoreConfig().getJobProperties().get(JobPropertiesEnum.JOB_EXCEPTION_HANDLER));
	}

	private void buildFlowJobSettings(final JobSettings result, final FlowJobConfiguration config) {
		result.setStreamingProcess(config.isStreamingProcess());
	}

}
