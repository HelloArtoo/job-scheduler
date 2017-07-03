/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.regcenter;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.jd.framework.job.regcenter.api.CoordinatorRegistryCenter;
import com.jd.framework.job.regcenter.conf.ZookeeperConfiguration;
import com.jd.framework.job.regcenter.exception.RegExceptionHandler;

/**
 * 
 * 基于zookeeper的注册中心
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-3
 */
@Slf4j
public class ZookeeperRegistryCenter implements CoordinatorRegistryCenter {

    private static final String DIGEST = "digest";
    /**
     * zk配置
     */
    @Getter(AccessLevel.PUBLIC)
    private ZookeeperConfiguration zkConfig;
    /** 连接本地缓存 */
    private final Map<String, TreeCache> caches = new HashMap<>();
    /** curator 客户端 */
    private CuratorFramework client;

    public ZookeeperRegistryCenter(final ZookeeperConfiguration zkConfig) {
        this.zkConfig = zkConfig;
    }

    @Override
    public void init() {

        log.debug("job-scheduler: zookeeper registry center init, the server list :{}", zkConfig.getServerLists());
        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder()
            .connectString(zkConfig.getServerLists())
            .retryPolicy(new ExponentialBackoffRetry(zkConfig.getBaseSleepTimeMilliseconds(), zkConfig.getMaxRetries(),
                zkConfig.getMaxSleepTimeMilliseconds()))
            .namespace(zkConfig.getNamespace());

        // session timeout
        if (0 != zkConfig.getSessionTimeoutMilliseconds()) {
            builder.sessionTimeoutMs(zkConfig.getSessionTimeoutMilliseconds());
        }

        // connection timeout
        if (0 != zkConfig.getConnectionTimeoutMilliseconds()) {
            builder.connectionTimeoutMs(zkConfig.getConnectionTimeoutMilliseconds());
        }

        // digest
        if (!Strings.isNullOrEmpty(zkConfig.getDigest())) {
            builder.authorization(DIGEST, zkConfig.getDigest().getBytes(Charsets.UTF_8)).aclProvider(new ACLProvider() {

                @Override
                public List<ACL> getDefaultAcl() {

                    return ZooDefs.Ids.CREATOR_ALL_ACL;
                }

                @Override
                public List<ACL> getAclForPath(final String path) {

                    return ZooDefs.Ids.CREATOR_ALL_ACL;
                }
            });
        }

        client = builder.build();
        client.start();

        // handle time block
        try {
            if (!client.blockUntilConnected(zkConfig.getMaxSleepTimeMilliseconds() * zkConfig.getMaxRetries(),
                TimeUnit.MILLISECONDS)) {
                client.close();
                throw new KeeperException.OperationTimeoutException();
            }
        } catch (final Exception e) {
            RegExceptionHandler.handleException(e);
        }

    }

    /**
     * 关闭
     */
    @Override
    public void close() {

        for (Entry<String, TreeCache> each : caches.entrySet()) {
            each.getValue().close();
        }
        waitForCacheClose();
        // invoke Curator close
        CloseableUtils.closeQuietly(client);
    }

    /**
     * 等待500ms, cache先关闭再关闭client, 否则会抛异常 因为异步处理, 可能会导致client先关闭而cache还未关闭结束.
     * 等待Curator新版本解决这个bug.
     * BUG地址：https://issues.apache.org/jira/browse/CURATOR-157
     */
    private void waitForCacheClose() {

        // FIXME 等待Curator新版本解决这个bug.
        try {
            Thread.sleep(500L);
        } catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public String get(final String key) {

        // tree cache缓存节点
        TreeCache cache = this.findTreeCache(key);

        if (null == cache) {
            return getDirectly(key);
        }

        ChildData result = cache.getCurrentData(key);
        if (null != result) {
            return null == result.getData() ? null : new String(result.getData(), Charsets.UTF_8);
        }

        return getDirectly(key);
    }

    private TreeCache findTreeCache(final String key) {

        for (Entry<String, TreeCache> entry : caches.entrySet()) {
            if (key.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    @Override
    public boolean isExisted(String key) {

        try {
            return null != client.checkExists().forPath(key);
        } catch (final Exception e) {
            RegExceptionHandler.handleException(e);
        }
        return false;
    }

    @Override
    public void persist(final String key, final String value) {

        try {
            if (!isExisted(key)) {
                client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(key,
                    value.getBytes(Charsets.UTF_8));
            } else {
                this.update(key, value);
            }
        } catch (final Exception e) {
            RegExceptionHandler.handleException(e);
        }
    }

    @Override
    public void update(final String key, final String value) {

        try {
            client.inTransaction().check().forPath(key).and().setData().forPath(key, value.getBytes(Charsets.UTF_8))
                .and().commit();
        } catch (final Exception e) {
            RegExceptionHandler.handleException(e);
        }

    }

    @Override
    public void remove(final String key) {

        try {
            client.delete().deletingChildrenIfNeeded().forPath(key);
        } catch (final Exception e) {
            RegExceptionHandler.handleException(e);
        }
    }

    @Override
    public long getRegistryCenterTime(final String key) {

        long rst = 0L;
        try {
            String forPath = client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                .forPath(key);
            rst = client.checkExists().forPath(forPath).getCtime();
        } catch (final Exception e) {
            RegExceptionHandler.handleException(e);
        }
        Preconditions.checkState(0L != rst, "can not get registry center time");
        return rst;
    }

    @Override
    public Object getRawClient() {

        return client;
    }

    @Override
    public String getDirectly(final String key) {

        try {
            return new String(client.getData().forPath(key), Charsets.UTF_8);
        } catch (final Exception e) {
            RegExceptionHandler.handleException(e);
            return null;
        }
    }

    @Override
    public List<String> getChildrenKeys(final String key) {

        try {
            List<String> keys = client.getChildren().forPath(key);
            Collections.sort(keys, new Comparator<String>() {

                @Override
                public int compare(String o1, String o2) {

                    return o2.compareTo(o1);
                }
            });
            return keys;
        } catch (final Exception e) {
            RegExceptionHandler.handleException(e);
        }
        return Collections.emptyList();
    }

    @Override
    public int getNumChildren(final String key) {

        try {
            Stat exists = client.getZookeeperClient().getZooKeeper().exists(getNameSpace() + key, false);
            if (null != exists) {
                return exists.getNumChildren();
            }

        } catch (final Exception e) {
            RegExceptionHandler.handleException(e);
        }
        return 0;
    }

    /**
     * 命名空间
     * 
     * @return String
     */
    private String getNameSpace() {

        String result = this.getZkConfig().getNamespace();
        return Strings.isNullOrEmpty(result) ? "" : "/" + result;
    }

    @Override
    public void persistEphemeral(final String key, final String value) {

        try {
            if (isExisted(key)) {
                client.delete().deletingChildrenIfNeeded().forPath(key);
            }
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(key,
                value.getBytes(Charsets.UTF_8));
        } catch (final Exception e) {
            RegExceptionHandler.handleException(e);
        }
    }

    @Override
    public String persistSequential(final String key, final String value) {

        try {
            return client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT_SEQUENTIAL).forPath(key,
                value.getBytes(Charsets.UTF_8));
        } catch (final Exception e) {
            RegExceptionHandler.handleException(e);
        }

        return null;
    }

    @Override
    public void persistEphemeralSequential(final String key) {

        try {
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(key);
        } catch (final Exception e) {
            RegExceptionHandler.handleException(e);
        }

    }

    @Override
    public void addCacheData(final String cachePath) {

        TreeCache cache = new TreeCache(client, cachePath);
        try {
            cache.start();
        } catch (final Exception e) {
            RegExceptionHandler.handleException(e);
        }

        caches.put(cachePath + "/", cache);
    }

    @Override
    public Object getRawCache(final String cachePath) {

        return caches.get(cachePath + "/");
    }

}
