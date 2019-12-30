package com.ijianjian.game.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ijianjian.core.common.constant.ResultType.CommonError;
import com.ijianjian.core.common.object.CommonResult;
import com.ijianjian.core.common.util.HttpUtil;
import com.ijianjian.game.domain.po.Channel;
import com.ijianjian.game.domain.po.ColumnGeneral;
import com.ijianjian.game.domain.po.GameInfo;
import com.ijianjian.game.domain.po.GameInfoDetail;
import com.ijianjian.game.domain.repository.ChannelRepository;
import com.ijianjian.game.domain.repository.GameInfoDetailRepository;
import com.ijianjian.game.domain.vo.ChannelVO_001;
import com.ijianjian.game.domain.dto.ChannelCreateDTO_001;
import com.ijianjian.game.domain.dto.ChannelSearchDTO;
import com.ijianjian.game.util.DomainFactory;
import com.ijianjian.game.util.LocalUser;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "渠道")
@RestController
public class ChannelService implements LocalUser {
private final ObjectMapper mapper = new ObjectMapper();
private final ChannelRepository channelRepository;
private final GameInfoDetailRepository gameInfoDetailRepository;

public ChannelService(ChannelRepository channelRepository, GameInfoDetailRepository gameInfoDetailRepository) {
	this.channelRepository = channelRepository;
	this.gameInfoDetailRepository = gameInfoDetailRepository;
}

@ApiOperation("查询")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@GetMapping("v1/channel")
public CommonResult query(ChannelSearchDTO dto) {
	dto.init();
	Page<Channel> page = this.channelRepository.query(dto);
	if (page == null || page.getTotalElements() == 0) {
		return CommonResult.errorResult(CommonError.list_empty);
	}
	List<ChannelVO_001> list = Lists.newArrayList();
	page.getContent().forEach(channel -> {
		ChannelVO_001 vo = DomainFactory._2VO(channel);
		if (channel.getGameInfo() != null) {
			Optional<GameInfoDetail> gido = this.gameInfoDetailRepository.findByGameInfo_fUuidAndFLanguageNumber(channel.getGameInfo().getFUuid(), channel.getGameInfo().getFLanguageDefault());
			if (gido.isPresent()) {
				vo.setGameInfoName(gido.get().getFName());
			}
		}
		list.add(vo);
	});
	return CommonResult.successResult(new PageImpl<>(list, page.getPageable(), page.getTotalElements()));
}

@ApiOperation("保存")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/channel")
public CommonResult save(ChannelCreateDTO_001 dto) {
	if (Strings.isNullOrEmpty(dto.getName())) {
		return CommonResult.errorResult(CommonError.param_error);
	}
	Channel channel;
	if (Strings.isNullOrEmpty(dto.getUuid())) {
		String maxNumber = this.channelRepository.maxNumber();
		channel = Channel.builder().createUser(localCore()).fNumber(maxNumber == null ? "000001" : String.format("%06d", Long.valueOf(maxNumber) + 1)).build();
	} else {
		Optional<Channel> uOptional = this.channelRepository.findById(dto.getUuid());
		if (!uOptional.isPresent()) {
			return CommonResult.errorResult(CommonError.data_not_exist);
		}
		channel = uOptional.get();
	}
	if (!Strings.isNullOrEmpty(dto.getName())) {
		channel.setFName(dto.getName());
	}
	if (dto.getType() != null) {
		channel.setType(dto.getType());
	}
	if (!Strings.isNullOrEmpty(dto.getCallBackParams())) {
		channel.setFCallBackParams(dto.getCallBackParams());
	}
	String param = "n=" + channel.getFNumber();
	if (!Strings.isNullOrEmpty(dto.getCallBackUrl())) {
		channel.setFCallBackUrl(dto.getCallBackUrl());
		param += "&ncb=true";
	}
	if (!Strings.isNullOrEmpty(dto.getGameInfoUuid())) {
		channel.setGameInfo(GameInfo.builder().fUuid(dto.getGameInfoUuid()).build());
		param += "&gu=" + dto.getGameInfoUuid();
	}
	if (!Strings.isNullOrEmpty(dto.getColumnGeneralUuid())) {
		channel.setColumnGeneral(ColumnGeneral.builder().fUuid(dto.getColumnGeneralUuid()).build());
		param += "&cu=" + dto.getColumnGeneralUuid();
	}
	if (!Strings.isNullOrEmpty(dto.getUrl())) {
		channel.setFUrl(dto.getUrl() + "?" + param);
	}
	if (!Strings.isNullOrEmpty(channel.getFUrl()) && channel.getFUrl().contains("?")) {
		String[] urlPart = channel.getFUrl().split("[?]");
		channel.setFUrlEncrypt(urlPart[0] + "?params=" + new String(Base64.getEncoder().encode(urlPart[1].getBytes())));
	}
	this.channelRepository.save(channel);
	return CommonResult.successResult(channel.getFNumber());
}

@ApiOperation("详情")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@GetMapping("v1/channel/{uuid}")
public CommonResult one(@PathVariable String uuid) {
	Optional<Channel> uOptional = this.channelRepository.findById(uuid);
	if (uOptional.isPresent()) {
		Channel channel = uOptional.get();
		ChannelVO_001 vo = DomainFactory._2VO(channel);
		if (channel.getGameInfo() != null) {
			vo.setGameInfoName(channel.getGameInfo().getFName());
			vo.setGameInfoUuid(channel.getGameInfo().getFUuid());
		}
		if (channel.getColumnGeneral() != null) {
			vo.setColumnGeneralName(channel.getColumnGeneral().getFName());
			vo.setColumnGeneralUuid(channel.getColumnGeneral().getFUuid());
		}
		return CommonResult.successResult(vo);
	}
	return CommonResult.errorResult(CommonError.data_not_exist);
}

@ApiOperation("删除")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/channel/delete/{uuid}")
public CommonResult delete(@PathVariable String uuid) {
	this.channelRepository.delete(uuid, LocalDateTime.now());
	return CommonResult.successResult("1");
}

@Scheduled(fixedRate = 60000 * 10)
public void callBack() {
	List<String[]> list = this.channelRepository.query001();
	for (String[] log : list) {
		Optional<Channel> cO = this.channelRepository.findByFNumber(log[1]);
		if (cO.isPresent()) {
			Channel channel = cO.get();
			String url = channel.getFCallBackUrl();
			String params = channel.getFCallBackParams();
			if (Strings.isNullOrEmpty(params)) {
				HttpUtil.sendGet(url);
			} else {
				Map<String, String> param = Maps.newHashMap();
				try {
					param = mapper.readValue(params, new TypeReference<Map<String, String>>() {
					});
				} catch (JsonParseException e) {
					e.printStackTrace();
				} catch (JsonMappingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				HttpUtil.sendPost(url, param);
			}
			this.channelRepository.updateNeedCallBack(log[0]);
		}
	}
}
}
