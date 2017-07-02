/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.console.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class HomeFolderTest {

	private static final String HOME_FOLDER = System.getProperty("user.home") + System.getProperty("file.separator")
			+ ".job-scheduler-console" + System.getProperty("file.separator");

	@Test
	public void assertGetFilePathInHomeFolder() {
		assertThat(HomeFolderUtils.getFilePathInHomeFolder("test_file"), is(HOME_FOLDER + "test_file"));
	}

	@Test
	public void assertGetRegCenterConfigurations() {
		System.out.println(HomeFolderUtils.getFilePathInHomeFolder("RegCenterConfigurations.xml"));
	}

}
