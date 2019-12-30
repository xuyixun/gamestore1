package com.ijianjian.core.security.authorization.phone;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;

public class PhoneAuthenticationToken extends AbstractAuthenticationToken {
private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

private final Object principal;

public PhoneAuthenticationToken(Object phone) {
	super(null);
	this.principal = phone;
	setAuthenticated(false);
}

public PhoneAuthenticationToken(Object phone, Collection<? extends GrantedAuthority> authorities) {
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

}
