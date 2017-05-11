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

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * 分段工具类
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */

@Getter
public class SegmentItems {

	private static final String DELIMITER = ",";

	/**
	 * 根据分段项字符串获取分段项列表.
	 * 
	 * @param itemsString
	 *            分段项字符串
	 * @return 分段项列表
	 */
	public static List<Integer> toItemList(final String itemsString) {
		if (Strings.isNullOrEmpty(itemsString)) {
			return Collections.emptyList();
		}
		String[] items = itemsString.split(DELIMITER);
		List<Integer> result = new ArrayList<>(items.length);
		for (String each : items) {
			int item = Integer.parseInt(each);
			if (!result.contains(item)) {
				result.add(item);
			}
		}
		return result;
	}

	/**
	 * 根据分段项列表获取分段项字符串.
	 * 
	 * @param items
	 *            分段项列表
	 * @return 分段项字符串
	 */
	public static String toItemsString(final List<Integer> items) {
		return items.isEmpty() ? "" : Joiner.on(DELIMITER).join(items);
	}
}
