package com.ijianjian.game.service;

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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.ijianjian.core.common.constant.ResultType.CommonError;
import com.ijianjian.core.common.object.CommonResult;
import com.ijianjian.core.domain.util.FileInfoUpload;
import com.ijianjian.file.service.FileInfoService;
import com.ijianjian.file.util.FieldConstant.FileType;
import com.ijianjian.game.domain.dto.GameStoreAppCreateDTO;
import com.ijianjian.game.domain.dto.GameStoreAppSearchDTO;
import com.ijianjian.game.domain.po.GameStoreApp;
import com.ijianjian.game.domain.po.GameStoreAppLog;
import com.ijianjian.game.domain.repository.GameStoreAppLogRepository;
import com.ijianjian.game.domain.repository.GameStoreAppRepository;
import com.ijianjian.game.domain.vo.GameStoreAppVO_001;
import com.ijianjian.game.util.DomainFactory;
import com.ijianjian.game.util.FieldConstant.GameStoreAppLogType;
import com.ijianjian.game.util.LocalUser;
import com.ijianjian.game.util.ResultType.GameStoreAppError;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "游戏商城应用")
@RestController
public class GameStoreAppService implements LocalUser {
private final ObjectMapper mapper = new ObjectMapper();
private final GameStoreAppRepository gameStoreAppRepository;
private final FileInfoService fileInfoService;
private final GameStoreAppLogRepository gameStoreAppLogRepository;

public GameStoreAppService(GameStoreAppRepository gameStoreAppRepository, FileInfoService fileInfoService, GameStoreAppLogRepository gameStoreAppLogRepository) {
	super();
	this.gameStoreAppRepository = gameStoreAppRepository;
	this.fileInfoService = fileInfoService;
	this.gameStoreAppLogRepository = gameStoreAppLogRepository;
}

@ApiOperation("查询")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@GetMapping("v1/game_store_app")
public CommonResult query(GameStoreAppSearchDTO dto) {
	dto.init();
	Page<GameStoreApp> page = this.gameStoreAppRepository.query(dto);
	if (page == null || page.getTotalElements() == 0)
		return CommonResult.errorResult(CommonError.list_empty);
	List<GameStoreAppVO_001> list = page.getContent().stream().map(game -> DomainFactory._2VO_001(game)).collect(Collectors.toList());
	return CommonResult.successResult(new PageImpl<>(list, page.getPageable(), page.getTotalElements()));
}

@ApiOperation("保存游戏应用信息及默认语言(文件参数 apk)")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/game_store_app")
public CommonResult save(GameStoreAppCreateDTO dto, HttpServletRequest request) {
	if (Strings.isNullOrEmpty(dto.getName()) || Strings.isNullOrEmpty(dto.getVersion()))
		return CommonResult.errorResult(CommonError.param_error);
	if (this.gameStoreAppRepository.existsByFDeletedFalseAndFVersion(dto.getVersion())) {
		return CommonResult.errorResult(GameStoreAppError.version_exists);
	}
	if (this.gameStoreAppRepository.existsByFDeletedFalseAndFVersionGreaterThan(dto.getVersion())) {
		return CommonResult.errorResult(GameStoreAppError.version_cant_low);
	}

	GameStoreApp po = GameStoreApp.builder().createUser(localCore()).build();
	if (!Strings.isNullOrEmpty(dto.getName()))
		po.setFName(dto.getName());
	if (!Strings.isNullOrEmpty(dto.getDetail()))
		po.setFDetail(dto.getDetail());
	if (!Strings.isNullOrEmpty(dto.getVersion()))
		po.setFVersion(dto.getVersion());

	List<MultipartFile> apk = ((MultipartHttpServletRequest) request).getFiles("apk");
	if (!apk.isEmpty()) {
		FileInfoUpload file = fileInfoService.upload1(apk, FileType.game_store_apk);
		po.setFApk(file.getFilePath());
		po.setFApkSize(file.getFileSize());
		po.setFApkName(file.getFileName());
	}

	this.gameStoreAppRepository.save(po);
	return CommonResult.successResult(po.getFName());
}

@ApiOperation("详情")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@GetMapping("v1/game_store_app/{uuid}")
public CommonResult one(@PathVariable String uuid) {
	Optional<GameStoreApp> uOptional = this.gameStoreAppRepository.findById(uuid);
	if (uOptional.isPresent()) {
		return CommonResult.successResult(DomainFactory._2VO_001(uOptional.get()));
	}
	return CommonResult.errorResult(CommonError.data_not_exist);
}

@ApiOperation("删除")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/game_store_app/delete/{uuid}")
public CommonResult delete(@PathVariable String uuid) {
	this.gameStoreAppRepository.updateLoginFalse(uuid);
	this.gameStoreAppRepository.delete(uuid, LocalDateTime.now());
	return CommonResult.successResult("1");
}

@ApiOperation("查询所有版本")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@GetMapping("v1/game_store_app/version")
public CommonResult version() {
	return CommonResult.successResult(DomainFactory._2GameStoreAppVO_002(this.gameStoreAppRepository.findByFDeletedFalse(Sort.by(Order.desc("fVersion")))));
}

@ApiOperation("查询版本信息")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@GetMapping("v1/game_store_app/version_info")
public CommonResult versionInfo() {
	Map<String, String> map = Maps.newHashMap();
	Optional<GameStoreApp> gsaOp1 = this.gameStoreAppRepository.findByFLoginVersionTrue();
	map.put("login_version", gsaOp1.isPresent() ? gsaOp1.get().getFVersion() : null);

	Optional<GameStoreApp> gsaOp2 = this.gameStoreAppRepository.findTop1ByFDeletedFalse(Sort.by(Order.desc("fVersion")));
	map.put("lastest_version", gsaOp2.isPresent() ? gsaOp2.get().getFVersion() : null);
	return CommonResult.successResult(map);
}

@ApiOperation("设置最低登录版本")
@PreAuthorize("hasRole('super_admin') OR hasRole('admin')")
@PostMapping("v1/game_store_app/login_version/{uuid}")
public CommonResult loginVersion(@PathVariable String uuid) {
	this.gameStoreAppRepository.updateAllLoginFalse();
	this.gameStoreAppRepository.updateLoginTrue(uuid);
	return CommonResult.successResult("1");
}

@ApiOperation("下载")
@GetMapping("v1/game_store_app/download/{version}")
public CommonResult download(@PathVariable String version) {
	Optional<GameStoreApp> gOptional = this.gameStoreAppRepository.findByFDeletedFalseAndFVersion(version);
	if (gOptional.isPresent()) {
		GameStoreApp gameStoreApp = gOptional.get();
		String apk = gameStoreApp.getFApk();
		if (!Strings.isNullOrEmpty(apk)) {
			String filePath = apk.split(",")[0];
			Map<String, String> map = Maps.newHashMap();
			map.put("file_path", filePath);
			map.put("apk_name", gameStoreApp.getFApkName());
			return CommonResult.successResult(map);
		}
	}
	return CommonResult.errorResult(CommonError.data_not_exist);
}

@ApiOperation("下载")
@GetMapping("v1/game_store_app/download_lastest")
public CommonResult download() {
	Optional<GameStoreApp> gOptional = this.gameStoreAppRepository.findTop1ByFDeletedFalse(Sort.by(Order.desc("fVersion")));
	if (gOptional.isPresent()) {
		GameStoreApp gameStoreApp = gOptional.get();
		String apk = gameStoreApp.getFApk();
		if (!Strings.isNullOrEmpty(apk)) {
			String filePath = apk.split(",")[0];
			Map<String, Object> map = Maps.newHashMap();
			map.put("file_path", filePath);
			map.put("apk_name", gameStoreApp.getFApkName());
			map.put("apk_size", gameStoreApp.getFApkSize());
			map.put("name", gameStoreApp.getFName());
			map.put("version", gameStoreApp.getFVersion());
			return CommonResult.successResult(map);
		}
	}
	return CommonResult.errorResult(CommonError.data_not_exist);
}

@ApiOperation("检查版本")
@PostMapping("v1/game_store_app/check/{version}")
public CommonResult appVersion(@PathVariable String version, String phone, String system, String phoneModel, String channelNumber, HttpServletRequest request) {
	Map<String, Object> map = Maps.newHashMap();
	Boolean loginVersionNew = this.gameStoreAppRepository.existsByFDeletedFalseAndFLoginVersionTrueAndFVersionGreaterThan(version);
	map.put("need_update", loginVersionNew);

	Optional<GameStoreApp> gsaOp2 = this.gameStoreAppRepository.findTop1ByFDeletedFalse(Sort.by(Order.desc("fVersion")));
	map.put("lastest_version", gsaOp2.isPresent() ? gsaOp2.get().getFVersion() : null);
	map.put("is_lastest_version", gsaOp2.isPresent() ? gsaOp2.get().getFVersion().equals(version) : true);

	if (gsaOp2.isPresent()) {
		GameStoreApp gameStoreApp = gsaOp2.get();
		String apk = gameStoreApp.getFApk();
		if (!Strings.isNullOrEmpty(apk)) {
			String filePath = apk.split(",")[0];
			map.put("file_path", filePath);
			map.put("apk_name", gameStoreApp.getFApkName());
		}
	}
	//landingPage检查版本 version为0 不记录app打开日志
	if (!version.equals("0")) {
		this.gameStoreAppLog(request, phone, GameStoreAppLogType.open, system, phoneModel, version);
	}
	return CommonResult.successResult(map);
}

@ApiOperation("首次登陆")
@PostMapping("v1/game_store_app/first_open")
public CommonResult firstOpen(String version, String phone, String system, String phoneModel, HttpServletRequest request) {
	this.gameStoreAppLog(request, phone, GameStoreAppLogType.first_open, system, phoneModel, version);
	return CommonResult.successResult(1);
}

private void gameStoreAppLog(HttpServletRequest request, String phone, GameStoreAppLogType type, String system, String phoneModel, String version) {
	Map<String, String> headerMap = Maps.newHashMap();
	Enumeration<String> headers = request.getHeaderNames();

	while (headers.hasMoreElements()) {
		String header = headers.nextElement();
		headerMap.put(header, request.getHeader(header));
	}
	try {
		this.gameStoreAppLogRepository.save(GameStoreAppLog.builder().fIp(this.getIp(request)).fVersion(version).fHeader(mapper.writeValueAsString(headerMap)).fPhone(phone).fSystem(system).fPhoneModel(phoneModel).type(type).build());
	} catch (JsonProcessingException e) {
		e.printStackTrace();
	}
}
}
