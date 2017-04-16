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
 * 作业配置自定义异常
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-6
 */
public class JobConfigurationException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7684067700878829153L;

	public JobConfigurationException(final String errorMessage, final Object... args) {
		super(String.format(errorMessage, args));
	}

	public JobConfigurationException(final Throwable cause) {
		super(cause);
	}
}
