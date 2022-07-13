package com.scmt.healthy.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.scmt.healthy.entity.TDepartResult;
import com.scmt.healthy.service.ITDepartResultService;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.mapper.TDepartResultMapper;
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
public class TDepartResultServiceImpl extends ServiceImpl<TDepartResultMapper, TDepartResult> implements ITDepartResultService {
    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private TDepartResultMapper tDepartResultMapper;

    @Override
    public List<TDepartResult> queryTDepartResultList(TDepartResult tDepartResult, SearchVo searchVo) {
        if(tDepartResult != null && tDepartResult.getGroupItemName() != null && tDepartResult.getGroupItemName().indexOf("复") > -1){
            QueryWrapper<TDepartResult> queryWrapper = new QueryWrapper<>();
            queryWrapper = LikeAllFeild(tDepartResult, searchVo);
            queryWrapper.orderByAsc("check_date");
            List<TDepartResult> result = tDepartResultMapper.selectList(queryWrapper);
            return result;
        }else{
            List<TDepartResult> result = tDepartResultMapper.queryTDepartResultList(tDepartResult.getPersonId(),tDepartResult.getGroupId());
            return result;
        }
    }

    @Override
    public List<TDepartResult> queryTDepartResultAndProjectId(TDepartResult tDepartResult, SearchVo searchVo) {
        QueryWrapper<TDepartResult> queryWrapper = new QueryWrapper<>();
        if (tDepartResult != null) {
            queryWrapper.eq("t_depart_result.office_id", tDepartResult.getOfficeId());
            queryWrapper.eq("t_depart_result.person_id", tDepartResult.getPersonId());
            queryWrapper.eq("t_depart_result.del_flag", 0);
            if (searchVo != null) {
                if (StringUtils.isNotBlank(searchVo.getStartDate()) && StringUtils.isNotBlank(searchVo.getEndDate())) {
                    queryWrapper.between("check_date", searchVo.getStartDate(), searchVo.getEndDate());
                }
            }
        }
        List<TDepartResult> result = tDepartResultMapper.queryTDepartResultAndProjectId(queryWrapper);
        return result;
    }

    @Override
    public IPage<TDepartResult> queryTDepartResultListAndOfficeNameByPage(TDepartResult tDepartResult, SearchVo searchVo, PageVo pageVo) {
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
        Page<TDepartResult> pageData = new Page<>(page, limit);
        QueryWrapper<TDepartResult> queryWrapper = new QueryWrapper<>();
        if (tDepartResult != null) {
            queryWrapper = LikeAllFeild1(tDepartResult, searchVo);
        }
        IPage<TDepartResult> result = tDepartResultMapper.queryTDepartResultListAndOfficeName(queryWrapper, pageData);
        return result;
    }

    @Override
    public List<TDepartResult> queryPersonCheckOffice(TDepartResult tDepartResult) {
        List<TDepartResult> result = tDepartResultMapper.queryPersonCheckOffice(tDepartResult);
        return result;
    }

    @Override
    public Integer isCheckComplete(String personId, String groupId, String officeId) {
        return tDepartResultMapper.isCheckComplete(personId, groupId, officeId);
    }

    @Override
    public Integer queryTDepartResultByPersonId(List<String> groupItemIdList, String personId) {
        return tDepartResultMapper.queryTDepartResultByPersonId(groupItemIdList, personId);
    }

    @Override
    public void download(TDepartResult tDepartResult, HttpServletResponse response) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        QueryWrapper<TDepartResult> queryWrapper = new QueryWrapper<>();
        if (tDepartResult != null) {
            queryWrapper = LikeAllFeild(tDepartResult, null);
        }
        List<TDepartResult> list = tDepartResultMapper.selectList(queryWrapper);
        for (TDepartResult re : list) {
            Map<String, Object> map = new LinkedHashMap<>();
            mapList.add(map);
        }
        FileUtil.createExcel(mapList, "exel.xlsx", response);
    }

    /**
     * 功能描述：构建模糊查询
     *
     * @param tDepartResult 需要模糊查询的信息
     * @return 返回查询
     */
    public QueryWrapper<TDepartResult> LikeAllFeild(TDepartResult tDepartResult, SearchVo searchVo) {
        QueryWrapper<TDepartResult> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(tDepartResult.getId())) {
            queryWrapper.lambda().and(i -> i.eq(TDepartResult::getId, tDepartResult.getId()));
        }
        if (StringUtils.isNotBlank(tDepartResult.getPersonId())) {
            queryWrapper.lambda().and(i -> i.eq(TDepartResult::getPersonId, tDepartResult.getPersonId()));
        }
        if (tDepartResult.getState() != null) {
            queryWrapper.lambda().and(i -> i.eq(TDepartResult::getState, tDepartResult.getState()));
        }

        if (StringUtils.isNotBlank(tDepartResult.getOfficeId())) {
            queryWrapper.lambda().and(i -> i.eq(TDepartResult::getOfficeId, tDepartResult.getOfficeId()));
        }
        if (StringUtils.isNotBlank(tDepartResult.getOfficeName())) {
            queryWrapper.lambda().and(i -> i.like(TDepartResult::getOfficeName, tDepartResult.getOfficeName()));
        }
        if (StringUtils.isNotBlank(tDepartResult.getGroupItemName())) {
            queryWrapper.lambda().and(i -> i.like(TDepartResult::getGroupItemName, tDepartResult.getGroupItemName()));
        }
        if (searchVo != null) {
            if (StringUtils.isNotBlank(searchVo.getStartDate()) && StringUtils.isNotBlank(searchVo.getEndDate())) {
                queryWrapper.between("check_date", searchVo.getStartDate(), searchVo.getEndDate());
            }
        }
        queryWrapper.lambda().and(i -> i.eq(TDepartResult::getDelFlag, 0));
        return queryWrapper;
    }

    /**
     * 功能描述：构建模糊查询
     *
     * @param tDepartResult 需要模糊查询的信息
     * @return 返回查询
     */
    public QueryWrapper<TDepartResult> LikeAllFeild1(TDepartResult tDepartResult, SearchVo searchVo) {
        QueryWrapper<TDepartResult> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(tDepartResult.getPersonId())) {
            queryWrapper.and(i -> i.eq("r.person_id", tDepartResult.getPersonId()));
        }
        if (StringUtils.isNotBlank(tDepartResult.getGroupItemId())) {
            queryWrapper.and(i -> i.eq("r.group_item_id", tDepartResult.getGroupItemId()));
        }
        queryWrapper.and(i -> i.eq("r.del_flag", 0));
        return queryWrapper;

    }
}
