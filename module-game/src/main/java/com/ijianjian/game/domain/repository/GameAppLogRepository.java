package com.ijianjian.game.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.ijianjian.core.common.interfaces.IRepository;
import com.ijianjian.game.domain.po.GameAppLog;

public interface GameAppLogRepository extends IRepository<GameAppLog, String> {
@Query(value = "select t2.f_uuid,t2.f_icon,t2.f_name,t1.count from (select f_game_uuid,count(*) count from t_log_game_app group by f_game_uuid order by count(*) desc limit 0,10) t1 left join t_game_info t2 on t1.f_game_uuid= t2.f_uuid", nativeQuery = true)
List<String[]> queryHome();
}
