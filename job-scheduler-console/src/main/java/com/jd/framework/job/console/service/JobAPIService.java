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

import com.google.common.base.Optional;
import com.jd.framework.job.console.domain.RegCenterConfiguration;
import com.jd.framework.job.console.factory.JobAPIFactory;
import com.jd.framework.job.console.service.api.JobOperateAPI;
import com.jd.framework.job.console.service.api.JobSettingsAPI;
import com.jd.framework.job.console.service.api.JobStatisticsAPI;
import com.jd.framework.job.console.service.api.ServerStatisticsAPI;
import com.jd.framework.job.console.utils.SessionRegCenterConfigUtils;

@Service
public final class JobAPIService {

	public JobSettingsAPI getJobSettingsAPI() {
		RegCenterConfiguration regCenterConfig = SessionRegCenterConfigUtils.getRegCenterConfiguration();
		return JobAPIFactory.createJobSettingsAPI(regCenterConfig.getZkAddressList(), regCenterConfig.getNamespace(),
				Optional.fromNullable(regCenterConfig.getDigest()));
	}

	public JobStatisticsAPI getJobStatisticsAPI() {
		RegCenterConfiguration regCenterConfig = SessionRegCenterConfigUtils.getRegCenterConfiguration();
		return JobAPIFactory.createJobStatisticsAPI(regCenterConfig.getZkAddressList(), regCenterConfig.getNamespace(),
				Optional.fromNullable(regCenterConfig.getDigest()));
	}

	public ServerStatisticsAPI getServerStatisticsAPI() {
		RegCenterConfiguration regCenterConfig = SessionRegCenterConfigUtils.getRegCenterConfiguration();
		return JobAPIFactory.createServerStatisticsAPI(regCenterConfig.getZkAddressList(),
				regCenterConfig.getNamespace(), Optional.fromNullable(regCenterConfig.getDigest()));
	}

	public JobOperateAPI getJobOperatorAPI() {
		RegCenterConfiguration regCenterConfig = SessionRegCenterConfigUtils.getRegCenterConfiguration();
		return JobAPIFactory.createJobOperateAPI(regCenterConfig.getZkAddressList(), regCenterConfig.getNamespace(),
				Optional.fromNullable(regCenterConfig.getDigest()));
	}
}
