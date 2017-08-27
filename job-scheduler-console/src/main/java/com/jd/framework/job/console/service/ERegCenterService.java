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

import java.util.Collection;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.google.common.base.Optional;
import com.jd.framework.job.console.domain.RegCenterConfiguration;
import com.jd.framework.job.console.domain.RegCenterConfigurations;
import com.jd.framework.job.console.repository.RegCenterConfigXmlRepository;

@Service
public class ERegCenterService {

	@Resource
	private RegCenterConfigXmlRepository regCenterConfigXmlRepository;

	public Collection<RegCenterConfiguration> loadAll() {
		return regCenterConfigXmlRepository.load().getRegCenterConfiguration();
	}

	public RegCenterConfiguration load(final String name) {
		RegCenterConfigurations configs = regCenterConfigXmlRepository.load();
		RegCenterConfiguration result = findRegCenterConfiguration(name, configs);
		setActivated(configs, result);
		return result;
	}

	private RegCenterConfiguration findRegCenterConfiguration(final String name, final RegCenterConfigurations configs) {
		for (RegCenterConfiguration each : configs.getRegCenterConfiguration()) {
			if (name.equals(each.getName())) {
				return each;
			}
		}
		return null;
	}

	private void setActivated(final RegCenterConfigurations configs, final RegCenterConfiguration toBeConnectedConfig) {
		RegCenterConfiguration activatedConfig = findActivatedRegCenterConfiguration(configs);
		if (!toBeConnectedConfig.equals(activatedConfig)) {
			if (null != activatedConfig) {
				activatedConfig.setActivated(false);
			}
			toBeConnectedConfig.setActivated(true);
			regCenterConfigXmlRepository.save(configs);
		}
	}

	public Optional<RegCenterConfiguration> loadActivated() {
		RegCenterConfigurations configs = regCenterConfigXmlRepository.load();
		RegCenterConfiguration result = findActivatedRegCenterConfiguration(configs);
		if (null == result) {
			return Optional.absent();
		}
		return Optional.of(result);
	}

	private RegCenterConfiguration findActivatedRegCenterConfiguration(final RegCenterConfigurations configs) {
		for (RegCenterConfiguration each : configs.getRegCenterConfiguration()) {
			if (each.isActivated()) {
				return each;
			}
		}
		return null;
	}

	public boolean add(final RegCenterConfiguration config) {
		RegCenterConfigurations configs = regCenterConfigXmlRepository.load();
		boolean result = configs.getRegCenterConfiguration().add(config);
		if (result) {
			regCenterConfigXmlRepository.save(configs);
		}
		return result;
	}

	public void delete(final String name) {
		RegCenterConfigurations configs = regCenterConfigXmlRepository.load();
		if (configs.getRegCenterConfiguration().remove(new RegCenterConfiguration(name))) {
			regCenterConfigXmlRepository.save(configs);
		}
	}
}
