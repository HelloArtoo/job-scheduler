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

import java.io.File;

public final class HomeFolderUtils {

	private static final String USER_HOME = System.getProperty("user.home");

	private static final String FILE_SEPARATOR = System.getProperty("file.separator");

	private static final String CONSOLE_ROOT_FOLDER = ".job-scheduler-console";

	private HomeFolderUtils() {
	}

	public static String getFilePathInHomeFolder(final String fileName) {
		return String.format("%s%s", getHomeFolder(), fileName);
	}

	public static void createHomeFolderIfNotExisted() {
		File file = new File(getHomeFolder());
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	private static String getHomeFolder() {
		return String.format("%s%s%s%s", USER_HOME, FILE_SEPARATOR, CONSOLE_ROOT_FOLDER, FILE_SEPARATOR);
	}
}
