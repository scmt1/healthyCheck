package com.scmt.healthy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.scmt.healthy.entity.TPositiveRule;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author dengjie
 * @since 2023-02-15
 */
public interface ITPositiveRuleService extends IService<TPositiveRule> {
    /**
     * 根据id查询阳性规则
     * @return
     */

    List<TPositiveRule> queryBasicUnitListByArchiveId(String id);


}
