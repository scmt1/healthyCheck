package com.scmt.healthy.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.scmt.healthy.entity.TOrderGroupItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scmt.healthy.entity.TOrderGroupItemProject;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 分组项目 Mapper 接口
 * </p>
 *
 * @author ycy
 * @since 2021-10-19
 */
public interface TOrderGroupItemMapper extends BaseMapper<TOrderGroupItem> {

    List<TOrderGroupItem> queryDataListByGroupId(@Param(Constants.WRAPPER) QueryWrapper<TOrderGroupItem> queryWrapper, @Param(value = "personId") String personId);

    Integer getAllCheckCount(@Param(value = "personId") String personId, @Param(value = "groupId") String groupId);

    Integer getDepartResultCount(@Param(value = "personId") String personId, @Param(value = "groupId") String groupId);

    List<TOrderGroupItem> queryOrderGroupItemList(@Param("groupOrderId") String groupOrderId, @Param(value = "groupId") String groupId);

    List<TOrderGroupItem> listByQueryWrapper(@Param("orderGroupItem") TOrderGroupItem orderGroupItem);
}
