/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.event.rdb;

import com.jd.framework.job.event.JobEventIdentity;

/**
 * 
 * 数据库作业标识
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-15
 */
public class RdbJobEventIdentity implements JobEventIdentity {

	@Override
	public String getIdentity() {
		return "rdb";
	}

}
