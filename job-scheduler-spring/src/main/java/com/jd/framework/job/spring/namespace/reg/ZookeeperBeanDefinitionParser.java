/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.spring.namespace.reg;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import com.google.common.base.Strings;
import com.jd.framework.job.regcenter.ZookeeperRegistryCenter;
import com.jd.framework.job.regcenter.conf.ZookeeperConfiguration;

/**
 * 
 * Zookeeper 注册中心命名空间解析器
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-15
 */
public class ZookeeperBeanDefinitionParser extends AbstractBeanDefinitionParser {

	@Override
	protected AbstractBeanDefinition parseInternal(final Element element, final ParserContext parserContext) {
		BeanDefinitionBuilder result = BeanDefinitionBuilder.rootBeanDefinition(ZookeeperRegistryCenter.class);
		result.addConstructorArgValue(this.buildZookeeperConfigBeanDefinition(element));
		result.setInitMethodName("init");
		return result.getBeanDefinition();
	}

	/**
	 * build bean definition
	 * 
	 * @param element
	 * @return
	 * @author Rong Hu
	 */
	private AbstractBeanDefinition buildZookeeperConfigBeanDefinition(final Element element) {
		BeanDefinitionBuilder configBuilder = BeanDefinitionBuilder.rootBeanDefinition(ZookeeperConfiguration.class);
		configBuilder.addConstructorArgValue(element.getAttribute("server-lists"));
		configBuilder.addConstructorArgValue(element.getAttribute("namespace"));
		addProperty("base-sleep-time-milliseconds", "baseSleepTimeMilliseconds", element, configBuilder);
		addProperty("max-sleep-time-milliseconds", "maxSleepTimeMilliseconds", element, configBuilder);
		addProperty("max-retries", "maxRetries", element, configBuilder);
		addProperty("session-timeout-milliseconds", "sessionTimeoutMilliseconds", element, configBuilder);
		addProperty("connection-timeout-milliseconds", "connectionTimeoutMilliseconds", element, configBuilder);
		addProperty("digest", "digest", element, configBuilder);
		return configBuilder.getBeanDefinition();
	}

	/**
	 * add a bean property
	 * 
	 * @param attributeName
	 * @param propertyName
	 * @param element
	 * @param factory
	 * @author Rong Hu
	 */
	private void addProperty(final String attributeName, final String propertyName, final Element element,
			final BeanDefinitionBuilder factory) {
		String attributeValue = element.getAttribute(attributeName);
		if (!Strings.isNullOrEmpty(attributeValue)) {
			factory.addPropertyValue(propertyName, attributeValue);
		}
	}

}
