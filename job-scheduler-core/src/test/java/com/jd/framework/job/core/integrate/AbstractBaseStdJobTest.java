/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.core.integrate;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import lombok.AccessLevel;
import lombok.Getter;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.quartz.SchedulerException;
import org.unitils.util.ReflectionUtils;

import com.jd.framework.job.api.ScheduleJob;
import com.jd.framework.job.api.flow.FlowJob;
import com.jd.framework.job.config.JobTypeConfiguration;
import com.jd.framework.job.config.core.JobCoreConfiguration;
import com.jd.framework.job.config.type.FlowJobConfiguration;
import com.jd.framework.job.config.type.SimpleJobConfiguration;
import com.jd.framework.job.constant.job.ServerStatus;
import com.jd.framework.job.core.api.JobScheduler;
import com.jd.framework.job.core.api.listener.AbstractOneOffJobListener;
import com.jd.framework.job.core.api.listener.ScheduleJobListener;
import com.jd.framework.job.core.config.FactJobConfiguration;
import com.jd.framework.job.core.integrate.fixture.IgnoreExceptionHandler;
import com.jd.framework.job.core.internal.executor.JobRegistry;
import com.jd.framework.job.core.internal.executor.quartz.JobScheduleController;
import com.jd.framework.job.core.internal.factory.FactJobConfigGsonFactory;
import com.jd.framework.job.core.internal.service.LeaderElectionService;
import com.jd.framework.job.executor.context.SegmentContexts;
import com.jd.framework.job.executor.handler.JobProperties;
import com.jd.framework.job.regcenter.ZookeeperRegistryCenter;
import com.jd.framework.job.regcenter.api.CoordinatorRegistryCenter;
import com.jd.framework.job.regcenter.conf.ZookeeperConfiguration;
import com.jd.framework.job.utils.concurrent.BlockUtils;
import com.jd.framework.job.utils.env.LocalHostService;

public abstract class AbstractBaseStdJobTest {

	private static ZookeeperConfiguration zkConfig = new ZookeeperConfiguration(
			EmbedTestingServer.getConnectionString(), "zkRegTestCenter");

	@Getter(value = AccessLevel.PROTECTED)
	private static CoordinatorRegistryCenter regCenter = new ZookeeperRegistryCenter(zkConfig);

	@Getter(AccessLevel.PROTECTED)
	private final LocalHostService localHostService = new LocalHostService();

	@Getter(AccessLevel.PROTECTED)
	private final FactJobConfiguration factJobConfig;

	private final JobScheduler jobScheduler;

	private final boolean disabled;

	private final int monitorPort;

	private final LeaderElectionService leaderElectionService;

	@Getter(AccessLevel.PROTECTED)
	private final String jobName = System.nanoTime() + "_test_job";

	protected AbstractBaseStdJobTest(final Class<? extends ScheduleJob> scheduleJobClass, final boolean disabled) {
		this.disabled = disabled;
		factJobConfig = initJobConfig(scheduleJobClass);
		jobScheduler = new JobScheduler(regCenter, factJobConfig, new ScheduleJobListener() {

			@Override
			public void beforeJobExecuted(final SegmentContexts segmentContexts) {
				regCenter.persist("/" + jobName + "/listener/every", "test");
			}

			@Override
			public void afterJobExecuted(final SegmentContexts segmentContexts) {
			}
		}, new AbstractOneOffJobListener(-1L, -1L) {

			@Override
			public void doBeforeJobExecutedAtLastStarted(final SegmentContexts segmentContexts) {
				regCenter.persist("/" + jobName + "/listener/once", "test");
			}

			@Override
			public void doAfterJobExecutedAtLastCompleted(final SegmentContexts segmentContexts) {
			}
		});
		monitorPort = -1;
		leaderElectionService = new LeaderElectionService(regCenter, jobName);
	}

	protected AbstractBaseStdJobTest(final Class<? extends ScheduleJob> scheduleJobClass, final int monitorPort) {
		this.monitorPort = monitorPort;
		factJobConfig = initJobConfig(scheduleJobClass);
		jobScheduler = new JobScheduler(regCenter, factJobConfig);
		disabled = false;
		leaderElectionService = new LeaderElectionService(regCenter, jobName);
	}

	private FactJobConfiguration initJobConfig(final Class<? extends ScheduleJob> scheduleJobClass) {
		String cron = "0/1 * * * * ?";
		int totalSegmentCount = 3;
		String segmentParameters = "0=A,1=B,2=C";
		JobCoreConfiguration jobCoreConfig = JobCoreConfiguration
				.newBuilder(jobName, cron, totalSegmentCount)
				.segmentItemParameters(segmentParameters)
				.jobProperties(JobProperties.JobPropertiesEnum.JOB_EXCEPTION_HANDLER.getKey(),
						IgnoreExceptionHandler.class.getCanonicalName()).build();
		JobTypeConfiguration jobTypeConfig;
		if (FlowJob.class.isAssignableFrom(scheduleJobClass)) {
			jobTypeConfig = new FlowJobConfiguration(jobCoreConfig, scheduleJobClass.getCanonicalName(), false);
		} /*
		 * else if (ScriptJob.class.isAssignableFrom(scheduleJobClass)) {
		 * jobTypeConfig = new ScriptJobConfiguration(jobCoreConfig,
		 * AbstractBaseStdJobTest.class.getResource(
		 * "/script/test.sh").getPath()); }
		 */else {
			jobTypeConfig = new SimpleJobConfiguration(jobCoreConfig, scheduleJobClass.getCanonicalName());
		}
		return FactJobConfiguration.newBuilder(jobTypeConfig).monitorPort(monitorPort).disabled(disabled)
				.overwrite(true).build();
	}

	@BeforeClass
	public static void init() {
		EmbedTestingServer.start();
		zkConfig.setConnectionTimeoutMilliseconds(30000);
		regCenter.init();
	}

	@Before
	public void setUp() {
		regCenter.init();
	}

	@After
	public void tearDown() throws SchedulerException, NoSuchFieldException {
		JobScheduleController jobScheduleController = JobRegistry.getInstance().getJobScheduleController(jobName);
		if (null != jobScheduleController) {
			JobRegistry.getInstance().getJobScheduleController(jobName).shutdown();
		}
		ReflectionUtils.setFieldValue(JobRegistry.getInstance(), "instance", null);
	}

	protected void initJob() {
		jobScheduler.init();
	}

	void assertRegCenterCommonInfoWithEnabled() {
		assertRegCenterCommonInfo();
		assertTrue(leaderElectionService.isLeader());
	}

	protected void assertRegCenterCommonInfoWithDisabled() {
		assertRegCenterCommonInfo();
		assertFalse(leaderElectionService.isLeader());
	}

	private void assertRegCenterCommonInfo() {
		FactJobConfiguration factJobConfig = FactJobConfigGsonFactory
				.fromJson(regCenter.get("/" + jobName + "/config"));
		assertThat(factJobConfig.getTypeConfig().getCoreConfig().getSegmentTotalCount(), is(3));
		assertThat(factJobConfig.getTypeConfig().getCoreConfig().getSegmentItemParameters(), is("0=A,1=B,2=C"));
		assertThat(factJobConfig.getTypeConfig().getCoreConfig().getCron(), is("0/1 * * * * ?"));
		assertThat(regCenter.get("/" + jobName + "/servers/" + localHostService.getIp() + "/hostName"),
				is(localHostService.getHostName()));
		if (disabled) {
			assertTrue(regCenter.isExisted("/" + jobName + "/servers/" + localHostService.getIp() + "/disabled"));
			while (null != regCenter.get("/" + jobName + "/leader/election/host")) {
				BlockUtils.waitingShortTime();
			}
		} else {
			assertFalse(regCenter.isExisted("/" + jobName + "/servers/" + localHostService.getIp() + "/disabled"));
			assertThat(regCenter.get("/" + jobName + "/leader/election/host"), is(localHostService.getIp()));
		}
		assertFalse(regCenter.isExisted("/" + jobName + "/servers/" + localHostService.getIp() + "/paused"));
		assertThat(regCenter.get("/" + jobName + "/servers/" + localHostService.getIp() + "/status"),
				CoreMatchers.is(ServerStatus.READY.name()));
		regCenter.remove("/" + jobName + "/leader/election");
	}

	void assertRegCenterListenerInfo() {
		assertThat(regCenter.get("/" + jobName + "/listener/once"), is("test"));
		assertThat(regCenter.get("/" + jobName + "/listener/every"), is("test"));
	}
}
