package com.scmt.healthy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.entity.TGroupPerson;
import com.scmt.healthy.entity.TUnitReport;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 单位报告表 Mapper 接口
 * </p>
 *
 * @author lbc
 * @since 2021-10-30
 */
public interface TUnitReportMapper extends BaseMapper<TUnitReport> {

    /**
     * 不分页查询全部
     */
    List<TUnitReport> queryTUnitReportListByNotPage(@Param("tUnitReport") TUnitReport tUnitReport, @Param("searchVo") SearchVo searchVo);

    List<TUnitReport> checkThePeopleStatisticsTable(@Param("orderId") String orderId);

    List<TUnitReport> checkThePeopleStatisticsTableFinish(@Param("orderId") String orderId);

    TUnitReport getTUnitReportByOrderId(@Param("orderId") String orderId);
}
