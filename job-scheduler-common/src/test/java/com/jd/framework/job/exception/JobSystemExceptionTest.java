/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.exception;

import org.junit.Test;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.instanceOf;

public class JobSystemExceptionTest {

	@Test
	public void assertGetMessage() {
		assertThat(new JobSystemException("message is: '%s'", "test").getMessage(), is("message is: 'test'"));
	}

	@Test
	public void assertGetCause() {
		assertThat(new JobSystemException(new RuntimeException()).getCause(), instanceOf(RuntimeException.class));
	}

}
