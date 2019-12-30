package com.ijianjian.channel.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.ijianjian.channel.domain.dto.UserCreateDTO;
import com.ijianjian.channel.domain.dto.UserSearchDTO;
import com.ijianjian.channel.domain.repository.UserRepository;
import com.ijianjian.channel.util.DomainFactory;
import com.ijianjian.channel.util.LocalUser;
import com.ijianjian.channel.util.ResultType.UserError;
import com.ijianjian.core.common.constant.ResultType.CommonError;
import com.ijianjian.core.common.object.CommonResult;
import com.ijianjian.core.domain.user.po.CoreUser;
import com.ijianjian.core.domain.user.repository.CoreUserRepository;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "用户其他")
@RestController
public class UserService implements LocalUser {
private final CoreUserRepository coreUserRepository;
private final UserRepository userRepository;
private final BCryptPasswordEncoder bCryptPasswordEncoder;

public UserService(CoreUserRepository coreUserRepository, UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
	super();
	this.coreUserRepository = coreUserRepository;
	this.userRepository = userRepository;
	this.bCryptPasswordEncoder = bCryptPasswordEncoder;
}

@ApiOperation("查询")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@GetMapping("v1/user")
public CommonResult query(UserSearchDTO dto) {
	dto.init();
	Page<CoreUser> page = this.userRepository.query(dto);
	if (page == null || page.getTotalElements() == 0) {
		return CommonResult.errorResult(CommonError.list_empty);
	}
	return CommonResult.successResult(new PageImpl<>(DomainFactory._2UserVO(page.getContent()), page.getPageable(), page.getTotalElements()));
}

@ApiOperation("保存")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/user")
public CommonResult save(UserCreateDTO dto) {
	if (Strings.isNullOrEmpty(dto.getUsername()) || (Strings.isNullOrEmpty(dto.getUuid()) && Strings.isNullOrEmpty(dto.getPassword()))) {
		return CommonResult.errorResult(CommonError.param_error);
	}
	CoreUser user;
	if (Strings.isNullOrEmpty(dto.getUuid())) {
		if (this.coreUserRepository.existsByFUserName(dto.getUsername())) {
			return CommonResult.errorResult(UserError.name_exists);
		}
		user = CoreUser.builder().createUser(localCore()).fRoles("ROLE_admin").fUserName(dto.getUsername()).fPassword(bCryptPasswordEncoder.encode(dto.getPassword())).fEnable(true).build();
		this.coreUserRepository.save(user);
	}
	return CommonResult.successResult(dto.getUsername());
}

@ApiOperation("重置密码")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/user/reset_password")
public CommonResult resetPassword(String uuid, String password) {
	this.coreUserRepository.updatePassword(uuid, bCryptPasswordEncoder.encode(password));
	return CommonResult.successResult("1");
}

@ApiOperation("详情")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@GetMapping("v1/user/{uuid}")
public CommonResult one(@PathVariable String uuid) {
	Optional<CoreUser> uOptional = this.coreUserRepository.findById(uuid);
	if (uOptional.isPresent()) {
		return CommonResult.successResult(DomainFactory._2VO(uOptional.get()));
	}
	return CommonResult.errorResult(CommonError.data_not_exist);
}

@ApiOperation("启用/禁用")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/user/able/{uuid}")
public CommonResult able(@PathVariable String uuid) {
	this.coreUserRepository.able(uuid);
	return CommonResult.successResult("1");
}

@ApiOperation("删除")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/user/delete/{uuid}")
public CommonResult delete(@PathVariable String uuid) {
	this.coreUserRepository.delete(uuid, LocalDateTime.now());
	return CommonResult.successResult("1");
}

@ApiOperation("登录用户详情")
@GetMapping("v1/user/self_info")
public CommonResult selfInfo() {
	Optional<CoreUser> uOptional = this.coreUserRepository.findById(localCore().getFUuid());
	if (uOptional.isPresent()) {
		return CommonResult.successResult(DomainFactory._2VO(uOptional.get()));
	}
	return CommonResult.errorResult(CommonError.data_not_exist);
}

@Scheduled(fixedRate = 60000 * 60)
public void firstLoginTime() {
	this.userRepository.findByFFirstLoginTimeIsNullAndFLastLoginTimeIsNotNull().forEach(i -> {
		this.coreUserRepository.updateFirstLoginTime(i.getFUuid());
	});
}
}
