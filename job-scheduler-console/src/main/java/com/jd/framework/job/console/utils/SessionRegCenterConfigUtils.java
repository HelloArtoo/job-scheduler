///*   
// * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
// *   
// * This software is the confidential and proprietary information of   
// * Founder. You shall not disclose such Confidential Information   
// * and shall use it only in accordance with the terms of the agreements   
// * you entered into with Founder.   
// *   
// */
//package com.jd.framework.job.console.utils;
//
//import lombok.AccessLevel;
//import lombok.NoArgsConstructor;
//
//import com.jd.framework.job.console.domain.RegCenterConfiguration;
//
//@NoArgsConstructor(access = AccessLevel.PRIVATE)
//public final class SessionRegCenterConfigUtils {
//
//	private static ThreadLocal<RegCenterConfiguration> regCenterConfig = new ThreadLocal<>();
//
//	public static RegCenterConfiguration getRegCenterConfiguration() {
//		return regCenterConfig.get();
//	}
//
//	public static void setRegCenterConfiguration(final RegCenterConfiguration regConfig) {
//		regCenterConfig.set(regConfig);
//	}
//
//	public static void clear() {
//		regCenterConfig.remove();
//	}
//}
