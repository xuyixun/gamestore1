package com.ijianjian.game.domain.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class MyGameVO {
private String uuid;
private Long time;
private String gameInfoUuid;
private String gameInfoName;
private String gameInfoIcon;
private String gameInfoApkName;
private Long gameInfoApkSize;
private Double score;
}
