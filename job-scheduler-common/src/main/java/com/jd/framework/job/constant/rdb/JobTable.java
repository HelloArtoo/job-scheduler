/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.constant.rdb;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 
 * 关系数据库表命名常量
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-15
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JobTable {

	// LOG START
	/** 作业执行日志表 */
	public static final String TABLE_LOG_JOB_EXECUTION = "LOG_JOB_EXECUTION";

	/** 作业执行痕迹追踪表 */
	public static final String TABLE_LOG_JOB_STATUS_TRACE = "LOG_JOB_STATUS_TRACE";
	// LOG END

	// STATISTICS START
	/** 任务结果统计表 */
	public static final String TABLE_STATISTICS_TASK_RESULT = "STATISTICS_TASK_RESULT";

	/** 运行时任务统计表 */
	public static final String TABLE_STATISTICS_TASK_RUNNING = "STATISTICS_TASK_RUNNING";

	/** 运行时作业统计表 */
	public static final String TABLE_STATISTICS_JOB_RUNNING = "STATISTICS_JOB_RUNNING";

	/** 作业注册统计表 */
	public static final String TABLE_STATISTICS_JOB_REGISTER = "STATISTICS_JOB_REGISTER";
	// STATISTICS END

	/** 索引 */
	public static final String INDEX_TASK_ID_STATE = "IDX_TASK_ID_STATE";
}
