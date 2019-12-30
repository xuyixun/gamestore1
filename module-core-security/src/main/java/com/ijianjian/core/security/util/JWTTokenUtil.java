package com.ijianjian.core.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

import org.springframework.security.core.GrantedAuthority;

import com.ijianjian.core.common.constant.Config.JWTConfig;
import com.ijianjian.core.security.authorization.UserDetailsImpl;

public class JWTTokenUtil {
public static String tokenGenerate(UserDetailsImpl userDetails,Long time) {
		return Jwts.builder()
				.claim("Role",userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toArray(String[]::new))
				.setSubject(userDetails.getUserUuid())
				.setIssuer("ijianjian")
				.setIssuedAt(new Date(time))
				.setExpiration(new Date(time + JWTConfig.expiration_time))
				.signWith(SignatureAlgorithm.HS256, JWTConfig.secret)
	  .compact();
}

public static Claims tokenParse(String token) {
	return Jwts.parser().setSigningKey(JWTConfig.secret).parseClaimsJws(token).getBody();
}
}
