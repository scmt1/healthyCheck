package com.scmt.healthy.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scmt.healthy.entity.TDepartResult;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scmt.healthy.entity.TGroupPerson;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author mike
 * @since 2021-10-21
 */
public interface TDepartResultMapper extends BaseMapper<TDepartResult> {

    IPage<TDepartResult> queryTDepartResultListAndOfficeName(@Param(Constants.WRAPPER) QueryWrapper<TDepartResult> queryWrapper, @Param("page") Page page);

    List<TDepartResult> queryTDepartResultAndProjectId(@Param(Constants.WRAPPER) QueryWrapper<TDepartResult> queryWrapper);

    List<TDepartResult> queryTDepartResultList(@Param("personId")String personId,@Param("groupId")String groupId);

    List<TDepartResult> queryPersonCheckOffice(TDepartResult  tDepartResult);

    Integer isCheckComplete(@Param("personId")String personId, @Param("groupId")String groupId, @Param("officeId")String officeId);

    Integer queryTDepartResultByPersonId(@Param("groupItemIdList") List<String> groupItemIdList,@Param("personId")String personId);

    /**
     * 查询科室已检查项目数量
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param officeIds 科室集合
     * @param dept 单位
     * @return 科室已检查项目数量
     */
    List<TDepartResult> queryTDepartResultStatistics(@Param("startDate")String startDate, @Param("endDate")String endDate, @Param("officeIds")List<String> officeIds, @Param("dept")String dept);
}
