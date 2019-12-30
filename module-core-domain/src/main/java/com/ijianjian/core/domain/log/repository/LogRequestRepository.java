package com.ijianjian.core.domain.log.repository;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.domain.Sort;

import com.ijianjian.core.common.interfaces.IRepository;
import com.ijianjian.core.domain.log.po.LogRequest;

public interface LogRequestRepository extends IRepository<LogRequest, String> {
Optional<LogRequest> findTopByfUserUuidAndFPathNotIn(String uuid, Collection<String> paths, Sort sort);
}
