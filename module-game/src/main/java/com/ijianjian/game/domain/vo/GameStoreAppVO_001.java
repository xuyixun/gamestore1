package com.ijianjian.game.domain.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class GameStoreAppVO_001 {
private String uuid;
private String name;
private String apk;
private long apkSize;
private String version;
private String detail;

private Long createTime;
}
