package com.scmt.healthy.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scmt.healthy.entity.TGroupOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ycy
 * @since 2021-10-12
 */
public interface TGroupOrderMapper extends BaseMapper<TGroupOrder> {

    List<TGroupOrder> queryAllTGroupOrderList(@Param(Constants.WRAPPER) QueryWrapper<TGroupOrder> queryWrapper);

    IPage<TGroupOrder> queryGroupOrderListByPage(@Param(Constants.WRAPPER) QueryWrapper<TGroupOrder> queryWrapper, @Param("page") Page page);

    IPage<TGroupOrder> queryGroupOrderAppListByPage(@Param(Constants.WRAPPER) QueryWrapper<TGroupOrder> queryWrapper, @Param("page") Page page);

    TGroupOrder getOneByWhere(String departmentId);

    TGroupOrder getTGroupOrderNumByCreateId(@Param("auditUserId") String auditUserId,@Param("physicalType") String physicalType);

    TGroupOrder getTGroupOrderNum(@Param("auditUserId") String auditUserId,@Param("physicalType") String physicalType);

    TGroupOrder getTGroupOrderNumAndByCreateId(@Param("auditUserId") String auditUserId,@Param("physicalType") String physicalType);

    TGroupOrder getTGroupOrderNumAll(@Param("auditUserId") String auditUserId,@Param("physicalType") String physicalType);

    TGroupOrder getTGroupOrderNumFinishAndByCreateId(@Param("auditUserId") String auditUserId,@Param("physicalType") String physicalType);

    TGroupOrder getTGroupOrderNumAndFinish(@Param("auditUserId") String auditUserId,@Param("physicalType") String physicalType);

    TGroupOrder getTGroupOrderNumFinish(@Param("auditUserId") String auditUserId,@Param("physicalType") String physicalType);

    Map<String, Object> getTGroupOrderByIdWithLink(String id);

    IPage<TGroupOrder> queryApproveTGroupOrderList(@Param(Constants.WRAPPER) QueryWrapper<TGroupOrder> queryWrapper, @Param("page") Page page);

    Map<String,Object> getComNameByGroupId(String groupId);


    /**
     * 获取当天最新的订单信息
     * @return
     */
    TGroupOrder getLastGroupOrderByOrderDateAndCheckOrgId();

}
