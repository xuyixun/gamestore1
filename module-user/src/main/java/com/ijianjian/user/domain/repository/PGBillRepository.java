package com.ijianjian.user.domain.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.ijianjian.core.common.interfaces.IRepository;
import com.ijianjian.user.domain.po.PGBill;

public interface PGBillRepository extends IRepository<PGBill, String> {
Boolean existsByFDate(String date);

List<PGBill> findByFInputTableFalse();

@Transactional
@Modifying
@Query(value = "update t_pg_bill set f_input_table=true where f_uuid=?1", nativeQuery = true)
void data(String fUuid);
}
