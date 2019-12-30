package com.ijianjian.user.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.fasterxml.jackson.databind.util.JSONWrappedObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.ijianjian.core.common.constant.Config.RC4Config;
import com.ijianjian.core.common.constant.ResultType.CommonError;
import com.ijianjian.core.common.object.CommonResult;
import com.ijianjian.core.common.util.RC4;
import com.ijianjian.core.domain.user.po.CoreUser;
import com.ijianjian.core.domain.user.repository.CoreUserRepository;
import com.ijianjian.user.domain.po.LandingPageLog;
import com.ijianjian.user.domain.po.SubscribeLog;
import com.ijianjian.user.domain.po.SubscribeBlackList;
import com.ijianjian.user.domain.po.UserInfo;
import com.ijianjian.user.domain.po.UserMemberCycle;
import com.ijianjian.user.domain.repository.LandingPageLogRepository;
import com.ijianjian.user.domain.repository.SubscribeLogRepository;
import com.ijianjian.user.domain.repository.SubscribeBlackListRepository;
import com.ijianjian.user.domain.repository.UserInfoRepository;
import com.ijianjian.user.domain.repository.UserMemberCycleRepository;
import com.ijianjian.user.util.LocalUser;
import com.ijianjian.user.util.FieldConstant.PGInterFaceType;
import com.ijianjian.user.util.FieldConstant.SubscribeType;
import com.ijianjian.user.util.FieldConstant.UserMemberCycleType;
import com.ijianjian.user.util.FieldConstant.UserSource;
import com.ijianjian.user.util.FieldConstant.UserType;
import com.ijianjian.user.util.ResultType.UserError;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.spring.web.json.Json;

@Api(tags = "用户其他")
@RestController
public class UserInfoOtherService implements LocalUser {
private final ObjectMapper mapper = new ObjectMapper();
private final UserInfoRepository userInfoRepository;
private final CoreUserRepository coreUserRepository;
private final LandingPageLogRepository landingPageLogRepository;
private final SubscribeLogRepository subscribeLogRepository;
private final BCryptPasswordEncoder bCryptPasswordEncoder;
private final PGRequestService pgInterFaceService;
private final UserMemberCycleRepository userMemberCycleRepository;
private final SubscribeBlackListRepository subscribeBlackListRepository;

public UserInfoOtherService(UserInfoRepository userInfoRepository, CoreUserRepository coreUserRepository, LandingPageLogRepository landingPageLogRepository, SubscribeLogRepository subscribeLogRepository, BCryptPasswordEncoder bCryptPasswordEncoder, PGRequestService pgInterFaceService,
  UserMemberCycleRepository userMemberCycleRepository, SubscribeBlackListRepository subscribeBlackListRepository) {
	super();
	this.userInfoRepository = userInfoRepository;
	this.coreUserRepository = coreUserRepository;
	this.landingPageLogRepository = landingPageLogRepository;
	this.subscribeLogRepository = subscribeLogRepository;
	this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	this.pgInterFaceService = pgInterFaceService;
	this.userMemberCycleRepository = userMemberCycleRepository;
	this.subscribeBlackListRepository = subscribeBlackListRepository;
}

@ApiOperation("检测手机 自动注册 h5 app")
@PostMapping("v1/user_info/auto_registered")
public CommonResult autoRegistered(@RequestHeader("msisdn") String msisdn, UserSource source) {
	if (Strings.isNullOrEmpty(msisdn)) {
		Map<String, String> map = Maps.newHashMap();
		map.put("phone", "000000");
		return CommonResult.successResult(map);
	} else {
		// question_xxx
		String phone = this.msisdn(msisdn);
		// question_xxx
		Optional<UserInfo> userinfoOptional = this.userInfoRepository.findByCore_fUserName(phone);
		UserInfo userInfo;
		if (userinfoOptional.isPresent()) {
			userInfo = userinfoOptional.get();
		} else {
			userInfo = UserInfo.builder().fPhone(phone).fName(phone).type(UserType.user_normal).core(CoreUser.builder().createUser(localCore()).fRoles("ROLE_user").fUserName(phone).fPassword(bCryptPasswordEncoder.encode("tgbyhn")).fEnable(true).build()).source(source).build();
			this.userInfoRepository.save(userInfo);
		}
		Map<String, String> map = Maps.newHashMap();
		map.put("phone", phone);
		map.put("is_member", String.valueOf(userInfo.getType() == UserType.user_member));
		map.put("uuid", userInfo.getId());
		return CommonResult.successResult(map);
	}
}

@ApiOperation("检测手机 自动注册 landingPage")
@PostMapping("v1/user_info/auto_registered/landing_page")
public CommonResult autoRegistered(@RequestHeader(name = "msisdn", required = false) String msisdn, HttpServletRequest request) {
	LandingPageLog lpLog = this.landingPageLog(request);
	if (Strings.isNullOrEmpty(msisdn)) {
		this.landingPageLogRepository.save(lpLog);
		Map<String, String> map = Maps.newHashMap();
		map.put("phone", "000000");
		map.put("game_uuid", lpLog.getGameInfoUuid());
		map.put("column_uuid", lpLog.getColumnUuid());
		map.put("channel_number", lpLog.getChannelNumber());
		return CommonResult.successResult(map);
	} else {
		// question_xxx
		String phone = this.msisdn(msisdn);
		// question_xxx
		Optional<UserInfo> coreUserO = this.userInfoRepository.findByCore_fUserName(phone);
		UserInfo userInfo;
		if (coreUserO.isPresent()) {
			userInfo = coreUserO.get();
		} else {
			userInfo = UserInfo.builder().channelNumber(lpLog.getChannelNumber()).fPhone(phone).fName(phone).type(UserType.user_normal).core(CoreUser.builder().createUser(localCore()).fRoles("ROLE_user").fUserName(phone).fPassword(bCryptPasswordEncoder.encode("tgbyhn")).fEnable(true).build())
			  .source(UserSource.landingPage).build();
			this.userInfoRepository.save(userInfo);
		}
		lpLog.setFPhone(phone);
		this.landingPageLogRepository.save(lpLog);
		Map<String, String> map = Maps.newHashMap();
		map.put("phone", phone);
		map.put("is_member", String.valueOf(userInfo.getType() == UserType.user_member));
		map.put("uuid", userInfo.getId());
		map.put("game_uuid", lpLog.getGameInfoUuid());
		map.put("column_uuid", lpLog.getColumnUuid());
		map.put("channel_number", lpLog.getChannelNumber());
		return CommonResult.successResult(map);
	}
}

@ApiOperation("发送验证码")
@PostMapping("v1/user_info/verification_code")
public CommonResult verificationcode(String phone) {
	String code = pgInterFaceService.verificationCode(phone);
	if (Strings.isNullOrEmpty(code)) {
		return CommonResult.errorResult(UserError.send_code_error);
	}
	Optional<UserInfo> userinfoOptional = this.userInfoRepository.findByCore_fUserName(phone);
	UserInfo userInfo;
	if (userinfoOptional.isPresent()) {
		userInfo = userinfoOptional.get();
		this.userInfoRepository.updateSmsCode(userInfo.getId(), code);
	} else {
		userInfo = UserInfo.builder().fPhone(phone).fName(phone).type(UserType.user_normal).core(CoreUser.builder().createUser(localCore()).fRoles("ROLE_user").fUserName(phone).fPassword(bCryptPasswordEncoder.encode("tgbyhn")).fEnable(true).build()).source(UserSource.landingPage).fSmsCode(code).build();
		this.userInfoRepository.save(userInfo);
	}
	return CommonResult.successResult(1);
}

@ApiOperation("订购 有号码")
@PostMapping("v1/user_info/subscribe")
public CommonResult subscribe(String uuid, UserSource source, String channelNumber, HttpServletRequest request) {
	if (source == null) {
		return CommonResult.errorResult(CommonError.param_error);
	}
	if (Strings.isNullOrEmpty(uuid)) {
		uuid = this.localCore().getFUuid();
	}
	Optional<UserInfo> userinfoOptional = this.userInfoRepository.findById(uuid);
	if (!userinfoOptional.isPresent()) {
		return CommonResult.errorResult(CommonError.data_not_exist);
	}
	UserInfo userInfo = userinfoOptional.get();
	return this.subscribe(userInfo, source, channelNumber, request);
}

@ApiOperation("订购 无号码")
@PostMapping("v1/user_info/subscribe/code")
public CommonResult subscribe(String phone, String code, UserSource source, String channelNumber, HttpServletRequest request) {
	if (Strings.isNullOrEmpty(phone) || Strings.isNullOrEmpty(code) || source == null) {
		return CommonResult.errorResult(CommonError.param_error);
	}
	Optional<UserInfo> userinfoOptional = this.userInfoRepository.findByCore_fUserName(phone);
	if (userinfoOptional.isPresent()) {
		UserInfo userInfo = userinfoOptional.get();
		if (userInfo.getFSmsCode().equals(code)) {
			return this.subscribe(userInfo, source, channelNumber, request);
		}
		return CommonResult.errorResult(UserError.code_error);
	}
	return CommonResult.errorResult(UserError.nu_send_code);
}

private CommonResult subscribe(UserInfo userInfo, UserSource source, String channelNumber, HttpServletRequest request) {
	UserType type = userInfo.getType();
	if (type == UserType.user_member) {
		return CommonResult.errorResult(UserError.re_subscribe);
	}
	if (type != UserType.user_normal) {
		return CommonResult.errorResult(UserError.cant_subscribe);
	}
	if (this.subscribeBlackListRepository.existsByUser_id(userInfo.getId())) {
		return CommonResult.errorResult(UserError.subscribe_blacklist);
	}
	LocalDateTime time = LocalDateTime.now();

	this.pgInterFaceService.createPending(PGInterFaceType.subscribe, userInfo.getId(), userInfo.getFPhone());
	this.subscribeLog(request, userInfo.getFPhone(), SubscribeType.subscribe, channelNumber);
	if (Strings.isNullOrEmpty(channelNumber)) {
		this.userInfoRepository.subscribe(userInfo.getId(), UserType.user_member.name(), time, source.name());
	} else {
		this.userInfoRepository.subscribe(userInfo.getId(), UserType.user_member.name(), time, source.name(), channelNumber);
	}
	this.userMemberCycleRepository.save(UserMemberCycle.builder().userInfo(userInfo).fStart(time).channelNumber(channelNumber).fCycleCount(1).fNumber(String.valueOf(System.currentTimeMillis())).type(UserMemberCycleType.subscribe).build());

	return CommonResult.successResult("1");
}

@ApiOperation("订购 取消")
@PostMapping("v1/user_info/un_subscribe")
public CommonResult unsubscribe(String uuid, HttpServletRequest request) {
	if (Strings.isNullOrEmpty(uuid)) {
		return CommonResult.errorResult(CommonError.param_error);
	}
	Optional<UserInfo> userinfoOptional = this.userInfoRepository.findById(uuid);
	if (userinfoOptional.isPresent()) {
		UserInfo userInfo = userinfoOptional.get();

		UserType type = userInfo.getType();
		if (type != UserType.user_member) {
			return CommonResult.errorResult(UserError.cant_unsubscribe);
		}
		LocalDateTime time = LocalDateTime.now();

		pgInterFaceService.createPending(PGInterFaceType.unsubscribe, userInfo.getId(), userInfo.getFPhone());
		this.userInfoRepository.unSubscribe(userInfo.getId(), UserType.user_normal.name(), time);
		this.subscribeLog(request, userInfo.getFPhone(), SubscribeType.unSubscribe, null);
		Optional<UserMemberCycle> umcO = this.userMemberCycleRepository.findByFEndIsNullAndUserInfo_id(userInfo.getId());
		if (umcO.isPresent()) {
			this.userMemberCycleRepository.update(umcO.get().getFUuid(), time, UserMemberCycleType.unSubscribe.name());
		}
		this.addUnSubscribeList(uuid);
	} else {
		return CommonResult.errorResult(CommonError.data_not_exist);
	}
	return CommonResult.successResult("1");
}

private LandingPageLog landingPageLog(HttpServletRequest request) {
	String params = null;
	if (request.getParameterMap().containsKey("params")) {
		params = new String(Base64.getDecoder().decode(request.getParameter("params")));
	}
	Boolean needCallBack = false;
	String channelNumber = null;
	String gameInfoUuid = null;
	String columnUuid = null;
	if (!Strings.isNullOrEmpty(params)) {
		String[] params1 = params.split("&");
		for (String param : params1) {
			String[] kv = param.split("=");
			if (kv[0].equals("ncb")) {
				if (kv[1].equals("true"))
					needCallBack = true;
			}
			if (kv[0].equals("n")) {
				channelNumber = kv[1];
			}
			if (kv[0].equals("gu")) {
				gameInfoUuid = kv[1];
			}
			if (kv[0].equals("cu")) {
				columnUuid = kv[1];
			}
		}
	}
	Map<String, String> headerMap = Maps.newHashMap();
	Enumeration<String> headers = request.getHeaderNames();

	while (headers.hasMoreElements()) {
		String header = headers.nextElement();
		headerMap.put(header, request.getHeader(header));
	}
	try {
		return LandingPageLog.builder().gameInfoUuid(gameInfoUuid).columnUuid(columnUuid).fIp(this.getIp(request)).fChannelParams(params).fHeader(mapper.writeValueAsString(headerMap)).channelNumber(channelNumber).fNeedCallback(needCallBack).build();
	} catch (JsonProcessingException e) {
		e.printStackTrace();
	}
	return LandingPageLog.builder().build();
}

private void subscribeLog(HttpServletRequest request, String phone, SubscribeType type, String channelNumber) {
	Map<String, String> headerMap = Maps.newHashMap();
	Enumeration<String> headers = request.getHeaderNames();

	while (headers.hasMoreElements()) {
		String header = headers.nextElement();
		headerMap.put(header, request.getHeader(header));
	}
	try {
		this.subscribeLogRepository.save(SubscribeLog.builder().channelNumber(channelNumber).fIp(this.getIp(request)).fHeader(mapper.writeValueAsString(headerMap)).fPhone(phone).type(type).build());
	} catch (JsonProcessingException e) {
		e.printStackTrace();
	}
}

private String msisdn(String msisdn) {
	System.out.println("msisdn:" + msisdn);

	if(!StringUtils.isEmpty(msisdn)) {
		msisdn = msisdn.replace(",", "");
	    msisdn = new String(new RC4(RC4Config.key.getBytes()).decrypt(Base64.getDecoder().decode(msisdn.getBytes())));
		msisdn = msisdn.replaceFirst("856", "").replace(",", "");
	}
	return msisdn;
}

@GetMapping("v1/user_info/msisdn1/{phone}")
public String msisdn1(@PathVariable String phone) {
	System.out.println("msisdn encrypt:" + phone);

    if(!StringUtils.isEmpty(phone)) {
		phone = phone.replace(",", "");
        phone = new String(new RC4(RC4Config.key.getBytes()).decrypt(Base64.getDecoder().decode(phone.getBytes())));

		phone = phone.replaceFirst("856", "").replace(",", "");
	}
    return phone;
}

@PostMapping("v1/user_info/token_alive")
public CommonResult tokenAlive(HttpServletRequest request, String apkName, String sn) {
	try {

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
		System.out.println("apkName:" + apkName + ",sn:" + sn);
	} catch (IOException e) {
		e.printStackTrace();
	}

	Optional<UserInfo> uO = this.userInfoRepository.findById(this.localCore().getFUuid());
	if (uO.isPresent()) {
		CoreUser user = uO.get().getCore();
		if (sn.equals(uO.get().getCore().getSn())) {
			String chargeType = this.userInfoRepository.chargeType(apkName);
			if (chargeType != "free" && uO.get().getType() == UserType.user_normal) {
				return CommonResult.successResult("member_expired");
			}
			return CommonResult.successResult("success");
		} else {
			return CommonResult.successResult("token_expired");
		}
	}
	return CommonResult.successResult("token_expired");
}

//更新首次登陆时间
@Scheduled(fixedRate = 60000 * 60)
public void firstLoginTime() {
	this.userInfoRepository.findByCore_fFirstLoginTimeIsNullAndCore_fLastLoginTimeIsNotNullAndType(UserType.user_member).forEach(i -> {
		this.coreUserRepository.updateFirstLoginTime(i.getId());
		if (!Strings.isNullOrEmpty(i.getFPhone())) {
			this.pgInterFaceService.createPending(PGInterFaceType.signUpStatusUpdate, i.getId(), i.getFPhone());
		}
	});
	this.userInfoRepository.findByCore_fFirstLoginTimeIsNullAndCore_fLastLoginTimeIsNotNull().forEach(i -> {
		this.coreUserRepository.updateFirstLoginTime(i.getId());
	});
}

//续订会员
@Scheduled(fixedRate = 60000 * 60)
public void memberCycle() {
	LocalDateTime now = LocalDateTime.now();
	now = now.minusDays(7);
	LocalDateTime _7DayBefore = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 23, 59, 59);

	List<UserMemberCycle> list1 = this.userMemberCycleRepository.findByFEndIsNullAndTypeInAndFStartLessThanEqual(new UserMemberCycleType[] { UserMemberCycleType.subscribe, UserMemberCycleType.renew }, _7DayBefore);
	list1.forEach(i -> {
		LocalDateTime start = i.getFStart();
		start = start.plusDays(7);
		LocalDateTime end = LocalDateTime.of(start.getYear(), start.getMonth(), start.getDayOfMonth(), 0, 0, 0);
		this.userMemberCycleRepository.update(i.getFUuid(), end);
		this.userMemberCycleRepository.save(UserMemberCycle.builder().userInfo(i.getUserInfo()).fStart(end).channelNumber(i.getChannelNumber()).fCycleCount(i.getFCycleCount() + 1).fNumber(i.getFNumber()).type(UserMemberCycleType.renew).build());
	});
}

private void addUnSubscribeList(String uuid) {
	if (!this.subscribeBlackListRepository.existsById(uuid)) {
		this.subscribeBlackListRepository.save(SubscribeBlackList.builder().user(UserInfo.builder().id(uuid).build()).build());
	}
}
}
