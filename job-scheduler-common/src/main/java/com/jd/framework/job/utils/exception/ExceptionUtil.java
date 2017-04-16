/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.utils.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 
 * 异常工具处理
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionUtil {

	public static String transform(final Throwable cause) {
		if (null == cause) {
			return "";
		}
		StringWriter result = new StringWriter();
		try (PrintWriter writer = new PrintWriter(result)) {
			cause.printStackTrace(writer);
		}
		return result.toString();
	}
}
