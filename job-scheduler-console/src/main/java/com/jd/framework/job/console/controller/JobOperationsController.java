package com.jd.framework.job.console.controller;

import java.util.Collection;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Optional;
import com.jd.framework.job.console.domain.job.ServerInfo;
import com.jd.framework.job.console.service.JobAPIService;


/**
 * 作业触发暂停恢复
 * @author hurong
 *
 */
@RestController
@RequestMapping("job")
public class JobOperationsController {

	@Resource
	private JobAPIService jobAPIService;

	@RequestMapping(value = "trigger", method = RequestMethod.POST)
	public void triggerJob(final ServerInfo jobServer) {
		jobAPIService.getJobOperatorAPI().trigger(Optional.of(jobServer.getJobName()), Optional.of(jobServer.getIp()));
	}

	@RequestMapping(value = "pause", method = RequestMethod.POST)
	public void pauseJob(final ServerInfo jobServer) {
		jobAPIService.getJobOperatorAPI().pause(Optional.of(jobServer.getJobName()), Optional.of(jobServer.getIp()));
	}

	@RequestMapping(value = "resume", method = RequestMethod.POST)
	public void resumeJob(final ServerInfo jobServer) {
		jobAPIService.getJobOperatorAPI().resume(Optional.of(jobServer.getJobName()), Optional.of(jobServer.getIp()));
	}

	@RequestMapping(value = "triggerAll/name", method = RequestMethod.POST)
	public void triggerAllJobsByJobName(final ServerInfo jobServer) {
		jobAPIService.getJobOperatorAPI().trigger(Optional.of(jobServer.getJobName()), Optional.<String>absent());
	}

	@RequestMapping(value = "pauseAll/name", method = RequestMethod.POST)
	public void pauseAllJobsByJobName(final ServerInfo jobServer) {
		jobAPIService.getJobOperatorAPI().pause(Optional.of(jobServer.getJobName()), Optional.<String>absent());
	}

	@RequestMapping(value = "resumeAll/name", method = RequestMethod.POST)
	public void resumeAllJobsByJobName(final ServerInfo jobServer) {
		jobAPIService.getJobOperatorAPI().resume(Optional.of(jobServer.getJobName()), Optional.<String>absent());
	}

	@RequestMapping(value = "triggerAll/ip", method = RequestMethod.POST)
	public void triggerAllJobs(final ServerInfo jobServer) {
		jobAPIService.getJobOperatorAPI().trigger(Optional.<String>absent(), Optional.of(jobServer.getIp()));
	}

	@RequestMapping(value = "pauseAll/ip", method = RequestMethod.POST)
	public void pauseAllJobs(final ServerInfo jobServer) {
		jobAPIService.getJobOperatorAPI().pause(Optional.<String>absent(), Optional.of(jobServer.getIp()));
	}

	@RequestMapping(value = "resumeAll/ip", method = RequestMethod.POST)
	public void resumeAllJobs(final ServerInfo jobServer) {
		jobAPIService.getJobOperatorAPI().resume(Optional.<String>absent(), Optional.of(jobServer.getIp()));
	}

	@RequestMapping(value = "shutdown", method = RequestMethod.POST)
	public void shutdownJob(final ServerInfo jobServer) {
		jobAPIService.getJobOperatorAPI().shutdown(Optional.of(jobServer.getJobName()), Optional.of(jobServer.getIp()));
	}

	@RequestMapping(value = "remove", method = RequestMethod.POST)
	public Collection<String> removeJob(final ServerInfo jobServer) {
		return jobAPIService.getJobOperatorAPI().remove(Optional.of(jobServer.getJobName()),
				Optional.of(jobServer.getIp()));
	}

	@RequestMapping(value = "disable", method = RequestMethod.POST)
	public void disableJob(final ServerInfo jobServer) {
		jobAPIService.getJobOperatorAPI().disable(Optional.of(jobServer.getJobName()), Optional.of(jobServer.getIp()));
	}

	@RequestMapping(value = "enable", method = RequestMethod.POST)
	public void enableJob(final ServerInfo jobServer) {
		jobAPIService.getJobOperatorAPI().enable(Optional.of(jobServer.getJobName()), Optional.of(jobServer.getIp()));
	}
}
