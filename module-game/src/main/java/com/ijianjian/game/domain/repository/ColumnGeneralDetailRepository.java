package com.ijianjian.game.domain.repository;

import java.util.Optional;

import com.ijianjian.core.common.interfaces.IRepository;
import com.ijianjian.game.domain.po.ColumnGeneralDetail;

public interface ColumnGeneralDetailRepository extends IRepository<ColumnGeneralDetail, String> {
Optional<ColumnGeneralDetail> findByColumnGeneral_fUuidAndFLanguageNumber(String uuid, String number);
}
