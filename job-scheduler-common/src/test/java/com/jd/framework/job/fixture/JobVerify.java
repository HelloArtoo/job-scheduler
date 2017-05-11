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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import com.jd.framework.job.event.type.JobStatusTraceEvent;
import com.jd.framework.job.exception.JobExecutionEnvironmentException;
import com.jd.framework.job.executor.context.SegmentContexts;
import com.jd.framework.job.executor.facade.JobFacade;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JobVerify {

	public static void prepareForIsNotMisfire(final JobFacade jobFacade, final SegmentContexts segmentContexts) {
		when(jobFacade.getSegmentContexts()).thenReturn(segmentContexts);
		when(jobFacade.misfireIfNecessary(segmentContexts.getSegmentItemParameters().keySet())).thenReturn(false);
		when(jobFacade.isExecuteMisfired(segmentContexts.getSegmentItemParameters().keySet())).thenReturn(false);
	}

	public static void verifyForIsNotMisfire(final JobFacade jobFacade, final SegmentContexts segmentContexts) {
		try {
			verify(jobFacade).checkJobExecutionEnvironment();
		} catch (final JobExecutionEnvironmentException ex) {
			throw new RuntimeException(ex);
		}
		verify(jobFacade).getSegmentContexts();
		verify(jobFacade).postJobStatusTraceEvent(segmentContexts.getTaskId(), JobStatusTraceEvent.State.TASK_STAGING,
				"Job 'test_job' execute begin.");
		verify(jobFacade).misfireIfNecessary(segmentContexts.getSegmentItemParameters().keySet());
		verify(jobFacade).cleanPreviousExecutionInfo();
		verify(jobFacade).beforeJobExecuted(segmentContexts);
		verify(jobFacade).registerJobBegin(segmentContexts);
		verify(jobFacade).registerJobCompleted(segmentContexts);
		verify(jobFacade).isExecuteMisfired(segmentContexts.getSegmentItemParameters().keySet());
		verify(jobFacade).afterJobExecuted(segmentContexts);
	}
}
