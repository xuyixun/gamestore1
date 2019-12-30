package com.ijianjian.core.domain.log.po;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "t_log_request")
@Builder
@Getter
@Setter
public class LogRequest {
@Tolerate
LogRequest() {
}

@Id
@GeneratedValue(generator = "uuid")
@GenericGenerator(name = "uuid", strategy = "uuid2")
private String fUuid;
@CreationTimestamp
private LocalDateTime fTime;
private String fUserUuid;
private String fClientIp;
private String fServerIp;
private String fMethod;
private String fPath;
@Lob
@Column(columnDefinition = "text")
private String fParam;
@Lob
@Column(columnDefinition = "text")
private String fHeader;
@Lob
@Column(columnDefinition = "text")
private String fFormParam;
private int fStatus;
}
