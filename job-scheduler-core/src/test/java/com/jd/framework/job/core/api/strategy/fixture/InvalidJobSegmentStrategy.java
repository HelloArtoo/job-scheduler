/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.core.api.strategy.fixture;

import java.util.List;
import java.util.Map;

import com.jd.framework.job.core.api.strategy.JobSegmentStrategy;
import com.jd.framework.job.core.api.strategy.JobSegmentStrategyOption;

public class InvalidJobSegmentStrategy implements JobSegmentStrategy {

	public InvalidJobSegmentStrategy(final String input) {
	}

	@Override
	public Map<String, List<Integer>> segment(final List<String> serversList, final JobSegmentStrategyOption option) {
		return null;
	}
}
