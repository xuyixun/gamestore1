package com.ijianjian.game.domain.po;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import com.ijianjian.core.domain.user.po.CoreUser;
import com.ijianjian.game.util.FieldConstant.GameInfoChargeType;
import com.ijianjian.game.util.FieldConstant.GameInfoStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

@Entity(name = "t_game_info")
@Builder
@Getter
@Setter
public class GameInfo {
@Tolerate
GameInfo() {
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

private String fLanguageDefault;

@Enumerated(EnumType.STRING)
private GameInfoStatus status;

@Enumerated(EnumType.STRING)
private GameInfoChargeType chargeType;

private double fScore;
private String fVersion;
private String fApk;
private long fApkSize;
private String fApkName;
private String fIcon;

private String fName;
private String fDevelopmentCompany;
private String fTag;
@Lob
@Column(columnDefinition = "text")
private String fDetail;
private String fAdsPictures;
private String fScreenshot;

private String fOnLineTime;
private String fOffLineTime;

private int fDownloadCount;

private boolean fNeedShell;
}
