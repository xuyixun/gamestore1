package com.ijianjian.user.domain.po;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.ijianjian.core.domain.user.po.CoreUser;
import com.ijianjian.user.util.FieldConstant.Sex;
import com.ijianjian.user.util.FieldConstant.UserSource;
import com.ijianjian.user.util.FieldConstant.UserType;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

@Entity(name = "t_user_info")
@Builder
@Getter
@Setter
public class UserInfo {
@Tolerate
UserInfo() {
}

@Id
private String id;
@CreationTimestamp
private LocalDateTime fCreateTime;
@UpdateTimestamp
private LocalDateTime fUpdateTime;

@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
@MapsId
private CoreUser core;
@Enumerated(EnumType.STRING)
private UserType type;

private String fName;
private String fPhone;
private String fNickeName;
@Enumerated(EnumType.STRING)
private Sex sex;
private LocalDate fBirthday;
private String fEmail;
private String fAddress;
private String fPhoto;

@Enumerated(EnumType.STRING)
private UserSource source;

private String fSmsCode;

private LocalDateTime fSubscribeTime;
private LocalDateTime fUnSubscribeTime;

private String channelNumber;
}
