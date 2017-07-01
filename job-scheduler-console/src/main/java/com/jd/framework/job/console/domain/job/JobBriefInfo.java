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

/**
 * 
 * 作业概览
 * 
 * @author Rong Hu
 * @version 1.0, 2017-7-1
 */
@Getter
@Setter
public final class JobBriefInfo implements Serializable, Comparable<JobBriefInfo> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5956230776243081263L;

	private String jobName;

	private String jobType;

	private JobStatus status;

	private String description;

	private String cron;

	@Override
	public int compareTo(final JobBriefInfo o) {
		return getJobName().compareTo(o.getJobName());
	}

	/**
	 * 作业状态.
	 */
	public enum JobStatus {

		OK, PARTIAL_ALIVE, DISABLED, ALL_CRASHED;

		/**
		 * 获取作业状态.
		 * 
		 * @param okCount
		 *            作业成功总数
		 * @param crashedCount
		 *            作业崩溃总数
		 * @param disabledCount
		 *            作业禁用总数
		 * @param serverCount
		 *            作业服务器总数
		 * @return 作业状态
		 */
		public static JobStatus getJobStatus(final int okCount, final int crashedCount, final int disabledCount,
				final int serverCount) {
			if (okCount == serverCount) {
				return OK;
			}
			if (crashedCount == serverCount) {
				return ALL_CRASHED;
			}
			if (crashedCount > 0) {
				return PARTIAL_ALIVE;
			}
			if (disabledCount > 0) {
				return DISABLED;
			}
			return OK;
		}
	}
}
