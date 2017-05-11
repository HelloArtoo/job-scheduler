package com.jd.framework.job.utils.json;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.jd.framework.job.config.JobTypeConfiguration;
import com.jd.framework.job.executor.handler.threadpool.DefaultExecutorServiceHandler;
import com.jd.framework.job.fixture.JsonConstants;
import com.jd.framework.job.fixture.config.TestFlowJobConfiguration;
import com.jd.framework.job.fixture.config.TestJobRootConfiguration;
import com.jd.framework.job.fixture.config.TestSimpleJobConfiguration;
import com.jd.framework.job.fixture.handler.IgnoreExceptionHandler;
import com.jd.framework.job.fixture.handler.ThrowExceptionHandler;

public final class JobConfigurationGsonTypeAdapterTest {

	@BeforeClass
	public static void setUp() {
		GsonFactory.registerTypeAdapter(TestJobRootConfiguration.class, new JobConfigurationGsonTypeAdapter());
	}

	@Test
	public void assertToSimpleJobJson() {
		assertThat(
				GsonFactory.getGson().toJson(
						new TestJobRootConfiguration(new TestSimpleJobConfiguration(ThrowExceptionHandler.class
								.getCanonicalName(), DefaultExecutorServiceHandler.class.getCanonicalName())
								.getTypeConfig())), is(JsonConstants.getSimpleJobJson(ThrowExceptionHandler.class
						.getCanonicalName())));
	}

	@Test
	public void assertToFlowJobJson() {
		assertThat(
				GsonFactory.getGson().toJson(
						new TestJobRootConfiguration(new TestFlowJobConfiguration(true).getTypeConfig())),
				is(JsonConstants.getFlowJobJson(IgnoreExceptionHandler.class.getCanonicalName())));
	}

	@Test
	public void assertFromSimpleJobJson() {
		TestJobRootConfiguration actual = GsonFactory.getGson().fromJson(
				JsonConstants.getSimpleJobJson(ThrowExceptionHandler.class.getCanonicalName()),
				TestJobRootConfiguration.class);
		TestJobRootConfiguration expected = new TestJobRootConfiguration(
				new TestSimpleJobConfiguration(ThrowExceptionHandler.class.getCanonicalName(),
						DefaultExecutorServiceHandler.class.getCanonicalName()).getTypeConfig());
		assertThat(GsonFactory.getGson().toJson(actual), is(GsonFactory.getGson().toJson(expected)));
	}

	@Test
	public void assertFromFlowJobJson() {
		TestJobRootConfiguration actual = GsonFactory.getGson().fromJson(
				JsonConstants.getFlowJobJson(IgnoreExceptionHandler.class.getCanonicalName()),
				TestJobRootConfiguration.class);
		TestJobRootConfiguration expected = new TestJobRootConfiguration(
				new TestFlowJobConfiguration(true).getTypeConfig());
		assertThat(GsonFactory.getGson().toJson(actual), is(GsonFactory.getGson().toJson(expected)));
	}

	private static class JobConfigurationGsonTypeAdapter extends
			AbstractJobConfigGsonTypeAdapter<TestJobRootConfiguration> {

		@Override
		protected void addToCustomizedValueMap(final String jsonName, final JsonReader in,
				final Map<String, Object> customizedValueMap) throws IOException {
		}

		@Override
		protected TestJobRootConfiguration getJobRootConfiguration(final JobTypeConfiguration typeConfig,
				final Map<String, Object> customizedValueMap) {
			return new TestJobRootConfiguration(typeConfig);
		}

		@Override
		protected void writeCustomized(final JsonWriter out, final TestJobRootConfiguration value) throws IOException {
		}
	}
}
