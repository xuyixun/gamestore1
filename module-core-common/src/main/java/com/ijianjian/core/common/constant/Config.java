package com.ijianjian.core.common.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

public class Config {
@Component
@PropertySource("config.yml")
public static class JWTConfig {
public static String header;
public static String token_prefix;
public static String secret;
public static long expiration_time;

@Value("${jwt.header}")
public void setHeader(String header) {
	JWTConfig.header = header;
}

@Value("${jwt.token_prefix}")
public void setToken_prefix(String token_prefix) {
	JWTConfig.token_prefix = token_prefix;
}

@Value("${jwt.secret}")
public void setSecret(String secret) {
	JWTConfig.secret = secret;
}

@Value("${jwt.expiration_time}")
public void setExpiration_time(long expiration_time) {
	JWTConfig.expiration_time = expiration_time;
}
}

@Component
@PropertySource("config.yml")
public static class FileConfig {
public static String uploadPahtApk;
public static String uploadPahtIcon;
public static String uploadPahtOther;

@Value("${file.upload_path_apk}")
public void setUploadPahtApk(String uploadPahtApk) {
	FileConfig.uploadPahtApk = uploadPahtApk;
}

@Value("${file.upload_path_icon}")
public void setUploadPahtIcon(String uploadPahtIcon) {
	FileConfig.uploadPahtIcon = uploadPahtIcon;
}

@Value("${file.upload_path_other}")
public void setUploadPahtOther(String uploadPahtOther) {
	FileConfig.uploadPahtOther = uploadPahtOther;
}
}

@Component
@PropertySource("config.yml")
public static class PGConfig {
public static String ip;
public static String port;
public static String portBill;
public static String appSecret;
public static String country;
public static String spid;
public static String appId;
public static String productId;
public static String billPath;
public static String billIP;

@Value("${pg.ip}")
public void setIp(String ip) {
	PGConfig.ip = ip;
}

@Value("${pg.port}")
public void setPort(String port) {
	PGConfig.port = port;
}

@Value("${pg.port_bill}")
public void setPortBill(String portBill) {
	PGConfig.portBill = portBill;
}

@Value("${pg.app_secret}")
public void setAppSecret(String appSecret) {
	PGConfig.appSecret = appSecret;
}

@Value("${pg.country}")
public void setCountry(String country) {
	PGConfig.country = country;
}

@Value("${pg.spid}")
public void setSpid(String spid) {
	PGConfig.spid = spid;
}

@Value("${pg.app_id}")
public void setAppId(String appId) {
	PGConfig.appId = appId;
}

@Value("${pg.product_id}")
public void setProductId(String productId) {
	PGConfig.productId = productId;
}

@Value("${pg.bill_path}")
public void setBillPath(String billPath) {
	PGConfig.billPath = billPath;
}

@Value("${pg.bill_ip}")
public void setBillIP(String billIP) {
	PGConfig.billIP = billIP;
}
}

@Component
@PropertySource("config.yml")
public static class RC4Config {
public static String key;

@Value("${rc4.key}")
public void setKey(String key) {
	RC4Config.key = key;
}
}

@Component
@PropertySource("config.yml")
public static class Log {
public static boolean enableRequest;

@Value("${log.enable_request}")
public void setEnableRequest(boolean enableRequest) {
	Log.enableRequest = enableRequest;
}
}

@Component
@PropertySource("config.yml")
public static class SystemParam {
public static Long adminLiveMinute;

@Value("${system_param.admin_live_minute}")
public void setAdminLiveMinute(Long adminLiveMinute) {
	SystemParam.adminLiveMinute = adminLiveMinute;
}
}
}
