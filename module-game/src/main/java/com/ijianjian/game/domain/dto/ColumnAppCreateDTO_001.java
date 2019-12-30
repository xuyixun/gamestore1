package com.ijianjian.game.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ColumnAppCreateDTO_001 {
private String uuid;
private String name;
private String detail;
private Integer order = 999;
private String languageNumber;
}
