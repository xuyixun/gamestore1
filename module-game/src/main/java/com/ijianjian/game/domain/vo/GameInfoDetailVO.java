package com.ijianjian.game.domain.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class GameInfoDetailVO {
private String gameInfoUuid;
private String name;
private String developmentCompany;
private String tag;
private String detail;
private String adsPictures;
private String screenshot;
private String language;
}
