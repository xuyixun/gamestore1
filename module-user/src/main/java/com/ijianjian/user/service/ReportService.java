package com.ijianjian.user.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.ijianjian.core.common.object.CommonResult;
import com.ijianjian.user.domain.repository.ReportRepository;
import com.ijianjian.user.util.FieldConstant.ReportGroupType;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api(tags = "报表")
@RestController
public class ReportService {
private ReportRepository r;

public ReportService(ReportRepository r) {
	this.r = r;
}

@ApiOperation("运营")
@ApiImplicitParams({ @ApiImplicitParam(name = "day", value = "日期", paramType = "query", dataType = "String"), @ApiImplicitParam(name = "user_all", value = "当日/月所有用户", paramType = "query", dataType = "String"),
  @ApiImplicitParam(name = "user_today", value = "当日/月新增用户", paramType = "query", dataType = "String"), @ApiImplicitParam(name = "member_all", value = "当日/月所有会员", paramType = "query", dataType = "String"),
  @ApiImplicitParam(name = "member_today", value = "当日/月新增会员", paramType = "query", dataType = "String"), @ApiImplicitParam(name = "un_subscribe_all", value = "当日/月所有退订会员", paramType = "query", dataType = "String"),
  @ApiImplicitParam(name = "un_subscribe_today", value = "当日/月新增退订会员", paramType = "query", dataType = "String"), @ApiImplicitParam(name = "7_b_all", value = "7日期所有会员", paramType = "query", dataType = "String"),
  @ApiImplicitParam(name = "7_b_retain", value = "7日期会员留存", paramType = "query", dataType = "String"), @ApiImplicitParam(name = "14_b_all", value = "14日期所有会员", paramType = "query", dataType = "String"),
  @ApiImplicitParam(name = "14_b_retain", value = "14日期会员留存", paramType = "query", dataType = "String"), @ApiImplicitParam(name = "expected_today", value = "当日/月预计应收", paramType = "query", dataType = "String"),
  @ApiImplicitParam(name = "actual_today", value = "当日/月真实收入", paramType = "query", dataType = "String"), @ApiImplicitParam(name = "app_download", value = "当日/月商城下载", paramType = "query", dataType = "String"),
  @ApiImplicitParam(name = "app_first_open", value = "当日/月商城首次打开", paramType = "query", dataType = "String"), @ApiImplicitParam(name = "app_open", value = "当日/月商城打开", paramType = "query", dataType = "String"),
  @ApiImplicitParam(name = "app_download_user", value = "当日/月商城下载用户数", paramType = "query", dataType = "String"), @ApiImplicitParam(name = "app_open_user", value = "当日/月商城打开用户数", paramType = "query", dataType = "String"),
  @ApiImplicitParam(name = "un_subscribe_rate", value = "当日/月退订率", paramType = "query", dataType = "String"), @ApiImplicitParam(name = "7_b_rate", value = "7日留存率", paramType = "query", dataType = "String"),
  @ApiImplicitParam(name = "14_b_rate", value = "14日留存率", paramType = "query", dataType = "String"), @ApiImplicitParam(name = "income_rate", value = "营收率", paramType = "query", dataType = "String"), })
@GetMapping("v1/operation/{type}")
public CommonResult operation(@PathVariable ReportGroupType type, String start, String end, Integer page, Integer size) {
	if (page == null) {
		page = 0;
	}
	if (size == null) {
		size = 10;
	}
	LocalDate now = LocalDate.now();
	DateTimeFormatter dayF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	Page<Map<String, String>> list = null;
	if (Strings.isNullOrEmpty(end)) {
		end = now.with(TemporalAdjusters.lastDayOfMonth()).format(dayF);
	}
	switch (type) {
	case day:
		if (Strings.isNullOrEmpty(start)) {
			start = now.with(TemporalAdjusters.firstDayOfMonth()).format(dayF);
		}
		list = this.r.operationDay(start, end, PageRequest.of(page, size));
		break;
	case month:
		if (Strings.isNullOrEmpty(start)) {
			start = now.with(TemporalAdjusters.firstDayOfYear()).format(dayF);
		}
		list = this.r.operationMonth(start.substring(0, 7), end.substring(0, 7), PageRequest.of(page, size));
		break;
	}
	return CommonResult.successResult(list);
}

@ApiOperation("渠道")
@ApiImplicitParams({ @ApiImplicitParam(name = "day", value = "日期", paramType = "query", dataType = "String"),
	@ApiImplicitParam(name = "f_name", value = "渠道名称", paramType = "query", dataType = "String"),
  @ApiImplicitParam(name = "gameName", value = "游戏名称", paramType = "query", dataType = "String"),
  @ApiImplicitParam(name = "type", value = "渠道类型", paramType = "query", dataType = "String"),
  @ApiImplicitParam(name = "click", value = "landingpage点击量", paramType = "query", dataType = "String"),
  @ApiImplicitParam(name = "download_app", value = "landingpage商城app下载量", paramType = "query", dataType = "String"),
  @ApiImplicitParam(name = "download_game", value = "landingpage游戏下载量", paramType = "query", dataType = "String"),
  @ApiImplicitParam(name = "user_all", value = "当日/月所有用户", paramType = "query", dataType = "String"),
  @ApiImplicitParam(name = "user_today", value = "当日/月新增用户", paramType = "query", dataType = "String"),
  @ApiImplicitParam(name = "member_all", value = "当日/月所有会员", paramType = "query", dataType = "String"),
  @ApiImplicitParam(name = "member_today", value = "当日/月新增会员", paramType = "query", dataType = "String"),
  @ApiImplicitParam(name = "un_subscribe_all", value = "当日/月所有退订会员", paramType = "query", dataType = "String"),
  @ApiImplicitParam(name = "un_subscribe_today", value = "当日/月新增退订会员", paramType = "query", dataType = "String"),
  @ApiImplicitParam(name = "7_b_all", value = "7日期所有会员", paramType = "query", dataType = "String"),
  @ApiImplicitParam(name = "7_b_retain", value = "7日期会员留存", paramType = "query", dataType = "String"),
  @ApiImplicitParam(name = "14_b_all", value = "14日期所有会员", paramType = "query", dataType = "String"),
  @ApiImplicitParam(name = "14_b_retain", value = "14日期会员留存", paramType = "query", dataType = "String"),
  @ApiImplicitParam(name = "expected_today", value = "当日/月预计应收", paramType = "query", dataType = "String"),
  @ApiImplicitParam(name = "actual_today", value = "当日/月真实收入", paramType = "query", dataType = "String"),
  @ApiImplicitParam(name = "un_subscribe_rate", value = "当日/月退订率", paramType = "query", dataType = "String"),
  @ApiImplicitParam(name = "7_b_rate", value = "7日留存率", paramType = "query", dataType = "String"),
  @ApiImplicitParam(name = "14_b_rate", value = "14日留存率", paramType = "query", dataType = "String"),
  @ApiImplicitParam(name = "income_rate", value = "营收率", paramType = "query", dataType = "String"), })
@GetMapping("v1/channel/{type}/{channelUuid}")
public CommonResult channel(@PathVariable ReportGroupType type, @PathVariable String channelUuid, String start, String end, Integer page, Integer size) {
	if (page == null) {
		page = 0;
	}
	if (size == null) {
		size = 10;
	}
	LocalDate now = LocalDate.now();
	DateTimeFormatter dayF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	Page<Map<String, String>> list = null;
	if (Strings.isNullOrEmpty(end)) {
		end = now.with(TemporalAdjusters.lastDayOfMonth()).format(dayF);
	}
	switch (type) {
	case day:
		if (Strings.isNullOrEmpty(start)) {
			start = now.with(TemporalAdjusters.firstDayOfMonth()).format(dayF);
		}
		list = this.r.channelDay(start, end, channelUuid, PageRequest.of(page, size));
		break;
	case month:
		if (Strings.isNullOrEmpty(start)) {
			start = now.with(TemporalAdjusters.firstDayOfYear()).format(dayF);
		}
		list = this.r.channelMonth(start.substring(0, 7), end.substring(0, 7), channelUuid, PageRequest.of(page, size));
		break;
	}
	return CommonResult.successResult(list);
}

@ApiOperation("游戏")
@ApiImplicitParams({ @ApiImplicitParam(name = "day", value = "日期", paramType = "query", dataType = "String"),
	@ApiImplicitParam(name = "f_name", value = "游戏名", paramType = "query", dataType = "String"),
  @ApiImplicitParam(name = "detail_count", value = "详情点击量", paramType = "query", dataType = "String"),
  @ApiImplicitParam(name = "download_count", value = "下载量", paramType = "query", dataType = "String"),
  @ApiImplicitParam(name = "home_download_count", value = "首页下载量", paramType = "query", dataType = "String"),
  @ApiImplicitParam(name = "column_app_download_count", value = "应用栏目下载量", paramType = "query", dataType = "String"),
  @ApiImplicitParam(name = "column_general_l1_download_count", value = "一级栏目下载量", paramType = "query", dataType = "String"),
  @ApiImplicitParam(name = "column_general_l2_download_count", value = "二级栏目下载量", paramType = "query", dataType = "String"),
  @ApiImplicitParam(name = "detail_download_count", value = "详情页下载量", paramType = "query", dataType = "String"), })
@GetMapping("v1/game/{type}")
public CommonResult game(@PathVariable ReportGroupType type, String start, String end, String gameInfoUuid, Integer page, Integer size) {
	if (page == null) {
		page = 0;
	}
	if (size == null) {
		size = 10;
	}
	LocalDate now = LocalDate.now();
	DateTimeFormatter dayF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	Page<Map<String, String>> list = null;
	if (Strings.isNullOrEmpty(end)) {
		end = now.with(TemporalAdjusters.lastDayOfMonth()).format(dayF);
	}
	switch (type) {
	case day:
		if (Strings.isNullOrEmpty(start)) {
			start = now.with(TemporalAdjusters.firstDayOfMonth()).format(dayF);
		}
		list = this.r.gameDay(start, end, gameInfoUuid, PageRequest.of(page, size));
		break;
	case month:
		if (Strings.isNullOrEmpty(start)) {
			start = now.with(TemporalAdjusters.firstDayOfYear()).format(dayF);
		}
		list = this.r.gameMonth(start.substring(0, 7), end.substring(0, 7), gameInfoUuid, PageRequest.of(page, size));
		break;
	}
	return CommonResult.successResult(list);
}
}
