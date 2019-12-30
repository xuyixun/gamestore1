package com.ijianjian.channel.domain.po;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import com.ijianjian.core.domain.user.po.CoreUser;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

@Entity(name = "t_channel")
@Builder
@Getter
@Setter
public class Channel {
@Tolerate
Channel() {
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

private int fNumber;
private String fName;

private String fScheme;
private String fHost;
private String fUrl;
private String fClickId;

private String fChannelUrl;
private String fHwUrl;
private String fCallBackUrl;

private int fThreshold;
}
