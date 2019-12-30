package com.ijianjian.channel.util;

import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.ijianjian.channel.domain.po.Channel;
import com.ijianjian.channel.domain.po.ChannelCallbackLog;
import com.ijianjian.channel.domain.po.ChannelParam;
import com.ijianjian.channel.domain.po.ClickLog;
import com.ijianjian.channel.domain.po.HuaWeiChannelLog;
import com.ijianjian.channel.domain.vo.ChannelCallbackLogVO;
import com.ijianjian.channel.domain.vo.ChannelParamVO;
import com.ijianjian.channel.domain.vo.ChannelVO;
import com.ijianjian.channel.domain.vo.ClickLogVO;
import com.ijianjian.channel.domain.vo.HuaWeiChannelLogVO;
import com.ijianjian.channel.domain.vo.UserVO;
import com.ijianjian.core.domain.user.po.CoreUser;

public class DomainFactory {
public static UserVO _2VO(CoreUser s) {
	return s == null ? UserVO.builder().build()
	  : UserVO.builder().uuid(s.getFUuid()).name(s.getFUserName()).createTime(s.getFCreateTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()).lastLoginTime(s.getFLastLoginTime() == null ? null : s.getFLastLoginTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()).build();
}

public static List<UserVO> _2UserVO(List<CoreUser> source) {
	return (source == null || source.isEmpty()) ? Collections.emptyList() : source.stream().map(DomainFactory::_2VO).collect(Collectors.toList());
}

public static ChannelVO _2VO(Channel s) {
	return s == null ? ChannelVO.builder().build()
	  : ChannelVO.builder().scheme(s.getFScheme()).host(s.getFHost()).url(s.getFUrl()).clickId(s.getFClickId()).uuid(s.getFUuid()).number(s.getFNumber()).name(s.getFName()).channelUrl(s.getFChannelUrl()).hwUrl(s.getFHwUrl()).callBackUrl(s.getFCallBackUrl())
	    .createTime(s.getFCreateTime() == null ? 0 : s.getFCreateTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()).threshold(s.getFThreshold()).build();
}

public static List<ChannelVO> _2ChannelVO(List<Channel> source) {
	return (source == null || source.isEmpty()) ? Collections.emptyList() : source.stream().map(DomainFactory::_2VO).collect(Collectors.toList());
}

public static ChannelParamVO _2VO(ChannelParam s) {
	return s == null ? ChannelParamVO.builder().build() : ChannelParamVO.builder().uuid(s.getFUuid()).paramName(s.getFParamName()).paramValue(s.getFParamValue()).createTime(s.getFCreateTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()).build();
}

public static List<ChannelParamVO> _2ChannelParamVO(List<ChannelParam> source) {
	return (source == null || source.isEmpty()) ? Collections.emptyList() : source.stream().map(DomainFactory::_2VO).collect(Collectors.toList());
}

public static HuaWeiChannelLogVO _2VO(HuaWeiChannelLog s) {
	return s == null ? HuaWeiChannelLogVO.builder().build() : HuaWeiChannelLogVO.builder().channelNumber(s.getChannelNumber()).ip(s.getIp()).param(s.getParams()).time(s.getTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()).build();
}

public static ChannelCallbackLogVO _2VO(ChannelCallbackLog s) {
	return s == null ? ChannelCallbackLogVO.builder().build() : ChannelCallbackLogVO.builder().channelNumber(s.getChannelNumber()).url(s.getUrl()).result(s.getResult()).time(s.getTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()).build();
}

public static ClickLogVO _2VO(ClickLog s) {
	return s == null ? ClickLogVO.builder().build() : ClickLogVO.builder().channelNumber(s.getChannelNumber()).url(s.getUrl()).ip(s.getIp()).params(s.getParams()).time(s.getTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()).build();
}
}
