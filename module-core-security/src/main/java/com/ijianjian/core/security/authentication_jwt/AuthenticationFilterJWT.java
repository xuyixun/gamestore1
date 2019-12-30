package com.ijianjian.core.security.authentication_jwt;

import io.jsonwebtoken.Claims;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.ijianjian.core.common.constant.Config.JWTConfig;
import com.ijianjian.core.security.util.JWTTokenUtil;
import com.ijianjian.core.security.util.JwtCacheService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AuthenticationFilterJWT extends BasicAuthenticationFilter {
private final JwtCacheService jwtCacheService;

public AuthenticationFilterJWT(AuthenticationManager authenticationManager, JwtCacheService jwtCacheService) {
	super(authenticationManager);
	this.jwtCacheService = jwtCacheService;
}

@Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
	try {
		String header = request.getHeader(JWTConfig.header);
		if (!StringUtils.isEmpty(header) && header.startsWith(JWTConfig.token_prefix)) {
			String authToken = header.substring(7);
			Claims claims = JWTTokenUtil.tokenParse(authToken);
			String jwtCache = this.jwtCacheService.getJwtCache(claims.getSubject());
			if (jwtCache != null && jwtCache.equals(authToken)) {
				if (claims.getExpiration().after(new Date())) {
					@SuppressWarnings("unchecked")
					List<String> dd = claims.get("Role", Lists.<String>newArrayList().getClass());
					Set<SimpleGrantedAuthority> set = dd.stream().map(s -> new SimpleGrantedAuthority((String) s)).collect(Collectors.toSet());
					UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(claims.getSubject(), authToken, set);
					SecurityContextHolder.getContext().setAuthentication(authentication);
				}
			}
		}
	} catch (Exception e) {
		e.printStackTrace();
	}
	doFilter(request, response, chain);
}
}
