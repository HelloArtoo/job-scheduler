/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.console.domain.job;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import com.jd.framework.job.executor.handler.JobProperties.JobPropertiesEnum;

@Getter
@Setter
public final class JobSettings implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4209469851068854035L;

	private String jobName;

	private String jobType;

	private String jobClass;

	private String cron;

	private int segmentTotalCount;

	private String segmentItemParameters;

	private String jobParameter;

	private boolean monitorExecution;

	private boolean streamingProcess;

	private int maxTimeDiffSeconds;

	private int monitorPort = -1;

	private boolean failover;

	private boolean misfire;

	private String jobSegmentStrategyClass;

	private String description;

	private Map<String, String> jobProperties = new LinkedHashMap<>(JobPropertiesEnum.values().length, 1);

	private int reconcileIntervalMinutes;
}
