package com.scmt.healthy.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.scmt.core.common.vo.Result;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.entity.TGroupPerson;
import com.scmt.healthy.entity.TOrderGroupItemProject;
import com.scmt.healthy.entity.TReviewProject;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
/**
 *@author
 **/
public interface ITReviewProjectService extends IService<TReviewProject> {

	/**
	* 功能描述：实现分页查询
	* @param tReviewProject 需要模糊查询的信息
	* @param searchVo 排序参数
	* @param pageVo 分页参数
	* @return 返回获取结果
	*/
	public IPage<TReviewProject> queryTReviewProjectListByPage(TReviewProject tReviewProject, SearchVo searchVo, PageVo pageVo);

	/**
	* 功能描述： 导出
	* @param tReviewProject 查询参数
	* @param response response参数
	*/
	public void download(TReviewProject tReviewProject, HttpServletResponse response) ;

	/**
	 * 功能描述： 查询未体检项目数据
	 * @param personId 人员id
	 */
	public List<TReviewProject> queryNoCheckReviewProject(String personId);

	IPage<TReviewProject> getTGroupPersonReviewer(TReviewProject tReviewProject, SearchVo searchVo, PageVo pageVo);

	TGroupPerson getTGroupPersonReviewerById(String id);

	List<TReviewProject> queryDataListByPersonId(QueryWrapper<TReviewProject> tReviewProjectQueryWrapper);

	/**
	 * 功能描述： 查询弃检项目数据
	 *
	 * @param personId     人员id
	 */
	List<TReviewProject> queryAbandonTReviewProjectList(String personId, String groupId);

    List<TReviewProject> listByWhere(TReviewProject tReviewProject);

    List<TGroupPerson> queryReviewPersonData(String orderId);

	List<TGroupPerson> queryAllPersonData(String orderId);
}
