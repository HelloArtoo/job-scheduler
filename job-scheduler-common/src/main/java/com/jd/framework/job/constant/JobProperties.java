/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.constant;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import com.jd.framework.job.executor.handler.exception.DefaultJobExceptionHandler;
import com.jd.framework.job.executor.handler.exception.JobExceptionHandler;
import com.jd.framework.job.executor.handler.threadpool.DefaultExecutorServiceHandler;
import com.jd.framework.job.executor.handler.threadpool.ExecutorServiceHandler;
import com.jd.framework.job.utils.json.GsonFactory;

/**
 * 
 * Job Properties 作业配置
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-4
 */
@AllArgsConstructor
@NoArgsConstructor
public class JobProperties {
	private Map<JobPropertiesEnum, String> map = new LinkedHashMap<>(
			JobPropertiesEnum.values().length, 1);

	/**
	 * 设置作业属性.
	 * 
	 * @param key
	 *            属性键
	 * @param value
	 *            属性值
	 */
	public void put(final String key, final String value) {
		JobPropertiesEnum jobPropertiesEnum = JobPropertiesEnum.from(key);
		if (null == jobPropertiesEnum || null == value) {
			return;
		}
		map.put(jobPropertiesEnum, value);
	}

	/**
	 * 获取作业属性.
	 * 
	 * @param jobPropertiesEnum
	 *            作业属性枚举
	 * @return 属性值
	 */
	public String get(final JobPropertiesEnum jobPropertiesEnum) {
		return map.containsKey(jobPropertiesEnum) ? map.get(jobPropertiesEnum)
				: jobPropertiesEnum.getDefaultValue();
	}

	/**
	 * 获取所有键.
	 * 
	 * @return 键集合
	 */
	public String json() {
		Map<String, String> jsonMap = new LinkedHashMap<>(
				JobPropertiesEnum.values().length, 1);
		for (JobPropertiesEnum each : JobPropertiesEnum.values()) {
			jsonMap.put(each.getKey(), get(each));
		}
		return GsonFactory.getGson().toJson(jsonMap);
	}

	/**
	 * 
	 * 作业属性枚举
	 * 
	 * @author Rong Hu
	 * @version 1.0, 2017-4-4
	 */
	@RequiredArgsConstructor
	@Getter
	public enum JobPropertiesEnum {
		/**
		 * 作业异常处理器.
		 */
		JOB_EXCEPTION_HANDLER("job_exception_handler",
				JobExceptionHandler.class, DefaultJobExceptionHandler.class
						.getCanonicalName()),

		/**
		 * 线程池服务处理器.
		 */
		EXECUTOR_SERVICE_HANDLER("executor_service_handler",
				ExecutorServiceHandler.class,
				DefaultExecutorServiceHandler.class.getCanonicalName());

		private final String key;

		private final Class<?> classType;

		private final String defaultValue;

		/**
		 * 通过属性键获取枚举.
		 * 
		 * @param key
		 *            属性键
		 * @return 枚举
		 */
		public static JobPropertiesEnum from(final String key) {
			for (JobPropertiesEnum each : JobPropertiesEnum.values()) {
				if (each.getKey().equals(key)) {
					return each;
				}
			}
			return null;
		}
	}
}
