/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.regcenter.zookeeper;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;
import org.apache.curator.test.KillSession;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.jd.framework.job.fixture.reg.EmbedTestingServer;
import com.jd.framework.job.regcenter.ZookeeperElectionService;
import com.jd.framework.job.regcenter.api.ElectionCandidate;

@Ignore
@RunWith(MockitoJUnitRunner.class)
public class ZookeeperElectionServiceTest {

	private static final String HOST_AND_PORT = "localhost:8899";

	private static final String ELECTION_PATH = "/election";

	@Mock
	private ElectionCandidate electionCandidate;

	@BeforeClass
	public static void init() throws InterruptedException {
		EmbedTestingServer.start();
	}

	@Test
	public void assertContend() throws Exception {
		CuratorFramework client = CuratorFrameworkFactory.newClient(EmbedTestingServer.getConnectionString(),
				new RetryOneTime(2000));
		client.start();
		client.blockUntilConnected();
		ZookeeperElectionService service = new ZookeeperElectionService(HOST_AND_PORT, client, ELECTION_PATH,
				electionCandidate);
		service.start();
		ElectionCandidate anotherElectionCandidate = mock(ElectionCandidate.class);
		CuratorFramework anotherClient = CuratorFrameworkFactory.newClient(EmbedTestingServer.getConnectionString(),
				new RetryOneTime(2000));
		ZookeeperElectionService anotherService = new ZookeeperElectionService("ANOTHER_CLIENT:8899", anotherClient,
				ELECTION_PATH, anotherElectionCandidate);
		anotherClient.start();
		anotherClient.blockUntilConnected();
		anotherService.start();
		KillSession.kill(client.getZookeeperClient().getZooKeeper(), EmbedTestingServer.getConnectionString());
		service.stop();
		// TODO
		verify(anotherElectionCandidate).startLeadership();
	}

}
