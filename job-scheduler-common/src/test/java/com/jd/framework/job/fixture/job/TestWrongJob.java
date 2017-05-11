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

import lombok.RequiredArgsConstructor;

import com.jd.framework.job.api.SegmentContext;
import com.jd.framework.job.api.simple.SimpleJob;

@RequiredArgsConstructor
public class TestWrongJob implements SimpleJob {

	@Override
	public void execute(final SegmentContext segmentContext) {
		throw new RuntimeException("Wrong Job Exception.");
	}

}
