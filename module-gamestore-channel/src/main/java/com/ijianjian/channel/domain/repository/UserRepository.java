package com.ijianjian.channel.domain.repository;

import java.util.List;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import com.google.common.base.Strings;
import com.ijianjian.channel.domain.dto.UserSearchDTO;
import com.ijianjian.core.common.interfaces.IRepository;
import com.ijianjian.core.domain.user.po.CoreUser;

public interface UserRepository extends IRepository<CoreUser, String> {
default Page<CoreUser> query(UserSearchDTO userInfoSearchDTO) {
	Pageable pageable = PageRequest.of(userInfoSearchDTO.getPage(), userInfoSearchDTO.getSize(), Sort.by(Order.desc("fCreateTime")));
	return this.findAll((root, query, cb) -> {
		Predicate predicate = cb.conjunction();
		List<Expression<Boolean>> expressions = predicate.getExpressions();
		if (!Strings.isNullOrEmpty(userInfoSearchDTO.getName())) {
			expressions.add(cb.like(root.get("fUserName"), "%" + userInfoSearchDTO.getName() + "%"));
		}
		expressions.add(cb.isFalse(root.get("fDeleted")));
		return predicate;
	}, pageable);
}

List<CoreUser> findByFFirstLoginTimeIsNullAndFLastLoginTimeIsNotNull();
}
