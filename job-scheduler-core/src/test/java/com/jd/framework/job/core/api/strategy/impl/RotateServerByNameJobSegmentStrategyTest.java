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

import com.jd.framework.job.core.api.strategy.JobSegmentStrategyOption;

/**
 * 
 * 根据作业名的哈希值对服务器列表进行轮转的分段策略.
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-29
 */
public class RotateServerByNameJobSegmentStrategyTest {

	private RotateServerByNameJobSegmentStrategy rotateServerByNameJobSegmentStrategy = new RotateServerByNameJobSegmentStrategy();

	@Test
	public void assertSegment1() {
		Map<String, List<Integer>> expected = new LinkedHashMap<>(3);
		expected.put("host0", Collections.<Integer> emptyList());
		expected.put("host1", Collections.singletonList(0));
		expected.put("host2", Collections.singletonList(1));
		assertThat(rotateServerByNameJobSegmentStrategy.segment(Arrays.asList("host0", "host1", "host2"),
				new JobSegmentStrategyOption("1", 2)), is(expected));
	}

	@Test
	public void assertSegment2() {
		Map<String, List<Integer>> expected = new LinkedHashMap<>(3);
		expected.put("host0", Collections.singletonList(1));
		expected.put("host1", Collections.<Integer> emptyList());
		expected.put("host2", Collections.singletonList(0));
		assertThat(rotateServerByNameJobSegmentStrategy.segment(Arrays.asList("host0", "host1", "host2"),
				new JobSegmentStrategyOption("2", 2)), is(expected));
	}

	@Test
	public void assertSegment3() {
		Map<String, List<Integer>> expected = new LinkedHashMap<>(3);
		expected.put("host0", Collections.singletonList(0));
		expected.put("host1", Collections.singletonList(1));
		expected.put("host2", Collections.<Integer> emptyList());
		assertThat(rotateServerByNameJobSegmentStrategy.segment(Arrays.asList("host0", "host1", "host2"),
				new JobSegmentStrategyOption("3", 2)), is(expected));
	}

}
