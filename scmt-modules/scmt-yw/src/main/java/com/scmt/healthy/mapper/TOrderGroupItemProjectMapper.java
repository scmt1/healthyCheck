package com.scmt.healthy.mapper;

import com.scmt.healthy.entity.TOrderGroupItem;
import com.scmt.healthy.entity.TOrderGroupItemProject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author ycy
 * @since 2021-10-21
 */
public interface TOrderGroupItemProjectMapper extends BaseMapper<TOrderGroupItemProject> {

    List<TOrderGroupItem> queryNoCheckTOrderGroupItemProjectList(@Param("personId") String personId, @Param("groupId") String groupId);

    List<TOrderGroupItemProject> getOrderGroupITemProjectByReview(@Param("portfolioId") String portfolioId, @Param("groupId") String groupId,
                                                                  @Param("officeId") List<String> officeId);

	List<TOrderGroupItem> queryAbandonTOrderGroupItemProjectList(@Param("personId") String personId, @Param("groupId") String groupId);
}
