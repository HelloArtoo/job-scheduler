package com.jd.framework.job.executor.context;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Map;

/**
 * 
 * 分段上下文集合
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-15
 */
@RequiredArgsConstructor
@Getter
@ToString
public final class SegmentContexts implements Serializable {

	private static final long serialVersionUID = -3053965209841154003L;

	/**
	 * 作业任务id
	 */
	private final String taskId;
	/**
	 * 作业名称
	 */
	private final String jobName;
	/**
	 * 总分段/环节数
	 */
	private final int segmentsSum;

	/**
	 * 自定义参数映射关系
	 */
	private final String jobParameter;
	/**
	 * 分段id和自定义参数Map
	 */
	private final Map<Integer, String> segmentItemParameters;

	/**
	 * 作业采样数量
	 */
	private int jobEventSampleCount;

	/**
	 * 当前事件采样统计数量
	 */
	@Setter
	private int currentJobEventSamoleCount;

	/**
	 * 允许发送作业
	 */
	@Setter
	private boolean allowSendJobEvent = true;

	public SegmentContexts(final String taskId, final String jobName, final int segmentsSum, final String jobParameter,
			final Map<Integer, String> segmentItemParameters, final int jobEventSampleCount) {
		this.taskId = taskId;
		this.jobName = jobName;
		this.segmentsSum = segmentsSum;
		this.jobParameter = jobParameter;
		this.segmentItemParameters = segmentItemParameters;
		this.jobEventSampleCount = jobEventSampleCount;
	}
}
