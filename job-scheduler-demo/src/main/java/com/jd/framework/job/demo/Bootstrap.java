/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.demo;

import static com.jd.framework.job.demo.fixture.ConfigurationHelper.createProductSyncSimpleConfiguration;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

import com.jd.framework.job.core.api.JobScheduler;
import com.jd.framework.job.demo.simple.pbs.listener.SyncListener;
import com.jd.framework.job.demo.simple.pbs.listener.SyncOneOffListener;
import com.jd.framework.job.event.JobEventConfiguration;
import com.jd.framework.job.event.rdb.RdbJobEventConfiguration;
import com.jd.framework.job.regcenter.ZookeeperRegistryCenter;
import com.jd.framework.job.regcenter.api.CoordinatorRegistryCenter;
import com.jd.framework.job.regcenter.conf.ZookeeperConfiguration;

public class Bootstrap {

	//private static final int ZK_PORT = 2181;
	// private static final String ZK_CONNECTION = "localhost:" + ZK_PORT;
	private static final String ZK_SERVERLIST = "localhost:2181,localhost:2182,localhost:2183";
	private static final String ZK_NAMESPACE = "Job-Scheduler-Demos";
	private static final String RDB_DRIVER = "org.h2.Driver";
	private static final String RDB_URL = "jdbc:h2:mem:job_event_storage";
	private static final String RDB_USERNAME = "sa";
	private static final String RDB_PASSWORD = "";

	public static void main(String[] args) {

		CoordinatorRegistryCenter registry = createRegistryCenter();
		registry.init();

		JobEventConfiguration jobEventConfig = new RdbJobEventConfiguration(createEventDataSource());

		new JobScheduler(registry, createProductSyncSimpleConfiguration(), jobEventConfig, new SyncListener(),
				new SyncOneOffListener(1000L, 2000L)).init();
		// new JobScheduler(registry, createProduct2DcFlowConfiguration(),
		// jobEventConfig).init();
	}

	private static CoordinatorRegistryCenter createRegistryCenter() {
		return new ZookeeperRegistryCenter(new ZookeeperConfiguration(ZK_SERVERLIST, ZK_NAMESPACE));
	}

	private static DataSource createEventDataSource() {
		BasicDataSource result = new BasicDataSource();
		result.setDriverClassName(RDB_DRIVER);
		result.setUrl(RDB_URL);
		result.setUsername(RDB_USERNAME);
		result.setPassword(RDB_PASSWORD);
		return result;
	}

}
