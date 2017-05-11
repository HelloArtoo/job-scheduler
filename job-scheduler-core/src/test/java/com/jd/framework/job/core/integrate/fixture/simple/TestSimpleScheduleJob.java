/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.core.integrate.fixture.simple;

import lombok.Getter;

import com.jd.framework.job.api.SegmentContext;
import com.jd.framework.job.api.simple.SimpleJob;

public class TestSimpleScheduleJob implements SimpleJob {

	@Getter
	private static volatile boolean completed;

	@Override
	public void execute(SegmentContext segmentContext) {
		completed = true;

	}

	public static void reset() {
		completed = false;
	}

}
