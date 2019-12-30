package com.ijianjian.game.domain.repository;

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
import com.ijianjian.game.domain.dto.ColumnAppGameSearchDTO;
import com.ijianjian.game.domain.po.ColumnAppGame;

public interface ColumnAppGameRepository extends IRepository<ColumnAppGame, String> {
default Page<ColumnAppGame> query(ColumnAppGameSearchDTO dto) {
	Pageable pageable = PageRequest.of(dto.getPage(), dto.getSize(), Sort.by(Order.asc("fOrder")));
	return this.findAll((root, query, cb) -> {
		Predicate predicate = cb.conjunction();
		List<Expression<Boolean>> expressions = predicate.getExpressions();
		expressions.add(cb.equal(root.get("columnApp").get("fUuid"), dto.getColumnAppUuid()));
		if (!Strings.isNullOrEmpty(dto.getColumnName())) {
			expressions.add(cb.like(root.get("gameInfo").get("fName"), "%" + dto.getColumnName() + "%"));
		}
		return predicate;
	}, pageable);
}

List<ColumnAppGame> findByGameInfo_fUuid(String uuid);

Page<ColumnAppGame> findByColumnApp_fUuid(String uuid, Pageable pageable);

Boolean existsByColumnApp_fUuidAndGameInfo_fUuid(String columnAppUuid, String gameUuid);

@Query(value = "select max(f_order) from t_game_column_app_game where column_app_f_uuid = ?1", nativeQuery = true)
Integer maxOrder(String columnAppUuid);

@Query(value = "select f_order from t_game_column_app_game where f_uuid = ?1", nativeQuery = true)
Integer order(String uuid);

@Transactional
void deleteByColumnApp_fUuidAndGameInfo_fUuid(String columnUuid, String uuid);

@Transactional
@Modifying
@Query(value = "update t_game_column_app_game set f_order = (f_order -1) where f_order >?2 and column_app_f_uuid=?1", nativeQuery = true)
void updateDelete(String uuid, Integer order);

@Transactional
@Modifying
@Query(value = "update t_game_column_app_game set f_order = (f_order +1) where f_order <?2 and column_app_f_uuid=?1", nativeQuery = true)
void updateTop(String uuid, Integer order);

@Transactional
@Modifying
@Query(value = "update t_game_column_app_game set f_order = 1 where f_uuid=?1 ", nativeQuery = true)
void updateTop(String uuid);

@Transactional
@Modifying
@Query(value = "update t_game_column_app_game set f_order = (f_order +1) where (f_order+1)=?2 and column_app_f_uuid=?1", nativeQuery = true)
void updateUp(String uuid, Integer order);

@Transactional
@Modifying
@Query(value = "update t_game_column_app_game set f_order = (f_order -1) where f_uuid=?1", nativeQuery = true)
void updateUp(String uuid);

@Transactional
@Modifying
@Query(value = "update t_game_column_app_game set f_order = (f_order -1) where (f_order-1)=?2 and column_app_f_uuid=?1", nativeQuery = true)
void updateDown(String uuid, Integer order);

@Transactional
@Modifying
@Query(value = "update t_game_column_app_game set f_order = (f_order +1) where f_uuid=?1", nativeQuery = true)
void updateDown(String uuid);
}
