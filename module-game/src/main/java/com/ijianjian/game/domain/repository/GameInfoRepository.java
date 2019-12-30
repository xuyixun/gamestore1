package com.ijianjian.game.domain.repository;

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
import com.ijianjian.game.domain.dto.GameInfoSearchDTO;
import com.ijianjian.game.domain.dto.GameInfoSearchDTO_002;
import com.ijianjian.game.domain.po.GameInfo;
import com.ijianjian.game.util.FieldConstant.GameInfoStatus;

public interface GameInfoRepository extends IRepository<GameInfo, String> {
default Page<GameInfo> query(GameInfoSearchDTO dto) {
	Pageable pageable = PageRequest.of(dto.getPage(), dto.getSize(), Sort.by(Order.desc("fUpdateTime")));
	return this.findAll((root, query, cb) -> {
		Predicate predicate = cb.conjunction();
		List<Expression<Boolean>> expressions = predicate.getExpressions();
		if (!Strings.isNullOrEmpty(dto.getName())) {
			expressions.add(cb.like(cb.upper(root.get("fName")), "%" + dto.getName().toUpperCase() + "%"));
		}
		if (dto.getStatus() != null) {
			expressions.add(cb.equal(root.get("status"), dto.getStatus()));
		}
		expressions.add(cb.isFalse(root.get("fDeleted")));
		return predicate;
	}, pageable);
}

default Page<GameInfo> queryB(GameInfoSearchDTO_002 dto) {
	Pageable pageable = PageRequest.of(dto.getPage(), dto.getSize(), Sort.by(Order.asc("fName")));
	return this.findAll((root, query, cb) -> {
		Predicate predicate = cb.conjunction();
		List<Expression<Boolean>> expressions = predicate.getExpressions();
		if (!Strings.isNullOrEmpty(dto.getName())) {
			expressions.add(cb.like(cb.upper(root.get("fName")), "%" + dto.getName().toUpperCase() + "%"));
		}
		expressions.add(cb.equal(root.get("status"), GameInfoStatus.shang_xian));
		expressions.add(cb.isFalse(root.get("fDeleted")));
		return predicate;
	}, pageable);
}

@Transactional
@Modifying
@Query(value = "update t_game_info set f_deleted=true,f_deleted_time=?2 where f_uuid=?1", nativeQuery = true)
void delete(String fUuid, LocalDateTime time);

@Transactional
@Modifying
@Query(value = "update t_game_info set status=?2,f_on_line_time=?3 where f_uuid=?1", nativeQuery = true)
void onLine(String fUuid, String status, LocalDateTime time);

@Transactional
@Modifying
@Query(value = "update t_game_info set status=?2,f_on_line_time=?3 where f_uuid=?1", nativeQuery = true)
void offLine(String fUuid, String status, LocalDateTime time);

@Transactional
@Modifying
@Query(value = "update t_game_info set status=?2 where f_uuid=?1", nativeQuery = true)
void status(String fUuid, String status);

@Transactional
@Modifying
@Query(value = "update t_game_info set f_need_shell=false where f_uuid=?1", nativeQuery = true)
void needShell(String fUuid);

@Transactional
@Modifying
@Query(value = "update t_game_info set f_apk_name=?2, f_version=?3 where f_uuid=?1", nativeQuery = true)
void apkName(String fUuid, String apkName, String versionCode);

@Transactional
@Modifying
@Query(value = "update t_game_info set f_download_count=(f_download_count+1) where f_uuid=?1", nativeQuery = true)
void downloadCount(String fUuid);

@Query(value = "select type from t_user_info where core_f_uuid=?1", nativeQuery = true)
String userType(String userUuid);

List<GameInfo> findTop10ByFDeletedFalseAndStatus(GameInfoStatus status, Sort sort);

List<GameInfo> findFirstByFDeletedFalseAndFNeedShellTrue();

Optional<GameInfo> findTopByFDeletedFalseAndFApkName(String apkName);
}
