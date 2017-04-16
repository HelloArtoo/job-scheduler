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

import com.google.common.base.Strings;
import com.jd.framework.job.exception.JobConfigurationException;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 分段序列号自定义个性化参数工具
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */
@Getter
public final class SegmentItemParameters {

	private static final String PARAMETER_DELIMITER = ",";

	private static final String KEY_VALUE_DELIMITER = "=";

	private final Map<Integer, String> map;

	public SegmentItemParameters(final String segmentItemParameters) {
		map = toMap(segmentItemParameters);
	}

	private Map<Integer, String> toMap(final String originalSegmentItemParameters) {
		if (Strings.isNullOrEmpty(originalSegmentItemParameters)) {
			return Collections.emptyMap();
		}
		String[] segmentItemParameters = originalSegmentItemParameters.split(PARAMETER_DELIMITER);
		Map<Integer, String> result = new HashMap<>(segmentItemParameters.length);
		for (String each : segmentItemParameters) {
			SegmentItem segmentItem = this.parse(each, originalSegmentItemParameters);
			result.put(segmentItem.item, segmentItem.parameter);
		}
		return result;
	}

	private SegmentItem parse(final String segmentItemParameter, final String originalSegmentItemParameters) {
		String[] pair = segmentItemParameter.trim().split(KEY_VALUE_DELIMITER);
		if (2 != pair.length) {
			throw new JobConfigurationException("Segment item parameters '%s' format error, should be int=xx,int=xx",
					originalSegmentItemParameters);
		}
		try {
			return new SegmentItem(Integer.parseInt(pair[0].trim()), pair[1].trim());
		} catch (final NumberFormatException ex) {
			throw new JobConfigurationException("Segment item parameters key '%s' is not an integer.", pair[0]);
		}
	}

	/**
	 * 分段项.
	 */
	@AllArgsConstructor
	private static final class SegmentItem {

		private final int item;

		private final String parameter;
	}
}
