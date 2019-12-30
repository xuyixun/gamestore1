package com.ijianjian.core.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.ijianjian.core.common.constant.Config;
import com.ijianjian.core.common.util.IpUtil;
import com.ijianjian.core.domain.log.po.LogRequest;
import com.ijianjian.core.domain.log.repository.LogRequestRepository;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.Map;

public class LogRequestFilter extends BasicAuthenticationFilter {
private final LogRequestRepository logRequestRepository;

public LogRequestFilter(AuthenticationManager authenticationManager, LogRequestRepository logRequestRepository) {
	super(authenticationManager);
	this.logRequestRepository = logRequestRepository;
}

@Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
	if (Config.Log.enableRequest) {
		String userUuid = null;
		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			userUuid = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
		} else {
			doFilter(request, response, chain);
			return;
		}
		// Map<String, String> formMap = request.getParts().stream().collect(Collectors.toMap(Part::getName, Part::getName));

		ObjectMapper mapper = new ObjectMapper();
		LogRequest logRequest = null;
		try {
			Map<String, String> headerMap = Maps.newHashMap();
			Enumeration<String> headers = request.getHeaderNames();
			while (headers.hasMoreElements()) {
				String header = headers.nextElement();
				headerMap.put(header, request.getHeader(header));
			}

			logRequest = LogRequest.builder().fClientIp(this.getIp(request)).fMethod(request.getMethod()).fParam(mapper.writeValueAsString(request.getParameterMap())).fHeader(mapper.writeValueAsString(headerMap)).fPath(request.getServletPath()).fServerIp(IpUtil.getLocalIpByNetcard())
			  .fStatus(response.getStatus()).fTime(LocalDateTime.now()).fUserUuid(userUuid).build();
			// logRequest.setFFormParam(mapper.writeValueAsString(formMap));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (logRequest != null) {
			this.save(logRequest);
		}
	}
	doFilter(request, response, chain);
}

@Transactional
public void save(LogRequest logRequest) {
	this.logRequestRepository.save(logRequest);
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
