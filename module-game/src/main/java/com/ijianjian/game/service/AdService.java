package com.ijianjian.game.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.google.common.base.Strings;
import com.ijianjian.core.common.constant.ResultType.CommonError;
import com.ijianjian.core.common.object.CommonResult;
import com.ijianjian.file.service.FileInfoService;
import com.ijianjian.file.util.FieldConstant.FileType;
import com.ijianjian.game.domain.dto.AdCreateDTO_001;
import com.ijianjian.game.domain.dto.AdCreateDTO_002;
import com.ijianjian.game.domain.dto.AdSearchDTO;
import com.ijianjian.game.domain.po.Ad;
import com.ijianjian.game.domain.po.AdDetail;
import com.ijianjian.game.domain.po.ColumnGeneral;
import com.ijianjian.game.domain.po.GameInfo;
import com.ijianjian.game.domain.repository.AdDetailRepository;
import com.ijianjian.game.domain.repository.AdRepository;
import com.ijianjian.game.domain.repository.ColumnMarketingAdRepository;
import com.ijianjian.game.domain.vo.AdVO_002;
import com.ijianjian.game.util.DomainFactory;
import com.ijianjian.game.util.LocalUser;
import com.ijianjian.game.util.ResultType.AdError;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "广告")
@RestController
public class AdService implements LocalUser {
private final AdRepository adRepository;
private final AdDetailRepository adDetailRepository;
private final ColumnMarketingAdRepository columnMarketingAdRepository;
private final FileInfoService fileInfoService;

public AdService(AdRepository adRepository, AdDetailRepository adDetailRepository, ColumnMarketingAdRepository columnMarketingAdRepository, FileInfoService fileInfoService) {
	super();
	this.adRepository = adRepository;
	this.adDetailRepository = adDetailRepository;
	this.columnMarketingAdRepository = columnMarketingAdRepository;
	this.fileInfoService = fileInfoService;
}

@ApiOperation("查询")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@GetMapping("v1/ad")
public CommonResult query(AdSearchDTO dto) {
	dto.init();
	Page<Ad> page = this.adRepository.query(dto);
	if (page == null || page.getTotalElements() == 0)
		return CommonResult.errorResult(CommonError.list_empty);
	return CommonResult.successResult(new PageImpl<>(DomainFactory._2AdVO_001(page.getContent()), page.getPageable(), page.getTotalElements()));
}

@ApiOperation("保存应用栏目信息及默认语言(文件参数  ad_picture)")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/ad")
public CommonResult save(AdCreateDTO_001 dto, HttpServletRequest request) {
	if ((Strings.isNullOrEmpty(dto.getUuid()) && Strings.isNullOrEmpty(dto.getName())) || (Strings.isNullOrEmpty(dto.getUuid()) && dto.getType() == null) || (Strings.isNullOrEmpty(dto.getUuid()) && Strings.isNullOrEmpty(dto.getLanguageNumber())))
		return CommonResult.errorResult(CommonError.param_error);

	Ad po;
	if (Strings.isNullOrEmpty(dto.getUuid())) {
		po = Ad.builder().createUser(localCore()).build();
	} else {
		Optional<Ad> uOptional = this.adRepository.findById(dto.getUuid());
		if (!uOptional.isPresent())
			return CommonResult.errorResult(CommonError.data_not_exist);
		po = uOptional.get();
	}

	if (!Strings.isNullOrEmpty(dto.getLanguageNumber()))
		po.setFLanguageDefault(dto.getLanguageNumber());

	if (!Strings.isNullOrEmpty(dto.getName()))
		po.setFName(dto.getName());
	if (dto.getType() != null) {
		po.setType(dto.getType());
		if (!Strings.isNullOrEmpty(dto.getData())) {
			switch (dto.getType()) {
			case lan_mu:
				po.setColumnGeneral(ColumnGeneral.builder().fUuid(dto.getData()).build());
				break;
			case ying_yong:
				po.setGameInfo(GameInfo.builder().fUuid(dto.getData()).build());
				break;
			default:
				po.setFData(dto.getData());
			}
		}
	}
	if (!Strings.isNullOrEmpty(dto.getDetail()))
		po.setFDetail(dto.getDetail());
	if (dto.getEnable() != null)
		po.setFEnable(dto.getEnable());

	List<MultipartFile> adPicture = ((MultipartHttpServletRequest) request).getFiles("ad_picture");
	if (!adPicture.isEmpty())
		po.setFAdPicture(fileInfoService.upload1(adPicture, FileType.ad_picture).getFilePath());

	this.adRepository.save(po);
	return CommonResult.successResult(po.getFName());
}

@ApiOperation("保存其他語言(文件参数 background)")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/ad/language")
public CommonResult saveDetail(AdCreateDTO_002 dto, HttpServletRequest request) {
	if (Strings.isNullOrEmpty(dto.getUuid()) || Strings.isNullOrEmpty(dto.getLanguageNumber()))
		return CommonResult.errorResult(CommonError.param_error);
	Optional<Ad> gOptional = this.adRepository.findById(dto.getUuid());
	if (gOptional.isPresent()) {
		if (gOptional.get().getFLanguageDefault().equals(dto.getLanguageNumber())) {
			return CommonResult.errorResult(CommonError.param_error);
		}
	} else {
		return CommonResult.errorResult(CommonError.data_not_exist);
	}

	Optional<AdDetail> gameInfoDetailOptional = this.adDetailRepository.findByAd_fUuidAndFLanguageNumber(dto.getUuid(), dto.getLanguageNumber());
	AdDetail po;
	if (gameInfoDetailOptional.isPresent()) {
		po = gameInfoDetailOptional.get();
	} else {
		po = AdDetail.builder().fLanguageNumber(dto.getLanguageNumber()).ad(Ad.builder().fUuid(dto.getUuid()).build()).build();
	}

	if (!Strings.isNullOrEmpty(dto.getDetail()))
		po.setFDetail(dto.getDetail());

	List<MultipartFile> adPicture = ((MultipartHttpServletRequest) request).getFiles("ad_picture");
	if (!adPicture.isEmpty())
		po.setFAdPicture(fileInfoService.upload1(adPicture, FileType.ad_picture).getFilePath());

	this.adDetailRepository.save(po);
	return CommonResult.successResult("");
}

@ApiOperation("详情")
@GetMapping("v1/ad/{uuid}/{language}")
public CommonResult one(@PathVariable String uuid, @PathVariable String language) {
	Optional<Ad> gameInfoOptional = this.adRepository.findById(uuid);
	if (gameInfoOptional.isPresent()) {
		Ad po = gameInfoOptional.get();
		AdVO_002 vo = DomainFactory._2AdVO_002(po);
		if (po.getColumnGeneral() != null) {
			vo.setColumnGeneralName(po.getColumnGeneral().getFName());
			vo.setColumnGeneralUuid(po.getColumnGeneral().getFUuid());
		}
		if (po.getGameInfo() != null) {
			vo.setGameInfoName(po.getGameInfo().getFName());
			vo.setGameInfoUuid(po.getGameInfo().getFUuid());
		}
		if (!po.getFLanguageDefault().equals(language)) {
			Optional<AdDetail> gameInfoDetailOptional = this.adDetailRepository.findByAd_fUuidAndFLanguageNumber(uuid, language);
			if (gameInfoDetailOptional.isPresent()) {
				AdDetail po2 = gameInfoDetailOptional.get();
				if (!Strings.isNullOrEmpty(po2.getFDetail()))
					vo.setDetail(po2.getFDetail());
				if (!Strings.isNullOrEmpty(po2.getFAdPicture()))
					vo.setAdPicture(po2.getFAdPicture());
			}
		}
		return CommonResult.successResult(vo);
	}
	return CommonResult.errorResult(CommonError.data_not_exist);
}

@ApiOperation("详情其他語言")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@GetMapping("v1/ad_detail/{uuid}/{language}")
public CommonResult oneDetail(@PathVariable String uuid, @PathVariable String language) {
	Optional<AdDetail> optional = this.adDetailRepository.findByAd_fUuidAndFLanguageNumber(uuid, language);
	if (optional.isPresent()) {
		return CommonResult.successResult(DomainFactory._2VO(optional.get()));
	} else {
		AdDetail gameInfoDetail = AdDetail.builder().fLanguageNumber(language).ad(Ad.builder().fUuid(uuid).build()).build();
		this.adDetailRepository.save(gameInfoDetail);
		return CommonResult.successResult(gameInfoDetail.getFUuid());
	}
}

@ApiOperation("删除")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/ad/delete/{uuid}")
public CommonResult delete(@PathVariable String uuid) {
	if (this.columnMarketingAdRepository.existsByAd_fUuid(uuid)) {
		return CommonResult.errorResult(AdError.bind_column_marking);
	}
	this.adRepository.delete(uuid, LocalDateTime.now());
	return CommonResult.successResult("1");
}
}
