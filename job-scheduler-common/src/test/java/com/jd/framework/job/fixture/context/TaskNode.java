/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.fixture.context;

import lombok.Builder;

import com.google.common.base.Joiner;
import com.jd.framework.job.constant.job.ExecutionType;

@Builder
public class TaskNode {

	private String jobName;

	private int segmentItem;

	private ExecutionType type;

	private String slaveId;

	private String uuid;

	public String getTaskNodePath() {
		return Joiner.on("@-@").join(null == jobName ? "test_job" : jobName, segmentItem);
	}

	public String getTaskNodeValue() {
		return Joiner.on("@-@").join(getTaskNodePath(), null == type ? ExecutionType.READY : type,
				null == slaveId ? "slave-S0" : slaveId, null == uuid ? "0" : uuid);
	}
}
