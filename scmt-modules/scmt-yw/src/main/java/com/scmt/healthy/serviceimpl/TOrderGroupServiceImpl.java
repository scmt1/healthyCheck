package com.scmt.healthy.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.scmt.healthy.entity.TGroupPerson;
import com.scmt.healthy.entity.TOrderGroup;
import com.scmt.healthy.service.ITOrderGroupService;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.mapper.TOrderGroupMapper;
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
public class TOrderGroupServiceImpl extends ServiceImpl<TOrderGroupMapper, TOrderGroup> implements ITOrderGroupService {
    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private TOrderGroupMapper tOrderGroupMapper;

    @Override
    public IPage<TOrderGroup> queryTOrderGroupListByPage(TOrderGroup tOrderGroup, SearchVo searchVo, PageVo pageVo) {
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
        Page<TOrderGroup> pageData = new Page<>(page, limit);
        QueryWrapper<TOrderGroup> queryWrapper = new QueryWrapper<>();
        if (tOrderGroup != null) {
            queryWrapper = LikeAllFeild(tOrderGroup, searchVo);
        }
        IPage<TOrderGroup> result = tOrderGroupMapper.selectPage(pageData, queryWrapper);
        return result;
    }

    @Override
    public void download(TOrderGroup tOrderGroup, HttpServletResponse response) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        QueryWrapper<TOrderGroup> queryWrapper = new QueryWrapper<>();
        if (tOrderGroup != null) {
            queryWrapper = LikeAllFeild(tOrderGroup, null);
        }
        List<TOrderGroup> list = tOrderGroupMapper.selectList(queryWrapper);
        for (TOrderGroup re : list) {
            Map<String, Object> map = new LinkedHashMap<>();
            mapList.add(map);
        }
        FileUtil.createExcel(mapList, "exel.xlsx", response);
    }

    @Override
    public List<TOrderGroup> queryTOrderGroupList(TOrderGroup tOrderGroup) {
        QueryWrapper<TOrderGroup> queryWrapper = new QueryWrapper<>();
        if (tOrderGroup != null) {
            queryWrapper = LikeAllFeild(tOrderGroup, null);
            queryWrapper.orderByAsc("order_num");
        }
        List<TOrderGroup> list = tOrderGroupMapper.selectList(queryWrapper);
        return list;
    }

    @Override
    public List<TOrderGroup> getTOrderGroupByGroupOrderId(String groupOrderId) {
        List<TOrderGroup> list = tOrderGroupMapper.getTOrderGroupByGroupOrderId(groupOrderId);
        return list;
    }

    @Override
    public List<TOrderGroup> getTOrderGroupByGroupUnitId(String groupUnitId) {
        List<TOrderGroup> list = tOrderGroupMapper.getTOrderGroupByGroupUnitId(groupUnitId);
        return list;
    }

    @Override
    public Map<String, Object> queryCheckProjectAndHazardFactors(String groupOrderId) {
        return tOrderGroupMapper.queryCheckProjectAndHazardFactors(groupOrderId);
    }

    @Override
    public Map<String, Object> queryCheckProjectAndHazardFactorsHealthy(String groupOrderId) {
        return tOrderGroupMapper.queryCheckProjectAndHazardFactorsHealthy(groupOrderId);
    }

    @Override
    public List<TGroupPerson> queryCheckResultByOrderId(String groupOrderId) {
        return tOrderGroupMapper.queryCheckResultByOrderId(groupOrderId);
    }

    @Override
    public List<TGroupPerson> queryReviewResultByOrderId(String groupOrderId) {
        return tOrderGroupMapper.queryReviewResultByOrderId(groupOrderId);
    }

    @Override
    public List<Map<String, Object>> queryCheckProjectAndHazardFactorsList(String groupOrderId) {
        return tOrderGroupMapper.queryCheckProjectAndHazardFactorsList(groupOrderId);
    }

    @Override
    public List<Map<String, Object>> queryCheckProjectAndHazardFactorsComboList(List<String> comboIdList) {
        return tOrderGroupMapper.queryCheckProjectAndHazardFactorsComboList(comboIdList);
    }

    @Override
    public List<Map<String, Object>> queryCheckResultAndHazardFactorsList(String groupOrderId) {
        return tOrderGroupMapper.queryCheckResultAndHazardFactorsList(groupOrderId);
    }

    @Override
    public List<Map<String, Object>> queryCheckResultReview(String groupOrderId) {
        return tOrderGroupMapper.queryCheckResultReview(groupOrderId);
    }

    @Override
    public List<Map<String, Object>> queryRegistDateReview(String groupOrderId) {
        return tOrderGroupMapper.queryRegistDateReview(groupOrderId);
    }

    @Override
    public List<Map<String, Object>> queryRegistDate(String groupOrderId) {
        return tOrderGroupMapper.queryRegistDate(groupOrderId);
    }

    /**
     * 功能描述：构建模糊查询
     *
     * @param tOrderGroup 需要模糊查询的信息
     * @return 返回查询
     */
    public QueryWrapper<TOrderGroup> LikeAllFeild(TOrderGroup tOrderGroup, SearchVo searchVo) {
        QueryWrapper<TOrderGroup> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(tOrderGroup.getId())) {
            queryWrapper.lambda().and(i -> i.like(TOrderGroup::getId, tOrderGroup.getId()));
        }
        if (StringUtils.isNotBlank(tOrderGroup.getGroupOrderId())) {
            queryWrapper.lambda().and(i -> i.like(TOrderGroup::getGroupOrderId, tOrderGroup.getGroupOrderId()));
        }
        if (StringUtils.isNotBlank(tOrderGroup.getName())) {
            queryWrapper.lambda().and(i -> i.like(TOrderGroup::getName, tOrderGroup.getName()));
        }
        if (tOrderGroup.getDelFlag() != null) {
            queryWrapper.lambda().and(i -> i.like(TOrderGroup::getDelFlag, tOrderGroup.getDelFlag()));
        }
        if (StringUtils.isNotBlank(tOrderGroup.getCreateId())) {
            queryWrapper.lambda().and(i -> i.like(TOrderGroup::getCreateId, tOrderGroup.getCreateId()));
        }
        if (tOrderGroup.getCreateTime() != null) {
            queryWrapper.lambda().and(i -> i.like(TOrderGroup::getCreateTime, tOrderGroup.getCreateTime()));
        }
        if (StringUtils.isNotBlank(tOrderGroup.getUpdateId())) {
            queryWrapper.lambda().and(i -> i.like(TOrderGroup::getUpdateId, tOrderGroup.getUpdateId()));
        }
        if (tOrderGroup.getUpdateTime() != null) {
            queryWrapper.lambda().and(i -> i.like(TOrderGroup::getUpdateTime, tOrderGroup.getUpdateTime()));
        }
        if (StringUtils.isNotBlank(tOrderGroup.getDeleteId())) {
            queryWrapper.lambda().and(i -> i.like(TOrderGroup::getDeleteId, tOrderGroup.getDeleteId()));
        }
        if (tOrderGroup.getDeleteTime() != null) {
            queryWrapper.lambda().and(i -> i.like(TOrderGroup::getDeleteTime, tOrderGroup.getDeleteTime()));
        }
        if (tOrderGroup.getDiscount() != null) {
            queryWrapper.lambda().and(i -> i.like(TOrderGroup::getDiscount, tOrderGroup.getDiscount()));
        }
        if (StringUtils.isNotBlank(tOrderGroup.getAdditionalPayment())) {
            queryWrapper.lambda().and(i -> i.like(TOrderGroup::getAdditionalPayment, tOrderGroup.getAdditionalPayment()));
        }
        if (tOrderGroup.getAddDiscount() != null) {
            queryWrapper.lambda().and(i -> i.like(TOrderGroup::getAddDiscount, tOrderGroup.getAddDiscount()));
        }
        if (tOrderGroup.getPersonCount() != null) {
            queryWrapper.lambda().and(i -> i.like(TOrderGroup::getPersonCount, tOrderGroup.getPersonCount()));
        }
        if (StringUtils.isNotBlank(tOrderGroup.getComboId())) {
            queryWrapper.lambda().and(i -> i.like(TOrderGroup::getComboId, tOrderGroup.getComboId()));
        }
        if (searchVo != null) {
            if (searchVo.getStartDate() != null && searchVo.getEndDate() != null) {
                queryWrapper.lambda().and(i -> i.between(TOrderGroup::getCreateTime, searchVo.getStartDate(), searchVo.getEndDate()));
            }
        }
        queryWrapper.lambda().and(i -> i.eq(TOrderGroup::getDelFlag, 0));
        return queryWrapper;

    }
}
