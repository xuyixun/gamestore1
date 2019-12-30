package com.ijianjian.core.security.authorization.up;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthenticationFilterJwtUP extends AbstractAuthenticationProcessingFilter {
public AuthenticationFilterJwtUP(AuthenticationManager authenticationManager, AuthenticationSuccessHandler successHandler, AuthenticationFailureHandler failureHandler) {
	super(new AntPathRequestMatcher("/v1/login/jwt/up", "POST"));
	super.setAuthenticationManager(authenticationManager);
	super.setAuthenticationSuccessHandler(successHandler);
	super.setAuthenticationFailureHandler(failureHandler);
}

@Override
public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
	String username = obtainUsername(request);
	String password = obtainPassword(request);
	if (username == null) {
		username = "";
	}
	if (password == null) {
		password = "";
	}
	password = password.trim();
	UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
	// Allow subclasses to set the "details" property
	setDetails(request, authRequest);
	request.setAttribute("login_type", "username_password");
	return this.getAuthenticationManager().authenticate(authRequest);
}

private static final String SPRING_SECURITY_FORM_USERNAME_KEY = "username";
private static final String SPRING_SECURITY_FORM_PASSWORD_KEY = "password";

private String obtainPassword(HttpServletRequest request) {
	return request.getParameter(SPRING_SECURITY_FORM_PASSWORD_KEY);
}

private String obtainUsername(HttpServletRequest request) {
	return request.getParameter(SPRING_SECURITY_FORM_USERNAME_KEY);
}

private void setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {
	authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
}
}
