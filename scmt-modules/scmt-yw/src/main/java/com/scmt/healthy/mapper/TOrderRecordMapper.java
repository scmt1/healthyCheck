package com.scmt.healthy.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scmt.healthy.entity.TCombo;
import com.scmt.healthy.entity.TOrderRecord;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Administrator
 * @since 2023-04-04
 */
@Repository
public interface TOrderRecordMapper extends BaseMapper<TOrderRecord> {

    /**
     * 分页查询预约记录
     * @param queryWrapper
     * @param page
     * @param personName
     * @param physicalType
     * @param checkDate
     * @param isPass
     * @return
     */
    IPage<TOrderRecord> selectRecordInfoPage(@Param(Constants.WRAPPER) QueryWrapper<TOrderRecord> queryWrapper, @Param("page") Page page,@Param("personName") String personName,@Param("physicalType") String physicalType,@Param("checkDate") String checkDate,@Param("isPass") String isPass);

    /**
     * 查询套餐对应项目名称
     * @param comboId
     * @return
     */
    List<String> selectTComboItemName(@Param("comboId")String comboId);

    /**
     * 通过电话号码查询对应预约记录
     * @param mobile
     * @return
     */
    List<TOrderRecord> selectOrderInfoByPhone(@Param("mobile") String mobile);

    /**
     * 根据订单id获取预约记录
     * @param orderId
     * @return
     */
    List<TOrderRecord> selectOrderRecordByOrderId(@Param("orderId") String orderId);

    /**
     * 根据套餐名称获取对应信息
     * @param name
     * @return
     */
    TCombo selectComboByName(@Param("name") String name);


    /**
     * 根据手机号和检查状态获取预约信息
     * @param mobile
     * @param isPass
     * @return
     */
    List<TOrderRecord> selectOrderRecordInfoByMobileAndIsPass(@Param("mobile") String mobile,@Param("isPass") String isPass);

    Integer selectRecordCountByIdCardAndCheckDateAndCheckOrgId(@Param("idCard")String idCard,@Param("checkDate")String checkDate,@Param("checkOrgId") String checkOrgId);
}
