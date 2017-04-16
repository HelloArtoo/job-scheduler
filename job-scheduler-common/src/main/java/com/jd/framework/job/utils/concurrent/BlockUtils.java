/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.utils.concurrent;

/**
 * 
 * 阻塞工具类
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */
public final class BlockUtils {

	public static void waitingShortTime() {
		sleep(100L);
	}

	public static void waitingSecondsTime() {
		sleep(10000L);
	}

	public static void sleep(final long millis) {
		try {
			Thread.sleep(millis);
		} catch (final InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}
}
