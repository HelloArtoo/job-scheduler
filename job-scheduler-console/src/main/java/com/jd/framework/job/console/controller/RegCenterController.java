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
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Optional;
import com.jd.framework.job.console.domain.RegCenterConfiguration;
import com.jd.framework.job.console.factory.RegCenterFactory;
import com.jd.framework.job.console.service.ERegCenterService;

@RestController
@RequestMapping("registry_center")
public class RegCenterController {
	public static final String REG_CENTER_CONFIG_KEY = "reg_center_config_key";

	@Resource
	private ERegCenterService regCenterService;

	@RequestMapping(method = RequestMethod.GET)
	public Collection<RegCenterConfiguration> load(final HttpSession session) {
		Optional<RegCenterConfiguration> regCenterConfig = regCenterService.loadActivated();
		if (regCenterConfig.isPresent()) {
			setRegCenterNameToSession(regCenterConfig.get(), session);
		}
		return regCenterService.loadAll();
	}

	@RequestMapping(method = RequestMethod.POST)
	public boolean add(final RegCenterConfiguration config) {
		return regCenterService.add(config);
	}

	@RequestMapping(value = "delete", method = RequestMethod.POST)
	public void delete(final RegCenterConfiguration config) {
		regCenterService.delete(config.getName());
	}

	@RequestMapping(value = "connect", method = RequestMethod.POST)
	public boolean connect(final RegCenterConfiguration config, final HttpSession session) {
		return setRegCenterNameToSession(regCenterService.load(config.getName()), session);
	}

	private boolean setRegCenterNameToSession(final RegCenterConfiguration regCenterConfig, final HttpSession session) {
		session.setAttribute(REG_CENTER_CONFIG_KEY, regCenterConfig);
		try {
			RegCenterFactory.createCoordinatorRegCenter(regCenterConfig.getZkAddressList(),
					regCenterConfig.getNamespace(), Optional.fromNullable(regCenterConfig.getDigest()));
		} catch (final Exception ex) {
			return false;
		}
		return true;
	}
}
