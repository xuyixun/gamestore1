package com.ijianjian.channel.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.ijianjian.channel.domain.dto.ChannelCreateDTO;
import com.ijianjian.channel.domain.dto.ChannelSearchDTO;
import com.ijianjian.channel.domain.po.Channel;
import com.ijianjian.channel.domain.repository.ChannelParamRepository;
import com.ijianjian.channel.domain.repository.ChannelRepository;
import com.ijianjian.channel.domain.vo.ChannelVO;
import com.ijianjian.channel.util.ConfigHW;
import com.ijianjian.channel.util.DomainFactory;
import com.ijianjian.channel.util.LocalUser;
import com.ijianjian.core.common.constant.ResultType.CommonError;
import com.ijianjian.core.common.object.CommonResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api(tags = "渠道")
@RestController
public class ChannelService implements LocalUser {
private final ChannelRepository r;
private final ChannelParamRepository cpr;

public ChannelService(ChannelRepository r, ChannelParamRepository cpr) {
	super();
	this.r = r;
	this.cpr = cpr;
}

@ApiOperation("查询")

@ApiImplicitParams({ @ApiImplicitParam(name = "queryOrder", value = "fNumber,fName", paramType = "query", dataType = "String"), })
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@GetMapping("v1/channel")
public CommonResult query(ChannelSearchDTO dto) {
	dto.init();
	if (Strings.isNullOrEmpty(dto.getQueryOrder())) {
		dto.setQueryOrder("fNumber_asc");
	}
	Page<Channel> page = this.r.query(dto);
	if (page == null || page.getTotalElements() == 0) {
		return CommonResult.errorResult(CommonError.list_empty);
	}
	return CommonResult.successResult(new PageImpl<>(DomainFactory._2ChannelVO(page.getContent()), page.getPageable(), page.getTotalElements()));
}

@ApiOperation("保存")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/channel")
public CommonResult save(ChannelCreateDTO dto) {
	if (Strings.isNullOrEmpty(dto.getName())) {
		return CommonResult.errorResult(CommonError.param_error);
	}
	Channel channel;
	if (Strings.isNullOrEmpty(dto.getUuid())) {
		Integer maxNumber = this.r.maxNumber();
		channel = Channel.builder().createUser(localCore()).fNumber(maxNumber == null ? 1 : (maxNumber + 1)).build();
	} else {
		Optional<Channel> uOptional = this.r.findById(dto.getUuid());
		if (!uOptional.isPresent()) {
			return CommonResult.errorResult(CommonError.data_not_exist);
		}
		channel = uOptional.get();
	}
	if (!Strings.isNullOrEmpty(dto.getName())) {
		channel.setFName(dto.getName());
	}
	channel.setFScheme(Strings.isNullOrEmpty(dto.getScheme()) ? ConfigHW.Channel.scheme : dto.getScheme());
	channel.setFHost(Strings.isNullOrEmpty(dto.getHost()) ? ConfigHW.Channel.host : dto.getHost());
	channel.setFUrl(Strings.isNullOrEmpty(dto.getUrl()) ? ConfigHW.Channel.url : dto.getUrl());
	channel.setFClickId(Strings.isNullOrEmpty(dto.getClickId()) ? ConfigHW.Channel.clickId : dto.getClickId());

	if (!Strings.isNullOrEmpty(dto.getChannelUrl())) {
		channel.setFChannelUrl(dto.getChannelUrl().replace("{number}", String.valueOf(channel.getFNumber())));
	}
	if (!Strings.isNullOrEmpty(dto.getHwUrl())) {
		channel.setFHwUrl(dto.getHwUrl().replace("{number}", String.valueOf(channel.getFNumber())));
	}
	if (!Strings.isNullOrEmpty(dto.getCallBackUrl())) {
		channel.setFCallBackUrl(dto.getCallBackUrl());
	}
	channel.setFThreshold(dto.getThreshold() == null ? -1 : dto.getThreshold());

	this.r.save(channel);
	return CommonResult.successResult(channel.getFNumber());
}

@ApiOperation("详情")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@GetMapping("v1/channel/{uuid}")
public CommonResult one(@PathVariable String uuid) {
	Optional<Channel> uOptional = this.r.findById(uuid);
	if (uOptional.isPresent()) {
		Channel channel = uOptional.get();
		ChannelVO vo = DomainFactory._2VO(channel);
		vo.setParams(DomainFactory._2ChannelParamVO(this.cpr.findByChannel_fUuid(channel.getFUuid(), Sort.by("fParamName"))));
		return CommonResult.successResult(vo);
	}
	return CommonResult.errorResult(CommonError.data_not_exist);
}

@ApiOperation("删除")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/channel/delete/{uuid}")
public CommonResult delete(@PathVariable String uuid) {
	this.r.delete(uuid, LocalDateTime.now());
	return CommonResult.successResult("1");
}
}
