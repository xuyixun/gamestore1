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
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.google.common.base.Strings;
import com.ijianjian.core.common.interfaces.IRepository;
import com.ijianjian.game.domain.dto.AdSearchDTO;
import com.ijianjian.game.domain.po.Ad;

public interface AdRepository extends IRepository<Ad, String> {
default Page<Ad> query(AdSearchDTO dto) {
	Pageable pageable = PageRequest.of(dto.getPage(), dto.getSize(), Sort.by("fName"));
	return this.findAll((root, query, cb) -> {
		Predicate predicate = cb.conjunction();
		List<Expression<Boolean>> expressions = predicate.getExpressions();
		if (!Strings.isNullOrEmpty(dto.getName())) {
			expressions.add(cb.like(root.get("fName"), "%" + dto.getName() + "%"));
		}
		expressions.add(cb.isFalse(root.get("fDeleted")));
		return predicate;
	}, pageable);
}

@Transactional
@Modifying
@Query(value = "update t_game_ad set f_deleted=true,f_deleted_time=?2 where f_uuid=?1", nativeQuery = true)
void delete(String fUuid, LocalDateTime time);

Boolean existsByFDeletedFalseAndColumnGeneral_fUuid(String uuid);
}
