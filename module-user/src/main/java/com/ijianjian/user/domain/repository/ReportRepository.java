package com.ijianjian.user.domain.repository;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import com.ijianjian.core.common.interfaces.IRepository;
import com.ijianjian.user.domain.po.UserInfo;

public interface ReportRepository extends IRepository<UserInfo, String> {
@Query(value = "select * " + 
		",if(member_all=0,1,un_subscribe_all/member_all) un_subscribe_rate " + 
		",if(7_b_all=0,1,7_b_retain/7_b_all) 7_b_rate " + 
		",if(14_b_all=0,1,14_b_retain/14_b_all) 14_b_rate " + 
		",if(expected_today=0,1,actual_today/expected_today) income_rate  " + 
		"from  " + 
		" (select datev.i day " + 
		"  ,(select count(*) from t_user_info where `type`in('user_member','user_normal') and date_format(f_create_time,'%Y-%m-%d') <= datev.i) user_all  " + 
		"  ,(select count(*) from t_user_info where `type`in('user_member','user_normal') and date_format(f_create_time,'%Y-%m-%d') = datev.i) user_today " + 
		"  ,(select count(*) from t_user_info where `type`='user_member' and date_format(f_subscribe_time,'%Y-%m-%d') <= datev.i) member_all  " + 
		"  ,(select count(*) from t_user_info where `type`='user_member' and date_format(f_subscribe_time,'%Y-%m-%d') = datev.i) member_today " + 
		"  ,(select count(*) from t_user_info where `type`='user_normal' and date_format(f_un_subscribe_time,'%Y-%m-%d') <= datev.i) un_subscribe_all  " + 
		"  ,(select count(*) from t_user_info where `type`='user_normal' and date_format(f_un_subscribe_time,'%Y-%m-%d') = datev.i) un_subscribe_today " + 
		"  ,(select count(*) from t_user_member_cycle where f_cycle_count = 1 and date_format(f_start,'%Y-%m-%d') = date_sub(datev.i,interval 7 day)) 7_b_all " + 
		"  ,(select count(*) from t_user_member_cycle where `type` = 'subscribe' and date_format(f_start,'%Y-%m-%d') = date_sub(datev.i,interval 7 day)) 7_b_retain " + 
		"  ,(select count(*) from t_user_member_cycle where f_cycle_count = 1 and date_format(f_start,'%Y-%m-%d') = date_sub(datev.i,interval 14 day)) 14_b_all " + 
		"  ,(select count(*) from t_user_member_cycle where f_cycle_count = 2 and `type` = 'renew' and f_number in (select f_number from t_user_member_cycle where `type` = 'subscribe' and date_format(f_start,'%Y-%m-%d') = date_sub(datev.i,interval 14 day))) 14_b_retain " + 
		"  ,(select count(*)*7 from t_user_member_cycle where `type` in ('subscribe','renew') and date_format(f_start,'%Y-%m-%d') = date_sub(datev.i,interval 14 day)) expected_today " + 
		"  ,(select ifnull(sum(deduct_case),0) from t_pg_data t1 left join t_pg_bill t2 on t1.pgbill_f_uuid = t2.f_uuid where date_format(t1.billcycledate,'%Y-%m-%d') = date_sub(datev.i,interval 7 day) and date_format(t2.f_date,'%Y-%m-%d') between date_sub(datev.i,interval 6 day) and datev.i) actual_today " +
		"  ,(select count(*) from t_log_app where `type` = 'download' and date_format(f_time,'%Y-%m-%d') = datev.i ) app_download " + 
		"  ,(select count(*) from t_log_app where `type` = 'first_open' and date_format(f_time,'%Y-%m-%d') = datev.i ) app_first_open " + 
		"  ,(select count(*) from t_log_app where `type` in ( 'first_open','open') and date_format(f_time,'%Y-%m-%d') = datev.i ) app_open " + 
		"  ,(select count(*) from (select 1,date_format(f_time,'%Y-%m-%d') date from t_log_app where `type` = 'download' group by f_phone) x where x.date= datev.i) app_download_user " + 
		"  ,(select count(*) from (select 1,date_format(f_time,'%Y-%m-%d') date from t_log_app where `type` in ( 'first_open','open') group by f_phone) x where x.date= datev.i) app_open_user" +
		
		"  from " + 
		"   (select date_add('1970-01-01',INTERVAL t4.i*10000 + t3.i*1000 + t2.i*100 + t1.i*10 + t0.i day) i " + 
		"    from   " + 
		"     (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t0, " + 
		"     (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t1, " + 
		"     (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t2, " + 
		"     (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t3, " + 
		"     (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t4 " + 
		"   ) datev  " + 
		"  where datev.i between ?1 and ?2 " + 
		" ) x",
		countQuery="select count(*) from " + 
				" (select date_add('1970-01-01',INTERVAL t4.i*10000 + t3.i*1000 + t2.i*100 + t1.i*10 + t0.i day) i " + 
				"  from   " + 
				"   (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t0, " + 
				"   (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t1, " + 
				"   (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t2, " + 
				"   (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t3, " + 
				"   (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t4 " + 
				" ) datev  " + 
				" where datev.i between ?1 and ?2", nativeQuery = true)
Page<Map<String, String>> operationDay(String start, String end,Pageable page);


@Query(value = "select * " + 
		",if(member_all=0,1,un_subscribe_all/member_all) un_subscribe_rate " + 
		",if(7_b_all=0,1,7_b_retain/7_b_all) 7_b_rate " + 
		",if(14_b_all=0,1,14_b_retain/14_b_all) 14_b_rate " + 
		",if(expected_today=0,1,actual_today/expected_today) income_rate " + 
		"from  " + 
		" (select datev.i day " + 
		"  ,(select count(*) from t_user_info where `type`in('user_member','user_normal') and date_format(f_create_time,'%Y-%m') <= datev.i) user_all " + 
		"  ,(select count(*) from t_user_info where `type`in('user_member','user_normal') and date_format(f_create_time,'%Y-%m') = datev.i) user_today " + 
		"  ,(select count(*) from t_user_info where `type`='user_member' and date_format(f_subscribe_time,'%Y-%m') <= datev.i) member_all " + 
		"  ,(select count(*) from t_user_info where `type`='user_member' and date_format(f_subscribe_time,'%Y-%m') = datev.i) member_today " + 
		"  ,(select count(*) from t_user_info where `type`='user_normal' and date_format(f_un_subscribe_time,'%Y-%m') <= datev.i) un_subscribe_all " + 
		"  ,(select count(*) from t_user_info where `type`='user_normal' and date_format(f_un_subscribe_time,'%Y-%m') = datev.i) un_subscribe_today " + 
		"  ,(select count(*) from t_user_member_cycle where f_cycle_count = 1 and date_format(f_start,'%Y-%m-%d') between date_sub(datev.i_s,interval 7 day) and date_sub(datev.i_e,interval 7 day)) 7_b_all " + 
		"  ,(select count(*) from t_user_member_cycle where `type` = 'subscribe' and date_format(f_start,'%Y-%m-%d') between date_sub(datev.i_s,interval 7 day) and date_sub(datev.i_e,interval 7 day)) 7_b_retain " + 
		"  ,(select count(*) from t_user_member_cycle where f_cycle_count = 1 and date_format(f_start,'%Y-%m-%d') between date_sub(datev.i_s,interval 14 day) and date_sub(datev.i_e,interval 14 day)) 14_b_all " + 
		"  ,(select count(*) from t_user_member_cycle where f_cycle_count = 2 and `type` = 'renew' and f_number in (select f_number from t_user_member_cycle where `type` = 'subscribe' and date_format(f_start,'%Y-%m-%d') between date_sub(datev.i_s,interval 14 day) and date_sub(datev.i_e,interval 14 day))) 14_b_retain " + 
		"  ,(select count(*)*7 from t_user_member_cycle where `type` in ('subscribe','renew') and date_format(f_start,'%Y-%m-%d') between date_sub(datev.i_s,interval 14 day) and date_sub(datev.i_e,interval 14 day)) expected_today " + 
		"  ,(select ifnull(sum(deduct_case),0) from t_pg_data t1 left join t_pg_bill t2 on t1.pgbill_f_uuid = t2.f_uuid where date_format(t1.billcycledate,'%Y-%m-%d') between date_sub(datev.i_s,interval 7 day) and date_sub(datev.i_e,interval 7 day) and date_format(t2.f_date,'%Y-%m-%d') between date_sub(datev.i_s,interval 6 day) and datev.i_e) actual_today "+ 
		"  ,(select count(*) from t_log_app where `type` = 'download' and date_format(f_time,'%Y-%m') = datev.i ) app_download "+ 
		"  ,(select count(*) from t_log_app where `type` = 'first_open' and date_format(f_time,'%Y-%m') = datev.i ) app_first_open " + 
		"  ,(select count(*) from t_log_app where `type` in ( 'first_open','open') and date_format(f_time,'%Y-%m') = datev.i ) app_open " + 
		"  ,(select count(*) from (select 1,date_format(f_time,'%Y-%m') date from t_log_app where `type` = 'download' group by f_phone) x where x.date= datev.i) app_download_user " + 
		"  ,(select count(*) from (select 1,date_format(f_time,'%Y-%m') date from t_log_app where `type` in ( 'first_open','open') group by f_phone) x where x.date= datev.i) app_open_user" + 
		"  from " + 
		"   (select i,concat(i,'-01') i_s, date_sub(date_add(concat(i,'-01'),interval 1 month), interval 1 day) i_e  " + 
		"    from " + 
		"     (select date_format(date_add('1970-01-01',INTERVAL t2.i*100 + t1.i*10 + t0.i month),'%Y-%m') i  " + 
		"      from " + 
		"       (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t0, " + 
		"       (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t1, " + 
		"       (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t2 " + 
		"     ) x " + 
		"    where x.i between ?1 and ?2 " + 
		"   ) datev " + 
		" ) x",
		countQuery="select count(*) " + 
				"from " + 
				" (select date_format(date_add('1970-01-01',INTERVAL t2.i*100 + t1.i*10 + t0.i month),'%Y-%m') i  " + 
				"  from " + 
				"   (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t0, " + 
				"   (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t1, " + 
				"   (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t2 " + 
				" ) x " + 
				"where x.i between ?1 and ?2",
		nativeQuery = true)
Page<Map<String, String>> operationMonth(String start, String end,Pageable page);

@Query(value="select * " + 
		",if(member_all=0,1,un_subscribe_all/member_all) un_subscribe_rate " + 
		",if(7_b_all=0,1,7_b_retain/7_b_all) 7_b_rate " + 
		",if(14_b_all=0,1,14_b_retain/14_b_all) 14_b_rate " + 
		",if(expected_today=0,1,actual_today/expected_today) income_rate  " + 
		"from  " + 
		" (select datev.i day " + 
		"  ,channel.f_name " + 
		"  ,channel.gameName " + 
		"  ,channel.`type` " + 
		"  ,(select count(*) from t_log_lp where channel_number = channel.f_number and date_format(f_time,'%Y-%m-%d') = datev.i) click " + 
		"  ,(select count(*) from t_log_game where `type` = 'landing_page' and f_from_uuid = channel.f_number and date_format(f_time,'%Y-%m-%d') = datev.i) download_app " + 
		"  ,(select count(*) from t_log_app where `type` = 'download' and channel_number = channel.f_number and date_format(f_time,'%Y-%m-%d') = datev.i) download_game " + 
		"  ,(select count(*) from t_user_info where `type`in('user_member','user_normal') and channel_number = channel.f_number and date_format(f_create_time,'%Y-%m-%d') <= datev.i) user_all  " + 
		"  ,(select count(*) from t_user_info where `type`in('user_member','user_normal') and channel_number = channel.f_number and date_format(f_create_time,'%Y-%m-%d') = datev.i) user_today " + 
		"  ,(select count(*) from t_user_info where `type`='user_member' and channel_number = channel.f_number and date_format(f_subscribe_time,'%Y-%m-%d') <= datev.i) member_all  " + 
		"  ,(select count(*) from t_user_info where `type`='user_member' and channel_number = channel.f_number and date_format(f_subscribe_time,'%Y-%m-%d') = datev.i) member_today " + 
		"  ,(select count(*) from t_user_info where `type`='user_normal' and channel_number = channel.f_number and date_format(f_un_subscribe_time,'%Y-%m-%d') <= datev.i) un_subscribe_all  " + 
		"  ,(select count(*) from t_user_info where `type`='user_normal' and channel_number = channel.f_number and date_format(f_un_subscribe_time,'%Y-%m-%d') = datev.i) un_subscribe_today " + 
		"  ,(select count(*) from t_user_member_cycle where f_cycle_count = 1 and channel_number = channel.f_number and date_format(f_start,'%Y-%m-%d') = date_sub(datev.i,interval 7 day)) 7_b_all " + 
		"  ,(select count(*) from t_user_member_cycle where `type` = 'subscribe' and channel_number = channel.f_number and date_format(f_start,'%Y-%m-%d') = date_sub(datev.i,interval 7 day)) 7_b_retain " + 
		"  ,(select count(*) from t_user_member_cycle where f_cycle_count = 1 and channel_number = channel.f_number and date_format(f_start,'%Y-%m-%d') = date_sub(datev.i,interval 14 day)) 14_b_all " + 
		"  ,(select count(*) from t_user_member_cycle where f_cycle_count = 2 and `type` = 'renew' and f_number in (select f_number from t_user_member_cycle where `type` = 'subscribe' and channel_number = channel.f_number and date_format(f_start,'%Y-%m-%d') = date_sub(datev.i,interval 14 day))) 14_b_retain " + 
		"  ,(select count(*)*7 from t_user_member_cycle where `type` in ('subscribe','renew') and channel_number = channel.f_number and date_format(f_start,'%Y-%m-%d') = date_sub(datev.i,interval 14 day)) expected_today " + 
		"  ,(select ifnull(sum(deduct_case),0) from t_pg_data t1 left join t_pg_bill t2 on t1.pgbill_f_uuid = t2.f_uuid where msisdn in (select f_phone from t_user_info where channel_number = channel.f_number) and date_format(t1.billcycledate,'%Y-%m-%d') = date_sub(datev.i,interval 7 day) and date_format(t2.f_date,'%Y-%m-%d') between date_sub(datev.i,interval 6 day) and datev.i) actual_today " + 
		"  from " + 
		"   (select date_add('1970-01-01',INTERVAL t4.i*10000 + t3.i*1000 + t2.i*100 + t1.i*10 + t0.i day) i " + 
		"    from   " + 
		"     (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t0, " + 
		"     (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t1, " + 
		"     (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t2, " + 
		"     (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t3, " + 
		"     (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t4 " + 
		"   ) datev  " + 
		"   ,(select t1.f_name,t1.f_number,t1.`type`,t2.f_name gameName from t_game_channel t1 left join t_game_info t2 on t1.game_info_f_uuid= t2.f_uuid where t1.f_uuid = ?3) channel " + 
		"  where datev.i between ?1 and ?2 " + 
		" ) x",
		countQuery="select count(*) from " + 
				" (select date_add('1970-01-01',INTERVAL t4.i*10000 + t3.i*1000 + t2.i*100 + t1.i*10 + t0.i day) i " + 
				"  from   " + 
				"   (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t0, " + 
				"   (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t1, " + 
				"   (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t2, " + 
				"   (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t3, " + 
				"   (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t4 " + 
				" ) datev  " + 
				" where datev.i between ?1 and ?2",nativeQuery=true)
Page<Map<String, String>> channelDay(String start, String end,String channelUuid,Pageable page);

@Query(value="select * " + 
		",if(member_all=0,1,un_subscribe_all/member_all) un_subscribe_rate " + 
		",if(7_b_all=0,1,7_b_retain/7_b_all) 7_b_rate " + 
		",if(14_b_all=0,1,14_b_retain/14_b_all) 14_b_rate " + 
		",if(expected_today=0,1,actual_today/expected_today) income_rate " + 
		"from  " + 
		" (select datev.i day " + 
		"  ,channel.f_name " + 
		"  ,channel.gameName " + 
		"  ,channel.`type` " + 
		"  ,(select count(*) from t_log_lp where channel_number = channel.f_number and date_format(f_time,'%Y-%m') = datev.i) click " + 
		"  ,(select count(*) from t_log_game where `type` = 'landing_page' and f_from_uuid = channel.f_number and date_format(f_time,'%Y-%m') = datev.i) download_app " + 
		"  ,(select count(*) from t_log_app where `type` = 'download' and channel_number = channel.f_number and date_format(f_time,'%Y-%m') = datev.i) download_game " + 
		"  ,(select count(*) from t_user_info where `type`in('user_member','user_normal') and channel_number = channel.f_number and date_format(f_create_time,'%Y-%m') <= datev.i) user_all " + 
		"  ,(select count(*) from t_user_info where `type`in('user_member','user_normal') and channel_number = channel.f_number and date_format(f_create_time,'%Y-%m') = datev.i) user_today " + 
		"  ,(select count(*) from t_user_info where `type`='user_member' and channel_number = channel.f_number and date_format(f_subscribe_time,'%Y-%m') <= datev.i) member_all " + 
		"  ,(select count(*) from t_user_info where `type`='user_member' and channel_number = channel.f_number and date_format(f_subscribe_time,'%Y-%m') = datev.i) member_today " + 
		"  ,(select count(*) from t_user_info where `type`='user_normal' and channel_number = channel.f_number and date_format(f_un_subscribe_time,'%Y-%m') <= datev.i) un_subscribe_all " + 
		"  ,(select count(*) from t_user_info where `type`='user_normal' and channel_number = channel.f_number and date_format(f_un_subscribe_time,'%Y-%m') = datev.i) un_subscribe_today " + 
		"  ,(select count(*) from t_user_member_cycle where f_cycle_count = 1 and channel_number = channel.f_number and date_format(f_start,'%Y-%m-%d') between date_sub(datev.i_s,interval 7 day) and date_sub(datev.i_e,interval 7 day)) 7_b_all " + 
		"  ,(select count(*) from t_user_member_cycle where `type` = 'subscribe' and channel_number = channel.f_number and date_format(f_start,'%Y-%m-%d') between date_sub(datev.i_s,interval 7 day) and date_sub(datev.i_e,interval 7 day)) 7_b_retain " + 
		"  ,(select count(*) from t_user_member_cycle where f_cycle_count = 1 and channel_number = channel.f_number and date_format(f_start,'%Y-%m-%d') between date_sub(datev.i_s,interval 14 day) and date_sub(datev.i_e,interval 14 day)) 14_b_all " + 
		"  ,(select count(*) from t_user_member_cycle where f_cycle_count = 2 and `type` = 'renew' and f_number in (select f_number from t_user_member_cycle where `type` = 'subscribe' and channel_number = channel.f_number and date_format(f_start,'%Y-%m-%d') between date_sub(datev.i_s,interval 14 day) and date_sub(datev.i_e,interval 14 day))) 14_b_retain " + 
		"  ,(select count(*)*7 from t_user_member_cycle where `type` in ('subscribe','renew') and channel_number = channel.f_number and date_format(f_start,'%Y-%m-%d') between date_sub(datev.i_s,interval 14 day) and date_sub(datev.i_e,interval 14 day)) expected_today " + 
		"  ,(select ifnull(sum(deduct_case),0) from t_pg_data t1 left join t_pg_bill t2 on t1.pgbill_f_uuid = t2.f_uuid where msisdn in (select f_phone from t_user_info where channel_number = channel.f_number) and date_format(t1.billcycledate,'%Y-%m-%d') between date_sub(datev.i_s,interval 7 day) and date_sub(datev.i_e,interval 7 day) and date_format(t2.f_date,'%Y-%m-%d') between date_sub(datev.i_s,interval 6 day) and datev.i_e) actual_today " + 
		"  from " + 
		"   (select i,concat(i,'-01') i_s, date_sub(date_add(concat(i,'-01'),interval 1 month), interval 1 day) i_e  " + 
		"    from " + 
		"     (select date_format(date_add('1970-01-01',INTERVAL t2.i*100 + t1.i*10 + t0.i month),'%Y-%m') i  " + 
		"      from " + 
		"       (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t0, " + 
		"       (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t1, " + 
		"       (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t2 " + 
		"     ) x " + 
		"    where x.i between ?1 and ?2 " + 
		"   ) datev " + 
		"   ,(select t1.f_name,t1.f_number,t1.`type`,t2.f_name gameName from t_game_channel t1 left join t_game_info t2 on t1.game_info_f_uuid= t2.f_uuid where t1.f_uuid = ?3) channel " + 
		" ) x",
		countQuery="select count(*) " + 
				"from " + 
				" (select date_format(date_add('1970-01-01',INTERVAL t2.i*100 + t1.i*10 + t0.i month),'%Y-%m') i  " + 
				"  from " + 
				"   (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t0, " + 
				"   (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t1, " + 
				"   (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t2 " + 
				" ) x " + 
				"where x.i between ?1 and ?2",nativeQuery=true)
Page<Map<String, String>> channelMonth(String start, String end,String channelUuid,Pageable page);

@Query(value="select datev.i day " + 
		",game.f_name " + 
		",(select count(*) from t_log_game where `type` = 'detail' and f_game_uuid = game.f_uuid and date_format(f_time,'%Y-%m-%d') = datev.i) detail_count " + 
		",(select count(*) from t_log_game where `type` = 'download' and f_game_uuid = game.f_uuid and date_format(f_time,'%Y-%m-%d') = datev.i) download_count " + 
		",(select count(*) from t_log_game where game_log_form = 'home' and `type` = 'download' and f_game_uuid = game.f_uuid and date_format(f_time,'%Y-%m-%d') = datev.i) home_download_count " + 
		",(select count(*) from t_log_game where game_log_form = 'column_app' and `type` = 'download' and f_game_uuid = game.f_uuid and date_format(f_time,'%Y-%m-%d') = datev.i) column_app_download_count " + 
		",(select count(*) from t_log_game where game_log_form = 'column_general_l1' and `type` = 'download' and f_game_uuid = game.f_uuid and date_format(f_time,'%Y-%m-%d') = datev.i) column_general_l1_download_count " + 
		",(select count(*) from t_log_game where game_log_form = 'column_general_l2' and `type` = 'download' and f_game_uuid = game.f_uuid and date_format(f_time,'%Y-%m-%d') = datev.i) column_general_l2_download_count " + 
		",(select count(*) from t_log_game where game_log_form = 'detail' and `type` = 'download' and f_game_uuid = game.f_uuid and date_format(f_time,'%Y-%m-%d') = datev.i) download_detail_count " + 
		"from " + 
		" (select date_add('1970-01-01',INTERVAL t4.i*10000 + t3.i*1000 + t2.i*100 + t1.i*10 + t0.i day) i " + 
		"  from   " + 
		"   (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t0, " + 
		"   (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t1, " + 
		"   (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t2, " + 
		"   (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t3, " + 
		"   (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t4 " + 
		" ) datev  " + 
		" ,(select f_uuid,f_name from t_game_info where f_deleted = false and status = 'shang_xian') game " + 
		" where " + 
		" if(isnull(?3),1=1,game.f_uuid = ?3) and "+
		" datev.i between ?1 and ?2 "
    ,countQuery="select count(*) " + 
				"from " + 
				" (select date_add('1970-01-01',INTERVAL t4.i*10000 + t3.i*1000 + t2.i*100 + t1.i*10 + t0.i day) i " + 
				"  from   " + 
				"   (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t0, " + 
				"   (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t1, " + 
				"   (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t2, " + 
				"   (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t3, " + 
				"   (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t4 " + 
				" ) datev  " + 
				" ,(select f_uuid,f_name from t_game_info where f_deleted = false and status = 'shang_xian') game " + 
				" where "+ 
				" if(isnull(?3),1=1,game.f_uuid = ?3) and "+ 
				" datev.i between ?1 and ?2",nativeQuery=true)
Page<Map<String,String>> gameDay(String start, String end,String gameInfoUuid,Pageable page);

@Query(value="select datev.i day " + 
		",game.f_name " + 
		",(select count(*) from t_log_game where `type` = 'detail' and f_game_uuid = game.f_uuid and date_format(f_time,'%Y-%m') = datev.i) detail_count " + 
		",(select count(*) from t_log_game where `type` = 'download' and f_game_uuid = game.f_uuid and date_format(f_time,'%Y-%m') = datev.i) download_count " + 
		",(select count(*) from t_log_game where game_log_form = 'home' and `type` = 'download' and f_game_uuid = game.f_uuid and date_format(f_time,'%Y-%m') = datev.i) home_download_count " + 
		",(select count(*) from t_log_game where game_log_form = 'column_app' and `type` = 'download' and f_game_uuid = game.f_uuid and date_format(f_time,'%Y-%m') = datev.i) column_app_download_count " + 
		",(select count(*) from t_log_game where game_log_form = 'column_general_l1' and `type` = 'download' and f_game_uuid = game.f_uuid and date_format(f_time,'%Y-%m') = datev.i) column_general_l1_download_count " + 
		",(select count(*) from t_log_game where game_log_form = 'column_general_l2' and `type` = 'download' and f_game_uuid = game.f_uuid and date_format(f_time,'%Y-%m') = datev.i) column_general_l2_download_count " + 
		",(select count(*) from t_log_game where game_log_form = 'detail' and `type` = 'download' and f_game_uuid = game.f_uuid and date_format(f_time,'%Y-%m') = datev.i) detail_download_count " + 
		"from " + 
		" (select date_format(date_add('1970-01-01',INTERVAL t2.i*100 + t1.i*10 + t0.i month),'%Y-%m') i  " + 
		"  from " + 
		"   (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t0, " + 
		"   (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t1, " + 
		"   (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t2 " + 
		"  ) datev " + 
		" ,(select f_uuid,f_name from t_game_info where f_deleted = false and status = 'shang_xian') game " + 
		" where "+ 
		" if(isnull(?3),1=1,game.f_uuid = ?3) and "+ 
		" datev.i between ?1 and ?2 "
		,countQuery="select count(*) " + 
				"from " + 
				" (select date_add('1970-01-01',INTERVAL t4.i*10000 + t3.i*1000 + t2.i*100 + t1.i*10 + t0.i day) i " + 
				"  from   " + 
				"   (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t0, " + 
				"   (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t1, " + 
				"   (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t2, " + 
				"   (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t3, " + 
				"   (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t4 " + 
				" ) datev  " + 
				" ,(select f_uuid,f_name from t_game_info where f_deleted = false and status = 'shang_xian') game " + 
				" where "+ 
				" if(isnull(?3),1=1,game.f_uuid = ?3) and "+ 
				" datev.i between ?1 and ?2",nativeQuery=true)
Page<Map<String,String>> gameMonth(String start, String end,String gameInfoUuid,Pageable page);
}
