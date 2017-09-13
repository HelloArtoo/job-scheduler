package com.jd.framework.job.console.domain.job;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public final class RegistryInfo {

	private String namespace;
	private String servers;
	private String digest;

}
