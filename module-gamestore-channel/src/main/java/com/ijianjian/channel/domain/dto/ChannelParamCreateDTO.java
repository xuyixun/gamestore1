package com.ijianjian.channel.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChannelParamCreateDTO {
private String uuid;
private String channelUuid;
private String paramName;
private String paramValue;
}
