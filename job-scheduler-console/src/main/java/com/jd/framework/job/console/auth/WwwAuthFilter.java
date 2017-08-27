/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.console.auth;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.codec.binary.Base64;

/**
 * 
 * 鉴权
 * 
 * @author Rong Hu
 * @version 1.0, 2017-7-1
 */
@Slf4j
public class WwwAuthFilter implements Filter {

	static final String PATH = Thread.currentThread().getContextClassLoader().getResource("").getPath()
			+ System.getProperty("file.separator");

	private static final String AUTH_PREFIX = "Basic ";

	// default username
	private String username = "root";

	// default password
	private String password = "root";

	@Override
	public void init(final FilterConfig filterConfig) throws ServletException {
		String configFilePath = PATH + filterConfig.getInitParameter("auth-config");
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(configFilePath));
		} catch (final IOException ex) {
			log.warn("Cannot found auth config file, use default auth config.");
		}
		username = props.getProperty("console.username", username);
		password = props.getProperty("console.password", password);
	}

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		String authorization = httpRequest.getHeader("authorization");
		if (null != authorization && authorization.length() > AUTH_PREFIX.length()) {
			authorization = authorization.substring(AUTH_PREFIX.length(), authorization.length());
			if ((username + ":" + password).equals(new String(Base64.decodeBase64(authorization)))) {
				authenticateSuccess(httpResponse);
				chain.doFilter(httpRequest, httpResponse);
			} else {
				needAuthenticate(httpRequest, httpResponse);
			}
		} else {
			needAuthenticate(httpRequest, httpResponse);
		}
	}

	private void authenticateSuccess(final HttpServletResponse response) {
		response.setStatus(200);
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-store");
		response.setDateHeader("Expires", 0);
	}

	private void needAuthenticate(final HttpServletRequest request, final HttpServletResponse response) {
		response.setStatus(401);
		response.setHeader("Cache-Control", "no-store");
		response.setDateHeader("Expires", 0);
		response.setHeader("WWW-authenticate", AUTH_PREFIX + "Realm=\"Job Scheduler Console Auth\"");
	}

	@Override
	public void destroy() {
	}

}
