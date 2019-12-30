package com.ijianjian.game.domain.repository;

import java.util.Optional;

import com.ijianjian.core.common.interfaces.IRepository;
import com.ijianjian.game.domain.po.ColumnMarketingDetail;

public interface ColumnMarketingDetailRepository  extends IRepository<ColumnMarketingDetail, String> {
Optional<ColumnMarketingDetail> findByColumnMarketing_fUuidAndFLanguageNumber(String uuid, String number);
}
