/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.event.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * 
 * 作业执行中的 Throwable
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@ToString(of = "plainText")
public class JobExecutionEventThrowable {

	private final Throwable throwable;

	private String plainText;
}
