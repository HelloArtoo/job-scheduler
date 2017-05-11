/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.core.internal.executor;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import com.jd.framework.job.core.internal.executor.quartz.JobScheduleController;

public class JobRegistryTest {

	@Test
	public void assertAddJobScheduler() {
		JobScheduleController jobScheduleController = mock(JobScheduleController.class);
		JobRegistry.getInstance().addJobScheduleController("test_job_AddJobScheduler", jobScheduleController);
		assertThat(JobRegistry.getInstance().getJobScheduleController("test_job_AddJobScheduler"),
				is(jobScheduleController));
	}
}
