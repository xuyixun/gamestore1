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
import com.ijianjian.game.domain.dto.ColumnGeneralRelationSearchDTO;
import com.ijianjian.game.domain.po.ColumnGeneral;
import com.ijianjian.game.domain.po.ColumnGeneralRelation;
import com.ijianjian.game.domain.repository.ColumnGeneralRelationRepository;
import com.ijianjian.game.util.DomainFactory;
import com.ijianjian.game.util.LocalUser;
import com.ijianjian.game.util.ResultType.ColumnGeneralGameError;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "普通栏目-关系")
@RestController
public class ColumnGeneralRelationService implements LocalUser {
private final ColumnGeneralRelationRepository columnGeneralRelationRepository;

public ColumnGeneralRelationService(ColumnGeneralRelationRepository columnGeneralRelationRepository) {
	this.columnGeneralRelationRepository = columnGeneralRelationRepository;
}

@ApiOperation("查询")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@GetMapping("v1/column_general_relation")
public CommonResult query(ColumnGeneralRelationSearchDTO dto) {
	dto.init();
	Page<ColumnGeneralRelation> page = this.columnGeneralRelationRepository.query(dto);
	if (page == null || page.getTotalElements() == 0)
		return CommonResult.errorResult(CommonError.list_empty);
	return CommonResult.successResult(new PageImpl<>(DomainFactory._2ColumnGeneralVO_001_a(page.getContent()), page.getPageable(), page.getTotalElements()));
}

@ApiOperation("保存应用栏目游戏")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/column_general_relation")
public CommonResult save(String parentUuid, String childUuid) {
	if (Strings.isNullOrEmpty(parentUuid) || Strings.isNullOrEmpty(childUuid) || this.columnGeneralRelationRepository.child(parentUuid) || !this.columnGeneralRelationRepository.child(childUuid))
		return CommonResult.errorResult(CommonError.param_error);

	if (this.columnGeneralRelationRepository.existsByChild_fUuid(childUuid))
		return CommonResult.errorResult(ColumnGeneralGameError.game_exists);

	Integer order = this.columnGeneralRelationRepository.maxOrder(parentUuid);
	if (order == null) {
		order = 1;
	} else {
		order += 1;
	}
	ColumnGeneralRelation po = ColumnGeneralRelation.builder().fOrder(order).createUser(localCore()).parent(ColumnGeneral.builder().fUuid(parentUuid).build()).child(ColumnGeneral.builder().fUuid(childUuid).build()).build();
	this.columnGeneralRelationRepository.save(po);
	return CommonResult.successResult(po.getFOrder());
}

@ApiOperation("删除")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/column_general_relation/delete/{parentUuid}/{uuid}")
public CommonResult delete(@PathVariable String parentUuid, @PathVariable String uuid) {
	Integer order = this.columnGeneralRelationRepository.order(uuid);
	this.columnGeneralRelationRepository.updateDelete(parentUuid, order);
	this.columnGeneralRelationRepository.deleteById(uuid);
	return CommonResult.successResult(1);
}

@ApiOperation("置顶")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/column_general_relation/top/{parentUuid}/{uuid}")
public CommonResult top(@PathVariable String parentUuid, @PathVariable String uuid) {
	Integer order = this.columnGeneralRelationRepository.order(uuid);
	this.columnGeneralRelationRepository.updateTop(parentUuid, order);
	this.columnGeneralRelationRepository.updateTop(uuid);
	return CommonResult.successResult(1);
}

@ApiOperation("上移")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/column_general_relation/up/{parentUuid}/{uuid}")
public CommonResult up(@PathVariable String parentUuid, @PathVariable String uuid) {
	Integer order = this.columnGeneralRelationRepository.order(uuid);
	this.columnGeneralRelationRepository.updateUp(parentUuid, order);
	this.columnGeneralRelationRepository.updateUp(uuid);
	return CommonResult.successResult(1);
}

@ApiOperation("下移")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/column_general_relation/down/{parentUuid}/{uuid}")
public CommonResult down(@PathVariable String parentUuid, @PathVariable String uuid) {
	Integer order = this.columnGeneralRelationRepository.order(uuid);
	this.columnGeneralRelationRepository.updateDown(parentUuid, order);
	this.columnGeneralRelationRepository.updateDown(uuid);
	return CommonResult.successResult(1);
}
}
