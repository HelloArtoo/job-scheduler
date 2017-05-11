/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.spring.namespace.base;

import java.io.File;
import java.io.IOException;

import org.apache.curator.test.TestingServer;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import com.jd.framework.job.regcenter.exception.RegExceptionHandler;
import com.jd.framework.job.utils.concurrent.BlockUtils;

public class EmbedZookeeperExecutionTestingListener extends AbstractTestExecutionListener {

	private static volatile TestingServer testingServer;

	@Override
	public void beforeTestClass(final TestContext testContext) throws Exception {
		startEmbedTestingServer();
	}

	private static void startEmbedTestingServer() {
		if (null != testingServer) {
			return;
		}
		try {
			testingServer = new TestingServer(3181, new File(
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
						BlockUtils.sleep(2000L);
						testingServer.close();
					} catch (final IOException ex) {
						RegExceptionHandler.handleException(ex);
					}
				}
			});
		}
	}
}
