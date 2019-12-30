package com.ijianjian.channel.domain.dto;

import com.ijianjian.core.domain.util.OrderDTO;
import com.ijianjian.core.domain.util.PageDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChannelSearchDTO implements PageDTO, OrderDTO {
private Integer page;
private Integer size;
private String name;
private String queryOrder;
}
