package com.ijianjian.channel.domain.repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

import com.ijianjian.channel.domain.dto.HuaWeiChannelLogSearchDTO;
import com.ijianjian.channel.domain.po.HuaWeiChannelLog;
import com.ijianjian.core.common.interfaces.IRepository;

public interface HuaWeiChannelLogRepository extends IRepository<HuaWeiChannelLog, String> {
default Page<HuaWeiChannelLog> query(HuaWeiChannelLogSearchDTO dto) {
	Pageable pageable = PageRequest.of(dto.getPage(), dto.getSize(), Sort.by(Order.desc("time")));
	return this.findAll((root, query, cb) -> {
		Predicate predicate = cb.conjunction();
		List<Expression<Boolean>> expressions = predicate.getExpressions();
		if (dto.getChannelNumbser() != null) {
			expressions.add(cb.equal(root.get("channelNumber"), dto.getChannelNumbser()));
		}
		if (dto.getStartTime() != null) {
			expressions.add(cb.greaterThanOrEqualTo(root.get("time"), Instant.ofEpochMilli(dto.getStartTime()).atZone(ZoneId.systemDefault()).toLocalDateTime()));
		}
		if (dto.getEndTime() != null) {
			expressions.add(cb.lessThanOrEqualTo(root.get("time"), Instant.ofEpochMilli(dto.getEndTime()).atZone(ZoneId.systemDefault()).toLocalDateTime()));
		}
		return predicate;
	}, pageable);
}

Boolean existsByClickId(String clickId);

List<HuaWeiChannelLog> findByCallbackFalseAndTimeBetween(LocalDateTime beg, LocalDateTime end);

@Transactional
@Modifying
@Query(value = "update t_huawei_channel_log set callback=true where f_uuid=?1", nativeQuery = true)
void callback(String fUuid);

@Query(value = "select count(*) from t_huawei_channel_log where channel_number = ?1 and callback_type = 'send_success' and date_format(time,'%Y%m%d') = ?2", nativeQuery = true)
Integer quereyDayCount(Integer channelNumber, String day);

@Transactional
@Modifying
@Query(value = "update t_huawei_channel_log set callback_type=?2 where f_uuid=?1", nativeQuery = true)
void callbackType(String fUuid, String callbackType);
}
