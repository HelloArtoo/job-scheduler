/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.core.integrate.fixture.flow;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import com.jd.framework.job.api.SegmentContext;
import com.jd.framework.job.api.flow.FlowJob;

public class TestOneOffFlowJob implements FlowJob<String> {

	private static volatile Set<String> processedData = new CopyOnWriteArraySet<>();

	private static volatile List<String> result = Arrays.asList("data0", "data1", "data2", "data3", "data4", "data5",
			"data6", "data7", "data8", "data9");

	@Override
	public List<String> fetchData(SegmentContext segmentContext) {
		return result;
	}

	@Override
	public void processData(SegmentContext segmentContext, List<String> data) {
		for (String each : data) {
			processedData.add(each);
		}

	}

	public static boolean isCompleted() {
		return result.size() == processedData.size();
	}

	public static void reset() {
		processedData.clear();
	}

}
