package com.ijianjian.core.security.authorization;

import org.springframework.security.core.AuthenticationException;

public class SmsCodeErrorException extends AuthenticationException {
private static final long serialVersionUID = 1L;

public SmsCodeErrorException(String msg) {
	super(msg);
}
}
