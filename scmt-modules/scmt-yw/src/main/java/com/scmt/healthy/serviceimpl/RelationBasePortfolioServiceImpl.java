package com.scmt.healthy.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.entity.RelationBasePortfolio;
import com.scmt.healthy.mapper.RelationBasePortfolioMapper;
import com.scmt.healthy.service.IRelationBasePortfolioService;
import com.scmt.core.utis.FileUtil;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedHashMap;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author
 **/
@Service
public class RelationBasePortfolioServiceImpl extends ServiceImpl<RelationBasePortfolioMapper, RelationBasePortfolio> implements IRelationBasePortfolioService {
    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private RelationBasePortfolioMapper relationBasePortfolioMapper;

    @Override
    public IPage<RelationBasePortfolio> queryRelationBasePortfolioListByPage(RelationBasePortfolio relationBasePortfolio, SearchVo searchVo, PageVo pageVo) {
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
        Page<RelationBasePortfolio> pageData = new Page<>(page, limit);
        QueryWrapper<RelationBasePortfolio> queryWrapper = new QueryWrapper<>();
        if (relationBasePortfolio != null) {
            queryWrapper = LikeAllFeild(relationBasePortfolio, searchVo);
        }
        IPage<RelationBasePortfolio> result = relationBasePortfolioMapper.selectPage(pageData, queryWrapper);
        return result;
    }

    @Override
    public void download(RelationBasePortfolio relationBasePortfolio, HttpServletResponse response) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        QueryWrapper<RelationBasePortfolio> queryWrapper = new QueryWrapper<>();
        if (relationBasePortfolio != null) {
            queryWrapper = LikeAllFeild(relationBasePortfolio, null);
        }
        List<RelationBasePortfolio> list = relationBasePortfolioMapper.selectList(queryWrapper);
        for (RelationBasePortfolio re : list) {
            Map<String, Object> map = new LinkedHashMap<>();
            mapList.add(map);
        }
        FileUtil.createExcel(mapList, "exel.xlsx", response);
    }

    @Override
    public ArrayList<String> queryBaseProjectIdList(String portfolioId) {
        return relationBasePortfolioMapper.queryBaseProjectIdList(portfolioId);
    }

    /**
     * 功能描述：构建模糊查询
     *
     * @param relationBasePortfolio 需要模糊查询的信息
     * @return 返回查询
     */
    public QueryWrapper<RelationBasePortfolio> LikeAllFeild(RelationBasePortfolio relationBasePortfolio, SearchVo searchVo) {
        QueryWrapper<RelationBasePortfolio> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(relationBasePortfolio.getId())) {
            queryWrapper.lambda().and(i -> i.eq(RelationBasePortfolio::getId, relationBasePortfolio.getId()));
        }
        if (StringUtils.isNotBlank(relationBasePortfolio.getBaseProjectId())) {
            queryWrapper.lambda().and(i -> i.eq(RelationBasePortfolio::getBaseProjectId, relationBasePortfolio.getBaseProjectId()));
        }
        if (StringUtils.isNotBlank(relationBasePortfolio.getPortfolioProjectId())) {
            queryWrapper.lambda().and(i -> i.eq(RelationBasePortfolio::getPortfolioProjectId, relationBasePortfolio.getPortfolioProjectId()));
        }
        return queryWrapper;

    }
}
