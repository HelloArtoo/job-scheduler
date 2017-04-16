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

import org.apache.curator.framework.api.transaction.CuratorTransactionFinal;

/**
 * 
 * 执行事务的回低调接口
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-6
 */
public interface TransactionExecutionCallback {
	/**
	 * 事务执行的回调方法.
	 * 
	 * @param curatorTransactionFinal
	 *            执行事务的上下文
	 * @throws Exception
	 *             处理中异常
	 */
	void execute(CuratorTransactionFinal curatorTransactionFinal)
			throws Exception;
}
