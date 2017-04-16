/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.api;

import org.junit.Assert;
import org.junit.Test;

import com.jd.framework.job.executor.context.SegmentContexts;
import com.jd.framework.job.fixture.SegmentContextsBuilder;

public final class SegmentContextTest {

	@Test
	public void assertNew() {
		SegmentContexts segmentContexts = SegmentContextsBuilder.getMultipleSegmentContexts();
		SegmentContext actual = new SegmentContext(segmentContexts, 1);
		// 分段项
		int item = 1;
		Assert.assertEquals(segmentContexts.getJobName(), actual.getJobName());
		Assert.assertEquals(segmentContexts.getTaskId(), actual.getTaskId());
		Assert.assertEquals(segmentContexts.getSegmentsSum(), actual.getSegmentsSum());
		Assert.assertEquals(segmentContexts.getJobParameter(), actual.getJobParameter());
		Assert.assertEquals(item, actual.getSegmentItem());
		Assert.assertEquals(segmentContexts.getSegmentItemParameters().get(item), actual.getSegmentParameter());
	}

	@Test
	public void assertToString() {
		Assert.assertEquals(
				"SegmentContext(segmentItem=1, segmentsSum=2, jobName=test_job, taskId=fake_task_id, jobParameter=, segmentParameter=B)",
				new SegmentContext(SegmentContextsBuilder.getMultipleSegmentContexts(), 1).toString());
	}

}
