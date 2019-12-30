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
import com.ijianjian.game.domain.dto.ColumnGeneralGameSearchDTO;
import com.ijianjian.game.domain.po.ColumnGeneral;
import com.ijianjian.game.domain.po.ColumnGeneralGame;
import com.ijianjian.game.domain.po.GameInfo;
import com.ijianjian.game.domain.repository.ColumnGeneralGameRepository;
import com.ijianjian.game.util.DomainFactory;
import com.ijianjian.game.util.LocalUser;
import com.ijianjian.game.util.ResultType.ColumnGeneralGameError;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "普通栏目-游戏")
@RestController
public class ColumnGeneralGameService implements LocalUser {
private final ColumnGeneralGameRepository columnGeneralGameRepository;

public ColumnGeneralGameService(ColumnGeneralGameRepository columnGeneralGameRepository) {
	this.columnGeneralGameRepository = columnGeneralGameRepository;
}

@ApiOperation("查询")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@GetMapping("v1/column_general_game")
public CommonResult query(ColumnGeneralGameSearchDTO dto) {
	dto.init();
	Page<ColumnGeneralGame> page = this.columnGeneralGameRepository.query(dto);
	if (page == null || page.getTotalElements() == 0)
		return CommonResult.errorResult(CommonError.list_empty);
	return CommonResult.successResult(new PageImpl<>(DomainFactory._2ColumnGeneralGameVO(page.getContent()), page.getPageable(), page.getTotalElements()));
}

@ApiOperation("保存应用栏目游戏")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/column_general_game")
public CommonResult save(String columnGeneralUuid, String gameUuid) {
	if (Strings.isNullOrEmpty(columnGeneralUuid) || Strings.isNullOrEmpty(gameUuid))
		return CommonResult.errorResult(CommonError.param_error);
	if (!this.columnGeneralGameRepository.child(columnGeneralUuid))
		return CommonResult.errorResult(ColumnGeneralGameError.no_child_game);

	if (this.columnGeneralGameRepository.existsByGameInfo_fUuid(gameUuid))
		return CommonResult.errorResult(ColumnGeneralGameError.game_exists);

	Integer order = this.columnGeneralGameRepository.maxOrder(columnGeneralUuid);
	if (order == null) {
		order = 1;
	} else {
		order += 1;
	}
	ColumnGeneralGame po = ColumnGeneralGame.builder().fOrder(order).createUser(localCore()).columnGeneral(ColumnGeneral.builder().fUuid(columnGeneralUuid).build()).gameInfo(GameInfo.builder().fUuid(gameUuid).build()).build();
	this.columnGeneralGameRepository.save(po);
	return CommonResult.successResult(po.getFOrder());
}

@ApiOperation("删除")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/column_general_game/delete/{coulumnGeneralUuid}/{uuid}")
public CommonResult delete(@PathVariable String coulumnGeneralUuid, @PathVariable String uuid) {
	Integer order = this.columnGeneralGameRepository.order(uuid);
	this.columnGeneralGameRepository.updateDelete(coulumnGeneralUuid, order);
	this.columnGeneralGameRepository.deleteById(uuid);
	return CommonResult.successResult(1);
}

@ApiOperation("置顶")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/column_general_game/top/{columnGeneralUuid}/{uuid}")
public CommonResult top(@PathVariable String columnGeneralUuid, @PathVariable String uuid) {
	Integer order = this.columnGeneralGameRepository.order(uuid);
	this.columnGeneralGameRepository.updateTop(columnGeneralUuid, order);
	this.columnGeneralGameRepository.updateTop(uuid);
	return CommonResult.successResult(1);
}

@ApiOperation("上移")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/column_general_game/up/{columnGeneralUuid}/{uuid}")
public CommonResult up(@PathVariable String columnGeneralUuid, @PathVariable String uuid) {
	Integer order = this.columnGeneralGameRepository.order(uuid);
	this.columnGeneralGameRepository.updateUp(columnGeneralUuid, order);
	this.columnGeneralGameRepository.updateUp(uuid);
	return CommonResult.successResult(1);
}

@ApiOperation("下移")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/column_general_game/down/{columnGeneralUuid}/{uuid}")
public CommonResult down(@PathVariable String columnGeneralUuid, @PathVariable String uuid) {
	Integer order = this.columnGeneralGameRepository.order(uuid);
	this.columnGeneralGameRepository.updateDown(columnGeneralUuid, order);
	this.columnGeneralGameRepository.updateDown(uuid);
	return CommonResult.successResult(1);
}
}
