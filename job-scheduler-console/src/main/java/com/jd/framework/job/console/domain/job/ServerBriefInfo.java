/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.console.domain.job;

import java.io.Serializable;
import java.util.Collection;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class ServerBriefInfo implements Serializable, Comparable<ServerBriefInfo> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6977330645298545434L;

	private String serverIp;

	private String serverHostName;

	private ServerBriefStatus status;

	@Override
	public int compareTo(final ServerBriefInfo o) {
		return getServerIp().compareTo(o.getServerIp());
	}

	/**
	 * 作业服务器状态.
	 */
	public enum ServerBriefStatus {

		OK, PARTIAL_ALIVE, ALL_CRASHED;

		/**
		 * 获取作业服务器状态.
		 * 
		 * @param aliveServers
		 *            存活的作业服务器集合
		 * @param crashedServers
		 *            崩溃的作业服务器集合
		 * @param serverIp
		 *            作业服务器IP地址
		 * @return 作业服务器状态
		 */
		public static ServerBriefStatus getServerBriefStatus(final Collection<String> aliveServers,
				final Collection<String> crashedServers, final String serverIp) {
			if (!aliveServers.contains(serverIp)) {
				return ALL_CRASHED;
			}
			if (!crashedServers.contains(serverIp)) {
				return OK;
			}
			return PARTIAL_ALIVE;
		}
	}
}
