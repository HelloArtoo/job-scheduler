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

@ContextConfiguration(locations = "classpath:spring/job/withoutListener.xml")
public class NamespaceWithoutListenerTest extends AbstractSpringJobIntegrateTest {

	public NamespaceWithoutListenerTest() {
		super("testSpringSimpleJob_namespace", "testSpringFlowJob_namespace");
	}

}
