package com.scmt.healthy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.entity.TDepartResult;
import com.scmt.healthy.entity.TGroupPerson;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author
 **/
public interface ITDepartResultService extends IService<TDepartResult> {

    /**
     * 功能描述：实现分页查询
     *
     * @param tDepartResult 需要模糊查询的信息
     * @param searchVo      排序参数
     * @return 返回获取结果
     */
    public List<TDepartResult> queryTDepartResultList(TDepartResult tDepartResult, SearchVo searchVo);

    /**
     * 功能描述：实现分页查询
     *
     * @param tDepartResult 需要模糊查询的信息
     * @param searchVo      排序参数
     * @return 返回获取结果
     */
    public List<TDepartResult> queryTDepartResultAndProjectId(TDepartResult tDepartResult, SearchVo searchVo);

    /**
     * 功能描述： 导出
     *
     * @param tDepartResult 查询参数
     * @param response      response参数
     */
    public void download(TDepartResult tDepartResult, HttpServletResponse response);

    /**
     * 功能描述：实现分页查询
     *
     * @param tDepartResult 需要模糊查询的信息
     * @param searchVo      排序参数
     * @param pageVo        分页参数
     * @return 返回获取结果
     */
    public IPage<TDepartResult> queryTDepartResultListAndOfficeNameByPage(TDepartResult tDepartResult, SearchVo searchVo, PageVo pageVo);

    /**
     * 功能描述：查询人员检查科室
     *
     * @return返回获取结果
     */
    public List<TDepartResult> queryPersonCheckOffice(TDepartResult tDepartResult);

    /**
     * 团检个人在科室是否检查完
     * @param personId
     * @param groupId
     * @param officeId
     * @return
     */
    Integer isCheckComplete(String personId,String groupId, String officeId);

    /**
     * 功能描述：实现分页查询
     * @return 返回获取结果
     */
    Integer queryTDepartResultByPersonId(List<String> groupItemIdList, String personId);

    /**
     * 查询科室已检查项目数量
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param officeIds 科室集合
     * @return
     */
    List<TDepartResult> queryTDepartResultStatistics(String startDate, String endDate, List<String> officeIds, String dept);
}
