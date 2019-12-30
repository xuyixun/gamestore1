package com.ijianjian.channel.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.ijianjian.channel.domain.dto.ClickLogSearchDTO;
import com.ijianjian.channel.domain.po.Channel;
import com.ijianjian.channel.domain.po.ClickLog;
import com.ijianjian.channel.domain.repository.ChannelRepository;
import com.ijianjian.channel.domain.repository.ClickLogRepository;
import com.ijianjian.channel.domain.vo.ClickLogVO;
import com.ijianjian.channel.util.DomainFactory;
import com.ijianjian.core.common.constant.ResultType.CommonError;
import com.ijianjian.core.common.object.CommonResult;

@RestController
public class ClickLogService {
private final ClickLogRepository clickLogRepository;
private final ChannelRepository r;

public ClickLogService(ClickLogRepository clickLogRepository, ChannelRepository r) {
	super();
	this.clickLogRepository = clickLogRepository;
	this.r = r;
}

@GetMapping("v1/click_log")
public CommonResult query(ClickLogSearchDTO dto) {
	dto.init();
	Page<ClickLog> page = this.clickLogRepository.query(dto);
	if (page == null || page.getTotalElements() == 0) {
		return CommonResult.errorResult(CommonError.list_empty);
	}
	List<ClickLogVO> list = Lists.newArrayList();

	for (ClickLog hwcl : page.getContent()) {
		ClickLogVO vo = DomainFactory._2VO(hwcl);
		Optional<Channel> co = this.r.findByFDeletedFalseAndFNumber(hwcl.getChannelNumber());
		if (co.isPresent()) {
			vo.setChannelName(co.get().getFName());
		}
		list.add(vo);
	}
	return CommonResult.successResult(new PageImpl<>(list, page.getPageable(), page.getTotalElements()));
}
}
