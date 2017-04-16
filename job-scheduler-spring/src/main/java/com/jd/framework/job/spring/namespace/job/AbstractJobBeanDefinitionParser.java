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

import static com.jd.framework.job.spring.namespace.job.tag.JobBeanDefinitionParserTag.CLASS_ATTRIBUTE;
import static com.jd.framework.job.spring.namespace.job.tag.JobBeanDefinitionParserTag.REGISTRY_CENTER_REF_ATTRIBUTE;
import static com.jd.framework.job.spring.namespace.job.tag.JobBeanDefinitionParserTag.MONITOR_EXECUTION_ATTRIBUTE;
import static com.jd.framework.job.spring.namespace.job.tag.JobBeanDefinitionParserTag.MAX_TIME_DIFF_SECONDS_ATTRIBUTE;
import static com.jd.framework.job.spring.namespace.job.tag.JobBeanDefinitionParserTag.MONITOR_PORT_ATTRIBUTE;
import static com.jd.framework.job.spring.namespace.job.tag.JobBeanDefinitionParserTag.JOB_SEGMENT_STRATEGY_CLASS_ATTRIBUTE;
import static com.jd.framework.job.spring.namespace.job.tag.JobBeanDefinitionParserTag.DISABLED_ATTRIBUTE;
import static com.jd.framework.job.spring.namespace.job.tag.JobBeanDefinitionParserTag.OVERWRITE_ATTRIBUTE;
import static com.jd.framework.job.spring.namespace.job.tag.JobBeanDefinitionParserTag.RECONCILE_INTERVAL_MINUTES;
import static com.jd.framework.job.spring.namespace.job.tag.JobBeanDefinitionParserTag.CRON_ATTRIBUTE;
import static com.jd.framework.job.spring.namespace.job.tag.JobBeanDefinitionParserTag.SEGMENT_TOTAL_COUNT_ATTRIBUTE;
import static com.jd.framework.job.spring.namespace.job.tag.JobBeanDefinitionParserTag.SEGMENT_ITEM_PARAMETERS_ATTRIBUTE;
import static com.jd.framework.job.spring.namespace.job.tag.JobBeanDefinitionParserTag.JOB_PARAMETER_ATTRIBUTE;
import static com.jd.framework.job.spring.namespace.job.tag.JobBeanDefinitionParserTag.FAILOVER_ATTRIBUTE;
import static com.jd.framework.job.spring.namespace.job.tag.JobBeanDefinitionParserTag.MISFIRE_ATTRIBUTE;
import static com.jd.framework.job.spring.namespace.job.tag.JobBeanDefinitionParserTag.DESCRIPTION_ATTRIBUTE;
import static com.jd.framework.job.spring.namespace.job.tag.JobBeanDefinitionParserTag.EXECUTOR_SERVICE_HANDLER_ATTRIBUTE;
import static com.jd.framework.job.spring.namespace.job.tag.JobBeanDefinitionParserTag.JOB_EXCEPTION_HANDLER_ATTRIBUTE;
import static com.jd.framework.job.spring.namespace.job.tag.JobBeanDefinitionParserTag.EVENT_TRACE_RDB_DATA_SOURCE_ATTRIBUTE;
import static com.jd.framework.job.spring.namespace.job.tag.JobBeanDefinitionParserTag.LISTENER_TAG;
import static com.jd.framework.job.spring.namespace.job.tag.JobBeanDefinitionParserTag.DISTRIBUTED_LISTENER_TAG;
import static com.jd.framework.job.spring.namespace.job.tag.JobBeanDefinitionParserTag.DISTRIBUTED_LISTENER_STARTED_TIMEOUT_MILLISECONDS_ATTRIBUTE;
import static com.jd.framework.job.spring.namespace.job.tag.JobBeanDefinitionParserTag.DISTRIBUTED_LISTENER_COMPLETED_TIMEOUT_MILLISECONDS_ATTRIBUTE;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import com.google.common.base.Strings;
import com.jd.framework.job.config.core.JobCoreConfiguration;
import com.jd.framework.job.core.config.FactJobConfiguration;
import com.jd.framework.job.event.rdb.RdbJobEventConfiguration;
import com.jd.framework.job.executor.handler.JobProperties;
import com.jd.framework.job.executor.handler.JobProperties.JobPropertiesEnum;
import com.jd.framework.job.spring.api.SpringJobScheduler;

/**
 * 
 * Job Bean命名空间解析基类
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-15
 */
public abstract class AbstractJobBeanDefinitionParser extends AbstractBeanDefinitionParser {

	@Override
	protected AbstractBeanDefinition parseInternal(final Element element, final ParserContext parserContext) {
		BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(SpringJobScheduler.class);
		factory.setInitMethodName("init");
		factory.setDestroyMethodName("shutdown");
		if ("".equals(element.getAttribute(CLASS_ATTRIBUTE))) {
			factory.addConstructorArgValue(null);
		} else {
			factory.addConstructorArgValue(BeanDefinitionBuilder.rootBeanDefinition(
					element.getAttribute(CLASS_ATTRIBUTE)).getBeanDefinition());
		}
		factory.addConstructorArgReference(element.getAttribute(REGISTRY_CENTER_REF_ATTRIBUTE));
		factory.addConstructorArgValue(createFactJobConfiguration(element));
		BeanDefinition jobEventConfig = createJobEventConfig(element);
		if (null != jobEventConfig) {
			factory.addConstructorArgValue(jobEventConfig);
		}
		factory.addConstructorArgValue(createJobListeners(element));
		return factory.getBeanDefinition();
	}

	protected abstract BeanDefinition getJobTypeConfigurationBeanDefinition(
			final BeanDefinition jobCoreConfigurationBeanDefinition, final Element element);

	private BeanDefinition createFactJobConfiguration(final Element element) {
		return createFactJobConfigurationBeanDefinition(element, createJobCoreBeanDefinition(element));
	}

	private BeanDefinition createFactJobConfigurationBeanDefinition(final Element element,
			final BeanDefinition jobCoreBeanDefinition) {
		BeanDefinitionBuilder result = BeanDefinitionBuilder.rootBeanDefinition(FactJobConfiguration.class);
		result.addConstructorArgValue(getJobTypeConfigurationBeanDefinition(jobCoreBeanDefinition, element));
		result.addConstructorArgValue(element.getAttribute(MONITOR_EXECUTION_ATTRIBUTE));
		result.addConstructorArgValue(element.getAttribute(MAX_TIME_DIFF_SECONDS_ATTRIBUTE));
		result.addConstructorArgValue(element.getAttribute(MONITOR_PORT_ATTRIBUTE));
		result.addConstructorArgValue(element.getAttribute(JOB_SEGMENT_STRATEGY_CLASS_ATTRIBUTE));
		result.addConstructorArgValue(element.getAttribute(DISABLED_ATTRIBUTE));
		result.addConstructorArgValue(element.getAttribute(OVERWRITE_ATTRIBUTE));
		result.addConstructorArgValue(element.getAttribute(RECONCILE_INTERVAL_MINUTES));
		return result.getBeanDefinition();
	}

	private BeanDefinition createJobCoreBeanDefinition(final Element element) {
		BeanDefinitionBuilder jobCoreBeanDefinitionBuilder = BeanDefinitionBuilder
				.rootBeanDefinition(JobCoreConfiguration.class);
		jobCoreBeanDefinitionBuilder.addConstructorArgValue(element.getAttribute(ID_ATTRIBUTE));
		jobCoreBeanDefinitionBuilder.addConstructorArgValue(element.getAttribute(CRON_ATTRIBUTE));
		jobCoreBeanDefinitionBuilder.addConstructorArgValue(element.getAttribute(SEGMENT_TOTAL_COUNT_ATTRIBUTE));
		jobCoreBeanDefinitionBuilder.addConstructorArgValue(element.getAttribute(SEGMENT_ITEM_PARAMETERS_ATTRIBUTE));
		jobCoreBeanDefinitionBuilder.addConstructorArgValue(element.getAttribute(JOB_PARAMETER_ATTRIBUTE));
		jobCoreBeanDefinitionBuilder.addConstructorArgValue(element.getAttribute(FAILOVER_ATTRIBUTE));
		jobCoreBeanDefinitionBuilder.addConstructorArgValue(element.getAttribute(MISFIRE_ATTRIBUTE));
		jobCoreBeanDefinitionBuilder.addConstructorArgValue(element.getAttribute(DESCRIPTION_ATTRIBUTE));
		jobCoreBeanDefinitionBuilder.addConstructorArgValue(createJobPropertiesBeanDefinition(element));
		return jobCoreBeanDefinitionBuilder.getBeanDefinition();
	}

	private BeanDefinition createJobPropertiesBeanDefinition(final Element element) {
		BeanDefinitionBuilder result = BeanDefinitionBuilder.rootBeanDefinition(JobProperties.class);
		Map<JobPropertiesEnum, String> map = new LinkedHashMap<>(JobPropertiesEnum.values().length, 1);
		map.put(JobPropertiesEnum.EXECUTOR_SERVICE_HANDLER, element.getAttribute(EXECUTOR_SERVICE_HANDLER_ATTRIBUTE));
		map.put(JobPropertiesEnum.JOB_EXCEPTION_HANDLER, element.getAttribute(JOB_EXCEPTION_HANDLER_ATTRIBUTE));
		result.addConstructorArgValue(map);
		return result.getBeanDefinition();
	}

	private BeanDefinition createJobEventConfig(final Element element) {
		String eventTraceDataSourceName = element.getAttribute(EVENT_TRACE_RDB_DATA_SOURCE_ATTRIBUTE);
		if (Strings.isNullOrEmpty(eventTraceDataSourceName)) {
			return null;
		}
		BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(RdbJobEventConfiguration.class);
		factory.addConstructorArgReference(eventTraceDataSourceName);
		return factory.getBeanDefinition();
	}

	private List<BeanDefinition> createJobListeners(final Element element) {
		Element listenerElement = DomUtils.getChildElementByTagName(element, LISTENER_TAG);
		Element distributedListenerElement = DomUtils.getChildElementByTagName(element, DISTRIBUTED_LISTENER_TAG);
		List<BeanDefinition> result = new ManagedList<>(2);
		if (null != listenerElement) {
			BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(listenerElement
					.getAttribute(CLASS_ATTRIBUTE));
			factory.setScope(BeanDefinition.SCOPE_PROTOTYPE);
			result.add(factory.getBeanDefinition());
		}
		if (null != distributedListenerElement) {
			BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(distributedListenerElement
					.getAttribute(CLASS_ATTRIBUTE));
			factory.setScope(BeanDefinition.SCOPE_PROTOTYPE);
			factory.addConstructorArgValue(distributedListenerElement
					.getAttribute(DISTRIBUTED_LISTENER_STARTED_TIMEOUT_MILLISECONDS_ATTRIBUTE));
			factory.addConstructorArgValue(distributedListenerElement
					.getAttribute(DISTRIBUTED_LISTENER_COMPLETED_TIMEOUT_MILLISECONDS_ATTRIBUTE));
			result.add(factory.getBeanDefinition());
		}
		return result;
	}

	@Override
	protected boolean shouldGenerateId() {
		return true;
	}
}
