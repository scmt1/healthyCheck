package com.scmt.healthy.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.scmt.healthy.entity.RelationPersonProjectCheck;
import com.scmt.healthy.entity.TOrderGroupItem;
import com.scmt.healthy.service.IRelationPersonProjectCheckService;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.mapper.RelationPersonProjectCheckMapper;
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
public class RelationPersonProjectCheckServiceImpl extends ServiceImpl<RelationPersonProjectCheckMapper, RelationPersonProjectCheck> implements IRelationPersonProjectCheckService {
    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private RelationPersonProjectCheckMapper relationPersonProjectCheckMapper;

    @Override
    public IPage<RelationPersonProjectCheck> queryRelationPersonProjectCheckListByPage(RelationPersonProjectCheck relationPersonProjectCheck, SearchVo searchVo, PageVo pageVo) {
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
        Page<RelationPersonProjectCheck> pageData = new Page<>(page, limit);
        QueryWrapper<RelationPersonProjectCheck> queryWrapper = new QueryWrapper<>();
        if (relationPersonProjectCheck != null) {
            queryWrapper = LikeAllField(relationPersonProjectCheck, searchVo);
        }
        IPage<RelationPersonProjectCheck> result = relationPersonProjectCheckMapper.selectPage(pageData, queryWrapper);
        return result;
    }

    @Override
    public List<RelationPersonProjectCheck> queryRelationPersonProjectCheckListAll(RelationPersonProjectCheck relationPersonProjectCheck, SearchVo searchVo) {
        QueryWrapper<RelationPersonProjectCheck> queryWrapper = new QueryWrapper<>();
        if (relationPersonProjectCheck != null) {
            queryWrapper = LikeAllField(relationPersonProjectCheck, searchVo);
        }
        List<RelationPersonProjectCheck> result = relationPersonProjectCheckMapper.selectList(queryWrapper);
        return result;
    }

    @Override
    public void download(RelationPersonProjectCheck relationPersonProjectCheck, HttpServletResponse response) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        QueryWrapper<RelationPersonProjectCheck> queryWrapper = new QueryWrapper<>();
        if (relationPersonProjectCheck != null) {
            queryWrapper = LikeAllField(relationPersonProjectCheck, null);
        }
        List<RelationPersonProjectCheck> list = relationPersonProjectCheckMapper.selectList(queryWrapper);
        for (RelationPersonProjectCheck re : list) {
            Map<String, Object> map = new LinkedHashMap<>();
            mapList.add(map);
        }
        FileUtil.createExcel(mapList, "exel.xlsx", response);
    }

    @Override
    public List<TOrderGroupItem> getNoRegistProjectData(String personId, List<String> deparmentIds) {
        return relationPersonProjectCheckMapper.getNoRegistProjectData(personId, deparmentIds);
    }

    @Override
    public List<TOrderGroupItem> getNoRegistProjectDataReview(String personId, List<String> deparmentIds) {
        return relationPersonProjectCheckMapper.getNoRegistProjectDataReview(personId, deparmentIds);
    }

    /**
     * 功能描述：构建模糊查询
     *
     * @param relationPersonProjectCheck 需要模糊查询的信息
     * @return 返回查询
     */
    public QueryWrapper<RelationPersonProjectCheck> LikeAllField(RelationPersonProjectCheck relationPersonProjectCheck, SearchVo searchVo) {
        QueryWrapper<RelationPersonProjectCheck> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(relationPersonProjectCheck.getId())) {
            queryWrapper.and(i -> i.like("relation_person_project_check.id", relationPersonProjectCheck.getId()));
        }
        if (StringUtils.isNotBlank(relationPersonProjectCheck.getPersonId())) {
            queryWrapper.and(i -> i.like("relation_person_project_check.person_id", relationPersonProjectCheck.getPersonId()));
        }
        if (StringUtils.isNotBlank(relationPersonProjectCheck.getOfficeId())) {
            queryWrapper.and(i -> i.like("relation_person_project_check.office_id", relationPersonProjectCheck.getOfficeId()));
        }
        if (relationPersonProjectCheck.getState() != null) {
            queryWrapper.and(i -> i.like("relation_person_project_check.state", relationPersonProjectCheck.getState()));
        }
        return queryWrapper;
    }
}