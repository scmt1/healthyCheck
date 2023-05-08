package com.scmt.healthy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scmt.healthy.entity.TPositiveResultsRule;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author dengjie
 * @since 2023-02-15
 */
public interface TPositiveResultsRuleMapper extends BaseMapper<TPositiveResultsRule> {

    List<TPositiveResultsRule> getByPositiveId(String id);

    TPositiveResultsRule getById(String id);

    /**
     * 查询所有阳性规则
     * @return
     */
    List<TPositiveResultsRule> getAllPositiceRuleList();
}
