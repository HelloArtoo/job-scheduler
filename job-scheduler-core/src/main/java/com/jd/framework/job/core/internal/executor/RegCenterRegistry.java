package com.jd.framework.job.core.internal.executor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.jd.framework.job.regcenter.api.CoordinatorRegistryCenter;

/**
 * 
 * 作业注册中心表
 * 
 * @author Rong Hu
 * @version 1.0, 2017-10-24
 */
public class RegCenterRegistry {

    private static volatile RegCenterRegistry instance;

    private Map<String, CoordinatorRegistryCenter> registryMap = new ConcurrentHashMap<>();

    /**
     * 获取作业注册中心表实例.
     * 
     * @return
     */
    public static RegCenterRegistry getInstance() {

        if (null == instance) {
            synchronized (RegCenterRegistry.class) {
                if (null == instance) {
                    instance = new RegCenterRegistry();
                }
            }
        }
        return instance;
    }

    /**
     * 添加作业注册中心对象.
     * 
     * @param jobName
     *            作业名称
     * @param registryCenter
     *            注册中心对象
     */
    public void addJobRegistryCenter(final String jobName, final CoordinatorRegistryCenter registryCenter) {

        if (jobName == null || registryCenter == null)
            return;

        registryMap.put(jobName, registryCenter);
    }

    /**
     * 获取作业注册中心对象.
     * 
     * @param jobName
     *            作业名称
     * @return 注册中心对象
     */
    public CoordinatorRegistryCenter getJobRegistryCenter(final String jobName) {

        return registryMap.get(jobName);
    }
}
