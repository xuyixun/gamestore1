package com.ijianjian.file.util;

import java.util.StringJoiner;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ijianjian.core.domain.user.po.CoreUser;

public interface LocalUser {
default CoreUser localCore() {
	CoreUser userInfo;
	SecurityContext context = SecurityContextHolder.getContext();
	if (context.getAuthentication() != null && !context.getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
		String uuid = context.getAuthentication().getPrincipal().toString();
		if (uuid.equals("000")) {
			userInfo = null;
		} else {
			StringJoiner joiner = new StringJoiner(",");
			context.getAuthentication().getAuthorities().forEach(i -> joiner.add(i.getAuthority()));
			userInfo = CoreUser.builder().fUuid(uuid).fRoles(joiner.toString()).build();
		}
	} else {
		userInfo = null;
	}
	return userInfo;
}

default String getIp(HttpServletRequest request) {
	String ip = request.getHeader("x-forwarded-for");
	if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
		ip = request.getHeader("Proxy-Client-IP");
	}
	if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
		ip = request.getHeader("WL-Proxy-Client-IP");
	}
	if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
		ip = request.getRemoteAddr();
	}
	return ip;
}
}
