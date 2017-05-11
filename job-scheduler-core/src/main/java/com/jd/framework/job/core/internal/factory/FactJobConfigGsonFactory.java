/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.core.internal.factory;

import java.io.IOException;
import java.util.Map;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.jd.framework.job.config.JobTypeConfiguration;
import com.jd.framework.job.core.config.FactJobConfiguration;
import com.jd.framework.job.utils.json.AbstractJobConfigGsonTypeAdapter;
import com.jd.framework.job.utils.json.GsonFactory;

/**
 * 
 * 作业配置的Gson工厂类
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-6
 */
public class FactJobConfigGsonFactory {
	static {
		GsonFactory.registerTypeAdapter(FactJobConfiguration.class, new FactJobConfigGsonTypeAdapter());
	}

	/**
	 * 将作业配置转换为JSON字符串.
	 * 
	 * @param factJobConfig
	 *            作业配置对象
	 * @return 作业配置JSON字符串
	 */
	public static String toJson(final FactJobConfiguration factJobConfig) {
		return GsonFactory.getGson().toJson(factJobConfig);
	}

	/**
	 * 将作业配置转换为JSON字符串.
	 * 
	 * @param factJobConfig
	 *            作业配置对象
	 * @return 作业配置JSON字符串
	 */
	public static String toJsonForObject(final Object factJobConfig) {
		return GsonFactory.getGson().toJson(factJobConfig);
	}

	/**
	 * 将JSON字符串转换为作业配置.
	 * 
	 * @param factJobConfigJson
	 *            作业配置JSON字符串
	 * @return 作业配置对象
	 */
	public static FactJobConfiguration fromJson(final String factJobConfigJson) {
		return GsonFactory.getGson().fromJson(factJobConfigJson, FactJobConfiguration.class);
	}

	/**
	 * fact作业配置的Json转换适配器.
	 * 
	 */
	static final class FactJobConfigGsonTypeAdapter extends AbstractJobConfigGsonTypeAdapter<FactJobConfiguration> {

		@Override
		protected void addToCustomizedValueMap(final String jsonName, final JsonReader in,
				final Map<String, Object> customizedValueMap) throws IOException {
			switch (jsonName) {
			case "monitorExecution":
				customizedValueMap.put("monitorExecution", in.nextBoolean());
				break;
			case "maxTimeDiffSeconds":
				customizedValueMap.put("maxTimeDiffSeconds", in.nextInt());
				break;
			case "monitorPort":
				customizedValueMap.put("monitorPort", in.nextInt());
				break;
			case "jobSegmentStrategyClass":
				customizedValueMap.put("jobSegmentStrategyClass", in.nextString());
				break;
			case "disabled":
				customizedValueMap.put("disabled", in.nextBoolean());
				break;
			case "overwrite":
				customizedValueMap.put("overwrite", in.nextBoolean());
				break;
			case "reconcileIntervalMinutes":
				customizedValueMap.put("reconcileIntervalMinutes", in.nextInt());
				break;
			default:
				in.skipValue();
				break;
			}
		}

		@Override
		protected FactJobConfiguration getJobRootConfiguration(final JobTypeConfiguration typeConfig,
				final Map<String, Object> customizedValueMap) {
			FactJobConfiguration.Builder builder = FactJobConfiguration.newBuilder(typeConfig);
			if (customizedValueMap.containsKey("monitorExecution")) {
				builder.monitorExecution((boolean) customizedValueMap.get("monitorExecution"));
			}
			if (customizedValueMap.containsKey("maxTimeDiffSeconds")) {
				builder.maxTimeDiffSeconds((int) customizedValueMap.get("maxTimeDiffSeconds"));
			}
			if (customizedValueMap.containsKey("monitorPort")) {
				builder.monitorPort((int) customizedValueMap.get("monitorPort"));
			}
			if (customizedValueMap.containsKey("jobSegmentStrategyClass")) {
				builder.jobSegmentStrategyClass((String) customizedValueMap.get("jobSegmentStrategyClass"));
			}
			if (customizedValueMap.containsKey("disabled")) {
				builder.disabled((boolean) customizedValueMap.get("disabled"));
			}
			if (customizedValueMap.containsKey("overwrite")) {
				builder.overwrite((boolean) customizedValueMap.get("overwrite"));
			}
			if (customizedValueMap.containsKey("reconcileIntervalMinutes")) {
				builder.reconcileIntervalMinutes((int) customizedValueMap.get("reconcileIntervalMinutes"));
			}
			return builder.build();
		}

		@Override
		protected void writeCustomized(final JsonWriter out, final FactJobConfiguration value) throws IOException {
			out.name("monitorExecution").value(value.isMonitorExecution());
			out.name("maxTimeDiffSeconds").value(value.getMaxTimeDiffSeconds());
			out.name("monitorPort").value(value.getMonitorPort());
			out.name("jobSegmentStrategyClass").value(value.getJobSegmentStrategyClass());
			out.name("disabled").value(value.isDisabled());
			out.name("overwrite").value(value.isOverwrite());
			out.name("reconcileIntervalMinutes").value(value.getReconcileIntervalMinutes());
		}
	}
}
