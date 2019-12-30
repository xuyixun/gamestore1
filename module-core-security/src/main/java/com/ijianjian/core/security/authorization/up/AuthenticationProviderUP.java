package com.ijianjian.core.security.authorization.up;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationProviderUP extends DaoAuthenticationProvider {
public AuthenticationProviderUP(UserDetailsServiceUP userDetailsServicePassword, BCryptPasswordEncoder bCryptPasswordEncoder) {
	this.setUserDetailsService(userDetailsServicePassword);
	this.setPasswordEncoder(bCryptPasswordEncoder);
}
}
