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
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class ExecutionInfo implements Serializable, Comparable<ExecutionInfo> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5909716617371856922L;

	private int item;

	private ExecutionStatus status;

	private String failoverIp;

	private Date lastBeginTime;

	private Date nextFireTime;

	private Date lastCompleteTime;
	
	public String getLastBeginTimeStr() {
		return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(lastBeginTime);
	}

	public String getnextFireTimeStr() {
		return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(nextFireTime);
	}

	public String getLastCompleteTimeStr() {
		return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(lastCompleteTime);
	}

	@Override
	public int compareTo(final ExecutionInfo o) {
		return getItem() - o.getItem();
	}

	/**
	 * 作业运行时状态.
	 */
	public enum ExecutionStatus {

		RUNNING, COMPLETED, PENDING;

		/**
		 * 获取作业运行时状态.
		 * 
		 * @param isRunning
		 *            是否在运行
		 * @param isCompleted
		 *            是否运行完毕
		 * @return 作业运行时状态
		 */
		public static ExecutionStatus getExecutionStatus(final boolean isRunning, final boolean isCompleted) {
			if (isRunning) {
				return RUNNING;
			}
			if (isCompleted) {
				return COMPLETED;
			}
			return PENDING;
		}
	}
}
