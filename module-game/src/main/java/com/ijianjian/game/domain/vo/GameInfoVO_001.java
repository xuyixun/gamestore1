package com.ijianjian.game.domain.vo;

import com.ijianjian.game.util.FieldConstant.GameInfoChargeType;
import com.ijianjian.game.util.FieldConstant.GameInfoStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class GameInfoVO_001 {
private String uuid;
private String name;
private GameInfoChargeType chargeType;
private Double score;
private String column;
private String version;
private String developmentCompany;
private GameInfoStatus status;
private String icon;
private Long apkSize;
private String apkName;
private Integer downloadCount;
private boolean needShell;
}
