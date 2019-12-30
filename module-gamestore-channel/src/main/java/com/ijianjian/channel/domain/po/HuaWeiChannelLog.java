package com.ijianjian.channel.domain.po;

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

import com.ijianjian.channel.util.FieldConstant.HuaWeiChannelLogType;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

@Entity(name = "t_huawei_channel_log")
@Builder
@Getter
@Setter
public class HuaWeiChannelLog {
@Tolerate
HuaWeiChannelLog() {
}

@Id
@GeneratedValue(generator = "uuid")
@GenericGenerator(name = "uuid", strategy = "uuid2")
private String fUuid;
@CreationTimestamp
private LocalDateTime time;

private String ip;
@Lob
@Column(columnDefinition = "text")
private String params;
private Integer channelNumber;
private String clickId;

@Enumerated(EnumType.STRING)
private HuaWeiChannelLogType type;

private boolean callback;
private String callbackType;
}
