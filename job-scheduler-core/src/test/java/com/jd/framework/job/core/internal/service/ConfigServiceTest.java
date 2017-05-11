/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.core.internal.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.unitils.util.ReflectionUtils;

import com.jd.framework.job.core.config.FactJobConfiguration;
import com.jd.framework.job.core.fixture.FactJsonConstants;
import com.jd.framework.job.core.fixture.JobConfigurationUtils;
import com.jd.framework.job.core.internal.factory.FactJobConfigGsonFactory;
import com.jd.framework.job.core.internal.helper.ConfigNodeHelper;
import com.jd.framework.job.core.internal.helper.JobNodeStorageHelper;
import com.jd.framework.job.exception.JobConfigurationException;
import com.jd.framework.job.exception.JobExecutionEnvironmentException;

public class ConfigServiceTest {

	@Mock
	private JobNodeStorageHelper jobNodeStorage;

	private final ConfigService configService = new ConfigService(null, "test_job");

	@Before
	public void initMocks() throws NoSuchFieldException {
		MockitoAnnotations.initMocks(this);
		ReflectionUtils.setFieldValue(configService, "jobNodeStorage", jobNodeStorage);
	}

	@Test
	public void assertLoadDirectly() {
		when(jobNodeStorage.getJobNodeDataDirectly(ConfigNodeHelper.ROOT)).thenReturn(FactJsonConstants.getJobJson());
		FactJobConfiguration actual = configService.load(false);
		assertThat(actual.getJobName(), is("test_job"));
		assertThat(actual.getTypeConfig().getCoreConfig().getCron(), is("0/1 * * * * ?"));
		assertThat(actual.getTypeConfig().getCoreConfig().getSegmentTotalCount(), is(3));
	}

	@Test
	public void assertLoadFromCache() {
		when(jobNodeStorage.getJobNodeData(ConfigNodeHelper.ROOT)).thenReturn(FactJsonConstants.getJobJson());
		FactJobConfiguration actual = configService.load(true);
		assertThat(actual.getJobName(), is("test_job"));
		assertThat(actual.getTypeConfig().getCoreConfig().getCron(), is("0/1 * * * * ?"));
		assertThat(actual.getTypeConfig().getCoreConfig().getSegmentTotalCount(), is(3));
	}

	@Test
	public void assertLoadFromCacheButNull() {
		when(jobNodeStorage.getJobNodeData(ConfigNodeHelper.ROOT)).thenReturn(null);
		when(jobNodeStorage.getJobNodeDataDirectly(ConfigNodeHelper.ROOT)).thenReturn(FactJsonConstants.getJobJson());
		FactJobConfiguration actual = configService.load(true);
		assertThat(actual.getJobName(), is("test_job"));
		assertThat(actual.getTypeConfig().getCoreConfig().getCron(), is("0/1 * * * * ?"));
		assertThat(actual.getTypeConfig().getCoreConfig().getSegmentTotalCount(), is(3));
	}

	@Test(expected = JobConfigurationException.class)
	public void assertPersistJobConfigurationForJobConflict() {
		when(jobNodeStorage.isJobNodeExisted(ConfigNodeHelper.ROOT)).thenReturn(true);
		when(jobNodeStorage.getJobNodeDataDirectly(ConfigNodeHelper.ROOT)).thenReturn(
				FactJsonConstants.getJobJson("com.jd.framework.job.core.fixture.TestOtherSimpleJob"));
		try {
			configService.persist(JobConfigurationUtils.createSimpleFactJobConfiguration());
		} finally {
			verify(jobNodeStorage).isJobNodeExisted(ConfigNodeHelper.ROOT);
			verify(jobNodeStorage).getJobNodeDataDirectly(ConfigNodeHelper.ROOT);
		}
	}

	@Test
	public void assertPersistNewJobConfiguration() {
		FactJobConfiguration factJobConfig = JobConfigurationUtils.createSimpleFactJobConfiguration();
		configService.persist(factJobConfig);
		verify(jobNodeStorage).replaceJobNode("config", FactJobConfigGsonFactory.toJson(factJobConfig));
	}

	@Test
	public void assertPersistExistedJobConfiguration() throws NoSuchFieldException {
		when(jobNodeStorage.isJobNodeExisted(ConfigNodeHelper.ROOT)).thenReturn(true);
		when(jobNodeStorage.getJobNodeDataDirectly(ConfigNodeHelper.ROOT)).thenReturn(FactJsonConstants.getJobJson());
		FactJobConfiguration factJobConfig = JobConfigurationUtils.createSimpleFactJobConfiguration(true);
		configService.persist(factJobConfig);
		verify(jobNodeStorage).replaceJobNode("config", FactJobConfigGsonFactory.toJson(factJobConfig));
	}

	@Test
	public void assertIsMaxTimeDiffSecondsTolerableWithDefaultValue() throws JobExecutionEnvironmentException {
		when(jobNodeStorage.getJobNodeData(ConfigNodeHelper.ROOT)).thenReturn(FactJsonConstants.getJobJson(-1));
		configService.checkMaxTimeDiffSecondsTolerable();
	}

	@Test
	public void assertIsMaxTimeDiffSecondsTolerable() throws JobExecutionEnvironmentException {
		when(jobNodeStorage.getJobNodeData(ConfigNodeHelper.ROOT)).thenReturn(FactJsonConstants.getJobJson());
		when(jobNodeStorage.getRegistryCenterTime()).thenReturn(System.currentTimeMillis());
		configService.checkMaxTimeDiffSecondsTolerable();
		verify(jobNodeStorage).getRegistryCenterTime();
	}

	@Test(expected = JobExecutionEnvironmentException.class)
	public void assertIsNotMaxTimeDiffSecondsTolerable() throws JobExecutionEnvironmentException {
		when(jobNodeStorage.getJobNodeData(ConfigNodeHelper.ROOT)).thenReturn(FactJsonConstants.getJobJson());
		when(jobNodeStorage.getRegistryCenterTime()).thenReturn(0L);
		try {
			configService.checkMaxTimeDiffSecondsTolerable();
		} finally {
			verify(jobNodeStorage).getRegistryCenterTime();
		}
	}
}
