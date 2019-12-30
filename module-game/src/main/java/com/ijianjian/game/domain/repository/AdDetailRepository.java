package com.ijianjian.game.domain.repository;

import java.util.Optional;

import com.ijianjian.core.common.interfaces.IRepository;
import com.ijianjian.game.domain.po.AdDetail;

public interface AdDetailRepository extends IRepository<AdDetail, String> {
Optional<AdDetail> findByAd_fUuidAndFLanguageNumber(String uuid, String number);
}
