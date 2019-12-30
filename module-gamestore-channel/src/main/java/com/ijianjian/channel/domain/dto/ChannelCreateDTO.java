package com.ijianjian.channel.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChannelCreateDTO {
private String uuid;
private String name;

private String scheme;
private String host;
private String url;
private String clickId;

private String channelUrl;
private String hwUrl;
private String callBackUrl;

private Integer threshold;
}
