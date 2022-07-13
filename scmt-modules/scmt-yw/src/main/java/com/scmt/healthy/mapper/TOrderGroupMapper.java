package com.scmt.healthy.mapper;

import com.scmt.healthy.entity.TGroupPerson;
import com.scmt.healthy.entity.TOrderGroup;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单分组 Mapper 接口
 * </p>
 *
 * @author ycy
 * @since 2021-10-19
 */
public interface TOrderGroupMapper extends BaseMapper<TOrderGroup> {

    public List<TOrderGroup> getTOrderGroupByGroupOrderId(@Param("groupOrderId") String groupOrderId);

    public List<TOrderGroup> getTOrderGroupByGroupUnitId(@Param("groupUnitId") String groupUnitId);

    Map<String, Object> queryCheckProjectAndHazardFactors(@Param("groupOrderId") String groupOrderId);

    public List<TGroupPerson> queryCheckResultByOrderId(@Param("groupOrderId") String groupOrderId);

    public List<TGroupPerson> queryReviewResultByOrderId(@Param("groupOrderId") String groupOrderId);

    List<Map<String, Object>> queryCheckProjectAndHazardFactorsList(@Param("groupOrderId") String groupOrderId);

    List<Map<String, Object>> queryCheckProjectAndHazardFactorsComboList(@Param("comboIdList") List<String> comboIdList);

    List<Map<String, Object>> queryCheckResultAndHazardFactorsList(@Param("groupOrderId") String groupOrderId);

    List<Map<String, Object>> queryCheckResultReview(@Param("groupOrderId") String groupOrderId);

    List<Map<String, Object>> queryRegistDateReview(@Param("groupOrderId") String groupOrderId);

    List<Map<String, Object>> queryRegistDate(@Param("groupOrderId") String groupOrderId);
}
