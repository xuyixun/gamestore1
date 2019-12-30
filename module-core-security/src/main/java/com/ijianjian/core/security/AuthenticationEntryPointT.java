package com.ijianjian.core.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ijianjian.core.common.object.CommonResult;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.ijianjian.core.common.constant.ResultType.AuthError.un_authorized;

import java.io.IOException;

@Component
public class AuthenticationEntryPointT implements AuthenticationEntryPoint {

@Override
public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
	ObjectMapper mapper = new ObjectMapper();
 //response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
 response.setHeader("content-type", "application/json;charset=UTF-8");
 response.setStatus(HttpStatus.UNAUTHORIZED.value());
 mapper.writeValue(response.getWriter(), CommonResult.errorResult(un_authorized));
 System.out.println("____AuthenticationEntryPointT");
}
}
