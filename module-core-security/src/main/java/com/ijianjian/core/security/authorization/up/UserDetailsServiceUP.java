package com.ijianjian.core.security.authorization.up;

import com.google.common.collect.Sets;
import com.ijianjian.core.domain.user.po.CoreUser;
import com.ijianjian.core.domain.user.repository.CoreUserRepository;
import com.ijianjian.core.security.authorization.UserDetailsImpl;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class UserDetailsServiceUP implements UserDetailsService {
private final CoreUserRepository coreUserRepository;
private MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

public UserDetailsServiceUP(CoreUserRepository userInfoRepository) {
	this.coreUserRepository = userInfoRepository;
}

@Override
public UserDetails loadUserByUsername(String username) throws AuthenticationException {
	Optional<CoreUser> coreUserOptional = this.coreUserRepository.findByFDeletedFalseAndFUserName(username);
	if (coreUserOptional.isPresent()) {
		CoreUser coreUser = coreUserOptional.get();
		if (!coreUser.isFEnable()) {
			throw new UsernameNotFoundException(this.messages.getMessage("UserDetailsServicePassword.disenable", new Object[] { username }, "Username disenable"));
		}
		return new UserDetailsImpl(username, coreUser.getFPassword(), (coreUser.getFRoles() == null || coreUser.getFRoles().equals("")) ? null : Sets.newHashSet(coreUser.getFRoles().split(",")).stream().map(s -> new SimpleGrantedAuthority(s)).collect(Collectors.toSet()), coreUser.getFUuid());
	} else {
		throw new UsernameNotFoundException(this.messages.getMessage("UserDetailsServicePassword.notFound", new Object[] { username }, "Username not found"));
	}
}
}
