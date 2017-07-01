/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.console.factory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import com.google.common.base.Optional;
import com.jd.framework.job.console.service.api.JobOperateAPI;
import com.jd.framework.job.console.service.api.JobSettingsAPI;
import com.jd.framework.job.console.service.api.JobStatisticsAPI;
import com.jd.framework.job.console.service.api.ServerStatisticsAPI;

/**
 * 
 * 作业API工厂
 * 
 * @author Rong Hu
 * @version 1.0, 2017-7-1
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JobAPIFactory {

	/**
	 * 创建作业配置API对象.
	 * 
	 * @param connectString
	 *            注册中心连接字符串
	 * @param namespace
	 *            注册中心命名空间
	 * @param digest
	 *            注册中心凭证
	 * @return 操作作业API对象
	 */
	public static JobSettingsAPI createJobSettingsAPI(final String connectString, final String namespace,
			final Optional<String> digest) {
		return new JobSettingsAPI(RegCenterFactory.createCoordinatorRegCenter(connectString, namespace, digest));
	}

	/**
	 * 创建操作作业API对象.
	 * 
	 * @param connectString
	 *            注册中心连接字符串
	 * @param namespace
	 *            注册中心命名空间
	 * @param digest
	 *            注册中心凭证
	 * @return 操作作业API对象
	 */
	public static JobOperateAPI createJobOperateAPI(final String connectString, final String namespace,
			final Optional<String> digest) {
		return new JobOperateAPI(RegCenterFactory.createCoordinatorRegCenter(connectString, namespace, digest));
	}

	/**
	 * 创建作业状态展示API对象.
	 * 
	 * @param connectString
	 *            注册中心连接字符串
	 * @param namespace
	 *            注册中心命名空间
	 * @param digest
	 *            注册中心凭证
	 * @return 操作作业API对象
	 */
	public static JobStatisticsAPI createJobStatisticsAPI(final String connectString, final String namespace,
			final Optional<String> digest) {
		return new JobStatisticsAPI(RegCenterFactory.createCoordinatorRegCenter(connectString, namespace, digest));
	}

	/**
	 * 创建作业服务器状态展示API对象.
	 * 
	 * @param connectString
	 *            注册中心连接字符串
	 * @param namespace
	 *            注册中心命名空间
	 * @param digest
	 *            注册中心凭证
	 * @return 操作作业API对象
	 */
	public static ServerStatisticsAPI createServerStatisticsAPI(final String connectString, final String namespace,
			final Optional<String> digest) {
		return new ServerStatisticsAPI(RegCenterFactory.createCoordinatorRegCenter(connectString, namespace, digest));
	}
}
