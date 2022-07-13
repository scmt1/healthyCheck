package com.scmt.healthy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.scmt.core.common.vo.Result;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.entity.TUnitReport;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
/**
 *@author 
 **/
public interface ITUnitReportService extends IService<TUnitReport> {

	/**
	* 功能描述：实现分页查询
	* @param tUnitReport 需要模糊查询的信息
	* @param searchVo 排序参数
	* @param pageVo 分页参数
	* @return 返回获取结果
	*/
	public IPage<TUnitReport> queryTUnitReportListByPage(TUnitReport tUnitReport, SearchVo searchVo, PageVo pageVo);

	/**
	 * 功能描述：实现查询全部
	 * @param tUnitReport 需要模糊查询的信息
	 * @param searchVo 排序参数
	 * @return 返回获取结果
	 */
	public List<TUnitReport> queryTUnitReportListByNotPage(TUnitReport tUnitReport, SearchVo searchVo);

	/**
	* 功能描述： 导出
	* @param tUnitReport 查询参数
	* @param response response参数
	*/
	public void download(TUnitReport tUnitReport, HttpServletResponse response) ;

	List<TUnitReport> checkThePeopleStatisticsTable(String orderId);

	List<TUnitReport> checkThePeopleStatisticsTableFinish(String orderId);

    TUnitReport getTUnitReportByOrderId(String orderId);
}
