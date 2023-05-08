package com.scmt.healthy.mapper;

import com.scmt.healthy.entity.TPositiveRule;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author dengjie
 * @since 2023-02-15
 */
public interface TPositiveRuleMapper extends BaseMapper<TPositiveRule> {

    List<TPositiveRule> queryBasicUnitListByArchiveId(String id);
}
