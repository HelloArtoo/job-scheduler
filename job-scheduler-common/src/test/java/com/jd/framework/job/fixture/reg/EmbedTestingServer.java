/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.fixture.reg;

import java.io.File;
import java.io.IOException;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.apache.curator.test.TestingServer;

import com.google.common.base.Joiner;
import com.jd.framework.job.regcenter.exception.RegExceptionHandler;

/**
 * <pre>
 * Curator TestingServer test
 * 模拟zookeeper client 
 * /target/test_zk_data
 * </pre>
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-29
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmbedTestingServer {

	private static final int PORT = 3181;

	private static volatile TestingServer testingServer;

	public static String getConnectionString() {
		return Joiner.on(":").join("localhost", PORT);
	}

	public static void start() {
		if (null != testingServer) {
			return;
		}
		try {
			testingServer = new TestingServer(PORT, new File(
					String.format("target/test_zk_data/%s/", System.nanoTime())));
			// CHECKSTYLE:OFF
		} catch (final Exception ex) {
			// CHECKSTYLE:ON
			RegExceptionHandler.handleException(ex);
		} finally {
			Runtime.getRuntime().addShutdownHook(new Thread() {

				@Override
				public void run() {
					try {
						testingServer.close();
					} catch (final IOException ex) {
						RegExceptionHandler.handleException(ex);
					}
				}
			});
		}
	}
}
