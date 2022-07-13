package com.scmt.healthy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.scmt.core.common.vo.Result;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.entity.TDepartItemResult;
import com.scmt.healthy.entity.TDepartResult;
import com.scmt.healthy.entity.TReviewProject;
import com.scmt.healthy.entity.TTemplate;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
/**
 *@author 
 **/
public interface ITTemplateService extends IService<TTemplate> {

	/**
	 * 功能描述：根据Id 查询模板信息（带上外键）
	 * @param id
	 * @return
	 */
	public TTemplate getTemplateById(String id);

	/**
	* 功能描述：实现分页查询
	* @param tTemplate 需要模糊查询的信息
	* @param searchVo 排序参数
	* @param pageVo 分页参数
	* @return 返回获取结果
	*/
	public IPage<TTemplate> queryTTemplateListByPage(TTemplate tTemplate, SearchVo searchVo, PageVo pageVo);

	/**
	* 功能描述： 导出
	* @param tTemplate 查询参数
	* @param response response参数
	*/
	public void download(TTemplate tTemplate, HttpServletResponse response) ;

	/**
	 * 功能描述： 根据条件模糊查询所有
	 * @param tTemplate
	 * @return
	 */
	public List<TTemplate> queryAllTTemplateList(TTemplate tTemplate);

	/**
	 * 功能描述： 根据用户id查询组合项目结果
	 * @param personId
	 * @return
	 */
	public List<TDepartResult> getDepartResultList(String personId,String groupId);

	/**
	 * 功能描述： 根据用户id集合查询分项组合项目结果
	 * @param personIds 用户id集合
	 * @return List<TDepartResult>
	 */
	public List<TDepartItemResult>  getDepartItemResultListByPersonIds( List<String> personIds,List<String> groupIds);


	/**
	 * 功能描述： 根据用户id 集合查询组合项目结果
	 * @param personIds 用户id集合
	 * @return List<TDepartResult>
	 */
	public List<TDepartResult> getDepartResultListByPersonIds( List<String> personIds,List<String> groupIds);

	/**
	 * 功能描述： 根据用户id查询分项组合项目结果
	 * @param personId
	 * @return
	 */
	public List<TDepartItemResult>  getDepartItemResultList(String personId,String groupId);
}
