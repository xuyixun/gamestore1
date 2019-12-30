package com.ijianjian.channel.domain.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class UserVO {
private String uuid;
private Long createTime;
private Long lastLoginTime;
private String name;
}
