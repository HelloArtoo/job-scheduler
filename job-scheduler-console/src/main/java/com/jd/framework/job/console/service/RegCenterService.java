///*   
// * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
// *   
// * This software is the confidential and proprietary information of   
// * Founder. You shall not disclose such Confidential Information   
// * and shall use it only in accordance with the terms of the agreements   
// * you entered into with Founder.   
// *   
// */
//package com.jd.framework.job.console.service;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import com.google.common.base.Optional;
//import com.jd.framework.job.regcenter.ZookeeperRegistryCenter;
//import com.jd.framework.job.regcenter.api.CoordinatorRegistryCenter;
//import com.jd.framework.job.regcenter.conf.ZookeeperConfiguration;
//
//@Service
//public class RegCenterService {
//
//	@Value("#{regcenterProperties['reg.namespace']}")
//	private String namespace;
//
//	@Value("#{regcenterProperties['reg.servers']}")
//	private String serverLists;
//
//	@Value("#{regcenterProperties['reg.digest']}")
//	private String digest;
//
//	/**
//	 * load from classpath:regcenter.properties first
//	 * 
//	 * @return
//	 */
//	public Optional<CoordinatorRegistryCenter> load() {
//
//		if (null == namespace || null == serverLists) {
//			return Optional.absent();
//		}
//
//		ZookeeperConfiguration zkConfig = new ZookeeperConfiguration(serverLists, namespace);
//		if (null != digest) {
//			zkConfig.setDigest(digest);
//		}
//		CoordinatorRegistryCenter result = new ZookeeperRegistryCenter(zkConfig);
//
//		return Optional.of(result);
//	}
//
//}
