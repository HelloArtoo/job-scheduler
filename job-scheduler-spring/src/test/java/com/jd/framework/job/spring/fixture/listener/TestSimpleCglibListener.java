/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.spring.fixture.listener;

import com.jd.framework.job.core.api.listener.ScheduleJobListener;
import com.jd.framework.job.executor.context.SegmentContexts;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TestSimpleCglibListener implements ScheduleJobListener {

	@Override
	public void beforeJobExecuted(SegmentContexts segmentContexts) {
		assertThat(segmentContexts.getJobName(), is("testSpringSimpleJob_namespace_listener_cglib"));
	}

	@Override
	public void afterJobExecuted(SegmentContexts segmentContexts) {
		assertThat(segmentContexts.getJobName(), is("testSpringSimpleJob_namespace_listener_cglib"));
	}

}
