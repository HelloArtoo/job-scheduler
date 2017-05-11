/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.executor.factory;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.jd.framework.job.exception.JobConfigurationException;
import com.jd.framework.job.executor.AbstractJobExecutor;
import com.jd.framework.job.executor.facade.JobFacade;
import com.jd.framework.job.executor.type.FlowJobExecutor;
import com.jd.framework.job.executor.type.SimpleJobExecutor;
import com.jd.framework.job.fixture.config.TestFlowJobConfiguration;
import com.jd.framework.job.fixture.config.TestSimpleJobConfiguration;
import com.jd.framework.job.fixture.job.OtherJob;
import com.jd.framework.job.fixture.job.TestFlowJob;
import com.jd.framework.job.fixture.job.TestSimpleJob;

@RunWith(MockitoJUnitRunner.class)
public class JobExecutorFactoryTest {

	@Mock
	private JobFacade jobFacade;

	@Test
	public void assertGetJobExecutorForSimpleJob() {
		when(jobFacade.loadJobRootConfiguration(true)).thenReturn(new TestSimpleJobConfiguration());
		assertThat(JobExecutorFactory.getJobExecutor(new TestSimpleJob(null), jobFacade),
				instanceOf(SimpleJobExecutor.class));
	}

	@Test
	public void assertGetJobExecutorForFlowJob() {
		when(jobFacade.loadJobRootConfiguration(true)).thenReturn(new TestFlowJobConfiguration(false));
		assertThat(JobExecutorFactory.getJobExecutor(new TestFlowJob(null), jobFacade),
				instanceOf(FlowJobExecutor.class));
	}

	@Test(expected = JobConfigurationException.class)
	public void assertGetJobExecutorWhenJobClassWhenUnsupportedJob() {
		when(jobFacade.loadJobRootConfiguration(true)).thenReturn(new TestSimpleJobConfiguration());
		JobExecutorFactory.getJobExecutor(new OtherJob(), jobFacade);
	}

	@Test
	public void assertGetJobExecutorTwice() {
		when(jobFacade.loadJobRootConfiguration(true)).thenReturn(new TestFlowJobConfiguration(false));
		AbstractJobExecutor executor = JobExecutorFactory.getJobExecutor(new TestSimpleJob(null), jobFacade);
		AbstractJobExecutor anotherExecutor = JobExecutorFactory.getJobExecutor(new TestSimpleJob(null), jobFacade);
		assertTrue(executor.hashCode() != anotherExecutor.hashCode());
	}

}
