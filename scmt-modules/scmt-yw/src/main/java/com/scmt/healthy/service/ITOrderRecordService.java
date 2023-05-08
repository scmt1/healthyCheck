package com.scmt.healthy.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.entity.TOrderRecord;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 *@author
 **/
public interface ITOrderRecordService extends IService<TOrderRecord> {

	/**
	* 功能描述：实现分页查询
	* @param tOrderRecord 需要模糊查询的信息
	* @param searchVo 排序参数
	* @param pageVo 分页参数
	* @return 返回获取结果
	*/
	public IPage<TOrderRecord> queryTOrderRecordListByPage(TOrderRecord  tOrderRecord, SearchVo searchVo, PageVo pageVo);

	/**
	* 功能描述： 导出
	* @param tOrderRecord 查询参数
	* @param response response参数
	*/
	public void download(TOrderRecord  tOrderRecord, HttpServletResponse response,SearchVo searchVo, PageVo pageVo) ;

	/**
	 * 根据订单id获取对应的预约信息
	 * @param orderId
	 * @return
	 */
    Map<String,Object> getOrderRecordInfoByOrderId(String orderId);

	/**
	 * 根据手机号和检查状态获取对应的预约记录信息
	 * @param mobile
	 * @param isPass
	 * @return
	 */
	List<Map<String, Object>> getOrderRecordInfoListByMobile(String mobile, String isPass);

	/**
	 * 校验体检人员在同一天是否在同一个机构多次提交
	 * @param idCard
	 * @param checkDate
	 * @param checkOrgId
	 * @return
	 */
	Boolean getGroupPersonRepeatCommit(String idCard, String checkDate,String checkOrgId);
}
