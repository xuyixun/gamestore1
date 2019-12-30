package com.ijianjian.core.security.authorization;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.ijianjian.core.common.constant.Config.JWTConfig;
import com.ijianjian.core.common.object.CommonResult;
import com.ijianjian.core.common.util.IpUtil;
import com.ijianjian.core.domain.log.po.LogLogin;
import com.ijianjian.core.domain.log.repository.LogLoginRespository;
import com.ijianjian.core.domain.user.repository.CoreUserRepository;
import com.ijianjian.core.security.util.JWTTokenUtil;
import com.ijianjian.core.security.util.JwtCacheService;

import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;

public class AuthenticationJwtHandlerSuccess implements AuthenticationSuccessHandler {
private final CoreUserRepository coreUserRepository;
private final LogLoginRespository loginRespository;
private final JwtCacheService jwtCacheService;

public AuthenticationJwtHandlerSuccess(CoreUserRepository coreUserRepository, LogLoginRespository loginRespository, JwtCacheService jwtCacheService) {
	super();
	this.coreUserRepository = coreUserRepository;
	this.loginRespository = loginRespository;
	this.jwtCacheService = jwtCacheService;
}

@Override
public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
	ObjectMapper mapper = new ObjectMapper();
	mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

	response.setHeader("content-type", "application/json;charset=UTF-8");
	response.setStatus(HttpStatus.OK.value());
	UserDetailsImpl userDetails = null;
	if (authentication.getPrincipal() instanceof UserDetailsImpl) {
		userDetails = (UserDetailsImpl) authentication.getPrincipal();
	} else if (authentication.getPrincipal() instanceof User) {
		User ss = (User) authentication.getPrincipal();
		userDetails = new UserDetailsImpl(ss.getUsername(), "", ss.getAuthorities(), "000");
	}

	LocalDateTime time = LocalDateTime.now();
	String token = JWTTokenUtil.tokenGenerate(userDetails, time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

	mapper.writeValue(response.getWriter(), CommonResult.successResult(new HashMap<String, String>() {
	private static final long serialVersionUID = 2309439594033466216L;

	{
		put("token", JWTConfig.token_prefix + token);
	}
	}));

	this.jwtCacheService.putJwtCache(userDetails.getUserUuid(), token);
	
	if (request.getAttribute("login_type") != null && request.getAttribute("login_type").toString().equals("username_password")) {
		this.coreUserRepository.updateOnline(userDetails.getUserUuid(), true);
	}

	this.saveLog(userDetails, time, token, request);
}

@Async
public void saveLog(UserDetailsImpl userDetails, LocalDateTime time, String token, HttpServletRequest request) {
	this.coreUserRepository.updateLastLoginTime(userDetails.getUserUuid(), time);
	if (!Strings.isNullOrEmpty(request.getParameter("sn"))) {
		this.coreUserRepository.updateSN(userDetails.getUserUuid(), request.getParameter("sn"));
	}
	this.loginRespository.save(LogLogin.builder().fClientIp(this.getIp(request)).fServerIp(IpUtil.getLocalIpByNetcard()).fToken(token).fUserUuid(userDetails.getUserUuid()).fType(request.getHeader("login_type")).build());
}

private String getIp(HttpServletRequest request) {
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
