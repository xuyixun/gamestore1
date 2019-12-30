package com.ijianjian.user.domain.vo;

import com.ijianjian.user.util.FieldConstant.UserType;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class UserInfoVO_001 {
private String uuid;
private String name;
private String nickName;
private Boolean enable;
private String phone;
private Long lastLoginTime;
private UserType type;
}
