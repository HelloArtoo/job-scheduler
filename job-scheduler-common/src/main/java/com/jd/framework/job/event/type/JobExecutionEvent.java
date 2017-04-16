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

import com.jd.framework.job.event.JobEvent;
import com.jd.framework.job.utils.env.LocalHostService;
import com.jd.framework.job.utils.exception.ExceptionUtil;

/**
 * 
 * 作业执行事件
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class JobExecutionEvent implements JobEvent {

	private static LocalHostService localHostService = new LocalHostService();

	private String id = UUID.randomUUID().toString();

	private String hostname = localHostService.getHostName();

	private String ip = localHostService.getIp();

	private final String taskId;

	private final String jobName;

	private final ExecutionSource source;

	private final int segmentItem;

	private Date startTime = new Date();

	@Setter
	private Date completeTime;

	@Setter
	private boolean success;

	@Setter
	private JobExecutionEventThrowable failureCause;

	/**
	 * 作业执行成功.
	 * 
	 * @return 作业执行事件
	 */
	public JobExecutionEvent executionSuccess() {
		JobExecutionEvent result = new JobExecutionEvent(id, hostname, ip, taskId, jobName, source, segmentItem,
				startTime, completeTime, success, failureCause);
		result.setCompleteTime(new Date());
		result.setSuccess(true);
		return result;
	}

	/**
	 * 作业执行失败.
	 * 
	 * @param failureCause
	 *            失败原因
	 * @return 作业执行事件
	 */
	public JobExecutionEvent executionFailure(final Throwable failureCause) {
		JobExecutionEvent result = new JobExecutionEvent(id, hostname, ip, taskId, jobName, source, segmentItem,
				startTime, completeTime, success, new JobExecutionEventThrowable(failureCause));
		result.setCompleteTime(new Date());
		result.setSuccess(false);
		return result;
	}

	/**
	 * 获取失败原因.
	 * 
	 * @return 失败原因
	 */
	public String getFailureCause() {
		return ExceptionUtil.transform(failureCause == null ? null : failureCause.getThrowable());
	}

	/**
	 * 执行来源.
	 */
	public enum ExecutionSource {
		NORMAL_TRIGGER, MISFIRE, FAILOVER
	}
}
