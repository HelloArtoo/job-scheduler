package com.jd.framework.job.core.api.operation;

import java.util.List;

import com.google.common.base.Preconditions;
import com.jd.framework.job.core.internal.executor.RegCenterRegistry;
import com.jd.framework.job.core.internal.helper.JobNodePathHelper;
import com.jd.framework.job.regcenter.api.CoordinatorRegistryCenter;

/**
 * 
 * <pre>
 *  作业触发辅助工具，用于更灵活的触发。
 *      1、基于MQ消息的触发
 *      2、基于RPC框架的远程触发。
 *      3、基于数据库控制的
 *      4、发挥想象力...
 * </pre>
 * 
 * @author Rong Hu
 * @version 1.0, 2017-10-24
 */
public final class JobOperator {

    /**
     * 根据作业名称触发所有节点的执行
     * 
     * @param jobName
     */
    public static void triggerJobAllNodes(final String jobName) {

        Preconditions.checkArgument(jobName != null, "jobName is null.");
        JobNodePathHelper jobNodePath = new JobNodePathHelper(jobName);

        // REG CENTER
        CoordinatorRegistryCenter regCenter = RegCenterRegistry.getInstance().getJobRegistryCenter(jobName);
        Preconditions.checkArgument(regCenter != null, "regCenter is null.");
        List<String> ipList = regCenter.getChildrenKeys(jobNodePath.getServerNodePath());
        // trigger all jobs
        for (String serverIp : ipList) {
            regCenter.persist(jobNodePath.getServerNodePath(serverIp, JobNodePathHelper.TRIGGER_NODE), "");
        }
    }

    /**
     * 传入注册中心
     * 
     * @param jobName
     *            作业名称
     * @param regCenter
     *            注册中心
     */
    public static void triggerJobAllNodes(final String jobName, final CoordinatorRegistryCenter regCenter) {

        Preconditions.checkArgument(jobName != null && regCenter != null, "jobName or regCenter is null.");
        JobNodePathHelper jobNodePath = new JobNodePathHelper(jobName);
        List<String> ipList = regCenter.getChildrenKeys(jobNodePath.getServerNodePath());
        // trigger all jobs
        for (String serverIp : ipList) {
            regCenter.persist(jobNodePath.getServerNodePath(serverIp, JobNodePathHelper.TRIGGER_NODE), "");
        }
    }
}
