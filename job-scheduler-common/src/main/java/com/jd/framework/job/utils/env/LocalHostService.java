/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.utils.env;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * 
 * 主机服务工具类
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */
public class LocalHostService {

	private static volatile String cachedIpAddress;

	/**
	 * 获取本机IP地址.
	 * 
	 * <p>
	 * 有限获取外网IP地址. 也有可能是链接着路由器的最终IP地址.
	 * </p>
	 * 
	 * @return 本机IP地址
	 */
	public String getIp() {
		if (null != cachedIpAddress) {
			return cachedIpAddress;
		}
		Enumeration<NetworkInterface> netInterfaces;
		try {
			netInterfaces = NetworkInterface.getNetworkInterfaces();
		} catch (final SocketException ex) {
			throw new HostException(ex);
		}
		String localIpAddress = null;
		while (netInterfaces.hasMoreElements()) {
			NetworkInterface netInterface = netInterfaces.nextElement();
			Enumeration<InetAddress> ipAddresses = netInterface.getInetAddresses();
			while (ipAddresses.hasMoreElements()) {
				InetAddress ipAddress = ipAddresses.nextElement();
				if (isPublicIpAddress(ipAddress)) {
					String publicIpAddress = ipAddress.getHostAddress();
					cachedIpAddress = publicIpAddress;
					return publicIpAddress;
				}
				if (isLocalIpAddress(ipAddress)) {
					localIpAddress = ipAddress.getHostAddress();
				}
			}
		}
		cachedIpAddress = localIpAddress;
		return localIpAddress;
	}

	private boolean isPublicIpAddress(final InetAddress ipAddress) {
		return !ipAddress.isSiteLocalAddress() && !ipAddress.isLoopbackAddress() && !isV6IpAddress(ipAddress);
	}

	private boolean isLocalIpAddress(final InetAddress ipAddress) {
		return ipAddress.isSiteLocalAddress() && !ipAddress.isLoopbackAddress() && !isV6IpAddress(ipAddress);
	}

	private boolean isV6IpAddress(final InetAddress ipAddress) {
		return ipAddress.getHostAddress().contains(":");
	}

	/**
	 * 获取本机Host名称.
	 * 
	 * @return 本机Host名称
	 */
	public String getHostName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (final UnknownHostException ex) {
			throw new HostException(ex);
		}
	}

	/**
	 * 
	 * 网络主机自定义异常
	 * 
	 * @author Rong Hu
	 * @version 1.0, 2017-4-9
	 */
	static class HostException extends RuntimeException {

		private static final long serialVersionUID = -7451646137661999912L;

		public HostException(final IOException cause) {
			super(cause);
		}
	}
}
