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
import com.scmt.healthy.entity.RelationProjectRules;
import com.scmt.healthy.mapper.RelationProjectRulesMapper;
import com.scmt.healthy.service.IRelationProjectRulesService;

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
public class RelationProjectRulesServiceImpl extends ServiceImpl<RelationProjectRulesMapper, RelationProjectRules> implements IRelationProjectRulesService {
    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private RelationProjectRulesMapper relationProjectRulesMapper;

    @Override
    public Result<Object> queryRelationProjectRulesListByPage(RelationProjectRules relationProjectRules, SearchVo searchVo, PageVo pageVo) {
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
        Page<RelationProjectRules> pageData = new Page<>(page, limit);
        QueryWrapper<RelationProjectRules> queryWrapper = new QueryWrapper<>();
        if (relationProjectRules != null) {
            queryWrapper = LikeAllFeild(relationProjectRules, searchVo);
        }
        if (pageVo.getSort() != null) {
            if (pageVo.getSort().equals("asc")) {
                queryWrapper.orderByAsc("relation_project_rules." + pageVo.getSort());
            } else {
                queryWrapper.orderByDesc("relation_project_rules." + pageVo.getSort());
            }
        } else {
            queryWrapper.orderByDesc("relation_project_rules.create_time");
        }
        IPage<RelationProjectRules> result = relationProjectRulesMapper.selectPage(pageData, queryWrapper);
        return ResultUtil.data(result);
    }

    @Override
    public void download(RelationProjectRules relationProjectRules, HttpServletResponse response) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        QueryWrapper<RelationProjectRules> queryWrapper = new QueryWrapper<>();
        if (relationProjectRules != null) {
            queryWrapper = LikeAllFeild(relationProjectRules, null);
        }
        List<RelationProjectRules> list = relationProjectRulesMapper.selectList(queryWrapper);
        for (RelationProjectRules re : list) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("名称", re.getName());
            map.put("简称", re.getShortName());
            map.put("类型", re.getType());
            map.put("区间值", re.getIntervalValue());
            map.put("适合性别", re.getAllowSex());
            map.put("年龄最低值", re.getMinAge());
            map.put("年龄最高值", re.getMaxAge());
            map.put("是否进入小结", re.getInConclusion());
            map.put("是否阳性", re.getPositive());
            map.put("职业建议", re.getCareerAdvice());
            map.put("健康建议", re.getHealthAdvice());
            map.put("饮食指导", re.getDietaryGuidance());
            map.put("运动指导", re.getSportsGuidance());
            map.put("健康知识", re.getHealthKnowledge());
            map.put("温馨提示", re.getReminder());
            mapList.add(map);
        }
        FileUtil.createExcel(mapList, "exel.xlsx", response);
    }

    /**
     * 功能描述：构建模糊查询
     *
     * @param relationProjectRules 需要模糊查询的信息
     * @return 返回查询
     */
    public QueryWrapper<RelationProjectRules> LikeAllFeild(RelationProjectRules relationProjectRules, SearchVo searchVo) {
        QueryWrapper<RelationProjectRules> queryWrapper = new QueryWrapper<>();
        if (relationProjectRules.getId() != null) {
            queryWrapper.and(i -> i.like("relation_project_rules.id", relationProjectRules.getId()));
        }
        if (StringUtils.isNotBlank(relationProjectRules.getName())) {
            queryWrapper.and(i -> i.like("relation_project_rules.name", relationProjectRules.getName()));
        }
        if (StringUtils.isNotBlank(relationProjectRules.getShortName())) {
            queryWrapper.and(i -> i.like("relation_project_rules.short_name", relationProjectRules.getShortName()));
        }
        if (StringUtils.isNotBlank(relationProjectRules.getType())) {
            queryWrapper.and(i -> i.like("relation_project_rules.type", relationProjectRules.getType()));
        }
        if (StringUtils.isNotBlank(relationProjectRules.getIntervalValue())) {
            queryWrapper.and(i -> i.like("relation_project_rules.interval_value", relationProjectRules.getIntervalValue()));
        }
        if (StringUtils.isNotBlank(relationProjectRules.getAllowSex())) {
            queryWrapper.and(i -> i.like("relation_project_rules.allow_sex", relationProjectRules.getAllowSex()));
        }
        if (StringUtils.isNotBlank(relationProjectRules.getInConclusion())) {
            queryWrapper.and(i -> i.like("relation_project_rules.in_conclusion", relationProjectRules.getInConclusion()));
        }
        if (StringUtils.isNotBlank(relationProjectRules.getPositive())) {
            queryWrapper.and(i -> i.like("relation_project_rules.positive", relationProjectRules.getPositive()));
        }
        if (StringUtils.isNotBlank(relationProjectRules.getCareerAdvice())) {
            queryWrapper.and(i -> i.like("relation_project_rules.career_advice", relationProjectRules.getCareerAdvice()));
        }
        if (StringUtils.isNotBlank(relationProjectRules.getHealthAdvice())) {
            queryWrapper.and(i -> i.like("relation_project_rules.health_advice", relationProjectRules.getHealthAdvice()));
        }
        if (StringUtils.isNotBlank(relationProjectRules.getDietaryGuidance())) {
            queryWrapper.and(i -> i.like("relation_project_rules.dietary_guidance", relationProjectRules.getDietaryGuidance()));
        }
        if (StringUtils.isNotBlank(relationProjectRules.getSportsGuidance())) {
            queryWrapper.and(i -> i.like("relation_project_rules.sports_guidance", relationProjectRules.getSportsGuidance()));
        }
        if (StringUtils.isNotBlank(relationProjectRules.getHealthKnowledge())) {
            queryWrapper.and(i -> i.like("relation_project_rules.health_knowledge", relationProjectRules.getHealthKnowledge()));
        }
        if (StringUtils.isNotBlank(relationProjectRules.getReminder())) {
            queryWrapper.and(i -> i.like("relation_project_rules.reminder", relationProjectRules.getReminder()));
        }
        if (StringUtils.isNotBlank(relationProjectRules.getDepartmentId())) {
            queryWrapper.and(i -> i.like("relation_project_rules.department_id", relationProjectRules.getDepartmentId()));
        }
        if (StringUtils.isNotBlank(relationProjectRules.getBaseProjectId())) {
            queryWrapper.and(i -> i.like("relation_project_rules.base_project_id", relationProjectRules.getBaseProjectId()));
        }
        if (searchVo != null) {
            if (StringUtils.isNotBlank(searchVo.getStartDate()) && StringUtils.isNotBlank(searchVo.getEndDate())) {
                queryWrapper.and(i -> i.between("relation_project_rules.create_time", searchVo.getStartDate(), searchVo.getEndDate()));
            }
        }
        return queryWrapper;
    }
}
