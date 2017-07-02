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

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.google.common.base.Optional;
import com.jd.framework.job.console.domain.RegCenterConfiguration;
import com.jd.framework.job.console.service.RegCenterService;

/**
 * 
 * dashboard page
 * 
 * @author Rong Hu
 * @version 1.0, 2017-7-1
 */
@Controller
@RequestMapping("/")
@SessionAttributes(RegCenterController.REG_CENTER_CONFIG_KEY)
public class DashboardController {

	@Resource
	private RegCenterService regCenterService;

	@RequestMapping(value = "test", method = RequestMethod.GET)
	public String test(final ModelMap model) {
		return "dashboard";
	}
	
	@RequestMapping(value = "jobtest", method = RequestMethod.GET)
	public String test2(final ModelMap model) {
		return "job";
	}
	
	@RequestMapping(value = "servertest", method = RequestMethod.GET)
	public String test3(final ModelMap model) {
		return "server";
	}

	@RequestMapping(method = RequestMethod.GET)
	public String homepage(final ModelMap model) {
		Optional<RegCenterConfiguration> activatedRegCenterConfig = regCenterService.loadActivated();
		if (activatedRegCenterConfig.isPresent()) {
			model.put(RegCenterController.REG_CENTER_CONFIG_KEY, activatedRegCenterConfig.get());
			return "redirect:overview";
		}
		return "redirect:registry";
	}

	@RequestMapping(value = "registry", method = RequestMethod.GET)
	public String registryCenterPage(final ModelMap model) {
		model.put("activeTab", 1);
		return "registry";
	}

	@RequestMapping(value = "job_detail", method = RequestMethod.GET)
	public String jobDetail(@RequestParam final String jobName, @RequestParam final String jobType, final ModelMap model) {
		model.put("jobName", jobName);
		model.put("jobType", jobType);
		return "job_detail";
	}

	@RequestMapping(value = "server_detail", method = RequestMethod.GET)
	public String serverDetail(@RequestParam final String serverIp, final ModelMap model) {
		model.put("serverIp", serverIp);
		return "server_detail";
	}

	@RequestMapping(value = "overview", method = RequestMethod.GET)
	public String overview(final ModelMap model) {
		model.put("activeTab", 0);
		return "dashboard";
	}

	@RequestMapping(value = "help", method = RequestMethod.GET)
	public String help(final ModelMap model) {
		model.put("activeTab", 2);
		return "help";
	}
}
