/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.fixture.job;

import java.util.List;

import lombok.RequiredArgsConstructor;

import com.jd.framework.job.api.SegmentContext;
import com.jd.framework.job.api.flow.FlowJob;

@RequiredArgsConstructor
public class TestFlowJob implements FlowJob<Object> {

	private final JobCaller jobCaller;

	@Override
	public List<Object> fetchData(final SegmentContext segmentContext) {
		return jobCaller.fetchData(segmentContext.getSegmentItem());
	}

	@Override
	public void processData(final SegmentContext segmentContext, final List<Object> data) {
		for (Object each : data) {
			jobCaller.processData(each);
		}
	}
}
