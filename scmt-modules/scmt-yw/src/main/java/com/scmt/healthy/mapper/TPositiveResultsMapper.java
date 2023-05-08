package com.scmt.healthy.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scmt.healthy.entity.TPositiveResults;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author dengjie
 * @since 2023-02-14
 */
public interface TPositiveResultsMapper extends BaseMapper<TPositiveResults> {

    TPositiveResults getByName(@Param("name")String name);
}
