package com.scmt.healthy.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.entity.TBaseProject;
import com.scmt.healthy.entity.TPortfolioProject;
import com.scmt.healthy.entity.TReviewProject;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 *@author
 **/
public interface ITPortfolioProjectService extends IService<TPortfolioProject> {

	/**
	* 功能描述：实现分页查询
	* @param searchVo 排序参数
	* @param pageVo 分页参数
	* @return 返回获取结果
	*/
	public IPage<TPortfolioProject> queryTPortfolioProjectListByPage(TPortfolioProject  tPortfolioProject, SearchVo searchVo, PageVo pageVo);

	/**
	* 功能描述： 导出
	* @param tPortfolioProject 查询参数
	* @param response response参数
	*/
	public void download(TPortfolioProject  tPortfolioProject, HttpServletResponse response) ;

    List<TPortfolioProject> queryTPortfolioProjectListByOfficeId(TPortfolioProject tPortfolioProject);

	/**
	 * 根据组合项目id获取基础项目
	 * @param portfolioProjectId
	 * @return
	 */
	List<TBaseProject> getBaseProjectByPortfolioProject(String portfolioProjectId);

	/**
	 * 模糊查询所有
	 * @param tPortfolioProject
	 * @return
	 */
	List<TPortfolioProject> queryPortfolioProjectList(TPortfolioProject tPortfolioProject);
}
