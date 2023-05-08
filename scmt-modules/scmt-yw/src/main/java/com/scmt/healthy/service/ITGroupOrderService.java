package com.scmt.healthy.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.entity.TGroupOrder;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 *@author
 **/
public interface ITGroupOrderService extends IService<TGroupOrder> {

	/**
	* 功能描述：实现分页查询
	* @param tGroupOrder 需要模糊查询的信息
	* @param searchVo 排序参数
	* @param pageVo 分页参数
	* @return 返回获取结果
	*/
	public IPage<TGroupOrder> queryTGroupOrderListByPage(TGroupOrder  tGroupOrder, SearchVo searchVo, PageVo pageVo);

	/**
	* 功能描述：实现分页查询
	* @param tGroupOrder 需要模糊查询的信息
	* @param searchVo 排序参数
	* @param unitIds 团检单位id
	* @return 返回获取结果
	*/
	public IPage<TGroupOrder> queryTGroupOrderAppList(TGroupOrder  tGroupOrder, SearchVo searchVo, PageVo pageVo, List<String> unitIds);

	/**
	* 功能描述： 导出
	* @param tGroupOrder 查询参数
	* @param response response参数
	*/
	public void download(TGroupOrder  tGroupOrder, HttpServletResponse response) ;

	/**
	 * 模糊查询所有
	 * @param tGroupOrder
	 * @return
	 */
    List<TGroupOrder> queryAllTGroupOrderList(TGroupOrder tGroupOrder);

    TGroupOrder getOneByWhere(String departmentId);

	/**
	 * 功能描述： 查询待审批与已审批数量
	 * @param auditUserId 查询参数
	 */
	TGroupOrder getTGroupOrderNumByCreateId(String auditUserId,String physicalType);

	/**
	 * 根据团检订单id 查询信息，同时关联公司信息
	 * @param id
	 * @return
	 */
	Map<String, Object> getTGroupOrderByIdWithLink(String id);

	/**
	 * 功能描述：实现分页查询
	 * @param tGroupOrder 需要模糊查询的信息
	 * @param searchVo 排序参数
	 * @param pageVo 分页参数
	 * @return 返回获取结果
	 */
	public IPage<TGroupOrder> queryApproveTGroupOrderList(String auditUserId,TGroupOrder  tGroupOrder, SearchVo searchVo, PageVo pageVo);

	/**
	 * 功能描述：获取套餐名称
	 * @param groupId 需要模糊查询的信息
	 * @return 返回获取结果
	 */
	Map<String,Object> getComNameByGroupId(String groupId);


	/**
	 * 获取当天最新的订单信息
	 * @return
	 */
    TGroupOrder getLastGroupOrderInfo();
}
