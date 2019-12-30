package com.ijianjian.user.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Maps;
import com.ijianjian.core.common.object.CommonResult;
import com.ijianjian.user.domain.repository.PGDataRepository;
import com.ijianjian.user.domain.repository.UserInfoRepository;
import com.ijianjian.user.domain.repository.UserMemberCycleRepository;
import com.ijianjian.user.util.FieldConstant.UserMemberCycleType;
import com.ijianjian.user.util.FieldConstant.UserType;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "统计1")
@RestController
public class StatisticsService {
private final UserInfoRepository userInfoRepository;
private final UserMemberCycleRepository userMemberCycleRepository;
private final PGDataRepository pGDataRepository;

public StatisticsService(UserInfoRepository userInfoRepository, UserMemberCycleRepository userMemberCycleRepository, PGDataRepository pGDataRepository) {
	this.userInfoRepository = userInfoRepository;
	this.userMemberCycleRepository = userMemberCycleRepository;
	this.pGDataRepository = pGDataRepository;
}

@ApiOperation("首页")
@GetMapping("v1/user_info/home")
public CommonResult home() {
	LocalDate now = LocalDate.now();
	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
	LocalDateTime start = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0, 0);
	LocalDateTime end = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 23, 59, 59);

	LocalDate time14Before = now.minusDays(14);
	LocalDateTime start14Before = LocalDateTime.of(time14Before.getYear(), time14Before.getMonth(), time14Before.getDayOfMonth(), 0, 0, 0);
	LocalDateTime end14Before = LocalDateTime.of(time14Before.getYear(), time14Before.getMonth(), time14Before.getDayOfMonth(), 23, 59, 59);

	Map<String, Object> map = Maps.newHashMap();
	map.put("expected_all", 7 * this.userMemberCycleRepository.countByTypeInAndFStartLessThanEqual(new UserMemberCycleType[] { UserMemberCycleType.subscribe, UserMemberCycleType.renew }, end14Before));
	map.put("expected_today", 7 * this.userMemberCycleRepository.countByTypeInAndFStartBetween(new UserMemberCycleType[] { UserMemberCycleType.subscribe, UserMemberCycleType.renew }, start14Before, end14Before));

	Long actual_all = this.pGDataRepository.all(now.minusDays(7).format(dtf), now.format(dtf));
	map.put("actual_all", actual_all == null ? 0 : actual_all);
	Long actual_today = this.pGDataRepository.today(now.minusDays(7).format(dtf), now.minusDays(6).format(dtf), now.format(dtf));
	map.put("actual_today", actual_today == null ? 0 : actual_today);

	map.put("user_all", this.userInfoRepository.countByCore_fDeletedFalseAndTypeIn(new UserType[] { UserType.user_normal, UserType.user_member }));
	map.put("user_today", this.userInfoRepository.countByCore_fDeletedFalseAndTypeInAndFCreateTimeBetween(new UserType[] { UserType.user_normal, UserType.user_member }, start, end));
	map.put("member_all", this.userInfoRepository.countByCore_fDeletedFalseAndTypeIn(new UserType[] { UserType.user_member }));
	map.put("member_today", this.userInfoRepository.countByCore_fDeletedFalseAndTypeInAndFCreateTimeBetween(new UserType[] { UserType.user_member }, start, end));
	map.put("un_member_all", this.userInfoRepository.countByCore_fDeletedFalseAndTypeAndFUnSubscribeTimeIsNotNull(UserType.user_member));
	map.put("un_member_today", this.userInfoRepository.countByCore_fDeletedFalseAndTypeAndFUnSubscribeTimeIsNotNullAndFUnSubscribeTimeBetween(UserType.user_normal, start, end));

	map.put("daliy_income", this.daliyIncome(null));
	map.put("month_income", this.monthIncome(null));

	Map<String, Long[]> memberFromChannel7Day = Maps.newHashMap();
	List<String[]> userFromGameList = this.userInfoRepository.memberFromChannel7Day(now.minusDays(6).format(dtf));
	for (String[] ufg : userFromGameList) {
		String name = ufg[0];
		Long[] list;
		if (memberFromChannel7Day.containsKey(name)) {
			list = memberFromChannel7Day.get(name);
		} else {
			list = new Long[] { 0l, 0l, 0l, 0l, 0l, 0l, 0l };
		}
		if (now.minusDays(6).format(dtf).equals(ufg[1])) {
			list[0] = Long.valueOf(ufg[2]);
		} else if (now.minusDays(5).format(dtf).equals(ufg[1])) {
			list[1] = Long.valueOf(ufg[2]);
		} else if (now.minusDays(4).format(dtf).equals(ufg[1])) {
			list[2] = Long.valueOf(ufg[2]);
		} else if (now.minusDays(3).format(dtf).equals(ufg[1])) {
			list[3] = Long.valueOf(ufg[2]);
		} else if (now.minusDays(2).format(dtf).equals(ufg[1])) {
			list[4] = Long.valueOf(ufg[2]);
		} else if (now.minusDays(1).format(dtf).equals(ufg[1])) {
			list[5] = Long.valueOf(ufg[2]);
		} else if (now.format(dtf).equals(ufg[1])) {
			list[6] = Long.valueOf(ufg[2]);
		}
		memberFromChannel7Day.put(name, list);
	}
	map.put("member_from_channel_7_day", memberFromChannel7Day);
	map.put("member_from_channel", this.userInfoRepository.memberFromChannel());
	map.put("member_from_source", this.userInfoRepository.memberFromSource());
	return CommonResult.successResult(map);
}

@ApiOperation("首页")
@GetMapping("v1/home/daliy_income")
public CommonResult daliyIncomeC(String month) {
	return CommonResult.successResult(this.daliyIncome(month));
}

private Map<String, Map<Integer, Long>> daliyIncome(String month) {
	Map<String, Map<Integer, Long>> list = Maps.newHashMap();
	LocalDate now;
	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
	if (month == null) {
		now = LocalDate.now();
	} else {
		now = LocalDate.parse(month + "01", dtf).plusMonths(1).minusDays(1);
	}
	Map<Integer, Long> map1 = Maps.newHashMap();
	Map<Integer, Long> map2 = Maps.newHashMap();
	while (true) {
		LocalDate time14Before = now.minusDays(14);
		LocalDateTime start14Before = LocalDateTime.of(time14Before.getYear(), time14Before.getMonth(), time14Before.getDayOfMonth(), 0, 0, 0);
		LocalDateTime end14Before = LocalDateTime.of(time14Before.getYear(), time14Before.getMonth(), time14Before.getDayOfMonth(), 23, 59, 59);
		Long expected = 7 * this.userMemberCycleRepository.countByTypeInAndFStartBetween(new UserMemberCycleType[] { UserMemberCycleType.subscribe, UserMemberCycleType.renew }, start14Before, end14Before);
		Long actual = this.pGDataRepository.today(now.minusDays(7).format(dtf), now.minusDays(6).format(dtf), now.format(dtf));

		map1.put(now.getDayOfMonth(), expected);
		map2.put(now.getDayOfMonth(), actual);
		if (now.getDayOfMonth() == 1) {
			break;
		}
		now = now.minusDays(1);
	}
	list.put("expected", map1);
	list.put("actual", map2);
	return list;
}

@ApiOperation("首页")
@GetMapping("v1/home/month_income")
public CommonResult monthIncomeC(String year) {
	return CommonResult.successResult(this.monthIncome(year));
}

private Map<String, Map<Integer, Long>> monthIncome(String year) {
	Map<String, Map<Integer, Long>> list = Maps.newHashMap();
	LocalDate monthEnd;
	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
	if (year == null) {
		monthEnd = LocalDate.now();
	} else {
		monthEnd = LocalDate.parse(year + "1231", dtf);
	}
	Map<Integer, Long> map1 = Maps.newHashMap();
	Map<Integer, Long> map2 = Maps.newHashMap();
	while (true) {
		LocalDate monthStart = LocalDate.of(monthEnd.getYear(), monthEnd.getMonth(), 1);
		LocalDate monthStart14Before = monthStart.minusDays(14);
		LocalDateTime start14Before = LocalDateTime.of(monthStart14Before.getYear(), monthStart14Before.getMonth(), monthStart14Before.getDayOfMonth(), 0, 0, 0);
		LocalDate monthEnd14Before = monthEnd.minusDays(14);
		LocalDateTime end14Before = LocalDateTime.of(monthEnd14Before.getYear(), monthEnd14Before.getMonth(), monthEnd14Before.getDayOfMonth(), 23, 59, 59);
		Long expected = 7 * this.userMemberCycleRepository.countByTypeInAndFStartBetween(new UserMemberCycleType[] { UserMemberCycleType.subscribe, UserMemberCycleType.renew }, start14Before, end14Before);
		Long actual = this.pGDataRepository.month(monthStart.minusDays(7).format(dtf), monthEnd.minusDays(7).format(dtf), monthStart.minusDays(6).format(dtf), monthEnd.format(dtf));

		map1.put(monthEnd.getMonthValue(), expected);
		map2.put(monthEnd.getMonthValue(), actual);
		if (monthEnd.getMonth() == Month.JANUARY) {
			break;
		}
		monthEnd = monthEnd.minusMonths(1);
	}
	list.put("expected", map1);
	list.put("actual", map2);
	return list;
}
}
