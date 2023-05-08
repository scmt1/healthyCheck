package com.scmt.healthy.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.entity.TDepartItemResult;
import com.scmt.healthy.entity.TDepartResult;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 *@author
 **/
public interface ITDepartItemResultService extends IService<TDepartItemResult> {

    /**
     * 功能描述：实现分页查询
     * @param tDepartItemResult 需要模糊查询的信息
     * @param searchVo 排序参数
     * @param pageVo 分页参数
     * @return 返回获取结果
     */
    public IPage<TDepartItemResult> queryTDepartItemResultListByPage(TDepartItemResult  tDepartItemResult, SearchVo searchVo, PageVo pageVo);

    /**
     * 功能描述： 导出
     * @param tDepartItemResult 查询参数
     * @param response response参数
     */
    public void download(TDepartItemResult  tDepartItemResult, HttpServletResponse response) ;

    /**
     * 功能描述：实现分页查询
     * @param personId 人员id
     * @param officeId 科室id
     * @param departResultId 分检结果id
     * @return 返回获取结果
     */
    public List<TDepartItemResult> queryAllTDepartItemResultList(String personId, String officeId, String checkDate, String departResultId);

    /**
     * 功能描述：实现分页查询
     * @param tDepartItemResult 需要模糊查询的信息
     * @param searchVo 排序参数
     * @param pageVo 分页参数
     * @return 返回获取结果
     */
    public IPage<TDepartItemResult> querySummaryResultListByPage(TDepartItemResult  tDepartItemResult, SearchVo searchVo, PageVo pageVo);

    /**
     * 功能描述：实现分页查询
     * @param tDepartItemResult 需要模糊查询的信息
     * @param searchVo 排序参数
     * @param pageVo 分页参数
     * @return 返回获取结果
     */
    public IPage<TDepartItemResult> querySummaryResultListReviewByPage(TDepartItemResult  tDepartItemResult, SearchVo searchVo, PageVo pageVo);

    /**
     * 功能描述：查询基础项目检查结果异常数据
     * @param tDepartItemResult
     * @return 返回获取结果
     */
    public List<TDepartItemResult> queryAllAbnormalItemResultList(TDepartItemResult tDepartItemResult);

    /**
     * 功能描述：根据人员id来获取异常数据
     * @param personId 人员id
     * @return 返回获取结果
     */
    public List<TDepartItemResult> getAbnormalResultList(String personId);

    /**
     * 功能描述：根据人员id来获取异常数据
     * @param personId 人员id
     * @return 返回获取结果
     */
    public List<TDepartItemResult> getAllListByPersonId(String personId);
}
