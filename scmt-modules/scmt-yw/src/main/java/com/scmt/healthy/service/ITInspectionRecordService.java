package com.scmt.healthy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.scmt.core.common.vo.Result;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.entity.TInspectionRecord;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
/**
 *@author
 **/
public interface ITInspectionRecordService extends IService<TInspectionRecord> {

	/**
	* 功能描述：实现分页查询
	* @param tInspectionRecord 需要模糊查询的信息
	* @param searchVo 排序参数
	* @param pageVo 分页参数
	* @return 返回获取结果
	*/
	public IPage<TInspectionRecord> queryTInspectionRecordListByPage(TInspectionRecord tInspectionRecord, SearchVo searchVo, PageVo pageVo);

	/**
	* 功能描述： 导出
	* @param tInspectionRecord 查询参数
	* @param response response参数
	*/
	public void download(TInspectionRecord tInspectionRecord, HttpServletResponse response) ;

	/**
	 * 功能描述： 根据人员id来获取总检结论数据
	 * @param personId 人员id
	 */
	public TInspectionRecord getByPersonId(String personId) ;
}
