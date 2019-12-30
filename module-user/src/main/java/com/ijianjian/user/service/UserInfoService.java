package com.ijianjian.user.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.ijianjian.core.common.constant.ResultType.CommonError;
import com.ijianjian.core.common.object.CommonResult;
import com.ijianjian.core.domain.user.po.CoreUser;
import com.ijianjian.core.domain.user.repository.CoreUserRepository;
import com.ijianjian.user.domain.dto.UserInfoCreateDTO_001;
import com.ijianjian.user.domain.dto.UserInfoSearchDTO;
import com.ijianjian.user.domain.po.UserInfo;
import com.ijianjian.user.domain.repository.UserInfoRepository;
import com.ijianjian.user.util.DomainFactory;
import com.ijianjian.user.util.LocalUser;
import com.ijianjian.user.util.FieldConstant.UserType;
import com.ijianjian.user.util.ResultType.UserError;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "用户")
@RestController
public class UserInfoService implements LocalUser {
private final UserInfoRepository userInfoRepository;
private final CoreUserRepository coreUserRepository;
private final BCryptPasswordEncoder bCryptPasswordEncoder;

public UserInfoService(UserInfoRepository userInfoRepository, CoreUserRepository coreUserRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
	this.userInfoRepository = userInfoRepository;
	this.coreUserRepository = coreUserRepository;
	this.bCryptPasswordEncoder = bCryptPasswordEncoder;
}

@ApiOperation("查询")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@GetMapping("v1/user_info")
public CommonResult query(UserInfoSearchDTO dto) {
	dto.init();
	Page<UserInfo> page = this.userInfoRepository.query(dto);
	if (page == null || page.getTotalElements() == 0) {
		return CommonResult.errorResult(CommonError.list_empty);
	}
	return CommonResult.successResult(new PageImpl<>(DomainFactory._2UserInfoVO_001(page.getContent()), page.getPageable(), page.getTotalElements()));
}

@ApiOperation("保存")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/user_info")
public CommonResult save(UserInfoCreateDTO_001 dto) {
	if (Strings.isNullOrEmpty(dto.getUsername()) || (Strings.isNullOrEmpty(dto.getUuid()) && Strings.isNullOrEmpty(dto.getPassword()))) {
		return CommonResult.errorResult(CommonError.param_error);
	}
	UserInfo user;
	if (Strings.isNullOrEmpty(dto.getUuid())) {
		if (this.coreUserRepository.existsByFUserName(dto.getUsername())) {
			return CommonResult.errorResult(UserError.name_exists);
		}
		user = UserInfo.builder().fName(dto.getUsername()).type(UserType.admin).core(CoreUser.builder().createUser(localCore()).fRoles("ROLE_admin").fUserName(dto.getUsername()).fPassword(bCryptPasswordEncoder.encode(dto.getPassword())).fEnable(true).build()).build();
	} else {
		Optional<UserInfo> uOptional = this.userInfoRepository.findById(dto.getUuid());
		if (!uOptional.isPresent()) {
			return CommonResult.errorResult(CommonError.data_not_exist);
		}
		user = uOptional.get();
	}
	if (!Strings.isNullOrEmpty(dto.getNickName())) {
		user.setFNickeName(dto.getNickName());
	}
	if (!Strings.isNullOrEmpty(dto.getPhone())) {
		user.setFPhone(dto.getPhone());
	}
	this.userInfoRepository.save(user);
	return CommonResult.successResult(user.getFName());
}

@ApiOperation("重置密码")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/user_info/reset_password")
public CommonResult resetPassword(String uuid, String password) {
	this.coreUserRepository.updatePassword(uuid, bCryptPasswordEncoder.encode(password));
	return CommonResult.successResult("1");
}

@ApiOperation("详情")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@GetMapping("v1/user_info/{uuid}")
public CommonResult one(@PathVariable String uuid) {
	Optional<UserInfo> uOptional = this.userInfoRepository.findById(uuid);
	if (uOptional.isPresent()) {
		return CommonResult.successResult(DomainFactory._2VO(uOptional.get()));
	}
	return CommonResult.errorResult(CommonError.data_not_exist);
}

@ApiOperation("启用/禁用")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/user_info/able/{uuid}")
public CommonResult able(@PathVariable String uuid) {
	this.coreUserRepository.able(uuid);
	return CommonResult.successResult("1");
}

@ApiOperation("删除")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/user_info/delete/{uuid}")
public CommonResult delete(@PathVariable String uuid) {
	this.coreUserRepository.delete(uuid, LocalDateTime.now());
	return CommonResult.successResult("1");
}

@ApiOperation("登录用户详情")
@GetMapping("v1/user_info/self_info")
public CommonResult selfInfo() {
	Optional<UserInfo> uOptional = this.userInfoRepository.findById(localCore().getFUuid());
	if (uOptional.isPresent()) {
		return CommonResult.successResult(DomainFactory._2VO(uOptional.get()));
	}
	return CommonResult.errorResult(CommonError.data_not_exist);
}
}
