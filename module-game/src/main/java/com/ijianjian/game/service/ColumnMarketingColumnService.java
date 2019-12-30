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
import com.ijianjian.game.domain.dto.ColumnMarketingColumnSearchDTO;
import com.ijianjian.game.domain.po.ColumnGeneral;
import com.ijianjian.game.domain.po.ColumnMarketing;
import com.ijianjian.game.domain.po.ColumnMarketingColumn;
import com.ijianjian.game.domain.repository.ColumnMarketingColumnRepository;
import com.ijianjian.game.util.DomainFactory;
import com.ijianjian.game.util.LocalUser;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "营销栏目-普通栏目")
@RestController
public class ColumnMarketingColumnService implements LocalUser {
private final ColumnMarketingColumnRepository columnMarketingColumnRepository;

public ColumnMarketingColumnService(ColumnMarketingColumnRepository columnMarketingColumnRepository) {
	this.columnMarketingColumnRepository = columnMarketingColumnRepository;
}

@ApiOperation("查询")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@GetMapping("v1/column_marketing_column")
public CommonResult query(ColumnMarketingColumnSearchDTO dto) {
	dto.init();
	Page<ColumnMarketingColumn> page = this.columnMarketingColumnRepository.query(dto);
	if (page == null || page.getTotalElements() == 0)
		return CommonResult.errorResult(CommonError.list_empty);
	return CommonResult.successResult(new PageImpl<>(DomainFactory._2ColumnMarketingColumnVO(page.getContent()), page.getPageable(), page.getTotalElements()));
}

@ApiOperation("保存应用栏目游戏")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/column_marketing_column")
public CommonResult save(String columnMarketingUuid, String adUuid) {
	if (Strings.isNullOrEmpty(columnMarketingUuid) || Strings.isNullOrEmpty(adUuid))
		return CommonResult.errorResult(CommonError.param_error);

	Integer order = this.columnMarketingColumnRepository.maxOrder(columnMarketingUuid);
	if (order == null) {
		order = 1;
	} else {
		order += 1;
	}
	ColumnMarketingColumn po = ColumnMarketingColumn.builder().fOrder(order).createUser(localCore()).columnMarketing(ColumnMarketing.builder().fUuid(columnMarketingUuid).build()).columnGeneral(ColumnGeneral.builder().fUuid(adUuid).build()).build();
	this.columnMarketingColumnRepository.save(po);
	return CommonResult.successResult(po.getFOrder());
}

@ApiOperation("删除")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/column_marketing_column/delete/{columnMarketingUuid}/{uuid}")
public CommonResult delete(@PathVariable String columnMarketingUuid, @PathVariable String uuid) {
	Integer order = this.columnMarketingColumnRepository.order(uuid);
	this.columnMarketingColumnRepository.updateDelete(columnMarketingUuid, order);
	this.columnMarketingColumnRepository.deleteById(uuid);
	return CommonResult.successResult(1);
}

@ApiOperation("置顶")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/column_marketing_column/top/{columnMarketingUuid}/{uuid}")
public CommonResult top(@PathVariable String columnMarketingUuid, @PathVariable String uuid) {
	Integer order = this.columnMarketingColumnRepository.order(uuid);
	this.columnMarketingColumnRepository.updateTop(columnMarketingUuid, order);
	this.columnMarketingColumnRepository.updateTop(uuid);
	return CommonResult.successResult(1);
}

@ApiOperation("上移")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/column_marketing_column/up/{columnMarketingUuid}/{uuid}")
public CommonResult up(@PathVariable String columnMarketingUuid, @PathVariable String uuid) {
	Integer order = this.columnMarketingColumnRepository.order(uuid);
	this.columnMarketingColumnRepository.updateUp(columnMarketingUuid, order);
	this.columnMarketingColumnRepository.updateUp(uuid);
	return CommonResult.successResult(1);
}

@ApiOperation("下移")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/column_marketing_column/down/{columnMarketingUuid}/{uuid}")
public CommonResult down(@PathVariable String columnMarketingUuid, @PathVariable String uuid) {
	Integer order = this.columnMarketingColumnRepository.order(uuid);
	this.columnMarketingColumnRepository.updateDown(columnMarketingUuid, order);
	this.columnMarketingColumnRepository.updateDown(uuid);
	return CommonResult.successResult(1);
}
}
