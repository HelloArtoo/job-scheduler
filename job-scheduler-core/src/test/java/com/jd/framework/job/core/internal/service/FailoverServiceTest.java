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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.unitils.util.ReflectionUtils;

import com.jd.framework.job.core.internal.executor.JobRegistry;
import com.jd.framework.job.core.internal.executor.quartz.JobScheduleController;
import com.jd.framework.job.core.internal.helper.JobNodeStorageHelper;
import com.jd.framework.job.core.internal.service.FailoverService.FailoverLeaderExecutionCallback;
import com.jd.framework.job.utils.env.LocalHostService;
    
public class FailoverServiceTest {
	
	@Mock
    private JobNodeStorageHelper jobNodeStorage;
    
    @Mock
    private LocalHostService localHostService;
    
    @Mock
    private ServerService serverService;
    
    @Mock
    private SegmentService segmentService;
    
    @Mock
    private JobScheduleController jobScheduleController;
    
    private final FailoverService failoverService = new FailoverService(null, "test_job");
    
    @Before
    public void setUp() throws NoSuchFieldException {
        MockitoAnnotations.initMocks(this);
        ReflectionUtils.setFieldValue(failoverService, "jobNodeStorage", jobNodeStorage);
        ReflectionUtils.setFieldValue(failoverService, "localHostService", localHostService);
        ReflectionUtils.setFieldValue(failoverService, "serverService", serverService);
        ReflectionUtils.setFieldValue(failoverService, "segmentService", segmentService);
        ReflectionUtils.setFieldValue(failoverService, "jobName", "test_job");
        when(localHostService.getIp()).thenReturn("mockedIP");
        when(localHostService.getHostName()).thenReturn("mockedHostName");
    }
    
    @Test
    public void assertSetCrashedFailoverFlagWhenItemIsNotAssigned() {
        when(jobNodeStorage.isJobNodeExisted("execution/0/failover")).thenReturn(true);
        failoverService.setCrashedFailoverFlag(0);
        verify(jobNodeStorage).isJobNodeExisted("execution/0/failover");
        verify(jobNodeStorage, times(0)).createJobNodeIfNeeded("leader/failover/items/0");
    }
    
    @Test
    public void assertSetCrashedFailoverFlagWhenItemIsAssigned() {
        when(jobNodeStorage.isJobNodeExisted("execution/0/failover")).thenReturn(false);
        failoverService.setCrashedFailoverFlag(0);
        verify(jobNodeStorage).isJobNodeExisted("execution/0/failover");
        verify(jobNodeStorage).createJobNodeIfNeeded("leader/failover/items/0");
    }
    
    @Test
    public void assertFailoverIfUnnecessaryWhenItemsRootNodeNotExisted() {
        when(jobNodeStorage.isJobNodeExisted("leader/failover/items")).thenReturn(false);
        failoverService.failoverIfNecessary();
        verify(jobNodeStorage).isJobNodeExisted("leader/failover/items");
        verify(jobNodeStorage, times(0)).executeInLeader(eq("leader/failover/latch"), Matchers.<FailoverLeaderExecutionCallback>any());
    }
    
    @Test
    public void assertFailoverIfUnnecessaryWhenItemsRootNodeIsEmpty() {
        when(jobNodeStorage.isJobNodeExisted("leader/failover/items")).thenReturn(true);
        when(jobNodeStorage.getJobNodeChildrenKeys("leader/failover/items")).thenReturn(Collections.<String>emptyList());
        failoverService.failoverIfNecessary();
        verify(jobNodeStorage).isJobNodeExisted("leader/failover/items");
        verify(jobNodeStorage).getJobNodeChildrenKeys("leader/failover/items");
        verify(jobNodeStorage, times(0)).executeInLeader(eq("leader/failover/latch"), Matchers.<FailoverLeaderExecutionCallback>any());
    }
    
    @Test
    public void assertFailoverIfUnnecessaryWhenServerIsNotReady() {
        when(jobNodeStorage.isJobNodeExisted("leader/failover/items")).thenReturn(true);
        when(jobNodeStorage.getJobNodeChildrenKeys("leader/failover/items")).thenReturn(Arrays.asList("0", "1", "2"));
        when(serverService.isLocalhostServerReady()).thenReturn(false);
        failoverService.failoverIfNecessary();
        verify(jobNodeStorage).isJobNodeExisted("leader/failover/items");
        verify(jobNodeStorage).getJobNodeChildrenKeys("leader/failover/items");
        verify(serverService).isLocalhostServerReady();
        verify(jobNodeStorage, times(0)).executeInLeader(eq("leader/failover/latch"), Matchers.<FailoverLeaderExecutionCallback>any());
    }
    
    @Test
    public void assertFailoverIfNecessary() {
        when(jobNodeStorage.isJobNodeExisted("leader/failover/items")).thenReturn(true);
        when(jobNodeStorage.getJobNodeChildrenKeys("leader/failover/items")).thenReturn(Arrays.asList("0", "1", "2"));
        when(serverService.isLocalhostServerReady()).thenReturn(true);
        failoverService.failoverIfNecessary();
        verify(jobNodeStorage).isJobNodeExisted("leader/failover/items");
        verify(jobNodeStorage).getJobNodeChildrenKeys("leader/failover/items");
        verify(serverService).isLocalhostServerReady();
        verify(jobNodeStorage).executeInLeader(eq("leader/failover/latch"), Matchers.<FailoverLeaderExecutionCallback>any());
    }
    
    @Test
    public void assertFailoverLeaderExecutionCallbackIfNotNecessary() {
        when(jobNodeStorage.isJobNodeExisted("leader/failover/items")).thenReturn(false);
        failoverService.new FailoverLeaderExecutionCallback().execute();
        verify(jobNodeStorage).isJobNodeExisted("leader/failover/items");
        verify(jobNodeStorage, times(0)).getJobNodeChildrenKeys("leader/failover/items");
    }
    
    @Test
    public void assertFailoverLeaderExecutionCallbackIfNecessary() {
        when(jobNodeStorage.isJobNodeExisted("leader/failover/items")).thenReturn(true);
        when(jobNodeStorage.getJobNodeChildrenKeys("leader/failover/items")).thenReturn(Arrays.asList("0", "1", "2"));
        when(serverService.isLocalhostServerReady()).thenReturn(true);
        JobRegistry.getInstance().addJobScheduleController("test_job", jobScheduleController);
        failoverService.new FailoverLeaderExecutionCallback().execute();
        verify(jobNodeStorage).isJobNodeExisted("leader/failover/items");
        verify(jobNodeStorage, times(2)).getJobNodeChildrenKeys("leader/failover/items");
        verify(serverService).isLocalhostServerReady();
        verify(jobNodeStorage).fillEphemeralJobNode("execution/0/failover", "mockedIP");
        verify(jobNodeStorage).removeJobNodeIfExisted("leader/failover/items/0");
        verify(jobScheduleController).triggerJob();
    }
    
    @Test
    public void assertUpdateFailoverComplete() {
        failoverService.updateFailoverComplete(Arrays.asList(0, 1));
        verify(jobNodeStorage).removeJobNodeIfExisted("execution/0/failover");
        verify(jobNodeStorage).removeJobNodeIfExisted("execution/1/failover");
    }
    
    @Test
    public void assertGetLocalHostFailoverItems() {
        when(jobNodeStorage.getJobNodeChildrenKeys("execution")).thenReturn(Arrays.asList("0", "1", "2"));
        when(jobNodeStorage.isJobNodeExisted("execution/0/failover")).thenReturn(true);
        when(jobNodeStorage.isJobNodeExisted("execution/1/failover")).thenReturn(true);
        when(jobNodeStorage.isJobNodeExisted("execution/2/failover")).thenReturn(false);
        when(jobNodeStorage.getJobNodeDataDirectly("execution/0/failover")).thenReturn("mockedIP");
        when(jobNodeStorage.getJobNodeDataDirectly("execution/1/failover")).thenReturn("otherIP");
        assertThat(failoverService.getLocalHostFailoverItems(), is(Collections.singletonList(0)));
        verify(jobNodeStorage).getJobNodeChildrenKeys("execution");
        verify(localHostService).getIp();
        verify(jobNodeStorage).isJobNodeExisted("execution/0/failover");
        verify(jobNodeStorage).isJobNodeExisted("execution/1/failover");
        verify(jobNodeStorage).getJobNodeDataDirectly("execution/0/failover");
        verify(jobNodeStorage).getJobNodeDataDirectly("execution/1/failover");
    }
    
    @Test
    public void assertGetLocalHostTakeOffItems() {
        when(segmentService.getLocalHostSegmentItems()).thenReturn(Arrays.asList(0, 1, 2));
        when(jobNodeStorage.isJobNodeExisted("execution/0/failover")).thenReturn(true);
        when(jobNodeStorage.isJobNodeExisted("execution/1/failover")).thenReturn(true);
        when(jobNodeStorage.isJobNodeExisted("execution/2/failover")).thenReturn(false);
        assertThat(failoverService.getLocalHostTakeOffItems(), is(Arrays.asList(0, 1)));
        verify(segmentService).getLocalHostSegmentItems();
        verify(jobNodeStorage).isJobNodeExisted("execution/0/failover");
        verify(jobNodeStorage).isJobNodeExisted("execution/1/failover");
        verify(jobNodeStorage).isJobNodeExisted("execution/2/failover");
    }
    
    @Test
    public void assertRemoveFailoverInfo() {
        when(jobNodeStorage.getJobNodeChildrenKeys("execution")).thenReturn(Arrays.asList("0", "1", "2"));
        failoverService.removeFailoverInfo();
        verify(jobNodeStorage).getJobNodeChildrenKeys("execution");
        verify(jobNodeStorage).removeJobNodeIfExisted("execution/0/failover");
        verify(jobNodeStorage).removeJobNodeIfExisted("execution/1/failover");
        verify(jobNodeStorage).removeJobNodeIfExisted("execution/2/failover");
    }

}
  