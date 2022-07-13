package com.scmt.healthy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scmt.healthy.entity.TReviewRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ycy
 * @since 2021-10-08
 */
public interface TReviewRecordMapper extends BaseMapper<TReviewRecord> {

    List<TReviewRecord> getCheckProjectByPersonId(@Param("personId") String personId);
}
