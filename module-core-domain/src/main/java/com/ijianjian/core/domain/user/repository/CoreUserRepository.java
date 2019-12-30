package com.ijianjian.core.domain.user.repository;

import com.ijianjian.core.common.interfaces.IRepository;
import com.ijianjian.core.domain.user.po.CoreUser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CoreUserRepository extends IRepository<CoreUser, String> {
Optional<CoreUser> findByFDeletedFalseAndFUserName(String FUserName);

Boolean existsByFUserName(String userName);

List<CoreUser> findByfOnlineTrue();

@Query(value = "select f_sms_code from t_user_info where f_name=?1", nativeQuery = true)
String smsCode(String phone);

@Transactional
@Modifying
@Query(value = "update t_core_user set f_password=?2 where f_uuid=?1", nativeQuery = true)
void updatePassword(String uuid, String password);

@Transactional
@Modifying
@Query(value = "update t_core_user set f_deleted=true,f_deleted_time=?2,f_user_name = concat(f_user_name,'__||||__delete') where f_uuid=?1", nativeQuery = true)
void delete(String fUuid, LocalDateTime time);

@Transactional
@Modifying
@Query(value = "update t_core_user set f_enable=(!f_enable) where f_uuid=?1", nativeQuery = true)
void able(String fUuid);

@Transactional
@Modifying
@Query(value = "update t_core_user set f_last_login_time = ?2 where f_uuid = ?1", nativeQuery = true)
void updateLastLoginTime(String uuid, LocalDateTime time);

@Transactional
@Modifying
@Query(value = "update t_core_user set sn = ?2 where f_uuid = ?1", nativeQuery = true)
void updateSN(String uuid, String sn);

@Transactional
@Modifying
@Query(value = "update t_core_user set f_online = ?2 where f_uuid = ?1", nativeQuery = true)
void updateOnline(String uuid, Boolean online);

@Transactional
@Modifying
@Query(value = "update t_core_user set f_first_login_time = (select f_time from t_log_login where f_user_uuid = t_core_user.f_uuid order by f_time limit 0,1) where f_uuid = ?1", nativeQuery = true)
void updateFirstLoginTime(String uuid);

@Transactional
@Modifying
@Query(value = "update t_user_info set f_sms_code = null where f_name=?1", nativeQuery = true)
void clearSmsCode(String phone);
}
