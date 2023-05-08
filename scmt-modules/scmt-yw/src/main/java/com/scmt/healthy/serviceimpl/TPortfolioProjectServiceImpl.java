package com.scmt.healthy.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.core.utis.FileUtil;
import com.scmt.healthy.entity.TBaseProject;
import com.scmt.healthy.entity.TPortfolioProject;
import com.scmt.healthy.mapper.TPortfolioProjectMapper;
import com.scmt.healthy.service.ITPortfolioProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author
 **/
@Service
public class TPortfolioProjectServiceImpl extends ServiceImpl<TPortfolioProjectMapper, TPortfolioProject> implements ITPortfolioProjectService {
    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private TPortfolioProjectMapper tPortfolioProjectMapper;

    @Override
    public IPage<TPortfolioProject> queryTPortfolioProjectListByPage(TPortfolioProject tPortfolioProject, SearchVo searchVo, PageVo pageVo) {
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
        Page<TPortfolioProject> pageData = new Page<>(page, limit);
        QueryWrapper<TPortfolioProject> queryWrapper = new QueryWrapper<>();
        if (tPortfolioProject != null) {
            if (StringUtils.isNotBlank(tPortfolioProject.getOfficeId())) {
                queryWrapper.and(i -> i.eq("office_id", tPortfolioProject.getOfficeId()));
            }
            if (StringUtils.isNotBlank(tPortfolioProject.getName())) {
                queryWrapper.and(i -> i.like("name", tPortfolioProject.getName()));
            }
            if (StringUtils.isNotBlank(tPortfolioProject.getShortName())) {
                queryWrapper.and(i -> i.like("short_name", tPortfolioProject.getShortName()));
            }
            queryWrapper.and(i -> i.isNull("parent_id").or().eq("parent_id", ""));
        }
        queryWrapper.eq("del_flag", 0);
        queryWrapper.orderByAsc("order_num");
        IPage<TPortfolioProject> result = tPortfolioProjectMapper.selectPage(pageData, queryWrapper);
//        if (result != null && result.getRecords() != null && result.getRecords().size() > 0) {
//            QueryWrapper<TPortfolioProject> wrapper = new QueryWrapper<>();
//            if (StringUtils.isNotBlank(tPortfolioProject.getOfficeId())) {
//                wrapper.and(i -> i.eq("office_id", tPortfolioProject.getOfficeId()));
//            }
//            List<TPortfolioProject> all = tPortfolioProjectMapper.selectList(wrapper);
//            for (TPortfolioProject dto : result.getRecords()) {
//                dto.setChildren(all.stream().filter(item -> item.getParentId().equals(dto.getId())).collect(Collectors.toList()));
//            }
//        }
        return result;
    }

    @Override
    public void download(TPortfolioProject tPortfolioProject, HttpServletResponse response) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        QueryWrapper<TPortfolioProject> queryWrapper = new QueryWrapper<>();
        if (tPortfolioProject != null) {
            queryWrapper = LikeAllFeild(tPortfolioProject, null);
        }
        List<TPortfolioProject> list = tPortfolioProjectMapper.selectList(queryWrapper);
        for (TPortfolioProject re : list) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("名称", re.getName());
            map.put("简称", re.getShortName());
            map.put("排序", re.getOrderNum());
            map.put("父节点ID", re.getParentId());
            map.put("原价（元）", re.getMarketPrice());
            map.put("销售价（元）", re.getSalePrice());
            map.put("成本价（元）", re.getCostPrice());
            map.put("适合人群", re.getSuitableRange());
            map.put("项目介绍", re.getIntroduce());
            map.put("检查地址", re.getAddress());
            map.put("备注", re.getRemark());
            map.put("诊断模板", re.getTemplate());
            map.put("服务类型", re.getServiceType());
            mapList.add(map);
        }
        FileUtil.createExcel(mapList, "exel.xlsx", response);
    }

    @Override
    public List<TPortfolioProject> queryTPortfolioProjectListByOfficeId(TPortfolioProject tPortfolioProject) {
        QueryWrapper<TPortfolioProject> queryWrapper = new QueryWrapper<>();
        if (tPortfolioProject != null) {
            queryWrapper = LikeAllFeild(tPortfolioProject, null);
        }
        List<TPortfolioProject> list = tPortfolioProjectMapper.selectList(queryWrapper);
        return list;
    }

    @Override
    public List<TBaseProject> getBaseProjectByPortfolioProject(String portfolioProjectId) {
        return tPortfolioProjectMapper.getBaseProjectByPortfolioProject(portfolioProjectId);
    }
    @Override
    public List<TPortfolioProject> queryPortfolioProjectList(TPortfolioProject tPortfolioProject) {
        QueryWrapper<TPortfolioProject> queryWrapper = new QueryWrapper<>();
        if (tPortfolioProject != null) {
            queryWrapper = LikeAllFeild(tPortfolioProject, null);
        }
        List<TPortfolioProject> list = tPortfolioProjectMapper.selectList(queryWrapper);
        return list;
    }

    @Override
    public List<TPortfolioProject> getProjectData(String id) {
        QueryWrapper<TPortfolioProject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("t_combo.id",id);
        List<TPortfolioProject> projectData = tPortfolioProjectMapper.getProjectData(queryWrapper);
        return projectData;
    }

    /**
     * 功能描述：构建模糊查询
     *
     * @param tPortfolioProject 需要模糊查询的信息
     * @return 返回查询
     */
    public QueryWrapper<TPortfolioProject> LikeAllFeild(TPortfolioProject tPortfolioProject, SearchVo searchVo) {
        QueryWrapper<TPortfolioProject> queryWrapper = new QueryWrapper<>();
        if (tPortfolioProject.getId() != null) {
            queryWrapper.lambda().and(i -> i.eq(TPortfolioProject::getId, tPortfolioProject.getId()));
        }
        if (StringUtils.isNotBlank(tPortfolioProject.getName())) {
            queryWrapper.lambda().and(i -> i.like(TPortfolioProject::getName, tPortfolioProject.getName()));
        }
        if (StringUtils.isNotBlank(tPortfolioProject.getShortName())) {
            queryWrapper.lambda().and(i -> i.like(TPortfolioProject::getShortName, tPortfolioProject.getShortName()));
        }

        if (StringUtils.isNotBlank(tPortfolioProject.getParentId())) {
            queryWrapper.lambda().and(i -> i.eq(TPortfolioProject::getParentId, tPortfolioProject.getParentId()));
        }
        if (StringUtils.isNotBlank(tPortfolioProject.getOfficeId())) {
            queryWrapper.lambda().and(i -> i.eq(TPortfolioProject::getOfficeId, tPortfolioProject.getOfficeId()));
        }

        if (StringUtils.isNotBlank(tPortfolioProject.getSuitableRange())) {
            queryWrapper.lambda().and(i -> i.like(TPortfolioProject::getSuitableRange, tPortfolioProject.getSuitableRange()));
        }
        if (StringUtils.isNotBlank(tPortfolioProject.getIntroduce())) {
            queryWrapper.lambda().and(i -> i.like(TPortfolioProject::getIntroduce, tPortfolioProject.getIntroduce()));
        }

        if (StringUtils.isNotBlank(tPortfolioProject.getAddress())) {
            queryWrapper.lambda().and(i -> i.like(TPortfolioProject::getAddress, tPortfolioProject.getAddress()));
        }
        if (StringUtils.isNotBlank(tPortfolioProject.getRemark())) {
            queryWrapper.lambda().and(i -> i.like(TPortfolioProject::getRemark, tPortfolioProject.getRemark()));
        }
        if (StringUtils.isNotBlank(tPortfolioProject.getCreateId())) {
            queryWrapper.lambda().and(i -> i.eq(TPortfolioProject::getCreateId, tPortfolioProject.getCreateId()));
        }

        if (tPortfolioProject.getDepartmentId() != null) {
            queryWrapper.lambda().and(i -> i.eq(TPortfolioProject::getDepartmentId, tPortfolioProject.getDepartmentId()));
        }
        if (StringUtils.isNotBlank(tPortfolioProject.getTemplate())) {
            queryWrapper.lambda().and(i -> i.eq(TPortfolioProject::getTemplate, tPortfolioProject.getTemplate()));
        }
        if (StringUtils.isNotBlank(tPortfolioProject.getServiceType())) {
            queryWrapper.lambda().and(i -> i.eq(TPortfolioProject::getServiceType, tPortfolioProject.getServiceType()));
        }
        if (searchVo != null) {
            if (searchVo.getStartDate() != null && searchVo.getEndDate() != null) {
                queryWrapper.lambda().and(i -> i.between(TPortfolioProject::getCreateTime, searchVo.getStartDate(), searchVo.getEndDate()));
            }
        }
        queryWrapper.lambda().and(i -> i.eq(TPortfolioProject::getDelFlag, 0));
        queryWrapper.orderByAsc("order_num");
        return queryWrapper;

    }
    @Override
    public List<TPortfolioProject> getProjectList(List<Object> id) {
        QueryWrapper<TPortfolioProject> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("t_combo.id",id);
        List<TPortfolioProject> projectData = tPortfolioProjectMapper.getProjectData(queryWrapper);
        return projectData;
    }
}
