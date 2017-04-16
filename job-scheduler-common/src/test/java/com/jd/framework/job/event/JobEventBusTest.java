/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.event;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.unitils.util.ReflectionUtils;

import com.google.common.eventbus.EventBus;
import com.jd.framework.job.event.fixture.JobEventCaller;
import com.jd.framework.job.event.fixture.TestJobEventConfiguration;
import com.jd.framework.job.event.fixture.TestJobEventFailureConfiguration;
import com.jd.framework.job.event.fixture.TestJobEventListener;
import com.jd.framework.job.event.type.JobExecutionEvent;
import com.jd.framework.job.event.type.JobExecutionEvent.ExecutionSource;
import com.jd.framework.job.utils.concurrent.BlockUtils;

@RunWith(MockitoJUnitRunner.class)
public final class JobEventBusTest {

	@Mock
	private JobEventCaller jobEventCaller;

	@Mock
	private EventBus eventBus;

	private JobEventBus jobEventBus;

	/**
	 * 不带监听
	 * 
	 * @throws NoSuchFieldException
	 * @author Rong Hu
	 */
	@Test
	public void assertPostWithoutListener() throws NoSuchFieldException {
		jobEventBus = new JobEventBus();
		assertIsRegistered(false);
		ReflectionUtils.setFieldValue(jobEventBus, "eventBus", eventBus);
		jobEventBus
				.post(new JobExecutionEvent("test_task_id", "test_event_bus_job", ExecutionSource.NORMAL_TRIGGER, 0));
		verify(eventBus, times(0)).post(Matchers.<JobEvent> any());
	}

	/**
	 * 异常情况注册失败
	 * 
	 * @throws NoSuchFieldException
	 * @author Rong Hu
	 */
	@Test
	public void assertRegisterFailure() throws NoSuchFieldException {
		jobEventBus = new JobEventBus(new TestJobEventFailureConfiguration());
		assertIsRegistered(false);
	}

	/**
	 * 发送
	 * 
	 * @throws NoSuchFieldException
	 * @author Rong Hu
	 */
	@Test
	public void assertPost() throws NoSuchFieldException {
		jobEventBus = new JobEventBus(new TestJobEventConfiguration(jobEventCaller));
		assertIsRegistered(true);
		// 发布
		jobEventBus
				.post(new JobExecutionEvent("test_task_id", "test_event_bus_job", ExecutionSource.NORMAL_TRIGGER, 0));
		while (!TestJobEventListener.isExecutionEventCalled()) {
			BlockUtils.waitingShortTime();
		}

		verify(jobEventCaller).call();
	}

	private void assertIsRegistered(final boolean actual) throws NoSuchFieldException {
		assertThat((boolean) ReflectionUtils.getFieldValue(jobEventBus,
				JobEventBus.class.getDeclaredField("isRegistered")), is(actual));
	}

}
