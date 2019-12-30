package com.ijianjian.game.domain.dto;

import com.ijianjian.core.domain.util.PageDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ColumnMarketingGameSearchDTO implements PageDTO {
private Integer page;
private Integer size;
private String columnMarketingUuid;
private String gameName;
}
