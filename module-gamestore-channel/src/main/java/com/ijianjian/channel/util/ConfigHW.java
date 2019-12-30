package com.ijianjian.channel.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

public class ConfigHW {
@Component
@PropertySource("config_hw.yml")
public static class Channel {
public static String scheme;
public static String host;
public static String url;
public static String clickId;
public static String scheduledIp;

@Value("${hw.channel.scheme}")
public void setScheme(String scheme) {
	Channel.scheme = scheme;
}

@Value("${hw.channel.host}")
public void setHost(String host) {
	Channel.host = host;
}

@Value("${hw.channel.url}")
public void setUrl(String url) {
	Channel.url = url;
}

@Value("${hw.channel.clickId}")
public void setClickId(String clickId) {
	Channel.clickId = clickId;
}

@Value("${hw.scheduled-ip}")
public void setScheduledIp(String scheduledIp) {
	Channel.scheduledIp = scheduledIp;
}
}
}
