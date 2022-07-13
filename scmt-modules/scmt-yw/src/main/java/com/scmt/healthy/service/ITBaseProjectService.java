package com.scmt.healthy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.Result;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.entity.TBaseProject;
import javax.servlet.http.HttpServletResponse;
/**
 *@author
 **/
public interface ITBaseProjectService extends IService<TBaseProject> {

	/**
	* 功能描述：实现分页查询
	* @param tBaseProject 需要模糊查询的信息
	* @param searchVo 排序参数
	* @param pageVo 分页参数
	* @return 返回获取结果
	*/
	public Result<Object> queryTBaseProjectListByPage(TBaseProject  tBaseProject, SearchVo searchVo, PageVo pageVo);

	/**
	* 功能描述： 导出
	* @param tBaseProject 查询参数
	* @param response response参数
	*/
	public void download(TBaseProject  tBaseProject, HttpServletResponse response) ;

	/**
	 * 功能描述：查询项目
	 *
	 * @param officeId 科室id
	 */
	public Result<Object> queryAllTBaseProject(String officeId);
}
