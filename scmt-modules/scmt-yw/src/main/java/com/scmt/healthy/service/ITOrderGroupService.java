package com.scmt.healthy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.scmt.core.common.vo.Result;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.entity.TGroupPerson;
import com.scmt.healthy.entity.TOrderGroup;
import io.netty.util.concurrent.ThreadPerTaskExecutor;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 *@author
 **/
public interface ITOrderGroupService extends IService<TOrderGroup> {

	/**
	* 功能描述：实现分页查询
	* @param tOrderGroup 需要模糊查询的信息
	* @param searchVo 排序参数
	* @param pageVo 分页参数
	* @return 返回获取结果
	*/
	public IPage<TOrderGroup> queryTOrderGroupListByPage(TOrderGroup  tOrderGroup, SearchVo searchVo, PageVo pageVo);

	/**
	* 功能描述： 导出
	* @param tOrderGroup 查询参数
	* @param response response参数
	*/
	public void download(TOrderGroup  tOrderGroup, HttpServletResponse response) ;

    List<TOrderGroup> queryTOrderGroupList(TOrderGroup tOrderGroup);

	List<TOrderGroup> getTOrderGroupByGroupOrderId(String groupOrderId);

	List<TOrderGroup> getTOrderGroupByGroupUnitId(String groupUnitId);

    Map<String, Object> queryCheckProjectAndHazardFactors(String groupOrderId);

    Map<String, Object> queryCheckProjectAndHazardFactorsHealthy(String groupOrderId);

	List<Map<String, Object>> queryCheckProjectAndHazardFactorsList(String groupOrderId);

	List<Map<String, Object>> queryCheckProjectAndHazardFactorsComboList(List<String> comboIdList);

	List<TGroupPerson> queryCheckResultByOrderId(String groupOrderId);

	List<TGroupPerson> queryReviewResultByOrderId(String groupOrderId);

	List<Map<String, Object>> queryCheckResultAndHazardFactorsList(String groupOrderId);

	List<Map<String, Object>> queryCheckResultReview(String groupOrderId);

	List<Map<String, Object>> queryRegistDateReview(String groupOrderId);

	List<Map<String, Object>> queryRegistDate(String groupOrderId);
}
