package com.scmt.healthy.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.scmt.healthy.entity.TDiseaseDiagnosis;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author dengjie
 * @since 2023-03-09
 */
public interface TDiseaseDiagnosisMapper extends BaseMapper<TDiseaseDiagnosis> {

    List<TDiseaseDiagnosis> getTDiseaseDiagnosisList(@Param(Constants.WRAPPER) QueryWrapper<TDiseaseDiagnosis> queryWrapper);
}
