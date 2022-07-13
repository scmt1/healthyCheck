package com.scmt.healthy.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.scmt.healthy.entity.TOrderFlow;
import com.scmt.healthy.service.ITOrderFlowService;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.mapper.TOrderFlowMapper;
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
public class TOrderFlowServiceImpl extends ServiceImpl<TOrderFlowMapper, TOrderFlow> implements ITOrderFlowService {
    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private TOrderFlowMapper tOrderFlowMapper;

    @Override
    public IPage<TOrderFlow> queryTOrderFlowListByPage(TOrderFlow tOrderFlow, SearchVo searchVo, PageVo pageVo) {
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
        Page<TOrderFlow> pageData = new Page<>(page, limit);
        QueryWrapper<TOrderFlow> queryWrapper = new QueryWrapper<>();
        if (tOrderFlow != null) {
            queryWrapper = LikeAllFeild(tOrderFlow, searchVo);
        }
        IPage<TOrderFlow> result = tOrderFlowMapper.selectPage(pageData, queryWrapper);
        return result;
    }

    @Override
    public void download(TOrderFlow tOrderFlow, HttpServletResponse response) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        QueryWrapper<TOrderFlow> queryWrapper = new QueryWrapper<>();
        if (tOrderFlow != null) {
            queryWrapper = LikeAllFeild(tOrderFlow, null);
        }
        List<TOrderFlow> list = tOrderFlowMapper.selectList(queryWrapper);
        for (TOrderFlow re : list) {
            Map<String, Object> map = new LinkedHashMap<>();
            mapList.add(map);
        }
        FileUtil.createExcel(mapList, "exel.xlsx", response);
    }

    @Override
    public List<TOrderFlow> queryAllTOrderFlowList(TOrderFlow tOrderFlow) {
        QueryWrapper<TOrderFlow> queryWrapper = new QueryWrapper<>();
        if (tOrderFlow != null) {
            queryWrapper = LikeAllFeild(tOrderFlow, null);
        }
        queryWrapper.orderByAsc("create_time");
        List<TOrderFlow> list = tOrderFlowMapper.selectList(queryWrapper);
        return list;
    }

    /**
     * 功能描述：构建模糊查询
     *
     * @param tOrderFlow 需要模糊查询的信息
     * @return 返回查询
     */
    public QueryWrapper<TOrderFlow> LikeAllFeild(TOrderFlow tOrderFlow, SearchVo searchVo) {
        QueryWrapper<TOrderFlow> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(tOrderFlow.getId())) {
            queryWrapper.lambda().and(i -> i.eq(TOrderFlow::getId, tOrderFlow.getId()));
        }
        if (StringUtils.isNotBlank(tOrderFlow.getGroupOrderId())) {
            queryWrapper.lambda().and(i -> i.eq(TOrderFlow::getGroupOrderId, tOrderFlow.getGroupOrderId()));
        }
        if (StringUtils.isNotBlank(tOrderFlow.getAuditUserId())) {
            queryWrapper.lambda().and(i -> i.eq(TOrderFlow::getAuditUserId, tOrderFlow.getAuditUserId()));
        }
        if (StringUtils.isNotBlank(tOrderFlow.getAuditUserName())) {
            queryWrapper.lambda().and(i -> i.like(TOrderFlow::getAuditUserName, tOrderFlow.getAuditUserName()));
        }
        if (tOrderFlow.getCreateTime() != null) {
            queryWrapper.lambda().and(i -> i.like(TOrderFlow::getCreateTime, tOrderFlow.getCreateTime()));
        }
        if (StringUtils.isNotBlank(tOrderFlow.getCreateUserId())) {
            queryWrapper.lambda().and(i -> i.eq(TOrderFlow::getCreateUserId, tOrderFlow.getCreateUserId()));
        }
        if (StringUtils.isNotBlank(tOrderFlow.getCreateUserName())) {
            queryWrapper.lambda().and(i -> i.like(TOrderFlow::getCreateUserName, tOrderFlow.getCreateUserName()));
        }
        if (tOrderFlow.getAuditTime() != null) {
            queryWrapper.lambda().and(i -> i.like(TOrderFlow::getAuditTime, tOrderFlow.getAuditTime()));
        }
        if (StringUtils.isNotBlank(tOrderFlow.getAuditContent())) {
            queryWrapper.lambda().and(i -> i.like(TOrderFlow::getAuditContent, tOrderFlow.getAuditContent()));
        }
        if (StringUtils.isNotBlank(tOrderFlow.getShowUserId())) {
            queryWrapper.lambda().and(i -> i.eq(TOrderFlow::getShowUserId, tOrderFlow.getShowUserId()));
        }
        if (StringUtils.isNotBlank(tOrderFlow.getShowUserName())) {
            queryWrapper.lambda().and(i -> i.like(TOrderFlow::getShowUserName, tOrderFlow.getShowUserName()));
        }
        if (tOrderFlow.getAuditState() != null) {
            queryWrapper.lambda().and(i -> i.like(TOrderFlow::getAuditState, tOrderFlow.getAuditState()));
        }
        if (searchVo != null) {
            if (searchVo.getStartDate() != null && searchVo.getEndDate() != null) {
                queryWrapper.lambda().and(i -> i.between(TOrderFlow::getCreateTime, searchVo.getStartDate(), searchVo.getEndDate()));
            }
        }
        return queryWrapper;

    }
}
