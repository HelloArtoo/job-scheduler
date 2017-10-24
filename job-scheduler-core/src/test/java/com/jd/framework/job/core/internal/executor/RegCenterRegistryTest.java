package com.jd.framework.job.core.internal.executor;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import com.jd.framework.job.regcenter.api.CoordinatorRegistryCenter;

public class RegCenterRegistryTest {

    @Test
    public void assertAddJobRegistryCenter() {

        CoordinatorRegistryCenter regCenter = mock(CoordinatorRegistryCenter.class);
        String jobName = "test_job_AddRegistyCenter";
        RegCenterRegistry.getInstance().addJobRegistryCenter(jobName, regCenter);
        assertThat(RegCenterRegistry.getInstance().getJobRegistryCenter(jobName), is(regCenter));
    }
}
