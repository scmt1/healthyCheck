package com.scmt.healthy.serviceimpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scmt.healthy.entity.TPositiveResultsRule;
import com.scmt.healthy.mapper.TPositiveResultsRuleMapper;
import com.scmt.healthy.service.ITPositiveResultsRuleService;
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
public class TPositiveResultsRuleServiceImpl extends ServiceImpl<TPositiveResultsRuleMapper, TPositiveResultsRule> implements ITPositiveResultsRuleService {


    @Autowired
    private TPositiveResultsRuleMapper tPositiveResultsRuleMapper;

    @Override
    public List<TPositiveResultsRule> getByPositiveId(String id) {
        return tPositiveResultsRuleMapper.getByPositiveId(id);
    }

    @Override
    public TPositiveResultsRule getById(String id) {
        return tPositiveResultsRuleMapper.getById(id);
    }

    @Override
    public List<TPositiveResultsRule> getAllPositiceRuleList() {
        return tPositiveResultsRuleMapper.getAllPositiceRuleList();
    }
}
