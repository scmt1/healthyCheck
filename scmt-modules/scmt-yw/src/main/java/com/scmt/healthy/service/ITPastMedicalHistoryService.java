package com.scmt.healthy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.scmt.core.common.vo.Result;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.entity.TPastMedicalHistory;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
/**
 *@author 
 **/
public interface ITPastMedicalHistoryService extends IService<TPastMedicalHistory> {

	/**
	* 功能描述：实现分页查询
	* @param tPastMedicalHistory 需要模糊查询的信息
	* @param searchVo 排序参数
	* @param pageVo 分页参数
	* @return 返回获取结果
	*/
	public IPage<TPastMedicalHistory> queryTPastMedicalHistoryListByPage(TPastMedicalHistory  tPastMedicalHistory, SearchVo searchVo, PageVo pageVo);

	/**
	* 功能描述：实现查询全部
	* @param tPastMedicalHistory 需要模糊查询的信息
	* @param searchVo 排序参数
	* @return 返回获取结果
	*/
	public List<TPastMedicalHistory> queryTPastMedicalHistoryAll(TPastMedicalHistory  tPastMedicalHistory, SearchVo searchVo);

	/**
	* 功能描述： 导出
	* @param tPastMedicalHistory 查询参数
	* @param response response参数
	*/
	public void download(TPastMedicalHistory  tPastMedicalHistory, HttpServletResponse response) ;
}
