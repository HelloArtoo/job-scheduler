/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.spring.namespace.base;

import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@TestExecutionListeners(EmbedZookeeperExecutionTestingListener.class)
public abstract class AbstractZookeeperJUnit4SpringContextTest extends AbstractJUnit4SpringContextTests {

}
