package com.ijianjian.user.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserInfoCreateDTO_001 {
private String uuid;
private String username;
private String password;
private String confimPassword;
private String nickName;
private String phone;
}
