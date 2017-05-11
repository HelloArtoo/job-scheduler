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

import java.util.Date;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;

import com.jd.framework.job.core.internal.facade.SchedulerFacade;
import com.jd.framework.job.exception.JobSystemException;

/**
 * 
 * 基于quartz的作业调度控制器
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */
@RequiredArgsConstructor
public class JobScheduleController {

	private final Scheduler scheduler;

	private final JobDetail jobDetail;

	private final SchedulerFacade schedulerFacade;

	private final String triggerIdentity;

	/**
	 * 调度作业.
	 * 
	 * @param cron
	 *            CRON表达式
	 */
	public void scheduleJob(final String cron) {
		try {
			if (!scheduler.checkExists(jobDetail.getKey())) {
				scheduler.scheduleJob(jobDetail, createTrigger(cron));
			}
			scheduler.start();
		} catch (final SchedulerException ex) {
			throw new JobSystemException(ex);
		}
	}

	/**
	 * 重新调度作业.
	 * 
	 * @param cron
	 *            CRON表达式
	 */
	public void rescheduleJob(final String cron) {
		try {
			CronTrigger trigger = (CronTrigger) scheduler.getTrigger(TriggerKey.triggerKey(triggerIdentity));
			if (!scheduler.isShutdown() && null != trigger && !cron.equals(trigger.getCronExpression())) {
				scheduler.rescheduleJob(TriggerKey.triggerKey(triggerIdentity), createTrigger(cron));
			}
		} catch (final SchedulerException ex) {
			throw new JobSystemException(ex);
		}
	}

	/**
	 * Trigger
	 * 
	 * @param cron
	 *            表达式
	 * @return CronTrigger
	 */
	private CronTrigger createTrigger(final String cron) {
		CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
		if (schedulerFacade.loadJobConfiguration().getTypeConfig().getCoreConfig().isMisfire()) {
			cronScheduleBuilder = cronScheduleBuilder.withMisfireHandlingInstructionFireAndProceed();
		} else {
			cronScheduleBuilder = cronScheduleBuilder.withMisfireHandlingInstructionDoNothing();
		}
		return TriggerBuilder.newTrigger().withIdentity(triggerIdentity).withSchedule(cronScheduleBuilder).build();
	}

	/**
	 * 获取下次作业触发时间.
	 * 
	 * @return Date 下次作业触发时间
	 */
	public Date getNextFireTime() {
		List<? extends Trigger> triggers;
		try {
			triggers = scheduler.getTriggersOfJob(jobDetail.getKey());
		} catch (final SchedulerException ex) {
			return null;
		}
		Date result = null;
		for (Trigger each : triggers) {
			Date nextFireTime = each.getNextFireTime();
			if (null == nextFireTime) {
				continue;
			}
			if (null == result) {
				result = nextFireTime;
			} else if (nextFireTime.getTime() < result.getTime()) {
				result = nextFireTime;
			}
		}
		return result;
	}

	/**
	 * 暂停作业.
	 */
	public void pauseJob() {
		try {
			if (!scheduler.isShutdown()) {
				scheduler.pauseAll();
			}
		} catch (final SchedulerException ex) {
			throw new JobSystemException(ex);
		}
	}

	/**
	 * 恢复作业.
	 */
	public void resumeJob() {
		try {
			if (!scheduler.isShutdown()) {
				scheduler.resumeAll();
			}
		} catch (final SchedulerException ex) {
			throw new JobSystemException(ex);
		}
	}

	/**
	 * 立刻启动作业.
	 */
	public void triggerJob() {
		try {
			if (!scheduler.isShutdown()) {
				scheduler.triggerJob(jobDetail.getKey());
			}
		} catch (final SchedulerException ex) {
			throw new JobSystemException(ex);
		}
	}

	/**
	 * 关闭调度器.
	 */
	public void shutdown() {
		schedulerFacade.releaseJobResource();
		try {
			if (!scheduler.isShutdown()) {
				scheduler.shutdown();
			}
		} catch (final SchedulerException ex) {
			throw new JobSystemException(ex);
		}
	}
}
