package com.ijianjian.game.domain.vo;

import com.ijianjian.game.util.FieldConstant.ColumnMarketingType;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ColumnMarketingVO_002 {
private String uuid;
private String name;
private String detail;
private String icon;
private String background;
private Integer order;
private String languageDefault;
private String language;
private ColumnMarketingType type;
}
