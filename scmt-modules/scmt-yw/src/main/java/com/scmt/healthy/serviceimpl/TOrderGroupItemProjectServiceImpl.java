package com.scmt.healthy.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.scmt.healthy.entity.TOrderGroupItem;
import com.scmt.healthy.entity.TOrderGroupItemProject;
import com.scmt.healthy.service.ITOrderGroupItemProjectService;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.mapper.TOrderGroupItemProjectMapper;
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
public class TOrderGroupItemProjectServiceImpl extends ServiceImpl<TOrderGroupItemProjectMapper, TOrderGroupItemProject> implements ITOrderGroupItemProjectService {
    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private TOrderGroupItemProjectMapper tOrderGroupItemProjectMapper;

    @Override
    public IPage<TOrderGroupItemProject> queryTOrderGroupItemProjectListByPage(TOrderGroupItemProject tOrderGroupItemProject, SearchVo searchVo, PageVo pageVo) {
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
        Page<TOrderGroupItemProject> pageData = new Page<>(page, limit);
        QueryWrapper<TOrderGroupItemProject> queryWrapper = new QueryWrapper<>();
        if (tOrderGroupItemProject != null) {
            queryWrapper = LikeAllFeild(tOrderGroupItemProject, searchVo);
        }
        IPage<TOrderGroupItemProject> result = tOrderGroupItemProjectMapper.selectPage(pageData, queryWrapper);
        return result;
    }

    @Override
    public void download(TOrderGroupItemProject tOrderGroupItemProject, HttpServletResponse response) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        QueryWrapper<TOrderGroupItemProject> queryWrapper = new QueryWrapper<>();
        if (tOrderGroupItemProject != null) {
            queryWrapper = LikeAllFeild(tOrderGroupItemProject, null);
        }
        List<TOrderGroupItemProject> list = tOrderGroupItemProjectMapper.selectList(queryWrapper);
        for (TOrderGroupItemProject re : list) {
            Map<String, Object> map = new LinkedHashMap<>();
            mapList.add(map);
        }
        FileUtil.createExcel(mapList, "exel.xlsx", response);
    }

    @Override
    public List<TOrderGroupItem> queryNoCheckTOrderGroupItemProjectList(String personId, String groupId) {
        return tOrderGroupItemProjectMapper.queryNoCheckTOrderGroupItemProjectList(personId, groupId);
    }

    @Override
    public List<TOrderGroupItemProject> getOrderGroupITemProjectByReview(String portfolioId, String groupId, List<String> officeId) {
        return tOrderGroupItemProjectMapper.getOrderGroupITemProjectByReview(portfolioId, groupId, officeId);
    }

    @Override
    public List<TOrderGroupItem> queryAbandonTOrderGroupItemProjectList(String personId, String groupId) {
        return tOrderGroupItemProjectMapper.queryAbandonTOrderGroupItemProjectList(personId, groupId);
    }

    /**
     * 功能描述：构建模糊查询
     *
     * @param tOrderGroupItemProject 需要模糊查询的信息
     * @return 返回查询
     */
    public QueryWrapper<TOrderGroupItemProject> LikeAllFeild(TOrderGroupItemProject tOrderGroupItemProject, SearchVo searchVo) {
        QueryWrapper<TOrderGroupItemProject> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(tOrderGroupItemProject.getId())) {
            queryWrapper.lambda().and(i -> i.like(TOrderGroupItemProject::getId, tOrderGroupItemProject.getId()));
        }
        if (StringUtils.isNotBlank(tOrderGroupItemProject.getTOrderGroupItemId())) {
            queryWrapper.lambda().and(i -> i.like(TOrderGroupItemProject::getTOrderGroupItemId, tOrderGroupItemProject.getTOrderGroupItemId()));
        }
        if (StringUtils.isNotBlank(tOrderGroupItemProject.getCode())) {
            queryWrapper.lambda().and(i -> i.like(TOrderGroupItemProject::getCode, tOrderGroupItemProject.getCode()));
        }
        if (StringUtils.isNotBlank(tOrderGroupItemProject.getName())) {
            queryWrapper.lambda().and(i -> i.like(TOrderGroupItemProject::getName, tOrderGroupItemProject.getName()));
        }
        if (StringUtils.isNotBlank(tOrderGroupItemProject.getShortName())) {
            queryWrapper.lambda().and(i -> i.like(TOrderGroupItemProject::getShortName, tOrderGroupItemProject.getShortName()));
        }
        if (tOrderGroupItemProject.getOrderNum() != null) {
            queryWrapper.lambda().and(i -> i.like(TOrderGroupItemProject::getOrderNum, tOrderGroupItemProject.getOrderNum()));
        }
        if (StringUtils.isNotBlank(tOrderGroupItemProject.getOfficeId())) {
            queryWrapper.lambda().and(i -> i.like(TOrderGroupItemProject::getOfficeId, tOrderGroupItemProject.getOfficeId()));
        }
        if (StringUtils.isNotBlank(tOrderGroupItemProject.getDefaultValue())) {
            queryWrapper.lambda().and(i -> i.like(TOrderGroupItemProject::getDefaultValue, tOrderGroupItemProject.getDefaultValue()));
        }
        if (StringUtils.isNotBlank(tOrderGroupItemProject.getResultType())) {
            queryWrapper.lambda().and(i -> i.like(TOrderGroupItemProject::getResultType, tOrderGroupItemProject.getResultType()));
        }
        if (StringUtils.isNotBlank(tOrderGroupItemProject.getInConclusion())) {
            queryWrapper.lambda().and(i -> i.like(TOrderGroupItemProject::getInConclusion, tOrderGroupItemProject.getInConclusion()));
        }
        if (StringUtils.isNotBlank(tOrderGroupItemProject.getInReport())) {
            queryWrapper.lambda().and(i -> i.like(TOrderGroupItemProject::getInReport, tOrderGroupItemProject.getInReport()));
        }
        if (StringUtils.isNotBlank(tOrderGroupItemProject.getRelationCode())) {
            queryWrapper.lambda().and(i -> i.like(TOrderGroupItemProject::getRelationCode, tOrderGroupItemProject.getRelationCode()));
        }
        return queryWrapper;

    }
}
