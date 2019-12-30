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
import com.ijianjian.game.domain.dto.ColumnMarketingGameSearchDTO;
import com.ijianjian.game.domain.po.ColumnMarketing;
import com.ijianjian.game.domain.po.ColumnMarketingGame;
import com.ijianjian.game.domain.po.GameInfo;
import com.ijianjian.game.domain.repository.ColumnMarketingGameRepository;
import com.ijianjian.game.util.DomainFactory;
import com.ijianjian.game.util.LocalUser;
import com.ijianjian.game.util.ResultType.ColumnMarketingError;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "营销栏目-游戏")
@RestController
public class ColumnMarketingGameService implements LocalUser {
private final ColumnMarketingGameRepository columnMarketingGameRepository;

public ColumnMarketingGameService(ColumnMarketingGameRepository columnMarketingGameRepository) {
	this.columnMarketingGameRepository = columnMarketingGameRepository;
}

@ApiOperation("查询")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@GetMapping("v1/column_marketing_game")
public CommonResult query(ColumnMarketingGameSearchDTO dto) {
	dto.init();
	Page<ColumnMarketingGame> page = this.columnMarketingGameRepository.query(dto);
	if (page == null || page.getTotalElements() == 0)
		return CommonResult.errorResult(CommonError.list_empty);
	return CommonResult.successResult(new PageImpl<>(DomainFactory._2ColumnMarketingGameVO(page.getContent()), page.getPageable(), page.getTotalElements()));
}

@ApiOperation("保存应用栏目游戏")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/column_marketing_game")
public CommonResult save(String columnMarketingUuid, String gameUuid) {
	if (Strings.isNullOrEmpty(columnMarketingUuid) || Strings.isNullOrEmpty(gameUuid))
		return CommonResult.errorResult(CommonError.param_error);
	if (this.columnMarketingGameRepository.existsByColumnMarketing_fUuidAndGameInfo_fUuid(columnMarketingUuid, gameUuid)) {
		return CommonResult.errorResult(ColumnMarketingError.game_exists);
	}

	Integer order = this.columnMarketingGameRepository.maxOrder(columnMarketingUuid);
	if (order == null) {
		order = 1;
	} else {
		order += 1;
	}
	ColumnMarketingGame po = ColumnMarketingGame.builder().fOrder(order).createUser(localCore()).columnMarketing(ColumnMarketing.builder().fUuid(columnMarketingUuid).build()).gameInfo(GameInfo.builder().fUuid(gameUuid).build()).build();
	this.columnMarketingGameRepository.save(po);
	return CommonResult.successResult(po.getFOrder());
}

@ApiOperation("删除")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/column_marketing_game/delete/{columnMarketingUuid}/{uuid}")
public CommonResult delete(@PathVariable String columnMarketingUuid, @PathVariable String uuid) {
	Integer order = this.columnMarketingGameRepository.order(uuid);
	this.columnMarketingGameRepository.updateDelete(columnMarketingUuid, order);
	this.columnMarketingGameRepository.deleteById(uuid);
	return CommonResult.successResult(1);
}

@ApiOperation("置顶")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/column_marketing_game/top/{columnMarketingUuid}/{uuid}")
public CommonResult top(@PathVariable String columnMarketingUuid, @PathVariable String uuid) {
	Integer order = this.columnMarketingGameRepository.order(uuid);
	this.columnMarketingGameRepository.updateTop(columnMarketingUuid, order);
	this.columnMarketingGameRepository.updateTop(uuid);
	return CommonResult.successResult(1);
}

@ApiOperation("上移")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/column_marketing_game/up/{columnMarketingUuid}/{uuid}")
public CommonResult up(@PathVariable String columnMarketingUuid, @PathVariable String uuid) {
	Integer order = this.columnMarketingGameRepository.order(uuid);
	this.columnMarketingGameRepository.updateUp(columnMarketingUuid, order);
	this.columnMarketingGameRepository.updateUp(uuid);
	return CommonResult.successResult(1);
}

@ApiOperation("下移")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/column_marketing_game/down/{columnMarketingUuid}/{uuid}")
public CommonResult down(@PathVariable String columnMarketingUuid, @PathVariable String uuid) {
	Integer order = this.columnMarketingGameRepository.order(uuid);
	this.columnMarketingGameRepository.updateDown(columnMarketingUuid, order);
	this.columnMarketingGameRepository.updateDown(uuid);
	return CommonResult.successResult(1);
}
}
