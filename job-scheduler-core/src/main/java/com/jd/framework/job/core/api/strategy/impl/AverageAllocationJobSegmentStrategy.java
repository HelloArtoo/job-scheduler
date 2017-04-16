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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.jd.framework.job.core.api.strategy.JobSegmentStrategy;
import com.jd.framework.job.core.api.strategy.JobSegmentStrategyOption;

/**
 * 
 * 均分分配策略
 * 
 * <pre>
 * 如果分段不能整除, 则不能整除的多余分段将依次追加到序号小的服务器.
 * 如: 
 * 1. 如果有3台服务器, 分成9段, 则每台服务器分到的分段是: 1=[0,1,2], 2=[3,4,5], 3=[6,7,8].
 * 2. 如果有3台服务器, 分成8段, 则每台服务器分到的分段是: 1=[0,1,6], 2=[2,3,7], 3=[4,5].
 * 3. 如果有3台服务器, 分成10段, 则每台服务器分到的分段是: 1=[0,1,2,9], 2=[3,4,5], 3=[6,7,8].
 * </pre>
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */
public class AverageAllocationJobSegmentStrategy implements JobSegmentStrategy {

	@Override
	public Map<String, List<Integer>> segment(final List<String> serversList, final JobSegmentStrategyOption option) {
		if (serversList.isEmpty()) {
			return Collections.emptyMap();
		}
		Map<String, List<Integer>> result = segmentAliquot(serversList, option.getSegmentTotalCount());
		addAliquant(serversList, option.getSegmentTotalCount(), result);
		return result;
	}

	private Map<String, List<Integer>> segmentAliquot(final List<String> serversList, final int segmentTotalCount) {
		Map<String, List<Integer>> result = new LinkedHashMap<>(serversList.size());
		int itemCountPerSegment = segmentTotalCount / serversList.size();
		int count = 0;
		for (String each : serversList) {
			List<Integer> segmentItems = new ArrayList<>(itemCountPerSegment + 1);
			for (int i = count * itemCountPerSegment; i < (count + 1) * itemCountPerSegment; i++) {
				segmentItems.add(i);
			}
			result.put(each, segmentItems);
			count++;
		}
		return result;
	}

	private void addAliquant(final List<String> serversList, final int segmentTotalCount,
			final Map<String, List<Integer>> segmentResult) {
		int aliquant = segmentTotalCount % serversList.size();
		int count = 0;
		for (Entry<String, List<Integer>> entry : segmentResult.entrySet()) {
			if (count < aliquant) {
				entry.getValue().add(segmentTotalCount / serversList.size() * serversList.size() + count);
			}
			count++;
		}
	}
}
