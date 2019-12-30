package com.ijianjian.core.domain.util;

import java.util.List;

import org.springframework.data.domain.Sort.Order;

import com.google.common.collect.Lists;

public interface OrderDTO {
String getQueryOrder();

default List<Order> order() {
	List<Order> orders = Lists.newArrayList();
	for (String orderParam : getQueryOrder().split(",")) {
		String[] param = orderParam.split("_");
		if (param[1].equals("asc")) {
			orders.add(Order.asc(param[0]));
		} else {
			orders.add(Order.desc(param[0]));
		}
	}
	return orders;
}
}
