/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.regcenter.exception;

import lombok.extern.slf4j.Slf4j;

import org.apache.zookeeper.KeeperException.ConnectionLossException;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.KeeperException.NodeExistsException;

/**
 * 
 * 注册中心异常处理
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-3
 */
@Slf4j
public class RegExceptionHandler {
	/**
	 * 处理异常.
	 * 
	 * <p>
	 * 处理掉中断和连接失效异常并继续抛注册中心.
	 * </p>
	 * 
	 * @param cause
	 *            待处理异常.
	 */
	public static void handleException(final Exception cause) {
		if (isIgnoredException(cause) || isIgnoredException(cause.getCause())) {
			log.debug("Job-scheduler: ignored exception for: {}", cause.getMessage());
		} else if (cause instanceof InterruptedException) {
			Thread.currentThread().interrupt();
		} else {
			throw new RegException(cause);
		}
	}

	/**
	 * 一下异常忽略
	 * 
	 * @param cause
	 * @return boolean
	 * @author Rong Hu
	 */
	private static boolean isIgnoredException(final Throwable cause) {
		return null != cause
				&& (cause instanceof ConnectionLossException || cause instanceof NoNodeException || cause instanceof NodeExistsException);
	}
}
