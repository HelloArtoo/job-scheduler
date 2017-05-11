/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.spring.fixture.handler;

import com.jd.framework.job.executor.handler.exception.JobExceptionHandler;

public class TestSimpleJobExceptionHandler implements JobExceptionHandler {

	@Override
	public void handleException(String jobName, Throwable cause) {
		throw new RuntimeException(cause);
	}

}
