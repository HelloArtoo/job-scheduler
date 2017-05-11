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

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

public class SegmentItemsTest {

	@Test
	public void assertTtoItemListWhenNull() {
		assertThat(SegmentItems.toItemList(null), is(Collections.EMPTY_LIST));
	}

	@Test
	public void assertToItemListWhenEmpty() {
		assertThat(SegmentItems.toItemList(""), is(Collections.EMPTY_LIST));
	}

	@Test
	public void assertToItemList() {
		assertThat(SegmentItems.toItemList("0,1,2"), is(Arrays.asList(0, 1, 2)));
	}

	@Test
	public void assertToItemListForDuplicated() {
		assertThat(SegmentItems.toItemList("0,1,2,2"), is(Arrays.asList(0, 1, 2)));
	}

	@Test
	public void assertToItemsStringWhenEmpty() {
		assertThat(SegmentItems.toItemsString(Collections.<Integer> emptyList()), is(""));
	}

	@Test
	public void assertToItemsString() {
		assertThat(SegmentItems.toItemsString(Arrays.asList(0, 1, 2)), is("0,1,2"));
	}

}
