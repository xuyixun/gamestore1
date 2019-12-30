package com.ijianjian.channel.domain.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ChannelParamVO {
private String uuid;
private Long createTime;
private String paramName;
private String paramValue;
}
