package com.jd.framework.job.api.simple;

import com.jd.framework.job.api.DistributedJob;
import com.jd.framework.job.api.SegmentContext;

/**
 * DESCRIPTION: 简单作业
 * AUTHOR: Artoo Hu
 * DATE: 17/3/31
 * VERSION: 1.0
 */
public interface SimpleJob extends DistributedJob {

    /**
     * 执行作业
     * @param segmentContext 分段上下文
     */
    void execute(final SegmentContext segmentContext);
}
