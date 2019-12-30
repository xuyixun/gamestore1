package com.ijianjian.channel.domain.po;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

@Entity(name = "t_channel_callback_log")
@Builder
@Getter
@Setter
public class ChannelCallbackLog {
@Tolerate
ChannelCallbackLog() {
}

@Id
@GeneratedValue(generator = "uuid")
@GenericGenerator(name = "uuid", strategy = "uuid2")
private String fUuid;
@CreationTimestamp
private LocalDateTime time;

private String hwLogId;
private Integer channelNumber;
private String clickId;
private String url;
private String result;
}
