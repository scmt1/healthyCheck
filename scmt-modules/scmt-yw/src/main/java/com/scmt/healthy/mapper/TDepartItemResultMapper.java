package com.scmt.healthy.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scmt.healthy.entity.TDepartItemResult;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author mike
 * @since 2021-10-22
 */
public interface TDepartItemResultMapper extends BaseMapper<TDepartItemResult> {

    List<TDepartItemResult> queryAllTDepartItemResultList(@Param("personId") String personId,@Param("officeId") String officeId,@Param("checkDate") String checkDate,@Param("departResultId") String departResultId);

    IPage<TDepartItemResult> querySummaryResultList(@Param(Constants.WRAPPER) QueryWrapper<TDepartItemResult> queryWrapper, @Param("page") Page page);

    List<TDepartItemResult> queryAllAbnormalItemResultList(@Param(Constants.WRAPPER) QueryWrapper<TDepartItemResult> queryWrapper);

    List<TDepartItemResult> getAbnormalResultList(@Param("personId") String personId);

    List<TDepartItemResult> getAllListByPersonId(@Param("personId") String personId);
}
