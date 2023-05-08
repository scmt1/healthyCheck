package com.scmt.healthy.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.core.utis.FileUtil;

import javax.servlet.http.HttpServletResponse;

import com.scmt.healthy.entity.TDepartItemResult;
import com.scmt.healthy.entity.TDepartResult;
import com.scmt.healthy.entity.TGroupPerson;
import com.scmt.healthy.entity.TReviewRecord;
import com.scmt.healthy.mapper.TDepartItemResultMapper;
import com.scmt.healthy.service.ITDepartItemResultService;
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
public class TDepartItemResultServiceImpl extends ServiceImpl<TDepartItemResultMapper, TDepartItemResult> implements ITDepartItemResultService {
    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private TDepartItemResultMapper tDepartItemResultMapper;

    @Override
    public IPage<TDepartItemResult> queryTDepartItemResultListByPage(TDepartItemResult tDepartItemResult, SearchVo searchVo, PageVo pageVo) {
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
        Page<TDepartItemResult> pageData = new Page<>(page, limit);
        QueryWrapper<TDepartItemResult> queryWrapper = new QueryWrapper<>();
        if (tDepartItemResult != null) {
            queryWrapper = LikeAllFeild(tDepartItemResult, searchVo);
        }
        IPage<TDepartItemResult> result = tDepartItemResultMapper.selectPage(pageData, queryWrapper);
        return result;
    }

    @Override
    public IPage<TDepartItemResult> querySummaryResultListByPage(TDepartItemResult tDepartItemResult, SearchVo searchVo, PageVo pageVo) {
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
        Page<TDepartItemResult> pageData = new Page<>(page, limit);
        QueryWrapper<TDepartItemResult> queryWrapper = new QueryWrapper<>();
        if (tDepartItemResult != null) {
            queryWrapper = LikeAllFeild1(tDepartItemResult, searchVo);
        }
        IPage<TDepartItemResult> result = tDepartItemResultMapper.querySummaryResultList(queryWrapper,pageData);
        return result;
    }

    @Override
    public IPage<TDepartItemResult> querySummaryResultListReviewByPage(TDepartItemResult tDepartItemResult, SearchVo searchVo, PageVo pageVo) {
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
        Page<TDepartItemResult> pageData = new Page<>(page, limit);
        QueryWrapper<TDepartItemResult> queryWrapper = new QueryWrapper<>();
        if (tDepartItemResult != null) {
            queryWrapper = LikeAllFeild1(tDepartItemResult, searchVo);
        }
        IPage<TDepartItemResult> result = tDepartItemResultMapper.querySummaryResultListReview(queryWrapper,pageData);
        return result;
    }

    @Override
    public void download(TDepartItemResult tDepartItemResult, HttpServletResponse response) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        QueryWrapper<TDepartItemResult> queryWrapper = new QueryWrapper<>();
        if (tDepartItemResult != null) {
            queryWrapper = LikeAllFeild(tDepartItemResult, null);
        }
        List<TDepartItemResult> list = tDepartItemResultMapper.selectList(queryWrapper);
        for (TDepartItemResult re : list) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("危急程度", re.getCrisisDegree());
            mapList.add(map);
        }
        FileUtil.createExcel(mapList, "exel.xlsx", response);
    }

    @Override
    public List<TDepartItemResult> queryAllTDepartItemResultList(String personId, String officeId, String checkDate, String departResultId) {
        List<TDepartItemResult> result = tDepartItemResultMapper.queryAllTDepartItemResultList(personId,officeId,checkDate,departResultId);
        return result;
    }

    @Override
    public List<TDepartItemResult> queryAllAbnormalItemResultList(TDepartItemResult tDepartItemResult) {
        QueryWrapper<TDepartItemResult> queryWrapper = new QueryWrapper<>();
        if (tDepartItemResult != null) {
            queryWrapper = LikeAllFeild1(tDepartItemResult, null);
        }
        List<TDepartItemResult> result = tDepartItemResultMapper.queryAllAbnormalItemResultList(queryWrapper);
        return result;
    }

    @Override
    public List<TDepartItemResult> getAbnormalResultList(String personId) {
        return tDepartItemResultMapper.getAbnormalResultList(personId);
    }

    @Override
    public List<TDepartItemResult> getAllListByPersonId(String personId) {
        return tDepartItemResultMapper.getAllListByPersonId(personId);
    }


    /**
     * 功能描述：构建模糊查询
     *
     * @param tDepartItemResult 需要模糊查询的信息
     * @return 返回查询
     */
    public QueryWrapper<TDepartItemResult> LikeAllFeild(TDepartItemResult tDepartItemResult, SearchVo searchVo) {
        QueryWrapper<TDepartItemResult> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(tDepartItemResult.getId())) {
            queryWrapper.lambda().and(i -> i.like(TDepartItemResult::getId, tDepartItemResult.getId()));
        }

        if (StringUtils.isNotBlank(tDepartItemResult.getResult())) {
            queryWrapper.lambda().and(i -> i.like(TDepartItemResult::getResult, tDepartItemResult.getResult()));
        }
        if (StringUtils.isNotBlank(tDepartItemResult.getScope())) {
            queryWrapper.lambda().and(i -> i.like(TDepartItemResult::getScope, tDepartItemResult.getScope()));
        }
        if (StringUtils.isNotBlank(tDepartItemResult.getCheckDoc())) {
            queryWrapper.lambda().and(i -> i.like(TDepartItemResult::getCheckDoc, tDepartItemResult.getCheckDoc()));
        }
        if (tDepartItemResult.getCheckDate() != null) {
            queryWrapper.lambda().and(i -> i.like(TDepartItemResult::getCheckDate, tDepartItemResult.getCheckDate()));
        }
        if (StringUtils.isNotBlank(tDepartItemResult.getCrisisDegree())) {
            queryWrapper.lambda().and(i -> i.like(TDepartItemResult::getCrisisDegree, tDepartItemResult.getCrisisDegree()));
        }
        if (tDepartItemResult.getDelFlag() != null) {
            queryWrapper.lambda().and(i -> i.like(TDepartItemResult::getDelFlag, tDepartItemResult.getDelFlag()));
        }
        if (StringUtils.isNotBlank(tDepartItemResult.getCreateId())) {
            queryWrapper.lambda().and(i -> i.like(TDepartItemResult::getCreateId, tDepartItemResult.getCreateId()));
        }
        if (tDepartItemResult.getCreateDate() != null) {
            queryWrapper.lambda().and(i -> i.like(TDepartItemResult::getCreateDate, tDepartItemResult.getCreateDate()));
        }
        if (StringUtils.isNotBlank(tDepartItemResult.getUpdateId())) {
            queryWrapper.lambda().and(i -> i.like(TDepartItemResult::getUpdateId, tDepartItemResult.getUpdateId()));
        }
        if (tDepartItemResult.getUpdateDate() != null) {
            queryWrapper.lambda().and(i -> i.like(TDepartItemResult::getUpdateDate, tDepartItemResult.getUpdateDate()));
        }
        if (StringUtils.isNotBlank(tDepartItemResult.getDeleteId())) {
            queryWrapper.lambda().and(i -> i.like(TDepartItemResult::getDeleteId, tDepartItemResult.getDeleteId()));
        }
        if (tDepartItemResult.getDeleteDate() != null) {
            queryWrapper.lambda().and(i -> i.like(TDepartItemResult::getDeleteDate, tDepartItemResult.getDeleteDate()));
        }
        if (StringUtils.isNotBlank(tDepartItemResult.getArrow())) {
            queryWrapper.lambda().and(i -> i.like(TDepartItemResult::getArrow, tDepartItemResult.getArrow()));
        }
        if (StringUtils.isNotBlank(tDepartItemResult.getPersonId())) {
            queryWrapper.lambda().and(i -> i.like(TDepartItemResult::getPersonId, tDepartItemResult.getPersonId()));
        }
        if (StringUtils.isNotBlank(tDepartItemResult.getOrderGroupItemProjectName())) {
            queryWrapper.lambda().and(i -> i.like(TDepartItemResult::getOrderGroupItemProjectName, tDepartItemResult.getOrderGroupItemProjectName()));
        }
        if (StringUtils.isNotBlank(tDepartItemResult.getOfficeId())) {
            queryWrapper.lambda().and(i -> i.like(TDepartItemResult::getOfficeId, tDepartItemResult.getOfficeId()));
        }
        if (StringUtils.isNotBlank(tDepartItemResult.getOfficeName())) {
            queryWrapper.lambda().and(i -> i.like(TDepartItemResult::getOfficeName, tDepartItemResult.getOfficeName()));
        }
        if (tDepartItemResult.getIgnoreStatus() != null) {
            queryWrapper.lambda().and(i -> i.like(TDepartItemResult::getIgnoreStatus, tDepartItemResult.getIgnoreStatus()));
        }
        if(searchVo!=null){
            if(searchVo.getStartDate()!=null && searchVo.getEndDate()!=null){
                queryWrapper.lambda().and(i -> i.between(TDepartItemResult::getCheckDate, searchVo.getStartDate(),searchVo.getEndDate()));
            }
        }
        queryWrapper.lambda().and(i -> i.eq(TDepartItemResult::getDelFlag, 0));
        return queryWrapper;

    }

    /**
     * 功能描述：构建模糊查询
     *
     * @param tDepartItemResult 需要模糊查询的信息
     * @return 返回查询
     */
    public QueryWrapper<TDepartItemResult> LikeAllFeild1(TDepartItemResult tDepartItemResult, SearchVo searchVo) {
        QueryWrapper<TDepartItemResult> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(tDepartItemResult.getArrow())) {
            if(tDepartItemResult.getArrow().equals("正常")){
//                queryWrapper.and(i -> i.eq("r.arrow", '-'));
                queryWrapper.and(i -> i.eq("r.crisis_degree", "正常").or().eq("r.crisis_degree","-").or().eq("r.positive","0"));
            }else{
//                queryWrapper.and(i -> i.ne("r.arrow", "-").or().eq("r.result", "阳性"));
//                queryWrapper.and(i -> i.ne("r.arrow", "正常"));
                queryWrapper.and(i -> i.ne("r.crisis_degree", "正常"));
                queryWrapper.and(i -> i.ne("r.crisis_degree", "-"));
                queryWrapper.and(i -> i.ne("r.positive", "0"));
            }
        }
        if (StringUtils.isNotBlank(tDepartItemResult.getOfficeId())) {
            queryWrapper.and(i -> i.eq("r.office_id", tDepartItemResult.getOfficeId()));
        }

        if (StringUtils.isNotBlank(tDepartItemResult.getTestNum())) {
            queryWrapper.and(i -> i.eq("p.test_num", tDepartItemResult.getTestNum()));
        }
        if (StringUtils.isNotBlank(tDepartItemResult.getPersonName())) {
            queryWrapper.and(i -> i.like("p.person_name", tDepartItemResult.getPersonName()));
        }
        if (StringUtils.isNotBlank(tDepartItemResult.getPersonId())) {
            queryWrapper.and(i -> i.eq("r.person_id", tDepartItemResult.getPersonId()));
        }
        if (tDepartItemResult.getIgnoreStatus() != null) {
            queryWrapper.and(i -> i.eq("r.ignore_status", tDepartItemResult.getIgnoreStatus()));
        }
        if(searchVo!=null){
            if(searchVo.getStartDate()!=null && searchVo.getEndDate()!=null){
                queryWrapper.and(i -> i.between("r.check_date", searchVo.getStartDate(),searchVo.getEndDate()));
            }
        }
        queryWrapper.and(i -> i.eq("r.del_flag", 0));
        return queryWrapper;

    }

}
