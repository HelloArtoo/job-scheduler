/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.executor.facade;

import java.util.Collection;

import com.jd.framework.job.config.JobRootConfiguration;
import com.jd.framework.job.event.type.JobExecutionEvent;
import com.jd.framework.job.event.type.JobStatusTraceEvent;
import com.jd.framework.job.exception.JobExecutionEnvironmentException;
import com.jd.framework.job.executor.context.SegmentContexts;

/**
 * 
 * 作业内部门面类
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */
public interface JobFacade {

	/**
	 * 读取作业配置.
	 * 
	 * @param fromCache
	 *            是否从缓存中读取
	 * @return 作业配置
	 */
	JobRootConfiguration loadJobRootConfiguration(boolean fromCache);

	/**
	 * 检查作业执行环境.
	 * 
	 * @throws JobExecutionEnvironmentException
	 *             作业执行环境异常
	 */
	void checkJobExecutionEnvironment() throws JobExecutionEnvironmentException;

	/**
	 * 如果需要失效转移, 则设置作业失效转移.
	 */
	void failoverIfNecessary();

	/**
	 * 注册作业启动信息.
	 * 
	 * @param segmentContexts
	 *            分段上下文
	 */
	void registerJobBegin(SegmentContexts segmentContexts);

	/**
	 * 注册作业完成信息.
	 * 
	 * @param segmentContexts
	 *            分段上下文
	 */
	void registerJobCompleted(SegmentContexts segmentContexts);

	/**
	 * 获取当前作业服务器的分段上下文.
	 * 
	 * @return 分段上下文
	 */
	SegmentContexts getSegmentContexts();

	/**
	 * 设置任务被错过执行的标记.
	 * 
	 * @param segmentItems
	 *            需要设置错过执行的任务分段项
	 * @return 是否满足misfire条件
	 */
	boolean misfireIfNecessary(Collection<Integer> segmentItems);

	/**
	 * 清除任务被错过执行的标记.
	 * 
	 * @param segmentItems
	 *            需要清除错过执行的任务分段项
	 */
	void clearMisfire(Collection<Integer> segmentItems);

	/**
	 * 判断作业是否需要执行错过的任务.
	 * 
	 * @param segmentItems
	 *            任务分段项集合
	 * @return 作业是否需要执行错过的任务
	 */
	boolean isExecuteMisfired(Collection<Integer> segmentItems);

	/**
	 * 判断作业是否符合继续运行的条件.
	 * 
	 * <p>
	 * 如果作业停止或需要重分段或非流式处理则作业将不会继续运行.
	 * </p>
	 * 
	 * @return 作业是否符合继续运行的条件
	 */
	boolean isEligibleForJobRunning();

	/**
	 * 判断是否需要重分段.
	 * 
	 * @return 是否需要重分段
	 */
	boolean isNeedSegment();

	/**
	 * 清理作业上次运行时信息. 只会在主节点进行.
	 */
	void cleanPreviousExecutionInfo();

	/**
	 * 作业执行前的执行的方法.
	 * 
	 * @param segmentContexts
	 *            分段上下文
	 */
	void beforeJobExecuted(SegmentContexts segmentContexts);

	/**
	 * 作业执行后的执行的方法.
	 * 
	 * @param segmentContexts
	 *            分段上下文
	 */
	void afterJobExecuted(SegmentContexts segmentContexts);

	/**
	 * 发布执行事件.
	 * 
	 * @param jobExecutionEvent
	 *            作业执行事件
	 */
	void postJobExecutionEvent(JobExecutionEvent jobExecutionEvent);

	/**
	 * 发布作业状态追踪事件.
	 * 
	 * @param taskId
	 *            作业Id
	 * @param state
	 *            作业执行状态
	 * @param message
	 *            作业执行消息
	 */
	void postJobStatusTraceEvent(String taskId, JobStatusTraceEvent.State state, String message);
}
