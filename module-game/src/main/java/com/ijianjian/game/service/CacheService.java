package com.ijianjian.game.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ijianjian.core.common.object.CommonResult;

@RestController
public class CacheService {
@GetMapping("v1/cache/home")
@CacheEvict(cacheNames = "home", allEntries = true)
public CommonResult homePage() {
	return CommonResult.successResult(1);
}

@GetMapping("v1/cache/column_app")
@CacheEvict(cacheNames = "column_app", allEntries = true)
public CommonResult columnApp() {
	return CommonResult.successResult(1);
}

@GetMapping("v1/cache/column_general")
@CacheEvict(cacheNames = "column_general", allEntries = true)
public CommonResult columnGeneral() {
	return CommonResult.successResult(1);
}
}
