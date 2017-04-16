/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.core.api.strategy;

import java.util.List;
import java.util.Map;

/**
 * 
 * 分段策略
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-9
 */
public interface JobSegmentStrategy {
	/**
	 * 进行作业分段.
	 * 
	 * @param serversList
	 *            所有参与分段的服务器列表
	 * @param option
	 *            作业分段策略选项
	 * @return 分配分段的服务器IP和分段集合的映射
	 */
	Map<String, List<Integer>> segment(List<String> serversList, JobSegmentStrategyOption option);
}
