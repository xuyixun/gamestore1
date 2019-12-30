package com.ijianjian.user.domain.po;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import com.ijianjian.user.util.FieldConstant.SubscribeType;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

@Entity(name = "t_log_subscribe")
@Builder
@Getter
@Setter
public class SubscribeLog {
@Tolerate
SubscribeLog() {
}

@Id
@GeneratedValue(generator = "uuid")
@GenericGenerator(name = "uuid", strategy = "uuid2")
private String fUuid;
@CreationTimestamp
private LocalDateTime fTime;

private String fIp;
private String fMac;
@Lob
@Column(columnDefinition = "text")
private String fHeader;
private String fPhone;

@Enumerated(EnumType.STRING)
private SubscribeType type;

private String channelNumber;
}
