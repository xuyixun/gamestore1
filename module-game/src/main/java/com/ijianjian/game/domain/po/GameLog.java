package com.ijianjian.game.domain.po;

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

import com.ijianjian.game.util.FieldConstant.GameLogClient;
import com.ijianjian.game.util.FieldConstant.GameLogFrom;
import com.ijianjian.game.util.FieldConstant.GameLogType;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

@Entity(name = "t_log_game")
@Builder
@Getter
@Setter
public class GameLog {
@Tolerate
GameLog() {
}

@Id
@GeneratedValue(generator = "uuid")
@GenericGenerator(name = "uuid", strategy = "uuid2")
private String fUuid;
@CreationTimestamp
private LocalDateTime fTime;

private String fIp;
private String fMac;
@Lob
@Column(columnDefinition = "text")
private String fHeader;
private String fGameUuid;
private String fUserUuid;

@Enumerated(EnumType.STRING)
private GameLogType type;

@Enumerated(EnumType.STRING)
private GameLogFrom gameLogForm;
private String fFromUuid;

@Enumerated(EnumType.STRING)
private GameLogClient client;
}
