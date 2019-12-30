package com.ijianjian.game.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ijianjian.core.common.constant.ResultType.CommonError;
import com.ijianjian.core.common.object.CommonResult;
import com.ijianjian.file.service.FileInfoService;
import com.ijianjian.file.util.FieldConstant.FileType;
import com.ijianjian.game.domain.dto.ColumnAppCreateDTO_001;
import com.ijianjian.game.domain.dto.ColumnAppCreateDTO_002;
import com.ijianjian.game.domain.dto.ColumnAppSearchDTO;
import com.ijianjian.game.domain.po.ColumnApp;
import com.ijianjian.game.domain.po.ColumnAppDetail;
import com.ijianjian.game.domain.po.ColumnAppGame;
import com.ijianjian.game.domain.po.GameInfo;
import com.ijianjian.game.domain.po.GameInfoDetail;
import com.ijianjian.game.domain.repository.ColumnAppDetailRepository;
import com.ijianjian.game.domain.repository.ColumnAppGameRepository;
import com.ijianjian.game.domain.repository.ColumnAppRepository;
import com.ijianjian.game.domain.repository.GameInfoDetailRepository;
import com.ijianjian.game.domain.vo.ColumnAppVO_002;
import com.ijianjian.game.util.DomainFactory;
import com.ijianjian.game.util.FieldConstant.GameInfoStatus;
import com.ijianjian.game.util.LocalUser;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "应用栏目")
@RestController
public class ColumnAppService implements LocalUser {
private final ColumnAppRepository columnAppRepository;
private final ColumnAppDetailRepository columnAppDetailRepository;
private final ColumnAppGameRepository columnAppGameRepository;
private final GameInfoDetailRepository gameInfoDetailRepository;
private final FileInfoService fileInfoService;

public ColumnAppService(ColumnAppRepository columnAppRepository, ColumnAppDetailRepository columnAppDetailRepository, ColumnAppGameRepository columnAppGameRepository, GameInfoDetailRepository gameInfoDetailRepository, FileInfoService fileInfoService) {
	this.columnAppRepository = columnAppRepository;
	this.columnAppDetailRepository = columnAppDetailRepository;
	this.columnAppGameRepository = columnAppGameRepository;
	this.gameInfoDetailRepository = gameInfoDetailRepository;
	this.fileInfoService = fileInfoService;
}

@ApiOperation("查询")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@GetMapping("v1/column_app")
public CommonResult query(ColumnAppSearchDTO dto) {
	dto.init();
	Page<ColumnApp> page = this.columnAppRepository.query(dto);
	if (page == null || page.getTotalElements() == 0)
		return CommonResult.errorResult(CommonError.list_empty);
	return CommonResult.successResult(new PageImpl<>(DomainFactory._2ColumnAppVO_001(page.getContent()), page.getPageable(), page.getTotalElements()));
}

@ApiOperation("保存应用栏目信息及默认语言(文件参数 background)")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/column_app")
public CommonResult save(ColumnAppCreateDTO_001 dto, HttpServletRequest request) {
	if (Strings.isNullOrEmpty(dto.getName()) || (Strings.isNullOrEmpty(dto.getUuid()) && Strings.isNullOrEmpty(dto.getLanguageNumber())))
		return CommonResult.errorResult(CommonError.param_error);

	ColumnApp po;
	if (Strings.isNullOrEmpty(dto.getUuid())) {
		po = ColumnApp.builder().createUser(localCore()).build();
	} else {
		Optional<ColumnApp> uOptional = this.columnAppRepository.findById(dto.getUuid());
		if (!uOptional.isPresent())
			return CommonResult.errorResult(CommonError.data_not_exist);
		po = uOptional.get();
	}

	if (!Strings.isNullOrEmpty(dto.getLanguageNumber()))
		po.setFLanguageDefault(dto.getLanguageNumber());

	if (!Strings.isNullOrEmpty(dto.getName()))
		po.setFName(dto.getName());
	if (!Strings.isNullOrEmpty(dto.getDetail()))
		po.setFDetail(dto.getDetail());
	po.setFOrder(dto.getOrder());

	List<MultipartFile> apk = ((MultipartHttpServletRequest) request).getFiles("background");
	if (!apk.isEmpty())
		po.setFBackground(fileInfoService.upload1(apk, FileType.column_app_background).getFilePath());
	this.columnAppRepository.save(po);
	return CommonResult.successResult(po.getFName());
}

@ApiOperation("保存其他語言(文件参数 background)")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/column_app/language")
public CommonResult saveDetail(ColumnAppCreateDTO_002 dto, HttpServletRequest request) {
	if (Strings.isNullOrEmpty(dto.getUuid()) || Strings.isNullOrEmpty(dto.getLanguageNumber()))
		return CommonResult.errorResult(CommonError.param_error);
	Optional<ColumnApp> gOptional = this.columnAppRepository.findById(dto.getUuid());
	if (gOptional.isPresent()) {
		if (gOptional.get().getFLanguageDefault().equals(dto.getLanguageNumber())) {
			return CommonResult.errorResult(CommonError.param_error);
		}
	} else {
		return CommonResult.errorResult(CommonError.data_not_exist);
	}

	Optional<ColumnAppDetail> gameInfoDetailOptional = this.columnAppDetailRepository.findByColumnApp_fUuidAndFLanguageNumber(dto.getUuid(), dto.getLanguageNumber());
	ColumnAppDetail po;
	if (gameInfoDetailOptional.isPresent()) {
		po = gameInfoDetailOptional.get();
	} else {
		po = ColumnAppDetail.builder().fLanguageNumber(dto.getLanguageNumber()).columnApp(ColumnApp.builder().fUuid(dto.getUuid()).build()).build();
	}

	if (!Strings.isNullOrEmpty(dto.getName()))
		po.setFName(dto.getName());

	List<MultipartFile> apk = ((MultipartHttpServletRequest) request).getFiles("background");
	if (!apk.isEmpty())
		po.setFBackground(fileInfoService.upload1(apk, FileType.column_app_background).getFilePath());

	this.columnAppDetailRepository.save(po);
	return CommonResult.successResult(po.getFName());
}

@ApiOperation("详情")
@GetMapping("v1/column_app/{uuid}/{language}")
public CommonResult one(@PathVariable String uuid, @PathVariable String language) {
	Optional<ColumnApp> gameInfoOptional = this.columnAppRepository.findById(uuid);
	if (gameInfoOptional.isPresent()) {
		ColumnApp po1 = gameInfoOptional.get();
		ColumnAppVO_002 vo = DomainFactory._2ColumnAppVO_002(po1);
		if (!po1.getFLanguageDefault().equals(language)) {
			Optional<ColumnAppDetail> gameInfoDetailOptional = this.columnAppDetailRepository.findByColumnApp_fUuidAndFLanguageNumber(uuid, language);
			if (gameInfoDetailOptional.isPresent()) {
				ColumnAppDetail po2 = gameInfoDetailOptional.get();
				if (!Strings.isNullOrEmpty(po2.getFName()))
					vo.setName(po2.getFName());
				if (!Strings.isNullOrEmpty(po2.getFBackground()))
					vo.setBackground(po2.getFBackground());
			}
		}
		return CommonResult.successResult(vo);
	}
	return CommonResult.errorResult(CommonError.data_not_exist);
}

@ApiOperation("详情其他語言")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@GetMapping("v1/column_app_detail/{uuid}/{language}")
public CommonResult oneDetail(@PathVariable String uuid, @PathVariable String language) {
	Optional<ColumnAppDetail> optional = this.columnAppDetailRepository.findByColumnApp_fUuidAndFLanguageNumber(uuid, language);
	if (optional.isPresent()) {
		return CommonResult.successResult(DomainFactory._2VO(optional.get()));
	} else {
		ColumnAppDetail gameInfoDetail = ColumnAppDetail.builder().fLanguageNumber(language).columnApp(ColumnApp.builder().fUuid(uuid).build()).build();
		this.columnAppDetailRepository.save(gameInfoDetail);
		return CommonResult.successResult(gameInfoDetail.getFUuid());
	}
}

@ApiOperation("删除")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/column_app/delete/{uuid}")
public CommonResult delete(@PathVariable String uuid) {
	this.columnAppRepository.delete(uuid, LocalDateTime.now());
	return CommonResult.successResult("1");
}

//@Cacheable(cacheNames = "column_app", key = "#language")
@GetMapping("v1/column_app/load/{language}")
public CommonResult load(@PathVariable String language) {
	List<Map<String, Object>> caList = Lists.newArrayList();
	List<ColumnApp> aList = this.columnAppRepository.findByFDeletedFalse(Sort.by("fOrder", "fName"));
	for (ColumnApp ca : aList) {
		Map<String, Object> caMap = Maps.newHashMap();
		if (!ca.getFLanguageDefault().equals(language)) {
			Optional<ColumnAppDetail> optional = this.columnAppDetailRepository.findByColumnApp_fUuidAndFLanguageNumber(ca.getFUuid(), language);
			if (optional.isPresent()) {
				ca.setFName(optional.get().getFName());
				ca.setFBackground(optional.get().getFBackground());
			}
		}
		caMap.put("uuid", ca.getFUuid());
		caMap.put("name", ca.getFName());
		caMap.put("background", ca.getFBackground());
		caMap.put("detail", ca.getFDetail());
		Page<ColumnAppGame> cagData = this.columnAppGameRepository.findByColumnApp_fUuid(ca.getFUuid(), PageRequest.of(0, 20, Sort.by("fOrder")));
		caMap.put("game_count", cagData.getTotalElements());

		List<Map<String, Object>> gameList = Lists.newArrayList();
		if (cagData.getTotalElements() != 0) {
			for (ColumnAppGame cag : cagData.getContent()) {
				GameInfo gameInfo = cag.getGameInfo();
				if (gameInfo.getStatus() == GameInfoStatus.shang_xian) {
					Map<String, Object> gameMap = Maps.newHashMap();
					if (!gameInfo.getFLanguageDefault().equals(language)) {
						Optional<GameInfoDetail> optional = this.gameInfoDetailRepository.findByGameInfo_fUuidAndFLanguageNumber(gameInfo.getFUuid(), language);
						if (optional.isPresent()) {
							gameInfo.setFName(optional.get().getFName());
						}
					}
					gameMap.put("uuid", gameInfo.getFUuid());
					gameMap.put("name", gameInfo.getFName());
					gameMap.put("icon", gameInfo.getFIcon());
					gameMap.put("score", gameInfo.getFScore());
					gameMap.put("apk_size", gameInfo.getFApkSize());
					gameMap.put("apk_name", gameInfo.getFApkName());
					gameMap.put("chargeType", gameInfo.getChargeType());
					gameMap.put("download_count", gameInfo.getFDownloadCount());
					gameList.add(gameMap);
				}
			}
		}
		caMap.put("game", gameList);
		caList.add(caMap);
	}
	return CommonResult.successResult(caList);
}

//@Cacheable(cacheNames = "column_app", key = "#language+'_'+#uuid+'_'+#page+'_'+#size")
@GetMapping("v1/column_app/load/{language}/{uuid}/{page}/{size}")
public CommonResult load(@PathVariable String language, @PathVariable String uuid, @PathVariable Integer page, @PathVariable Integer size) {
	Page<ColumnAppGame> cagData = this.columnAppGameRepository.findByColumnApp_fUuid(uuid, PageRequest.of(page, size, Sort.by("fOrder")));
	Map<String, Object> data = Maps.newHashMap();
	List<Map<String, Object>> list = Lists.newArrayList();
	if (cagData.getTotalElements() != 0) {
		for (ColumnAppGame cag : cagData.getContent()) {
			GameInfo gameInfo = cag.getGameInfo();
			if (gameInfo.getStatus() == GameInfoStatus.shang_xian) {
				Map<String, Object> gameMap = Maps.newHashMap();
				if (!gameInfo.getFLanguageDefault().equals(language)) {
					Optional<GameInfoDetail> optional = this.gameInfoDetailRepository.findByGameInfo_fUuidAndFLanguageNumber(gameInfo.getFUuid(), language);
					if (optional.isPresent()) {
						gameInfo.setFName(optional.get().getFName());
					}
				}
				gameMap.put("uuid", gameInfo.getFUuid());
				gameMap.put("name", gameInfo.getFName());
				gameMap.put("icon", gameInfo.getFIcon());
				gameMap.put("score", gameInfo.getFScore());
				gameMap.put("apk_size", gameInfo.getFApkSize());
				gameMap.put("apk_name", gameInfo.getFApkName());
				gameMap.put("chargeType", gameInfo.getChargeType());
				list.add(gameMap);
			}
		}
	}
	data.put("count", cagData.getTotalElements());
	data.put("game", list);
	return CommonResult.successResult(data);
}
}
