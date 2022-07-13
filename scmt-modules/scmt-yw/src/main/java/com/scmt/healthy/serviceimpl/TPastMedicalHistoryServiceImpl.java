package com.scmt.healthy.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.scmt.healthy.entity.TPastMedicalHistory;
import com.scmt.healthy.service.ITPastMedicalHistoryService;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.mapper.TPastMedicalHistoryMapper;
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
public class TPastMedicalHistoryServiceImpl extends ServiceImpl<TPastMedicalHistoryMapper, TPastMedicalHistory> implements ITPastMedicalHistoryService {
    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private TPastMedicalHistoryMapper tPastMedicalHistoryMapper;

    @Override
    public IPage<TPastMedicalHistory> queryTPastMedicalHistoryListByPage(TPastMedicalHistory tPastMedicalHistory, SearchVo searchVo, PageVo pageVo) {
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
        Page<TPastMedicalHistory> pageData = new Page<>(page, limit);
        QueryWrapper<TPastMedicalHistory> queryWrapper = new QueryWrapper<>();
        if (tPastMedicalHistory != null) {
            queryWrapper = LikeAllField(tPastMedicalHistory, searchVo);
        }
        IPage<TPastMedicalHistory> result = tPastMedicalHistoryMapper.selectPage(pageData, queryWrapper);
        return result;
    }
    @Override
    public List<TPastMedicalHistory> queryTPastMedicalHistoryAll(TPastMedicalHistory tPastMedicalHistory, SearchVo searchVo) {
        QueryWrapper<TPastMedicalHistory> queryWrapper = new QueryWrapper<>();
        if (tPastMedicalHistory != null) {
            queryWrapper = LikeAllField(tPastMedicalHistory, searchVo);
        }
        List<TPastMedicalHistory> result = tPastMedicalHistoryMapper.selectList(queryWrapper);
        return result;
    }

    @Override
    public void download(TPastMedicalHistory tPastMedicalHistory, HttpServletResponse response) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        QueryWrapper<TPastMedicalHistory> queryWrapper = new QueryWrapper<>();
        if (tPastMedicalHistory != null) {
            queryWrapper = LikeAllField(tPastMedicalHistory, null);
        }
        List<TPastMedicalHistory> list = tPastMedicalHistoryMapper.selectList(queryWrapper);
        for (TPastMedicalHistory re : list) {
            Map<String, Object> map = new LinkedHashMap<>();
            mapList.add(map);
        }
        FileUtil.createExcel(mapList, "exel.xlsx", response);
    }

    /**
     * 功能描述：构建模糊查询
     *
     * @param tPastMedicalHistory 需要模糊查询的信息
     * @return 返回查询
     */
    public QueryWrapper<TPastMedicalHistory> LikeAllField(TPastMedicalHistory tPastMedicalHistory, SearchVo searchVo) {
        QueryWrapper<TPastMedicalHistory> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(tPastMedicalHistory.getId())) {
            queryWrapper.and(i -> i.like("t_past_medical_history.id", tPastMedicalHistory.getId()));
        }
        if (StringUtils.isNotBlank(tPastMedicalHistory.getDiseaseName())) {
            queryWrapper.and(i -> i.like("t_past_medical_history.disease_name", tPastMedicalHistory.getDiseaseName()));
        }
        if (tPastMedicalHistory.getDiseaseDate() != null) {
            queryWrapper.and(i -> i.like("t_past_medical_history.disease_date", tPastMedicalHistory.getDiseaseDate()));
        }
        if (StringUtils.isNotBlank(tPastMedicalHistory.getDiagnosticUnit())) {
            queryWrapper.and(i -> i.like("t_past_medical_history.diagnostic_unit", tPastMedicalHistory.getDiagnosticUnit()));
        }
        if (StringUtils.isNotBlank(tPastMedicalHistory.getAfterTreatment())) {
            queryWrapper.and(i -> i.like("t_past_medical_history.after_treatment", tPastMedicalHistory.getAfterTreatment()));
        }
        if (StringUtils.isNotBlank(tPastMedicalHistory.getFate())) {
            queryWrapper.and(i -> i.like("t_past_medical_history.fate", tPastMedicalHistory.getFate()));
        }
        if (StringUtils.isNotBlank(tPastMedicalHistory.getPersonId())) {
            queryWrapper.and(i -> i.like("t_past_medical_history.person_id", tPastMedicalHistory.getPersonId()));
        }
        if (StringUtils.isNotBlank(tPastMedicalHistory.getCreateId())) {
            queryWrapper.and(i -> i.like("t_past_medical_history.create_id", tPastMedicalHistory.getCreateId()));
        }
        if (tPastMedicalHistory.getCreateTime() != null) {
            queryWrapper.and(i -> i.like("t_past_medical_history.create_time", tPastMedicalHistory.getCreateTime()));
        }
        if (searchVo != null) {
            if (searchVo.getStartDate() != null && searchVo.getEndDate() != null) {
                queryWrapper.lambda().and(i -> i.between(TPastMedicalHistory::getCreateTime, searchVo.getStartDate(), searchVo.getEndDate()));
            }
        }
        return queryWrapper;

    }
}