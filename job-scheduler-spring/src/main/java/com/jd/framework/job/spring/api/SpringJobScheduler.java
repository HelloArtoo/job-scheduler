/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.spring.api;

import com.google.common.base.Optional;
import com.jd.framework.job.api.ScheduleJob;
import com.jd.framework.job.core.api.JobScheduler;
import com.jd.framework.job.core.api.listener.ScheduleJobListener;
import com.jd.framework.job.core.config.FactJobConfiguration;
import com.jd.framework.job.event.JobEventConfiguration;
import com.jd.framework.job.regcenter.api.CoordinatorRegistryCenter;
import com.jd.framework.job.spring.util.AopTargetUtils;

/**
 * 
 * 基于Spring的Job调度中心
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-15
 */
public class SpringJobScheduler extends JobScheduler {

	private final ScheduleJob scheduleJob;

	public SpringJobScheduler(final ScheduleJob scheduleJob, final CoordinatorRegistryCenter regCenter,
			final FactJobConfiguration jobConfig, final ScheduleJobListener... scheduleJobListeners) {
		super(regCenter, jobConfig, getTargetScheduleJobListeners(scheduleJobListeners));
		this.scheduleJob = scheduleJob;
	}

	public SpringJobScheduler(final ScheduleJob scheduleJob, final CoordinatorRegistryCenter regCenter,
			final FactJobConfiguration jobConfig, final JobEventConfiguration jobEventConfig,
			final ScheduleJobListener... scheduleJobListeners) {
		super(regCenter, jobConfig, jobEventConfig, getTargetScheduleJobListeners(scheduleJobListeners));
		this.scheduleJob = scheduleJob;
	}

	private static ScheduleJobListener[] getTargetScheduleJobListeners(final ScheduleJobListener[] scheduleJobListeners) {
		final ScheduleJobListener[] result = new ScheduleJobListener[scheduleJobListeners.length];
		for (int i = 0; i < scheduleJobListeners.length; i++) {
			result[i] = (ScheduleJobListener) AopTargetUtils.getTarget(scheduleJobListeners[i]);
		}
		return result;
	}

	@Override
	protected Optional<ScheduleJob> createScheduleJobInstance() {
		return Optional.fromNullable(scheduleJob);
	}

}
