/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.core.internal.service;

import com.jd.framework.job.regcenter.api.CoordinatorRegistryCenter;
import com.jd.framework.job.utils.env.SensitiveInfoUtils;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 监控服务
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */
@Slf4j
public class MonitorService {

	public static final String DUMP_COMMAND = "dump";

	private final String jobName;

	private final CoordinatorRegistryCenter regCenter;

	private final ConfigService configService;

	private ServerSocket serverSocket;

	private volatile boolean closed;

	public MonitorService(final CoordinatorRegistryCenter regCenter, final String jobName) {
		this.jobName = jobName;
		this.regCenter = regCenter;
		configService = new ConfigService(regCenter, jobName);
	}

	/**
	 * 初始化作业监听服务.
	 */
	public void listen() {
		int port = configService.load(true).getMonitorPort();
		if (port < 0) {
			return;
		}
		try {
			log.info("Job-scheduler: Monitor service is running, the port is '{}'", port);
			openSocketForMonitor(port);
		} catch (final IOException ex) {
			log.error("Job-scheduler: Monitor service listen failure, error is: ", ex);
		}
	}

	private void openSocketForMonitor(final int port) throws IOException {
		serverSocket = new ServerSocket(port);
		new Thread() {

			@Override
			public void run() {
				while (!closed) {
					try {
						process(serverSocket.accept());
					} catch (final IOException ex) {
						log.error("Job-scheduler: Monitor service open socket for monitor failure, error is: ", ex);
					}
				}
			}
		}.start();
	}

	private void process(final Socket socket) throws IOException {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				Socket autoCloseSocket = socket) {
			String cmdLine = reader.readLine();
			if (null != cmdLine && DUMP_COMMAND.equalsIgnoreCase(cmdLine)) {
				List<String> result = new ArrayList<>();
				dumpDirectly("/" + jobName, result);
				outputMessage(writer, Joiner.on("\n").join(SensitiveInfoUtils.filterSensitiveIps(result)) + "\n");
			}
		}
	}

	private void dumpDirectly(final String path, final List<String> result) {
		for (String each : regCenter.getChildrenKeys(path)) {
			String zkPath = path + "/" + each;
			String zkValue = regCenter.get(zkPath);
			if (null == zkValue) {
				zkValue = "";
			}
			TreeCache treeCache = (TreeCache) regCenter.getRawCache("/" + jobName);
			ChildData treeCacheData = treeCache.getCurrentData(zkPath);
			String treeCachePath = null == treeCacheData ? "" : treeCacheData.getPath();
			String treeCacheValue = null == treeCacheData ? "" : new String(treeCacheData.getData());
			if (zkValue.equals(treeCacheValue) && zkPath.equals(treeCachePath)) {
				result.add(Joiner.on(" | ").join(zkPath, zkValue));
			} else {
				result.add(Joiner.on(" | ").join(zkPath, zkValue, treeCachePath, treeCacheValue));
			}
			dumpDirectly(zkPath, result);
		}
	}

	private void outputMessage(final BufferedWriter outputWriter, final String msg) throws IOException {
		outputWriter.append(msg);
		outputWriter.flush();
	}

	/**
	 * 关闭作业监听服务.
	 */
	public void close() {
		closed = true;
		if (null != serverSocket && !serverSocket.isClosed()) {
			try {
				serverSocket.close();
			} catch (final IOException ex) {
				log.error("Job-scheduler: Monitor service close failure, error is: ", ex);
			}
		}
	}
}
