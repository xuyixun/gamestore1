package com.ijianjian.game.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.ijianjian.core.common.constant.ResultType.CommonError;
import com.ijianjian.core.common.object.CommonResult;
import com.ijianjian.game.domain.dto.ColumnMarketingAdSearchDTO;
import com.ijianjian.game.domain.po.Ad;
import com.ijianjian.game.domain.po.ColumnMarketing;
import com.ijianjian.game.domain.po.ColumnMarketingAd;
import com.ijianjian.game.domain.repository.ColumnMarketingAdRepository;
import com.ijianjian.game.util.DomainFactory;
import com.ijianjian.game.util.LocalUser;
import com.ijianjian.game.util.ResultType.ColumnMarketingError;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "营销栏目-广告")
@RestController
public class ColumnMarketingAdService implements LocalUser {
private final ColumnMarketingAdRepository columnMarketingAdRepository;

public ColumnMarketingAdService(ColumnMarketingAdRepository columnMarketingAdRepository) {
	this.columnMarketingAdRepository = columnMarketingAdRepository;
}

@ApiOperation("查询")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@GetMapping("v1/column_marketing_ad")
public CommonResult query(ColumnMarketingAdSearchDTO dto) {
	dto.init();
	Page<ColumnMarketingAd> page = this.columnMarketingAdRepository.query(dto);
	if (page == null || page.getTotalElements() == 0)
		return CommonResult.errorResult(CommonError.list_empty);
	return CommonResult.successResult(new PageImpl<>(DomainFactory._2ColumnMarketingAdVO(page.getContent()), page.getPageable(), page.getTotalElements()));
}

@ApiOperation("保存应用栏目游戏")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/column_marketing_ad")
public CommonResult save(String columnMarketingUuid, String adUuid) {
	if (Strings.isNullOrEmpty(columnMarketingUuid) || Strings.isNullOrEmpty(adUuid))
		return CommonResult.errorResult(CommonError.param_error);
	if (this.columnMarketingAdRepository.existsByColumnMarketing_fUuidAndAd_fUuid(columnMarketingUuid, adUuid)) {
		return CommonResult.errorResult(ColumnMarketingError.ad_exists);
	}

	Integer order = this.columnMarketingAdRepository.maxOrder(columnMarketingUuid);
	if (order == null) {
		order = 1;
	} else {
		order += 1;
	}
	ColumnMarketingAd po = ColumnMarketingAd.builder().fOrder(order).createUser(localCore()).columnMarketing(ColumnMarketing.builder().fUuid(columnMarketingUuid).build()).ad(Ad.builder().fUuid(adUuid).build()).build();
	this.columnMarketingAdRepository.save(po);
	return CommonResult.successResult(po.getFOrder());
}

@ApiOperation("删除")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/column_marketing_ad/delete/{columnMarketingUuid}/{uuid}")
public CommonResult delete(@PathVariable String columnMarketingUuid, @PathVariable String uuid) {
	Integer order = this.columnMarketingAdRepository.order(uuid);
	this.columnMarketingAdRepository.updateDelete(columnMarketingUuid, order);
	this.columnMarketingAdRepository.deleteById(uuid);
	return CommonResult.successResult(1);
}

@ApiOperation("置顶")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/column_marketing_ad/top/{columnMarketingUuid}/{uuid}")
public CommonResult top(@PathVariable String columnMarketingUuid, @PathVariable String uuid) {
	Integer order = this.columnMarketingAdRepository.order(uuid);
	this.columnMarketingAdRepository.updateTop(columnMarketingUuid, order);
	this.columnMarketingAdRepository.updateTop(uuid);
	return CommonResult.successResult(1);
}

@ApiOperation("上移")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/column_marketing_ad/up/{columnMarketingUuid}/{uuid}")
public CommonResult up(@PathVariable String columnMarketingUuid, @PathVariable String uuid) {
	Integer order = this.columnMarketingAdRepository.order(uuid);
	this.columnMarketingAdRepository.updateUp(columnMarketingUuid, order);
	this.columnMarketingAdRepository.updateUp(uuid);
	return CommonResult.successResult(1);
}

@ApiOperation("下移")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/column_marketing_ad/down/{columnMarketingUuid}/{uuid}")
public CommonResult down(@PathVariable String columnMarketingUuid, @PathVariable String uuid) {
	Integer order = this.columnMarketingAdRepository.order(uuid);
	this.columnMarketingAdRepository.updateDown(columnMarketingUuid, order);
	this.columnMarketingAdRepository.updateDown(uuid);
	return CommonResult.successResult(1);
}
}
