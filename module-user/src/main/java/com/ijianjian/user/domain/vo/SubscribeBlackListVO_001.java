package com.ijianjian.user.domain.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class SubscribeBlackListVO_001 {
private String userUuid;
private String userPhone;
private Long time;
}
