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
import com.scmt.healthy.entity.RelationProjectCritical;
import com.scmt.healthy.mapper.RelationProjectCriticalMapper;
import com.scmt.healthy.service.IRelationProjectCriticalService;

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
public class RelationProjectCriticalServiceImpl extends ServiceImpl<RelationProjectCriticalMapper, RelationProjectCritical> implements IRelationProjectCriticalService {
    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private RelationProjectCriticalMapper relationProjectCriticalMapper;

    @Override
    public Result<Object> queryRelationProjectCriticalListByPage(RelationProjectCritical relationProjectCritical, SearchVo searchVo, PageVo pageVo) {
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
        Page<RelationProjectCritical> pageData = new Page<>(page, limit);
        QueryWrapper<RelationProjectCritical> queryWrapper = new QueryWrapper<>();
        if (relationProjectCritical != null) {
            queryWrapper = LikeAllFeild(relationProjectCritical, searchVo);
        }
        if (pageVo.getSort() != null) {
            if (pageVo.getSort().equals("asc")) {
                queryWrapper.orderByAsc("relation_project_critical." + pageVo.getSort());
            } else {
                queryWrapper.orderByDesc("relation_project_critical." + pageVo.getSort());
            }
        } else {
            queryWrapper.orderByDesc("relation_project_critical.create_time");
        }
        IPage<RelationProjectCritical> result = relationProjectCriticalMapper.selectPage(pageData, queryWrapper);
        return ResultUtil.data(result);
    }

    @Override
    public void download(RelationProjectCritical relationProjectCritical, HttpServletResponse response) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        QueryWrapper<RelationProjectCritical> queryWrapper = new QueryWrapper<>();
        if (relationProjectCritical != null) {
            queryWrapper = LikeAllFeild(relationProjectCritical, null);
        }
        List<RelationProjectCritical> list = relationProjectCriticalMapper.selectList(queryWrapper);
        for (RelationProjectCritical re : list) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("等级", re.getLevel());
            map.put("类型", re.getType());
            map.put("区间值", re.getIntervalValue());
            map.put("适合性别", re.getAllowSex());
            map.put("年龄最低值", re.getMinAge());
            map.put("年龄最高值", re.getMaxAge());
            mapList.add(map);
        }
        FileUtil.createExcel(mapList, "exel.xlsx", response);
    }

    @Override
    public List<RelationProjectCritical> queryRelationProjectCriticalList(RelationProjectCritical relationProjectCritical) {
        QueryWrapper<RelationProjectCritical> queryWrapper = new QueryWrapper<>();
        if (relationProjectCritical != null) {
            queryWrapper = LikeAllFeild(relationProjectCritical, null);
        }
        List<RelationProjectCritical> list = relationProjectCriticalMapper.selectList(queryWrapper);
        return list;
    }

    /**
     * 功能描述：构建模糊查询
     *
     * @param relationProjectCritical 需要模糊查询的信息
     * @return 返回查询
     */
    public QueryWrapper<RelationProjectCritical> LikeAllFeild(RelationProjectCritical relationProjectCritical, SearchVo searchVo) {
        QueryWrapper<RelationProjectCritical> queryWrapper = new QueryWrapper<>();
        if (relationProjectCritical.getId() != null) {
            queryWrapper.and(i -> i.like("relation_project_critical.id", relationProjectCritical.getId()));
        }
        if (StringUtils.isNotBlank(relationProjectCritical.getBaseProjectId())) {
            queryWrapper.and(i -> i.like("relation_project_critical.base_project_id", relationProjectCritical.getBaseProjectId()));
        }
        if (StringUtils.isNotBlank(relationProjectCritical.getLevel())) {
            queryWrapper.and(i -> i.like("relation_project_critical.level", relationProjectCritical.getLevel()));
        }
        if (StringUtils.isNotBlank(relationProjectCritical.getType())) {
            queryWrapper.and(i -> i.like("relation_project_critical.type", relationProjectCritical.getType()));
        }
        if (StringUtils.isNotBlank(relationProjectCritical.getIntervalValue())) {
            queryWrapper.and(i -> i.like("relation_project_critical.interval_value", relationProjectCritical.getIntervalValue()));
        }
        if (StringUtils.isNotBlank(relationProjectCritical.getAllowSex())) {
            queryWrapper.and(i -> i.like("relation_project_critical.allow_sex", relationProjectCritical.getAllowSex()));
        }
        if (relationProjectCritical.getDepartmentId() != null) {
            queryWrapper.and(i -> i.like("relation_project_critical.department_id", relationProjectCritical.getDepartmentId()));
        }
        if (searchVo != null) {
            if (StringUtils.isNotBlank(searchVo.getStartDate()) && StringUtils.isNotBlank(searchVo.getEndDate())) {
                queryWrapper.and(i -> i.between("relation_project_critical.create_time", searchVo.getStartDate(), searchVo.getEndDate()));
            }
        }
        return queryWrapper;
    }
}
