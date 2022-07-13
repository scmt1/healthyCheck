package com.scmt.healthy.mapper;

import com.scmt.healthy.entity.TInspectionRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.web.bind.annotation.RequestParam;

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
}
