/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.spring.namespace.job.tag;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 
 * 作业属性标签
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-15
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JobBeanDefinitionParserTag {

	public static final String REGISTRY_CENTER_REF_ATTRIBUTE = "registry-center-ref";

	public static final String CLASS_ATTRIBUTE = "class";

	public static final String CRON_ATTRIBUTE = "cron";

	public static final String SEGMENT_TOTAL_COUNT_ATTRIBUTE = "segment-total-count";

	public static final String SEGMENT_ITEM_PARAMETERS_ATTRIBUTE = "segment-item-parameters";

	public static final String JOB_PARAMETER_ATTRIBUTE = "job-parameter";

	public static final String MONITOR_EXECUTION_ATTRIBUTE = "monitor-execution";

	public static final String MONITOR_PORT_ATTRIBUTE = "monitor-port";

	public static final String FAILOVER_ATTRIBUTE = "failover";

	public static final String MAX_TIME_DIFF_SECONDS_ATTRIBUTE = "max-time-diff-seconds";

	public static final String MISFIRE_ATTRIBUTE = "misfire";

	public static final String JOB_SEGMENT_STRATEGY_CLASS_ATTRIBUTE = "job-segment-strategy-class";

	public static final String DESCRIPTION_ATTRIBUTE = "description";

	public static final String DISABLED_ATTRIBUTE = "disabled";

	public static final String OVERWRITE_ATTRIBUTE = "overwrite";

	public static final String LISTENER_TAG = "listener";

	public static final String DISTRIBUTED_LISTENER_TAG = "distributed-listener";

	public static final String DISTRIBUTED_LISTENER_STARTED_TIMEOUT_MILLISECONDS_ATTRIBUTE = "started-timeout-milliseconds";

	public static final String DISTRIBUTED_LISTENER_COMPLETED_TIMEOUT_MILLISECONDS_ATTRIBUTE = "completed-timeout-milliseconds";

	public static final String EXECUTOR_SERVICE_HANDLER_ATTRIBUTE = "executor-service-handler";

	public static final String JOB_EXCEPTION_HANDLER_ATTRIBUTE = "job-exception-handler";

	public static final String EVENT_TRACE_RDB_DATA_SOURCE_ATTRIBUTE = "event-trace-rdb-data-source";

	public static final String RECONCILE_INTERVAL_MINUTES = "reconcile-interval-minutes";
}
