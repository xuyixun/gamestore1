package com.ijianjian.user.domain.po;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import com.ijianjian.user.util.FieldConstant.UserMemberCycleType;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

@Entity(name = "t_user_member_cycle")
@Builder
@Getter
@Setter
public class UserMemberCycle {
@Tolerate
UserMemberCycle() {
}

@Id
@GeneratedValue(generator = "uuid")
@GenericGenerator(name = "uuid", strategy = "uuid2")
private String fUuid;
@CreationTimestamp
private LocalDateTime fTime;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn
private UserInfo userInfo;
private LocalDateTime fStart;
private LocalDateTime fEnd;
private int fCycleCount;
private String fNumber;
@Enumerated(EnumType.STRING)
private UserMemberCycleType type;
private String channelNumber;
}
