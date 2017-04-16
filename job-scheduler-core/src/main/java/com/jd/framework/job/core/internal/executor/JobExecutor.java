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

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import com.jd.framework.job.core.api.listener.AbstractOneOffJobListener;
import com.jd.framework.job.core.api.listener.ScheduleJobListener;
import com.jd.framework.job.core.config.FactJobConfiguration;
import com.jd.framework.job.core.internal.facade.SchedulerFacade;
import com.jd.framework.job.core.internal.service.GuaranteeService;
import com.jd.framework.job.regcenter.api.CoordinatorRegistryCenter;

/**
 * 
 * 核心作业启动器
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */
@Slf4j
public class JobExecutor {

	private final FactJobConfiguration factJobConfig;

	private final CoordinatorRegistryCenter regCenter;

	@Getter
	private final SchedulerFacade schedulerFacade;

	public JobExecutor(final CoordinatorRegistryCenter regCenter, final FactJobConfiguration factJobConfig,
			final ScheduleJobListener... scheduleJobListeners) {
		this.factJobConfig = factJobConfig;
		this.regCenter = regCenter;
		List<ScheduleJobListener> factJobListenerList = Arrays.asList(scheduleJobListeners);
		setGuaranteeServiceForScheduleJobListeners(regCenter, factJobListenerList);
		schedulerFacade = new SchedulerFacade(regCenter, factJobConfig.getJobName(), factJobListenerList);
	}

	private void setGuaranteeServiceForScheduleJobListeners(final CoordinatorRegistryCenter regCenter,
			final List<ScheduleJobListener> scheduleJobListeners) {
		GuaranteeService guaranteeService = new GuaranteeService(regCenter, factJobConfig.getJobName());
		for (ScheduleJobListener each : scheduleJobListeners) {
			if (each instanceof AbstractOneOffJobListener) {
				((AbstractOneOffJobListener) each).setGuaranteeService(guaranteeService);
			}
		}
	}

	/**
	 * 初始化作业.
	 */
	public void init() {
		log.debug("Job '{}' controller init.", factJobConfig.getJobName());
		schedulerFacade.clearPreviousServerStatus();
		regCenter.addCacheData("/" + factJobConfig.getJobName());
		schedulerFacade.registerStartUpInfo(factJobConfig);
	}

}
