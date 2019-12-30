package com.ijianjian.channel.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.ijianjian.channel.domain.dto.ChannelCallbackLogSearchDTO;
import com.ijianjian.channel.domain.po.Channel;
import com.ijianjian.channel.domain.po.ChannelCallbackLog;
import com.ijianjian.channel.domain.repository.ChannelCallbackLogRepository;
import com.ijianjian.channel.domain.repository.ChannelRepository;
import com.ijianjian.channel.domain.vo.ChannelCallbackLogVO;
import com.ijianjian.channel.util.DomainFactory;
import com.ijianjian.core.common.constant.ResultType.CommonError;
import com.ijianjian.core.common.object.CommonResult;

@RestController
public class ChannelCallbackLogService {
private final ChannelCallbackLogRepository rclr;
private final ChannelRepository r;

public ChannelCallbackLogService(ChannelCallbackLogRepository rclr, ChannelRepository r) {
	super();
	this.rclr = rclr;
	this.r = r;
}

@GetMapping("v1/ccbl")
public CommonResult query(ChannelCallbackLogSearchDTO dto) {
	dto.init();
	Page<ChannelCallbackLog> page = this.rclr.query(dto);
	if (page == null || page.getTotalElements() == 0) {
		return CommonResult.errorResult(CommonError.list_empty);
	}
	List<ChannelCallbackLogVO> list = Lists.newArrayList();

	for (ChannelCallbackLog hwcl : page.getContent()) {
		ChannelCallbackLogVO vo = DomainFactory._2VO(hwcl);
		Optional<Channel> co = this.r.findByFDeletedFalseAndFNumber(hwcl.getChannelNumber());
		if (co.isPresent()) {
			vo.setChannelName(co.get().getFName());
		}
		list.add(vo);
	}
	return CommonResult.successResult(new PageImpl<>(list, page.getPageable(), page.getTotalElements()));
}
}
