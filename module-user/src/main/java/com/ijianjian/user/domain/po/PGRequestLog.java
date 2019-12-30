package com.ijianjian.user.domain.po;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

@Entity(name = "t_pg_request_log")
@Builder
@Getter
@Setter
public class PGRequestLog {
@Tolerate
PGRequestLog() {
}

@Id
@GeneratedValue(generator = "uuid")
@GenericGenerator(name = "uuid", strategy = "uuid2")
private String fUuid;
@CreationTimestamp
private LocalDateTime fTime;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn
private PGRequest pgRequest;

private String fUrl;
@Lob
@Column(columnDefinition = "text")
private String fParam;

@CreationTimestamp
private LocalDateTime fRequestTime;

@Lob
@Column(columnDefinition = "text")
private String fReponse;
private String fCode;
@CreationTimestamp
private LocalDateTime fReponseTime;
}
