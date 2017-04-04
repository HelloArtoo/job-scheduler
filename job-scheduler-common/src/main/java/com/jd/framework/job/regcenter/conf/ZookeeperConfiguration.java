/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.regcenter.conf;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * 基于Zookeeper的注册中心配置<br/>
 * 可实现基于别处的注册中心
 * 
 */
@Getter
@Setter
@RequiredArgsConstructor
public class ZookeeperConfiguration {

	/**
	 * 连接Zookeeper服务器的列表. 包括IP地址和端口号. 多个地址用逗号分隔. 如: host1:2181,host2:2181
	 */
	private final String serverLists;

	/**
	 * 命名空间.
	 */
	private final String namespace;

	/**
	 * 等待重试的间隔时间的初始值. 单位毫秒.
	 */
	private int baseSleepTimeMilliseconds = 1000;

	/**
	 * 等待重试的间隔时间的最大值. 单位毫秒.
	 */
	private int maxSleepTimeMilliseconds = 3000;

	/**
	 * 最大重试次数.
	 */
	private int maxRetries = 3;

	/**
	 * 会话超时时间. 单位毫秒.
	 */
	private int sessionTimeoutMilliseconds;

	/**
	 * 连接超时时间. 单位毫秒.
	 */
	private int connectionTimeoutMilliseconds;

	/**
	 * 连接Zookeeper的权限令牌. 缺省为不需要权限验证.
	 */
	private String digest;
}