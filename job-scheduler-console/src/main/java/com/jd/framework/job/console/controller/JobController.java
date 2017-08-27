/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.console.controller;

import java.util.Collection;

import javax.annotation.Resource;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jd.framework.job.console.domain.job.ExecutionInfo;
import com.jd.framework.job.console.domain.job.JobBriefInfo;
import com.jd.framework.job.console.domain.job.JobSettings;
import com.jd.framework.job.console.domain.job.ServerInfo;
import com.jd.framework.job.console.domain.view.ResWrap;
import com.jd.framework.job.console.service.JobAPIService;

@RestController
@RequestMapping("job")
public class JobController {

	@Resource
	private JobAPIService jobAPIService;

	@RequestMapping(value = "jobs", method = RequestMethod.GET)
	public ResWrap getAllJobsBriefInfo() {
		Collection<JobBriefInfo> infos = jobAPIService.getJobStatisticsAPI().getAllJobsBriefInfo();
		return new ResWrap(infos);
	}

	@RequestMapping(value = "settings", method = RequestMethod.GET)
	public JobSettings getJobSettings(final JobSettings jobSettings, final ModelMap model) {
		return jobAPIService.getJobSettingsAPI().getJobSettings(jobSettings.getJobName());
	}

	@RequestMapping(value = "settings", method = RequestMethod.POST)
	public void updateJobSettings(final JobSettings jobSettings) {
		jobAPIService.getJobSettingsAPI().updateJobSettings(jobSettings);
	}

	@RequestMapping(value = "servers", method = RequestMethod.GET)
	public ResWrap getServers(final ServerInfo jobServer) {
		Collection<ServerInfo> servers = jobAPIService.getJobStatisticsAPI().getServers(jobServer.getJobName());
		return new ResWrap(servers);
	}

	@RequestMapping(value = "execution", method = RequestMethod.GET)
	public ResWrap getExecutionInfo(final JobSettings jobSettings) {
		Collection<ExecutionInfo> executionInfo = jobAPIService.getJobStatisticsAPI().getExecutionInfo(jobSettings.getJobName());
		return new ResWrap(executionInfo);
	}
}
