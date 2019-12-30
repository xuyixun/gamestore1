package com.ijianjian.core.security;

import com.ijianjian.core.domain.log.repository.LogLoginRespository;
import com.ijianjian.core.domain.log.repository.LogRequestRepository;
import com.ijianjian.core.domain.user.repository.CoreUserRepository;
import com.ijianjian.core.security.authentication_jwt.AuthenticationFilterJWT;
import com.ijianjian.core.security.authorization.AuthenticationJwtHandlerFailure;
import com.ijianjian.core.security.authorization.AuthenticationJwtHandlerSuccess;
import com.ijianjian.core.security.authorization.phone.AuthenticationFilterJwtPhone;
import com.ijianjian.core.security.authorization.phone.AuthenticationProviderPhone;
import com.ijianjian.core.security.authorization.smscode.AuthenticationFilterJwtSmsCode;
import com.ijianjian.core.security.authorization.smscode.AuthenticationProviderSmsCode;
import com.ijianjian.core.security.authorization.up.AuthenticationFilterJwtUP;
import com.ijianjian.core.security.authorization.up.AuthenticationProviderUP;
import com.ijianjian.core.security.util.JwtCacheService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfigJWT extends WebSecurityConfigurerAdapter {
private final BCryptPasswordEncoder bCryptPasswordEncoder;

private final AuthenticationEntryPointT authenticationEntryPointT;
private final AccessDeniedHandlerT accessDeniedHandlerT;
//授权
private final AuthenticationProviderUP authenticationProviderUP;
private final AuthenticationProviderPhone authenticationProviderPhone;
private final AuthenticationProviderSmsCode authenticationProviderSmsCode;

private final LogRequestRepository logRequestRepository;

private final CoreUserRepository coreUserRepository;
private final LogLoginRespository loginRespository;

private final JwtCacheService jwtCacheService;

public WebSecurityConfigJWT(BCryptPasswordEncoder bCryptPasswordEncoder, AuthenticationEntryPointT authenticationEntryPointT, AccessDeniedHandlerT accessDeniedHandlerT, AuthenticationProviderUP authenticationProviderUP, AuthenticationProviderPhone authenticationProviderPhone,
  AuthenticationProviderSmsCode authenticationProviderSmsCode, LogRequestRepository logRequestRepository, CoreUserRepository coreUserRepository, LogLoginRespository loginRespository, JwtCacheService jwtCacheService) {
	super();
	this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	this.authenticationEntryPointT = authenticationEntryPointT;
	this.accessDeniedHandlerT = accessDeniedHandlerT;
	this.authenticationProviderUP = authenticationProviderUP;
	this.authenticationProviderPhone = authenticationProviderPhone;
	this.authenticationProviderSmsCode = authenticationProviderSmsCode;
	this.logRequestRepository = logRequestRepository;
	this.coreUserRepository = coreUserRepository;
	this.loginRespository = loginRespository;
	this.jwtCacheService = jwtCacheService;
}

//鉴权
public AuthenticationFilterJWT jwtBean() {
	return new AuthenticationFilterJWT(this.authenticationManagerBean(), jwtCacheService);
}

//日志
public LogRequestFilter logBean() {
	return new LogRequestFilter(this.authenticationManagerBean(), logRequestRepository);
}

public AuthenticationFilterJwtUP AuthenticationFilterJwtUPBean() {
	return new AuthenticationFilterJwtUP(authenticationManagerBean(), successBean(), failure());
}

public AuthenticationFilterJwtPhone AuthenticationFilterJwtPhoneBean() {
	return new AuthenticationFilterJwtPhone(authenticationManagerBean(), successBean(), failure());
}

public AuthenticationFilterJwtSmsCode AuthenticationFilterJwtSmsCodeBean() {
	return new AuthenticationFilterJwtSmsCode(authenticationManagerBean(), successBean(), failure());
}

public AuthenticationJwtHandlerSuccess successBean() {
	return new AuthenticationJwtHandlerSuccess(coreUserRepository, loginRespository, jwtCacheService);
}

public AuthenticationJwtHandlerFailure failure() {
	return new AuthenticationJwtHandlerFailure();
}

@Override
protected void configure(HttpSecurity httpSecurity) throws Exception {
	httpSecurity
	  // 鐢变簬浣跨敤鐨勬槸JWT锛屾垜浠繖閲屼笉闇�瑕乧srf
	  .csrf().disable().exceptionHandling()
	  // access fail
	  .authenticationEntryPoint(authenticationEntryPointT)
	  // role fail
	  .accessDeniedHandler(accessDeniedHandlerT).and()
	  // 鍩轰簬token锛屾墍浠ヤ笉闇�瑕乻ession
	  .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().headers().frameOptions().sameOrigin().and().authorizeRequests().antMatchers("/favicon.ico").permitAll().antMatchers("/swagger-ui.html", "/webjars/springfox-swagger-ui/**", "/swagger-resources/**", "/v2/api-docs")
	  .permitAll()

	  .antMatchers(HttpMethod.POST, "/v1/user_info/auto_registered").permitAll().antMatchers(HttpMethod.POST, "/v1/user_info/auto_registered/landing_page").permitAll().antMatchers(HttpMethod.POST, "/v1/user_info/verification_code").permitAll()

	  .antMatchers(HttpMethod.POST, "/v1/user_info/subscribe").permitAll().antMatchers(HttpMethod.POST, "/v1/user_info/subscribe/code").permitAll()

	  .antMatchers(HttpMethod.GET, "/v1/column_app/load/*").permitAll().antMatchers(HttpMethod.GET, "/v1/column_app/load/*/*/*/*").permitAll().antMatchers(HttpMethod.GET, "/v1/column_general/load/*").permitAll().antMatchers(HttpMethod.GET, "/v1/column_general/load/*/*").permitAll()
	  .antMatchers(HttpMethod.GET, "/v1/column_general/load/*/*/*/*").permitAll().antMatchers(HttpMethod.GET, "/v1/column_general/*/*").permitAll().antMatchers(HttpMethod.GET, "/v1/column_marketing_game/load/*/*/*/*").permitAll().antMatchers(HttpMethod.GET, "/v1/game_info/shang_xian").permitAll()
	  .antMatchers(HttpMethod.GET, "/v1/game_info/*/*").permitAll().antMatchers(HttpMethod.GET, "/v1/game_info/ad_picture/*").permitAll()
	  // .antMatchers(HttpMethod.GET,"/v1/game_info/download/*").permitAll()
	  .antMatchers(HttpMethod.GET, "/v1/game_info/download").permitAll().antMatchers(HttpMethod.GET, "/v1/home/*").permitAll()

	  .antMatchers(HttpMethod.GET, "/v1/game_store_app/download_lastest").permitAll().antMatchers(HttpMethod.GET, "/v1/game_store_app/download").permitAll().antMatchers(HttpMethod.POST, "/v1/game_store_app/check/*").permitAll()

	  .antMatchers(HttpMethod.POST, "/pg/datasync").permitAll()

	  .antMatchers(HttpMethod.GET, "/pg/bill").permitAll().antMatchers(HttpMethod.GET, "/pg/get_bill").permitAll().antMatchers(HttpMethod.GET, "/pg/gzip_bill").permitAll()

	  // channel project
	  .antMatchers("/hw/channel_notify").permitAll().antMatchers("/r/*").permitAll()

	  .antMatchers("/test_xyx/*").permitAll()

	  .anyRequest().authenticated().and().addFilterBefore(AuthenticationFilterJwtUPBean(), UsernamePasswordAuthenticationFilter.class).addFilterBefore(AuthenticationFilterJwtPhoneBean(), UsernamePasswordAuthenticationFilter.class)
	  .addFilterBefore(AuthenticationFilterJwtSmsCodeBean(), UsernamePasswordAuthenticationFilter.class)
	  // token filter
	  .addFilter(jwtBean()).addFilter(logBean())
	  // auth filter
	  .headers().cacheControl();
}

@Override
protected void configure(AuthenticationManagerBuilder auth) {
	try {
		auth.inMemoryAuthentication().withUser("qazedc").password(bCryptPasswordEncoder.encode("wsxrfv11")).roles("super_admin", "admin").and().and().authenticationProvider(authenticationProviderUP).authenticationProvider(authenticationProviderPhone).authenticationProvider(authenticationProviderSmsCode);
	} catch (Exception e) {
		e.printStackTrace();
	}
}

@Bean
@Override
public AuthenticationManager authenticationManagerBean() {
	try {
		return super.authenticationManagerBean();
	} catch (Exception e) {
		e.printStackTrace();
	}
	return null;
}
}
