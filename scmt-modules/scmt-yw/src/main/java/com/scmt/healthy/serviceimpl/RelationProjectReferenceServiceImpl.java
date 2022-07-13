package com.scmt.healthy.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scmt.core.common.utils.ResultUtil;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.Result;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.core.utis.FileUtil;
import com.scmt.healthy.entity.RelationProjectReference;
import com.scmt.healthy.mapper.RelationProjectReferenceMapper;
import com.scmt.healthy.service.IRelationProjectReferenceService;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.util.StringUtil;
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
public class RelationProjectReferenceServiceImpl extends ServiceImpl<RelationProjectReferenceMapper, RelationProjectReference> implements IRelationProjectReferenceService {
    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private RelationProjectReferenceMapper relationProjectReferenceMapper;

    @Override
    public Result<Object> queryRelationProjectReferenceListByPage(RelationProjectReference relationProjectReference, SearchVo searchVo, PageVo pageVo) {
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
        Page<RelationProjectReference> pageData = new Page<>(page, limit);
        QueryWrapper<RelationProjectReference> queryWrapper = new QueryWrapper<>();
        if (relationProjectReference != null) {
            queryWrapper = LikeAllFeild(relationProjectReference, searchVo);
        }
        if (pageVo.getSort() != null) {
            if (pageVo.getSort().equals("asc")) {
                queryWrapper.orderByAsc("relation_project_reference." + pageVo.getSort());
            } else {
                queryWrapper.orderByDesc("relation_project_reference." + pageVo.getSort());
            }
        } else {
            queryWrapper.orderByDesc("relation_project_reference.create_time");
        }
        IPage<RelationProjectReference> result = relationProjectReferenceMapper.selectPage(pageData, queryWrapper);
        return ResultUtil.data(result);
    }

    @Override
    public void download(RelationProjectReference relationProjectReference, HttpServletResponse response) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        QueryWrapper<RelationProjectReference> queryWrapper = new QueryWrapper<>();
        if (relationProjectReference != null) {
            queryWrapper = LikeAllFeild(relationProjectReference, null);
        }
        List<RelationProjectReference> list = relationProjectReferenceMapper.selectList(queryWrapper);
        for (RelationProjectReference re : list) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("适合性别", re.getAllowSex());
            map.put("年龄最低值", re.getMinAge());
            map.put("年龄最高值", re.getMaxAge());
            map.put("健康参考值", re.getHealthyValue());
            map.put("职业参考值", re.getOccupationValue());
            mapList.add(map);
        }
        FileUtil.createExcel(mapList, "exel.xlsx", response);
    }

    @Override
    public List<RelationProjectReference> queryRelationProjectReferenceList(RelationProjectReference relationProjectReference) {
        QueryWrapper<RelationProjectReference> queryWrapper = new QueryWrapper<>();
        if (relationProjectReference != null) {
            queryWrapper = LikeAllFeild(relationProjectReference, null);
        }
        return relationProjectReferenceMapper.selectList(queryWrapper);
    }

    /**
     * 功能描述：构建模糊查询
     *
     * @param relationProjectReference 需要模糊查询的信息
     * @return 返回查询
     */
    public QueryWrapper<RelationProjectReference> LikeAllFeild(RelationProjectReference relationProjectReference, SearchVo searchVo) {
        QueryWrapper<RelationProjectReference> queryWrapper = new QueryWrapper<>();
        if (relationProjectReference.getId() != null) {
            queryWrapper.and(i -> i.like("relation_project_reference.id", relationProjectReference.getId()));
        }
        if (StringUtils.isNotBlank(relationProjectReference.getBaseProjectId())) {
            queryWrapper.and(i -> i.like("relation_project_reference.base_project_id", relationProjectReference.getBaseProjectId()));
        }
        if (StringUtils.isNotBlank(relationProjectReference.getAllowSex())) {
            queryWrapper.and(i -> i.like("relation_project_reference.allow_sex", relationProjectReference.getAllowSex()));
        }
        if (StringUtils.isNotBlank(relationProjectReference.getHealthyValue())) {
            queryWrapper.and(i -> i.like("relation_project_reference.healthy_value", relationProjectReference.getHealthyValue()));
        }
        if (StringUtils.isNotBlank(relationProjectReference.getOccupationValue())) {
            queryWrapper.and(i -> i.like("relation_project_reference.occupation_value", relationProjectReference.getOccupationValue()));
        }
        if (relationProjectReference.getDepartmentId() != null) {
            queryWrapper.and(i -> i.like("relation_project_reference.department_id", relationProjectReference.getDepartmentId()));
        }
        if (searchVo != null) {
            if (StringUtils.isNotBlank(searchVo.getStartDate()) && StringUtils.isNotBlank(searchVo.getEndDate())) {
                queryWrapper.and(i -> i.between("relation_project_reference.create_time", searchVo.getStartDate(), searchVo.getEndDate()));
            }
        }
        return queryWrapper;

    }
}
