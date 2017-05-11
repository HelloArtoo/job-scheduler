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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.jd.framework.job.core.api.strategy.JobSegmentStrategy;
import com.jd.framework.job.core.api.strategy.JobSegmentStrategyOption;

/**
 * 
 * 均分切割
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-29
 */
public class AverageAllocationJobSegmentStrategyTest {

	private final JobSegmentStrategy jobSegmentStrategy = new AverageAllocationJobSegmentStrategy();

	@Test
	public void segmentForZeroServer() {
		assertThat(jobSegmentStrategy.segment(Collections.<String> emptyList(), getJobSegmentStrategyOption(3)),
				is(Collections.EMPTY_MAP));
	}

	@Test
	public void segmentForOneServer() {
		Map<String, List<Integer>> expected = new LinkedHashMap<>(1);
		expected.put("host0", Arrays.asList(0, 1, 2));
		assertThat(jobSegmentStrategy.segment(Collections.singletonList("host0"), getJobSegmentStrategyOption(3)),
				is(expected));
	}

	/**
	 * 前俩分了
	 * 
	 * @author Rong Hu
	 */
	@Test
	public void segmentForServersMoreThanSegmentCount() {
		Map<String, List<Integer>> expected = new LinkedHashMap<>(3);
		expected.put("host0", Collections.singletonList(0));
		expected.put("host1", Collections.singletonList(1));
		expected.put("host2", Collections.<Integer> emptyList());
		assertThat(
				jobSegmentStrategy.segment(Arrays.asList("host0", "host1", "host2"), getJobSegmentStrategyOption(2)),
				is(expected));
	}

	/**
	 * 
	 * 
	 * @author Rong Hu
	 */
	@Test
	public void segmentForServersLessThanSegmentCountAliquot() {
		Map<String, List<Integer>> expected = new LinkedHashMap<>(3);
		expected.put("host0", Arrays.asList(0, 1, 2));
		expected.put("host1", Arrays.asList(3, 4, 5));
		expected.put("host2", Arrays.asList(6, 7, 8));
		assertThat(
				jobSegmentStrategy.segment(Arrays.asList("host0", "host1", "host2"), getJobSegmentStrategyOption(9)),
				is(expected));
	}

	@Test
	public void segmentForServersLessThanSegmentCountAliquantFor8SegmentCountAnd3Servers() {
		Map<String, List<Integer>> expected = new LinkedHashMap<>(3);
		expected.put("host0", Arrays.asList(0, 1, 6));
		expected.put("host1", Arrays.asList(2, 3, 7));
		expected.put("host2", Arrays.asList(4, 5));
		assertThat(
				jobSegmentStrategy.segment(Arrays.asList("host0", "host1", "host2"), getJobSegmentStrategyOption(8)),
				is(expected));
	}

	@Test
	public void segmentForServersLessThanSegmentCountAliquantFor10SegmentCountAnd3Servers() {
		Map<String, List<Integer>> expected = new LinkedHashMap<>(3);
		expected.put("host0", Arrays.asList(0, 1, 2, 9));
		expected.put("host1", Arrays.asList(3, 4, 5));
		expected.put("host2", Arrays.asList(6, 7, 8));
		assertThat(
				jobSegmentStrategy.segment(Arrays.asList("host0", "host1", "host2"), getJobSegmentStrategyOption(10)),
				is(expected));
	}

	private JobSegmentStrategyOption getJobSegmentStrategyOption(final int segmentTotalCount) {
		return new JobSegmentStrategyOption("test_job", segmentTotalCount);
	}

}
