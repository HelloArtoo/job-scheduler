/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.exception;

/**
 * 
 * JOB 执行环境自定义异常
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-6
 */
public class JobExecutionEnvironmentException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1309770180793783449L;

	public JobExecutionEnvironmentException(final String errorMessage, final Object... args) {
		super(String.format(errorMessage, args));
	}
}
