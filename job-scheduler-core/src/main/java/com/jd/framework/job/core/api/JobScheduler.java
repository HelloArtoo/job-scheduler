/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.core.api;

import java.util.Arrays;
import java.util.Properties;

import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.plugins.management.ShutdownHookPlugin;

import com.google.common.base.Optional;
import com.jd.framework.job.api.ScheduleJob;
import com.jd.framework.job.config.JobTypeConfiguration;
import com.jd.framework.job.core.api.listener.ScheduleJobListener;
import com.jd.framework.job.core.config.FactJobConfiguration;
import com.jd.framework.job.core.internal.executor.JobExecutor;
import com.jd.framework.job.core.internal.executor.JobRegistry;
import com.jd.framework.job.core.internal.executor.RegCenterRegistry;
import com.jd.framework.job.core.internal.executor.quartz.JobScheduleController;
import com.jd.framework.job.core.internal.facade.FactJobFacade;
import com.jd.framework.job.event.JobEventBus;
import com.jd.framework.job.event.JobEventConfiguration;
import com.jd.framework.job.exception.JobConfigurationException;
import com.jd.framework.job.exception.JobSystemException;
import com.jd.framework.job.executor.facade.JobFacade;
import com.jd.framework.job.executor.factory.JobExecutorFactory;
import com.jd.framework.job.regcenter.api.CoordinatorRegistryCenter;

import lombok.Setter;

/**
 * 
 * 作业调度中心，总调度入口
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-6
 */
public class JobScheduler {

    public static final String JOB_DATA_MAP_KEY = "scheduleJob";

    private static final String JOB_FACADE_DATA_MAP_KEY = "jobFacade";

    private final String jobName;

    private final JobExecutor jobExecutor;

    private final JobFacade jobFacade;

    private final JobRegistry jobRegistry;

    private final CoordinatorRegistryCenter registryCenter;

    public JobScheduler(final CoordinatorRegistryCenter regCenter, final FactJobConfiguration factJobConfig,
                        final ScheduleJobListener... scheduleJobListeners) {
        this(regCenter, factJobConfig, new JobEventBus(), scheduleJobListeners);
    }

    public JobScheduler(final CoordinatorRegistryCenter regCenter, final FactJobConfiguration factJobConfig,
                        final JobEventConfiguration jobEventConfig, final ScheduleJobListener... scheduleJobListeners) {
        this(regCenter, factJobConfig, new JobEventBus(jobEventConfig), scheduleJobListeners);
    }

    private JobScheduler(final CoordinatorRegistryCenter regCenter, final FactJobConfiguration factJobConfig,
                         final JobEventBus jobEventBus, final ScheduleJobListener... scheduleJobListeners) {
        jobName = factJobConfig.getJobName();
        jobExecutor = new JobExecutor(regCenter, factJobConfig, scheduleJobListeners);
        jobFacade = new FactJobFacade(regCenter, jobName, Arrays.asList(scheduleJobListeners), jobEventBus);
        jobRegistry = JobRegistry.getInstance();
        registryCenter = regCenter;
    }

    /**
     * 初始化作业.
     */
    public void init() {

        jobExecutor.init();
        JobTypeConfiguration jobTypeConfig = jobExecutor.getSchedulerFacade().loadJobConfiguration().getTypeConfig();
        JobScheduleController jobScheduleController = new JobScheduleController(
            createScheduler(jobTypeConfig.getCoreConfig().isMisfire()), createJobDetail(jobTypeConfig.getJobClass()),
            jobExecutor.getSchedulerFacade(), jobName);
        jobScheduleController.scheduleJob(jobTypeConfig.getCoreConfig().getCron());
        jobRegistry.addJobScheduleController(jobName, jobScheduleController);
        // 注册中心保存
        RegCenterRegistry.getInstance().addJobRegistryCenter(jobName, registryCenter);
    }

    private JobDetail createJobDetail(final String jobClass) {

        JobDetail result = JobBuilder.newJob(FactJob.class).withIdentity(jobName).build();
        result.getJobDataMap().put(JOB_FACADE_DATA_MAP_KEY, jobFacade);
        Optional<ScheduleJob> factJobInstance = createScheduleJobInstance();
        if (factJobInstance.isPresent()) {
            result.getJobDataMap().put(JOB_DATA_MAP_KEY, factJobInstance.get());
        } else {
            try {
                result.getJobDataMap().put(JOB_DATA_MAP_KEY, Class.forName(jobClass).newInstance());
            } catch (final ReflectiveOperationException ex) {
                throw new JobConfigurationException("Fact-Job: Job class '%s' can not initialize.", jobClass);
            }
        }

        return result;
    }

    protected Optional<ScheduleJob> createScheduleJobInstance() {

        return Optional.absent();
    }

    private Scheduler createScheduler(final boolean isMisfire) {

        Scheduler result;
        try {
            StdSchedulerFactory factory = new StdSchedulerFactory();
            factory.initialize(getBaseQuartzProperties(isMisfire));
            result = factory.getScheduler();
            result.getListenerManager().addTriggerListener(jobExecutor.getSchedulerFacade().newJobTriggerListener());
        } catch (final SchedulerException ex) {
            throw new JobSystemException(ex);
        }
        return result;
    }

    private Properties getBaseQuartzProperties(final boolean isMisfire) {

        Properties result = new Properties();
        result.put("org.quartz.threadPool.class", org.quartz.simpl.SimpleThreadPool.class.getName());
        result.put("org.quartz.threadPool.threadCount", "1");
        result.put("org.quartz.scheduler.instanceName", jobName);
        if (!isMisfire) {
            result.put("org.quartz.jobStore.misfireThreshold", "1");
        }
        result.put("org.quartz.plugin.shutdownhook.class", ShutdownHookPlugin.class.getName());
        result.put("org.quartz.plugin.shutdownhook.cleanShutdown", Boolean.TRUE.toString());
        return result;
    }

    /**
     * 停止作业调度.
     */
    public void shutdown() {

        jobRegistry.getJobScheduleController(jobName).shutdown();
    }

    /**
     * Fact调度作业.
     * 
     */
    public static final class FactJob implements Job {

        @Setter
        private ScheduleJob scheduleJob;

        @Setter
        private JobFacade jobFacade;

        @Override
        public void execute(final JobExecutionContext context) throws JobExecutionException {

            JobExecutorFactory.getJobExecutor(scheduleJob, jobFacade).execute();
        }
    }
}
