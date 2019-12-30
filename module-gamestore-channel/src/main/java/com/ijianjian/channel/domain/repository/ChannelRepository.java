package com.ijianjian.channel.domain.repository;

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
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.google.common.base.Strings;
import com.ijianjian.channel.domain.dto.ChannelSearchDTO;
import com.ijianjian.channel.domain.po.Channel;
import com.ijianjian.core.common.interfaces.IRepository;

public interface ChannelRepository extends IRepository<Channel, String> {
default Page<Channel> query(ChannelSearchDTO dto) {
	Pageable pageable = PageRequest.of(dto.getPage(), dto.getSize());
	if (!Strings.isNullOrEmpty(dto.getQueryOrder())) {
		pageable = PageRequest.of(dto.getPage(), dto.getSize(), Sort.by(dto.order()));
	}
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
@Query(value = "update t_channel set f_deleted=true,f_deleted_time=?2 where f_uuid=?1", nativeQuery = true)
void delete(String fUuid, LocalDateTime time);

@Query(value = "select max(f_number) from t_channel where f_deleted=false", nativeQuery = true)
Integer maxNumber();

@Query(value = "select f_hw_url from t_channel where f_deleted=false and f_number = ?1", nativeQuery = true)
String hwUrl(Integer number);

@Query(value = "select f_call_back_url from t_channel where f_deleted=false and f_number = ?1", nativeQuery = true)
String callBackUrl(Integer number);

Optional<Channel> findByFDeletedFalseAndFNumber(Integer number);

@Query(value = "select f_threshold from t_channel where f_deleted=false and f_number = ?1", nativeQuery = true)
Integer queryThreshold(Integer number);
}
