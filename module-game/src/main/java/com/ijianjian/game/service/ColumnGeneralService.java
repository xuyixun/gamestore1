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
import com.ijianjian.game.domain.dto.ColumnGeneralCreateDTO_001;
import com.ijianjian.game.domain.dto.ColumnGeneralCreateDTO_002;
import com.ijianjian.game.domain.dto.ColumnGeneralSearchDTO;
import com.ijianjian.game.domain.po.ColumnGeneral;
import com.ijianjian.game.domain.po.ColumnGeneralDetail;
import com.ijianjian.game.domain.po.ColumnGeneralGame;
import com.ijianjian.game.domain.po.ColumnGeneralRelation;
import com.ijianjian.game.domain.po.GameInfo;
import com.ijianjian.game.domain.po.GameInfoDetail;
import com.ijianjian.game.domain.repository.AdRepository;
import com.ijianjian.game.domain.repository.ChannelRepository;
import com.ijianjian.game.domain.repository.ColumnGeneralDetailRepository;
import com.ijianjian.game.domain.repository.ColumnGeneralGameRepository;
import com.ijianjian.game.domain.repository.ColumnGeneralRelationRepository;
import com.ijianjian.game.domain.repository.ColumnGeneralRepository;
import com.ijianjian.game.domain.repository.ColumnMarketingColumnRepository;
import com.ijianjian.game.domain.repository.GameInfoDetailRepository;
import com.ijianjian.game.domain.vo.ColumnGeneralVO_002;
import com.ijianjian.game.util.DomainFactory;
import com.ijianjian.game.util.FieldConstant.GameInfoStatus;
import com.ijianjian.game.util.LocalUser;
import com.ijianjian.game.util.ResultType.ColumnGeneralGameError;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "普通栏目")
@RestController
public class ColumnGeneralService implements LocalUser {
private final ColumnGeneralRepository columnGeneralRepository;
private final ColumnGeneralDetailRepository columnGeneralDetailRepository;
private final ColumnGeneralGameRepository columnGeneralGameRepository;
private final ColumnGeneralRelationRepository columnGeneralRelationRepository;
private final GameInfoDetailRepository gameInfoDetailRepository;
private final AdRepository adRepository;
private final ColumnMarketingColumnRepository columnMarketingColumnRepository;
private final ChannelRepository channelRepository;
private final FileInfoService fileInfoService;

public ColumnGeneralService(ColumnGeneralRepository columnGeneralRepository, ColumnGeneralDetailRepository columnGeneralDetailRepository, ColumnGeneralGameRepository columnGeneralGameRepository, ColumnGeneralRelationRepository columnGeneralRelationRepository,
  GameInfoDetailRepository gameInfoDetailRepository, AdRepository adRepository, ColumnMarketingColumnRepository columnMarketingColumnRepository, ChannelRepository channelRepository, FileInfoService fileInfoService) {
	super();
	this.columnGeneralRepository = columnGeneralRepository;
	this.columnGeneralDetailRepository = columnGeneralDetailRepository;
	this.columnGeneralGameRepository = columnGeneralGameRepository;
	this.columnGeneralRelationRepository = columnGeneralRelationRepository;
	this.gameInfoDetailRepository = gameInfoDetailRepository;
	this.adRepository = adRepository;
	this.columnMarketingColumnRepository = columnMarketingColumnRepository;
	this.channelRepository = channelRepository;
	this.fileInfoService = fileInfoService;
}

@ApiOperation("查询")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@GetMapping("v1/column_general")
public CommonResult query(ColumnGeneralSearchDTO dto) {
	dto.init();
	Page<ColumnGeneral> page = this.columnGeneralRepository.query(dto);
	if (page == null || page.getTotalElements() == 0)
		return CommonResult.errorResult(CommonError.list_empty);
	return CommonResult.successResult(new PageImpl<>(DomainFactory._2ColumnGeneralVO_001(page.getContent()), page.getPageable(), page.getTotalElements()));
}

@ApiOperation("保存应用栏目信息及默认语言(文件参数 background)")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/column_general")
public CommonResult save(ColumnGeneralCreateDTO_001 dto, HttpServletRequest request) {
	if (Strings.isNullOrEmpty(dto.getName()) || (Strings.isNullOrEmpty(dto.getUuid()) && Strings.isNullOrEmpty(dto.getLanguageNumber())))
		return CommonResult.errorResult(CommonError.param_error);

	ColumnGeneral po;
	if (Strings.isNullOrEmpty(dto.getUuid())) {
		po = ColumnGeneral.builder().createUser(localCore()).build();
	} else {
		Optional<ColumnGeneral> uOptional = this.columnGeneralRepository.findById(dto.getUuid());
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

	po.setFChild(dto.getChild());

	List<MultipartFile> icon = ((MultipartHttpServletRequest) request).getFiles("icon");
	if (!icon.isEmpty())
		po.setFIcon(fileInfoService.upload1(icon, FileType.column_general_background).getFilePath());

	List<MultipartFile> background = ((MultipartHttpServletRequest) request).getFiles("background");
	if (!background.isEmpty())
		po.setFBackground(fileInfoService.upload1(background, FileType.column_general_background).getFilePath());

	this.columnGeneralRepository.save(po);
	return CommonResult.successResult(po.getFUuid());
}

@ApiOperation("保存其他語言(文件参数 background)")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/column_general/language")
public CommonResult saveDetail(ColumnGeneralCreateDTO_002 dto, HttpServletRequest request) {
	if (Strings.isNullOrEmpty(dto.getUuid()) || Strings.isNullOrEmpty(dto.getLanguageNumber()))
		return CommonResult.errorResult(CommonError.param_error);
	Optional<ColumnGeneral> gOptional = this.columnGeneralRepository.findById(dto.getUuid());
	if (gOptional.isPresent()) {
		if (gOptional.get().getFLanguageDefault().equals(dto.getLanguageNumber())) {
			return CommonResult.errorResult(CommonError.param_error);
		}
	} else {
		return CommonResult.errorResult(CommonError.data_not_exist);
	}

	Optional<ColumnGeneralDetail> gameInfoDetailOptional = this.columnGeneralDetailRepository.findByColumnGeneral_fUuidAndFLanguageNumber(dto.getUuid(), dto.getLanguageNumber());
	ColumnGeneralDetail po;
	if (gameInfoDetailOptional.isPresent()) {
		po = gameInfoDetailOptional.get();
	} else {
		po = ColumnGeneralDetail.builder().fLanguageNumber(dto.getLanguageNumber()).columnGeneral(ColumnGeneral.builder().fUuid(dto.getUuid()).build()).build();
	}

	if (!Strings.isNullOrEmpty(dto.getName()))
		po.setFName(dto.getName());

	List<MultipartFile> icon = ((MultipartHttpServletRequest) request).getFiles("icon");
	if (!icon.isEmpty())
		po.setFBackground(fileInfoService.upload1(icon, FileType.column_general_background).getFilePath());

	List<MultipartFile> background = ((MultipartHttpServletRequest) request).getFiles("background");
	if (!background.isEmpty())
		po.setFBackground(fileInfoService.upload1(background, FileType.column_general_background).getFilePath());

	this.columnGeneralDetailRepository.save(po);
	return CommonResult.successResult(po.getFName());
}

@ApiOperation("详情")
@GetMapping("v1/column_general/{uuid}/{language}")
public CommonResult one(@PathVariable String uuid, @PathVariable String language) {
	Optional<ColumnGeneral> gameInfoOptional = this.columnGeneralRepository.findById(uuid);
	if (gameInfoOptional.isPresent()) {
		ColumnGeneral po1 = gameInfoOptional.get();
		ColumnGeneralVO_002 vo = DomainFactory._2ColumnGeneralVO_002(po1);
		if (!po1.getFLanguageDefault().equals(language)) {
			Optional<ColumnGeneralDetail> gameInfoDetailOptional = this.columnGeneralDetailRepository.findByColumnGeneral_fUuidAndFLanguageNumber(uuid, language);
			if (gameInfoDetailOptional.isPresent()) {
				ColumnGeneralDetail po2 = gameInfoDetailOptional.get();
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

@ApiOperation("普通栏目背景图片")
@GetMapping("v1/column_general/back_ground/{uuid}")
public CommonResult adPicture(@PathVariable String uuid) {
	Optional<ColumnGeneral> gameInfoOptional = this.columnGeneralRepository.findById(uuid);
	if (gameInfoOptional.isPresent()) {
		return CommonResult.successResult(gameInfoOptional.get().getFBackground());
	}
	return CommonResult.errorResult(CommonError.data_not_exist);
}

@ApiOperation("详情其他語言")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@GetMapping("v1/column_general_detail/{uuid}/{language}")
public CommonResult oneDetail(@PathVariable String uuid, @PathVariable String language) {
	Optional<ColumnGeneralDetail> optional = this.columnGeneralDetailRepository.findByColumnGeneral_fUuidAndFLanguageNumber(uuid, language);
	if (optional.isPresent()) {
		return CommonResult.successResult(DomainFactory._2VO(optional.get()));
	} else {
		ColumnGeneralDetail gameInfoDetail = ColumnGeneralDetail.builder().fLanguageNumber(language).columnGeneral(ColumnGeneral.builder().fUuid(uuid).build()).build();
		this.columnGeneralDetailRepository.save(gameInfoDetail);
		return CommonResult.successResult(gameInfoDetail.getFUuid());
	}
}

@ApiOperation("删除")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/column_general/delete/{uuid}")
public CommonResult delete(@PathVariable String uuid) {
	if (this.adRepository.existsByFDeletedFalseAndColumnGeneral_fUuid(uuid)) {
		return CommonResult.errorResult(ColumnGeneralGameError.bind_ad);
	}
	if (this.channelRepository.existsByFDeletedFalseAndColumnGeneral_fUuid(uuid)) {
		return CommonResult.errorResult(ColumnGeneralGameError.bind_channel);
	}
	if (this.columnMarketingColumnRepository.existsByColumnGeneral_fUuid(uuid)) {
		return CommonResult.errorResult(ColumnGeneralGameError.bind_column_marking);
	}
	if (this.columnGeneralRelationRepository.existsByParent_fUuid(uuid)) {
		return CommonResult.errorResult(ColumnGeneralGameError.exists_child);
	}
	Integer order = this.columnGeneralRelationRepository.order(uuid);
	if (order != null) {
		String parentUuid = this.columnGeneralRelationRepository.parentUuid(uuid);
		this.columnGeneralRelationRepository.updateDelete(parentUuid, order);
		this.columnGeneralRelationRepository.deleteById(uuid);
	}
	this.columnGeneralRepository.delete(uuid, LocalDateTime.now());
	return CommonResult.successResult("1");
}

//@Cacheable(cacheNames = "column_general", key = "#language")
@GetMapping("v1/column_general/load/{language}")
public CommonResult load(@PathVariable String language) {
	List<Map<String, Object>> cgList = Lists.newArrayList();
	List<ColumnGeneral> aList = this.columnGeneralRepository.findByFDeletedFalseAndFChildFalse(Sort.by("fOrder", "fName"));
	for (ColumnGeneral cg : aList) {
		Map<String, Object> caMap = Maps.newHashMap();
		if (!cg.getFLanguageDefault().equals(language)) {
			Optional<ColumnGeneralDetail> optional = this.columnGeneralDetailRepository.findByColumnGeneral_fUuidAndFLanguageNumber(cg.getFUuid(), language);
			if (optional.isPresent()) {
				cg.setFName(optional.get().getFName());
				cg.setFBackground(optional.get().getFBackground());
			}
		}
		caMap.put("uuid", cg.getFUuid());
		caMap.put("name", cg.getFName());
		caMap.put("icon", cg.getFIcon());
		caMap.put("background", cg.getFBackground());
		caMap.put("detail", cg.getFDetail());
		List<ColumnGeneralRelation> cgr = this.columnGeneralRelationRepository.findByParent_fUuid(cg.getFUuid(), Sort.by("fOrder"));
		caMap.put("column_count", cgr.size());

		List<Map<String, Object>> gameList = Lists.newArrayList();
		if (!cgr.isEmpty()) {
			for (ColumnGeneralRelation cag : cgr) {
				ColumnGeneral columnGeneral = cag.getChild();
				Map<String, Object> columnGeneralMap = Maps.newHashMap();
				if (!columnGeneral.getFLanguageDefault().equals(language)) {
					Optional<ColumnGeneralDetail> optional = this.columnGeneralDetailRepository.findByColumnGeneral_fUuidAndFLanguageNumber(columnGeneral.getFUuid(), language);
					if (optional.isPresent()) {
						columnGeneral.setFName(optional.get().getFName());
						columnGeneral.setFBackground(optional.get().getFBackground());
					}
				}
				columnGeneralMap.put("uuid", columnGeneral.getFUuid());
				columnGeneralMap.put("name", columnGeneral.getFName());
				columnGeneralMap.put("icon", columnGeneral.getFIcon());
				columnGeneralMap.put("background", columnGeneral.getFBackground());
				columnGeneralMap.put("detail", columnGeneral.getFDetail());
				gameList.add(columnGeneralMap);
			}
		}
		caMap.put("column", gameList);
		cgList.add(caMap);
	}
	return CommonResult.successResult(cgList);
}

//@Cacheable(cacheNames = "column_general", key = "#language+'_'+#uuid")
@GetMapping("v1/column_general/load/{language}/{uuid}")
public CommonResult load(@PathVariable String language, @PathVariable String uuid) {
	List<ColumnGeneralRelation> cgrList = this.columnGeneralRelationRepository.findByParent_fUuid(uuid, Sort.by("fOrder"));

	List<Map<String, Object>> cgList = Lists.newArrayList();
	if (!cgrList.isEmpty()) {
		for (ColumnGeneralRelation cgr : cgrList) {

			Page<ColumnGeneralGame> cagData = this.columnGeneralGameRepository.findByColumnGeneral_fUuid(cgr.getChild().getFUuid(), PageRequest.of(0, 4, Sort.by("fOrder")));
			Map<String, Object> data = Maps.newHashMap();
			List<Map<String, Object>> list = Lists.newArrayList();
			for (ColumnGeneralGame cag : cagData.getContent()) {
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
					list.add(gameMap);
				}
			}

			data.put("count", cagData.getTotalElements());
			data.put("game", list);
			data.put("name", cgr.getChild().getFName());
			data.put("uuid", cgr.getChild().getFUuid());

			cgList.add(data);
		}
	}
	return CommonResult.successResult(cgList);
}

//@Cacheable(cacheNames = "column_general", key = "#language+'_'+#uuid+'_'+#page+'_'+#size")
@GetMapping("v1/column_general/load/{language}/{uuid}/{page}/{size}")
public CommonResult load(@PathVariable String language, @PathVariable String uuid, @PathVariable Integer page, @PathVariable Integer size) {
	Page<ColumnGeneralGame> cagData = this.columnGeneralGameRepository.findByColumnGeneral_fUuid(uuid, PageRequest.of(page, size, Sort.by("fOrder")));
	Map<String, Object> data = Maps.newHashMap();
	List<Map<String, Object>> list = Lists.newArrayList();
	if (!cagData.isEmpty()) {
		for (ColumnGeneralGame cag : cagData.getContent()) {
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
				list.add(gameMap);
			}
		}
		data.put("count", cagData.getTotalElements());
		data.put("game", list);
	}
	return CommonResult.successResult(data);
}
}
