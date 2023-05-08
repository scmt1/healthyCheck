package com.scmt.healthy.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.entity.TCheckOrg;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 *@author
 **/
public interface ITCheckOrgService extends IService<TCheckOrg> {

	/**
	* 功能描述：实现分页查询
	* @param tCheckOrg 需要模糊查询的信息
	* @param searchVo 排序参数
	* @param pageVo 分页参数
	* @return 返回获取结果
	*/
	public IPage<TCheckOrg> queryTCheckOrgListByPage(TCheckOrg  tCheckOrg, SearchVo searchVo, PageVo pageVo);

	/**
	* 功能描述： 导出
	* @param tCheckOrg 查询参数
	* @param response response参数
	*/
	public void download(TCheckOrg  tCheckOrg, HttpServletResponse response) ;

	/**
	 * 模糊查询所有体检机构
	 * @param tCheckOrg
	 * @param searchVo
	 * @return
	 */
	List<TCheckOrg> getAllCheckOrg(TCheckOrg tCheckOrg,SearchVo searchVo);

	/**
	 * 检查体检机构名称
	 * @param tCheckOrg
	 * @return
	 */
	Boolean checkOrgName(TCheckOrg tCheckOrg);

	/**
	 * 连表分页查询体检机构及对应套餐信息
	 * @param tCheckOrg
	 * @param searchVo
	 * @param pageVo
	 * @return
	 */
	public IPage<TCheckOrg> getOrgAndComboInfoByPage(TCheckOrg tCheckOrg,SearchVo searchVo,PageVo pageVo);


	/**
	 *根据id获取体检机构及对应套餐信息
	 * @param tCheckOrg
	 * @return
	 */
	TCheckOrg getOrgAndComboInfo(TCheckOrg tCheckOrg);

	/**
	 * 保存操作或者更新操作时处理上传的图片
	 * @param tCheckOrg
	 * @param checkImage
	 * @return
	 */
	Boolean handleCheckOrgImg(TCheckOrg tCheckOrg,String checkImage);

}
