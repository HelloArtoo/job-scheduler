package com.jd.framework.job.regcenter.api;

/**
 * 选举候选人. 保证{@link #startLeadership()}与{@link #stopLeadership()}方法在同一个线程内交替运行,
 * 且不会出现并发执行的情况.
 */
public interface ElectionCandidate {

	/**
	 * 开始领导状态.
	 * 
	 * @throws Exception
	 */
	void startLeadership() throws Exception;

	/**
	 * 终止领导状态. 实现该方法时不应该抛出任何异常
	 */
	void stopLeadership();
}
