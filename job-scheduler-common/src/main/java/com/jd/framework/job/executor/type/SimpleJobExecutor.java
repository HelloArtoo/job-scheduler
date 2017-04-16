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

import com.jd.framework.job.api.SegmentContext;
import com.jd.framework.job.api.simple.SimpleJob;
import com.jd.framework.job.executor.AbstractJobExecutor;
import com.jd.framework.job.executor.facade.JobFacade;

/**
 * 
 * 简单作业执行器，分布式分段定时执行。
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-15
 */
public class SimpleJobExecutor extends AbstractJobExecutor {

	private final SimpleJob simpleJob;

	public SimpleJobExecutor(final SimpleJob simpleJob, final JobFacade jobFacade) {
		super(jobFacade);
		this.simpleJob = simpleJob;
	}

	@Override
	protected void process(SegmentContext segmentContext) {
		simpleJob.execute(segmentContext);
	}

}
