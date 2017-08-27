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

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 
 * dashboard page
 * 
 * @author Rong Hu
 * @version 1.0, 2017-7-1
 */
@Controller
@RequestMapping("/")
public class DashboardController {

	@RequestMapping(value = "home", method = RequestMethod.GET)
	public String homepage(final ModelMap model) {
		return "dashboard";
	}

	@RequestMapping(value = "job", method = RequestMethod.GET)
	public String job(final ModelMap model) {
		return "job";
	}

	@RequestMapping(value = "server", method = RequestMethod.GET)
	public String server(final ModelMap model) {
		return "server";
	}

	@RequestMapping(value = "other", method = RequestMethod.GET)
	public String other(final ModelMap model) {
		return "other";
	}

	@RequestMapping(value = "help", method = RequestMethod.GET)
	public String help(final ModelMap model) {
		return "help";
	}

	@RequestMapping(value = "error", method = RequestMethod.GET)
	public String error(final ModelMap model) {
		
		return "error";
	}

	// @RequestMapping(value = "job_detail", method = RequestMethod.GET)
	// public String jobDetail(@RequestParam final String jobName, @RequestParam
	// final String jobType,
	// final ModelMap model) {
	// model.put("jobName", jobName);
	// model.put("jobType", jobType);
	// return "job_detail";
	// }
	//
	// @RequestMapping(value = "server_detail", method = RequestMethod.GET)
	// public String serverDetail(@RequestParam final String serverIp, final
	// ModelMap model) {
	// model.put("serverIp", serverIp);
	// return "server_detail";
	// }
	//
	// @RequestMapping(value = "overview", method = RequestMethod.GET)
	// public String overview(final ModelMap model) {
	// model.put("activeTab", 0);
	// return "dashboard";
	// }
}
