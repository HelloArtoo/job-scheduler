/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.config.core;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.jd.framework.job.constant.JobProperties;

/**
 * 
 * 作业核心配置
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-4
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class JobCoreConfiguration {

	private final String jobName;

	private final String cron;

	private final int segmentTotalCount;

	private final String segmentItemParameters;

	private final String jobParameter;

	private final boolean failover;

	private final boolean misfire;

	private final String description;

	private final JobProperties jobProperties;

	/**
	 * 创建简单作业配置构建器.
	 * 
	 * @param jobName
	 *            作业名称
	 * @param cron
	 *            作业启动时间的cron表达式
	 * @param shardingTotalCount
	 *            作业分片总数
	 * @return 简单作业配置构建器
	 */
	public static Builder newBuilder(final String jobName, final String cron,
			final int segmentTotalCount) {
		return new Builder(jobName, cron, segmentTotalCount);
	}

	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Builder {

		private final String jobName;

		private final String cron;

		private final int segmentTotalCount;

		private String segmentItemParameters = "";

		private String jobParameter = "";

		private boolean failover;

		private boolean misfire = true;

		private String description = "";

		private final JobProperties jobProperties = new JobProperties();

		/**
		 * 设置分片序列号和个性化参数对照表.
		 * 
		 * <p>
		 * 分片序列号和参数用等号分隔, 多个键值对用逗号分隔. 类似map. 分片序列号从0开始, 不可大于或等于作业分片总数. 如:
		 * 0=a,1=b,2=c
		 * </p>
		 * 
		 * @param shardingItemParameters
		 *            分片序列号和个性化参数对照表
		 * 
		 * @return 作业配置构建器
		 */
		public Builder segmentItemParameters(final String segmentItemParameters) {
			if (null != segmentItemParameters) {
				this.segmentItemParameters = segmentItemParameters;
			}
			return this;
		}

		/**
		 * 设置作业自定义参数.
		 * 
		 * <p>
		 * 可以配置多个相同的作业, 但是用不同的参数作为不同的调度实例.
		 * </p>
		 * 
		 * @param jobParameter
		 *            作业自定义参数
		 * 
		 * @return 作业配置构建器
		 */
		public Builder jobParameter(final String jobParameter) {
			if (null != jobParameter) {
				this.jobParameter = jobParameter;
			}
			return this;
		}

		/**
		 * 设置是否开启失效转移.
		 * 
		 * <p>
		 * 只有对monitorExecution的情况下才可以开启失效转移.
		 * </p>
		 * 
		 * @param failover
		 *            是否开启失效转移
		 * 
		 * @return 作业配置构建器
		 */
		public Builder failover(final boolean failover) {
			this.failover = failover;
			return this;
		}

		/**
		 * 设置是否开启misfire.
		 * 
		 * @param misfire
		 *            是否开启misfire
		 * 
		 * @return 作业配置构建器
		 */
		public Builder misfire(final boolean misfire) {
			this.misfire = misfire;
			return this;
		}

		/**
		 * 设置作业描述信息.
		 * 
		 * @param description
		 *            作业描述信息
		 * 
		 * @return 作业配置构建器
		 */
		public Builder description(final String description) {
			if (null != description) {
				this.description = description;
			}
			return this;
		}

		/**
		 * 设置作业属性.
		 * 
		 * @param key
		 *            属性键
		 * @param value
		 *            属性值
		 * 
		 * @return 作业配置构建器
		 */
		public Builder jobProperties(final String key, final String value) {
			jobProperties.put(key, value);
			return this;
		}

		/**
		 * 构建作业配置对象.
		 * 
		 * @return 作业配置对象
		 */
		public final JobCoreConfiguration build() {
			Preconditions.checkArgument(!Strings.isNullOrEmpty(jobName),
					"jobName can not be empty.");
			Preconditions.checkArgument(!Strings.isNullOrEmpty(cron),
					"cron can not be empty.");
			Preconditions.checkArgument(segmentTotalCount > 0,
					"segmentTotalCount should larger than zero.");
			return new JobCoreConfiguration(jobName, cron, segmentTotalCount,
					segmentItemParameters, jobParameter, failover, misfire,
					description, jobProperties);
		}
	}
}
