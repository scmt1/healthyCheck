package com.scmt.healthy.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.scmt.healthy.entity.TCertificateManage;
import com.scmt.healthy.entity.TUnitReport;
import com.scmt.healthy.service.ITCertificateManageService;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.mapper.TCertificateManageMapper;
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
public class TCertificateManageServiceImpl extends ServiceImpl<TCertificateManageMapper, TCertificateManage> implements ITCertificateManageService {
    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private TCertificateManageMapper tCertificateManageMapper;

    @Override
    public IPage<TCertificateManage> queryTCertificateManageListByPage(TCertificateManage tCertificateManage, SearchVo searchVo, PageVo pageVo) {
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
        Page<TCertificateManage> pageData = new Page<>(page, limit);
        QueryWrapper<TCertificateManage> queryWrapper = new QueryWrapper<>();
        if (tCertificateManage != null) {
            queryWrapper = LikeAllField(tCertificateManage, searchVo);
        }
//        IPage<TCertificateManage> result = tCertificateManageMapper.selectPage(pageData, queryWrapper);
        IPage<TCertificateManage> result = tCertificateManageMapper.queryTCertificateManageListByPage(queryWrapper, pageData);
        return result;
    }

    @Override
    public List<TCertificateManage> queryTCertificateManageByNotPage(TCertificateManage tCertificateManage, SearchVo searchVo) {
        List<TCertificateManage> result = tCertificateManageMapper.queryTCertificateManageListByNotPage(tCertificateManage, searchVo);
        return result;
    }

    @Override
    public void download(TCertificateManage tCertificateManage, HttpServletResponse response) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        QueryWrapper<TCertificateManage> queryWrapper = new QueryWrapper<>();
        if (tCertificateManage != null) {
            queryWrapper = LikeAllField(tCertificateManage, null);
        }
        List<TCertificateManage> list = tCertificateManageMapper.selectList(queryWrapper);
        for (TCertificateManage re : list) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("编号", re.getCode());
            map.put("姓名", re.getName());
            map.put("性别", re.getSex());
            map.put("年龄", re.getAge());
            map.put("有效期", re.getTermOfValidity());
            map.put("发证单位", re.getUnitOfIssue());
            mapList.add(map);
        }
        FileUtil.createExcel(mapList, "exel.xlsx", response);
    }

    /**
     * 功能描述：构建模糊查询
     *
     * @param tCertificateManage 需要模糊查询的信息
     * @return 返回查询
     */
    public QueryWrapper<TCertificateManage> LikeAllField(TCertificateManage tCertificateManage, SearchVo searchVo) {
        QueryWrapper<TCertificateManage> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(tCertificateManage.getId())) {
            queryWrapper.and(i -> i.like("t_certificate_manage.id", tCertificateManage.getId()));
        }
        if (StringUtils.isNotBlank(tCertificateManage.getOrderId())) {
            queryWrapper.and(i -> i.like("t_certificate_manage.order_id", tCertificateManage.getOrderId()));
        }
        if (StringUtils.isNotBlank(tCertificateManage.getCode())) {
            queryWrapper.and(i -> i.like("t_certificate_manage.code", tCertificateManage.getCode()));
        }
        if (StringUtils.isNotBlank(tCertificateManage.getName())) {
            queryWrapper.and(i -> i.like("t_certificate_manage.name", tCertificateManage.getName()));
        }
        if (StringUtils.isNotBlank(tCertificateManage.getSex())) {
            queryWrapper.and(i -> i.like("t_certificate_manage.sex", tCertificateManage.getSex()));
        }
        if (tCertificateManage.getAge() != null) {
            queryWrapper.and(i -> i.like("t_certificate_manage.age", tCertificateManage.getAge()));
        }
        if (StringUtils.isNotBlank(tCertificateManage.getResults())) {
            queryWrapper.and(i -> i.like("t_certificate_manage.results", tCertificateManage.getResults()));
        }
        if (tCertificateManage.getDateOfIssue() != null) {
            queryWrapper.and(i -> i.like("t_certificate_manage.date_of_issue", tCertificateManage.getDateOfIssue()));
        }
        if (tCertificateManage.getTermOfValidity() != null) {
            queryWrapper.and(i -> i.like("t_certificate_manage.term_of_validity", tCertificateManage.getTermOfValidity()));
        }
        if (StringUtils.isNotBlank(tCertificateManage.getUnitOfIssue())) {
            queryWrapper.and(i -> i.like("t_certificate_manage.unit_of_issue", tCertificateManage.getUnitOfIssue()));
        }
        if (tCertificateManage.getIsShow() != null) {
            queryWrapper.and(i -> i.like("t_certificate_manage.is_show", tCertificateManage.getIsShow()));
        }
        if (tCertificateManage.getDelFlag() != null) {
            queryWrapper.and(i -> i.like("t_certificate_manage.del_flag", tCertificateManage.getDelFlag()));
        }
        if (StringUtils.isNotBlank(tCertificateManage.getCreateId())) {
            queryWrapper.and(i -> i.like("t_certificate_manage.create_id", tCertificateManage.getCreateId()));
        }
        if (tCertificateManage.getCreateTime() != null) {
            queryWrapper.and(i -> i.like("t_certificate_manage.create_time", tCertificateManage.getCreateTime()));
        }
        if (StringUtils.isNotBlank(tCertificateManage.getUpdateId())) {
            queryWrapper.and(i -> i.like("t_certificate_manage.update_id", tCertificateManage.getUpdateId()));
        }
        if (tCertificateManage.getUpdateTime() != null) {
            queryWrapper.and(i -> i.like("t_certificate_manage.update_time", tCertificateManage.getUpdateTime()));
        }
        if (StringUtils.isNotBlank(tCertificateManage.getDeleteId())) {
            queryWrapper.and(i -> i.like("t_certificate_manage.delete_id", tCertificateManage.getDeleteId()));
        }
        if (tCertificateManage.getDeleteTime() != null) {
            queryWrapper.and(i -> i.like("t_certificate_manage.delete_time", tCertificateManage.getDeleteTime()));
        }
        if (StringUtils.isNotBlank(tCertificateManage.getPersonId())) {
            queryWrapper.and(i -> i.like("t_certificate_manage.person_id", tCertificateManage.getPersonId()));
        }
        if (searchVo != null) {
            if (searchVo.getStartDate() != null && searchVo.getEndDate() != null) {
                queryWrapper.lambda().and(i -> i.between(TCertificateManage::getCreateTime, searchVo.getStartDate(), searchVo.getEndDate()));
            }
        }
        queryWrapper.lambda().and(i -> i.eq(TCertificateManage::getDelFlag, 0));
        return queryWrapper;

    }
}
