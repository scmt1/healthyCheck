package com.scmt.healthy.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.scmt.healthy.entity.TdTjBhk;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 体检主表信息 Mapper 接口
 * </p>
 *
 * @author dengjie
 * @since 2021-10-30
 */
@DS("sub")
public interface TdTjBhkMapper extends BaseMapper<TdTjBhk> {

    List<Map<String, Object>> queryCompanyList();

}
