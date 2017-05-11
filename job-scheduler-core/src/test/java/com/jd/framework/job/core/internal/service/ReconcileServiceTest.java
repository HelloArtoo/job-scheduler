/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */     
package com.jd.framework.job.core.internal.service;    

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.unitils.util.ReflectionUtils;

import com.jd.framework.job.config.core.JobCoreConfiguration;
import com.jd.framework.job.config.type.SimpleJobConfiguration;
import com.jd.framework.job.core.config.FactJobConfiguration;
import com.jd.framework.job.core.fixture.TestSimpleJob;
    
public class ReconcileServiceTest {
	
	@Mock
    private ConfigService configService;
    
    @Mock
    private SegmentService segmentService;
    
    @Mock
    private LeaderElectionService leaderElectionService;
    
    private final ReconcileService reconcileService = new ReconcileService(null, "job_test");
    
    @Before
    public void setup() throws NoSuchFieldException {
        MockitoAnnotations.initMocks(this);
        ReflectionUtils.setFieldValue(reconcileService, "lastReconcileTime", 1L);
        ReflectionUtils.setFieldValue(reconcileService, "configService", configService);
        ReflectionUtils.setFieldValue(reconcileService, "segmentService", segmentService);
        ReflectionUtils.setFieldValue(reconcileService, "leaderElectionService", leaderElectionService);
    }
    
    @Test
    public void assertReconcile() throws Exception {
        Mockito.when(configService.load(true)).thenReturn(FactJobConfiguration.newBuilder(new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).build(),
                TestSimpleJob.class.getCanonicalName())).reconcileIntervalMinutes(1).build());
        Mockito.when(segmentService.isNeedSegment()).thenReturn(false);
        Mockito.when(segmentService.hasNotRunningSegmentNode()).thenReturn(true);
        Mockito.when(leaderElectionService.isLeader()).thenReturn(true);
        reconcileService.runOneIteration();
        Mockito.verify(segmentService).isNeedSegment();
        Mockito.verify(segmentService).hasNotRunningSegmentNode();
        Mockito.verify(segmentService).setResegmentFlag();
        Mockito.verify(leaderElectionService).isLeader();
    }
}
  