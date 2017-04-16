/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.core.internal.callback;

/**
 * 
 * 主节点执行的回调接口
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-6
 */
public interface LeaderExecutionCallback {

	/**
	 * 节点选中之后执行的回调方法.
	 */
	void execute();

}
