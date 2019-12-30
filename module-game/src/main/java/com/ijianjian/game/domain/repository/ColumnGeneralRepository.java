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

import com.google.common.base.Strings;
import com.ijianjian.core.common.interfaces.IRepository;
import com.ijianjian.game.domain.dto.ColumnGeneralSearchDTO;
import com.ijianjian.game.domain.po.ColumnGeneral;

public interface ColumnGeneralRepository extends IRepository<ColumnGeneral, String> {
default Page<ColumnGeneral> query(ColumnGeneralSearchDTO dto) {
	Pageable pageable = PageRequest.of(dto.getPage(), dto.getSize(), Sort.by(Order.asc("fChild"),Order.desc("fOrder"), Order.desc("fName")));
	return this.findAll((root, query, cb) -> {
		Predicate predicate = cb.conjunction();
		List<Expression<Boolean>> expressions = predicate.getExpressions();
		if (!Strings.isNullOrEmpty(dto.getName())) {
			expressions.add(cb.equal(root.get("fName"), dto.getName()));
		}
		if (dto.getChild() != null) {
			expressions.add(dto.getChild() ? cb.isTrue(root.get("fChild")) : cb.isFalse(root.get("fChild")));
		}
		expressions.add(cb.isFalse(root.get("fDeleted")));
		return predicate;
	}, pageable);
}

@Transactional
@Modifying
@Query(value = "update t_game_column_general set f_deleted=true,f_deleted_time=?2 where f_uuid=?1", nativeQuery = true)
void delete(String fUuid, LocalDateTime time);

List<ColumnGeneral> findByFDeletedFalseAndFChildFalse(Sort sort);
}
