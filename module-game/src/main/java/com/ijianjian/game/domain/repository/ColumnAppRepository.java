package com.ijianjian.game.domain.repository;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.ijianjian.core.common.interfaces.IRepository;
import com.ijianjian.game.domain.dto.ColumnAppSearchDTO;
import com.ijianjian.game.domain.po.ColumnApp;

public interface ColumnAppRepository extends IRepository<ColumnApp, String> {
default Page<ColumnApp> query(ColumnAppSearchDTO dto) {
	Pageable pageable = PageRequest.of(dto.getPage(), dto.getSize(), Sort.by(Order.asc("fOrder"), Order.desc("fName")));
	return this.findAll((root, query, cb) -> {
		Predicate predicate = cb.conjunction();
		List<Expression<Boolean>> expressions = predicate.getExpressions();
		expressions.add(cb.isFalse(root.get("fDeleted")));
		return predicate;
	}, pageable);
}

@Transactional
@Modifying
@Query(value = "update t_game_column_app set f_deleted=true,f_deleted_time=?2 where f_uuid=?1", nativeQuery = true)
void delete(String fUuid, LocalDateTime time);

List<ColumnApp> findByFDeletedFalse(Sort sort);
}
