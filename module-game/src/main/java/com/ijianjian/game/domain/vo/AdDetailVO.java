package com.ijianjian.game.domain.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class AdDetailVO {
private String adUuid;
private String adPicture;
private String detail;
private String language;
}
