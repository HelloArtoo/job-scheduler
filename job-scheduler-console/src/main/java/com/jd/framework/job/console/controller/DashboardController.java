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

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.jd.framework.job.console.domain.job.RegistryInfo;
import com.jd.framework.job.console.repository.RegCenterRepository;
import com.jd.framework.job.regcenter.ZookeeperRegistryCenter;
import com.jd.framework.job.regcenter.api.CoordinatorRegistryCenter;
import com.jd.framework.job.regcenter.conf.ZookeeperConfiguration;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * dashboard page
 * 
 * @author Rong Hu
 * @version 1.0, 2017-7-1
 */
@Controller
@RequestMapping("/")
@Slf4j
public class DashboardController {

	// FIXME why always redirect to '/index
	@RequestMapping(value = "index", method = RequestMethod.GET)
	public String index(final ModelMap model) {
		return "dashboard";
	}

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

	@RequestMapping(value = "help", method = RequestMethod.GET)
	public String help(final ModelMap model) {
		return "help";
	}

	@RequestMapping(value = "registry", method = RequestMethod.GET)
	public String error(final ModelMap model) {
		return "registry";
	}

	/**
	 * 切换注册中心
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "switch", method = RequestMethod.GET)
	public String regSwitch(RegistryInfo registry, HttpServletRequest request, final ModelMap model) {
		try {
			ZookeeperConfiguration zkConfig = new ZookeeperConfiguration(registry.getServers(),
					registry.getNamespace());
			if (StringUtils.isNotBlank(registry.getDigest())) {
				zkConfig.setDigest(registry.getDigest());
			}
			CoordinatorRegistryCenter result = new ZookeeperRegistryCenter(zkConfig);
			result.init();
			RegCenterRepository.INSTANCE = result;
			request.getSession().setAttribute("namespace", registry.getNamespace());
			request.getSession().setAttribute("servers", registry.getServers());
		} catch (Exception e) {
			log.error("init zookeeper registry failed, zookeeper configuration: " + registry, e);
			model.addAttribute("msg", e.getMessage());
			return "registry";
		}
		return "dashboard";
	}
}
