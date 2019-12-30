package com.ijianjian.user.domain.repository;

import java.util.List;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import com.google.common.base.Strings;
import com.ijianjian.core.common.interfaces.IRepository;
import com.ijianjian.user.domain.dto.SubscribeBlackListSearchDTO;
import com.ijianjian.user.domain.po.SubscribeBlackList;

public interface SubscribeBlackListRepository extends IRepository<SubscribeBlackList, String> {
default Page<SubscribeBlackList> query(SubscribeBlackListSearchDTO searchDTO) {
	Pageable pageable = PageRequest.of(searchDTO.getPage(), searchDTO.getSize(), Sort.by(Order.desc("fTime")));
	return this.findAll((root, query, cb) -> {
		Predicate predicate = cb.conjunction();
		List<Expression<Boolean>> expressions = predicate.getExpressions();
		if (!Strings.isNullOrEmpty(searchDTO.getPhone())) {
			expressions.add(cb.like(root.get("user").get("fPhone"), "%" + searchDTO.getPhone() + "%"));
		}
		return predicate;
	}, pageable);
}

Boolean existsByUser_id(String uuid);

@Transactional
void deleteByUser_id(String uuid);
}
