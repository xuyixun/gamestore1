package com.ijianjian.game.domain.repository;

import java.time.LocalDateTime;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.ijianjian.core.common.interfaces.IRepository;
import com.ijianjian.game.domain.po.Language;

public interface LanguageRepository extends IRepository<Language, String> {
List<Language> findByFDeletedFalse();

Boolean existsByFNumber(String number);

@Transactional
@Modifying
@Query(value = "update t_game_language set f_deleted=true,f_deleted_time=?2 where f_uuid=?1", nativeQuery = true)
void delete(String fUuid, LocalDateTime time);
}
