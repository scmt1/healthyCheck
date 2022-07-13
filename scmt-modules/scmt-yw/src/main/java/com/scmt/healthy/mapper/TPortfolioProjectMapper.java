package com.scmt.healthy.mapper;

import com.scmt.healthy.entity.TBaseProject;
import com.scmt.healthy.entity.TPortfolioProject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author mike
 * @since 2021-09-30
 */
public interface TPortfolioProjectMapper extends BaseMapper<TPortfolioProject> {

    List<TBaseProject> getBaseProjectByPortfolioProject(String portfolioProjectId);

    /**
     * 根据id查询组合项目
     * @param id
     * @return
     */
    TPortfolioProject selectTSectionOfficeById(@Param(value = "id")String id );
}
