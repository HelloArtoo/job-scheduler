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

import com.jd.framework.job.console.repository.RegCenterRepository;
import com.jd.framework.job.regcenter.ZookeeperRegistryCenter;
import com.jd.framework.job.regcenter.api.CoordinatorRegistryCenter;
import com.jd.framework.job.regcenter.conf.ZookeeperConfiguration;

import lombok.extern.slf4j.Slf4j;

/**
 * 注册中心过滤，实际项目中添加注册中心的配置文件classpath:conf/reg.properties
 * 
 * <pre>
 * 	   reg.namespace=
 *     reg.servers=
 *     reg.digest=
 * </pre>
 * 
 * @author hurong
 *
 */
@Slf4j
public final class RegCenterFilter implements Filter {

	private String namespace;
	private String servers;
	private String digest;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		String configFilePath = WwwAuthFilter.PATH + filterConfig.getInitParameter("reg-config");
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(configFilePath));
		} catch (final IOException ex) {
			throw new ServletException("Cannot found registry config file [config/reg.properties].");
		}
		namespace = props.getProperty("reg.namespace");
		servers = props.getProperty("reg.servers");
		digest = props.getProperty("reg.digest");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpreq = (HttpServletRequest) request;
		if (RegCenterRepository.INSTANCE == null) {
			synchronized (RegCenterRepository.class) {
				try {
					ZookeeperConfiguration zkConfig = new ZookeeperConfiguration(servers, namespace);
					if (null != digest) {
						zkConfig.setDigest(digest);
					}
					CoordinatorRegistryCenter result = new ZookeeperRegistryCenter(zkConfig);
					result.init();
					RegCenterRepository.INSTANCE = result;
					httpreq.getSession().setAttribute("namespace", namespace);
					httpreq.getSession().setAttribute("servers", servers);
				} catch (Exception e) {
					String msg = "zookeeper connection timeout. please check the [config/reg.properties] or maybe you can set below";
					log.error(msg, e);
					request.setAttribute("msg", msg);
					request.getRequestDispatcher("/registry").forward(request, response);
				}
			}
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
	}

}
