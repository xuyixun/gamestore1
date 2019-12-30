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
import com.ijianjian.game.domain.dto.ColumnAppGameSearchDTO;
import com.ijianjian.game.domain.po.ColumnApp;
import com.ijianjian.game.domain.po.ColumnAppGame;
import com.ijianjian.game.domain.po.GameInfo;
import com.ijianjian.game.domain.repository.ColumnAppGameRepository;
import com.ijianjian.game.util.DomainFactory;
import com.ijianjian.game.util.ResultType.ColumnAppGameError;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "应用栏目-游戏")
@RestController
public class ColumnAppGameService implements com.ijianjian.game.util.LocalUser {
private final ColumnAppGameRepository columnAppGameRepository;

public ColumnAppGameService(ColumnAppGameRepository columnAppGameRepository) {
	this.columnAppGameRepository = columnAppGameRepository;
}

@ApiOperation("查询")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@GetMapping("v1/column_app_game")
public CommonResult query(ColumnAppGameSearchDTO dto) {
	dto.init();
	Page<ColumnAppGame> page = this.columnAppGameRepository.query(dto);
	if (page == null || page.getTotalElements() == 0)
		return CommonResult.errorResult(CommonError.list_empty);
	return CommonResult.successResult(new PageImpl<>(DomainFactory._2ColumnAppGameVO(page.getContent()), page.getPageable(), page.getTotalElements()));
}

@ApiOperation("保存应用栏目游戏")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/column_app_game")
public CommonResult save(String columnAppUuid, String gameUuid) {
	if (Strings.isNullOrEmpty(columnAppUuid) || Strings.isNullOrEmpty(gameUuid))
		return CommonResult.errorResult(CommonError.param_error);
	if (this.columnAppGameRepository.existsByColumnApp_fUuidAndGameInfo_fUuid(columnAppUuid, gameUuid))
		return CommonResult.errorResult(ColumnAppGameError.game_exists);

	Integer order = this.columnAppGameRepository.maxOrder(columnAppUuid);
	if (order == null) {
		order = 1;
	} else {
		order += 1;
	}
	ColumnAppGame po = ColumnAppGame.builder().fOrder(order).createUser(localCore()).columnApp(ColumnApp.builder().fUuid(columnAppUuid).build()).gameInfo(GameInfo.builder().fUuid(gameUuid).build()).build();
	this.columnAppGameRepository.save(po);
	return CommonResult.successResult(po.getFOrder());
}

@ApiOperation("删除")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/column_app_game/delete/{coulumnAppUuid}/{uuid}")
public CommonResult delete(@PathVariable String coulumnAppUuid, @PathVariable String uuid) {
	Integer order = this.columnAppGameRepository.order(uuid);
	this.columnAppGameRepository.updateDelete(coulumnAppUuid, order);
	this.columnAppGameRepository.deleteByColumnApp_fUuidAndGameInfo_fUuid(coulumnAppUuid, uuid);
	return CommonResult.successResult(1);
}

@ApiOperation("置顶")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/column_app_game/top/{columnAppUuid}/{uuid}")
public CommonResult top(@PathVariable String columnAppUuid, @PathVariable String uuid) {
	Integer order = this.columnAppGameRepository.order(uuid);
	this.columnAppGameRepository.updateTop(columnAppUuid, order);
	this.columnAppGameRepository.updateTop(uuid);
	return CommonResult.successResult(1);
}

@ApiOperation("上移")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/column_app_game/up/{columnAppUuid}/{uuid}")
public CommonResult up(@PathVariable String columnAppUuid, @PathVariable String uuid) {
	Integer order = this.columnAppGameRepository.order(uuid);
	this.columnAppGameRepository.updateUp(columnAppUuid, order);
	this.columnAppGameRepository.updateUp(uuid);
	return CommonResult.successResult(1);
}

@ApiOperation("下移")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/column_app_game/down/{columnAppUuid}/{uuid}")
public CommonResult down(@PathVariable String columnAppUuid, @PathVariable String uuid) {
	Integer order = this.columnAppGameRepository.order(uuid);
	this.columnAppGameRepository.updateDown(columnAppUuid, order);
	this.columnAppGameRepository.updateDown(uuid);
	return CommonResult.successResult(1);
}
}
