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

import java.util.concurrent.CountDownLatch;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;

import com.jd.framework.job.exception.JobSystemException;
import com.jd.framework.job.regcenter.api.ElectionCandidate;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 通过{@link LeaderSelector}实现选举
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-3
 */
@Slf4j
public class ZookeeperElectionService {

	private final CountDownLatch leaderLatch = new CountDownLatch(1);
	private final LeaderSelector leaderSelector;

	public ZookeeperElectionService(final String identity,
			CuratorFramework client, final String electionPath,
			final ElectionCandidate electionCandidate) {
		leaderSelector = new LeaderSelector(client, electionPath,
				new LeaderSelectorListenerAdapter() {

					@Override
					public void takeLeadership(CuratorFramework client)
							throws Exception {
						log.info("Job-scheduler:{} has leadership", identity);
						try {
							electionCandidate.startLeadership();
							leaderLatch.await();
							log.warn("Job-scheduler:{} lost leadership",
									identity);
							electionCandidate.stopLeadership();
						} catch (JobSystemException e) {
							log.error("Job-scheduler system error happened", e);
							System.exit(1);
						}
					}
				});
		leaderSelector.autoRequeue();
		leaderSelector.setId(identity);
	}

	/**
	 * 开始选举
	 * 
	 * @author Rong Hu
	 */
	public void start() {
		log.debug("Job-scheduler:{} start electing leadership",
				leaderSelector.getId());
		leaderSelector.start();
	}

	/**
	 * 选举取消
	 * 
	 * @author Rong Hu
	 */
	public void stop() {
		log.info("Job-scheduler is stopping the election");
		leaderLatch.countDown();
		try {
			leaderSelector.close();
		} catch (Exception e) {
			// ignore
		}
	}
}
