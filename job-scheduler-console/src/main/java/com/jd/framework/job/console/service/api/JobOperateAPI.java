/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.console.service.api;

import java.util.Collection;

import com.google.common.base.Optional;
import com.jd.framework.job.core.internal.helper.JobNodePathHelper;
import com.jd.framework.job.regcenter.api.CoordinatorRegistryCenter;

public final class JobOperateAPI {

	private final CoordinatorRegistryCenter regCenter;

	private final JobOperateTemplate jobOperatorTemplate;

	public JobOperateAPI(final CoordinatorRegistryCenter regCenter) {
		this.regCenter = regCenter;
		jobOperatorTemplate = new JobOperateTemplate(regCenter);
	}

	/**
	 * 作业立刻执行.
	 * 
	 * <p>
	 * 作业在不与上次运行中作业冲突的情况下才会启动, 并在启动后自动清理此标记.
	 * </p>
	 * 
	 * @param jobName
	 *            作业名称
	 * @param serverIp
	 *            作业服务器IP地址
	 */
	public void trigger(final Optional<String> jobName, final Optional<String> serverIp) {
		jobOperatorTemplate.operate(jobName, serverIp, new JobOperateCallback() {

			@Override
			public boolean operate(final String jobName, final String serverIp) {
				regCenter.persist(
						new JobNodePathHelper(jobName).getServerNodePath(serverIp, JobNodePathHelper.TRIGGER_NODE), "");
				return true;
			}
		});
	}

	/**
	 * 作业暂停.
	 * 
	 * <p>
	 * 不会导致重新分段.
	 * </p>
	 * 
	 * @param jobName
	 *            作业名称
	 * @param serverIp
	 *            作业服务器IP地址
	 */
	public void pause(final Optional<String> jobName, final Optional<String> serverIp) {
		jobOperatorTemplate.operate(jobName, serverIp, new JobOperateCallback() {

			@Override
			public boolean operate(final String jobName, final String serverIp) {
				regCenter.persist(
						new JobNodePathHelper(jobName).getServerNodePath(serverIp, JobNodePathHelper.PAUSED_NODE), "");
				return true;
			}
		});
	}

	/**
	 * 作业恢复.
	 * 
	 * @param jobName
	 *            作业名称
	 * @param serverIp
	 *            作业服务器IP地址
	 */
	public void resume(final Optional<String> jobName, final Optional<String> serverIp) {
		jobOperatorTemplate.operate(jobName, serverIp, new JobOperateCallback() {

			@Override
			public boolean operate(final String jobName, final String serverIp) {
				regCenter.remove(new JobNodePathHelper(jobName).getServerNodePath(serverIp,
						JobNodePathHelper.PAUSED_NODE));
				return true;
			}
		});
	}

	/**
	 * 作业禁用.
	 * 
	 * <p>
	 * 会重新分段.
	 * </p>
	 * 
	 * @param jobName
	 *            作业名称
	 * @param serverIp
	 *            作业服务器IP地址
	 */
	public void disable(final Optional<String> jobName, final Optional<String> serverIp) {
		jobOperatorTemplate.operate(jobName, serverIp, new JobOperateCallback() {

			@Override
			public boolean operate(final String jobName, final String serverIp) {
				regCenter.persist(
						new JobNodePathHelper(jobName).getServerNodePath(serverIp, JobNodePathHelper.DISABLED_NODE), "");
				return true;
			}
		});
	}

	/**
	 * 作业启用.
	 * 
	 * @param jobName
	 *            作业名称
	 * @param serverIp
	 *            作业服务器IP地址
	 */
	public void enable(final Optional<String> jobName, final Optional<String> serverIp) {
		jobOperatorTemplate.operate(jobName, serverIp, new JobOperateCallback() {

			@Override
			public boolean operate(final String jobName, final String serverIp) {
				regCenter.remove(new JobNodePathHelper(jobName).getServerNodePath(serverIp,
						JobNodePathHelper.DISABLED_NODE));
				return true;
			}
		});
	}

	/**
	 * 作业关闭.
	 * 
	 * @param jobName
	 *            作业名称
	 * @param serverIp
	 *            作业服务器IP地址
	 */
	public void shutdown(final Optional<String> jobName, final Optional<String> serverIp) {
		jobOperatorTemplate.operate(jobName, serverIp, new JobOperateCallback() {

			@Override
			public boolean operate(final String jobName, final String serverIp) {
				regCenter.persist(
						new JobNodePathHelper(jobName).getServerNodePath(serverIp, JobNodePathHelper.SHUTDOWN_NODE), "");
				return true;
			}
		});
	}

	/**
	 * 作业删除.
	 * 
	 * <p>
	 * 只有停止运行的作业才能删除.
	 * </p>
	 * 
	 * @param jobName
	 *            作业名称
	 * @param serverIp
	 *            作业服务器IP地址
	 * @return 因为未停止而导致未能成功删除的作业服务器IP地址列表
	 */
	public Collection<String> remove(final Optional<String> jobName, final Optional<String> serverIp) {
		return jobOperatorTemplate.operate(jobName, serverIp, new JobOperateCallback() {

			@Override
			public boolean operate(final String jobName, final String serverIp) {
				JobNodePathHelper jobNodePath = new JobNodePathHelper(jobName);
				if (regCenter.isExisted(jobNodePath.getServerNodePath(serverIp, JobNodePathHelper.STATUS_NODE))
						|| regCenter.isExisted(jobNodePath.getLeaderHostNodePath())) {
					return false;
				}
				regCenter.remove(jobNodePath.getServerNodePath(serverIp));
				if (0 == regCenter.getNumChildren(jobNodePath.getServerNodePath())) {
					regCenter.remove("/" + jobName);
				}
				return true;
			}
		});
	}
}
