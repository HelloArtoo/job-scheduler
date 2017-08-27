/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.console.service;

import org.springframework.stereotype.Service;

import com.jd.framework.job.console.repository.RegCenterRepository;
import com.jd.framework.job.console.service.api.JobOperateAPI;
import com.jd.framework.job.console.service.api.JobSettingsAPI;
import com.jd.framework.job.console.service.api.JobStatisticsAPI;
import com.jd.framework.job.console.service.api.ServerStatisticsAPI;

@Service
public final class JobAPIService {

	public JobSettingsAPI getJobSettingsAPI() {
		return new JobSettingsAPI(RegCenterRepository.INSTANCE);
	}

	public JobStatisticsAPI getJobStatisticsAPI() {
		return new JobStatisticsAPI(RegCenterRepository.INSTANCE);
	}

	public ServerStatisticsAPI getServerStatisticsAPI() {
		return new ServerStatisticsAPI(RegCenterRepository.INSTANCE);
	}

	public JobOperateAPI getJobOperatorAPI() {
		return new JobOperateAPI(RegCenterRepository.INSTANCE);
	}
}
