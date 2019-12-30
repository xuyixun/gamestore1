package com.ijianjian.game.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ijianjian.core.common.constant.ResultType.CommonError;
import com.ijianjian.core.common.object.CommonResult;
import com.ijianjian.core.domain.user.po.CoreUser;
import com.ijianjian.core.domain.util.FileInfoUpload;
import com.ijianjian.file.service.FileInfoService;
import com.ijianjian.file.util.FieldConstant.FileType;
import com.ijianjian.game.domain.dto.GameInfoCreateDTO_001;
import com.ijianjian.game.domain.dto.GameInfoCreateDTO_002;
import com.ijianjian.game.domain.dto.GameInfoSearchDTO;
import com.ijianjian.game.domain.dto.GameInfoSearchDTO_002;
import com.ijianjian.game.domain.po.ColumnAppGame;
import com.ijianjian.game.domain.po.ColumnGeneralGame;
import com.ijianjian.game.domain.po.ColumnMarketingGame;
import com.ijianjian.game.domain.po.GameAppLog;
import com.ijianjian.game.domain.po.GameInfo;
import com.ijianjian.game.domain.po.GameInfoDetail;
import com.ijianjian.game.domain.po.GameLog;
import com.ijianjian.game.domain.po.MyGame;
import com.ijianjian.game.domain.repository.ColumnAppGameRepository;
import com.ijianjian.game.domain.repository.ColumnGeneralGameRepository;
import com.ijianjian.game.domain.repository.ColumnMarketingGameRepository;
import com.ijianjian.game.domain.repository.GameAppLogRepository;
import com.ijianjian.game.domain.repository.GameInfoDetailRepository;
import com.ijianjian.game.domain.repository.GameInfoRepository;
import com.ijianjian.game.domain.repository.GameLogRepository;
import com.ijianjian.game.domain.repository.MyGameRepository;
import com.ijianjian.game.domain.vo.GameInfoVO_001;
import com.ijianjian.game.domain.vo.GameInfoVO_002;
import com.ijianjian.game.util.DomainFactory;
import com.ijianjian.game.util.FieldConstant.GameInfoChargeType;
import com.ijianjian.game.util.FieldConstant.GameInfoStatus;
import com.ijianjian.game.util.FieldConstant.GameLogClient;
import com.ijianjian.game.util.FieldConstant.GameLogFrom;
import com.ijianjian.game.util.FieldConstant.GameLogType;
import com.ijianjian.game.util.LocalUser;
import com.ijianjian.game.util.ResultType.GameInfoError;
import com.ijianjian.shell.GameShellToolkit;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "游戏应用")
@RestController
public class GameInfoService implements LocalUser {
private final ObjectMapper mapper = new ObjectMapper();
private final GameInfoRepository gameInfoRepository;
private final GameInfoDetailRepository gameInfoDetailRepository;
private final FileInfoService fileInfoService;

private final ColumnAppGameRepository columnAppGameRepository;
private final ColumnGeneralGameRepository columnGeneralGameRepository;
private final ColumnMarketingGameRepository columnMarketingGameRepository;
private final MyGameRepository myGameRepository;

private final GameLogRepository gameLogRepository;
private final GameAppLogRepository gameAppLogRepository;

public GameInfoService(GameInfoRepository gameInfoRepository, GameInfoDetailRepository gameInfoDetailRepository, FileInfoService fileInfoService, ColumnAppGameRepository columnAppGameRepository, ColumnGeneralGameRepository columnGeneralGameRepository,
  ColumnMarketingGameRepository columnMarketingGameRepository, MyGameRepository myGameRepository, GameLogRepository gameLogRepository, GameAppLogRepository gameAppLogRepository) {
	super();
	this.gameInfoRepository = gameInfoRepository;
	this.gameInfoDetailRepository = gameInfoDetailRepository;
	this.fileInfoService = fileInfoService;
	this.columnAppGameRepository = columnAppGameRepository;
	this.columnGeneralGameRepository = columnGeneralGameRepository;
	this.columnMarketingGameRepository = columnMarketingGameRepository;
	this.myGameRepository = myGameRepository;
	this.gameLogRepository = gameLogRepository;
	this.gameAppLogRepository = gameAppLogRepository;
}

@ApiOperation("查询")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@GetMapping("v1/game_info")
public CommonResult query(GameInfoSearchDTO dto) {
	dto.init();
	Page<GameInfo> page = this.gameInfoRepository.query(dto);
	if (page == null || page.getTotalElements() == 0)
		return CommonResult.errorResult(CommonError.list_empty);
	List<GameInfoVO_001> list = page.getContent().stream().map(game -> DomainFactory._2GameInfoVO_001(game)).collect(Collectors.toList());
	return CommonResult.successResult(new PageImpl<>(list, page.getPageable(), page.getTotalElements()));
}

@ApiOperation("查询")
@GetMapping("v1/game_info/shang_xian")
public CommonResult queryA(GameInfoSearchDTO_002 dto) {
	dto.init();
	Page<GameInfo> page = this.gameInfoRepository.queryB(dto);
	if (page == null || page.getTotalElements() == 0)
		return CommonResult.errorResult(CommonError.list_empty);
	List<GameInfoVO_001> list = page.getContent().stream().map(game -> {
		GameInfoVO_001 vo = DomainFactory._2GameInfoVO_001(game);
		Optional<GameInfoDetail> gameInfoDetailOptional = this.gameInfoDetailRepository.findByGameInfo_fUuidAndFLanguageNumber(game.getFUuid(), dto.getLanguage());
		if (gameInfoDetailOptional.isPresent()) {
			GameInfoDetail po2 = gameInfoDetailOptional.get();
			if (!Strings.isNullOrEmpty(po2.getFName()))
				vo.setName(po2.getFName());
		}
		return vo;
	}).collect(Collectors.toList());
	return CommonResult.successResult(new PageImpl<>(list, page.getPageable(), page.getTotalElements()));
}

@ApiOperation("保存游戏应用信息及默认语言(文件参数 apk,icon,ads_pictures,screenshot)")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/game_info")
public CommonResult save(GameInfoCreateDTO_001 dto, HttpServletRequest request) {
	if (Strings.isNullOrEmpty(dto.getName()) || (Strings.isNullOrEmpty(dto.getUuid()) && Strings.isNullOrEmpty(dto.getLanguageNumber())))
		return CommonResult.errorResult(CommonError.param_error);

	GameInfo po;
	if (Strings.isNullOrEmpty(dto.getUuid())) {
		po = GameInfo.builder().createUser(localCore()).status(GameInfoStatus.cao_gao).chargeType(GameInfoChargeType.member).build();
	} else {
		Optional<GameInfo> uOptional = this.gameInfoRepository.findById(dto.getUuid());
		if (!uOptional.isPresent())
			return CommonResult.errorResult(CommonError.data_not_exist);
		po = uOptional.get();
	}
	if (!Strings.isNullOrEmpty(dto.getLanguageNumber()))
		po.setFLanguageDefault(dto.getLanguageNumber());

	if (dto.getScore() != null)
		po.setFScore(dto.getScore());
	if (!Strings.isNullOrEmpty(dto.getVersion()))
		po.setFVersion(dto.getVersion());

	if (!Strings.isNullOrEmpty(dto.getApkName()))
		po.setFApkName(dto.getApkName());

    if (dto.getApk()!=null) {
        FileInfoUpload file = dto.getApk();
        po.setFApk(file.getFilePath());
        po.setFApkSize(file.getFileSize());
        po.setFNeedShell(true);
    }

    List<MultipartFile> icon = ((MultipartHttpServletRequest) request).getFiles("icon");
	if (!icon.isEmpty())
		po.setFIcon(fileInfoService.upload1(icon, FileType.game_icon).getFilePath());

	if (!Strings.isNullOrEmpty(dto.getName()))
		po.setFName(dto.getName());
	if (!Strings.isNullOrEmpty(dto.getDevelopmentCompany()))
		po.setFDevelopmentCompany(dto.getDevelopmentCompany());
	if (!Strings.isNullOrEmpty(dto.getTag()))
		po.setFTag(dto.getTag());
	if (!Strings.isNullOrEmpty(dto.getDetail()))
		po.setFDetail(dto.getDetail());
	if (dto.getChargeType() != null) {
		po.setChargeType(dto.getChargeType());
	}

	List<MultipartFile> adsPictures = ((MultipartHttpServletRequest) request).getFiles("ads_pictures");
	if (!adsPictures.isEmpty())
		po.setFAdsPictures(fileInfoService.upload1(adsPictures, FileType.game_ads_pictures).getFilePath());

	List<MultipartFile> screenshot = ((MultipartHttpServletRequest) request).getFiles("screenshot");
	if (!screenshot.isEmpty())
		po.setFScreenshot(fileInfoService.upload1(screenshot, FileType.game_screenshot).getFilePath());

	this.gameInfoRepository.save(po);
	return CommonResult.successResult(po.getFUuid());
}

@ApiOperation("保存其他語言")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/game_info/language")
public CommonResult saveDetail(GameInfoCreateDTO_002 dto, HttpServletRequest request) {
	if (Strings.isNullOrEmpty(dto.getUuid()) || Strings.isNullOrEmpty(dto.getLanguageNumber()))
		return CommonResult.errorResult(CommonError.param_error);
	Optional<GameInfo> gOptional = this.gameInfoRepository.findById(dto.getUuid());
	if (gOptional.isPresent()) {
		if (gOptional.get().getFLanguageDefault().equals(dto.getLanguageNumber())) {
			return CommonResult.errorResult(CommonError.param_error);
		}
	} else {
		return CommonResult.errorResult(CommonError.data_not_exist);
	}

	Optional<GameInfoDetail> gameInfoDetailOptional = this.gameInfoDetailRepository.findByGameInfo_fUuidAndFLanguageNumber(dto.getUuid(), dto.getLanguageNumber());
	GameInfoDetail po;
	if (gameInfoDetailOptional.isPresent()) {
		po = gameInfoDetailOptional.get();
	} else {
		po = GameInfoDetail.builder().fLanguageNumber(dto.getLanguageNumber()).gameInfo(GameInfo.builder().fUuid(dto.getUuid()).build()).build();
	}

	if (!Strings.isNullOrEmpty(dto.getName()))
		po.setFName(dto.getName());
	if (!Strings.isNullOrEmpty(dto.getDevelopmentCompany()))
		po.setFDevelopmentCompany(dto.getDevelopmentCompany());
	if (!Strings.isNullOrEmpty(dto.getTag()))
		po.setFTag(dto.getTag());
	if (!Strings.isNullOrEmpty(dto.getDetail()))
		po.setFDetail(dto.getDetail());

	List<MultipartFile> adsPictures = ((MultipartHttpServletRequest) request).getFiles("ads_pictures");
	if (!adsPictures.isEmpty())
		po.setFAdsPictures(fileInfoService.upload1(adsPictures, FileType.game_ads_pictures).getFilePath());

	List<MultipartFile> screenshot = ((MultipartHttpServletRequest) request).getFiles("screenshot");
	if (!screenshot.isEmpty())
		po.setFScreenshot(fileInfoService.upload1(screenshot, FileType.game_screenshot).getFilePath());

	this.gameInfoDetailRepository.save(po);
	return CommonResult.successResult(po.getFUuid());
}

@ApiOperation("详情")
@GetMapping("v1/game_info/{uuid}/{language}")
public CommonResult one(@PathVariable String uuid, @PathVariable String language, GameLogFrom from, String fromUuid, GameLogClient client, HttpServletRequest request) {
	Optional<GameInfo> gameInfoOptional = this.gameInfoRepository.findById(uuid);
	if (gameInfoOptional.isPresent()) {
		GameInfo po1 = gameInfoOptional.get();
		GameInfoVO_002 vo = DomainFactory._2GameInfoVO_002(po1);
		if (!po1.getFLanguageDefault().equals(language)) {
			Optional<GameInfoDetail> gameInfoDetailOptional = this.gameInfoDetailRepository.findByGameInfo_fUuidAndFLanguageNumber(uuid, language);
			if (gameInfoDetailOptional.isPresent()) {
				GameInfoDetail po2 = gameInfoDetailOptional.get();
				vo.setLanguage(po2.getFLanguageNumber());
				if (!Strings.isNullOrEmpty(po2.getFName()))
					vo.setName(po2.getFName());
				if (!Strings.isNullOrEmpty(po2.getFDevelopmentCompany()))
					vo.setDevelopmentCompany(po2.getFDevelopmentCompany());
				if (!Strings.isNullOrEmpty(po2.getFTag()))
					vo.setTag(po2.getFTag());
				if (!Strings.isNullOrEmpty(po2.getFDetail()))
					vo.setDetail(po2.getFDetail());
				if (!Strings.isNullOrEmpty(po2.getFAdsPictures()))
					vo.setAdsPictures(po2.getFAdsPictures());
				if (!Strings.isNullOrEmpty(po2.getFScreenshot()))
					vo.setScreenshot(po2.getFScreenshot());
			}
		}
		Optional<ColumnGeneralGame> s = this.columnGeneralGameRepository.findByGameInfo_fUuid(uuid);
		if (s.isPresent()) {
			vo.setColumnName(s.get().getColumnGeneral().getFName());
			vo.setColumnUuid(s.get().getColumnGeneral().getFUuid());
		}
		this.gameLog(request, uuid, this.localCore() == null ? null : this.localCore().getFUuid(), GameLogType.detail, from, fromUuid, client);
		return CommonResult.successResult(vo);
	}
	return CommonResult.errorResult(CommonError.data_not_exist);
}

@ApiOperation("游戏广告图片")
@GetMapping("v1/game_info/ad_picture/{uuid}")
public CommonResult adPicture(@PathVariable String uuid) {
	Optional<GameInfo> gameInfoOptional = this.gameInfoRepository.findById(uuid);
	if (gameInfoOptional.isPresent()) {
		return CommonResult.successResult(gameInfoOptional.get().getFAdsPictures());
	}
	return CommonResult.errorResult(CommonError.data_not_exist);
}

@ApiOperation("详情")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@GetMapping("v1/game_info_detail/{uuid}/{language}")
public CommonResult oneDetail(@PathVariable String uuid, @PathVariable String language) {
	Optional<GameInfoDetail> optional = this.gameInfoDetailRepository.findByGameInfo_fUuidAndFLanguageNumber(uuid, language);
	if (optional.isPresent()) {
		return CommonResult.successResult(DomainFactory._2GameInfoDetailVO(optional.get()));
	} else {
		GameInfoDetail po = GameInfoDetail.builder().fLanguageNumber(language).gameInfo(GameInfo.builder().fUuid(uuid).build()).build();
		this.gameInfoDetailRepository.save(po);
		return CommonResult.successResult(po.getFUuid());
	}
}

@ApiOperation("删除")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/game_info/delete/{uuid}")
public CommonResult delete(@PathVariable String uuid) {
	List<ColumnAppGame> cagList = this.columnAppGameRepository.findByGameInfo_fUuid(uuid);
	cagList.forEach(i -> {
		Integer order = this.columnAppGameRepository.order(i.getFUuid());
		this.columnAppGameRepository.updateDelete(i.getColumnApp().getFUuid(), order);
		this.columnAppGameRepository.deleteByColumnApp_fUuidAndGameInfo_fUuid(i.getColumnApp().getFUuid(), i.getFUuid());
	});
	Optional<ColumnGeneralGame> s = this.columnGeneralGameRepository.findByGameInfo_fUuid(uuid);
	if (s.isPresent()) {
		Integer order = this.columnGeneralGameRepository.order(s.get().getFUuid());
		this.columnGeneralGameRepository.updateDelete(s.get().getColumnGeneral().getFUuid(), order);
		this.columnGeneralGameRepository.deleteById(s.get().getFUuid());
	}
	List<ColumnMarketingGame> cmgList = this.columnMarketingGameRepository.findByGameInfo_fUuid(uuid);
	cmgList.forEach(i -> {
		Integer order = this.columnMarketingGameRepository.order(i.getFUuid());
		this.columnMarketingGameRepository.updateDelete(i.getColumnMarketing().getFUuid(), order);
		this.columnMarketingGameRepository.deleteById(i.getFUuid());
	});
	this.myGameRepository.deleteByGameInfo_fUuid(uuid);
	this.gameInfoRepository.delete(uuid, LocalDateTime.now());
	return CommonResult.successResult("1");
}

@ApiOperation("下载")
@GetMapping("v1/game_info/download/{uuid}")
public CommonResult download(@PathVariable String uuid, GameLogFrom from, String fromUuid, GameLogClient client, HttpServletRequest request) {
	Optional<GameInfo> gOptional = this.gameInfoRepository.findById(uuid);
	if (gOptional.isPresent()) {
		CoreUser user = this.localCore();
		if (user == null) {
			return CommonResult.errorResult(GameInfoError.login_user_download);
		}
		GameInfo gameInfo = gOptional.get();
		if (gameInfo.isFNeedShell()) {
			return CommonResult.errorResult(GameInfoError.need_shell);
		}
		if (gameInfo.getChargeType() != GameInfoChargeType.free && this.gameInfoRepository.userType(user.getFUuid()).contains("user_normal")) {
			return CommonResult.errorResult(GameInfoError.only_member_download);
		}
		String apk = gameInfo.getFApk();
		if (!Strings.isNullOrEmpty(apk)) {
			String filePath = apk.split(",")[0];

			if (!this.myGameRepository.existsByUser_fUuidAndGameInfo_fUuid(user.getFUuid(), uuid)) {
				this.myGameRepository.save(MyGame.builder().user(user).gameInfo(GameInfo.builder().fUuid(uuid).build()).build());
			}

			this.gameInfoRepository.downloadCount(uuid);
			this.gameLog(request, uuid, user.getFUuid(), GameLogType.download, from, fromUuid, client);
			String suffix = filePath.substring(filePath.lastIndexOf("."), filePath.length());
			return CommonResult.successResult(filePath + "___" + gameInfo.getFName() + suffix);
		}
	}
	return CommonResult.errorResult(CommonError.data_not_exist);
}

@ApiOperation("处理")
@GetMapping("v1/game_info/handle/{uuid}")
public CommonResult handle(@PathVariable String uuid, Boolean handle) {
	if (handle == null) {
		handle = true;
	}
	Optional<GameInfo> gOptional = this.gameInfoRepository.findById(uuid);
	if (gOptional.isPresent()) {
		GameInfo gameInfo = gOptional.get();
		GameInfoStatus status = gameInfo.getStatus();
		switch (status) {
		case cao_gao:
			this.gameInfoRepository.status(uuid, GameInfoStatus.dai_shen_he.name());
			break;
		case dai_shen_he:
			if (handle) {
				this.gameInfoRepository.onLine(uuid, GameInfoStatus.shang_xian.name(), LocalDateTime.now());
			} else {
				this.gameInfoRepository.status(uuid, GameInfoStatus.bo_hui.name());
			}
			break;
		case shang_xian:
			this.gameInfoRepository.offLine(uuid, GameInfoStatus.xia_xian.name(), LocalDateTime.now());
			break;
		case xia_xian:
			this.gameInfoRepository.onLine(uuid, GameInfoStatus.shang_xian.name(), LocalDateTime.now());
			break;
		case bo_hui:
			this.gameInfoRepository.status(uuid, GameInfoStatus.cao_gao.name());
			break;
		}
	}
	return CommonResult.successResult(1);
}

private void gameLog(HttpServletRequest request, String gameUuid, String userUuid, GameLogType type, GameLogFrom from, String fromUuid, GameLogClient client) {
	Map<String, String> headerMap = Maps.newHashMap();
	Enumeration<String> headers = request.getHeaderNames();

	while (headers.hasMoreElements()) {
		String header = headers.nextElement();
		headerMap.put(header, request.getHeader(header));
	}
	try {
		this.gameLogRepository.save(GameLog.builder().client(client).fIp(this.getIp(request)).fHeader(mapper.writeValueAsString(headerMap)).fGameUuid(gameUuid).fUserUuid(userUuid).type(type).gameLogForm(from).fFromUuid(fromUuid).build());
	} catch (JsonProcessingException e) {
		e.printStackTrace();
	}
}

@ApiOperation("首页")
@GetMapping("v1/game_info/home")
public CommonResult home() {
	Map<String, Object> map = Maps.newHashMap();

	List<Map<String, String>> list1 = Lists.newArrayList();
	List<GameInfo> data1 = this.gameInfoRepository.findTop10ByFDeletedFalseAndStatus(GameInfoStatus.shang_xian, Sort.by(Order.desc("fDownloadCount")));
	for (GameInfo game : data1) {
		Map<String, String> map1 = Maps.newHashMap();
		map1.put("uuid", game.getFUuid());
		map1.put("icon", game.getFIcon());
		map1.put("name", game.getFName());
		map1.put("count", String.valueOf(game.getFDownloadCount()));
		list1.add(map1);
	}
	map.put("game_download", list1);

	List<Map<String, String>> list2 = Lists.newArrayList();
	List<String[]> data2 = this.gameAppLogRepository.queryHome();
	for (String[] game : data2) {
		Map<String, String> map1 = Maps.newHashMap();
		map1.put("uuid", game[0]);
		map1.put("icon", game[1]);
		map1.put("name", game[2]);
		map1.put("count", game[3]);
		list2.add(map1);
	}
	map.put("game_open", list2);

	return CommonResult.successResult(map);
}

@ApiOperation("游戏应用可用检查")
@PostMapping("v1/game_info/check")
public CommonResult gameCheck(String apkName, String sn, HttpServletRequest request) {
    try {
        if(StringUtils.isEmpty(apkName)) {
            InputStream in = request.getInputStream();
            BufferedReader bf = new BufferedReader(new InputStreamReader(in));
            StringBuilder stringBuilder = new StringBuilder();
            String s = "";
            while((s = bf.readLine()) != null) {
                stringBuilder.append(s);
            }

            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> req = mapper.readValue(stringBuilder.toString(), Map.class);
            apkName = req.get("apkName");
            sn = req.get("sn");
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
	String gameUuid = apkName;
	Optional<GameInfo> giO = this.gameInfoRepository.findTopByFDeletedFalseAndFApkName(apkName);
	if (giO.isPresent()) {
		CoreUser user = this.localCore();
		GameInfo gameInfo = giO.get();
		gameUuid = gameInfo.getFUuid();
		if (gameInfo.getChargeType() != GameInfoChargeType.free && this.gameInfoRepository.userType(user.getFUuid()).contains("user_normal")) {
			return CommonResult.successResult(false);
		}
		this.gameAppLog(gameUuid, sn, request);
		return CommonResult.successResult(true);
	} else {
		return CommonResult.successResult(false);
	}
}

private void gameAppLog(String gameUuid, String sn, HttpServletRequest request) {
	Map<String, String> headerMap = Maps.newHashMap();
	Enumeration<String> headers = request.getHeaderNames();

	while (headers.hasMoreElements()) {
		String header = headers.nextElement();
		headerMap.put(header, request.getHeader(header));
	}
	try {
		this.gameAppLogRepository.save(GameAppLog.builder().fIp(this.getIp(request)).fHeader(mapper.writeValueAsString(headerMap)).fSN(sn).fGameUuid(gameUuid).fUserUuid(this.localCore().getFUuid()).build());
	} catch (JsonProcessingException e) {
		e.printStackTrace();
	}
}

@Scheduled(fixedRate = 60000 * 1)
public void addShell() {
	List<GameInfo> list = this.gameInfoRepository.findFirstByFDeletedFalseAndFNeedShellTrue();
	list.forEach(game -> {
		String apkName = "";
		try {
			apkName = GameShellToolkit.gameShell(game.getFApkName(), game.getFApk().replace(",", ""));
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("add_shell");
		System.out.println(game.getFUuid());
		System.out.println(apkName);
		if (!Strings.isNullOrEmpty(apkName)) {
		    String[] apk = apkName.split("___");
			this.gameInfoRepository.apkName(game.getFUuid(), apk[0], apk[1]);
			this.gameInfoRepository.needShell(game.getFUuid());
		}
	});
}
}
