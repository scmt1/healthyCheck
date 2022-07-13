package com.scmt.healthy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.scmt.core.common.vo.Result;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.entity.TSymptom;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
/**
 *@author 
 **/
public interface ITSymptomService extends IService<TSymptom> {

	/**
	* 功能描述：实现分页查询
	* @param tSymptom 需要模糊查询的信息
	* @param searchVo 排序参数
	* @param pageVo 分页参数
	* @return 返回获取结果
	*/
	public IPage<TSymptom> queryTSymptomListByPage(TSymptom  tSymptom, SearchVo searchVo, PageVo pageVo);

	/**
	* 功能描述：实现查询全部
	* @param tSymptom 需要模糊查询的信息
	* @param searchVo 排序参数
	* @return 返回获取结果
	*/
	public List<TSymptom> queryTSymptomAll(TSymptom  tSymptom, SearchVo searchVo);

	/**
	* 功能描述： 导出
	* @param tSymptom 查询参数
	* @param response response参数
	*/
	public void download(TSymptom  tSymptom, HttpServletResponse response) ;
}
