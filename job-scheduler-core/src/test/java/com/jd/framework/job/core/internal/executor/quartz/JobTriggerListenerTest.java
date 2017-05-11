/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */     
package com.jd.framework.job.core.internal.executor.quartz;    

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.jd.framework.job.core.internal.service.ExecutionService;
import com.jd.framework.job.core.internal.service.SegmentService;
    
public class JobTriggerListenerTest {

	@Mock
    private ExecutionService executionService;
    
    @Mock
    private SegmentService segmentService;
    
    private JobTriggerListener jobTriggerListener;
    
    @Before
    public void setUp() throws NoSuchFieldException {
        MockitoAnnotations.initMocks(this);
        jobTriggerListener = new JobTriggerListener(executionService, segmentService);
    }
    
    @Test
    public void assertGetName() {
        assertThat(jobTriggerListener.getName(), is("JobTriggerListener"));
    }
    
    @Test
    public void assertTriggerMisfired() {
        when(segmentService.getLocalHostSegmentItems()).thenReturn(Collections.singletonList(0));
        jobTriggerListener.triggerMisfired(null);
		verify(segmentService).getLocalHostSegmentItems();
        verify(executionService).setMisfire(Collections.singletonList(0));
    }

}
  