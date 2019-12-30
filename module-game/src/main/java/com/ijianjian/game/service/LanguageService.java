package com.ijianjian.game.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.ijianjian.core.common.constant.ResultType.CommonError;
import com.ijianjian.core.common.object.CommonResult;
import com.ijianjian.game.domain.dto.LanguageCreateDTO;
import com.ijianjian.game.domain.po.Language;
import com.ijianjian.game.domain.repository.LanguageRepository;
import com.ijianjian.game.util.DomainFactory;
import com.ijianjian.game.util.LocalUser;
import com.ijianjian.game.util.ResultType.LanguageError;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "语言")
@RestController
public class LanguageService implements LocalUser {
private final LanguageRepository languageRepository;

public LanguageService(LanguageRepository languageRepository) {
	this.languageRepository = languageRepository;
}

@ApiOperation("查询")
@GetMapping("v1/language")
public CommonResult query() {
	return CommonResult.successResult(DomainFactory._2LanguageVO_001(this.languageRepository.findByFDeletedFalse()));
}

@ApiOperation("保存")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/language")
public CommonResult save(LanguageCreateDTO dto) {
	if (Strings.isNullOrEmpty(dto.getNumber()) || Strings.isNullOrEmpty(dto.getName())) {
		return CommonResult.errorResult(CommonError.param_error);
	}
	Language language;
	if (Strings.isNullOrEmpty(dto.getUuid())) {
		if (this.languageRepository.existsByFNumber(dto.getNumber())) {
			return CommonResult.errorResult(LanguageError.number_exists);
		}
		language = Language.builder().createUser(localCore()).build();
	} else {
		Optional<Language> uOptional = this.languageRepository.findById(dto.getUuid());
		if (!uOptional.isPresent()) {
			return CommonResult.errorResult(CommonError.data_not_exist);
		}
		language = uOptional.get();
		if (!dto.getNumber().equals(language.getFNumber()) && this.languageRepository.existsByFNumber(dto.getName())) {
			return CommonResult.errorResult(LanguageError.number_exists);
		}
	}
	language.setFNumber(dto.getNumber());
	language.setFName(dto.getName());
	this.languageRepository.save(language);
	return CommonResult.successResult(language.getFName());
}

@ApiOperation("删除")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/language/delete/{uuid}")
public CommonResult delete(@PathVariable String uuid) {
	this.languageRepository.delete(uuid, LocalDateTime.now());
	return CommonResult.successResult("1");
}
}
