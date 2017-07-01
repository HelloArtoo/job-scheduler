/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.console.repository.impl;

import org.springframework.stereotype.Repository;

import com.jd.framework.job.console.domain.RegCenterConfigurations;
import com.jd.framework.job.console.repository.AbstractXmlRepository;
import com.jd.framework.job.console.repository.RegCenterConfigXmlRepository;

@Repository
public class RegCenterConfigXmlRepositoryImpl extends AbstractXmlRepository<RegCenterConfigurations> implements
		RegCenterConfigXmlRepository {

	protected RegCenterConfigXmlRepositoryImpl(String fileName, Class<RegCenterConfigurations> clazz) {
		super("RegCenterConfigurations.xml", RegCenterConfigurations.class);
	}

}
