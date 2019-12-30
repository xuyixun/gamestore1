package com.ijianjian.user.util;

import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.ijianjian.user.domain.po.SubscribeBlackList;
import com.ijianjian.user.domain.po.UserInfo;
import com.ijianjian.user.domain.vo.SubscribeBlackListVO_001;
import com.ijianjian.user.domain.vo.UserInfoVO_001;

public class DomainFactory {
public static UserInfoVO_001 _2VO(UserInfo s) {
	return s == null ? UserInfoVO_001.builder().build()
	  : UserInfoVO_001.builder().uuid(s.getId()).type(s.getType()).name(s.getFName()).nickName(s.getFNickeName()).phone(s.getFPhone()).enable(s.getCore().isFEnable())
	    .lastLoginTime(s.getCore().getFLastLoginTime() == null ? null : s.getCore().getFLastLoginTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()).build();
}

public static List<UserInfoVO_001> _2UserInfoVO_001(List<UserInfo> source) {
	return (source == null || source.isEmpty()) ? Collections.emptyList() : source.stream().map(DomainFactory::_2VO).collect(Collectors.toList());
}

public static SubscribeBlackListVO_001 _2VO(SubscribeBlackList s) {
	return s == null ? SubscribeBlackListVO_001.builder().build() : SubscribeBlackListVO_001.builder().userUuid(s.getUser().getId()).time(s.getFTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()).userPhone(s.getUser().getFPhone()).build();
}

public static List<SubscribeBlackListVO_001> _2UnSubscribeListVO_001(List<SubscribeBlackList> source) {
	return (source == null || source.isEmpty()) ? Collections.emptyList() : source.stream().map(DomainFactory::_2VO).collect(Collectors.toList());
}
}
