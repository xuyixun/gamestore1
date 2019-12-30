package com.ijianjian.game.domain.repository;

import java.util.List;
import java.util.Optional;

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
import com.ijianjian.game.domain.dto.ColumnGeneralGameSearchDTO;
import com.ijianjian.game.domain.po.ColumnGeneralGame;

public interface ColumnGeneralGameRepository extends IRepository<ColumnGeneralGame, String> {
default Page<ColumnGeneralGame> query(ColumnGeneralGameSearchDTO dto) {
	Pageable pageable = PageRequest.of(dto.getPage(), dto.getSize(), Sort.by(Order.asc("fOrder")));
	return this.findAll((root, query, cb) -> {
		Predicate predicate = cb.conjunction();
		List<Expression<Boolean>> expressions = predicate.getExpressions();
		expressions.add(cb.equal(root.get("columnGeneral").get("fUuid"), dto.getColumnGeneralUuid()));
		if (!Strings.isNullOrEmpty(dto.getGameName())) {
			expressions.add(cb.like(root.get("gameInfo").get("fName"), "%" + dto.getGameName() + "%"));
		}
		return predicate;
	}, pageable);
}

Optional<ColumnGeneralGame> findByGameInfo_fUuid(String uuid);

Page<ColumnGeneralGame> findByColumnGeneral_fUuid(String uuid, Pageable pageable);

List<ColumnGeneralGame> findByColumnGeneral_fUuid(String uuid, Sort sort);

Boolean existsByGameInfo_fUuid(String gameUuid);

@Query(value = "select f_child from t_game_column_general where f_uuid = ?1", nativeQuery = true)
Boolean child(String columnGeneralUuid);

@Query(value = "select max(f_order) from t_game_column_general_game where column_general_f_uuid = ?1", nativeQuery = true)
Integer maxOrder(String columnGeneralUuid);

@Query(value = "select f_order from t_game_column_general_game where f_uuid =?1", nativeQuery = true)
Integer order(String uuid);

@Transactional
@Modifying
@Query(value = "update t_game_column_general_game set f_order = (f_order -1) where f_order >?2 and column_general_f_uuid=?1", nativeQuery = true)
void updateDelete(String uuid, Integer order);

@Transactional
@Modifying
@Query(value = "update t_game_column_general_game set f_order = (f_order +1) where f_order <?2 and column_general_f_uuid=?1", nativeQuery = true)
void updateTop(String uuid, Integer order);

@Transactional
@Modifying
@Query(value = "update t_game_column_general_game set f_order = 1 where f_uuid=?1 ", nativeQuery = true)
void updateTop(String uuid);

@Transactional
@Modifying
@Query(value = "update t_game_column_general_game set f_order = ?2 where (f_order+1)=?2 and column_general_f_uuid=?1", nativeQuery = true)
void updateUp(String uuid, Integer order);

@Transactional
@Modifying
@Query(value = "update t_game_column_general_game set f_order = (f_order -1) where f_uuid=?1", nativeQuery = true)
void updateUp(String uuid);

@Transactional
@Modifying
@Query(value = "update t_game_column_general_game set f_order = ?2 where (f_order-1)=?2 and column_general_f_uuid=?1", nativeQuery = true)
void updateDown(String uuid, Integer order);

@Transactional
@Modifying
@Query(value = "update t_game_column_general_game set f_order = (f_order +1) where f_uuid=?1", nativeQuery = true)
void updateDown(String uuid);
}
