/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.executor.type;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.jd.framework.job.event.type.JobStatusTraceEvent.State;
import com.jd.framework.job.executor.context.SegmentContexts;
import com.jd.framework.job.executor.facade.JobFacade;
import com.jd.framework.job.fixture.config.TestSimpleJobConfiguration;
import com.jd.framework.job.fixture.job.TestWrongJob;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExceptionJobExecutorTest {

	@Mock
	private JobFacade jobFacade;

	private SimpleJobExecutor wrongSimpleJobExecutor;

	@Before
	public void setUp() throws NoSuchFieldException {
		when(jobFacade.loadJobRootConfiguration(true)).thenReturn(new TestSimpleJobConfiguration());
		wrongSimpleJobExecutor = new SimpleJobExecutor(new TestWrongJob(), jobFacade);
	}

	@Test(expected = RuntimeException.class)
	public void assertWrongJobExecutorWithSingleItem() throws NoSuchFieldException {
		Map<Integer, String> map = new HashMap<>(1, 1);
		map.put(0, "A");
		SegmentContexts segmentContexts = new SegmentContexts("fake_task_id", "test_job", 10, "", map);
		when(jobFacade.getSegmentContexts()).thenReturn(segmentContexts);
		wrongSimpleJobExecutor.execute();
	}

	@Test
	public void assertWrongJobExecutorWithMultipleItems() throws NoSuchFieldException {
		Map<Integer, String> map = new HashMap<>(1, 1);
		map.put(0, "A");
		map.put(1, "B");
		SegmentContexts segmentContexts = new SegmentContexts("fake_task_id", "test_job", 10, "", map);
		when(jobFacade.getSegmentContexts()).thenReturn(segmentContexts);
		wrongSimpleJobExecutor.execute();
		verify(jobFacade).getSegmentContexts();
		verify(jobFacade).postJobStatusTraceEvent("fake_task_id", State.TASK_RUNNING, "");
	}

}
