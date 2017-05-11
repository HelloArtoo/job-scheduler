/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.utils.json;

import static org.junit.Assert.assertThat;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class GsonFactoryTest {

	@Test
	public void assertGetGson() {
		assertThat(GsonFactory.getGson(), is(GsonFactory.getGson()));
	}

	@Test
	public void assertRegisterTypeAdapter() {
		Gson beforeRegisterGson = GsonFactory.getGson();
		GsonFactory.registerTypeAdapter(GsonFactoryTest.class, new TypeAdapter() {

			@Override
			public Object read(final JsonReader in) throws IOException {
				return null;
			}

			@Override
			public void write(final JsonWriter out, final Object value) throws IOException {
				out.jsonValue("test write json");
			}
		});
		assertThat(beforeRegisterGson.toJson(new GsonFactoryTest()), is("{}"));
		assertThat(GsonFactory.getGson().toJson(new GsonFactoryTest()), is("test write json"));
	}
}
