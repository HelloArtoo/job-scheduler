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

import lombok.Getter;
import lombok.Setter;

import com.google.common.base.Strings;

@Getter
@Setter
public final class ServerInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2663857675417230366L;

	private String jobName;

	private String ip;

	private String hostName;

	private ServerStatus status;

	private String segment;

	/**
	 * 作业服务器状态.
	 * 
	 */
	public enum ServerStatus {

		READY, RUNNING, DISABLED, PAUSED, CRASHED, SHUTDOWN;

		/**
		 * 获取作业服务器状态.
		 * 
		 * @param status
		 *            作业状态
		 * @param isDisabled
		 *            作业是否禁用
		 * @param isPaused
		 *            作业是否暂停
		 * @param isShutdown
		 *            作业是否关闭
		 * @return 作业服务器状态
		 */
		public static ServerStatus getServerStatus(final String status, final boolean isDisabled,
				final boolean isPaused, final boolean isShutdown) {
			if (isShutdown) {
				return SHUTDOWN;
			}
			if (Strings.isNullOrEmpty(status)) {
				return CRASHED;
			}
			if (isDisabled) {
				return DISABLED;
			}
			if (isPaused) {
				return PAUSED;
			}
			return ServerStatus.valueOf(status);
		}
	}
}
