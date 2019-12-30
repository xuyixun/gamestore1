package com.ijianjian.user.domain.po;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

@Entity(name = "t_log_lp")
@Builder
@Getter
@Setter
public class LandingPageLog {
@Tolerate
LandingPageLog() {
}

@Id
@GeneratedValue(generator = "uuid")
@GenericGenerator(name = "uuid", strategy = "uuid2")
private String fUuid;
@CreationTimestamp
private LocalDateTime fTime;

private String fIp;
private String fMac;
private String fChannelParams;
@Lob
@Column(columnDefinition = "text")
private String fHeader;
private String fPhone;

private String channelNumber;
private Boolean fNeedCallback;
private Boolean fCallback;

private String gameInfoUuid;
private String columnUuid;
}
