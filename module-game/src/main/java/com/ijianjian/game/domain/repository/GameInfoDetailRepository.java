package com.ijianjian.game.domain.repository;

import java.util.Optional;

import com.ijianjian.core.common.interfaces.IRepository;
import com.ijianjian.game.domain.po.GameInfoDetail;

public interface GameInfoDetailRepository extends IRepository<GameInfoDetail, String> {
Optional<GameInfoDetail> findByGameInfo_fUuidAndFLanguageNumber(String gameUuid, String number);
}
