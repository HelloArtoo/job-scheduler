/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.core.fixture;

import java.util.List;

import com.jd.framework.job.api.SegmentContext;
import com.jd.framework.job.api.flow.FlowJob;

public class TestFlowJob implements FlowJob<Object> {

	@Override
	public List<Object> fetchData(SegmentContext segmentContext) {
		return null;
	}

	@Override
	public void processData(SegmentContext segmentContext, List<Object> data) {

	}

}
