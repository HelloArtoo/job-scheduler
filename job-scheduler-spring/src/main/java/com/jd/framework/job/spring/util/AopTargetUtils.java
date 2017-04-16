/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.spring.util;

import java.lang.reflect.Field;

import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.support.AopUtils;

import com.jd.framework.job.exception.JobSystemException;

/**
 * 
 * 基于Spring AOP获取目标对象.
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-15
 */
public class AopTargetUtils {

	/**
	 * 获取目标对象.
	 * 
	 * @param proxy
	 *            代理对象
	 * @return 目标对象
	 */
	public static Object getTarget(final Object proxy) {
		if (!AopUtils.isAopProxy(proxy)) {
			return proxy;
		}
		if (AopUtils.isJdkDynamicProxy(proxy)) {
			return getProxyTargetObject(proxy, "h");
		} else {
			return getProxyTargetObject(proxy, "CGLIB$CALLBACK_0");
		}
	}

	/**
	 * 基于代理类型
	 * 
	 * @param proxy
	 * @param proxyType
	 * @return
	 * @author Rong Hu
	 */
	private static Object getProxyTargetObject(final Object proxy, final String proxyType) {
		Field h;
		try {
			h = proxy.getClass().getSuperclass().getDeclaredField(proxyType);
		} catch (final NoSuchFieldException ex) {
			return getProxyTargetObjectForCglibAndSpring4(proxy);
		}
		h.setAccessible(true);
		try {
			return getTargetObject(h.get(proxy));
		} catch (final IllegalAccessException ex) {
			throw new JobSystemException(ex);
		}
	}

	/**
	 * cglib代理对象
	 * 
	 * @param proxy
	 * @return
	 * @author Rong Hu
	 */
	private static Object getProxyTargetObjectForCglibAndSpring4(final Object proxy) {
		Field h;
		try {
			h = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
			h.setAccessible(true);
			Object dynamicAdvisedInterceptor = h.get(proxy);
			Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");
			advised.setAccessible(true);
			return ((AdvisedSupport) advised.get(dynamicAdvisedInterceptor)).getTargetSource().getTarget();
		} catch (final Exception ex) {
			throw new JobSystemException(ex);
		}
	}

	/**
	 * 目标对象
	 * 
	 * @param object
	 * @return
	 * @author Rong Hu
	 */
	private static Object getTargetObject(final Object object) {
		try {
			Field advised = object.getClass().getDeclaredField("advised");
			advised.setAccessible(true);
			return ((AdvisedSupport) advised.get(object)).getTargetSource().getTarget();
		} catch (final Exception ex) {
			throw new JobSystemException(ex);
		}
	}
}
