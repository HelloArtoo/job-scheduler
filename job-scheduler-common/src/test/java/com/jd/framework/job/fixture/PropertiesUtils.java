/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.fixture;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public final class PropertiesUtils {

	public static Properties getZookeeperProperties() throws IOException {
		Properties prop = new Properties();
		System.out.println(System.getProperty("user.dir"));
		FileInputStream fis = new FileInputStream("classpath:zookeeper.properties");
		prop.load(fis);
		return prop;
	}
}
