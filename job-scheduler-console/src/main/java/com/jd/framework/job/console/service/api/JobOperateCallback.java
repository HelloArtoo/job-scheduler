/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.console.service.api;

public interface JobOperateCallback {

	/**
	 * 操作作业.
	 * 
	 * @param jobName
	 *            作业名称
	 * @param serverIp
	 *            服务器地址
	 * @return 操作是否成功
	 */
	boolean operate(String jobName, String serverIp);
}
