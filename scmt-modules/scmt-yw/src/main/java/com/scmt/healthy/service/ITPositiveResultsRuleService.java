package com.scmt.healthy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.scmt.healthy.entity.TPositiveResultsRule;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author dengjie
 * @since 2023-02-15
 */
public interface ITPositiveResultsRuleService extends IService<TPositiveResultsRule> {

    List<TPositiveResultsRule> getByPositiveId(String id);

    TPositiveResultsRule getById(String id);


    /**
     * 查询所有阳性规则
     * @return
     */
    List<TPositiveResultsRule> getAllPositiceRuleList();


}
