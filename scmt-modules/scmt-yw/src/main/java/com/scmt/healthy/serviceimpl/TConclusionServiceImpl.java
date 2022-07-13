package com.scmt.healthy.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.entity.TConclusion;
import com.scmt.healthy.mapper.TConclusionMapper;
import com.scmt.healthy.service.ITConclusionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @author
 **/
@Service
public class TConclusionServiceImpl extends ServiceImpl<TConclusionMapper, TConclusion> implements ITConclusionService {
    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private TConclusionMapper tConclusionMapper;


    @Override
    public IPage<TConclusion> queryTConclusionDataListByPage(TConclusion tConclusion, SearchVo searchVo, PageVo pageVo) {
        int page = 1;
        int limit = 10;
        if (pageVo != null) {
            if (pageVo.getPageNumber() != 0) {
                page = pageVo.getPageNumber();
            }
            if (pageVo.getPageSize() != 0) {
                limit = pageVo.getPageSize();
            }
        }
        Page<TConclusion> pageData = new Page<>(page, limit);
        QueryWrapper<TConclusion> queryWrapper = new QueryWrapper<>();
        if (tConclusion != null) {
            queryWrapper = LikeAllFeild(tConclusion, searchVo);
        }
        queryWrapper.orderByDesc("create_time");
        return tConclusionMapper.selectPage(pageData, queryWrapper);

    }

    /**
     * 功能描述：构建模糊查询
     *
     * @param tConclusion 需要模糊查询的信息
     * @return 返回查询
     */
    public QueryWrapper<TConclusion> LikeAllFeild(TConclusion tConclusion, SearchVo searchVo) {
        QueryWrapper<TConclusion> queryWrapper = new QueryWrapper<>();

        if (StringUtils.isNotBlank(tConclusion.getId())) {
            queryWrapper.lambda().and(i -> i.eq(TConclusion::getId, tConclusion.getId()));
        }
        if (StringUtils.isNotBlank(tConclusion.getComments())) {
            queryWrapper.lambda().and(i -> i.like(TConclusion::getComments, tConclusion.getComments()));
        }
        if (StringUtils.isNotBlank(tConclusion.getPersonName())) {
            queryWrapper.lambda().and(i -> i.like(TConclusion::getPersonName, tConclusion.getPersonName()));
        }
        if (StringUtils.isNotBlank(tConclusion.getSeeing())) {
            queryWrapper.lambda().and(i -> i.like(TConclusion::getSeeing, tConclusion.getSeeing()));
        }
        if (StringUtils.isNotBlank(tConclusion.getViewpos())) {
            queryWrapper.lambda().and(i -> i.like(TConclusion::getViewpos, tConclusion.getViewpos()));
        }
        if (StringUtils.isNotBlank(tConclusion.getType())) {
            queryWrapper.lambda().and(i -> i.like(TConclusion::getType, tConclusion.getType()));
        }
        if (StringUtils.isNotBlank(tConclusion.getCode())) {
            queryWrapper.lambda().and(i -> i.like(TConclusion::getCode, tConclusion.getCode()));
        }
        if (searchVo != null) {
            if (searchVo.getStartDate() != null && searchVo.getEndDate() != null) {
                queryWrapper.lambda().and(i -> i.between(TConclusion::getCreateTime, searchVo.getStartDate(), searchVo.getEndDate()));
            }
        }
        return queryWrapper;
    }
}
