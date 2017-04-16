/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.event.rdb;

import java.io.Serializable;
import java.sql.SQLException;

import javax.sql.DataSource;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.jd.framework.job.event.JobEventConfiguration;
import com.jd.framework.job.event.JobEventListener;
import com.jd.framework.job.exception.JobEventListenerConfigException;

/**
 * RDB作业事件配置
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-15
 */
@RequiredArgsConstructor
@Getter
public class RdbJobEventConfiguration extends RdbJobEventIdentity implements JobEventConfiguration, Serializable {

	private static final long serialVersionUID = 1509886614843094285L;

	private final DataSource dataSource;

	@Override
	public JobEventListener createJobEventListener() throws JobEventListenerConfigException {
		try {
			return new RdbJobEventListener(dataSource);
		} catch (final SQLException ex) {
			throw new JobEventListenerConfigException(ex);
		}
	}

}
