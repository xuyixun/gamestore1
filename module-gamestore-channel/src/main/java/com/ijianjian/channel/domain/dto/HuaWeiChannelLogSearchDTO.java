package com.ijianjian.channel.domain.dto;

import com.ijianjian.core.domain.util.PageDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class HuaWeiChannelLogSearchDTO implements PageDTO {
private Integer page;
private Integer size;
private Integer channelNumbser;
private Long startTime;
private Long endTime;
}
