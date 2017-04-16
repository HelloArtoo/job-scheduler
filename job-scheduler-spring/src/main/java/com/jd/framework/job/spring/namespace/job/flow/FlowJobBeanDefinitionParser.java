/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.spring.namespace.job.flow;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.w3c.dom.Element;

import com.jd.framework.job.config.type.FlowJobConfiguration;
import com.jd.framework.job.spring.namespace.job.AbstractJobBeanDefinitionParser;
import static com.jd.framework.job.spring.namespace.job.tag.JobBeanDefinitionParserTag.CLASS_ATTRIBUTE;

public final class FlowJobBeanDefinitionParser extends AbstractJobBeanDefinitionParser {

	/** 流式处理TAG */
	private static final String STREAMING_PROCESS_ATTRIBUTE = "streaming-process";

	@Override
	protected BeanDefinition getJobTypeConfigurationBeanDefinition(BeanDefinition jobCoreConfigurationBeanDefinition,
			Element element) {
		BeanDefinitionBuilder result = BeanDefinitionBuilder.rootBeanDefinition(FlowJobConfiguration.class);
		result.addConstructorArgValue(jobCoreConfigurationBeanDefinition);
		result.addConstructorArgValue(element.getAttribute(CLASS_ATTRIBUTE));
		result.addConstructorArgValue(element.getAttribute(STREAMING_PROCESS_ATTRIBUTE));
		return result.getBeanDefinition();
	}

}
