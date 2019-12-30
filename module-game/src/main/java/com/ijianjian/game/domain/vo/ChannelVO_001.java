package com.ijianjian.game.domain.vo;

import com.ijianjian.game.util.FieldConstant.ChannelType;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ChannelVO_001 {
private String uuid;
private String number;
private String name;
private String url;
private String urlEncrypt;
private ChannelType type;
private String callBackUrl;
private String callBackParams;

private String gameInfoUuid;
private String gameInfoName;

private String columnGeneralUuid;
private String columnGeneralName;
}
