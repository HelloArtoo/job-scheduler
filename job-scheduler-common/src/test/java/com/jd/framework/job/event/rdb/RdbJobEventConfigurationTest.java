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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.apache.commons.dbcp.BasicDataSource;
import org.junit.Test;

import com.jd.framework.job.exception.JobEventListenerConfigException;

public class RdbJobEventConfigurationTest {

	private final String jdbc = "jdbc:h2:mem:job_event_storage";

	@Test
	public void assertGetDataSource() throws JobEventListenerConfigException {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(org.h2.Driver.class.getName());
		dataSource.setUrl(jdbc);
		dataSource.setUsername("sa");
		dataSource.setPassword("");
		assertThat((BasicDataSource) (new RdbJobEventConfiguration(dataSource).getDataSource()), is(dataSource));
	}

	@Test
	public void assertCreateJobEventListenerSuccess() throws JobEventListenerConfigException {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(org.h2.Driver.class.getName());
		dataSource.setUrl(jdbc);
		dataSource.setUsername("sa");
		dataSource.setPassword("");
		assertThat(new RdbJobEventConfiguration(dataSource).createJobEventListener(),
				instanceOf(RdbJobEventListener.class));
	}

	@Test(expected = JobEventListenerConfigException.class)
	public void assertCreateJobEventListenerFailure() throws JobEventListenerConfigException {
		new RdbJobEventConfiguration(new BasicDataSource()).createJobEventListener();
	}

}
