/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.fixture;

import java.util.HashMap;
import java.util.Map;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import com.jd.framework.job.executor.context.SegmentContexts;

/**
 * 
 * SegmentContext 辅助创建工具
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-15
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SegmentContextsBuilder {

	public static final String JOB_NAME = "test_job";

	public static SegmentContexts getSingleSegmentContexts() {
		Map<Integer, String> map = new HashMap<>(1, 1);
		map.put(0, "A");
		return new SegmentContexts("fake_task_id", JOB_NAME, 1, "", map);
	}

	public static SegmentContexts getMultipleSegmentContexts() {
		Map<Integer, String> map = new HashMap<>(2, 1);
		map.put(0, "A");
		map.put(1, "B");
		return new SegmentContexts("fake_task_id", JOB_NAME, 2, "", map);
	}
}
