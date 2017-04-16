/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.executor.factory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import com.jd.framework.job.api.ScheduleJob;
import com.jd.framework.job.api.flow.FlowJob;
import com.jd.framework.job.api.simple.SimpleJob;
import com.jd.framework.job.exception.JobConfigurationException;
import com.jd.framework.job.executor.AbstractJobExecutor;
import com.jd.framework.job.executor.facade.JobFacade;
import com.jd.framework.job.executor.type.FlowJobExecutor;
import com.jd.framework.job.executor.type.SimpleJobExecutor;

/**
 * 
 * 分布式作业 Executor 工厂
 * <p>
 * 通过{@link #getJobExecutor(ScheduleJob, JobFacade)} 工厂方法获取实际执行器
 * </p>
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-15
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JobExecutorFactory {

	/**
	 * 获取作业执行器.
	 * 
	 * @param elasticJob
	 *            分布式弹性作业
	 * @param jobFacade
	 *            作业内部服务门面服务
	 * @return 作业执行器
	 */
	@SuppressWarnings("unchecked")
	public static AbstractJobExecutor getJobExecutor(final ScheduleJob scheduleJob, final JobFacade jobFacade) {

		if (scheduleJob instanceof SimpleJob) {
			return new SimpleJobExecutor((SimpleJob) scheduleJob, jobFacade);
		}
		if (scheduleJob instanceof FlowJob) {
			return new FlowJobExecutor((FlowJob) scheduleJob, jobFacade);
		}
		throw new JobConfigurationException("Cannot support job type '%s'", scheduleJob.getClass().getCanonicalName());
	}
}
