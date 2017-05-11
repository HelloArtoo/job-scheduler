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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.unitils.util.ReflectionUtils;

import com.jd.framework.job.config.core.JobCoreConfiguration;
import com.jd.framework.job.config.type.FlowJobConfiguration;
import com.jd.framework.job.config.type.SimpleJobConfiguration;
import com.jd.framework.job.core.config.FactJobConfiguration;
import com.jd.framework.job.core.fixture.TestFlowJob;
import com.jd.framework.job.core.fixture.TestSimpleJob;
import com.jd.framework.job.core.internal.helper.JobNodeStorageHelper;

public class GuaranteeServiceTest {

	@Mock
	private JobNodeStorageHelper jobNodeStorage;

	@Mock
	private ConfigService configService;

	private final GuaranteeService guaranteeService = new GuaranteeService(null, "test_job");

	@Before
	public void setUp() throws NoSuchFieldException {
		MockitoAnnotations.initMocks(this);
		ReflectionUtils.setFieldValue(guaranteeService, "jobNodeStorage", jobNodeStorage);
		ReflectionUtils.setFieldValue(guaranteeService, "configService", configService);
	}

	@Test
	public void assertRegisterStart() {
		guaranteeService.registerStart(Arrays.asList(0, 1));
		verify(jobNodeStorage).createJobNodeIfNeeded("guarantee/started/0");
		verify(jobNodeStorage).createJobNodeIfNeeded("guarantee/started/1");
	}

	@Test
	public void assertIsNotAllStartedWhenRootNodeIsNotExisted() {
		when(jobNodeStorage.isJobNodeExisted("guarantee/started")).thenReturn(false);
		assertFalse(guaranteeService.isAllStarted());
	}

	@Test
	public void assertIsNotAllStarted() {
		when(configService.load(false)).thenReturn(
				FactJobConfiguration.newBuilder(
						new FlowJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3)
								.build(), TestFlowJob.class.getCanonicalName(), true)).build());
		when(jobNodeStorage.isJobNodeExisted("guarantee/started")).thenReturn(true);
		//size not equals
		when(jobNodeStorage.getJobNodeChildrenKeys("guarantee/started")).thenReturn(Arrays.asList("0", "1"));
		assertFalse(guaranteeService.isAllStarted());
	}

	@Test
	public void assertIsAllStarted() {
		when(jobNodeStorage.isJobNodeExisted("guarantee/started")).thenReturn(true);
		when(configService.load(false)).thenReturn(
				FactJobConfiguration.newBuilder(
						new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3)
								.build(), TestSimpleJob.class.getCanonicalName())).build());
		when(jobNodeStorage.getJobNodeChildrenKeys("guarantee/started")).thenReturn(Arrays.asList("0", "1", "2"));
		assertTrue(guaranteeService.isAllStarted());
	}

	@Test
	public void assertClearAllStartedInfo() {
		guaranteeService.clearAllStartedInfo();
		verify(jobNodeStorage).removeJobNodeIfExisted("guarantee/started");
	}

	@Test
	public void assertRegisterComplete() {
		guaranteeService.registerComplete(Arrays.asList(0, 1));
		verify(jobNodeStorage).createJobNodeIfNeeded("guarantee/completed/0");
		verify(jobNodeStorage).createJobNodeIfNeeded("guarantee/completed/1");
	}

	@Test
	public void assertIsNotAllCompletedWhenRootNodeIsNotExisted() {
		when(jobNodeStorage.isJobNodeExisted("guarantee/completed")).thenReturn(false);
		assertFalse(guaranteeService.isAllCompleted());
	}

	@Test
	public void assertIsNotAllCompleted() {
		when(configService.load(false)).thenReturn(
				FactJobConfiguration.newBuilder(
						new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 10)
								.build(), TestSimpleJob.class.getCanonicalName())).build());
		when(jobNodeStorage.isJobNodeExisted("guarantee/completed")).thenReturn(false);
		when(jobNodeStorage.getJobNodeChildrenKeys("guarantee/completed")).thenReturn(Arrays.asList("0", "1"));
		assertFalse(guaranteeService.isAllCompleted());
	}

	@Test
	public void assertIsAllCompleted() {
		when(jobNodeStorage.isJobNodeExisted("guarantee/completed")).thenReturn(true);
		when(configService.load(false)).thenReturn(
				FactJobConfiguration.newBuilder(
						new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3)
								.build(), TestSimpleJob.class.getCanonicalName())).build());
		when(jobNodeStorage.getJobNodeChildrenKeys("guarantee/completed")).thenReturn(Arrays.asList("0", "1", "2"));
		assertTrue(guaranteeService.isAllCompleted());
	}

	@Test
	public void assertClearAllCompletedInfo() {
		guaranteeService.clearAllCompletedInfo();
		verify(jobNodeStorage).removeJobNodeIfExisted("guarantee/completed");
	}
}
