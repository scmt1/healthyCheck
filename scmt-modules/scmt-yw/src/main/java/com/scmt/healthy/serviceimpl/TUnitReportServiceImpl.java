package com.scmt.healthy.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.scmt.healthy.entity.TUnitReport;
import com.scmt.healthy.service.ITUnitReportService;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.mapper.TUnitReportMapper;
import com.scmt.core.utis.FileUtil;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.LinkedHashMap;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;
/**
 *@author 
 **/
@Service
public class TUnitReportServiceImpl extends ServiceImpl<TUnitReportMapper, TUnitReport> implements ITUnitReportService {
	@Autowired
	@SuppressWarnings("SpringJavaAutowiringInspection")
	private TUnitReportMapper tUnitReportMapper;

	@Override
	public IPage<TUnitReport> queryTUnitReportListByPage(TUnitReport  tUnitReport, SearchVo searchVo, PageVo pageVo){
		int page = 1;
		int limit = 10;
		if (pageVo != null) {
			if (pageVo.getPageNumber() != 0) {
				page = pageVo.getPageNumber();
			}
			if (pageVo.getPageSize() != 0) {
				limit = pageVo.getPageSize();
			}
		}
		Page<TUnitReport> pageData = new Page<>(page, limit);
		QueryWrapper<TUnitReport> queryWrapper = new QueryWrapper<>();
		if (tUnitReport !=null) {
			queryWrapper = LikeAllField(tUnitReport,searchVo);
		}
		IPage<TUnitReport> result = tUnitReportMapper.selectPage(pageData, queryWrapper);
		return  result;
	}

	@Override
	public List<TUnitReport> queryTUnitReportListByNotPage(TUnitReport  tUnitReport, SearchVo searchVo){
		List<TUnitReport> result = tUnitReportMapper.queryTUnitReportListByNotPage(tUnitReport,searchVo);
		return  result;
	}

	@Override
	public void download(TUnitReport tUnitReport, HttpServletResponse response) {
		List<Map<String, Object>> mapList = new ArrayList<>();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		QueryWrapper<TUnitReport> queryWrapper = new QueryWrapper<>();
		if (tUnitReport !=null) {
			queryWrapper = LikeAllField(tUnitReport,null);
		}
		List<TUnitReport> list = tUnitReportMapper.selectList(queryWrapper);
		for (TUnitReport re : list) {
			Map<String, Object> map = new LinkedHashMap<>();
			map.put("id编号", re.getId());
			map.put("创建人", re.getCreateId());
			map.put("创建时间", re.getCreateTime());
			map.put("修改人", re.getUpdateId());
			map.put("修改时间", re.getUpdateTime());
			map.put("删除人", re.getDeleteId());
			map.put("删除时间", re.getDeleteTime());
			map.put("订单ID(外键)", re.getOrderId());
			map.put("报告编号", re.getCode());
			map.put("体检单位", re.getPhysicalUnit());
			map.put("委托单位", re.getEntrustUnit());
			map.put("危险因素", re.getHazardFactors());
			map.put("体检类型", re.getPhysicalType());
			map.put("体检日期", re.getPhysicalDate());
			map.put("体检人数", re.getPhysicalNumber());
			map.put("体检项目", re.getPhysicalProject());
			map.put("评论依据", re.getEvaluationBasis());
			map.put("结论意见", re.getConcludingObservations());
			map.put("是否显示(0-不显示，1-显示)", re.getIsShow());
			map.put("是否删除(0-未删除，1-已删除)", re.getDelFlag());
			mapList.add(map);
		}
		FileUtil.createExcel(mapList, "exel.xlsx", response);
	}

	@Override
	public List<TUnitReport> checkThePeopleStatisticsTable(String orderId) {
		return tUnitReportMapper.checkThePeopleStatisticsTable(orderId);
	}

	@Override
	public List<TUnitReport> checkThePeopleStatisticsTableFinish(String orderId) {
		return tUnitReportMapper.checkThePeopleStatisticsTableFinish(orderId);
	}

	@Override
	public TUnitReport getTUnitReportByOrderId(String orderId) {
		return tUnitReportMapper.getTUnitReportByOrderId(orderId);
	}

	/**
	* 功能描述：构建模糊查询
	* @param tUnitReport 需要模糊查询的信息
	* @return 返回查询
	*/
	public QueryWrapper<TUnitReport>  LikeAllField(TUnitReport  tUnitReport, SearchVo searchVo) {
		QueryWrapper<TUnitReport> queryWrapper = new QueryWrapper<>();
		if(StringUtils.isNotBlank(tUnitReport.getId())){
			queryWrapper.and(i -> i.like("t_unit_report.id", tUnitReport.getId()));
		}
		if(StringUtils.isNotBlank(tUnitReport.getCreateId())){
			queryWrapper.and(i -> i.like("t_unit_report.create_id", tUnitReport.getCreateId()));
		}
		if(tUnitReport.getCreateTime() != null){
			queryWrapper.and(i -> i.like("t_unit_report.create_time", tUnitReport.getCreateTime()));
		}
		if(StringUtils.isNotBlank(tUnitReport.getUpdateId())){
			queryWrapper.and(i -> i.like("t_unit_report.update_id", tUnitReport.getUpdateId()));
		}
		if(tUnitReport.getUpdateTime() != null){
			queryWrapper.and(i -> i.like("t_unit_report.update_time", tUnitReport.getUpdateTime()));
		}
		if(StringUtils.isNotBlank(tUnitReport.getDeleteId())){
			queryWrapper.and(i -> i.like("t_unit_report.delete_id", tUnitReport.getDeleteId()));
		}
		if(tUnitReport.getDeleteTime() != null){
			queryWrapper.and(i -> i.like("t_unit_report.delete_time", tUnitReport.getDeleteTime()));
		}
		if(tUnitReport.getOrderId() != null){
			queryWrapper.and(i -> i.like("t_unit_report.order_id", tUnitReport.getOrderId()));
		}
		if(StringUtils.isNotBlank(tUnitReport.getCode())){
			queryWrapper.and(i -> i.like("t_unit_report.code", tUnitReport.getCode()));
		}
		if(StringUtils.isNotBlank(tUnitReport.getPhysicalUnit())){
			queryWrapper.and(i -> i.like("t_unit_report.physical_unit", tUnitReport.getPhysicalUnit()));
		}
		if(StringUtils.isNotBlank(tUnitReport.getEntrustUnit())){
			queryWrapper.and(i -> i.like("t_unit_report.entrust_unit", tUnitReport.getEntrustUnit()));
		}
		if(StringUtils.isNotBlank(tUnitReport.getHazardFactors())){
			queryWrapper.and(i -> i.like("t_unit_report.hazard_factors", tUnitReport.getHazardFactors()));
		}
		if(StringUtils.isNotBlank(tUnitReport.getPhysicalType())){
			queryWrapper.and(i -> i.like("t_unit_report.physical_type", tUnitReport.getPhysicalType()));
		}
		if(tUnitReport.getPhysicalDate() != null){
			queryWrapper.and(i -> i.like("t_unit_report.physical_date", tUnitReport.getPhysicalDate()));
		}
		if(tUnitReport.getPhysicalNumber() != null){
			queryWrapper.and(i -> i.like("t_unit_report.physical_number", tUnitReport.getPhysicalNumber()));
		}
		if(StringUtils.isNotBlank(tUnitReport.getPhysicalProject())){
			queryWrapper.and(i -> i.like("t_unit_report.physical_project", tUnitReport.getPhysicalProject()));
		}
		if(StringUtils.isNotBlank(tUnitReport.getEvaluationBasis())){
			queryWrapper.and(i -> i.like("t_unit_report.evaluation_basis", tUnitReport.getEvaluationBasis()));
		}
		if(StringUtils.isNotBlank(tUnitReport.getConcludingObservations())){
			queryWrapper.and(i -> i.like("t_unit_report.concluding_observations", tUnitReport.getConcludingObservations()));
		}
		if(tUnitReport.getIsShow() != null){
			queryWrapper.and(i -> i.like("t_unit_report.is_show", tUnitReport.getIsShow()));
		}
		if(tUnitReport.getDelFlag() != null){
			queryWrapper.and(i -> i.like("t_unit_report.del_flag", tUnitReport.getDelFlag()));
		}
		if(searchVo!=null){
			if(searchVo.getStartDate()!=null && searchVo.getEndDate()!=null){
				queryWrapper.lambda().and(i -> i.between(TUnitReport::getCreateTime, searchVo.getStartDate(),searchVo.getEndDate()));
			}
		}
		queryWrapper.lambda().and(i -> i.eq(TUnitReport::getDelFlag, 0));
		return queryWrapper;
	
}
}