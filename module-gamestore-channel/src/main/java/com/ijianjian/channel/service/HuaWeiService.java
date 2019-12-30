package com.ijianjian.channel.service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.utils.URIBuilder;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ijianjian.channel.domain.dto.HuaWeiChannelLogSearchDTO;
import com.ijianjian.channel.domain.po.Channel;
import com.ijianjian.channel.domain.po.ChannelCallbackLog;
import com.ijianjian.channel.domain.po.ClickLog;
import com.ijianjian.channel.domain.po.HuaWeiChannelLog;
import com.ijianjian.channel.domain.repository.ChannelCallbackLogRepository;
import com.ijianjian.channel.domain.repository.ChannelRepository;
import com.ijianjian.channel.domain.repository.ClickLogRepository;
import com.ijianjian.channel.domain.repository.HuaWeiChannelLogRepository;
import com.ijianjian.channel.domain.vo.HuaWeiChannelLogVO;
import com.ijianjian.core.common.constant.ResultType.CommonError;
import com.ijianjian.core.common.object.CommonResult;
import com.ijianjian.core.common.util.HttpUtil;
import com.ijianjian.channel.util.DomainFactory;
import com.ijianjian.channel.util.LocalUser;
import com.ijianjian.channel.util.RabbitObject001;

@RestController
public class HuaWeiService implements LocalUser {
private final ObjectMapper om = new ObjectMapper();
private final HuaWeiChannelLogRepository hwclr;
private final ChannelRepository r;
private final ChannelCallbackLogRepository rclr;
private final ClickLogRepository clickLogRepository;
private final AmqpTemplate rabbitTemplate;

public HuaWeiService(HuaWeiChannelLogRepository hwclr, ChannelRepository r, ChannelCallbackLogRepository rclr, ClickLogRepository clickLogRepository, AmqpTemplate rabbitTemplate) {
	super();
	this.hwclr = hwclr;
	this.r = r;
	this.rclr = rclr;
	this.clickLogRepository = clickLogRepository;
	this.rabbitTemplate = rabbitTemplate;
}

@GetMapping("v1/hw")
public CommonResult query(HuaWeiChannelLogSearchDTO dto) {
	dto.init();
	Page<HuaWeiChannelLog> page = this.hwclr.query(dto);
	if (page == null || page.getTotalElements() == 0) {
		return CommonResult.errorResult(CommonError.list_empty);
	}
	List<HuaWeiChannelLogVO> list = Lists.newArrayList();

	for (HuaWeiChannelLog hwcl : page.getContent()) {
		HuaWeiChannelLogVO vo = DomainFactory._2VO(hwcl);
		Optional<Channel> co = this.r.findByFDeletedFalseAndFNumber(hwcl.getChannelNumber());
		if (co.isPresent()) {
			vo.setChannelName(co.get().getFName());
		}
		list.add(vo);
	}
	return CommonResult.successResult(new PageImpl<>(list, page.getPageable(), page.getTotalElements()));
}

@GetMapping("r/{number}")
public String redirect(@PathVariable Integer number, HttpServletResponse response, HttpServletRequest request) {
	String clickId = request.getParameter("click_id");
	if (Strings.isNullOrEmpty(clickId)) {
		clickId = "ijianjian___" + number + "_" + String.valueOf(System.currentTimeMillis());
	}
	String hwUrl = this.r.hwUrl(number);
	if (!Strings.isNullOrEmpty(hwUrl)) {
		try {
			hwUrl = hwUrl.replace("{@click_id}", clickId);
			this.clickLogRepository.save(ClickLog.builder().channelNumber(number).ip(this.getIp(request)).clickId(clickId).params(request.getParameterMap().size() == 0 ? null : om.writeValueAsString(request.getParameterMap())).url(hwUrl).build());
			Integer threshold = this.r.queryThreshold(number);
			if (threshold == -1 || threshold != 0) {
				response.sendRedirect(hwUrl);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	return "error";
}

@RequestMapping("hw/channel_notify")
public String channelNotify(HttpServletRequest request) {
	Map<String, String[]> params = request.getParameterMap();
	try {
		String clickId = (params.containsKey("click_id") && !Strings.isNullOrEmpty(params.get("click_id")[0])) ? params.get("click_id")[0] : null;
		String channelNumber = params.containsKey("channelNumber") ? params.get("channelNumber")[0] : null;

		HuaWeiChannelLog hwcl = HuaWeiChannelLog.builder().ip(this.getIp(request)).params(om.writeValueAsString(params)).channelNumber(Integer.valueOf(channelNumber)).clickId(clickId).callbackType("wait").build();
		this.hwclr.save(hwcl);

		if (!Strings.isNullOrEmpty(clickId) && !Strings.isNullOrEmpty(channelNumber)) {
			this.sendMessage(hwcl);
		}
	} catch (JsonProcessingException e) {
		e.printStackTrace();
	}
	return "success";
}

//@Scheduled(fixedRate = 60000 * 1)
/*
 * public void callback() { LocalDateTime now = LocalDateTime.now(); now = now.minusMinutes(3);
 * 
 * try { String addr = InetAddress.getLocalHost().getHostAddress(); System.out.println("@Scheduled callback" + addr.equals(ConfigHW.Channel.scheduledIp)); if (!addr.equals(ConfigHW.Channel.scheduledIp)) { return; } } catch (UnknownHostException e) { e.printStackTrace(); } List<HuaWeiChannelLog>
 * hwlList = this.hwclr.findByCallbackFalseAndTimeBetween(now.withSecond(0), now.withSecond(59)); for (HuaWeiChannelLog hwl : hwlList) { if (hwl.isCallback()) { continue; } this.callback(hwl.getFUuid(), hwl.getChannelNumber(), hwl.getClickId()); } }
 */

/*
 * @GetMapping("v1/callback/manual/{uuid}") public CommonResult manual(@PathVariable String uuid) { Optional<HuaWeiChannelLog> hclO = this.hwclr.findById(uuid); if (hclO.isPresent()) { this.callback(hclO.get().getFUuid(), hclO.get().getChannelNumber(), hclO.get().getClickId()); } return
 * CommonResult.successResult(1); }
 */

/*
 * private void callback(String uuid, String channelNumber, String clickId) { Optional<Channel> co = this.r.findByFDeletedFalseAndFNumber(channelNumber); Optional<ClickLog> clo = this.clickLogRepository.findTopByClickId(clickId); if (co.isPresent() && clo.isPresent()) { String callbackUrl =
 * co.get().getFCallBackUrl();
 * 
 * if (!Strings.isNullOrEmpty(callbackUrl)) { callbackUrl = callbackUrl.replace("{click_id}", clickId);
 * 
 * // 组装参数 String paramString = clo.get().getParams(); if (!Strings.isNullOrEmpty(paramString)) { try { URIBuilder urlb = new URIBuilder(callbackUrl);
 * 
 * @SuppressWarnings("unchecked") Map<String, List<String>> params = om.readValue(paramString, Maps.<String, List<String>>newHashMap().getClass()); params.forEach((k, v) -> { urlb.addParameter(k, v.get(0)); }); System.out.println(urlb.build().toString()); callbackUrl = urlb.build().toString(); }
 * catch (URISyntaxException e) { e.printStackTrace(); } catch (JsonParseException e) { e.printStackTrace(); } catch (JsonMappingException e) { e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); } }
 * 
 * String result = HttpUtil.sendGet(callbackUrl);
 * 
 * this.rclr.save(ChannelCallbackLog.builder().channelNumber(channelNumber).clickId(clickId).hwLogId(uuid).url(callbackUrl).result(result).build()); if (result.contains("200___")) { this.hwclr.callback(uuid); } }
 * 
 * } }
 */

//发送消息
private void sendMessage(HuaWeiChannelLog hwcl) {
	RabbitObject001 mo = new RabbitObject001();
	mo.setUuid(hwcl.getFUuid());
	mo.setChannelNumber(hwcl.getChannelNumber());
	mo.setClickId(hwcl.getClickId());

	this.rabbitTemplate.convertAndSend("rbmq_channel_callback", mo);
}

//接收消息
@RabbitListener(queues = "rbmq_channel_callback")
public void receiverMessage(RabbitObject001 mo) {
	try {
		String addr = InetAddress.getLocalHost().getHostAddress();
		System.out.println("rbmq__" + addr + "__" + mo.getUuid());
	} catch (UnknownHostException e1) {
		e1.printStackTrace();
	}

	String day = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
	Integer dayCount = this.hwclr.quereyDayCount(mo.getChannelNumber(), day);
	Integer threshold = this.r.queryThreshold(mo.getChannelNumber());
	if (threshold == null || (threshold != -1 && dayCount >= threshold)) {
		this.hwclr.callbackType(mo.getUuid(), "out_threshold");
		return;
	}

	String callbackUrl = this.r.callBackUrl(mo.getChannelNumber());
	if (Strings.isNullOrEmpty(callbackUrl)) {
		this.hwclr.callbackType(mo.getUuid(), "url_null");
		return;
	}

	if (this.rclr.existsByChannelNumberAndClickId(mo.getChannelNumber(), mo.getClickId())) {
		this.hwclr.callbackType(mo.getUuid(), "exists_data");
		return;
	}

	Optional<ClickLog> clo = this.clickLogRepository.findTopByClickId(mo.getClickId());
	if (!clo.isPresent()) {
		this.hwclr.callbackType(mo.getUuid(), "click_null");
		return;
	}

	callbackUrl = callbackUrl.replace("{click_id}", mo.getClickId());

	// 组装参数
	String paramString = clo.get().getParams();
	if (!Strings.isNullOrEmpty(paramString)) {
		try {
			URIBuilder urlb = new URIBuilder(callbackUrl);
			@SuppressWarnings("unchecked")
			Map<String, List<String>> params = om.readValue(paramString, Maps.<String, List<String>>newHashMap().getClass());
			params.forEach((k, v) -> {
				urlb.addParameter(k, v.get(0));
			});
			callbackUrl = urlb.build().toString();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	String result = HttpUtil.sendGet(callbackUrl);

	this.rclr.save(ChannelCallbackLog.builder().channelNumber(mo.getChannelNumber()).clickId(mo.getClickId()).hwLogId(mo.getUuid()).url(callbackUrl).result(result).build());
	if (result.contains("200___")) {
		this.hwclr.callbackType(mo.getUuid(), "send_success");
	} else {
		this.hwclr.callbackType(mo.getUuid(), "send_error");
	}
}

@GetMapping("test_xyx/ddddd")
public void sss() {

}
}
