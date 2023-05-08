package com.scmt.healthy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scmt.healthy.entity.TOrderSetting;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
public interface TOrderSettingMapper extends BaseMapper<TOrderSetting> {


    /**
     * 根据年份统计每一个月份对应的可预约人数总和和可预约人数总和
     * @param CheckOrgId
     * @param dateTime
     * @return
     */
    List<Map<String,Object>> getCountInfoByOrgIdAndYear(@Param("CheckOrgId") String CheckOrgId,@Param("dateTime") String dateTime);



    /**
     * 根据结构id和时间格式查询预约设置信息
     * @param CheckOrgId
     * @param dateTime
     * @return
     */
    List<Map<String,Object>> getOrderInfoByOrgIdAndMonth(@Param("CheckOrgId") String CheckOrgId,@Param("dateTime") String dateTime);


    /**
     * 根据时间段获取对应机构的预约信息
     * @param tOrderSetting
     * @return
     */
    List<Map<String,Object>> getAvailableTimeByOrgIdAndOrderDate(TOrderSetting tOrderSetting);
}
