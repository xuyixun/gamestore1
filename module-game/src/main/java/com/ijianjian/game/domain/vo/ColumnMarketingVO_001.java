package com.ijianjian.game.domain.vo;

import com.ijianjian.game.util.FieldConstant.ColumnMarketingType;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ColumnMarketingVO_001 {
private String uuid;
private String name;
private String detail;
private ColumnMarketingType type;
private Integer orderHome;
}
