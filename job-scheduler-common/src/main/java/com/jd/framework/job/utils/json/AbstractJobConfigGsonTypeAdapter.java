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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.jd.framework.job.config.JobRootConfiguration;
import com.jd.framework.job.config.JobTypeConfiguration;
import com.jd.framework.job.config.core.JobCoreConfiguration;
import com.jd.framework.job.config.type.FlowJobConfiguration;
import com.jd.framework.job.config.type.SimpleJobConfiguration;
import com.jd.framework.job.constant.job.JobType;
import com.jd.framework.job.executor.handler.JobProperties;

/**
 * 作业配置的Json转换适配器抽象层.
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-6
 */
public abstract class AbstractJobConfigGsonTypeAdapter<T extends JobRootConfiguration> extends TypeAdapter<T> {
	@Override
	public T read(final JsonReader in) throws IOException {
		String jobName = "";
		String cron = "";
		int segmentTotalCount = 0;
		String segmentItemParameters = "";
		String jobParameter = "";
		boolean failover = false;
		boolean misfire = failover;
		String description = "";
		JobProperties jobProperties = new JobProperties();
		JobType jobType = null;
		String jobClass = "";
		boolean streamingProcess = false;
		String scriptCommandLine = "";
		Map<String, Object> customizedValueMap = new HashMap<>(32, 1);
		in.beginObject();
		while (in.hasNext()) {
			String jsonName = in.nextName();
			switch (jsonName) {
			case "jobName":
				jobName = in.nextString();
				break;
			case "cron":
				cron = in.nextString();
				break;
			case "segmentTotalCount":
				segmentTotalCount = in.nextInt();
				break;
			case "segmentItemParameters":
				segmentItemParameters = in.nextString();
				break;
			case "jobParameter":
				jobParameter = in.nextString();
				break;
			case "failover":
				failover = in.nextBoolean();
				break;
			case "misfire":
				misfire = in.nextBoolean();
				break;
			case "description":
				description = in.nextString();
				break;
			case "jobProperties":
				jobProperties = getJobProperties(in);
				break;
			case "jobType":
				jobType = JobType.valueOf(in.nextString());
				break;
			case "jobClass":
				jobClass = in.nextString();
				break;
			case "streamingProcess":
				streamingProcess = in.nextBoolean();
				break;
			/*case "scriptCommandLine":
				scriptCommandLine = in.nextString();
				break;*/
			default:
				addToCustomizedValueMap(jsonName, in, customizedValueMap);
				break;
			}
		}
		in.endObject();
		JobCoreConfiguration coreConfig = getJobCoreConfiguration(jobName, cron, segmentTotalCount,
				segmentItemParameters, jobParameter, failover, misfire, description, jobProperties);
		JobTypeConfiguration typeConfig = getJobTypeConfiguration(coreConfig, jobType, jobClass, streamingProcess,
				scriptCommandLine);
		return getJobRootConfiguration(typeConfig, customizedValueMap);
	}

	private JobProperties getJobProperties(final JsonReader in) throws IOException {
		JobProperties result = new JobProperties();
		in.beginObject();
		while (in.hasNext()) {
			switch (in.nextName()) {
			case "job_exception_handler":
				result.put(JobProperties.JobPropertiesEnum.JOB_EXCEPTION_HANDLER.getKey(), in.nextString());
				break;
			case "executor_service_handler":
				result.put(JobProperties.JobPropertiesEnum.EXECUTOR_SERVICE_HANDLER.getKey(), in.nextString());
				break;
			default:
				break;
			}
		}
		in.endObject();
		return result;
	}

	protected abstract void addToCustomizedValueMap(final String jsonName, final JsonReader in,
			final Map<String, Object> customizedValueMap) throws IOException;

	private JobCoreConfiguration getJobCoreConfiguration(final String jobName, final String cron,
			final int segmentTotalCount, final String segmentItemParameters, final String jobParameter,
			final boolean failover, final boolean misfire, final String description, final JobProperties jobProperties) {
		return JobCoreConfiguration
				.newBuilder(jobName, cron, segmentTotalCount)
				.segmentItemParameters(segmentItemParameters)
				.jobParameter(jobParameter)
				.failover(failover)
				.misfire(misfire)
				.description(description)
				.jobProperties(JobProperties.JobPropertiesEnum.JOB_EXCEPTION_HANDLER.getKey(),
						jobProperties.get(JobProperties.JobPropertiesEnum.JOB_EXCEPTION_HANDLER))
				.jobProperties(JobProperties.JobPropertiesEnum.EXECUTOR_SERVICE_HANDLER.getKey(),
						jobProperties.get(JobProperties.JobPropertiesEnum.EXECUTOR_SERVICE_HANDLER)).build();
	}

	private JobTypeConfiguration getJobTypeConfiguration(final JobCoreConfiguration coreConfig, final JobType jobType,
			final String jobClass, final boolean streamingProcess, final String scriptCommandLine) {
		JobTypeConfiguration result;
		Preconditions.checkNotNull(jobType, "jobType cannot be null.");
		switch (jobType) {
		case SIMPLE:
			Preconditions.checkArgument(!Strings.isNullOrEmpty(jobClass), "jobClass cannot be empty.");
			result = new SimpleJobConfiguration(coreConfig, jobClass);
			break;
		case FLOW:
			Preconditions.checkArgument(!Strings.isNullOrEmpty(jobClass), "jobClass cannot be empty.");
			result = new FlowJobConfiguration(coreConfig, jobClass, streamingProcess);
			break;
		default:
			throw new UnsupportedOperationException(jobType.name());
		}
		return result;
	}

	protected abstract T getJobRootConfiguration(final JobTypeConfiguration typeConfig,
			final Map<String, Object> customizedValueMap);

	@Override
	public void write(final JsonWriter out, final T value) throws IOException {
		out.beginObject();
		out.name("jobName").value(value.getTypeConfig().getCoreConfig().getJobName());
		out.name("jobClass").value(value.getTypeConfig().getJobClass());
		out.name("jobType").value(value.getTypeConfig().getJobType().name());
		out.name("cron").value(value.getTypeConfig().getCoreConfig().getCron());
		out.name("segmentTotalCount").value(value.getTypeConfig().getCoreConfig().getSegmentTotalCount());
		out.name("segmentItemParameters").value(value.getTypeConfig().getCoreConfig().getSegmentItemParameters());
		out.name("jobParameter").value(value.getTypeConfig().getCoreConfig().getJobParameter());
		out.name("failover").value(value.getTypeConfig().getCoreConfig().isFailover());
		out.name("misfire").value(value.getTypeConfig().getCoreConfig().isMisfire());
		out.name("description").value(value.getTypeConfig().getCoreConfig().getDescription());
		out.name("jobProperties").jsonValue(value.getTypeConfig().getCoreConfig().getJobProperties().json());
		if (value.getTypeConfig().getJobType() == JobType.FLOW) {
			FlowJobConfiguration flowJobConfig = (FlowJobConfiguration) value.getTypeConfig();
			out.name("streamingProcess").value(flowJobConfig.isStreamingProcess());
		}
		writeCustomized(out, value);
		out.endObject();
	}

	protected abstract void writeCustomized(final JsonWriter out, final T value) throws IOException;
}
