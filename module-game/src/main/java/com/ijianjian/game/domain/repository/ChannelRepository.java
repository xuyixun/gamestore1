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

import com.google.common.base.Strings;
import com.ijianjian.core.common.interfaces.IRepository;
import com.ijianjian.game.domain.po.Channel;
import com.ijianjian.game.domain.dto.ChannelSearchDTO;

public interface ChannelRepository extends IRepository<Channel, String> {
default Page<Channel> query(ChannelSearchDTO dto) {
	Pageable pageable = PageRequest.of(dto.getPage(), dto.getSize(), Sort.by(Order.desc("fCreateTime")));
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
@Query(value = "update t_game_channel set f_deleted=true,f_deleted_time=?2 where f_uuid=?1", nativeQuery = true)
void delete(String fUuid, LocalDateTime time);

@Query(value = "select max(f_number) from t_game_channel where f_deleted=false", nativeQuery = true)
String maxNumber();

Boolean existsByFDeletedFalseAndColumnGeneral_fUuid(String uuid);

@Query(value = "select f_uuid,channel_number from t_log_lp where f_need_callback = true and f_callback is null limit 0,500", nativeQuery = true)
List<String[]> query001();

@Transactional
@Modifying
@Query(value = "update t_log_lp set f_callback=true where f_uuid=?1", nativeQuery = true)
void updateNeedCallBack(String uuid);

Optional<Channel> findByFNumber(String number);
}
