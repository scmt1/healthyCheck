package com.scmt.healthy.mapper;

import com.scmt.healthy.entity.RelationPersonProjectCheck;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scmt.healthy.entity.TOrderGroupItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author dengjie
 * @since 2021-11-29
 */
public interface RelationPersonProjectCheckMapper extends BaseMapper<RelationPersonProjectCheck> {

    List<TOrderGroupItem> getNoRegistProjectData(@Param("personId") String personId, @Param("departmentIds") List<String> deparmentIds);

    List<TOrderGroupItem> getNoRegistProjectDataReview(@Param("personId") String personId, @Param("departmentIds") List<String> deparmentIds);
}
