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

import java.util.concurrent.ConcurrentHashMap;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.jd.framework.job.regcenter.ZookeeperRegistryCenter;
import com.jd.framework.job.regcenter.api.CoordinatorRegistryCenter;
import com.jd.framework.job.regcenter.conf.ZookeeperConfiguration;

/**
 * 
 * 注册中心工厂类
 * 
 * @author Rong Hu
 * @version 1.0, 2017-7-1
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RegCenterFactory {

	private static final ConcurrentHashMap<HashCode, CoordinatorRegistryCenter> REG_CENTER_REGISTRY = new ConcurrentHashMap<>();

	/**
	 * 创建注册中心.
	 * 
	 * @param connectString
	 *            注册中心连接字符串
	 * @param namespace
	 *            注册中心命名空间
	 * @param digest
	 *            注册中心凭证
	 * @return 注册中心对象
	 */
	public static CoordinatorRegistryCenter createCoordinatorRegCenter(final String connectString,
			final String namespace, final Optional<String> digest) {
		Hasher hasher = Hashing.md5().newHasher().putString(connectString, Charsets.UTF_8)
				.putString(namespace, Charsets.UTF_8);
		if (digest.isPresent()) {
			hasher.putString(digest.get(), Charsets.UTF_8);
		}
		HashCode hashCode = hasher.hash();
		CoordinatorRegistryCenter result = REG_CENTER_REGISTRY.get(hashCode);
		if (null != result) {
			return result;
		}
		ZookeeperConfiguration zkConfig = new ZookeeperConfiguration(connectString, namespace);
		if (digest.isPresent()) {
			zkConfig.setDigest(digest.get());
		}
		result = new ZookeeperRegistryCenter(zkConfig);
		result.init();
		REG_CENTER_REGISTRY.put(hashCode, result);
		return result;
	}

}
