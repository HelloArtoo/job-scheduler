/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.demo.fixture;

import com.jd.framework.job.config.core.JobCoreConfiguration;
import com.jd.framework.job.config.type.FlowJobConfiguration;
import com.jd.framework.job.config.type.SimpleJobConfiguration;
import com.jd.framework.job.core.config.FactJobConfiguration;
import com.jd.framework.job.demo.flow.pbs.Product2DcJob;
import com.jd.framework.job.demo.simple.pbs.ProductSyncJob;

public class ConfigurationHelper {

	public static FactJobConfiguration createProductSyncSimpleConfiguration() {
		JobCoreConfiguration coreConfig = JobCoreConfiguration
				.newBuilder("SimpleJob-Product-Sync-Datasource", "0/10 * * * * ?", 3)
				.segmentItemParameters("0=6,1=3,2=10").build();
		SimpleJobConfiguration simpleConfig = new SimpleJobConfiguration(coreConfig,
				ProductSyncJob.class.getCanonicalName());
		return FactJobConfiguration.newBuilder(simpleConfig).overwrite(true).build();
	}

	// 0=北京,1=上海,2=广州
	public static FactJobConfiguration createProduct2DcFlowConfiguration() {
		JobCoreConfiguration coreConfig = JobCoreConfiguration.newBuilder("FlowJob-Product-To-Dc", "0 0/10 * * * ?", 3)
				.segmentItemParameters("0=6,1=3,2=10").build();
		FlowJobConfiguration flowConfig = new FlowJobConfiguration(coreConfig, Product2DcJob.class.getCanonicalName(),
				true);
		return FactJobConfiguration.newBuilder(flowConfig).build();
	}

}
