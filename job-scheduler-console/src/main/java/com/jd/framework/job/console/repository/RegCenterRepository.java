package com.jd.framework.job.console.repository;

import com.jd.framework.job.regcenter.api.CoordinatorRegistryCenter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RegCenterRepository {

	public static volatile CoordinatorRegistryCenter INSTANCE;

}
