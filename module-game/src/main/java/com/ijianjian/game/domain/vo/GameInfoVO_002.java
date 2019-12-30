package com.ijianjian.game.domain.vo;

import com.ijianjian.game.util.FieldConstant.GameInfoChargeType;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class GameInfoVO_002 {
private String uuid;
private Double score;
private String columnUuid;
private String columnName;
private String version;
private String name;
private String developmentCompany;
private String tag;
private String detail;
private String apk;
private String apkName;
private String icon;
private String adsPictures;
private String screenshot;
private String languageDefault;
private String language;
private Long apkSize;
private Integer downloadCount;
private GameInfoChargeType chargeType;
}
