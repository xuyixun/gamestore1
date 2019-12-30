package com.ijianjian.game.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ijianjian.core.common.constant.ResultType.CommonError;
import com.ijianjian.core.common.object.CommonResult;
import com.ijianjian.game.domain.dto.MyGameSearchDTO;
import com.ijianjian.game.domain.po.GameInfoDetail;
import com.ijianjian.game.domain.po.MyGame;
import com.ijianjian.game.domain.repository.GameInfoDetailRepository;
import com.ijianjian.game.domain.repository.MyGameRepository;
import com.ijianjian.game.domain.vo.MyGameVO;
import com.ijianjian.game.util.DomainFactory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "我的游戏")
@RestController
public class MyGameService {
private final MyGameRepository myGameRepository;
private final GameInfoDetailRepository gameInfoDetailRepository;

public MyGameService(MyGameRepository myGameRepository, GameInfoDetailRepository gameInfoDetailRepository) {
	this.myGameRepository = myGameRepository;
	this.gameInfoDetailRepository = gameInfoDetailRepository;
}

@ApiOperation("查询")
@GetMapping("v1/my_game")
public CommonResult query(MyGameSearchDTO dto) {
	if (Strings.isNullOrEmpty(dto.getUserUuid()) || Strings.isNullOrEmpty(dto.getLanguage())) {
		return CommonResult.errorResult(CommonError.param_error);
	}
	dto.init();
	Page<MyGame> page = this.myGameRepository.query(dto);
	if (page == null || page.getTotalElements() == 0) {
		return CommonResult.errorResult(CommonError.list_empty);
	}
	List<MyGameVO> list = Lists.newArrayList();
	page.getContent().forEach(channel -> {
		MyGameVO vo = DomainFactory._2VO(channel);
		if (channel.getGameInfo() != null) {
			Optional<GameInfoDetail> gido = this.gameInfoDetailRepository.findByGameInfo_fUuidAndFLanguageNumber(channel.getGameInfo().getFUuid(), dto.getLanguage());
			if (gido.isPresent()) {
				vo.setGameInfoName(gido.get().getFName());
			}
		}
		list.add(vo);
	});
	return CommonResult.successResult(new PageImpl<>(list, page.getPageable(), page.getTotalElements()));
}

@ApiOperation("删除")
@PostMapping("v1/my_game/delete/{uuid}")
public CommonResult delete(@PathVariable String uuid) {
	this.myGameRepository.deleteById(uuid);
	return CommonResult.successResult(1);
}

@ApiOperation("删除")
@PostMapping("v1/my_game/delete")
public CommonResult deleteA(String uuids) {
	for (String uuid : uuids.split(",")) {
		this.delete(uuid);
	}
	return CommonResult.successResult(1);
}
}
