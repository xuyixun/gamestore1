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
import com.ijianjian.game.domain.dto.ColumnMarketingCreateDTO_001;
import com.ijianjian.game.domain.dto.ColumnMarketingCreateDTO_002;
import com.ijianjian.game.domain.dto.ColumnMarketingSearchDTO;
import com.ijianjian.game.domain.po.ColumnMarketing;
import com.ijianjian.game.domain.po.ColumnMarketingDetail;
import com.ijianjian.game.domain.po.ColumnMarketingGame;
import com.ijianjian.game.domain.po.GameInfo;
import com.ijianjian.game.domain.po.GameInfoDetail;
import com.ijianjian.game.domain.repository.ColumnMarketingDetailRepository;
import com.ijianjian.game.domain.repository.ColumnMarketingGameRepository;
import com.ijianjian.game.domain.repository.ColumnMarketingRepository;
import com.ijianjian.game.domain.repository.GameInfoDetailRepository;
import com.ijianjian.game.domain.vo.ColumnMarketingVO_002;
import com.ijianjian.game.util.DomainFactory;
import com.ijianjian.game.util.FieldConstant.GameInfoStatus;
import com.ijianjian.game.util.LocalUser;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "营销栏目")
@RestController
public class ColumnMarketingService implements LocalUser {
private final ColumnMarketingRepository columnMarketingRepository;
private final ColumnMarketingDetailRepository columnMarketingDetailRepository;
private final ColumnMarketingGameRepository columnMarketingGameRepository;
private final GameInfoDetailRepository gameInfoDetailRepository;
private final FileInfoService fileInfoService;

public ColumnMarketingService(ColumnMarketingRepository columnMarketingRepository, ColumnMarketingDetailRepository columnMarketingDetailRepository, ColumnMarketingGameRepository columnMarketingGameRepository, GameInfoDetailRepository gameInfoDetailRepository, FileInfoService fileInfoService) {
	this.columnMarketingRepository = columnMarketingRepository;
	this.columnMarketingDetailRepository = columnMarketingDetailRepository;
	this.columnMarketingGameRepository = columnMarketingGameRepository;
	this.gameInfoDetailRepository = gameInfoDetailRepository;
	this.fileInfoService = fileInfoService;
}

@ApiOperation("查询")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@GetMapping("v1/column_marketing")
public CommonResult query(ColumnMarketingSearchDTO dto) {
	dto.init();
	Page<ColumnMarketing> page = this.columnMarketingRepository.query(dto);
	if (page == null || page.getTotalElements() == 0)
		return CommonResult.errorResult(CommonError.list_empty);
	return CommonResult.successResult(new PageImpl<>(DomainFactory._2ColumnMarketingVO_001(page.getContent()), page.getPageable(), page.getTotalElements()));
}

@ApiOperation("保存应用栏目信息及默认语言(文件参数 background)")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/column_marketing")
public CommonResult save(ColumnMarketingCreateDTO_001 dto, HttpServletRequest request) {
	if (Strings.isNullOrEmpty(dto.getName()) || dto.getType() == null || (Strings.isNullOrEmpty(dto.getUuid()) && Strings.isNullOrEmpty(dto.getLanguageNumber())))
		return CommonResult.errorResult(CommonError.param_error);

	ColumnMarketing po;
	if (Strings.isNullOrEmpty(dto.getUuid())) {
		po = ColumnMarketing.builder().createUser(localCore()).fOrderHome(0).build();
	} else {
		Optional<ColumnMarketing> uOptional = this.columnMarketingRepository.findById(dto.getUuid());
		if (!uOptional.isPresent())
			return CommonResult.errorResult(CommonError.data_not_exist);
		po = uOptional.get();
	}
	po.setType(dto.getType());

	if (!Strings.isNullOrEmpty(dto.getLanguageNumber()))
		po.setFLanguageDefault(dto.getLanguageNumber());

	if (!Strings.isNullOrEmpty(dto.getName()))
		po.setFName(dto.getName());
	if (!Strings.isNullOrEmpty(dto.getDetail()))
		po.setFDetail(dto.getDetail());
	po.setFOrder(dto.getOrder());

	List<MultipartFile> icon = ((MultipartHttpServletRequest) request).getFiles("icon");
	if (!icon.isEmpty())
		po.setFIcon(fileInfoService.upload1(icon, FileType.column_general_background).getFilePath());

	List<MultipartFile> background = ((MultipartHttpServletRequest) request).getFiles("background");
	if (!background.isEmpty())
		po.setFBackground(fileInfoService.upload1(background, FileType.column_general_background).getFilePath());

	this.columnMarketingRepository.save(po);
	return CommonResult.successResult(po.getFName());
}

@ApiOperation("保存其他語言(文件参数 background)")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/column_marketing/language")
public CommonResult saveDetail(ColumnMarketingCreateDTO_002 dto, HttpServletRequest request) {
	if (Strings.isNullOrEmpty(dto.getUuid()) || Strings.isNullOrEmpty(dto.getLanguageNumber()))
		return CommonResult.errorResult(CommonError.param_error);
	Optional<ColumnMarketing> gOptional = this.columnMarketingRepository.findById(dto.getUuid());
	if (gOptional.isPresent()) {
		if (gOptional.get().getFLanguageDefault().equals(dto.getLanguageNumber())) {
			return CommonResult.errorResult(CommonError.param_error);
		}
	} else {
		return CommonResult.errorResult(CommonError.data_not_exist);
	}

	Optional<ColumnMarketingDetail> gameInfoDetailOptional = this.columnMarketingDetailRepository.findByColumnMarketing_fUuidAndFLanguageNumber(dto.getUuid(), dto.getLanguageNumber());
	ColumnMarketingDetail po;
	if (gameInfoDetailOptional.isPresent()) {
		po = gameInfoDetailOptional.get();
	} else {
		po = ColumnMarketingDetail.builder().fLanguageNumber(dto.getLanguageNumber()).columnMarketing(ColumnMarketing.builder().fUuid(dto.getUuid()).build()).build();
	}

	if (!Strings.isNullOrEmpty(dto.getName()))
		po.setFName(dto.getName());

	List<MultipartFile> icon = ((MultipartHttpServletRequest) request).getFiles("icon");
	if (!icon.isEmpty())
		po.setFBackground(fileInfoService.upload1(icon, FileType.column_general_background).getFilePath());

	List<MultipartFile> background = ((MultipartHttpServletRequest) request).getFiles("background");
	if (!background.isEmpty())
		po.setFBackground(fileInfoService.upload1(background, FileType.column_general_background).getFilePath());

	this.columnMarketingDetailRepository.save(po);
	return CommonResult.successResult(po.getFName());
}

@ApiOperation("详情")
@GetMapping("v1/column_marketing/{uuid}/{language}")
public CommonResult one(@PathVariable String uuid, @PathVariable String language) {
	Optional<ColumnMarketing> gameInfoOptional = this.columnMarketingRepository.findById(uuid);
	if (gameInfoOptional.isPresent()) {
		ColumnMarketing po1 = gameInfoOptional.get();
		ColumnMarketingVO_002 vo = DomainFactory._2ColumnMarketingVO_002(po1);
		if (!po1.getFLanguageDefault().equals(language)) {
			Optional<ColumnMarketingDetail> gameInfoDetailOptional = this.columnMarketingDetailRepository.findByColumnMarketing_fUuidAndFLanguageNumber(uuid, language);
			if (gameInfoDetailOptional.isPresent()) {
				ColumnMarketingDetail po2 = gameInfoDetailOptional.get();
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
@GetMapping("v1/column_marketing_detail/{uuid}/{language}")
public CommonResult oneDetail(@PathVariable String uuid, @PathVariable String language) {
	Optional<ColumnMarketingDetail> optional = this.columnMarketingDetailRepository.findByColumnMarketing_fUuidAndFLanguageNumber(uuid, language);
	if (optional.isPresent()) {
		return CommonResult.successResult(DomainFactory._2VO(optional.get()));
	} else {
		ColumnMarketingDetail gameInfoDetail = ColumnMarketingDetail.builder().fLanguageNumber(language).columnMarketing(ColumnMarketing.builder().fUuid(uuid).build()).build();
		this.columnMarketingDetailRepository.save(gameInfoDetail);
		return CommonResult.successResult(gameInfoDetail.getFUuid());
	}
}

@ApiOperation("删除")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/column_marketing/delete/{uuid}")
public CommonResult delete(@PathVariable String uuid) {
	Integer order = this.columnMarketingRepository.order(uuid);
	this.columnMarketingRepository.updateDelete(order);
	this.columnMarketingRepository.removeHome(uuid);
	this.columnMarketingRepository.delete(uuid, LocalDateTime.now());
	return CommonResult.successResult("1");
}

@ApiOperation("首页栏目")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@GetMapping("v1/column_marketing/home")
public CommonResult query() {
	List<ColumnMarketing> cmList = this.columnMarketingRepository.findByFDeletedFalseAndFOrderHomeNot(0, Sort.by("fOrderHome"));
	return CommonResult.successResult(DomainFactory._2ColumnMarketingVO_001(cmList));
}

@ApiOperation("保存首页栏目")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/column_marketing/order_home/{uuid}")
public CommonResult save(@PathVariable String uuid) {
	if (this.columnMarketingRepository.existsByFUuidAndFOrderHomeNot(uuid, 0)) {
		return CommonResult.successResult(0);
	}
	Integer order = this.columnMarketingRepository.maxOrder(uuid);
	if (order == null) {
		order = 1;
	} else {
		order += 1;
	}
	this.columnMarketingRepository.orderHome(uuid, order);
	return CommonResult.successResult(1);
}

@ApiOperation("移除首页栏目")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/column_marketing/remove/{uuid}")
public CommonResult remove(@PathVariable String uuid) {
	Integer order = this.columnMarketingRepository.order(uuid);
	this.columnMarketingRepository.updateDelete(order);
	this.columnMarketingRepository.removeHome(uuid);
	return CommonResult.successResult(1);
}

@ApiOperation("置顶")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/column_marketing/top/{uuid}")
public CommonResult top(@PathVariable String uuid) {
	Integer order = this.columnMarketingRepository.order(uuid);
	this.columnMarketingRepository.updateTop(order);
	this.columnMarketingRepository.updateTop(uuid);
	return CommonResult.successResult(1);
}

@ApiOperation("上移")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/column_marketing/up/{uuid}")
public CommonResult up(@PathVariable String uuid) {
	Integer order = this.columnMarketingRepository.order(uuid);
	this.columnMarketingRepository.updateUp(order);
	this.columnMarketingRepository.updateUp(uuid);
	return CommonResult.successResult(1);
}

@ApiOperation("下移")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/column_marketing/down/{uuid}")
public CommonResult down(@PathVariable String uuid) {
	Integer order = this.columnMarketingRepository.order(uuid);
	this.columnMarketingRepository.updateDown(order);
	this.columnMarketingRepository.updateDown(uuid);
	return CommonResult.successResult(1);
}

@GetMapping("v1/column_marketing_game/load/{language}/{uuid}/{page}/{size}")
public CommonResult load(@PathVariable String language, @PathVariable String uuid, @PathVariable Integer page, @PathVariable Integer size) {
	Page<ColumnMarketingGame> cagData = this.columnMarketingGameRepository.findByColumnMarketing_fUuid(uuid, PageRequest.of(page, size, Sort.by("fOrder")));
	Map<String, Object> data = Maps.newHashMap();
	List<Map<String, Object>> list = Lists.newArrayList();
	if (cagData.getTotalElements() != 0) {
		for (ColumnMarketingGame cag : cagData.getContent()) {
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
				list.add(gameMap);
			}
		}
	}
	data.put("count", cagData.getTotalElements());
	data.put("game", list);
	return CommonResult.successResult(data);
}
}
