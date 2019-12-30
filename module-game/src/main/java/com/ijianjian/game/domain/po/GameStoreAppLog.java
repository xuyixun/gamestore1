package com.ijianjian.game.domain.po;

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

import com.ijianjian.game.util.FieldConstant.GameStoreAppLogType;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

@Entity(name = "t_log_app")
@Builder
@Getter
@Setter
public class GameStoreAppLog {
@Tolerate
GameStoreAppLog() {
}

@Id
@GeneratedValue(generator = "uuid")
@GenericGenerator(name = "uuid", strategy = "uuid2")
private String fUuid;
@CreationTimestamp
private LocalDateTime fTime;

private String fIp;
private String fMac;
private String fVersion;
private String channelNumber;
@Lob
@Column(columnDefinition = "text")
private String fHeader;
private String fSystem;
private String fPhoneModel;
private String fPhone;

@Enumerated(EnumType.STRING)
private GameStoreAppLogType type;
}
