package com.ijianjian.game.domain.dto;

import com.ijianjian.core.domain.util.FileInfoUpload;
import com.ijianjian.game.util.FieldConstant.GameInfoChargeType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GameInfoCreateDTO_001 {
private String uuid;
private String languageNumber;
private GameInfoChargeType chargeType;
private Double score;
private String version;
private String name;
private String developmentCompany;
private String tag;
private String detail;
private String apkName;
private FileInfoUpload apk;
}
