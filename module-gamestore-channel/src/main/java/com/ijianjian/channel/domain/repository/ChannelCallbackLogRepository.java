package com.ijianjian.channel.domain.repository;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import com.ijianjian.channel.domain.dto.ChannelCallbackLogSearchDTO;
import com.ijianjian.channel.domain.po.ChannelCallbackLog;
import com.ijianjian.core.common.interfaces.IRepository;

public interface ChannelCallbackLogRepository extends IRepository<ChannelCallbackLog, String> {
default Page<ChannelCallbackLog> query(ChannelCallbackLogSearchDTO dto) {
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

Boolean existsByChannelNumberAndClickId(Integer channelNumber, String clickId);
}
