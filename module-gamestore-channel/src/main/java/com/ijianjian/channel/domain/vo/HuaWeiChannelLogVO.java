package com.ijianjian.channel.domain.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class HuaWeiChannelLogVO {
private Long time;
private Integer channelNumber;
private String channelName;
private String ip;
private String param;
}
