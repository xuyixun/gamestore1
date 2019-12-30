package com.ijianjian.game.domain.vo;

import com.ijianjian.game.util.FieldConstant.AdType;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class AdVO_001 {
private String uuid;
private String name;
private AdType type;
private String data;
private Boolean enable;
}
