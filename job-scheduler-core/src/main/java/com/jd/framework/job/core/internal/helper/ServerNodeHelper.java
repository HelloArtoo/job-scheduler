/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.core.internal.helper;

import com.jd.framework.job.utils.env.LocalHostService;

/**
 * 
 * 作业服务器节点常量工具助手
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */
public final class ServerNodeHelper {
	/**
	 * 作业服务器信息根节点.
	 */
	public static final String ROOT = "servers";

	public static final String HOST_NAME = ROOT + "/%s/hostName";

	public static final String STATUS_APPENDIX = "status";

	public static final String STATUS = ROOT + "/%s/" + STATUS_APPENDIX;

	public static final String TRIGGER_APPENDIX = "trigger";

	public static final String TRIGGER = ROOT + "/%s/" + TRIGGER_APPENDIX;

	public static final String DISABLED_APPENDIX = "disabled";

	public static final String DISABLED = ROOT + "/%s/" + DISABLED_APPENDIX;

	public static final String PAUSED = ROOT + "/%s/paused";

	public static final String SHUTDOWN_APPENDIX = "shutdown";

	public static final String SHUTDOWN = ROOT + "/%s/" + SHUTDOWN_APPENDIX;

	private final LocalHostService localHostService = new LocalHostService();

	private final JobNodePathHelper jobNodePathHelper;

	public ServerNodeHelper(final String jobName) {
		jobNodePathHelper = new JobNodePathHelper(jobName);
	}

	public static String getHostNameNode(final String ip) {
		return String.format(HOST_NAME, ip);
	}

	public static String getStatusNode(final String ip) {
		return String.format(STATUS, ip);
	}

	public static String getTriggerNode(final String ip) {
		return String.format(TRIGGER, ip);
	}

	public static String getDisabledNode(final String ip) {
		return String.format(DISABLED, ip);
	}

	public static String getPausedNode(final String ip) {
		return String.format(PAUSED, ip);
	}

	public static String getShutdownNode(final String ip) {
		return String.format(SHUTDOWN, ip);
	}

	/**
	 * 判断给定路径是否为作业服务器立刻触发路径.
	 * 
	 * @param path
	 *            待判断的路径
	 * @return 是否为作业服务器立刻触发路径
	 */
	public boolean isLocalJobTriggerPath(final String path) {
		return path.startsWith(jobNodePathHelper.getFullPath(String.format(ServerNodeHelper.TRIGGER,
				localHostService.getIp())));
	}

	/**
	 * 判断给定路径是否为作业服务器暂停路径.
	 * 
	 * @param path
	 *            待判断的路径
	 * @return 是否为作业服务器暂停路径
	 */
	public boolean isLocalJobPausedPath(final String path) {
		return path.startsWith(jobNodePathHelper.getFullPath(String.format(ServerNodeHelper.PAUSED,
				localHostService.getIp())));
	}

	/**
	 * 判断给定路径是否为作业服务器关闭路径.
	 * 
	 * @param path
	 *            待判断的路径
	 * @return 是否为作业服务器关闭路径
	 */
	public boolean isLocalJobShutdownPath(final String path) {
		return path.startsWith(jobNodePathHelper.getFullPath(String.format(ServerNodeHelper.SHUTDOWN,
				localHostService.getIp())));
	}

	/**
	 * 判断给定路径是否为作业服务器禁用路径.
	 * 
	 * @param path
	 *            待判断的路径
	 * @return 是否为作业服务器禁用路径
	 */
	public boolean isLocalServerDisabledPath(final String path) {
		return path.startsWith(jobNodePathHelper.getFullPath(String.format(ServerNodeHelper.DISABLED,
				localHostService.getIp())));
	}

	/**
	 * 判断给定路径是否为作业服务器状态路径.
	 * 
	 * @param path
	 *            待判断的路径
	 * @return 是否为作业服务器状态路径
	 */
	public boolean isServerStatusPath(final String path) {
		return path.startsWith(jobNodePathHelper.getFullPath(ServerNodeHelper.ROOT))
				&& path.endsWith(ServerNodeHelper.STATUS_APPENDIX);
	}

	/**
	 * 判断给定路径是否为作业服务器禁用路径.
	 * 
	 * @param path
	 *            待判断的路径
	 * @return 是否为作业服务器禁用路径
	 */
	public boolean isServerDisabledPath(final String path) {
		return path.startsWith(jobNodePathHelper.getFullPath(ServerNodeHelper.ROOT))
				&& path.endsWith(ServerNodeHelper.DISABLED_APPENDIX);
	}

	/**
	 * 判断给定路径是否为作业服务器关闭路径.
	 * 
	 * @param path
	 *            待判断的路径
	 * @return 是否为作业服务器关闭路径
	 */
	public boolean isServerShutdownPath(final String path) {
		return path.startsWith(jobNodePathHelper.getFullPath(ServerNodeHelper.ROOT))
				&& path.endsWith(ServerNodeHelper.SHUTDOWN_APPENDIX);
	}
}
