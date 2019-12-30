package com.ijianjian.channel.domain.repository;

import java.util.List;

import org.springframework.data.domain.Sort;

import com.ijianjian.channel.domain.po.ChannelParam;
import com.ijianjian.core.common.interfaces.IRepository;

public interface ChannelParamRepository extends IRepository<ChannelParam, String> {
List<ChannelParam> findByChannel_fUuid(String uuid, Sort sort);
}
