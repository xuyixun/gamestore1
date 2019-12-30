package com.ijianjian.user.domain.po;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import com.ijianjian.user.util.FieldConstant.PGInterFaceStatus;
import com.ijianjian.user.util.FieldConstant.PGInterFaceType;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

@Entity(name = "t_pg_request")
@Builder
@Getter
@Setter
public class PGRequest {
@Tolerate
PGRequest() {
}

@Id
@GeneratedValue(generator = "uuid")
@GenericGenerator(name = "uuid", strategy = "uuid2")
private String fUuid;
@CreationTimestamp
private LocalDateTime fTime;

@Enumerated(EnumType.STRING)
private PGInterFaceType type;
@Enumerated(EnumType.STRING)
private PGInterFaceStatus status;
private String fUserUuid;
private String fNumber;

private String phone;
}
