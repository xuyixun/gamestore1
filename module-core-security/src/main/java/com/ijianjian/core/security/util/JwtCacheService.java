package com.ijianjian.core.security.util;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class JwtCacheService {
@CachePut(cacheNames = "jwt", key = "#p0")
public String putJwtCache(String uuid, String token) {
	return token;
}

@Cacheable(cacheNames = "jwt", key = "#p0")
public String getJwtCache(String uuid) {
	return null;
}

@CacheEvict(cacheNames = "jwt", key = "#p0")
public void evictJwtCache(String uuid) {
}
}
