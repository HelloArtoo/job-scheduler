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

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;


/**
 * 注册中心命名空间
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-15
 */
public class RegNamespaceHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {
		registerBeanDefinitionParser("zookeeper", new ZookeeperBeanDefinitionParser());
	}

}
