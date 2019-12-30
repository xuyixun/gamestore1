package com.ijianjian.core.security.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;
import com.ijianjian.core.common.constant.Config;
import com.ijianjian.core.domain.log.po.LogRequest;
import com.ijianjian.core.domain.log.repository.LogRequestRepository;
import com.ijianjian.core.domain.user.po.CoreUser;
import com.ijianjian.core.domain.user.repository.CoreUserRepository;

@Component
public class JwtScheduledService {
private final CoreUserRepository coreUserRepository;
private final LogRequestRepository logRequestRepository;
private final JwtCacheService jwtCacheService;

public JwtScheduledService(CoreUserRepository coreUserRepository, LogRequestRepository logRequestRepository, JwtCacheService jwtCacheService) {
	super();
	this.coreUserRepository = coreUserRepository;
	this.logRequestRepository = logRequestRepository;
	this.jwtCacheService = jwtCacheService;
}

@Scheduled(fixedRate = 60000)
public void checkOnline() {
	List<CoreUser> list = this.coreUserRepository.findByfOnlineTrue();
	for (CoreUser user : list) {
		Set<String> paths = Sets.newHashSet();
		paths.add("");
		if (Duration.between(user.getFLastLoginTime(), LocalDateTime.now()).toMinutes() <= Config.SystemParam.adminLiveMinute) {
			continue;
		}
		Optional<LogRequest> lo = this.logRequestRepository.findTopByfUserUuidAndFPathNotIn(user.getFUuid(), paths, Sort.by(Direction.DESC, "fTime"));
		if (lo.isPresent()) {
			if (Duration.between(lo.get().getFTime(), LocalDateTime.now()).toMinutes() <= Config.SystemParam.adminLiveMinute) {
				continue;
			}
		}
		this.jwtCacheService.evictJwtCache(user.getFUuid());
		this.coreUserRepository.updateOnline(user.getFUuid(), false);
	}
}
}
