package com.scmt.healthy.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.scmt.healthy.entity.TGroupPerson;
import com.scmt.healthy.entity.TOrderGroupItemProject;
import com.scmt.healthy.entity.TReviewProject;
import com.scmt.healthy.service.ITReviewProjectService;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.mapper.TReviewProjectMapper;
import com.scmt.core.utis.FileUtil;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.text.SimpleDateFormat;

/**
 * @author
 **/
@Service
public class TReviewProjectServiceImpl extends ServiceImpl<TReviewProjectMapper, TReviewProject> implements ITReviewProjectService {
    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private TReviewProjectMapper tReviewProjectMapper;

    @Override
    public IPage<TReviewProject> queryTReviewProjectListByPage(TReviewProject tReviewProject, SearchVo searchVo, PageVo pageVo) {
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
        Page<TReviewProject> pageData = new Page<>(page, limit);
        QueryWrapper<TReviewProject> queryWrapper = new QueryWrapper<>();
        if (tReviewProject != null) {
            queryWrapper = LikeAllField(tReviewProject, searchVo);
        }
        IPage<TReviewProject> result = tReviewProjectMapper.selectPage(pageData, queryWrapper);
        return result;
    }

    @Override
    public List<TReviewProject> queryNoCheckReviewProject(String personId) {
        return tReviewProjectMapper.queryNoCheckReviewProject(personId);
    }

    @Override
    public IPage<TReviewProject> getTGroupPersonReviewer(TReviewProject tReviewProject, SearchVo searchVo, PageVo pageVo) {
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
        Page<TReviewProject> pageData = new Page<>(page, limit);
        QueryWrapper<TReviewProject> queryWrapper = new QueryWrapper<>();
        if (tReviewProject != null) {
            queryWrapper = LikeAllField(tReviewProject, searchVo);
        }
        if((tReviewProject.getDept()!=null && tReviewProject.getDept().trim().length()>0) || (tReviewProject.getTestNum()!=null && tReviewProject.getTestNum().trim().length()>0)){
            return tReviewProjectMapper.getTGroupPersonReviewerAndDept(queryWrapper, pageData,tReviewProject.getDept(),tReviewProject.getTestNum());
        }else{
            queryWrapper.groupBy("t_review_project.person_id");
            return tReviewProjectMapper.getTGroupPersonReviewer(queryWrapper, pageData);
        }
    }

    @Override
    public TGroupPerson getTGroupPersonReviewerById(String id) {
        return tReviewProjectMapper.getTGroupPersonReviewerById(id);
    }

    @Override
    public List<TReviewProject> queryDataListByPersonId(QueryWrapper<TReviewProject> tReviewProjectQueryWrapper) {
        return tReviewProjectMapper.queryDataListByPersonId(tReviewProjectQueryWrapper);
    }

    @Override
    public List<TReviewProject> queryAbandonTReviewProjectList(String personId, String groupId) {
        return tReviewProjectMapper.queryAbandonTReviewProjectList(personId, groupId);
    }

    @Override
    public List<TReviewProject> listByWhere(TReviewProject tReviewProject) {
        return tReviewProjectMapper.listByWhere(tReviewProject);
    }

    @Override
    public List<TGroupPerson> queryReviewPersonData(String orderId) {
        return tReviewProjectMapper.queryReviewPersonData(orderId);
    }

    @Override
    public List<TGroupPerson> queryAllPersonData(String orderId) {
        return tReviewProjectMapper.queryAllPersonData(orderId);
    }

    @Override
    public void download(TReviewProject tReviewProject, HttpServletResponse response) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        QueryWrapper<TReviewProject> queryWrapper = new QueryWrapper<>();
        if (tReviewProject != null) {
            queryWrapper = LikeAllField(tReviewProject, null);
        }
        List<TReviewProject> list = tReviewProjectMapper.selectList(queryWrapper);
        for (TReviewProject re : list) {
            Map<String, Object> map = new LinkedHashMap<>();
            mapList.add(map);
        }
        FileUtil.createExcel(mapList, "exel.xlsx", response);
    }

    /**
     * 功能描述：构建模糊查询
     *
     * @param tReviewProject 需要模糊查询的信息
     * @return 返回查询
     */
    public QueryWrapper<TReviewProject> LikeAllField(TReviewProject tReviewProject, SearchVo searchVo) {
        QueryWrapper<TReviewProject> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(tReviewProject.getId())) {
            queryWrapper.and(i -> i.like("t_review_project.id", tReviewProject.getId()));
        }
        if (StringUtils.isNotBlank(tReviewProject.getPersonId())) {
            queryWrapper.and(i -> i.like("t_review_project.person_id", tReviewProject.getPersonId()));
        }
        if (StringUtils.isNotBlank(tReviewProject.getPersonName())) {
            queryWrapper.and(i -> i.like("t_review_project.person_name", tReviewProject.getPersonName()));
        }
        /*if (StringUtils.isNotBlank(tReviewProject.getTestNum())) {
            queryWrapper.and(i -> i.like("t_review_project.test_num", tReviewProject.getTestNum()));
        }*/
        if (StringUtils.isNotBlank(tReviewProject.getTOrderGroupItemId())) {
            queryWrapper.and(i -> i.like("t_review_project.t_order_group_item_id", tReviewProject.getTOrderGroupItemId()));
        }
        if (StringUtils.isNotBlank(tReviewProject.getName())) {
            queryWrapper.and(i -> i.like("t_review_project.name", tReviewProject.getName()));
        }
        if (StringUtils.isNotBlank(tReviewProject.getShortName())) {
            queryWrapper.and(i -> i.like("t_review_project.short_name", tReviewProject.getShortName()));
        }
        if (tReviewProject.getOrderNum() != null) {
            queryWrapper.and(i -> i.like("t_review_project.order_num", tReviewProject.getOrderNum()));
        }
        if (StringUtils.isNotBlank(tReviewProject.getOfficeId())) {
            queryWrapper.and(i -> i.like("t_review_project.office_id", tReviewProject.getOfficeId()));
        }
        if (StringUtils.isNotBlank(tReviewProject.getOfficeName())) {
            queryWrapper.and(i -> i.like("t_review_project.office_name", tReviewProject.getOfficeName()));
        }
        if (StringUtils.isNotBlank(tReviewProject.getUnitCode())) {
            queryWrapper.and(i -> i.like("t_review_project.unit_code", tReviewProject.getUnitCode()));
        }
        if (StringUtils.isNotBlank(tReviewProject.getUnitName())) {
            queryWrapper.and(i -> i.like("t_review_project.unit_name", tReviewProject.getUnitName()));
        }
        if (StringUtils.isNotBlank(tReviewProject.getDefaultValue())) {
            queryWrapper.and(i -> i.like("t_review_project.default_value", tReviewProject.getDefaultValue()));
        }
        if (StringUtils.isNotBlank(tReviewProject.getResultType())) {
            queryWrapper.and(i -> i.like("t_review_project.result_type", tReviewProject.getResultType()));
        }
        if (StringUtils.isNotBlank(tReviewProject.getInConclusion())) {
            queryWrapper.and(i -> i.like("t_review_project.in_conclusion", tReviewProject.getInConclusion()));
        }
        if (StringUtils.isNotBlank(tReviewProject.getInReport())) {
            queryWrapper.and(i -> i.like("t_review_project.in_report", tReviewProject.getInReport()));
        }
        if (StringUtils.isNotBlank(tReviewProject.getRelationCode())) {
            queryWrapper.and(i -> i.like("t_review_project.relation_code", tReviewProject.getRelationCode()));
        }
        if (StringUtils.isNotBlank(tReviewProject.getGroupOrderId())) {
            queryWrapper.and(i -> i.like("t_review_project.group_order_id", tReviewProject.getGroupOrderId()));
        }

        if (StringUtils.isNotBlank(tReviewProject.getPhysicalType())) {
            queryWrapper.and(i -> i.eq("t_review_project.physical_type", tReviewProject.getPhysicalType()));
        }
        if (tReviewProject.getIsPass() != null) {
            if (tReviewProject.getIsPass() == 99) {
                queryWrapper.and(i -> i.ne("t_review_project.is_pass", 1));
            } else if(tReviewProject.getIsPass() == 88) {
                queryWrapper.and(i -> i.ge("t_review_project.is_pass", 4));
            } else {
                queryWrapper.and(i -> i.eq("t_review_project.is_pass", tReviewProject.getIsPass()));
            }
        }
        if (StringUtils.isNotBlank(tReviewProject.getKeyword())) {
            queryWrapper.and(i -> i.like("t_review_project.person_name", tReviewProject.getKeyword())
                    .or().like("t_review_project.test_num", tReviewProject.getKeyword()));
        }
        if (searchVo != null) {
            SimpleDateFormat format = new SimpleDateFormat();
            if (StringUtils.isNotBlank(searchVo.getStartDate()) && StringUtils.isNotBlank(searchVo.getEndDate())) {
                queryWrapper.and(i->i.ge("t_review_project.regist_date",searchVo.getStartDate()));
                queryWrapper.and(i->i.le("t_review_project.regist_date",searchVo.getEndDate()));
            }
            //当天
            else if (StringUtils.isNotBlank(searchVo.getStartDate()) && StringUtils.isBlank(searchVo.getEndDate())) {
                SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd");
                String date =sdf1.format(new Date());
                date =date+" 00:00:00";
                String finalDate = date;
                queryWrapper.and(i->i.ge("t_review_project.regist_date",finalDate));
                queryWrapper.and(i->i.le("t_review_project.regist_date",searchVo.getStartDate()));
            }
            //当月
            else if (StringUtils.isBlank(searchVo.getStartDate()) && StringUtils.isNotBlank(searchVo.getEndDate())) {
                SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM");
                String date =sdf1.format(new Date());
                date =date+"-01 00:00:00";
                String finalDate = date;
                queryWrapper.and(i->i.ge("t_review_project.regist_date",finalDate));
                queryWrapper.and(i->i.le("t_review_project.regist_date",searchVo.getEndDate()));
            }
        }
        queryWrapper.and(i -> i.eq("t_review_project.del_flag", 0));
        return queryWrapper;
    }
}
