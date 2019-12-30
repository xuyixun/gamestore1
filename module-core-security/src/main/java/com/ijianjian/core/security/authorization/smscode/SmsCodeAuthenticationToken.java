package com.ijianjian.core.security.authorization.smscode;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;

public class SmsCodeAuthenticationToken extends AbstractAuthenticationToken {
private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

private final Object principal;
private String code;

public SmsCodeAuthenticationToken(Object phone, String code) {
	super(null);
	this.principal = phone;
	this.code = code;
	setAuthenticated(false);
}

public SmsCodeAuthenticationToken(Object phone, Collection<? extends GrantedAuthority> authorities) {
	super(authorities);
	this.principal = phone;
	super.setAuthenticated(true);
}

@Override
public Object getCredentials() {
	return null;
}

@Override
public Object getPrincipal() {
	return principal;
}

public String getCode() {
	return code;
}
}
