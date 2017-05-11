/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.utils.digest;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import com.jd.framework.job.exception.JobSystemException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EncryptionUtils {

	private static final String MD5 = "MD5";

	/**
	 * 采用MD5算法加密字符串.
	 * 
	 * @param str
	 *            需要加密的字符串
	 * @return 加密后的字符串
	 */
	public static String md5(final String str) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance(MD5);
			messageDigest.update(str.getBytes());
			return new BigInteger(1, messageDigest.digest()).toString(16);
		} catch (final NoSuchAlgorithmException ex) {
			throw new JobSystemException(ex);
		}
	}
}
