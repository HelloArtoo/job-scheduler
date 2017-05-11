/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.spring.namespace.tests;

import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(locations = "classpath:spring/job/withEventTraceRdb.xml")
public class NamespaceWithEventTraceRdbTest extends AbstractSpringJobIntegrateTest{

	public NamespaceWithEventTraceRdbTest() {
		super("testSpringSimpleJob_namespace_event_trace_rdb", "testSpringFlowJob_namespace_event_trace_rdb");
	}

}
