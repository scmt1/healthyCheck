package com.scmt.healthy.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scmt.healthy.entity.TGroupPerson;
import com.scmt.healthy.entity.TOrderGroupItemProject;
import com.scmt.healthy.entity.TReviewProject;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ycy
 * @since 2021-10-21
 */
public interface TReviewProjectMapper extends BaseMapper<TReviewProject> {

    List<TReviewProject> queryNoCheckReviewProject(@Param("personId") String personId);

    IPage<TReviewProject> getTGroupPersonReviewer(@Param(Constants.WRAPPER) QueryWrapper<TReviewProject> queryWrapper,@Param("page") Page page);

    IPage<TReviewProject> getTGroupPersonReviewerAndDept(@Param(Constants.WRAPPER) QueryWrapper<TReviewProject> queryWrapper,@Param("page") Page page,@Param("deptName") String deptName,@Param("testNum") String testNum);

    TGroupPerson getTGroupPersonReviewerById(String id);

    List<TReviewProject> queryDataListByPersonId(@Param(Constants.WRAPPER) QueryWrapper<TReviewProject> tReviewProjectQueryWrapper);

    List<TReviewProject> queryAbandonTReviewProjectList(@Param("personId") String personId, @Param("groupId") String groupId);

    List<TReviewProject> listByWhere(@Param("tReviewProject") TReviewProject tReviewProject);

    List<TGroupPerson> queryReviewPersonData(@Param("orderId") String orderId);

    List<TGroupPerson> queryAllPersonData(@Param("orderId") String orderId);
}
