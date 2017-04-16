/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.spring.namespace.job;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

import com.jd.framework.job.spring.namespace.job.flow.FlowJobBeanDefinitionParser;
import com.jd.framework.job.spring.namespace.job.simple.SimpleJobBeanDefinitionParser;

/**
 * 
 * 分布式调度作业命名空间解析
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-15
 */
public final class JobNamespaceHandler extends NamespaceHandlerSupport {

	/**
	 * Job类型Bean解析
	 */
	@Override
	public void init() {
		registerBeanDefinitionParser("simple", new SimpleJobBeanDefinitionParser());
		registerBeanDefinitionParser("flow", new FlowJobBeanDefinitionParser());
	}

}
