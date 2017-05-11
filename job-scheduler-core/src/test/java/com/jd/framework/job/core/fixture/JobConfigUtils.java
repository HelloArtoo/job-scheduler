/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.core.fixture;

import org.unitils.util.ReflectionUtils;

import com.jd.framework.job.api.flow.FlowJob;
import com.jd.framework.job.config.core.JobCoreConfiguration;
import com.jd.framework.job.config.type.FlowJobConfiguration;
import com.jd.framework.job.config.type.SimpleJobConfiguration;
import com.jd.framework.job.core.config.FactJobConfiguration;

public class JobConfigUtils {

	public static void setFieldValue(final Object config, final String fieldName, final Object fieldValue) {
		try {
			ReflectionUtils.setFieldValue(config, config.getClass().getDeclaredField(fieldName), fieldValue);
		} catch (final NoSuchFieldException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static FactJobConfiguration createSimpleFactJobConfiguration() {
		return FactJobConfiguration.newBuilder(
				new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).build(),
						TestSimpleJob.class.getCanonicalName())).build();
	}

	public static FactJobConfiguration createSimpleFactJobConfiguration(final boolean overwrite) {
		return FactJobConfiguration
				.newBuilder(
						new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3)
								.build(), TestSimpleJob.class.getCanonicalName())).overwrite(overwrite).build();
	}

	public static FactJobConfiguration createFlowFactJobConfiguration() {
		return FactJobConfiguration.newBuilder(
				new FlowJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).build(),
						FlowJob.class.getCanonicalName(), false)).build();
	}
}
