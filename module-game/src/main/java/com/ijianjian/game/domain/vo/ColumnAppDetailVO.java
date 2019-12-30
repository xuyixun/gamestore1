package com.ijianjian.game.domain.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ColumnAppDetailVO {
private String columnAppUuid;
private String name;
private String background;
private String language;
}
