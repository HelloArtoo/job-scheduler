/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.constant.rdb;

import java.util.Arrays;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;

/**
 * 数据库枚举
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-15
 */
public enum Database {

	MySQL("MySQL"), Oracle("Oracle"), SQLServer("Microsoft SQL Server"), DB2("DB2"), PostgreSQL("PostgreSQL"), H2("H2");

	private final String productName;

	Database(final String productName) {
		this.productName = productName;
	}

	/**
	 * 获取数据库类型枚举.
	 * 
	 * @param databaseProductName
	 *            数据库类型
	 * @return 数据库类型枚举
	 */
	public static Database valueFrom(final String databaseProductName) {
		Optional<Database> databaseOptional = Iterators.tryFind(Arrays.asList(Database.values()).iterator(),
				new Predicate<Database>() {
					@Override
					public boolean apply(final Database input) {
						return input.productName.equals(databaseProductName);
					}
				});
		if (databaseOptional.isPresent()) {
			return databaseOptional.get();
		} else {
			throw new RuntimeException("Unsupported database:" + databaseProductName);
		}
	}
}
