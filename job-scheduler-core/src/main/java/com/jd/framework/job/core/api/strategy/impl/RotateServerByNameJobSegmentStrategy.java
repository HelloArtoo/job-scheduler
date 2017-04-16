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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jd.framework.job.core.api.strategy.JobSegmentStrategy;
import com.jd.framework.job.core.api.strategy.JobSegmentStrategyOption;

/**
 * 
 * 根据作业名的哈希值对服务器列表进行轮转的分片策略.
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */
public class RotateServerByNameJobSegmentStrategy implements JobSegmentStrategy {

	private AverageAllocationJobSegmentStrategy averageAllocationJobShardingStrategy = new AverageAllocationJobSegmentStrategy();

	@Override
	public Map<String, List<Integer>> segment(List<String> serversList, JobSegmentStrategyOption option) {
		return averageAllocationJobShardingStrategy.segment(rotateServerList(serversList, option.getJobName()), option);
	}

	private List<String> rotateServerList(final List<String> serversList, final String jobName) {
		int serverSize = serversList.size();
		int offset = Math.abs(jobName.hashCode()) % serverSize;
		if (0 == offset) {
			return serversList;
		}
		List<String> result = new ArrayList<>(serverSize);
		for (int i = 0; i < serverSize; i++) {
			int index = (i + offset) % serverSize;
			result.add(serversList.get(index));
		}
		return result;
	}

}
