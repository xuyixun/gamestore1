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

import com.ijianjian.core.domain.user.po.CoreUser;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

@Entity(name = "t_game_my")
@Builder
@Getter
@Setter
public class MyGame {
@Tolerate
MyGame() {
}

@Id
@GeneratedValue(generator = "uuid")
@GenericGenerator(name = "uuid", strategy = "uuid2")
private String fUuid;
@CreationTimestamp
private LocalDateTime fTime;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn
private CoreUser user;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn
private GameInfo gameInfo;
}
