package com.jd.framework.job.api;

import com.jd.framework.job.executor.SegmentContexts;
import lombok.Getter;
import lombok.ToString;

/**
 * DESCRIPTION: 任务分环节，分段处理；此类作为每个环节内容的上下文。
 * AUTHOR: Artoo Hu
 * DATE: 17/3/31
 * VERSION: 1.0
 */
@Getter
@ToString
public final class SegmentContext {
    /**
     * 当前分段项
     */
    private final int segmentItem;
    /**
     * 总分段/环节数
     */
    private final int segmentsSum;
    /**
     * 作业名称
     */
    private final String jobName;
    /**
     * 作业任务id
     */
    private final String taskId;
    /**
     * 自定义参数映射关系
     */
    private final String jobParameter;
    /**
     * 分段参数
     */
    private final String segmentParameter;

    public SegmentContext(final SegmentContexts segmentContexts, final int segmentItem){
        this.jobName = segmentContexts.getJobName();
        this.taskId = segmentContexts.getTaskId();
        this.segmentsSum = segmentContexts.getSegmentsSum();
        this.jobParameter = segmentContexts.getJobParameter();
        this.segmentItem = segmentItem;
        this.segmentParameter = segmentContexts.getSegmentItemParameters().get(segmentItem);
    }

}
