/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.event.fixture;

import com.jd.framework.job.event.JobEventConfiguration;
import com.jd.framework.job.event.JobEventListener;
import com.jd.framework.job.exception.JobEventListenerConfigException;

public class TestJobEventFailureConfiguration extends TestJobEventIdentity implements JobEventConfiguration {

	@Override
	public JobEventListener createJobEventListener() throws JobEventListenerConfigException {
		throw new JobEventListenerConfigException(new RuntimeException("assert failure"));
	}

}
