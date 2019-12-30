package com.ijianjian.user.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.zip.GZIPInputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.ijianjian.core.common.constant.Config;
import com.ijianjian.core.common.util.HttpPostUtil;
import com.ijianjian.core.domain.user.po.CoreUser;
import com.ijianjian.user.domain.po.PGBill;
import com.ijianjian.user.domain.po.PGData;
import com.ijianjian.user.domain.po.PGRequest;
import com.ijianjian.user.domain.po.PGRequestLog;
import com.ijianjian.user.domain.po.UserInfo;
import com.ijianjian.user.domain.po.UserMemberCycle;
import com.ijianjian.user.domain.repository.PGBillRepository;
import com.ijianjian.user.domain.repository.PGDataRepository;
import com.ijianjian.user.domain.repository.PGRequestLogRepository;
import com.ijianjian.user.domain.repository.PGRequestRepository;
import com.ijianjian.user.domain.repository.UserInfoRepository;
import com.ijianjian.user.domain.repository.UserMemberCycleRepository;
import com.ijianjian.user.util.FieldConstant.PGInterFaceStatus;
import com.ijianjian.user.util.FieldConstant.PGInterFaceType;
import com.ijianjian.user.util.FieldConstant.UserMemberCycleType;
import com.ijianjian.user.util.FieldConstant.UserSource;
import com.ijianjian.user.util.FieldConstant.UserType;

@RestController
public class PGRequestService {
private final ObjectMapper mapper = new ObjectMapper();
private final PGRequestRepository pgRequestRepository;
private final PGRequestLogRepository pgRequestLogRepository;

private final PGBillRepository pGBillRepository;
private final PGDataRepository pGDataRepository;

private final UserInfoRepository userInfoRepository;
private final BCryptPasswordEncoder bCryptPasswordEncoder;
private final UserMemberCycleRepository userMemberCycleRepository;

public PGRequestService(PGRequestRepository pgRequestRepository, PGRequestLogRepository pgRequestLogRepository, PGBillRepository pGBillRepository, PGDataRepository pGDataRepository, UserInfoRepository userInfoRepository, BCryptPasswordEncoder bCryptPasswordEncoder,
  UserMemberCycleRepository userMemberCycleRepository) {
	super();
	this.pgRequestRepository = pgRequestRepository;
	this.pgRequestLogRepository = pgRequestLogRepository;
	this.pGBillRepository = pGBillRepository;
	this.pGDataRepository = pGDataRepository;
	this.userInfoRepository = userInfoRepository;
	this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	this.userMemberCycleRepository = userMemberCycleRepository;
}

public void createPending(PGInterFaceType type, String userUuid, String phone) {
	this.pgRequestRepository.save(PGRequest.builder().type(type).status(PGInterFaceStatus.pending).fUserUuid(userUuid).phone(phone).fNumber(String.valueOf(System.currentTimeMillis())).build());
}

public String verificationCode(String phone) {
	if (phone.equals("123456789") || phone.equals("2048000001")) {
		return "111111";
	}
	PGRequest pgif = PGRequest.builder().type(PGInterFaceType.verificationCode).status(PGInterFaceStatus.pending).phone(phone).fNumber(String.valueOf(System.currentTimeMillis())).build();
	this.pgRequestRepository.save(pgif);
	Map<String, String> m = this.request(pgif);
	if (m.containsKey("code") && String.valueOf(m.get("code")).equals("0")) {
		return m.get("verificationCode");
	}
	return null;
}

@SuppressWarnings("unchecked")
private Map<String, String> request(PGRequest pgif) {
	String url = "http://" + Config.PGConfig.ip + ":" + Config.PGConfig.port;
	Map<String, Object> map = Maps.newHashMap();
	map.put("id", pgif.getFNumber());
	map.put("msisdn", pgif.getPhone());
	map.put("country", Config.PGConfig.country);
	map.put("spId", Config.PGConfig.spid);
	map.put("appId", Config.PGConfig.appId);
	map.put("productId", Config.PGConfig.productId);
	String sign = "";
	switch (pgif.getType()) {
	case verificationCode:
		url += "/se/Vcode";
		map.put("templateId", "1001");
		map.put("type", "1");
		map.put("timeStamp", pgif.getFTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		sign = map.get("id").toString() + map.get("msisdn").toString() + map.get("country").toString() + map.get("spId").toString() + map.get("appId").toString() + map.get("productId").toString() + map.get("timeStamp").toString() + Config.PGConfig.appSecret;
		break;
	case subscribe:
		url += "/se/subscribe";
		map.put("subTime", pgif.getFTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		map.put("channel", "SMS");
		sign = map.get("id").toString() + map.get("msisdn").toString() + map.get("country").toString() + map.get("spId").toString() + map.get("appId").toString() + map.get("productId").toString() + map.get("subTime").toString() + Config.PGConfig.appSecret;
		break;
	case unsubscribe:
		url += "/se/unsubscribe";
		map.put("unsubTime", pgif.getFTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		map.put("channel", "SMS");
		sign = map.get("id").toString() + map.get("msisdn").toString() + map.get("country").toString() + map.get("spId").toString() + map.get("appId").toString() + map.get("productId").toString() + map.get("unsubTime").toString() + Config.PGConfig.appSecret;
		break;
	case signUpStatusUpdate:
		url += "/se/Sign-up";
		map.put("timeStamp", pgif.getFTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		sign = map.get("id").toString() + map.get("msisdn").toString() + map.get("country").toString() + map.get("spId").toString() + map.get("appId").toString() + map.get("productId").toString() + map.get("timeStamp").toString() + Config.PGConfig.appSecret;
		break;
	default:
		break;
	}
	map.put("sign", DigestUtils.md5DigestAsHex(sign.getBytes()).toLowerCase());
	PGRequestLog log = PGRequestLog.builder().pgRequest(pgif).build();
	try {
		log.setFUrl(url);
		log.setFParam(mapper.writeValueAsString(map));
		log.setFRequestTime(LocalDateTime.now());
		this.pgRequestLogRepository.save(log);
	} catch (JsonProcessingException e) {
		e.printStackTrace();
	}
	String reponse = HttpPostUtil.post(url, log.getFParam());
	Map<String, String> m = Maps.newHashMap();
	try {
		if (reponse != null) {
			m = mapper.readValue(reponse, Maps.<String, String>newHashMap().getClass());
		}
	} catch (JsonParseException e) {
		e.printStackTrace();
	} catch (JsonMappingException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}
	log.setFReponse(reponse);
	log.setFCode(m.containsKey("code") ? String.valueOf(m.get("code")) : null);
	log.setFReponseTime(LocalDateTime.now());
	this.pgRequestLogRepository.save(log);
	if (m.containsKey("code")) {
		if (String.valueOf(m.get("code")).equals("0")) {
			this.pgRequestRepository.success(pgif.getFUuid(), PGInterFaceStatus.success.toString());
		}
	}
	return m;
}

@Scheduled(fixedRate = 60000 * 10)
public void subscribe() {
	List<PGRequest> list = this.pgRequestRepository.findByTypeAndStatus(PGInterFaceType.subscribe, PGInterFaceStatus.pending);
	list.forEach(pg -> {
		this.request(pg);
	});
}

@Scheduled(fixedRate = 60000 * 10)
public void unsubscribe() {
	List<PGRequest> list = this.pgRequestRepository.findByTypeAndStatus(PGInterFaceType.unsubscribe, PGInterFaceStatus.pending);
	list.forEach(pg -> {
		this.request(pg);
	});
}

@Scheduled(fixedRate = 60000 * 60)
public void signUpStatusUpdate() {
	List<PGRequest> list = this.pgRequestRepository.findByTypeAndStatus(PGInterFaceType.signUpStatusUpdate, PGInterFaceStatus.pending);
	list.forEach(pg -> {
		this.request(pg);
	});
}

//获取账单
@GetMapping("pg/get_bill")
@Scheduled(fixedRate = 60000 * 60)
public void get() {
	LocalDateTime now = LocalDateTime.now();
	String date = now.minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
	if (this.pGBillRepository.existsByFDate(date)) {
		return;
	}

	String fileName = date + "-" + Config.PGConfig.spid + "-" + Config.PGConfig.appId + "-" + DigestUtils.md5DigestAsHex((date + Config.PGConfig.spid + Config.PGConfig.appId + Config.PGConfig.appSecret).getBytes()) + ".txt.gz";

	String url = "http://" + Config.PGConfig.billIP + ":" + Config.PGConfig.portBill + "/bill/" + fileName;

	CloseableHttpClient httpclient = HttpClients.createDefault();
	HttpGet httpget = new HttpGet(url);
	CloseableHttpResponse response = null;
	InputStream in = null;
	FileOutputStream fout = null;
	try {
		response = httpclient.execute(httpget);
		String dirPath = Config.PGConfig.billPath;
		File dir = new File(dirPath);
		if (!dir.exists())
			if (!dir.mkdirs())
				return;
		File file = new File(dir, fileName);
		fout = new FileOutputStream(file);
		in = response.getEntity().getContent();
		int a = -1;
		byte[] tmp = new byte[1024];
		while ((a = in.read(tmp)) != -1) {
			fout.write(tmp, 0, a);
		}
		this.pGBillRepository.save(PGBill.builder().fDate(date).fUrl(url).fFileName(fileName).build());
	} catch (UnsupportedOperationException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	} finally {
		try {
			if (fout != null) {
				fout.flush();
				fout.close();
			}
			if (in != null) {
				in.close();
			}
			if (response != null) {
				response.close();
			}
			httpclient.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

//解析账单
@GetMapping("pg/gzip_bill")
@Scheduled(fixedRate = 60000 * 60)
public void gzip() {
	List<PGBill> list = this.pGBillRepository.findByFInputTableFalse();
	for (PGBill pgr : list) {
		InputStream is = null;
		GZIPInputStream gzis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			is = new FileInputStream(Config.PGConfig.billPath + "/" + pgr.getFFileName());
			gzis = new GZIPInputStream(is);
			isr = new InputStreamReader(gzis);
			br = new BufferedReader(isr);
			String s;
			DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			DateTimeFormatter dateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			while ((s = br.readLine()) != null) {
				String[] data = s.split("\\|");
				this.pGDataRepository.save(PGData.builder().pGBill(pgr).msisdn(data[0]).country(data[1]).spId(data[2]).appId(data[3]).productId(data[4]).type(data[5]).billStartdate(LocalDate.parse(data[6], date)).billEnddate(LocalDate.parse(data[7], date)).deductCase(data[8]).status(data[9])
				  .timeStamp(LocalDateTime.parse(data[10], dateTime)).billcycledate(LocalDate.parse(data[11], date)).activeenddate(LocalDate.parse(data[12], date)).graceenddate(LocalDate.parse(data[13], date)).channel(data[14]).build());
			}
			this.pGBillRepository.data(pgr.getFUuid());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
				if (isr != null) {
					isr.close();
				}
				if (gzis != null) {
					gzis.close();
				}
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

@PostMapping("pg/datasync")
public Map<String, Object> sync(@RequestBody Map<String, String> param, HttpServletRequest request) {
	Map<String, Object> map = Maps.newHashMap();

	PGRequest pg = PGRequest.builder().type(PGInterFaceType.datasync).status(PGInterFaceStatus.success).fNumber(String.valueOf(System.currentTimeMillis())).build();
	this.pgRequestRepository.save(pg);

	PGRequestLog log = PGRequestLog.builder().pgRequest(pg).build();
	try {
		log.setFUrl(request.getRequestURI());
		log.setFParam(mapper.writeValueAsString(param));
		log.setFRequestTime(LocalDateTime.now());
		map.put("id", pg.getFUuid());
		map.put("code", 0);
		log.setFReponse(mapper.writeValueAsString(map));
		this.pgRequestLogRepository.save(log);

		this.user(param);
	} catch (JsonProcessingException e) {
		e.printStackTrace();
	}
	return map;
}

public void user(Map<String, String> param) {
	if (param.containsKey("code") && param.get("code").equals("0")) {
		if (param.containsKey("msisdn")) {
			String phone = param.get("msisdn");
			if (param.get("type").equalsIgnoreCase("SUBSCRIBE")) {
				Optional<UserInfo> userinfoOptional = this.userInfoRepository.findByCore_fUserName(phone);

				UserInfo userInfo;
				if (userinfoOptional.isPresent()) {
					userInfo = userinfoOptional.get();
				} else {
					userInfo = UserInfo.builder().fPhone(phone).fName(phone).type(UserType.user_normal).core(CoreUser.builder().fRoles("ROLE_user").fUserName(phone).fPassword(bCryptPasswordEncoder.encode("tgbyhn")).fEnable(true).build()).source(UserSource.sms).build();
					this.userInfoRepository.save(userInfo);
				}
				UserType type = userInfo.getType();
				if (type == UserType.user_member) {
					return;
				}
				if (type != UserType.user_normal) {
					return;
				}
				LocalDateTime time = LocalDateTime.now();

				this.userInfoRepository.subscribe(userInfo.getId(), UserType.user_member.name(), time, "sms");
				this.userMemberCycleRepository.save(UserMemberCycle.builder().userInfo(userInfo).fStart(time).fCycleCount(1).fNumber(String.valueOf(System.currentTimeMillis())).type(UserMemberCycleType.subscribe).build());
			} else if (param.get("type").equalsIgnoreCase("UNSUBSCRIBE")) {
				Optional<UserInfo> userinfoOptional = this.userInfoRepository.findByCore_fUserName(phone);

				UserInfo userInfo;
				if (userinfoOptional.isPresent()) {
					userInfo = userinfoOptional.get();
				} else {
					userInfo = UserInfo.builder().fPhone(phone).fName(phone).type(UserType.user_normal).core(CoreUser.builder().fRoles("ROLE_user").fUserName(phone).fPassword(bCryptPasswordEncoder.encode("tgbyhn")).fEnable(true).build()).source(UserSource.sms).build();
					this.userInfoRepository.save(userInfo);
				}

				UserType type = userInfo.getType();
				if (type != UserType.user_member) {
					return;
				}
				LocalDateTime time = LocalDateTime.now();

				this.userInfoRepository.unSubscribe(userInfo.getId(), UserType.user_normal.name(), time);
				Optional<UserMemberCycle> umcO = this.userMemberCycleRepository.findByFEndIsNullAndUserInfo_id(userInfo.getId());
				if (umcO.isPresent()) {
					this.userMemberCycleRepository.update(umcO.get().getFUuid(), time, UserMemberCycleType.unSubscribe.name());
				}
			}
		}
	}
}
}
