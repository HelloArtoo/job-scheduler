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

import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.transaction.CuratorTransactionFinal;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.state.ConnectionStateListener;

import com.jd.framework.job.core.internal.callback.LeaderExecutionCallback;
import com.jd.framework.job.core.internal.callback.TransactionExecutionCallback;
import com.jd.framework.job.exception.JobSystemException;
import com.jd.framework.job.regcenter.api.CoordinatorRegistryCenter;
import com.jd.framework.job.regcenter.exception.RegExceptionHandler;

/**
 * 
 * 作业存储助手
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-6
 */
public class JobNodeStorageHelper {
	
	private final CoordinatorRegistryCenter regCenter;

	private final String jobName;

	private final JobNodePathHelper jobNodePathHelper;

	public JobNodeStorageHelper(final CoordinatorRegistryCenter regCenter, final String jobName) {
		this.regCenter = regCenter;
		this.jobName = jobName;
		jobNodePathHelper = new JobNodePathHelper(jobName);
	}

	/**
	 * 判断作业节点是否存在.
	 * 
	 * @param node
	 *            作业节点名称
	 * @return 作业节点是否存在
	 */
	public boolean isJobNodeExisted(final String node) {
		return regCenter.isExisted(jobNodePathHelper.getFullPath(node));
	}

	/**
	 * 获取作业节点数据.
	 * 
	 * @param node
	 *            作业节点名称
	 * @return 作业节点数据值
	 */
	public String getJobNodeData(final String node) {
		return regCenter.get(jobNodePathHelper.getFullPath(node));
	}

	/**
	 * 直接从注册中心而非本地缓存获取作业节点数据.
	 * 
	 * @param node
	 *            作业节点名称
	 * @return 作业节点数据值
	 */
	public String getJobNodeDataDirectly(final String node) {
		return regCenter.getDirectly(jobNodePathHelper.getFullPath(node));
	}

	/**
	 * 获取作业节点子节点名称列表.
	 * 
	 * @param node
	 *            作业节点名称
	 * @return 作业节点子节点名称列表
	 */
	public List<String> getJobNodeChildrenKeys(final String node) {
		return regCenter.getChildrenKeys(jobNodePathHelper.getFullPath(node));
	}

	/**
	 * 如果存在则创建作业节点.
	 * 
	 * <p>
	 * 如果作业根节点不存在表示作业已经停止, 不再继续创建节点.
	 * </p>
	 * 
	 * @param node
	 *            作业节点名称
	 */
	public void createJobNodeIfNeeded(final String node) {
		if (isJobRootNodeExisted() && !isJobNodeExisted(node)) {
			regCenter.persist(jobNodePathHelper.getFullPath(node), "");
		}
	}

	private boolean isJobRootNodeExisted() {
		return regCenter.isExisted("/" + jobName);
	}

	/**
	 * 删除作业节点.
	 * 
	 * @param node
	 *            作业节点名称
	 */
	public void removeJobNodeIfExisted(final String node) {
		if (isJobNodeExisted(node)) {
			regCenter.remove(jobNodePathHelper.getFullPath(node));
		}
	}

	/**
	 * 填充节点数据.
	 * 
	 * @param node
	 *            作业节点名称
	 * @param value
	 *            作业节点数据值
	 */
	public void fillJobNode(final String node, final Object value) {
		regCenter.persist(jobNodePathHelper.getFullPath(node), value.toString());
	}

	/**
	 * 填充临时节点数据.
	 * 
	 * @param node
	 *            作业节点名称
	 * @param value
	 *            作业节点数据值
	 */
	public void fillEphemeralJobNode(final String node, final Object value) {
		regCenter.persistEphemeral(jobNodePathHelper.getFullPath(node), value.toString());
	}

	/**
	 * 更新节点数据.
	 * 
	 * @param node
	 *            作业节点名称
	 * @param value
	 *            作业节点数据值
	 */
	public void updateJobNode(final String node, final Object value) {
		regCenter.update(jobNodePathHelper.getFullPath(node), value.toString());
	}

	/**
	 * 替换作业节点数据.
	 * 
	 * @param node
	 *            作业节点名称
	 * @param value
	 *            待替换的数据
	 */
	public void replaceJobNode(final String node, final Object value) {
		regCenter.persist(jobNodePathHelper.getFullPath(node), value.toString());
	}

	/**
	 * 在事务中执行操作.
	 * 
	 * @param callback
	 *            执行操作的回调
	 */
	public void executeInTransaction(final TransactionExecutionCallback callback) {
		try {
			CuratorTransactionFinal curatorTransactionFinal = getClient().inTransaction().check().forPath("/").and();
			callback.execute(curatorTransactionFinal);
			curatorTransactionFinal.commit();
		} catch (final Exception ex) {
			RegExceptionHandler.handleException(ex);
		}
	}

	/**
	 * 在主节点执行操作.
	 * 
	 * @param latchNode
	 *            分布式锁使用的作业节点名称
	 * @param callback
	 *            执行操作的回调
	 */
	public void executeInLeader(final String latchNode, final LeaderExecutionCallback callback) {
		try (LeaderLatch latch = new LeaderLatch(getClient(), jobNodePathHelper.getFullPath(latchNode))) {
			latch.start();
			latch.await();
			callback.execute();
		} catch (final Exception ex) {
			handleException(ex);
		}
	}

	private void handleException(final Exception ex) {
		if (ex instanceof InterruptedException) {
			Thread.currentThread().interrupt();
		} else {
			throw new JobSystemException(ex);
		}
	}

	/**
	 * 注册连接状态监听器.
	 * 
	 * @param listener
	 *            连接状态监听器
	 */
	public void addConnectionStateListener(final ConnectionStateListener listener) {
		getClient().getConnectionStateListenable().addListener(listener);
	}

	private CuratorFramework getClient() {
		return (CuratorFramework) regCenter.getRawClient();
	}

	/**
	 * 注册数据监听器.
	 * 
	 * @param listener
	 *            数据监听器
	 */
	public void addDataListener(final TreeCacheListener listener) {
		TreeCache cache = (TreeCache) regCenter.getRawCache("/" + jobName);
		cache.getListenable().addListener(listener);
	}

	/**
	 * 获取注册中心当前时间.
	 * 
	 * @return 注册中心当前时间
	 */
	public long getRegistryCenterTime() {
		return regCenter.getRegistryCenterTime(jobNodePathHelper.getFullPath("systemTime/current"));
	}
}
