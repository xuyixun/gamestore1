package com.ijianjian.channel.service;

import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.ijianjian.channel.domain.dto.ChannelParamCreateDTO;
import com.ijianjian.channel.domain.po.Channel;
import com.ijianjian.channel.domain.po.ChannelParam;
import com.ijianjian.channel.domain.repository.ChannelParamRepository;
import com.ijianjian.channel.util.DomainFactory;
import com.ijianjian.channel.util.LocalUser;
import com.ijianjian.core.common.constant.ResultType.CommonError;
import com.ijianjian.core.common.object.CommonResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "渠道参数")
@RestController
public class ChannelParamService implements LocalUser {
private final ChannelParamRepository r;

public ChannelParamService(ChannelParamRepository r) {
	super();
	this.r = r;
}

@ApiOperation("查询")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@GetMapping("v1/channel_param")
public CommonResult query(String channelUuid) {
	return CommonResult.successResult(DomainFactory._2ChannelParamVO(this.r.findByChannel_fUuid(channelUuid, Sort.by("fParamName"))));
}

@ApiOperation("保存")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/channel_param")
public CommonResult save(ChannelParamCreateDTO dto) {
	if ((Strings.isNullOrEmpty(dto.getUuid()) && Strings.isNullOrEmpty(dto.getChannelUuid())) || Strings.isNullOrEmpty(dto.getParamName()) || Strings.isNullOrEmpty(dto.getParamValue())) {
		return CommonResult.errorResult(CommonError.param_error);
	}
	ChannelParam channel;
	if (Strings.isNullOrEmpty(dto.getUuid())) {
		channel = ChannelParam.builder().createUser(localCore()).channel(Channel.builder().fUuid(dto.getChannelUuid()).build()).build();
	} else {
		Optional<ChannelParam> uOptional = this.r.findById(dto.getUuid());
		if (!uOptional.isPresent()) {
			return CommonResult.errorResult(CommonError.data_not_exist);
		}
		channel = uOptional.get();
	}
	channel.setFParamName(dto.getParamName());
	channel.setFParamValue(dto.getParamValue());
	this.r.save(channel);
	return CommonResult.successResult(1);
}

@ApiOperation("删除")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/channel_param/delete/{uuid}")
public CommonResult delete(@PathVariable String uuid) {
	this.r.deleteById(uuid);
	return CommonResult.successResult("1");
}
}
