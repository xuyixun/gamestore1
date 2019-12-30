package com.ijianjian.core.security.authorization.phone;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthenticationFilterJwtPhone extends AbstractAuthenticationProcessingFilter {
public AuthenticationFilterJwtPhone(AuthenticationManager authenticationManager, AuthenticationSuccessHandler successHandler, AuthenticationFailureHandler failureHandler) {
	super(new AntPathRequestMatcher("/v1/login/jwt/phone", "POST"));
	super.setAuthenticationManager(authenticationManager);
	super.setAuthenticationSuccessHandler(successHandler);
	super.setAuthenticationFailureHandler(failureHandler);
}

@Override
public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
	String phone = obtainUsername(request);
	if (phone == null) {
		phone = "";
	}
	PhoneAuthenticationToken authRequest = new PhoneAuthenticationToken(phone);
	return this.getAuthenticationManager().authenticate(authRequest);
}

private static final String SPRING_SECURITY_FORM_PHONE = "phone";

private String obtainUsername(HttpServletRequest request) {
	return request.getParameter(SPRING_SECURITY_FORM_PHONE);
}
}
