package com.scmt.healthy.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scmt.healthy.entity.TGroupPerson;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author mike
 * @since 2021-10-09
 */
public interface TGroupPersonMapper extends BaseMapper<TGroupPerson> {

    IPage<TGroupPerson> getPersonByOfficeId(@Param("list")List<String> officeId,@Param("isCheck")Integer isCheck, @Param("officeIds")String officeIds,
                                            @Param(Constants.WRAPPER) QueryWrapper<TGroupPerson> queryWrapper, @Param("page") Page page);

    Map<String, Object> getGroupPersonByIdWithLink(String id);

    List<TGroupPerson> getTGroupPersonByOrderId(String orderId);

    IPage<TGroupPerson> getTGroupPersonInspection(@Param(Constants.WRAPPER) QueryWrapper<TGroupPerson> queryWrapper, @Param("page") Page page);

    IPage<TGroupPerson> getExamineFinishPersonData(@Param(Constants.WRAPPER) QueryWrapper<TGroupPerson> queryWrapper, @Param("page") Page page);

    Map<String, Object> getGroupPersonInfo(@Param("id") String id);

    Map<String, Object> getGroupPersonInfoById(@Param("id") String id);

    IPage<TGroupPerson> getInspectionTGroupPersonList(@Param(Constants.WRAPPER) QueryWrapper<TGroupPerson> queryWrapper, @Param("page") Page page);

    IPage<TGroupPerson> getInspectionTGroupPersonReviewList(@Param(Constants.WRAPPER) QueryWrapper<TGroupPerson> queryWrapper, @Param("page") Page page);

    IPage<TGroupPerson> queryNoCheckProjectPersonList(@Param(Constants.WRAPPER) QueryWrapper<TGroupPerson> queryWrapper, @Param("page") Page page);

    IPage<TGroupPerson> queryTGroupPersonAndResultList(@Param(Constants.WRAPPER) QueryWrapper<TGroupPerson> queryWrapper, @Param("page") Page page);

    IPage<TGroupPerson> queryTGroupPersonAndResultAppList(@Param(Constants.WRAPPER) QueryWrapper<TGroupPerson> queryWrapper, @Param("page") Page page);

    TGroupPerson getPersonListNum(@Param("orderIdList") List<String> orderIdList,@Param("physicalType") String physicalType);

    TGroupPerson getPersonNumByGroupId(@Param("groupId") String groupId);

    List<TGroupPerson> queryPersonDataListByOrderId(String orderId);

    /**
     *根据 Id集合查询人员信息
     * @param ids
     * @return
     */
    List<Map<String, Object>>  getGroupPersonInfoByIds(@Param("ids")  List<String> ids);

    /**
     * 根据当前登录医生的科室，查询对应的复查体检人员(未检)
     * @param officeId
     * @param queryWrapper
     * @param page
     * @param startDate
     * @param endDate
     * @return
     */
    IPage<TGroupPerson> getPersonReviewerCheck(@Param("list")List<String> officeId,
                                               @Param(Constants.WRAPPER) QueryWrapper<TGroupPerson> queryWrapper, @Param("page") Page page,
                                               @Param("startDate")String startDate,@Param("endDate")String endDate);

    /**
     * 根据当前登录医生的科室，查询对应的复查体检人员(已检)
     * @param officeId
     * @param queryWrapper
     * @param page
     * @param startDate
     * @param endDate
     * @return
     */
    IPage<TGroupPerson> getPersonReviewerNoCheck(@Param("list")List<String> officeId,
                                                 @Param(Constants.WRAPPER) QueryWrapper<TGroupPerson> queryWrapper, @Param("page") Page page,
                                                 @Param("startDate")String startDate,@Param("endDate")String endDate);
    /**
     * 更新问诊签名
     */
    Integer updatewAutograph();
}
