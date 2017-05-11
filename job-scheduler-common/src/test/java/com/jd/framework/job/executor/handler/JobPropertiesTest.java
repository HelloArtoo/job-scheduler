/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.executor.handler;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;
import org.unitils.util.ReflectionUtils;

import com.jd.framework.job.executor.handler.exception.DefaultJobExceptionHandler;
import com.jd.framework.job.executor.handler.threadpool.DefaultExecutorServiceHandler;
import com.jd.framework.job.fixture.JsonConstants;
import com.jd.framework.job.fixture.handler.IgnoreExceptionHandler;

public class JobPropertiesTest {

	@Test
	public void assertInvalidkey() throws NoSuchFieldException {
		JobProperties actual = new JobProperties();
		actual.put("invalid_key", "");
		actual.put("invalid_key", null);
		actual.put(null, "invalid_value");
		assertTrue(this.getMap(actual).isEmpty());
	}

	@Test
	public void assertPutSuccess() throws NoSuchFieldException {
		JobProperties actual = new JobProperties();
		actual.put(JobProperties.JobPropertiesEnum.JOB_EXCEPTION_HANDLER.getKey(),
				DefaultJobExceptionHandler.class.getCanonicalName());
		assertThat(getMap(actual).size(), is(1));
	}

	@Test
	public void assertDefaultValue() throws NoSuchFieldException {
		JobProperties actual = new JobProperties();
		assertThat(actual.get(JobProperties.JobPropertiesEnum.JOB_EXCEPTION_HANDLER),
				is(DefaultJobExceptionHandler.class.getCanonicalName()));
		assertThat(actual.get(JobProperties.JobPropertiesEnum.EXECUTOR_SERVICE_HANDLER),
				is(DefaultExecutorServiceHandler.class.getCanonicalName()));
	}

	@Test
	public void assertValueNotEmpty() throws NoSuchFieldException {
		JobProperties actual = new JobProperties();
		actual.put(JobProperties.JobPropertiesEnum.JOB_EXCEPTION_HANDLER.getKey(),
				IgnoreExceptionHandler.class.getCanonicalName());
		assertThat(actual.get(JobProperties.JobPropertiesEnum.JOB_EXCEPTION_HANDLER),
				is(IgnoreExceptionHandler.class.getCanonicalName()));
	}

	@Test
	public void assertJson() {
		assertThat(new JobProperties().json(),
				is(JsonConstants.getJobPropertiesJson(DefaultJobExceptionHandler.class.getCanonicalName())));
	}

	@Test
	public void assertJobPropertiesEnumFromValidValue() {
		assertThat(
				JobProperties.JobPropertiesEnum.from(JobProperties.JobPropertiesEnum.JOB_EXCEPTION_HANDLER.getKey()),
				is(JobProperties.JobPropertiesEnum.JOB_EXCEPTION_HANDLER));
	}

	@Test
	public void assertJobPropertiesEnumFromInvalidValue() {
		assertNull(JobProperties.JobPropertiesEnum.from("invalid_key"));
	}

	private Map getMap(JobProperties actual) throws NoSuchFieldException {
		return (Map) ReflectionUtils.getFieldValue(actual, JobProperties.class.getDeclaredField("map"));
	}

}
