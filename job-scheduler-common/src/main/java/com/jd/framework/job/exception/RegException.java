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

public class RegException extends RuntimeException {

	/**
	 * seial Id
	 */
	private static final long serialVersionUID = -1000723602544550632L;

	public RegException(final String errorMessage, final Object... args) {
		super(String.format(errorMessage, args));
	}

	public RegException(final Exception cause) {
		super(cause);
	}
}
