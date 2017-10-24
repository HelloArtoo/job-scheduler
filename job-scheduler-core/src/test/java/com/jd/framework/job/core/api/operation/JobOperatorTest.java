package com.jd.framework.job.core.api.operation;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.jd.framework.job.core.internal.executor.RegCenterRegistry;
import com.jd.framework.job.core.internal.helper.JobNodePathHelper;
import com.jd.framework.job.regcenter.api.CoordinatorRegistryCenter;

public class JobOperatorTest {

    @Mock
    private CoordinatorRegistryCenter regCenter;

    private String jobName = "test_jobOperator";

    @Before
    public void setup() throws NoSuchFieldException {

        MockitoAnnotations.initMocks(this);
        RegCenterRegistry.getInstance().addJobRegistryCenter(jobName, regCenter);
    }

    @Test(expected = IllegalArgumentException.class)
    public void assertJobNameNull() {

        JobOperator.triggerJobAllNodes(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void assertRegCenterNull() {

        JobOperator.triggerJobAllNodes("test_jobOperator_notExist");
    }

    @Test
    public void assertRegCenter() {

        String ip = "192.168.101.1";
        JobNodePathHelper jobNodePath = new JobNodePathHelper(jobName);
        when(regCenter.getChildrenKeys(jobNodePath.getServerNodePath())).thenReturn(Arrays.asList(ip));
        // INVOKE
        JobOperator.triggerJobAllNodes(jobName);
        verify(regCenter).persist(jobNodePath.getServerNodePath(ip, JobNodePathHelper.TRIGGER_NODE), "");
    }
}
