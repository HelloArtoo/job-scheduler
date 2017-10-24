/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */     
package com.jd.framework.job.core.internal;    

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.jd.framework.job.core.internal.executor.JobExecutorTest;
import com.jd.framework.job.core.internal.executor.JobRegistryTest;
import com.jd.framework.job.core.internal.executor.RegCenterRegistryTest;
import com.jd.framework.job.core.internal.executor.quartz.JobScheduleControllerTest;
import com.jd.framework.job.core.internal.executor.quartz.JobTriggerListenerTest;
import com.jd.framework.job.core.internal.facade.FactJobFacadeTest;
import com.jd.framework.job.core.internal.facade.SchedulerFacadeTest;
import com.jd.framework.job.core.internal.factory.FactJobConfigGsonFactoryTest;
import com.jd.framework.job.core.internal.helper.ConfigNodeHelperTest;
import com.jd.framework.job.core.internal.helper.ElectionNodeHelperTest;
import com.jd.framework.job.core.internal.helper.ExecutionNodeHelperTest;
import com.jd.framework.job.core.internal.helper.FailoverNodeHelperTest;
import com.jd.framework.job.core.internal.helper.GuaranteeNodeHelperTest;
import com.jd.framework.job.core.internal.helper.JobNodePathHelperTest;
import com.jd.framework.job.core.internal.helper.JobNodeStorageHelperTest;
import com.jd.framework.job.core.internal.helper.SegmentNodeHelperTest;
import com.jd.framework.job.core.internal.helper.ServerNodeHelperTest;
import com.jd.framework.job.core.internal.listener.ListenerManagerTest;
import com.jd.framework.job.core.internal.listener.parent.JobListenerTest;
import com.jd.framework.job.core.internal.listener.sub.ConfigListenerManagerTest;
import com.jd.framework.job.core.internal.listener.sub.ElectionListenerManagerTest;
import com.jd.framework.job.core.internal.listener.sub.ExecutionListenerManagerTest;
import com.jd.framework.job.core.internal.listener.sub.FailoverListenerManagerTest;
import com.jd.framework.job.core.internal.listener.sub.GuaranteeListenerManagerTest;
import com.jd.framework.job.core.internal.listener.sub.JobOperationListenerManagerTest;
import com.jd.framework.job.core.internal.listener.sub.SegmentListenerManagerTest;
import com.jd.framework.job.core.internal.service.ConfigServiceTest;
import com.jd.framework.job.core.internal.service.ExecutionContextServiceTest;
import com.jd.framework.job.core.internal.service.ExecutionServiceTest;
import com.jd.framework.job.core.internal.service.FailoverServiceTest;
import com.jd.framework.job.core.internal.service.GuaranteeServiceTest;
import com.jd.framework.job.core.internal.service.LeaderElectionServiceTest;
import com.jd.framework.job.core.internal.service.MonitorServiceDisableTest;
import com.jd.framework.job.core.internal.service.MonitorServiceEnableTest;
import com.jd.framework.job.core.internal.service.ReconcileServiceTest;
import com.jd.framework.job.core.internal.service.SegmentServiceTest;
import com.jd.framework.job.core.internal.service.ServerServiceTest;
    
@RunWith(Suite.class)
@SuiteClasses({
	//QUARTZ
	JobScheduleControllerTest.class,
	JobTriggerListenerTest.class,
	JobExecutorTest.class,
	JobRegistryTest.class,
	RegCenterRegistryTest.class,
	//FACADE
	FactJobFacadeTest.class,
	SchedulerFacadeTest.class,
	//FACTORY
	FactJobConfigGsonFactoryTest.class,
	//HELPER
	ConfigNodeHelperTest.class,
	ElectionNodeHelperTest.class,
	ExecutionNodeHelperTest.class,
	FailoverNodeHelperTest.class,
	GuaranteeNodeHelperTest.class,
	JobNodePathHelperTest.class,
	JobNodeStorageHelperTest.class,
	SegmentNodeHelperTest.class,
	ServerNodeHelperTest.class,
	//LISTENER
	JobListenerTest.class,
	ListenerManagerTest.class,
	ConfigListenerManagerTest.class,
	ElectionListenerManagerTest.class,
	ExecutionListenerManagerTest.class,
	FailoverListenerManagerTest.class,
	GuaranteeListenerManagerTest.class,
	JobOperationListenerManagerTest.class,
	SegmentListenerManagerTest.class,
	ListenerManagerTest.class,
	
	//SERVICE
	ConfigServiceTest.class,
	ExecutionContextServiceTest.class,
	ExecutionServiceTest.class,
	FailoverServiceTest.class,
	GuaranteeServiceTest.class,
	LeaderElectionServiceTest.class,
	MonitorServiceDisableTest.class,
	MonitorServiceEnableTest.class,
	ReconcileServiceTest.class,
	SegmentServiceTest.class,
	ServerServiceTest.class,	
})
public class AllInternalTests {
	// TEST THIS , TEST INTERNAL ALL
}
  