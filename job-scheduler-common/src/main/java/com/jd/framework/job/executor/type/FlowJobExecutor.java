/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.executor.type;

import java.util.List;

import com.jd.framework.job.api.SegmentContext;
import com.jd.framework.job.api.flow.FlowJob;
import com.jd.framework.job.config.type.FlowJobConfiguration;
import com.jd.framework.job.executor.AbstractJobExecutor;
import com.jd.framework.job.executor.facade.JobFacade;

/**
 * 
 * 流式作业处理器
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-15
 */
public class FlowJobExecutor extends AbstractJobExecutor {

	private final FlowJob<Object> flowJob;

	public FlowJobExecutor(final FlowJob<Object> flowJob, final JobFacade jobFacade) {
		super(jobFacade);
		this.flowJob = flowJob;
	}

	/**
	 * 流式作业处理
	 */
	@Override
	protected void process(SegmentContext segmentContext) {
		FlowJobConfiguration dataflowConfig = (FlowJobConfiguration) getJobRootConfig().getTypeConfig();
		// 流式处理，fetchData不为空则继续执行
		if (dataflowConfig.isStreamingProcess()) {
			streamingExecute(segmentContext);
		} else { // 一次行执行任务
			oneOffExecute(segmentContext);
		}
	}

	private void streamingExecute(final SegmentContext segmentContext) {
		List<Object> data = fetchData(segmentContext);
		while (null != data && !data.isEmpty()) {
			processData(segmentContext, data);
			if (!getJobFacade().isEligibleForJobRunning()) {
				break;
			}
			data = fetchData(segmentContext);
		}
	}

	private void oneOffExecute(final SegmentContext segmentContext) {
		List<Object> data = fetchData(segmentContext);
		if (null != data && !data.isEmpty()) {
			processData(segmentContext, data);
		}
	}

	private List<Object> fetchData(final SegmentContext segmentContext) {
		return flowJob.fetchData(segmentContext);
	}

	private void processData(final SegmentContext segmentContext, final List<Object> data) {
		flowJob.processData(segmentContext, data);
	}
}
