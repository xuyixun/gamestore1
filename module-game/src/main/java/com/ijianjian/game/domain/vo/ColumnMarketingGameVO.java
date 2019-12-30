package com.ijianjian.game.domain.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ColumnMarketingGameVO {
private String uuid;
private String gameName;
private Double gameScore;
private String gameVersion;
private String gameDevelopmentCompany;
}
