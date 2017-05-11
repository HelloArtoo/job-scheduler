/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */     
package com.jd.framework.job.core.internal.facade;    

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.unitils.util.ReflectionUtils;

import com.jd.framework.job.config.core.JobCoreConfiguration;
import com.jd.framework.job.config.type.FlowJobConfiguration;
import com.jd.framework.job.core.api.listener.ScheduleJobListener;
import com.jd.framework.job.core.config.FactJobConfiguration;
import com.jd.framework.job.core.fixture.JobConfigUtils;
import com.jd.framework.job.core.fixture.TestFlowJob;
import com.jd.framework.job.core.internal.executor.quartz.JobTriggerListener;
import com.jd.framework.job.core.internal.listener.ListenerManager;
import com.jd.framework.job.core.internal.service.ConfigService;
import com.jd.framework.job.core.internal.service.ExecutionService;
import com.jd.framework.job.core.internal.service.LeaderElectionService;
import com.jd.framework.job.core.internal.service.MonitorService;
import com.jd.framework.job.core.internal.service.SegmentService;
import com.jd.framework.job.core.internal.service.ServerService;
    
public class SchedulerFacadeTest {

	@Mock
    private ConfigService configService;
    
    @Mock
    private LeaderElectionService leaderElectionService;
    
    @Mock
    private ServerService serverService;
    
    @Mock
    private SegmentService segmentService;
    
    @Mock
    private ExecutionService executionService;
    
    @Mock
    private MonitorService monitorService;
    
    @Mock
    private ListenerManager listenerManager;
    
    private final FactJobConfiguration factJobConfig = JobConfigUtils.createFlowFactJobConfiguration();
    
    private SchedulerFacade schedulerFacade;
    
    @Before
    public void setUp() throws NoSuchFieldException {
        MockitoAnnotations.initMocks(this);
        schedulerFacade = new SchedulerFacade(null, "test_job", Collections.<ScheduleJobListener>emptyList());
        when(configService.load(true)).thenReturn(FactJobConfiguration.newBuilder(new FlowJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).build(),
                TestFlowJob.class.getCanonicalName(), false)).build());
        ReflectionUtils.setFieldValue(schedulerFacade, "configService", configService);
        ReflectionUtils.setFieldValue(schedulerFacade, "leaderElectionService", leaderElectionService);
        ReflectionUtils.setFieldValue(schedulerFacade, "serverService", serverService);
        ReflectionUtils.setFieldValue(schedulerFacade, "segmentService", segmentService);
        ReflectionUtils.setFieldValue(schedulerFacade, "executionService", executionService);
        ReflectionUtils.setFieldValue(schedulerFacade, "monitorService", monitorService);
        ReflectionUtils.setFieldValue(schedulerFacade, "listenerManager", listenerManager);
    }
    
    @Test
    public void assertClearPreviousServerStatus() {
        schedulerFacade.clearPreviousServerStatus();
        verify(serverService).clearPreviousServerStatus();
    }
    
    @Test
    public void assertRegisterStartUpInfo() {
        when(configService.load(false)).thenReturn(FactJobConfiguration.newBuilder(new FlowJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).build(),
                TestFlowJob.class.getCanonicalName(), false)).build());
        schedulerFacade.registerStartUpInfo(factJobConfig);
        verify(listenerManager).startAllListeners();
        verify(leaderElectionService).leaderForceElection();
        verify(configService).persist(factJobConfig);
        verify(serverService).persistServerOnline(true);
        verify(serverService).clearJobPausedStatus();
        verify(segmentService).setResegmentFlag();
        verify(monitorService).listen();
        verify(configService).load(false);
        verify(listenerManager).setCurrentSegmentTotalCount(3);
    }
    
    @Test
    public void assertReleaseJobResource() {
        schedulerFacade.releaseJobResource();
        verify(monitorService).close();
        verify(serverService).removeServerStatus();
    }
    
    @Test
    public void assertLoadJobConfiguration() {
        FactJobConfiguration expected = FactJobConfiguration.newBuilder(null).build();
        when(configService.load(false)).thenReturn(expected);
        assertThat(schedulerFacade.loadJobConfiguration(), is(expected));
    }
    
    @Test
    public void assertNewJobTriggerListener() {
        assertThat(schedulerFacade.newJobTriggerListener(), instanceOf(JobTriggerListener.class));
    }

}
  