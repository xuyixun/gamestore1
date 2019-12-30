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
import com.ijianjian.game.domain.dto.ColumnMarketingSearchDTO;
import com.ijianjian.game.domain.po.ColumnMarketing;

public interface ColumnMarketingRepository extends IRepository<ColumnMarketing, String> {
default Page<ColumnMarketing> query(ColumnMarketingSearchDTO dto) {
	Pageable pageable = PageRequest.of(dto.getPage(), dto.getSize(), Sort.by(Order.desc("fOrder"), Order.desc("fName")));
	return this.findAll((root, query, cb) -> {
		Predicate predicate = cb.conjunction();
		List<Expression<Boolean>> expressions = predicate.getExpressions();
		if (!Strings.isNullOrEmpty(dto.getName())) {
			expressions.add(cb.equal(root.get("fName"), dto.getName()));
		}
		expressions.add(cb.isFalse(root.get("fDeleted")));
		return predicate;
	}, pageable);
}

@Transactional
@Modifying
@Query(value = "update t_game_column_marketing set f_deleted=true,f_deleted_time=?2,f_order_home=0 where f_uuid=?1", nativeQuery = true)
void delete(String fUuid, LocalDateTime time);

List<ColumnMarketing> findByFDeletedFalseAndFOrderHomeNot(Integer orderHome, Sort sort);

Boolean existsByFUuidAndFOrderHomeNot(String uuid, Integer orderHome);

@Transactional
@Modifying
@Query(value = "update t_game_column_marketing set f_order_home=?2 where f_uuid=?1", nativeQuery = true)
void orderHome(String fUuid, Integer order);

@Query(value = "select max(f_order_home) from t_game_column_marketing where f_order_home <> 0", nativeQuery = true)
Integer maxOrder(String columnUuid);

@Query(value = "select f_order_home from t_game_column_marketing where f_uuid = ?1", nativeQuery = true)
Integer order(String uuid);

@Transactional
@Modifying
@Query(value = "update t_game_column_marketing set f_order_home = (f_order_home -1) where f_order_home >?1", nativeQuery = true)
void updateDelete(Integer order);

@Transactional
@Modifying
@Query(value = "update t_game_column_marketing set f_order_home=0 where f_uuid=?1", nativeQuery = true)
void removeHome(String uuid);

@Transactional
@Modifying
@Query(value = "update t_game_column_marketing set f_order_home = (f_order_home +1) where f_order_home <?1 and f_order_home <> 0", nativeQuery = true)
void updateTop(Integer order);

@Transactional
@Modifying
@Query(value = "update t_game_column_marketing set f_order_home = 1 where f_uuid=?1 and f_order_home <> 0", nativeQuery = true)
void updateTop(String uuid);

@Transactional
@Modifying
@Query(value = "update t_game_column_marketing set f_order_home = ?1 where (f_order_home+1)=?1 and f_order_home <> 0", nativeQuery = true)
void updateUp(Integer order);

@Transactional
@Modifying
@Query(value = "update t_game_column_marketing set f_order_home = (f_order_home -1) where f_uuid=?1 and f_order_home <> 0", nativeQuery = true)
void updateUp(String uuid);

@Transactional
@Modifying
@Query(value = "update t_game_column_marketing set f_order_home = ?1 where (f_order_home-1)=?1 and f_order_home <> 0", nativeQuery = true)
void updateDown(Integer order);

@Transactional
@Modifying
@Query(value = "update t_game_column_marketing set f_order_home = (f_order_home +1) where f_uuid=?1", nativeQuery = true)
void updateDown(String uuid);
}
