package com.ijianjian.game.domain.dto;

import com.ijianjian.game.util.FieldConstant.ColumnMarketingType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ColumnMarketingCreateDTO_001 {
private String uuid;
private String name;
private ColumnMarketingType type;
private String detail;
private Integer order = 999;
private String languageNumber;
}
