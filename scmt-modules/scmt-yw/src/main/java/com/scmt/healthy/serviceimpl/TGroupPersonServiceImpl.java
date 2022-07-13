package com.scmt.healthy.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.entity.TGroupPerson;
import com.scmt.healthy.mapper.TGroupPersonMapper;
import com.scmt.core.utis.FileUtil;

import javax.servlet.http.HttpServletResponse;

import com.scmt.healthy.service.ITGroupPersonService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.text.SimpleDateFormat;

/**
 * @author
 **/
@Service
public class TGroupPersonServiceImpl extends ServiceImpl<TGroupPersonMapper, TGroupPerson> implements ITGroupPersonService {
    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private TGroupPersonMapper tGroupPersonMapper;

    @Override
    public IPage<TGroupPerson> queryTGroupPersonListByPage(TGroupPerson tGroupPerson, SearchVo searchVo, PageVo pageVo) {
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
        Page<TGroupPerson> pageData = new Page<>(page, limit);
        QueryWrapper<TGroupPerson> queryWrapper = new QueryWrapper<>();
        if (tGroupPerson != null) {
            queryWrapper = LikeAllFeild(tGroupPerson, searchVo);
        }
        IPage<TGroupPerson> result = tGroupPersonMapper.selectPage(pageData, queryWrapper);
        return result;
    }

    @Override
    public IPage<TGroupPerson> queryTGroupPersonAndResultList(TGroupPerson tGroupPerson, PageVo pageVo) {
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
        Page<TGroupPerson> pageData = new Page<>(page, limit);
        QueryWrapper<TGroupPerson> queryWrapper = new QueryWrapper<>();
        if (tGroupPerson != null) {
            queryWrapper.eq("t_group_person.del_flag",0);
            queryWrapper.eq("t_group_person.mobile",tGroupPerson.getMobile());
            if(tGroupPerson.getTestNum() != null && tGroupPerson.getTestNum().trim().length() > 0){
                queryWrapper.like("test_num",tGroupPerson.getTestNum());
            }
        }
        IPage<TGroupPerson> result = tGroupPersonMapper.queryTGroupPersonAndResultList(queryWrapper, pageData);
        return result;
    }

    @Override
    public IPage<TGroupPerson> queryTGroupPersonAndResultAppList(TGroupPerson tGroupPerson, PageVo pageVo) {
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
        Page<TGroupPerson> pageData = new Page<>(page, limit);
        QueryWrapper<TGroupPerson> queryWrapper = new QueryWrapper<>();
        if (tGroupPerson != null) {
            queryWrapper.eq("t_group_person.del_flag",0);
            queryWrapper.eq("t_group_person.physical_type",tGroupPerson.getPhysicalType());
            queryWrapper.eq("t_group_person.mobile",tGroupPerson.getMobile());
            if(tGroupPerson.getTestNum() != null && tGroupPerson.getTestNum().trim().length() > 0){
                queryWrapper.like("test_num",tGroupPerson.getTestNum());
            }
        }
        IPage<TGroupPerson> result = tGroupPersonMapper.queryTGroupPersonAndResultAppList(queryWrapper, pageData);
        return result;
    }

    @Override
    public void download(TGroupPerson tGroupPerson, HttpServletResponse response) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        QueryWrapper<TGroupPerson> queryWrapper = new QueryWrapper<>();
        if (tGroupPerson != null) {
            queryWrapper = LikeAllFeild(tGroupPerson, null);
        }
        List<TGroupPerson> list = tGroupPersonMapper.selectList(queryWrapper);
        for (TGroupPerson re : list) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("人员姓名", re.getPersonName());
            map.put("性别", re.getSex());
            map.put("证件号码", re.getIdCard());
            map.put("出生日期", re.getBirth());
            map.put("年龄", re.getAge());
            map.put("是否结婚（0-未婚，1-已婚）", re.getIsMarry());
            map.put("手机号码", re.getMobile());
            map.put("体检人员工作部门", re.getDept());
            map.put("人员工号", re.getWorkNum());
            map.put("总工龄年数", re.getWorkYear());
            map.put("总工龄月数", re.getWorkMonth());
            map.put("接害工龄年数", re.getExposureWorkYear());
            map.put("接害工龄月数", re.getExposureWorkMonth());
            map.put("工种其他名称", re.getWorkName());
            map.put("在岗状态编码", re.getWorkStateCode());
            map.put("接害开始日期", re.getExposureStartDate());
            map.put("工种代码", re.getWorkTypeCode());
            map.put("监测类型", re.getJcType());
            mapList.add(map);
        }
        FileUtil.createExcel(mapList, "exel.xlsx", response);
    }

    @Override
    public IPage<TGroupPerson> getPersonByOfficeId(List<String> officeId, TGroupPerson tGroupPerson, PageVo pageVo, SearchVo searchVo) {
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
        Page<TGroupPerson> pageData = new Page<>(page, limit);
        QueryWrapper<TGroupPerson> queryWrapper = new QueryWrapper<>();
        if (tGroupPerson != null) {
            queryWrapper = LikeAllFeild(tGroupPerson, searchVo);
        }
        if (tGroupPerson.getIsWzCheck() != null) {
            queryWrapper.and(i -> i.ne("physical_type", "健康体检"));
        }
        String officeIds = "";
        if (officeId != null) {
            for (String s : officeId) {
                officeIds += "," + s;
            }
            officeIds = officeIds.substring(1);
        }
        return tGroupPersonMapper.getPersonByOfficeId(officeId, tGroupPerson.getIsCheck(), officeIds, queryWrapper, pageData);
    }

    @Override
    public Map<String, Object> getGroupPersonByIdWithLink(String id) {
        return tGroupPersonMapper.getGroupPersonByIdWithLink(id);
    }

    @Override
    public List<TGroupPerson> getTGroupPersonByOrderId(String orderId) {
        return tGroupPersonMapper.getTGroupPersonByOrderId(orderId);
    }

    @Override
    public Map<String, Object> getGroupPersonInfo(String id, String type) {
        if (StringUtils.isNotBlank(type)) {
            return tGroupPersonMapper.getGroupPersonInfoById(id);
        } else {
            return tGroupPersonMapper.getGroupPersonInfo(id);
        }
    }

    @Override
    public IPage<TGroupPerson> queryNoCheckProjectPersonList(TGroupPerson tGroupPerson, SearchVo searchVo, PageVo pageVo) {
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
        Page<TGroupPerson> pageData = new Page<>(page, limit);
        QueryWrapper<TGroupPerson> queryWrapper = new QueryWrapper<>();
        if (tGroupPerson != null) {
            queryWrapper = LikeAllFeild1(tGroupPerson, searchVo);
        }
        IPage<TGroupPerson> result = tGroupPersonMapper.queryNoCheckProjectPersonList(queryWrapper, pageData);
        return result;
    }

    @Override
    public IPage<TGroupPerson> queryStatisticPersonList(TGroupPerson tGroupPerson, SearchVo searchVo, PageVo pageVo) {
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
        Page<TGroupPerson> pageData = new Page<>(page, limit);
        QueryWrapper<TGroupPerson> queryWrapper = new QueryWrapper<>();
        if (tGroupPerson != null) {
            queryWrapper = LikeAllFeild1(tGroupPerson, searchVo);
            //登记时间排序
            queryWrapper.orderByDesc("regist_Date");
        }
        IPage<TGroupPerson> result = tGroupPersonMapper.getTGroupPersonInspection(queryWrapper, pageData);
        return result;
    }

    @Override
    public IPage<TGroupPerson> queryExamineFinishPersonList(TGroupPerson tGroupPerson, SearchVo searchVo, PageVo pageVo) {
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
        Page<TGroupPerson> pageData = new Page<>(page, limit);
        QueryWrapper<TGroupPerson> queryWrapper = new QueryWrapper<>();
        if (tGroupPerson != null) {
            queryWrapper = LikeAllFeild1(tGroupPerson, searchVo);
            //登记时间排序
            queryWrapper.orderByDesc("regist_Date");
        }
        IPage<TGroupPerson> result = tGroupPersonMapper.getExamineFinishPersonData(queryWrapper, pageData);
        return result;
    }

    @Override
    public List<TGroupPerson> queryPersonDataListByOrderId(String orderId) {
        return tGroupPersonMapper.queryPersonDataListByOrderId(orderId);
    }

    @Override
    public List<Map<String, Object>> getGroupPersonInfoByIds(List<String> ids) {
        return tGroupPersonMapper.getGroupPersonInfoByIds(ids);
    }

    @Override
    public IPage<TGroupPerson> getPersonReviewerByOfficeId(List<String> officeId, TGroupPerson tGroupPerson, PageVo pageVo, SearchVo searchVo) {
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
        Page<TGroupPerson> pageData = new Page<>(page, limit);
        QueryWrapper<TGroupPerson> queryWrapper = new QueryWrapper<>();
        String startDate = "";
        String endDate = "";
        if (searchVo != null) {
            if (StringUtils.isNotBlank(searchVo.getStartDate()) && StringUtils.isNotBlank(searchVo.getEndDate())) {
                startDate = searchVo.getStartDate();
                endDate = searchVo.getEndDate();
            }
            //当天
            else if (StringUtils.isNotBlank(searchVo.getStartDate()) && StringUtils.isBlank(searchVo.getEndDate())) {
                SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd");
                String date =sdf1.format(new Date());
                date =date+" 00:00:00";
                String finalDate = date;
                startDate = finalDate;
                endDate = searchVo.getStartDate();
            }
            //当月
            else if (StringUtils.isBlank(searchVo.getStartDate()) && StringUtils.isNotBlank(searchVo.getEndDate())) {
                SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM");
                String date =sdf1.format(new Date());
                date =date+"-01 00:00:00";
                String finalDate = date;
                startDate = finalDate;
                endDate = searchVo.getEndDate();
            }
            searchVo.setEndDate(null);
            searchVo.setStartDate(null);
        }

        if (tGroupPerson != null) {
            tGroupPerson.setIsPass(null);
            queryWrapper = LikeAllFeild(tGroupPerson, searchVo);
        }
        if (tGroupPerson.getIsWzCheck() != null) {
            queryWrapper.and(i -> i.ne("physical_type", "健康体检"));
        }
        if(tGroupPerson.getIsCheck() == 0){
            return tGroupPersonMapper.getPersonReviewerCheck(officeId, queryWrapper, pageData,startDate,endDate);
        }
        return tGroupPersonMapper.getPersonReviewerNoCheck(officeId, queryWrapper, pageData,startDate,endDate);
    }

    @Override
    public TGroupPerson getPersonListNum(List<String> orderIdList, String physicalType) {
        return tGroupPersonMapper.getPersonListNum(orderIdList, physicalType);
    }

    @Override
    public TGroupPerson getPersonNumByGroupId(String groupId) {
        return tGroupPersonMapper.getPersonNumByGroupId(groupId);
    }

    @Override
    public IPage<TGroupPerson> getTGroupPersonInspection(TGroupPerson tGroupPerson, SearchVo searchVo, PageVo pageVo) {
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
        Page<TGroupPerson> pageData = new Page<>(page, limit);
        QueryWrapper<TGroupPerson> queryWrapper = new QueryWrapper<>();

        if (tGroupPerson != null) {
            queryWrapper = LikeAllFeild(tGroupPerson, searchVo);
            if(tGroupPerson.getIsReviewer()!=null && tGroupPerson.getIsReviewer()){//复查筛选 true 筛选复查人员
                queryWrapper.eq("is_recheck",1);//是否复查 1是
                queryWrapper.eq("review_statu",1);//复查结果 1已出
            }
        }
        queryWrapper.orderByAsc("diagnosis_date").orderByAsc("person_name");
        IPage<TGroupPerson> result = tGroupPersonMapper.getTGroupPersonInspection(queryWrapper, pageData);
        return result;
    }

    @Override
    public IPage<TGroupPerson> getInspectionTGroupPersonList(TGroupPerson tGroupPerson, SearchVo searchVo, PageVo pageVo) {
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
        Page<TGroupPerson> pageData = new Page<>(page, limit);
        QueryWrapper<TGroupPerson> queryWrapper = new QueryWrapper<>();
        if (tGroupPerson != null) {
            queryWrapper = LikeAllFeild(tGroupPerson, searchVo);
        }
        queryWrapper.orderByAsc("t_group_person.regist_date");
        IPage<TGroupPerson> result = tGroupPersonMapper.getInspectionTGroupPersonList(queryWrapper, pageData);
        return result;
    }

    @Override
    public IPage<TGroupPerson> getInspectionTGroupPersonReviewList(TGroupPerson tGroupPerson, SearchVo searchVo, PageVo pageVo) {
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
        Page<TGroupPerson> pageData = new Page<>(page, limit);
        QueryWrapper<TGroupPerson> queryWrapper = new QueryWrapper<>();
        if (tGroupPerson != null) {
            if(tGroupPerson.getIsPass() != null){
                if(tGroupPerson.getIsPass() == 3){//待总检(复查)
                    tGroupPerson.setIsPass(null);
                    queryWrapper = LikeAllFeild(tGroupPerson, searchVo);
                    queryWrapper.eq("review_statu",0);
                    queryWrapper.gt("is_pass",2);
                }else if(tGroupPerson.getIsPass() == 88){//已总检(复查)
                    tGroupPerson.setIsPass(null);
                    queryWrapper = LikeAllFeild(tGroupPerson, searchVo);
                    queryWrapper.eq("review_statu",1);
                    queryWrapper.gt("is_pass",3);
                }else{//在检(复查)
                    queryWrapper = LikeAllFeild(tGroupPerson, searchVo);
                    queryWrapper.eq("review_statu",0);
                }
            }
        }
        queryWrapper.eq("is_recheck",1);
        queryWrapper.orderByAsc("t_group_person.regist_date");
        IPage<TGroupPerson> result = tGroupPersonMapper.getInspectionTGroupPersonReviewList(queryWrapper, pageData);
        return result;
    }
    @Override
    public  TGroupPerson queryTGroupPersonAndResultApp(TGroupPerson tGroupPerson) {
        QueryWrapper<TGroupPerson> queryWrapper = new QueryWrapper<>();
        SearchVo searchVo=new SearchVo();
        if (tGroupPerson != null) {
            queryWrapper = LikeAllFeild(tGroupPerson,searchVo);

        }
        queryWrapper.eq("t_group_person.id",tGroupPerson.getId());
        TGroupPerson tGroupPeople =  tGroupPersonMapper.selectOne(queryWrapper);
        return tGroupPeople;
    }
    /**
     * 功能描述：构建模糊查询
     *
     * @param tGroupPerson 需要模糊查询的信息
     * @return 返回查询
     */
    public QueryWrapper<TGroupPerson> LikeAllFeild(TGroupPerson tGroupPerson, SearchVo searchVo) {
        QueryWrapper<TGroupPerson> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(i -> i.eq("t_group_person.del_flag", 0));
        if (StringUtils.isNotBlank(tGroupPerson.getPersonName())) {
            queryWrapper.lambda().and(i -> i.like(TGroupPerson::getPersonName, tGroupPerson.getPersonName()));
        }
        if (StringUtils.isNotBlank(tGroupPerson.getSex())) {
            queryWrapper.lambda().and(i -> i.like(TGroupPerson::getSex, tGroupPerson.getSex()));
        }
        if (StringUtils.isNotBlank(tGroupPerson.getIdCard())) {
            queryWrapper.lambda().and(i -> i.like(TGroupPerson::getIdCard, tGroupPerson.getIdCard()));
        }
        if (tGroupPerson.getBirth() != null) {
            queryWrapper.lambda().and(i -> i.like(TGroupPerson::getBirth, tGroupPerson.getBirth()));
        }
        if (tGroupPerson.getAge() != null) {
            queryWrapper.lambda().and(i -> i.like(TGroupPerson::getAge, tGroupPerson.getAge()));
        }
        if (tGroupPerson.getIsMarry() != null) {
            queryWrapper.lambda().and(i -> i.like(TGroupPerson::getIsMarry, tGroupPerson.getIsMarry()));
        }
        if (StringUtils.isNotBlank(tGroupPerson.getMobile())) {
            queryWrapper.lambda().and(i -> i.like(TGroupPerson::getMobile, tGroupPerson.getMobile()));
        }
        if (StringUtils.isNotBlank(tGroupPerson.getDept())) {
            queryWrapper.lambda().and(i -> i.eq(TGroupPerson::getDept, tGroupPerson.getDept()));
        }
        if (StringUtils.isNotBlank(tGroupPerson.getWorkNum())) {
            queryWrapper.lambda().and(i -> i.like(TGroupPerson::getWorkNum, tGroupPerson.getWorkNum()));
        }
        if (tGroupPerson.getWorkYear() != null) {
            queryWrapper.lambda().and(i -> i.like(TGroupPerson::getWorkYear, tGroupPerson.getWorkYear()));
        }
        if (tGroupPerson.getWorkMonth() != null) {
            queryWrapper.lambda().and(i -> i.like(TGroupPerson::getWorkMonth, tGroupPerson.getWorkMonth()));
        }
        if (tGroupPerson.getExposureWorkYear() != null) {
            queryWrapper.lambda().and(i -> i.like(TGroupPerson::getExposureWorkYear, tGroupPerson.getExposureWorkYear()));
        }
        if (tGroupPerson.getExposureWorkMonth() != null) {
            queryWrapper.lambda().and(i -> i.like(TGroupPerson::getExposureWorkMonth, tGroupPerson.getExposureWorkMonth()));
        }
        if (StringUtils.isNotBlank(tGroupPerson.getWorkName())) {
            queryWrapper.lambda().and(i -> i.like(TGroupPerson::getWorkName, tGroupPerson.getWorkName()));
        }
        if (StringUtils.isNotBlank(tGroupPerson.getWorkStateCode())) {
            queryWrapper.lambda().and(i -> i.eq(TGroupPerson::getWorkStateCode, tGroupPerson.getWorkStateCode()));
        }
        if (StringUtils.isNotBlank(tGroupPerson.getTestNum())) {
            queryWrapper.lambda().and(i -> i.eq(TGroupPerson::getTestNum, tGroupPerson.getTestNum()));
        }
        if (tGroupPerson.getExposureStartDate() != null) {
            queryWrapper.lambda().and(i -> i.like(TGroupPerson::getExposureStartDate, tGroupPerson.getExposureStartDate()));
        }
        if (StringUtils.isNotBlank(tGroupPerson.getWorkTypeCode())) {
            queryWrapper.lambda().and(i -> i.eq(TGroupPerson::getWorkTypeCode, tGroupPerson.getWorkTypeCode()));
        }
        if (tGroupPerson.getJcType() != null) {
            queryWrapper.lambda().and(i -> i.eq(TGroupPerson::getJcType, tGroupPerson.getJcType()));
        }
        if (StringUtils.isNotBlank(tGroupPerson.getOrderId())) {
            queryWrapper.lambda().and(i -> i.eq(TGroupPerson::getOrderId, tGroupPerson.getOrderId()));
        }
        if (StringUtils.isNotBlank(tGroupPerson.getUnitId())) {
            queryWrapper.lambda().and(i -> i.eq(TGroupPerson::getUnitId, tGroupPerson.getUnitId()));
        }
        if (StringUtils.isNotBlank(tGroupPerson.getGroupId())) {
            queryWrapper.lambda().and(i -> i.eq(TGroupPerson::getGroupId, tGroupPerson.getGroupId()).or().eq(TGroupPerson::getOldGroupId, tGroupPerson.getGroupId()));
        }
        if (StringUtils.isNotBlank(tGroupPerson.getPhysicalType())) {
            queryWrapper.lambda().and(i -> i.eq(TGroupPerson::getPhysicalType, tGroupPerson.getPhysicalType()));
        }

        if (tGroupPerson.getIsCheck() != null) {
            if (tGroupPerson.getIsCheck() == 1) {
                tGroupPerson.setIsPass(null);
            }
        }
        if (tGroupPerson.getIsWzCheck() != null) {
            queryWrapper.lambda().and(i -> i.eq(TGroupPerson::getIsWzCheck, tGroupPerson.getIsWzCheck()));
            if (tGroupPerson.getIsWzCheck() == 1) {
                tGroupPerson.setIsPass(null);
            }
        }
        if (tGroupPerson.getIsPass() != null) {
            if (tGroupPerson.getIsPass() == 99) {
                queryWrapper.lambda().and(i -> i.ne(TGroupPerson::getIsPass, 1).ne(TGroupPerson::getIsPass, 10));
            } else if (tGroupPerson.getIsPass() == 88) {
                queryWrapper.lambda().and(i -> i.ge(TGroupPerson::getIsPass, 4));
                queryWrapper.lambda().and(i -> i.lt(TGroupPerson::getIsPass, 10));
            } else if (tGroupPerson.getIsPass() == 1) {
                queryWrapper.lambda().and(i -> i.eq(TGroupPerson::getIsPass, 1).or().eq(TGroupPerson::getIsPass, 10));
            } else if (tGroupPerson.getIsPass() == 2) {
                if (tGroupPerson.getIsCheck() != null) {
                    if (tGroupPerson.getIsCheck() == 0) {
                        queryWrapper.lambda().and(i -> i.ge(TGroupPerson::getIsPass, tGroupPerson.getIsPass()));
                    }else{
                        queryWrapper.lambda().and(i -> i.eq(TGroupPerson::getIsPass, tGroupPerson.getIsPass()));
                    }
                }else{
                    queryWrapper.lambda().and(i -> i.eq(TGroupPerson::getIsPass, tGroupPerson.getIsPass()));
                }
            } else {
                queryWrapper.lambda().and(i -> i.eq(TGroupPerson::getIsPass, tGroupPerson.getIsPass()));
            }
        }
        if (StringUtils.isNotBlank(tGroupPerson.getKeyword())) {
            queryWrapper.lambda().and(i -> i.like(TGroupPerson::getPersonName, tGroupPerson.getKeyword())
                    .or().eq(TGroupPerson::getMobile, tGroupPerson.getKeyword())
                    .or().eq(TGroupPerson::getTestNum, tGroupPerson.getKeyword())
                    .or().eq(TGroupPerson::getId, tGroupPerson.getKeyword())
                    .or().like(TGroupPerson::getDept,tGroupPerson.getKeyword())
                    .or().eq(TGroupPerson::getIdCard, tGroupPerson.getKeyword()));
        }
        if (searchVo != null) {
            SimpleDateFormat format = new SimpleDateFormat();
            if (StringUtils.isNotBlank(searchVo.getStartDate()) && StringUtils.isNotBlank(searchVo.getEndDate())) {
                queryWrapper.lambda().and(i->i.ge(TGroupPerson::getRegistDate,searchVo.getStartDate()));
                queryWrapper.lambda().and(i->i.le(TGroupPerson::getRegistDate,searchVo.getEndDate()));
            }
            //当天
            else if (StringUtils.isNotBlank(searchVo.getStartDate()) && StringUtils.isBlank(searchVo.getEndDate())) {
                SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd");
                String date =sdf1.format(new Date());
                date =date+" 00:00:00";
                String finalDate = date;
                queryWrapper.lambda().and(i->i.ge(TGroupPerson::getRegistDate,finalDate));
                queryWrapper.lambda().and(i->i.le(TGroupPerson::getRegistDate,searchVo.getStartDate()));
            }
            //当月
            else if (StringUtils.isBlank(searchVo.getStartDate()) && StringUtils.isNotBlank(searchVo.getEndDate())) {
                SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM");
                String date =sdf1.format(new Date());
                date =date+"-01 00:00:00";
                String finalDate = date;
                queryWrapper.lambda().and(i->i.ge(TGroupPerson::getRegistDate,finalDate));
                queryWrapper.lambda().and(i->i.le(TGroupPerson::getRegistDate,searchVo.getEndDate()));
            }
        }
        return queryWrapper;
    }

    /**
     * 功能描述：构建模糊查询
     *
     * @param tGroupPerson 需要模糊查询的信息
     * @return 返回查询
     */
    public QueryWrapper<TGroupPerson> LikeAllFeild1(TGroupPerson tGroupPerson, SearchVo searchVo) {
        QueryWrapper<TGroupPerson> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(tGroupPerson.getPersonName())) {
            queryWrapper.lambda().and(i -> i.eq(TGroupPerson::getPersonName, tGroupPerson.getPersonName()));
        }
        if (StringUtils.isNotBlank(tGroupPerson.getOrderId())) {
            queryWrapper.lambda().and(i -> i.eq(TGroupPerson::getOrderId, tGroupPerson.getOrderId()));
        }
        if (StringUtils.isNotBlank(tGroupPerson.getIdCard())) {
            queryWrapper.lambda().and(i -> i.eq(TGroupPerson::getIdCard, tGroupPerson.getIdCard()));
        }
        if (StringUtils.isNotBlank(tGroupPerson.getWorkStateCode())) {
            queryWrapper.lambda().and(i -> i.eq(TGroupPerson::getWorkStateCode, tGroupPerson.getWorkStateCode()));
        }
        if (StringUtils.isNotBlank(tGroupPerson.getTestNum())) {
            queryWrapper.lambda().and(i -> i.eq(TGroupPerson::getTestNum, tGroupPerson.getTestNum()));
        }
        if (StringUtils.isNotBlank(tGroupPerson.getSex())) {
            queryWrapper.lambda().and(i -> i.like(TGroupPerson::getSex, tGroupPerson.getSex()));
        }
        if (StringUtils.isNotBlank(tGroupPerson.getPhysicalType())) {
            queryWrapper.lambda().and(i -> i.eq(TGroupPerson::getPhysicalType, tGroupPerson.getPhysicalType()));
        }
        if (tGroupPerson.getOrderIdList().size() > 0) {
            queryWrapper.and(i -> i.in("t_group_person.order_id", tGroupPerson.getOrderIdList()));
        }
        if (StringUtils.isNotBlank(tGroupPerson.getGroupId())) {
            queryWrapper.and(i -> i.eq("t_group_person.group_id", tGroupPerson.getGroupId()));
        }
        if (tGroupPerson.getAge() != null) {
            queryWrapper.lambda().and(i -> i.like(TGroupPerson::getAge, tGroupPerson.getAge()));
        }

        if (tGroupPerson.getStatu() != null) {
            queryWrapper.lambda().and(i -> i.eq(TGroupPerson::getStatu, tGroupPerson.getStatu()));
        }
        if (tGroupPerson.getIsPass() != null) {
            if (tGroupPerson.getIsPass() == 99) {//未登记
                queryWrapper.and(i -> i.ne("t_group_person.is_pass", 1));
            } else if (tGroupPerson.getIsPass() == 87) {//待总检
                queryWrapper.and(i -> i.eq("t_group_person.is_pass", 3));
            } else if (tGroupPerson.getIsPass() == 88) {//已总检
                queryWrapper.and(i -> i.eq("t_group_person.is_pass", 4)
                        .or().eq("t_group_person.is_pass", 5));
            } else if (tGroupPerson.getIsPass() == 89) {//全部
                queryWrapper.and(i -> i.eq("t_group_person.is_pass", 1)
                        .or().eq("t_group_person.is_pass", 2)
                        .or().eq("t_group_person.is_pass", 3)
                        .or().eq("t_group_person.is_pass", 4)
                        .or().eq("t_group_person.is_pass", 5));
            } else {//在体检
                queryWrapper.and(i -> i.eq("t_group_person.is_pass", tGroupPerson.getIsPass()));
            }
        }
        if (tGroupPerson.getAge() != null) {
            queryWrapper.and(i -> i.eq("t_group_person.age", tGroupPerson.getAge()));
        }
        queryWrapper.and(i -> i.eq("t_group_person.del_flag", 0));

        if (searchVo != null) {
            SimpleDateFormat format = new SimpleDateFormat();
            if (StringUtils.isNotBlank(searchVo.getStartDate()) && StringUtils.isNotBlank(searchVo.getEndDate())) {
                queryWrapper.lambda().and(i->i.ge(TGroupPerson::getRegistDate,searchVo.getStartDate()));
                queryWrapper.lambda().and(i->i.le(TGroupPerson::getRegistDate,searchVo.getEndDate()));
            }
            //当天
            else if (StringUtils.isNotBlank(searchVo.getStartDate()) && StringUtils.isBlank(searchVo.getEndDate())) {
                SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd");
                String date =sdf1.format(new Date());
                date =date+" 00:00:00";
                String finalDate = date;
                queryWrapper.lambda().and(i->i.ge(TGroupPerson::getRegistDate,finalDate));
                queryWrapper.lambda().and(i->i.le(TGroupPerson::getRegistDate,searchVo.getStartDate()));
            }
            //当月
            else if (StringUtils.isBlank(searchVo.getStartDate()) && StringUtils.isNotBlank(searchVo.getEndDate())) {
                SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM");
                String date =sdf1.format(new Date());
                date =date+"-01 00:00:00";
                String finalDate = date;
                queryWrapper.lambda().and(i->i.ge(TGroupPerson::getRegistDate,finalDate));
                queryWrapper.lambda().and(i->i.le(TGroupPerson::getRegistDate,searchVo.getEndDate()));
            }
        }
        return queryWrapper;
    }

    @Override
    public Integer updatewAutograph(){
        return tGroupPersonMapper.updatewAutograph();
    }


}
