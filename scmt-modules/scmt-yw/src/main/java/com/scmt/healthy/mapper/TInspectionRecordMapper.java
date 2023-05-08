package com.scmt.healthy.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.scmt.healthy.entity.TInspectionRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ycy
 * @since 2021-10-22
 */
public interface TInspectionRecordMapper extends BaseMapper<TInspectionRecord> {

    public TInspectionRecord getByPersonId(@RequestParam(name = "personId")String personId);

    List<TInspectionRecord> getInspectionRecordList(@Param(Constants.WRAPPER) QueryWrapper<TInspectionRecord> queryWrapper);
}
