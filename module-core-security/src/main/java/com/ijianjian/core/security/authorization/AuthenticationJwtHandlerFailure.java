package com.ijianjian.core.security.authorization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ijianjian.core.common.object.CommonResult;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.ijianjian.core.common.constant.ResultType.AuthError.*;

import java.io.IOException;

public class AuthenticationJwtHandlerFailure implements AuthenticationFailureHandler {

@Override
public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
	ObjectMapper mapper = new ObjectMapper();
	response.setHeader("content-type", "application/json;charset=UTF-8");
	response.setStatus(HttpStatus.OK.value());
	if (exception instanceof BadCredentialsException) {
		mapper.writeValue(response.getWriter(), CommonResult.errorResult(bad_credentials));
	} else if (exception instanceof UsernameNotFoundException) {
		mapper.writeValue(response.getWriter(), CommonResult.errorResult(user_not_exist));
	} else if (exception instanceof SmsCodeErrorException) {
		mapper.writeValue(response.getWriter(), CommonResult.errorResult(sms_code_error));
	} else {
		response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
	}
	System.out.println("____AuthenticationFailureHandlerT");
}
}
