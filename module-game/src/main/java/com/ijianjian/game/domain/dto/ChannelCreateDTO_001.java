package com.ijianjian.game.domain.dto;

import com.ijianjian.game.util.FieldConstant.ChannelType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChannelCreateDTO_001 {
private String uuid;
private String name;
private String url;
private ChannelType type;
private String callBackUrl;
private String callBackParams;
private String gameInfoUuid;
private String columnGeneralUuid;
}
