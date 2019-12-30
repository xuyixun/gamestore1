package com.ijianjian.game.domain.repository;

import java.util.Optional;
import com.ijianjian.core.common.interfaces.IRepository;
import com.ijianjian.game.domain.po.ColumnAppDetail;

public interface ColumnAppDetailRepository extends IRepository<ColumnAppDetail, String> {
Optional<ColumnAppDetail> findByColumnApp_fUuidAndFLanguageNumber(String uuid, String number);
}
