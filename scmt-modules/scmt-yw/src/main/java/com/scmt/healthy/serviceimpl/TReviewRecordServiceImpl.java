package com.scmt.healthy.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.core.utis.FileUtil;
import javax.servlet.http.HttpServletResponse;

import com.scmt.healthy.entity.TReviewRecord;
import com.scmt.healthy.mapper.TReviewRecordMapper;
import com.scmt.healthy.service.ITReviewRecordService;
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
public class TReviewRecordServiceImpl extends ServiceImpl<TReviewRecordMapper, TReviewRecord> implements ITReviewRecordService {
	@Autowired
	@SuppressWarnings("SpringJavaAutowiringInspection")
	private TReviewRecordMapper tReviewRecordMapper;

	@Override
	public IPage<TReviewRecord> queryTReviewRecordListByPage(TReviewRecord  tReviewRecord, SearchVo searchVo, PageVo pageVo){
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
		Page<TReviewRecord> pageData = new Page<>(page, limit);
		QueryWrapper<TReviewRecord> queryWrapper = new QueryWrapper<>();
		if (tReviewRecord !=null) {
			queryWrapper = LikeAllFeild(tReviewRecord,searchVo);
		}
		IPage<TReviewRecord> result = tReviewRecordMapper.selectPage(pageData, queryWrapper);
		return  result;
	}
	@Override
	public List<TReviewRecord> queryTReviewRecordList(TReviewRecord  tReviewRecord, SearchVo searchVo){
		QueryWrapper<TReviewRecord> queryWrapper = new QueryWrapper<>();
		if (tReviewRecord !=null) {
			queryWrapper = LikeAllFeild(tReviewRecord,searchVo);
		}
		List<TReviewRecord> result = tReviewRecordMapper.selectList( queryWrapper);
		return  result;
	}
	@Override
	public void download(TReviewRecord tReviewRecord, HttpServletResponse response) {
		List<Map<String, Object>> mapList = new ArrayList<>();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		QueryWrapper<TReviewRecord> queryWrapper = new QueryWrapper<>();
		if (tReviewRecord !=null) {
			queryWrapper = LikeAllFeild(tReviewRecord,null);
		}
		List<TReviewRecord> list = tReviewRecordMapper.selectList(queryWrapper);
		for (TReviewRecord re : list) {
			Map<String, Object> map = new LinkedHashMap<>();
			mapList.add(map);
		}
		FileUtil.createExcel(mapList, "exel.xlsx", response);
	}

	@Override
	public List<TReviewRecord> getCheckProjectByPersonId(String personId) {
		return tReviewRecordMapper.getCheckProjectByPersonId(personId);
	}

	/**
	* 功能描述：构建模糊查询
	* @param tReviewRecord 需要模糊查询的信息
	* @return 返回查询
	*/
	public QueryWrapper<TReviewRecord>  LikeAllFeild(TReviewRecord  tReviewRecord, SearchVo searchVo) {
		QueryWrapper<TReviewRecord> queryWrapper = new QueryWrapper<>();
		if(StringUtils.isNotBlank(tReviewRecord.getId())){
			queryWrapper.lambda().and(i -> i.eq(TReviewRecord::getId, tReviewRecord.getId()));
		}
		if(StringUtils.isNotBlank(tReviewRecord.getPersonId())){
			queryWrapper.lambda().and(i -> i.eq(TReviewRecord::getPersonId, tReviewRecord.getPersonId()));
		}
		if(StringUtils.isNotBlank(tReviewRecord.getMobile())){
			queryWrapper.lambda().and(i -> i.like(TReviewRecord::getMobile, tReviewRecord.getMobile()));
		}
		if(StringUtils.isNotBlank(tReviewRecord.getOfficeId())){
			queryWrapper.lambda().and(i -> i.eq(TReviewRecord::getOfficeId, tReviewRecord.getOfficeId()));
		}
		if(StringUtils.isNotBlank(tReviewRecord.getCheckProjectId())){
			queryWrapper.lambda().and(i -> i.eq(TReviewRecord::getCheckProjectId, tReviewRecord.getCheckProjectId()));
		}
		if(StringUtils.isNotBlank(tReviewRecord.getCheckProjectName())){
			queryWrapper.lambda().and(i -> i.like(TReviewRecord::getCheckProjectName, tReviewRecord.getCheckProjectName()));
		}
		if(StringUtils.isNotBlank(tReviewRecord.getReviewExplain())){
			queryWrapper.lambda().and(i -> i.like(TReviewRecord::getReviewExplain, tReviewRecord.getReviewExplain()));
		}
		if(StringUtils.isNotBlank(tReviewRecord.getOperateDoctor())){
			queryWrapper.lambda().and(i -> i.like(TReviewRecord::getOperateDoctor, tReviewRecord.getOperateDoctor()));
		}
		if(tReviewRecord.getReviewTime() != null){
			queryWrapper.lambda().and(i -> i.like(TReviewRecord::getReviewTime, tReviewRecord.getReviewTime()));
		}
		if(tReviewRecord.getDelFlag() != null){
			queryWrapper.lambda().and(i -> i.like(TReviewRecord::getDelFlag, tReviewRecord.getDelFlag()));
		}
		if(StringUtils.isNotBlank(tReviewRecord.getCreateId())){
			queryWrapper.lambda().and(i -> i.like(TReviewRecord::getCreateId, tReviewRecord.getCreateId()));
		}
		if(tReviewRecord.getCreateTime() != null){
			queryWrapper.lambda().and(i -> i.like(TReviewRecord::getCreateTime, tReviewRecord.getCreateTime()));
		}
		if(StringUtils.isNotBlank(tReviewRecord.getUpdateId())){
			queryWrapper.lambda().and(i -> i.like(TReviewRecord::getUpdateId, tReviewRecord.getUpdateId()));
		}
		if(tReviewRecord.getUpdateTime() != null){
			queryWrapper.lambda().and(i -> i.like(TReviewRecord::getUpdateTime, tReviewRecord.getUpdateTime()));
		}
		if(tReviewRecord.getState() != null){
			queryWrapper.lambda().and(i -> i.like(TReviewRecord::getState, tReviewRecord.getState()));
		}
		if(StringUtils.isNotBlank(tReviewRecord.getGroupUnitId())){
			queryWrapper.lambda().and(i -> i.like(TReviewRecord::getGroupUnitId, tReviewRecord.getGroupUnitId()));
		}
		if(StringUtils.isNotBlank(tReviewRecord.getGroupUnitName())){
			queryWrapper.lambda().and(i -> i.like(TReviewRecord::getGroupUnitName, tReviewRecord.getGroupUnitName()));
		}
		if(StringUtils.isNotBlank(tReviewRecord.getGroupOrderId())){
			queryWrapper.lambda().and(i -> i.like(TReviewRecord::getGroupOrderId, tReviewRecord.getGroupOrderId()));
		}
		if(searchVo!=null){
			if(searchVo.getStartDate()!=null && searchVo.getEndDate()!=null){
				queryWrapper.lambda().and(i -> i.between(TReviewRecord::getCreateTime, searchVo.getStartDate(),searchVo.getEndDate()));
			}
		}
		queryWrapper.lambda().and(i -> i.eq(TReviewRecord::getDelFlag, 0));
		return queryWrapper;

}
}
