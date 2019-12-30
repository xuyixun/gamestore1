package com.ijianjian.game.domain.dto;

import com.ijianjian.game.util.FieldConstant.AdType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdCreateDTO_001 {
private String uuid;
private String name;
private AdType type;
private String data;
private String detail;
private Boolean enable;
private String languageNumber;
}
