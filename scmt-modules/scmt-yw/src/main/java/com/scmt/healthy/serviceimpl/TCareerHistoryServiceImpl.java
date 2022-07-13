package com.scmt.healthy.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.scmt.healthy.entity.TCareerHistory;
import com.scmt.healthy.service.ITCareerHistoryService;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.mapper.TCareerHistoryMapper;
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
public class TCareerHistoryServiceImpl extends ServiceImpl<TCareerHistoryMapper, TCareerHistory> implements ITCareerHistoryService {
	@Autowired
	@SuppressWarnings("SpringJavaAutowiringInspection")
	private TCareerHistoryMapper tCareerHistoryMapper;

	@Override
	public IPage<TCareerHistory> queryTCareerHistoryListByPage(TCareerHistory  tCareerHistory, SearchVo searchVo, PageVo pageVo){
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
		Page<TCareerHistory> pageData = new Page<>(page, limit);
		QueryWrapper<TCareerHistory> queryWrapper = new QueryWrapper<>();
		if (tCareerHistory !=null) {
			queryWrapper = LikeAllField(tCareerHistory,searchVo);
		}
		IPage<TCareerHistory> result = tCareerHistoryMapper.selectPage(pageData, queryWrapper);
		return  result;
	}
	@Override
	public List<TCareerHistory> queryTCareerHistoryAll(TCareerHistory  tCareerHistory, SearchVo searchVo){
		QueryWrapper<TCareerHistory> queryWrapper = new QueryWrapper<>();
		if (tCareerHistory !=null) {
			queryWrapper = LikeAllField(tCareerHistory,searchVo);
		}
		List<TCareerHistory> result = tCareerHistoryMapper.selectList(queryWrapper);
		return  result;
	}
	@Override
	public void download(TCareerHistory tCareerHistory, HttpServletResponse response) {
		List<Map<String, Object>> mapList = new ArrayList<>();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		QueryWrapper<TCareerHistory> queryWrapper = new QueryWrapper<>();
		if (tCareerHistory !=null) {
			queryWrapper = LikeAllField(tCareerHistory,null);
		}
		List<TCareerHistory> list = tCareerHistoryMapper.selectList(queryWrapper);
		for (TCareerHistory re : list) {
			Map<String, Object> map = new LinkedHashMap<>();
			mapList.add(map);
		}
		FileUtil.createExcel(mapList, "exel.xlsx", response);
	}

	/**
	* 功能描述：构建模糊查询
	* @param tCareerHistory 需要模糊查询的信息
	* @return 返回查询
	*/
	public QueryWrapper<TCareerHistory>  LikeAllField(TCareerHistory  tCareerHistory, SearchVo searchVo) {
		QueryWrapper<TCareerHistory> queryWrapper = new QueryWrapper<>();
		if(StringUtils.isNotBlank(tCareerHistory.getId())){
			queryWrapper.lambda().and(i -> i.eq(TCareerHistory::getId, tCareerHistory.getId()));
		}
		if(tCareerHistory.getStartDate() != null){
			queryWrapper.lambda().and(i -> i.like(TCareerHistory::getStartDate, tCareerHistory.getStartDate()));
		}
		if(StringUtils.isNotBlank(tCareerHistory.getWorkUnit())){
			queryWrapper.lambda().and(i -> i.like(TCareerHistory::getWorkUnit, tCareerHistory.getWorkUnit()));
		}
		if(StringUtils.isNotBlank(tCareerHistory.getWorkTypeText())){
			queryWrapper.lambda().and(i -> i.like(TCareerHistory::getWorkTypeText, tCareerHistory.getWorkTypeText()));
		}
		if(StringUtils.isNotBlank(tCareerHistory.getHazardFactorsText())){
			queryWrapper.lambda().and(i -> i.like(TCareerHistory::getHazardFactorsText, tCareerHistory.getHazardFactorsText()));
		}
		if(StringUtils.isNotBlank(tCareerHistory.getProtectiveMeasures())){
			queryWrapper.lambda().and(i -> i.like(TCareerHistory::getProtectiveMeasures, tCareerHistory.getProtectiveMeasures()));
		}
		if(StringUtils.isNotBlank(tCareerHistory.getPersonId())){
			queryWrapper.lambda().and(i -> i.eq(TCareerHistory::getPersonId, tCareerHistory.getPersonId()));
		}
		if(searchVo!=null){
			if(searchVo.getStartDate()!=null && searchVo.getEndDate()!=null){
				queryWrapper.lambda().and(i -> i.between(TCareerHistory::getCreateTime, searchVo.getStartDate(),searchVo.getEndDate()));
			}
		}
		return queryWrapper;

}
}
