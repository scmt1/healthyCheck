package com.scmt.healthy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scmt.healthy.entity.TBaseProject;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Mike
 * @since 2021-09-29
 */
public interface TBaseProjectMapper extends BaseMapper<TBaseProject> {
    /**
     * 查询项目
     * @param officeId
     * @return
     */
    List<TBaseProject> queryAllTBaseProject(@Param("officeId") String officeId);
}
