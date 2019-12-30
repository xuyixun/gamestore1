package com.ijianjian.core.security.authorization.smscode;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

public class AuthenticationFilterJwtSmsCode extends AbstractAuthenticationProcessingFilter {
public AuthenticationFilterJwtSmsCode(AuthenticationManager authenticationManager, AuthenticationSuccessHandler successHandler, AuthenticationFailureHandler failureHandler) {
	super(new AntPathRequestMatcher("/v1/login/jwt/sms_code", "POST"));
	super.setAuthenticationManager(authenticationManager);
	super.setAuthenticationSuccessHandler(successHandler);
	super.setAuthenticationFailureHandler(failureHandler);
}

@Override
public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
	String phone = obtainPhone(request);
	if (phone == null) {
		phone = "";
	}
	String smsCode = obtainSmsCode(request);
	if (smsCode == null) {
		smsCode = "";
	}
	SmsCodeAuthenticationToken authRequest = new SmsCodeAuthenticationToken(phone, smsCode);
	return this.getAuthenticationManager().authenticate(authRequest);
}

private static final String SPRING_SECURITY_FORM_PHONE = "phone";
private static final String SPRING_SECURITY_FORM_SMS_CODE = "sms_code";

private String obtainPhone(HttpServletRequest request) {
	return request.getParameter(SPRING_SECURITY_FORM_PHONE);
}

private String obtainSmsCode(HttpServletRequest request) {
	return request.getParameter(SPRING_SECURITY_FORM_SMS_CODE);
}
}
