package com.ijianjian.core.domain.user.po;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "t_core_user")
@Builder
@Getter
@Setter
public class CoreUser {
@Tolerate
CoreUser() {
}

@Id
@GeneratedValue(generator = "uuid")
@GenericGenerator(name = "uuid", strategy = "uuid2")
private String fUuid;
@CreationTimestamp
private LocalDateTime fCreateTime;
@UpdateTimestamp
private LocalDateTime fUpdateTime;
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn
private CoreUser createUser;

private boolean fDeleted;
private LocalDateTime fDeletedTime;

private String fUserName;
private String fPassword;

private String fRoles;

private boolean fEnable;

private LocalDateTime fFirstLoginTime;
private LocalDateTime fLastLoginTime;

private String sn;

private boolean fOnline;
}
