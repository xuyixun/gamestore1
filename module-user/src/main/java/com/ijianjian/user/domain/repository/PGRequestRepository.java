package com.ijianjian.user.domain.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.ijianjian.core.common.interfaces.IRepository;
import com.ijianjian.user.domain.po.PGRequest;
import com.ijianjian.user.util.FieldConstant.PGInterFaceStatus;
import com.ijianjian.user.util.FieldConstant.PGInterFaceType;

public interface PGRequestRepository extends IRepository<PGRequest, String> {
List<PGRequest> findByTypeAndStatus(PGInterFaceType type, PGInterFaceStatus status);

@Transactional
@Modifying
@Query(value = "update t_pg_request set status=?2 where f_uuid=?1", nativeQuery = true)
void success(String fUuid, String status);
}
