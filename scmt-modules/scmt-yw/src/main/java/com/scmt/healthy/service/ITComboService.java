package com.scmt.healthy.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.entity.TCheckOrg;
import com.scmt.healthy.entity.TCombo;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
/**
 *@author
 **/
public interface ITComboService extends IService<TCombo> {

	/**
	* 功能描述：实现分页查询
	* @param tCombo 需要模糊查询的信息
	* @param searchVo 排序参数
	* @param pageVo 分页参数
	* @return 返回获取结果
	*/
	public IPage<TCombo> queryTComboListByPage(TCombo tCombo, SearchVo searchVo, PageVo pageVo);

	/**
	* 功能描述： 根据人员id来获取体检套餐数据
	* @param tCombo 主键
	* @param response response参数
	*/
	public void download(TCombo tCombo, HttpServletResponse response) ;

	/**
	 * 功能描述： 导出
	 * @param personId 主键
	 */
	public TCombo getTComboByPersonId(String personId,String hazardFactors,String content) ;

	/**
	 * 功能描述： 通过分组id查询体检套餐危害因素
	 * @param groupId 分组id
	 */
	public List<TCombo> gethazardFactorsByGroupId(String groupId);

	/**
	 * 功能描述：实现分页查询
	 * @param tCombo 需要模糊查询的信息
	 * @param pageVo 分页参数
	 * @return 返回获取结果
	 */
	public IPage<TCombo> queryTComboAndItemList(TCombo  tCombo, PageVo pageVo);

	List<TCombo>  getItemById(String id);

	List<TCombo> getTComboById(String[] id);

	List<TCombo> getOrgAndComboData(TCheckOrg tCheckOrg);

	TCombo getTCombo(String id);

	/**
	 * 根据机构id获取套餐分页列表
	 * @param tCombo
	 * @param searchVo
	 * @param pageVo
	 * @param checkOrgId
	 * @return
	 */
	IPage<TCombo> getComboListInfoByPage(TCombo tCombo, SearchVo searchVo, PageVo pageVo,String checkOrgId);
}
