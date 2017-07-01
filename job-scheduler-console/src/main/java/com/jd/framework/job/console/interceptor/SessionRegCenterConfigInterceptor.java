/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.console.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.jd.framework.job.console.controller.RegCenterController;
import com.jd.framework.job.console.domain.RegCenterConfiguration;
import com.jd.framework.job.console.utils.SessionRegCenterConfigUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SessionRegCenterConfigInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler)
			throws Exception {
		SessionRegCenterConfigUtils.setRegCenterConfiguration((RegCenterConfiguration) request.getSession()
				.getAttribute(RegCenterController.REG_CENTER_CONFIG_KEY));
		return true;
	}

	@Override
	public void postHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler,
			final ModelAndView modelAndView) throws Exception {
		SessionRegCenterConfigUtils.clear();
	}
}
