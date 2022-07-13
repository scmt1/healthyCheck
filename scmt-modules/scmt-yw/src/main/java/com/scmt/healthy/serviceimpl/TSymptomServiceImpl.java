package com.scmt.healthy.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.scmt.healthy.entity.TSymptom;
import com.scmt.healthy.service.ITSymptomService;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.mapper.TSymptomMapper;
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
public class TSymptomServiceImpl extends ServiceImpl<TSymptomMapper, TSymptom> implements ITSymptomService {
    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private TSymptomMapper tSymptomMapper;

    @Override
    public IPage<TSymptom> queryTSymptomListByPage(TSymptom tSymptom, SearchVo searchVo, PageVo pageVo) {
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
        Page<TSymptom> pageData = new Page<>(page, limit);
        QueryWrapper<TSymptom> queryWrapper = new QueryWrapper<>();
        if (tSymptom != null) {
            queryWrapper = LikeAllField(tSymptom, searchVo);
        }
        IPage<TSymptom> result = tSymptomMapper.selectPage(pageData, queryWrapper);
        return result;
    }
    @Override
    public List<TSymptom> queryTSymptomAll(TSymptom tSymptom, SearchVo searchVo) {
        QueryWrapper<TSymptom> queryWrapper = new QueryWrapper<>();
        if (tSymptom != null) {
            queryWrapper = LikeAllField(tSymptom, searchVo);
        }
        List<TSymptom> result = tSymptomMapper.selectList(queryWrapper);
        return result;
    }

    @Override
    public void download(TSymptom tSymptom, HttpServletResponse response) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        QueryWrapper<TSymptom> queryWrapper = new QueryWrapper<>();
        if (tSymptom != null) {
            queryWrapper = LikeAllField(tSymptom, null);
        }
        List<TSymptom> list = tSymptomMapper.selectList(queryWrapper);
        for (TSymptom re : list) {
            Map<String, Object> map = new LinkedHashMap<>();
            mapList.add(map);
        }
        FileUtil.createExcel(mapList, "exel.xlsx", response);
    }

    /**
     * 功能描述：构建模糊查询
     *
     * @param tSymptom 需要模糊查询的信息
     * @return 返回查询
     */
    public QueryWrapper<TSymptom> LikeAllField(TSymptom tSymptom, SearchVo searchVo) {
        QueryWrapper<TSymptom> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(tSymptom.getId())) {
            queryWrapper.and(i -> i.eq("t_symptom.id", tSymptom.getId()));
        }
        if (StringUtils.isNotBlank(tSymptom.getType())) {
            queryWrapper.and(i -> i.like("t_symptom.type", tSymptom.getType()));
        }
        if (StringUtils.isNotBlank(tSymptom.getProjectName())) {
            queryWrapper.and(i -> i.like("t_symptom.project_name", tSymptom.getProjectName()));
        }
        if (StringUtils.isNotBlank(tSymptom.getDegree())) {
            queryWrapper.and(i -> i.like("t_symptom.degree", tSymptom.getDegree()));
        }
        if (StringUtils.isNotBlank(tSymptom.getPersonId())) {
            queryWrapper.and(i -> i.eq("t_symptom.person_id", tSymptom.getPersonId()));
        }
        if (StringUtils.isNotBlank(tSymptom.getCreateId())) {
            queryWrapper.and(i -> i.like("t_symptom.create_id", tSymptom.getCreateId()));
        }
        if (tSymptom.getCreateTime() != null) {
            queryWrapper.and(i -> i.like("t_symptom.create_time", tSymptom.getCreateTime()));
        }
        if (searchVo != null) {
            if (searchVo.getStartDate() != null && searchVo.getEndDate() != null) {
                queryWrapper.lambda().and(i -> i.between(TSymptom::getCreateTime, searchVo.getStartDate(), searchVo.getEndDate()));
            }
        }
        return queryWrapper;

    }
}