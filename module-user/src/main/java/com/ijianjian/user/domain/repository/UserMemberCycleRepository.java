package com.ijianjian.user.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.ijianjian.core.common.interfaces.IRepository;
import com.ijianjian.user.domain.po.UserMemberCycle;
import com.ijianjian.user.util.FieldConstant.UserMemberCycleType;

public interface UserMemberCycleRepository extends IRepository<UserMemberCycle, String> {
Optional<UserMemberCycle> findByFEndIsNullAndUserInfo_id(String userUuid);

@Transactional
@Modifying
@Query(value = "update t_user_member_cycle set f_end=?2,type=?3 where f_uuid=?1", nativeQuery = true)
void update(String uuid, LocalDateTime time, String type);

@Transactional
@Modifying
@Query(value = "update t_user_member_cycle set f_end=?2 where f_uuid=?1", nativeQuery = true)
void update(String uuid, LocalDateTime time);

List<UserMemberCycle> findByFEndIsNullAndFStartBetween(LocalDateTime start, LocalDateTime end);

List<UserMemberCycle> findByFEndIsNullAndTypeInAndFStartLessThanEqual(UserMemberCycleType[] type, LocalDateTime time);

Long countByTypeInAndFStartLessThanEqual(UserMemberCycleType[] type, LocalDateTime time);

Long countByTypeInAndFStartBetween(UserMemberCycleType[] type, LocalDateTime start, LocalDateTime end);
}
