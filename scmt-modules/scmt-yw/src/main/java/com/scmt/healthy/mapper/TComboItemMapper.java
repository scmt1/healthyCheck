package com.scmt.healthy.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.scmt.healthy.entity.TComboItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author mike
 * @since 2021-10-14
 */
public interface TComboItemMapper extends BaseMapper<TComboItem> {

    List<TComboItem> listByComboIds(@Param(Constants.WRAPPER) QueryWrapper<TComboItem> queryWrapper);
}
