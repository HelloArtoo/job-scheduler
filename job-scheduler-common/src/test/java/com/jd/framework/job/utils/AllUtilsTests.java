/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */     
package com.jd.framework.job.utils;    

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.jd.framework.job.utils.digest.EncryptionUtilsTest;
import com.jd.framework.job.utils.env.LocalHostServiceTest;
import com.jd.framework.job.utils.env.TimeServiceTest;
import com.jd.framework.job.utils.json.GsonFactoryTest;
import com.jd.framework.job.utils.json.JobConfigurationGsonTypeAdapterTest;
import com.jd.framework.job.utils.segment.SegmentItemParametersTest;
import com.jd.framework.job.utils.segment.SegmentItemsTest;
import com.jd.framework.job.utils.threadpool.ThreadPoolWrapperTest;

@RunWith(Suite.class)
@SuiteClasses({
        ThreadPoolWrapperTest.class, 
        EncryptionUtilsTest.class, 
        TimeServiceTest.class, 
        LocalHostServiceTest.class, 
        GsonFactoryTest.class, 
        JobConfigurationGsonTypeAdapterTest.class, 
        SegmentItemsTest.class, 
        SegmentItemParametersTest.class
    })
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AllUtilsTests {
	//ALL UTILS TEST
}
  