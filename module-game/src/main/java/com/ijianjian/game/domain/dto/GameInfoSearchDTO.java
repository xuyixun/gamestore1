package com.ijianjian.game.domain.dto;

import com.ijianjian.core.domain.util.PageDTO;
import com.ijianjian.game.util.FieldConstant.GameInfoChargeType;
import com.ijianjian.game.util.FieldConstant.GameInfoStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GameInfoSearchDTO implements PageDTO {
private Integer page;
private Integer size;
private String name;
private GameInfoStatus status;
private GameInfoChargeType chargeType;
}
