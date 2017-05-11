/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.core.fixture;

import com.jd.framework.job.executor.handler.exception.DefaultJobExceptionHandler;
import com.jd.framework.job.executor.handler.threadpool.DefaultExecutorServiceHandler;

public class FactJsonConstants {
	
	private static final String JOB_PROPS_JSON = "{\"job_exception_handler\":\""
			+ DefaultJobExceptionHandler.class.getCanonicalName() + "\"," + "\"executor_service_handler\":\""
			+ DefaultExecutorServiceHandler.class.getCanonicalName() + "\"}";

	private static final String JOB_JSON = "{\"jobName\":\"test_job\",\"jobClass\":\"%s\",\"jobType\":\"SIMPLE\",\"cron\":\"0/1 * * * * ?\","
			+ "\"segmentTotalCount\":3,\"segmentItemParameters\":\"\",\"jobParameter\":\"param\",\"failover\":true,\"misfire\":false,\"description\":\"desc\","
			+ "\"jobProperties\":"
			+ JOB_PROPS_JSON
			+ ",\"monitorExecution\":%s,\"maxTimeDiffSeconds\":%s,"
			+ "\"monitorPort\":8888,\"jobSegmentStrategyClass\":\"testClass\",\"disabled\":true,\"overwrite\":true, \"reconcileIntervalMinutes\": %s}";

	private static final String DEFAULT_JOB_CLASS = "com.jd.framework.job.core.fixture.TestSimpleJob";

	private static final boolean DEFAULT_MONITOR_EXECUTION = true;

	private static final int DEFAULT_MAX_TIME_DIFF_SECONDS = 1000;

	private static final int DEFAULT_RECONCILE_CYCLE_TIME = 15;

	public static String getJobJson() {
		return String.format(JOB_JSON, DEFAULT_JOB_CLASS, DEFAULT_MONITOR_EXECUTION, DEFAULT_MAX_TIME_DIFF_SECONDS,
				DEFAULT_RECONCILE_CYCLE_TIME);
	}

	public static String getJobJson(final String jobClass) {
		return String.format(JOB_JSON, jobClass, DEFAULT_MONITOR_EXECUTION, DEFAULT_MAX_TIME_DIFF_SECONDS,
				DEFAULT_RECONCILE_CYCLE_TIME);
	}

	public static String getJobJson(final boolean monitorExecution) {
		return String.format(JOB_JSON, DEFAULT_JOB_CLASS, monitorExecution, DEFAULT_MAX_TIME_DIFF_SECONDS,
				DEFAULT_RECONCILE_CYCLE_TIME);
	}

	public static String getJobJson(final int maxTimeDiffSeconds) {
		return String.format(JOB_JSON, DEFAULT_JOB_CLASS, DEFAULT_MONITOR_EXECUTION, maxTimeDiffSeconds,
				DEFAULT_RECONCILE_CYCLE_TIME);
	}

	public static String getJobJson(final long reconcileCycleTime) {
		return String.format(JOB_JSON, DEFAULT_JOB_CLASS, DEFAULT_MONITOR_EXECUTION, DEFAULT_MAX_TIME_DIFF_SECONDS,
				reconcileCycleTime);
	}
}
