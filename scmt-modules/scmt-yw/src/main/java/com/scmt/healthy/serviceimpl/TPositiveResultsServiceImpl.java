package com.scmt.healthy.serviceimpl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.entity.TPositiveResults;
import com.scmt.healthy.mapper.TPositiveResultsMapper;
import com.scmt.healthy.service.ITPositiveResultsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author dengjie
 * @since 2023-02-14
 */
@Service
public class TPositiveResultsServiceImpl extends ServiceImpl<TPositiveResultsMapper, TPositiveResults> implements ITPositiveResultsService {

    @Autowired
    private TPositiveResultsMapper tPositiveResultsMapper;

    @Override
    public IPage<TPositiveResults> querytPositiveResultList(TPositiveResults  tPositiveResults, SearchVo searchVo, PageVo pageVo) {
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
        Page<TPositiveResults> pageData = new Page<>(page, limit);
        QueryWrapper<TPositiveResults> queryWrapper = new QueryWrapper<>();
        if (tPositiveResults !=null) {
            queryWrapper = LikeAllFeild(tPositiveResults,searchVo);
        }
        IPage<TPositiveResults> result = tPositiveResultsMapper.selectPage(pageData, queryWrapper);
        return  result;
    }

    @Override
    public List<TPositiveResults> getPositiveResultList(TPositiveResults  tPositiveResults, SearchVo searchVo) {
        QueryWrapper<TPositiveResults> queryWrapper = new QueryWrapper<>();
        if (tPositiveResults !=null) {
            queryWrapper = LikeAllFeild(tPositiveResults,searchVo);
        }
        List<TPositiveResults> result = tPositiveResultsMapper.selectList(queryWrapper);
        return  result;
    }

    @Override
    public TPositiveResults getByName(String name) {
        return tPositiveResultsMapper.getByName(name);
    }


    /**
     * 功能描述：构建模糊查询
     * @param tPositiveResults 需要模糊查询的信息
     * @return 返回查询
     */
    public QueryWrapper<TPositiveResults>  LikeAllFeild(TPositiveResults  tPositiveResults, SearchVo searchVo) {
        QueryWrapper<TPositiveResults> queryWrapper = new QueryWrapper<>();
        if (tPositiveResults.getName() != null) {
            queryWrapper.lambda().and(i -> i.like(TPositiveResults::getName, tPositiveResults.getName()));
        }
        if (tPositiveResults.getDeptId() != null) {
            queryWrapper.lambda().and(i -> i.like(TPositiveResults::getDeptId, tPositiveResults.getDeptId()));
        }
        return queryWrapper;
    }

}
