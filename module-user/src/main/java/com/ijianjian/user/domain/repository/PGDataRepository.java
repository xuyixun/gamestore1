package com.ijianjian.user.domain.repository;

import org.springframework.data.jpa.repository.Query;

import com.ijianjian.core.common.interfaces.IRepository;
import com.ijianjian.user.domain.po.PGData;

public interface PGDataRepository extends IRepository<PGData, String> {
@Query(value = "select sum(deduct_case) from t_pg_data t1 left join t_pg_bill t2 on t1.pgbill_f_uuid = t2.f_uuid where t1.billcycledate = ?1 and t2.f_date between ?2 and ?3", nativeQuery = true)
Long today(String cycleDay, String billStart, String billEnd);

@Query(value = "select sum(deduct_case) from t_pg_data t1 left join t_pg_bill t2 on t1.pgbill_f_uuid = t2.f_uuid where t1.billcycledate between ?1 and ?2 and t2.f_date between ?3 and ?4", nativeQuery = true)
Long month(String cycleDayStart, String cycleDayEnd, String billStart, String billEnd);

@Query(value = "select sum(deduct_case) from t_pg_data t1 left join t_pg_bill t2 on t1.pgbill_f_uuid = t2.f_uuid where t1.billcycledate <= ?1 and t2.f_date <= ?2", nativeQuery = true)
Long all(String cycleDay, String billEnd);
}
