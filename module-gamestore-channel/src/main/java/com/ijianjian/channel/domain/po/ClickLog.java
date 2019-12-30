package com.ijianjian.channel.domain.po;

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

@Entity(name = "t_click_log")
@Builder
@Getter
@Setter
public class ClickLog {
@Tolerate
ClickLog() {
}

@Id
@GeneratedValue(generator = "uuid")
@GenericGenerator(name = "uuid", strategy = "uuid2")
private String fUuid;
@CreationTimestamp
private LocalDateTime time;

private Integer channelNumber;
private String clickId;

private String ip;
@Lob
@Column(columnDefinition = "text")
private String params;
private String url;

}
