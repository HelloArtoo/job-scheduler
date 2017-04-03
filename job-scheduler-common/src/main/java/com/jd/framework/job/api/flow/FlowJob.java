package com.jd.framework.job.api.flow;

import com.jd.framework.job.api.DistributedJob;
import com.jd.framework.job.api.SegmentContext;

import java.util.List;

/**
 * DESCRIPTION: 流式作业
 * AUTHOR: Artoo Hu
 * DATE: 17/3/31
 * VERSION: 1.0
 */
public interface FlowJob<T> extends DistributedJob{

    /**
     * 抓取所需任务数据
     * @param segmentContext
     * @return
     */
    List<T> fetchData(SegmentContext segmentContext);

    /**
     * 处理任务数据
     * @param segmentContext
     * @param data
     */
    void processData(SegmentContext segmentContext, List<T> data);

}
