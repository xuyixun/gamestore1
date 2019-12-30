package com.ijianjian.user.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ijianjian.core.common.constant.ResultType.CommonError;
import com.ijianjian.core.common.object.CommonResult;
import com.ijianjian.user.domain.dto.SubscribeBlackListSearchDTO;
import com.ijianjian.user.domain.po.SubscribeBlackList;
import com.ijianjian.user.domain.repository.SubscribeBlackListRepository;
import com.ijianjian.user.util.DomainFactory;

import io.swagger.annotations.ApiOperation;

@RestController
public class SubscribeBlackListService {
private final SubscribeBlackListRepository unSubscribeListRepository;

public SubscribeBlackListService(SubscribeBlackListRepository unSubscribeListRepository) {
	this.unSubscribeListRepository = unSubscribeListRepository;
}

@ApiOperation("订阅黑名单移除")
@PostMapping("v1/subscribe_blacklist/remove/{userUuid}")
public CommonResult remove(@PathVariable String userUuid) {
	this.unSubscribeListRepository.deleteByUser_id(userUuid);
	return CommonResult.successResult(1);
}

@ApiOperation("查询")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@GetMapping("v1/subscribe_blacklist")
public CommonResult query(SubscribeBlackListSearchDTO dto) {
	dto.init();
	Page<SubscribeBlackList> page = this.unSubscribeListRepository.query(dto);
	if (page == null || page.getTotalElements() == 0) {
		return CommonResult.errorResult(CommonError.list_empty);
	}
	return CommonResult.successResult(new PageImpl<>(DomainFactory._2UnSubscribeListVO_001(page.getContent()), page.getPageable(), page.getTotalElements()));
}
}
