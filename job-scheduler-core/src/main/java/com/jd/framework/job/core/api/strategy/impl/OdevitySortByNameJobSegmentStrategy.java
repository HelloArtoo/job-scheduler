/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.core.api.strategy.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.jd.framework.job.core.api.strategy.JobSegmentStrategy;
import com.jd.framework.job.core.api.strategy.JobSegmentStrategyOption;

/**
 * 根据作业名的哈希值奇偶数决定IP升降序算法的分段策略.
 * 
 * <pre>
 * 作业名的哈希值为奇数则IP升序.
 * 作业名的哈希值为偶数则IP降序.
 * 用于不同的作业平均分配负载至不同的服务器.
 * 如: 
 * 1. 如果有3台服务器, 分成2段, 作业名称的哈希值为奇数, 则每台服务器分到的分段是: 1=[0], 2=[1], 3=[].
 * 2. 如果有3台服务器, 分成2段, 作业名称的哈希值为偶数, 则每台服务器分到的分段是: 3=[0], 2=[1], 1=[].
 * </pre>
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */
public class OdevitySortByNameJobSegmentStrategy implements JobSegmentStrategy {

	private AverageAllocationJobSegmentStrategy averageAllocationJobSegmentStrategy = new AverageAllocationJobSegmentStrategy();

	@Override
	public Map<String, List<Integer>> segment(final List<String> serversList, final JobSegmentStrategyOption option) {
		long jobNameHash = option.getJobName().hashCode();
		if (0 == jobNameHash % 2) {
			Collections.reverse(serversList);
		}
		return averageAllocationJobSegmentStrategy.segment(serversList, option);
	}
}
