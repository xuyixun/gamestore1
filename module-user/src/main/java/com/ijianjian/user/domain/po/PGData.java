package com.ijianjian.user.domain.po;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

@Entity(name = "t_pg_data")
@Builder
@Getter
@Setter
public class PGData {
@Tolerate
PGData() {
}

@Id
@GeneratedValue(generator = "uuid")
@GenericGenerator(name = "uuid", strategy = "uuid2")
private String fUuid;
@CreationTimestamp
private LocalDateTime fTime;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn
private PGBill pGBill;

private String msisdn;
private String country;
private String spId;
private String appId;
private String productId;
private String type;
private LocalDate billStartdate;
private LocalDate billEnddate;
private String deductCase;
private String status;
private LocalDateTime timeStamp;
private LocalDate billcycledate;
private LocalDate activeenddate;
private LocalDate graceenddate;
private String channel;
}
