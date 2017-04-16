/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.statistics.type.task;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.jd.framework.job.constant.statistics.StatisticInterval;

/**
 * 
 * 任务运行结果统计数据
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-15
 */
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class TaskResultStatistic {

	private long id;

	private final int successCount;

	private final int failedCount;

	private final StatisticInterval statisticInterval;

	private final Date statisticTime;

	private Date createTime = new Date();
}
