package com.jd.framework.job.api.flow;

import com.jd.framework.job.api.ScheduleJob;
import com.jd.framework.job.api.SegmentContext;

import java.util.List;

/**
 * DESCRIPTION: 流式作业 AUTHOR: Artoo Hu DATE: 17/3/31 VERSION: 1.0
 */
public interface FlowJob<T> extends ScheduleJob {

	/**
	 * 抓取所需任务数据
	 * 
	 * @param segmentContext 分段上下文
	 * @return T
	 */
	List<T> fetchData(SegmentContext segmentContext);

	/**
	 * 
	 * @param segmentContext 分段上下文
	 * @param data 实际数据
	 */
	void processData(SegmentContext segmentContext, List<T> data);

}
