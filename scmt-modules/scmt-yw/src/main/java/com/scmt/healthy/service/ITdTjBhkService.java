package com.scmt.healthy.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.entity.TdTjBhk;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 *@author 
 **/
public interface ITdTjBhkService extends IService<TdTjBhk> {

	/**
	* 功能描述：实现分页查询
	* @param tdTjBhk 需要模糊查询的信息
	* @param searchVo 排序参数
	* @param pageVo 分页参数
	* @return 返回获取结果
	*/
	public IPage<TdTjBhk> queryTdTjBhkListByPage(TdTjBhk  tdTjBhk, SearchVo searchVo, PageVo pageVo);

	/**
	* 功能描述： 导出
	* @param tdTjBhk 查询参数
	* @param response response参数
	*/
	public void download(TdTjBhk  tdTjBhk, HttpServletResponse response) ;

	List<Map<String, Object>> queryCompanyList();

}
