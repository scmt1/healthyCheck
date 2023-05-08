package com.scmt.healthy.mapper;

import com.scmt.healthy.entity.TdTjBadrsns;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 危害因素体检结论表 Mapper 接口
 * </p>
 *
 * @author dengjie
 * @since 2023-02-17
 */
public interface TdTjBadrsnsMapper extends BaseMapper<TdTjBadrsns> {

    public List<TdTjBadrsns> selectListByIds(@Param("ids")  List<String> ids);
}
