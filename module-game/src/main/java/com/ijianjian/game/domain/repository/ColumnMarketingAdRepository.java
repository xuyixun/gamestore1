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
import com.ijianjian.game.domain.dto.ColumnMarketingAdSearchDTO;
import com.ijianjian.game.domain.po.ColumnMarketingAd;

public interface ColumnMarketingAdRepository extends IRepository<ColumnMarketingAd, String> {
default Page<ColumnMarketingAd> query(ColumnMarketingAdSearchDTO dto) {
	Pageable pageable = PageRequest.of(dto.getPage(), dto.getSize(), Sort.by(Order.asc("fOrder")));
	return this.findAll((root, query, cb) -> {
		Predicate predicate = cb.conjunction();
		List<Expression<Boolean>> expressions = predicate.getExpressions();
		expressions.add(cb.equal(root.get("columnMarketing").get("fUuid"), dto.getColumnMarketingUuid()));
		if (!Strings.isNullOrEmpty(dto.getAdName())) {
			expressions.add(cb.like(root.get("ad").get("fName"), "%" + dto.getAdName() + "%"));
		}
		return predicate;
	}, pageable);
}

Page<ColumnMarketingAd> findByColumnMarketing_fUuid(String uuid, Pageable pageable);

Boolean existsByAd_fUuid(String uuid);

List<ColumnMarketingAd> findByColumnMarketing_fUuid(String uuid, Sort sort);

Boolean existsByColumnMarketing_fUuidAndAd_fUuid(String cmUuid, String adUuid);

@Query(value = "select max(f_order) from t_game_column_marketing_ad where column_marketing_f_uuid = ?1", nativeQuery = true)
Integer maxOrder(String columnUuid);

@Query(value = "select f_order from t_game_column_marketing_ad where f_uuid = ?1", nativeQuery = true)
Integer order(String uuid);

@Transactional
@Modifying
@Query(value = "update t_game_column_marketing_ad set f_order = (f_order -1) where f_order >?2 and column_marketing_f_uuid=?1", nativeQuery = true)
void updateDelete(String uuid, Integer order);

@Transactional
@Modifying
@Query(value = "update t_game_column_marketing_ad set f_order = (f_order +1) where f_order <?2 and column_marketing_f_uuid=?1", nativeQuery = true)
void updateTop(String uuid, Integer order);

@Transactional
@Modifying
@Query(value = "update t_game_column_marketing_ad set f_order = 1 where f_uuid=?1 ", nativeQuery = true)
void updateTop(String uuid);

@Transactional
@Modifying
@Query(value = "update t_game_column_marketing_ad set f_order = ?2 where (f_order+1)=?2 and column_marketing_f_uuid=?1", nativeQuery = true)
void updateUp(String uuid, Integer order);

@Transactional
@Modifying
@Query(value = "update t_game_column_marketing_ad set f_order = (f_order -1) where f_uuid=?1", nativeQuery = true)
void updateUp(String uuid);

@Transactional
@Modifying
@Query(value = "update t_game_column_marketing_ad set f_order = ?2 where (f_order-1)=?2 and column_marketing_f_uuid=?1", nativeQuery = true)
void updateDown(String uuid, Integer order);

@Transactional
@Modifying
@Query(value = "update t_game_column_marketing_ad set f_order = (f_order +1) where f_uuid=?1", nativeQuery = true)
void updateDown(String uuid);
}
