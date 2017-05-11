/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.core.internal.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 
 * 作业分段节点助手
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SegmentNodeHelper {

	public static final String LEADER_SEGMENT_ROOT = ElectionNodeHelper.ROOT + "/segment";

	public static final String NECESSARY = LEADER_SEGMENT_ROOT + "/necessary";

	public static final String PROCESSING = LEADER_SEGMENT_ROOT + "/processing";

	private static final String SERVER_SEGMENT = ServerNodeHelper.ROOT + "/%s/segment";

	public static String getSegmentNode(final String ip) {
		return String.format(SERVER_SEGMENT, ip);
	}
}
