package com.scmt.healthy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.scmt.core.common.vo.Result;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.entity.TOfficeTerm;
import com.scmt.healthy.entity.TSectionOffice;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
/**
 *@author
 **/
public interface ITOfficeTermService extends IService<TOfficeTerm> {

	/**
	* 功能描述：实现分页查询
	* @param tOfficeTerm 需要模糊查询的信息
	* @param searchVo 排序参数
	* @param pageVo 分页参数
	* @return 返回获取结果
	*/
	public IPage<TOfficeTerm> queryTOfficeTermListByPage(TOfficeTerm tOfficeTerm, SearchVo searchVo, PageVo pageVo);

	/**
	* 功能描述： 导出
	* @param tOfficeTerm 查询参数
	* @param response response参数
	*/
	public void download(TOfficeTerm tOfficeTerm, HttpServletResponse response) ;

	List<TOfficeTerm> queryAllOfficeTermData(TOfficeTerm tOfficeTerm);
}
