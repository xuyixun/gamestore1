package com.ijianjian.channel.domain.vo;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ChannelVO {
private String uuid;
private Long createTime;
private Integer number;
private String name;

private String scheme;
private String host;
private String url;
private String clickId;
private List<ChannelParamVO> params;

private String channelUrl;
private String hwUrl;
private String callBackUrl;

private int threshold;
}
