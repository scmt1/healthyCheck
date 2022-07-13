package com.scmt.healthy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.scmt.core.common.vo.Result;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.entity.TOrderFlow;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
/**
 *@author
 **/
public interface ITOrderFlowService extends IService<TOrderFlow> {

	/**
	* 功能描述：实现分页查询
	* @param tOrderFlow 需要模糊查询的信息
	* @param searchVo 排序参数
	* @param pageVo 分页参数
	* @return 返回获取结果
	*/
	public IPage<TOrderFlow> queryTOrderFlowListByPage(TOrderFlow  tOrderFlow, SearchVo searchVo, PageVo pageVo);

	/**
	* 功能描述： 导出
	* @param tOrderFlow 查询参数
	* @param response response参数
	*/
	public void download(TOrderFlow  tOrderFlow, HttpServletResponse response) ;

    List<TOrderFlow> queryAllTOrderFlowList(TOrderFlow tOrderFlow);
}
