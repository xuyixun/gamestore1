package com.ijianjian.core.security.authorization.phone;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;
import com.ijianjian.core.domain.user.po.CoreUser;
import com.ijianjian.core.domain.user.repository.CoreUserRepository;
import com.ijianjian.core.security.authorization.UserDetailsImpl;

@Component
public class AuthenticationProviderPhone implements AuthenticationProvider {
protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
private final CoreUserRepository coreUserRepository;

public AuthenticationProviderPhone(CoreUserRepository coreUserRepository) {
	this.coreUserRepository = coreUserRepository;
}

@Override
public Authentication authenticate(Authentication authentication) throws AuthenticationException {
	String phone = authentication.getPrincipal().toString();
	Optional<CoreUser> coreUserOptional = this.coreUserRepository.findByFDeletedFalseAndFUserName(phone);
	if (coreUserOptional.isPresent()) {
		CoreUser coreUser = coreUserOptional.get();
		UserDetails user = new UserDetailsImpl(phone, "", (coreUser.getFRoles() == null || coreUser.getFRoles().equals("")) ? null : Sets.newHashSet(coreUser.getFRoles().split(",")).stream().map(s -> new SimpleGrantedAuthority(s)).collect(Collectors.toSet()), coreUser.getFUuid());
		PhoneAuthenticationToken result = new PhoneAuthenticationToken(user, user.getAuthorities());
		return result;
	} else {
		throw new UsernameNotFoundException(this.messages.getMessage("UserDetailsServicePassword.notFound", new Object[] { phone }, "Username not found"));
	}
}

@Override
public boolean supports(Class<?> authentication) {
	return (PhoneAuthenticationToken.class.isAssignableFrom(authentication));
}
}
