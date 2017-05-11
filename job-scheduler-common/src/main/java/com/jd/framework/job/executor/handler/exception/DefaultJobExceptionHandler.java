/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.executor.handler.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 使用 {@link #handleException(String, Throwable)} 异常处理
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-4
 */
@Slf4j
public final class DefaultJobExceptionHandler implements JobExceptionHandler {

	@Override
	public void handleException(String jobName, Throwable cause) {
		log.error(String.format(
				"Exception occured during processing the job '%s' ", jobName),
				cause);
	}
}
