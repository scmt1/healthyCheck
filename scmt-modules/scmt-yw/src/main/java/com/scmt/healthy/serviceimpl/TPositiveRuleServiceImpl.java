package com.scmt.healthy.serviceimpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scmt.healthy.entity.TPositiveRule;
import com.scmt.healthy.mapper.TPositiveRuleMapper;
import com.scmt.healthy.service.ITPositiveRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author dengjie
 * @since 2023-02-15
 */
@Service
public class TPositiveRuleServiceImpl extends ServiceImpl<TPositiveRuleMapper, TPositiveRule> implements ITPositiveRuleService {

    @Autowired
    private TPositiveRuleMapper tPositiveRuleMapper;

    @Override
    public List<TPositiveRule> queryBasicUnitListByArchiveId(String id) {
        return tPositiveRuleMapper.queryBasicUnitListByArchiveId(id);
    }
}
