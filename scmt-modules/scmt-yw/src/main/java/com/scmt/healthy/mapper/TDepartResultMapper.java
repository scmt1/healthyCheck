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
}
