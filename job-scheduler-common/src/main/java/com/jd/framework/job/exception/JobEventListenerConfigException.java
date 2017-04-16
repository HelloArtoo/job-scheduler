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
 * 监听事件配置异常
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */
public class JobEventListenerConfigException extends Exception {

	private static final long serialVersionUID = 5816631485791623371L;

	public JobEventListenerConfigException(final Exception ex) {
		super(ex);
	}
}
