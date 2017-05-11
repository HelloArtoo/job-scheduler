/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.fixture;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import com.jd.framework.job.executor.handler.threadpool.DefaultExecutorServiceHandler;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonConstants {
	private static final String JOB_PROPS_JSON = "{\"job_exception_handler\":\"%s\",\"executor_service_handler\":\""
			+ DefaultExecutorServiceHandler.class.getCanonicalName() + "\"}";

	// CHECKSTYLE:OFF
	private static final String SIMPLE_JOB_JSON = "{\"jobName\":\"test_job\",\"jobClass\":\"com.jd.framework.job.fixture.job.TestSimpleJob\",\"jobType\":\"SIMPLE\","
			+ "\"cron\":\"0/1 * * * * ?\",\"segmentTotalCount\":3,\"segmentItemParameters\":\"0\\u003dA,1\\u003dB,2\\u003dC\",\"jobParameter\":\"param\",\"failover\":true,\"misfire\":false,"
			+ "\"description\":\"desc\",\"jobProperties\":%s}";
	// CHECKSTYLE:ON

	private static final String FLOW_JOB_JSON = "{\"jobName\":\"test_job\",\"jobClass\":\"com.jd.framework.job.fixture.job.TestFlowJob\",\"jobType\":\"FLOW\","
			+ "\"cron\":\"0/1 * * * * ?\",\"segmentTotalCount\":3,\"segmentItemParameters\":\"\",\"jobParameter\":\"\",\"failover\":false,\"misfire\":true,\"description\":\"\","
			+ "\"jobProperties\":%s,\"streamingProcess\":true}";	

	public static String getJobPropertiesJson(final String jobExceptionHandler) {
		return String.format(JOB_PROPS_JSON, jobExceptionHandler);
	}

	public static String getSimpleJobJson(final String jobExceptionHandler) {
		return String.format(SIMPLE_JOB_JSON, getJobPropertiesJson(jobExceptionHandler));
	}

	public static String getFlowJobJson(final String jobExceptionHandler) {
		return String.format(FLOW_JOB_JSON, getJobPropertiesJson(jobExceptionHandler));
	}

}
