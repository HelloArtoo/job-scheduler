/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.fixture.config;

import lombok.NoArgsConstructor;

import com.jd.framework.job.config.JobRootConfiguration;
import com.jd.framework.job.config.JobTypeConfiguration;
import com.jd.framework.job.config.core.JobCoreConfiguration;
import com.jd.framework.job.config.type.SimpleJobConfiguration;
import com.jd.framework.job.executor.handler.JobProperties;
import com.jd.framework.job.fixture.SegmentContextsBuilder;
import com.jd.framework.job.fixture.handler.ThrowExceptionHandler;
import com.jd.framework.job.fixture.job.TestSimpleJob;

@NoArgsConstructor
public final class TestSimpleJobConfiguration implements JobRootConfiguration {
	
	private String jobExceptionHandlerClassName;

	private String executorServiceHandlerClassName;

	public TestSimpleJobConfiguration(final String jobExceptionHandlerClassName,
			final String executorServiceHandlerClassName) {
		this.jobExceptionHandlerClassName = jobExceptionHandlerClassName;
		this.executorServiceHandlerClassName = executorServiceHandlerClassName;
	}

	@Override
	public JobTypeConfiguration getTypeConfig() {
		JobCoreConfiguration.Builder builder = JobCoreConfiguration
				.newBuilder(SegmentContextsBuilder.JOB_NAME, "0/1 * * * * ?", 3).segmentItemParameters("0=A,1=B,2=C")
				.jobParameter("param").failover(true).misfire(false).description("desc");
		if (null == jobExceptionHandlerClassName) {
			builder.jobProperties(JobProperties.JobPropertiesEnum.JOB_EXCEPTION_HANDLER.getKey(),
					ThrowExceptionHandler.class.getCanonicalName());
		} else {
			builder.jobProperties(JobProperties.JobPropertiesEnum.JOB_EXCEPTION_HANDLER.getKey(),
					jobExceptionHandlerClassName);
		}
		if (null != executorServiceHandlerClassName) {
			builder.jobProperties(JobProperties.JobPropertiesEnum.EXECUTOR_SERVICE_HANDLER.getKey(),
					executorServiceHandlerClassName);
		}
		return new SimpleJobConfiguration(builder.build(), TestSimpleJob.class.getCanonicalName());
	}
}
