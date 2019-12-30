package com.ijianjian.game.domain.repository;

import java.time.LocalDateTime;
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

import com.ijianjian.core.common.interfaces.IRepository;
import com.ijianjian.game.domain.dto.GameStoreAppSearchDTO;
import com.ijianjian.game.domain.po.GameStoreApp;

public interface GameStoreAppRepository extends IRepository<GameStoreApp, String> {
default Page<GameStoreApp> query(GameStoreAppSearchDTO dto) {
	Pageable pageable = PageRequest.of(dto.getPage(), dto.getSize(), Sort.by(Order.desc("fVersion")));
	return this.findAll((root, query, cb) -> {
		Predicate predicate = cb.conjunction();
		List<Expression<Boolean>> expressions = predicate.getExpressions();
		expressions.add(cb.isFalse(root.get("fDeleted")));
		return predicate;
	}, pageable);
}

@Transactional
@Modifying
@Query(value = "update t_game_store_app set f_deleted=true,f_deleted_time=?2 where f_uuid=?1", nativeQuery = true)
void delete(String fUuid, LocalDateTime time);

@Transactional
@Modifying
@Query(value = "update t_game_store_app set f_login_version=false", nativeQuery = true)
void updateAllLoginFalse();

@Transactional
@Modifying
@Query(value = "update t_game_store_app set f_login_version=true where f_uuid = ?1", nativeQuery = true)
void updateLoginTrue(String uuid);

@Transactional
@Modifying
@Query(value = "update t_game_store_app set f_login_version=false where f_uuid = ?1", nativeQuery = true)
void updateLoginFalse(String uuid);

Boolean existsByFDeletedFalseAndFVersion(String version);

Boolean existsByFDeletedFalseAndFVersionGreaterThan(String version);

Boolean existsByFDeletedFalseAndFLoginVersionTrueAndFVersionGreaterThan(String version);

List<GameStoreApp> findByFDeletedFalse(Sort sort);

Optional<GameStoreApp> findByFLoginVersionTrue();

Optional<GameStoreApp> findTop1ByFDeletedFalse(Sort sort);

Optional<GameStoreApp> findByFDeletedFalseAndFVersion(String version);
}
