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
 * 作业系统异常   
 * @author Rong Hu  
 * @version   
 *       1.0, 2017-4-3
 */
public class JobSystemException extends RuntimeException {

	/**
	 * serial Id
	 */
	private static final long serialVersionUID = -6289696491220505295L;

	public JobSystemException(final String errorMessage, final Object... args) {
		super(String.format(errorMessage, args));
	}

	public JobSystemException(final Throwable cause) {
		super(cause);
	}
}
