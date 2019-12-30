package com.ijianjian.game.service;

import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.ijianjian.game.domain.po.GameStoreAppLog;
import com.ijianjian.game.domain.repository.GameStoreAppLogRepository;
import com.ijianjian.game.util.FieldConstant.GameStoreAppLogType;
import com.ijianjian.game.util.LocalUser;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Controller
@Api(tags = "游戏商城应用")
public class GameStoreAppDownloadService implements LocalUser {
private final ObjectMapper mapper = new ObjectMapper();
private final GameStoreAppLogRepository gameStoreAppLogRepository;

public GameStoreAppDownloadService(GameStoreAppLogRepository gameStoreAppLogRepository) {
	this.gameStoreAppLogRepository = gameStoreAppLogRepository;
}

@ApiOperation("下载")
@GetMapping("v1/game_store_app/download")
public void download(String filePath, String fileName, HttpServletRequest request, HttpServletResponse response, String version, String phone, String system, String phoneModel, String channelNumber) {
	this.gameStoreAppLog(request, phone, GameStoreAppLogType.download, system, phoneModel, version);
	DownloadService.download(filePath, fileName, request, response);
}

private void gameStoreAppLog(HttpServletRequest request, String phone, GameStoreAppLogType type, String system, String phoneModel, String version) {
	Map<String, String> headerMap = Maps.newHashMap();
	Enumeration<String> headers = request.getHeaderNames();

	while (headers.hasMoreElements()) {
		String header = headers.nextElement();
		headerMap.put(header, request.getHeader(header));
	}
	try {
		this.gameStoreAppLogRepository.save(GameStoreAppLog.builder().fIp(this.getIp(request)).fVersion(version).fHeader(mapper.writeValueAsString(headerMap)).fPhone(phone).fSystem(system).fPhoneModel(phoneModel).type(type).build());
	} catch (JsonProcessingException e) {
		e.printStackTrace();
	}
}
}
