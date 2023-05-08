package com.scmt.healthy.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.scmt.healthy.entity.TPastMedicalHistory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 既往病史 Mapper 接口
 * </p>
 *
 * @author dengjie
 * @since 2021-11-02
 */
public interface TPastMedicalHistoryMapper extends BaseMapper<TPastMedicalHistory> {

    List<TPastMedicalHistory> getByPersonIdList(@Param(Constants.WRAPPER) QueryWrapper<TPastMedicalHistory> queryWrapper);
}
