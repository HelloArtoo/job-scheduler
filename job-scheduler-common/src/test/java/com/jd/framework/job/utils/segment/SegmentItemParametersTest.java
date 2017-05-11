/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.utils.segment;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.jd.framework.job.exception.JobConfigurationException;

public class SegmentItemParametersTest {

	@Test(expected = JobConfigurationException.class)
	public void assertInvalidParameters() {
		new SegmentItemParameters("xxx-xxx");
	}

	@Test(expected = JobConfigurationException.class)
	public void assertItemIsNotNumber() {
		new SegmentItemParameters("xxx=xxx");
	}

	@Test
	public void assertGetMapWhenIsEmpty() {
		assertThat(new SegmentItemParameters("").getMap(), is(Collections.EMPTY_MAP));
	}

	@Test
	public void assertGetMap() {
		Map<Integer, String> expected = new HashMap<>(3);
		expected.put(0, "北京");
		expected.put(1, "上海");
		expected.put(2, "广州");
		assertThat(new SegmentItemParameters("0=北京,1=上海,2=广州").getMap(), is(expected));
	}

}
