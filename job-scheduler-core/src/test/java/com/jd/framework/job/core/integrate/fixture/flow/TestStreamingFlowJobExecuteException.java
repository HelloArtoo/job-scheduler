/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.core.integrate.fixture.flow;

import java.util.Collections;
import java.util.List;

import lombok.Getter;

import com.jd.framework.job.api.SegmentContext;
import com.jd.framework.job.api.flow.FlowJob;
import com.jd.framework.job.exception.JobSystemException;

public class TestStreamingFlowJobExecuteException implements FlowJob<String> {

	@Getter
    private static volatile boolean completed;
    
    @Override
    public List<String> fetchData(final SegmentContext segmentContext) {
        if (completed) {
            return null;
        }
        return Collections.singletonList("data");
    }
    
    @Override
    public void processData(final SegmentContext segmentContext, final List<String> data) {
        completed = true;
        throw new JobSystemException("I want an error.");
    }
    
    public static void reset() {
        completed = false;
    }
}
