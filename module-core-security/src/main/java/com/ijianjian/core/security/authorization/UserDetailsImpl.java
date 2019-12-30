package com.ijianjian.core.security.authorization;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class UserDetailsImpl extends User {
private static final long serialVersionUID = -1847721807165944772L;
private final String userUuid;

public UserDetailsImpl(String username, String password, Collection<? extends GrantedAuthority> authorities, String userUuid) {
	super(username, password, authorities);
	this.userUuid = userUuid;
}
}
