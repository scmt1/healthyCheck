package com.scmt.healthy.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.entity.TOrderSetting;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 *@author
 **/
public interface ITOrderSettingService extends IService<TOrderSetting> {

	/**
	* 功能描述：实现分页查询
	* @param tOrderSetting 需要模糊查询的信息
	* @param searchVo 排序参数
	* @param pageVo 分页参数
	* @return 返回获取结果
	*/
	public IPage<TOrderSetting> queryTOrderSettingListByPage(TOrderSetting  tOrderSetting, SearchVo searchVo, PageVo pageVo);

	/**
	* 功能描述： 导出
	* @param tOrderSetting 查询参数
	* @param response response参数
	*/
	public void download(TOrderSetting  tOrderSetting, HttpServletResponse response);


	/**
	 * 根据机构id和日期格式查询不同的预约设置信息
	 * @param tOrderSetting
	 * @param dateTime
	 * @return
	 */
	List<Map<String,Object>> findOrderSettingInfoByOrg(TOrderSetting tOrderSetting,String dateTime);

	/**
	 * 更新体检预约设置信息
	 * @param tOrderSetting
	 * @return
	 */
	Boolean updateOrderSettingInfo(TOrderSetting tOrderSetting);

	/**
	 * 批量保存和添加预约设置数据
	 * @param tOrderSettings
	 * @return
	 */
    boolean saveOrUpdateBatchInfo(List<TOrderSetting> tOrderSettings);

	/**
	 * 根据检查机构id获取对应的预约设置信息
	 * @param tOrderSetting
	 * @return
	 */
	List<Map<String,Object>> findOrderSettingInfoByOrgId(TOrderSetting tOrderSetting);

	/**
	 * 根据体检机构id和体检时间获取预约设置
	 * @param checkOrgId
	 * @param checkDate
	 * @return
	 */
	TOrderSetting findOrderSettingByCheckOrgAndCheckDate(String checkOrgId, String checkDate);
}
