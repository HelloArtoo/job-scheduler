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

import com.jd.framework.job.console.domain.job.ServerBriefInfo;
import com.jd.framework.job.console.domain.job.ServerInfo;
import com.jd.framework.job.console.domain.view.ResWrap;
import com.jd.framework.job.console.service.JobAPIService;

@RestController
@RequestMapping("server")
public class ServerController {

	@Resource
	private JobAPIService jobAPIService;

	@RequestMapping(value = "servers", method = RequestMethod.GET)
	public ResWrap getAllServersBriefInfo() {
		Collection<ServerBriefInfo> allServersBriefInfo = jobAPIService.getServerStatisticsAPI()
				.getAllServersBriefInfo();
		return new ResWrap(allServersBriefInfo);
	}

	@RequestMapping(value = "jobs", method = RequestMethod.GET)
	public ResWrap getJobs(final ServerInfo jobServer, final ModelMap model) {
		model.put("serverIp", jobServer.getIp());
		Collection<ServerInfo> infos = jobAPIService.getServerStatisticsAPI().getJobs(jobServer.getIp());
		return new ResWrap(infos);
	}
}
