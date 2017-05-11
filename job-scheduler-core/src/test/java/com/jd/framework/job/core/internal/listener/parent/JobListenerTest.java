/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.core.internal.listener.parent;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JobListenerTest {

	@Mock
	private CuratorFramework client;

	@Mock
	private TreeCacheEvent event;

	private TestJobListener testJobListener = new TestJobListener();

	@Test
	public void assertChildEventWhenEventDataIsEmpty() throws Exception {
		when(event.getData()).thenReturn(null);
		testJobListener.childEvent(client, event);
		verify(client, times(0)).getNamespace();
	}

	@Test
	public void assertChildEventSuccess() throws Exception {
		when(event.getData()).thenReturn(new ChildData("/test_job", null, null));
		testJobListener.childEvent(client, event);
		verify(client).getNamespace();
	}

}
