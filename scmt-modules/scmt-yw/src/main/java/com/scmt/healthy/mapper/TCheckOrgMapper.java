package com.scmt.healthy.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scmt.healthy.entity.TCheckOrg;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Administrator
 * @since 2023-03-31
 */
@Repository
public interface TCheckOrgMapper extends BaseMapper<TCheckOrg> {

    /**
     * 联表分页查询
     * @param queryWrapper
     * @param page
     * @return
     */
    IPage<TCheckOrg> selectOrgAndCombo(@Param(Constants.WRAPPER) QueryWrapper<TCheckOrg> queryWrapper, @Param("page") Page page);

    /**
     * 联表分页查询
     * @param queryWrapper
     * @return
     */
    TCheckOrg selectOrgAndCombo(@Param(Constants.WRAPPER) QueryWrapper<TCheckOrg> queryWrapper);

}
