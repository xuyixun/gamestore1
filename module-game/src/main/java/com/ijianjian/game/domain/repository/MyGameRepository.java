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
import com.ijianjian.game.domain.dto.MyGameSearchDTO;
import com.ijianjian.game.domain.po.MyGame;

public interface MyGameRepository extends IRepository<MyGame, String> {
default Page<MyGame> query(MyGameSearchDTO dto) {
	Pageable pageable = PageRequest.of(dto.getPage(), dto.getSize(), Sort.by("gameInfo.fName"));
	return this.findAll((root, query, cb) -> {
		Predicate predicate = cb.conjunction();
		List<Expression<Boolean>> expressions = predicate.getExpressions();
		if (!Strings.isNullOrEmpty(dto.getName())) {
			expressions.add(cb.like(root.get("fName"), "%" + dto.getName() + "%"));
		}
		expressions.add(cb.equal(root.get("user").get("fUuid"), dto.getUserUuid()));
		return predicate;
	}, pageable);
}

Boolean existsByUser_fUuidAndGameInfo_fUuid(String userUuid, String gameInfoUuid);

@Transactional
@Modifying
@Query(value = "update t_game_my set f_deleted=true,f_deleted_time=?2 where f_uuid=?1", nativeQuery = true)
void delete(String fUuid, LocalDateTime time);

void deleteByGameInfo_fUuid(String uuid);
}
