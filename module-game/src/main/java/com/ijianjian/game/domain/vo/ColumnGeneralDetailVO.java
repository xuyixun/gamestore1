package com.ijianjian.game.domain.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ColumnGeneralDetailVO {
private String columnGeneralUuid;
private String name;
private String background;
private String language;
}
