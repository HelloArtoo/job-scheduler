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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import javax.annotation.Resource;

import com.jd.framework.job.core.api.listener.AbstractOneOffJobListener;
import com.jd.framework.job.executor.context.SegmentContexts;
import com.jd.framework.job.spring.fixture.service.XService;

public class TestSimpleOnceListener extends AbstractOneOffJobListener {

	@Resource
	private XService xService;

	private final long startedTimeoutMilliseconds;

	private final long completedTimeoutMilliseconds;

	public TestSimpleOnceListener(final long startedTimeoutMilliseconds, final long completedTimeoutMilliseconds) {
		super(startedTimeoutMilliseconds, completedTimeoutMilliseconds);
		this.startedTimeoutMilliseconds = startedTimeoutMilliseconds;
		this.completedTimeoutMilliseconds = completedTimeoutMilliseconds;
	}

	@Override
	public void doBeforeJobExecutedAtLastStarted(final SegmentContexts segmentContexts) {
		assertThat(startedTimeoutMilliseconds, is(10000L));
		assertThat(xService.xxx(), is("this is x service."));
	}

	@Override
	public void doAfterJobExecutedAtLastCompleted(final SegmentContexts segmentContexts) {
		assertThat(completedTimeoutMilliseconds, is(20000L));
	}

}
