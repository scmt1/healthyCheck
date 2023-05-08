package com.scmt.healthy.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.entity.TPortfolioProject;
import com.scmt.healthy.entity.TReviewPerson;
import com.scmt.healthy.mapper.TPortfolioProjectMapper;
import com.scmt.healthy.mapper.TReviewPersonMapper;
import com.scmt.healthy.service.ITReviewPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author
 **/
@Service
public class TReviewPersonServiceImpl extends ServiceImpl<TReviewPersonMapper, TReviewPerson> implements ITReviewPersonService {
    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private TReviewPersonMapper tReviewPersonMapper;

    @Autowired
    private TPortfolioProjectMapper tPortfolioProjectMapper;

    @Override
    public IPage<TReviewPerson> queryTReviewPersonListByPage(TReviewPerson tReviewPerson, SearchVo searchVo, PageVo pageVo) {
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
        Page<TReviewPerson> pageData = new Page<>(page, limit);
        QueryWrapper<TReviewPerson> queryWrapper = new QueryWrapper<>();
        if (tReviewPerson != null) {
            queryWrapper = LikeAllFeild(tReviewPerson, searchVo);
        }
        IPage<TReviewPerson> result = tReviewPersonMapper.selectPage(pageData, queryWrapper);
        return result;
    }

    @Override
    public IPage<TReviewPerson> getReviewProjectPerson(TReviewPerson tReviewPerson, SearchVo searchVo, PageVo pageVo) {
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
        Page pageData = new Page<>(page, limit);
        QueryWrapper<TReviewPerson> queryWrapper = new QueryWrapper<>();
        if (tReviewPerson != null) {
            queryWrapper = LikeAllFeildNew(tReviewPerson, searchVo);
        }
        IPage<TReviewPerson> result = tReviewPersonMapper.getReviewProjectPerson(queryWrapper, pageData);
        if(result!=null && result.getCurrent()>0 && result.getRecords()!=null && result.getRecords().size()>0){
            QueryWrapper<TPortfolioProject> tReviewPersonQueryWrapper = new QueryWrapper<>();
            tReviewPersonQueryWrapper.lambda().and(i -> i.eq(TPortfolioProject::getDelFlag, 0));
            //查询所有的组合项目
            List<TPortfolioProject> tPortfolioProjects = tPortfolioProjectMapper.selectList(tReviewPersonQueryWrapper);
            /**
             * 循环遍历查询体检小结
             */
            List<TReviewPerson> records = result.getRecords();
            if(records!=null && records.size()>0){
                for (TReviewPerson item : records) {
                    if(StringUtils.isNotBlank(item.getWorkStateText())){
                        item.setWorkStateText(item.getWorkStateText().replaceAll(" ", ""));
                    }
                    if (StringUtils.isNotBlank(item.getId()) && StringUtils.isNotBlank(item.getPortfolioProjectId())) {
                        List<TPortfolioProject> collect = tPortfolioProjects.stream().filter(aa -> item.getPortfolioProjectId().equals(aa.getId())).collect(Collectors.toList());
                        if (collect.size() > 0) {
                            item.setPortfolioProjectName(collect.get(0).getName());
                            item.setOfficeName(collect.get(0).getOfficeName());
                            item.setSalePrice(collect.get(0).getSalePrice());
                        }
                    }
                }
            }
        }
        return result;
    }

    @Override
    public TReviewPerson getReviewPersonById(String id) {
        QueryWrapper<TReviewPerson> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("t_review_person.id",id);
        return tReviewPersonMapper.getReviewPersonById(queryWrapper);
    }


    /**
     * 功能描述：构建模糊查询
     *
     * @param tReviewPerson 需要模糊查询的信息
     * @return 返回查询
     */
    public QueryWrapper<TReviewPerson> LikeAllFeild(TReviewPerson tReviewPerson, SearchVo searchVo) {
        QueryWrapper<TReviewPerson> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(i -> i.eq("t_review_person.del_flag", 0));
        if (StringUtils.isNotBlank(tReviewPerson.getPersonName())) {
            queryWrapper.lambda().and(i -> i.like(TReviewPerson::getPersonName, tReviewPerson.getPersonName()));
        }
        if (StringUtils.isNotBlank(tReviewPerson.getIdCard())) {
            queryWrapper.lambda().and(i -> i.like(TReviewPerson::getIdCard, tReviewPerson.getIdCard()));
        }
        if (StringUtils.isNotBlank(tReviewPerson.getDept())) {
            queryWrapper.lambda().and(i -> i.eq(TReviewPerson::getDept, tReviewPerson.getDept()));
        }
        if (StringUtils.isNotBlank(tReviewPerson.getTestNum())) {
            queryWrapper.lambda().and(i -> i.eq(TReviewPerson::getTestNum, tReviewPerson.getTestNum()));
        }
        if (StringUtils.isNotBlank(tReviewPerson.getOrderId())) {
            queryWrapper.lambda().and(i -> i.eq(TReviewPerson::getOrderId, tReviewPerson.getOrderId()));
        }
        if (StringUtils.isNotBlank(tReviewPerson.getUnitId())) {
            queryWrapper.lambda().and(i -> i.eq(TReviewPerson::getUnitId, tReviewPerson.getUnitId()));
        }
        if (StringUtils.isNotBlank(tReviewPerson.getGroupId())) {
            queryWrapper.lambda().and(i -> i.eq(TReviewPerson::getGroupId, tReviewPerson.getGroupId()));
        }
        if (StringUtils.isNotBlank(tReviewPerson.getPhysicalType())) {
            queryWrapper.lambda().and(i -> i.eq(TReviewPerson::getPhysicalType, tReviewPerson.getPhysicalType()));
        }
        if (tReviewPerson.getIsPass() != null) {
            queryWrapper.lambda().and(i -> i.eq(TReviewPerson::getIsPass, tReviewPerson.getIsPass()));
        }
        if (searchVo != null) {
            SimpleDateFormat format = new SimpleDateFormat();
            if (StringUtils.isNotBlank(searchVo.getStartDate()) && StringUtils.isNotBlank(searchVo.getEndDate())) {
                queryWrapper.lambda().and(i->i.ge(TReviewPerson::getRegistDate,searchVo.getStartDate()));
                queryWrapper.lambda().and(i->i.le(TReviewPerson::getRegistDate,searchVo.getEndDate()));
            }
            //当天
            else if (StringUtils.isNotBlank(searchVo.getStartDate()) && StringUtils.isBlank(searchVo.getEndDate())) {
                SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd");
                String date =sdf1.format(new Date());
                date =date+" 00:00:00";
                String finalDate = date;
                queryWrapper.lambda().and(i->i.ge(TReviewPerson::getRegistDate,finalDate));
                queryWrapper.lambda().and(i->i.le(TReviewPerson::getRegistDate,searchVo.getStartDate()));
            }
            //当月
            else if (StringUtils.isBlank(searchVo.getStartDate()) && StringUtils.isNotBlank(searchVo.getEndDate())) {
                SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM");
                String date =sdf1.format(new Date());
                date =date+"-01 00:00:00";
                String finalDate = date;
                queryWrapper.lambda().and(i->i.ge(TReviewPerson::getRegistDate,finalDate));
                queryWrapper.lambda().and(i->i.le(TReviewPerson::getRegistDate,searchVo.getEndDate()));
            }
        }
        return queryWrapper;
    }
    /**
     * 功能描述：构建模糊查询
     *
     * @param tReviewPerson 需要模糊查询的信息
     * @return 返回查询
     */
    public QueryWrapper<TReviewPerson> LikeAllFeildNew(TReviewPerson tReviewPerson, SearchVo searchVo) {

        QueryWrapper<TReviewPerson> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(i -> i.eq("t_review_person.del_flag", 0));
        if (StringUtils.isNotBlank(tReviewPerson.getPersonName())) {
            queryWrapper.and(i -> i.like("t_review_person.person_name", tReviewPerson.getPersonName()));
        }
        if (StringUtils.isNotBlank(tReviewPerson.getIdCard())) {
            queryWrapper.and(i -> i.like("t_review_person.id_card", tReviewPerson.getIdCard()));
        }
        if (StringUtils.isNotBlank(tReviewPerson.getDept())) {
            queryWrapper.and(i -> i.like("t_review_person.dept", tReviewPerson.getDept()));
        }
        if (StringUtils.isNotBlank(tReviewPerson.getPortfolioProjectName())) {
            queryWrapper.and(i -> i.like("t_review_project.portfolio_project_name", tReviewPerson.getPortfolioProjectName()));
        }
        if (StringUtils.isNotBlank(tReviewPerson.getTestNum())) {
            queryWrapper.and(i -> i.like("t_review_person.test_num", tReviewPerson.getTestNum()));
        }
        if (StringUtils.isNotBlank(tReviewPerson.getWorkStateCode())) {
            queryWrapper.and(i -> i.eq("t_group_person.work_state_code", tReviewPerson.getWorkStateCode()));
        }
        if (StringUtils.isNotBlank(tReviewPerson.getSex())) {
            queryWrapper.and(i -> i.eq("t_group_person.sex", tReviewPerson.getSex()));
        }
        if (StringUtils.isNotBlank(tReviewPerson.getOrderId())) {
            queryWrapper.lambda().and(i -> i.eq(TReviewPerson::getOrderId, tReviewPerson.getOrderId()));
        }
        if (StringUtils.isNotBlank(tReviewPerson.getUnitId())) {
            queryWrapper.lambda().and(i -> i.eq(TReviewPerson::getUnitId, tReviewPerson.getUnitId()));
        }
        if (StringUtils.isNotBlank(tReviewPerson.getGroupId())) {
            queryWrapper.lambda().and(i -> i.eq(TReviewPerson::getGroupId, tReviewPerson.getGroupId()));
        }
        if (StringUtils.isNotBlank(tReviewPerson.getPhysicalType())) {
            queryWrapper.lambda().and(i -> i.eq(TReviewPerson::getPhysicalType, tReviewPerson.getPhysicalType()));
        }
        if (tReviewPerson.getIsPass() != null) {
            if (tReviewPerson.getIsPass() == 99) {//未登记
                queryWrapper.and(i -> i.ne("t_review_person.is_pass", 1));
            } else if (tReviewPerson.getIsPass() == 87) {//待总检
                queryWrapper.and(i -> i.eq("t_review_person.is_pass", 3));
            } else if (tReviewPerson.getIsPass() == 88) {//已总检
                queryWrapper.and(i -> i.ge("t_review_person.is_pass", 4));
            } else if (tReviewPerson.getIsPass() == 89) {//全部
                queryWrapper.and(i -> i.ge("t_review_person.is_pass", 1));
            } else {//在体检
                queryWrapper.and(i -> i.eq("t_review_person.is_pass", tReviewPerson.getIsPass()));
            }
        }
        if (searchVo != null) {
            SimpleDateFormat format = new SimpleDateFormat();
            if (StringUtils.isNotBlank(searchVo.getStartDate()) && StringUtils.isNotBlank(searchVo.getEndDate())) {
                queryWrapper.and(i -> i.ge("t_review_person.regist_date", searchVo.getStartDate()));
                queryWrapper.and(i -> i.le("t_review_person.regist_date", searchVo.getEndDate()));
            }
            //当天
            else if (StringUtils.isNotBlank(searchVo.getStartDate()) && StringUtils.isBlank(searchVo.getEndDate())) {
                SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd");
                String date =sdf1.format(new Date());
                date =date+" 00:00:00";
                String finalDate = date;

                queryWrapper.and(i -> i.ge("t_review_person.regist_date", finalDate));
                queryWrapper.and(i -> i.le("t_review_person.regist_date", searchVo.getStartDate()));
            }
            //当月
            else if (StringUtils.isBlank(searchVo.getStartDate()) && StringUtils.isNotBlank(searchVo.getEndDate())) {
                SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM");
                String date =sdf1.format(new Date());
                date =date+"-01 00:00:00";
                String finalDate = date;

                queryWrapper.and(i -> i.ge("t_review_person.regist_date", finalDate));
                queryWrapper.and(i -> i.le("t_review_person.regist_date", searchVo.getEndDate()));
            }
        }
        return queryWrapper;
    }
}
