package com.ijianjian.game.domain.po;

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

@Entity(name = "t_game_column_general")
@Builder
@Getter
@Setter
public class ColumnGeneral {
@Tolerate
ColumnGeneral() {
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

private int fOrder;

private String fName;
private boolean fChild;
private String fIcon;
private String fBackground;
private String fDetail;
}
