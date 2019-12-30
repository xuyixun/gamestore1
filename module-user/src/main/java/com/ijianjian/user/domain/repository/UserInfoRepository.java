package com.ijianjian.user.domain.repository;

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
import com.ijianjian.user.domain.dto.UserInfoSearchDTO;
import com.ijianjian.user.domain.po.UserInfo;
import com.ijianjian.user.util.FieldConstant.UserType;

public interface UserInfoRepository extends IRepository<UserInfo, String> {
default Page<UserInfo> query(UserInfoSearchDTO userInfoSearchDTO) {
	Pageable pageable = PageRequest.of(userInfoSearchDTO.getPage(), userInfoSearchDTO.getSize(), Sort.by(Order.desc("fCreateTime")));
	return this.findAll((root, query, cb) -> {
		Predicate predicate = cb.conjunction();
		List<Expression<Boolean>> expressions = predicate.getExpressions();
		if (!Strings.isNullOrEmpty(userInfoSearchDTO.getName())) {
			expressions.add(cb.like(root.get("fName"), "%" + userInfoSearchDTO.getName() + "%"));
		}
		expressions.add(cb.equal(root.get("type"), UserType.admin));
		expressions.add(cb.isFalse(root.get("core").get("fDeleted")));
		return predicate;
	}, pageable);
}

Optional<UserInfo> findByCore_fUserName(String FUserName);

@Transactional
@Modifying
@Query(value = "update t_user_info set f_sms_code=?2 where core_f_uuid=?1", nativeQuery = true)
void updateSmsCode(String uuid, String verificationCode);

@Transactional
@Modifying
@Query(value = "update t_user_info set type=?2,f_subscribe_time=?3,source=?4 where core_f_uuid=?1", nativeQuery = true)
void subscribe(String uuid, String type, LocalDateTime time, String source);

@Transactional
@Modifying
@Query(value = "update t_user_info set type=?2,f_subscribe_time=?3,source=?4,channel_number = ?4 where core_f_uuid=?1", nativeQuery = true)
void subscribe(String uuid, String type, LocalDateTime time, String source, String channelNumber);

@Transactional
@Modifying
@Query(value = "update t_user_info set type=?2,f_un_subscribe_time=?3 where core_f_uuid=?1", nativeQuery = true)
void unSubscribe(String uuid, String type, LocalDateTime time);

List<UserInfo> findByCore_fFirstLoginTimeIsNullAndCore_fLastLoginTimeIsNotNullAndType(UserType type);

List<UserInfo> findByCore_fFirstLoginTimeIsNullAndCore_fLastLoginTimeIsNotNull();

Long countByCore_fDeletedFalseAndTypeIn(UserType[] type);

Long countByCore_fDeletedFalseAndTypeInAndFCreateTimeBetween(UserType[] type, LocalDateTime start, LocalDateTime end);

Long countByCore_fDeletedFalseAndTypeAndFUnSubscribeTimeIsNotNull(UserType type);

Long countByCore_fDeletedFalseAndTypeAndFUnSubscribeTimeIsNotNullAndFUnSubscribeTimeBetween(UserType type, LocalDateTime start, LocalDateTime end);

@Query(value = "select t2.f_name,date_format(t1.f_time,'%Y%m%d') day,count(*) count,t2.f_uuid from t_log_subscribe t1 left join t_game_channel t2 on t1.channel_number = t2.f_number where t1.type = 'subscribe' and t1.channel_number is not null and t1.f_time >= ?1 group by t1.channel_number,date_format(t1.f_time,'%Y%m%d') order by t2.f_name, date_format(t1.f_time,'%Y%m%d')", nativeQuery = true)
List<String[]> memberFromChannel7Day(String date);

@Query(value = "select count(*) count,t2.f_name,t2.f_uuid from t_log_subscribe t1 left join t_game_channel t2 on t1.channel_number = t2.f_number where t1.type = 'subscribe' and t1.channel_number is not null group by t1.channel_number order by count(*) desc", nativeQuery = true)
List<String[]> memberFromChannel();

@Query(value = "select count(*) count,source from t_user_info where type = 'user_member' and source is not null group by source order by count(*) desc", nativeQuery = true)
List<String[]> memberFromSource();

@Query(value = "select max(charge_type) charge_type from t_game_info where f_apk_name=?1", nativeQuery = true)
String chargeType(String apkName);
}
