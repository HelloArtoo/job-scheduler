/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.executor.context;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.jd.framework.job.constant.job.ExecutionType;

/**
 * 
 * 任务上下文
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */
@Getter
@EqualsAndHashCode(of = "id")
@ToString(of = "id")
public final class TaskContext {

	private static final String DELIMITER = "@-@";

	private static final String UNASSIGNED_SLAVE_ID = "unassigned-slave";

	private String id;

	private final MetaInfo metaInfo;

	private final ExecutionType type;

	private String slaveId;

	@Setter
	private boolean idle;

	public TaskContext(final String jobName, final List<Integer> segmentItem, final ExecutionType type) {
		this(jobName, segmentItem, type, UNASSIGNED_SLAVE_ID);
	}

	public TaskContext(final String jobName, final List<Integer> segmentItem, final ExecutionType type,
			final String slaveId) {
		metaInfo = new MetaInfo(jobName, segmentItem);
		this.type = type;
		this.slaveId = slaveId;
		id = Joiner.on(DELIMITER).join(metaInfo, type, slaveId, UUID.randomUUID().toString());
	}

	private TaskContext(final String id, final MetaInfo metaInfo, final ExecutionType type, final String slaveId) {
		this.id = id;
		this.metaInfo = metaInfo;
		this.type = type;
		this.slaveId = slaveId;
	}

	/**
	 * 根据任务主键获取任务上下文.
	 * 
	 * @param id
	 *            任务主键
	 * @return 任务上下文
	 */
	public static TaskContext from(final String id) {
		String[] result = id.split(DELIMITER);
		Preconditions.checkState(5 == result.length);
		return new TaskContext(id, MetaInfo.from(result[0] + DELIMITER + result[1]), ExecutionType.valueOf(result[2]),
				result[3]);
	}

	/**
	 * 获取未分配执行服务器前的任务主键.
	 * 
	 * @param id
	 *            任务主键
	 * @return 未分配执行服务器前的任务主键
	 */
	public static String getIdForUnassignedSlave(final String id) {
		return id.replaceAll(TaskContext.from(id).getSlaveId(), UNASSIGNED_SLAVE_ID);
	}

	/**
	 * 设置任务执行服务器主键.
	 * 
	 * @param slaveId
	 *            任务执行服务器主键
	 */
	public void setSlaveId(final String slaveId) {
		id = id.replaceAll(this.slaveId, slaveId);
		this.slaveId = slaveId;
	}

	/**
	 * 获取任务名称.
	 * 
	 * @return 任务名称
	 */
	public String getTaskName() {
		return Joiner.on(DELIMITER).join(metaInfo, type, slaveId);
	}

	/**
	 * 获取任务执行器主键.
	 * 
	 * @param appName
	 *            应用名称
	 * @return 任务执行器主键
	 */
	public String getExecutorId(final String appName) {
		return Joiner.on(DELIMITER).join(appName, slaveId);
	}

	/**
	 * 任务元信息.
	 */
	@RequiredArgsConstructor
	@Getter
	@EqualsAndHashCode
	public static class MetaInfo {

		private final String jobName;

		private final List<Integer> segmentItems;

		/**
		 * 根据任务元信息字符串获取元信息对象.
		 * 
		 * @param value
		 *            任务元信息字符串
		 * @return 元信息对象
		 */
		public static MetaInfo from(final String value) {
			String[] result = value.split(DELIMITER);
			Preconditions.checkState(1 == result.length || 2 == result.length || 5 == result.length);
			return new MetaInfo(result[0],
					1 == result.length || "".equals(result[1]) ? Collections.<Integer> emptyList() : Lists.transform(
							Splitter.on(",").splitToList(result[1]), new Function<String, Integer>() {

								@Override
								public Integer apply(final String input) {
									return Integer.parseInt(input);
								}
							}));
		}

		@Override
		public String toString() {
			return Joiner.on(DELIMITER).join(jobName, Joiner.on(",").join(segmentItems));
		}
	}
}
