/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.event.type;

import java.util.Date;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import com.jd.framework.job.constant.job.ExecutionType;
import com.jd.framework.job.event.JobEvent;
import com.jd.framework.job.utils.env.LocalHostService;

/**
 * 
 * 作业状态痕迹事件
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class JobStatusTraceEvent implements JobEvent {

	private static LocalHostService localHostService = new LocalHostService();

	private String id = UUID.randomUUID().toString();

	private final String jobName;

	@Setter
	private String originalTaskId = "";

	private final String taskId;

	private final String slaveId;

	private final Source source;

	private final ExecutionType executionType;

	private final String segmentItems;

	private final State state;

	private final String message;

	private Date creationTime = new Date();

	public enum State {
		TASK_STAGING, TASK_RUNNING, TASK_FINISHED, TASK_KILLED, TASK_LOST, TASK_FAILED, TASK_ERROR
	}

	public enum Source {
		CLOUD_SCHEDULER, CLOUD_EXECUTOR, LITE_EXECUTOR
	}
}
