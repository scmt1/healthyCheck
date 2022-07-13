package com.scmt.healthy.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.scmt.healthy.entity.TInspectionRecord;
import com.scmt.healthy.service.ITInspectionRecordService;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.mapper.TInspectionRecordMapper;
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
public class TInspectionRecordServiceImpl extends ServiceImpl<TInspectionRecordMapper, TInspectionRecord> implements ITInspectionRecordService {
	@Autowired
	@SuppressWarnings("SpringJavaAutowiringInspection")
	private TInspectionRecordMapper tInspectionRecordMapper;

	@Override
	public IPage<TInspectionRecord> queryTInspectionRecordListByPage(TInspectionRecord  tInspectionRecord, SearchVo searchVo, PageVo pageVo){
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
		Page<TInspectionRecord> pageData = new Page<>(page, limit);
		QueryWrapper<TInspectionRecord> queryWrapper = new QueryWrapper<>();
		if (tInspectionRecord !=null) {
			queryWrapper = LikeAllFeild(tInspectionRecord,searchVo);
		}
		IPage<TInspectionRecord> result = tInspectionRecordMapper.selectPage(pageData, queryWrapper);
		return  result;
	}
	@Override
	public void download(TInspectionRecord tInspectionRecord, HttpServletResponse response) {
		List<Map<String, Object>> mapList = new ArrayList<>();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		QueryWrapper<TInspectionRecord> queryWrapper = new QueryWrapper<>();
		if (tInspectionRecord !=null) {
			queryWrapper = LikeAllFeild(tInspectionRecord,null);
		}
		List<TInspectionRecord> list = tInspectionRecordMapper.selectList(queryWrapper);
		for (TInspectionRecord re : list) {
			Map<String, Object> map = new LinkedHashMap<>();
			mapList.add(map);
		}
		FileUtil.createExcel(mapList, "exel.xlsx", response);
	}

	@Override
	public TInspectionRecord getByPersonId(String personId) {
		return tInspectionRecordMapper.getByPersonId(personId);
	}

	/**
	* 功能描述：构建模糊查询
	* @param tInspectionRecord 需要模糊查询的信息
	* @return 返回查询
	*/
	public QueryWrapper<TInspectionRecord>  LikeAllFeild(TInspectionRecord  tInspectionRecord, SearchVo searchVo) {
		QueryWrapper<TInspectionRecord> queryWrapper = new QueryWrapper<>();
		if(StringUtils.isNotBlank(tInspectionRecord.getId())){
			queryWrapper.lambda().and(i -> i.like(TInspectionRecord::getId, tInspectionRecord.getId()));
		}
		if(StringUtils.isNotBlank(tInspectionRecord.getPersonId())){
			queryWrapper.lambda().and(i -> i.like(TInspectionRecord::getPersonId, tInspectionRecord.getPersonId()));
		}
		if(StringUtils.isNotBlank(tInspectionRecord.getInspectionDoctor())){
			queryWrapper.lambda().and(i -> i.like(TInspectionRecord::getInspectionDoctor, tInspectionRecord.getInspectionDoctor()));
		}
		if(tInspectionRecord.getInspectionDate() != null){
			queryWrapper.lambda().and(i -> i.like(TInspectionRecord::getInspectionDate, tInspectionRecord.getInspectionDate()));
		}
		if(StringUtils.isNotBlank(tInspectionRecord.getOperator())){
			queryWrapper.lambda().and(i -> i.like(TInspectionRecord::getOperator, tInspectionRecord.getOperator()));
		}
		if(StringUtils.isNotBlank(tInspectionRecord.getConclusion())){
			queryWrapper.lambda().and(i -> i.like(TInspectionRecord::getConclusion, tInspectionRecord.getConclusion()));
		}
		if(StringUtils.isNotBlank(tInspectionRecord.getHealthCertificateConditions())){
			queryWrapper.lambda().and(i -> i.like(TInspectionRecord::getHealthCertificateConditions, tInspectionRecord.getHealthCertificateConditions()));
		}
		if(tInspectionRecord.getDelFlag() != null){
			queryWrapper.lambda().and(i -> i.like(TInspectionRecord::getDelFlag, tInspectionRecord.getDelFlag()));
		}
		if(StringUtils.isNotBlank(tInspectionRecord.getCreateId())){
			queryWrapper.lambda().and(i -> i.like(TInspectionRecord::getCreateId, tInspectionRecord.getCreateId()));
		}
		if(tInspectionRecord.getCreateTime() != null){
			queryWrapper.lambda().and(i -> i.like(TInspectionRecord::getCreateTime, tInspectionRecord.getCreateTime()));
		}
		if(StringUtils.isNotBlank(tInspectionRecord.getUpdateId())){
			queryWrapper.lambda().and(i -> i.like(TInspectionRecord::getUpdateId, tInspectionRecord.getUpdateId()));
		}
		if(tInspectionRecord.getUpdateTime() != null){
			queryWrapper.lambda().and(i -> i.like(TInspectionRecord::getUpdateTime, tInspectionRecord.getUpdateTime()));
		}
		if(StringUtils.isNotBlank(tInspectionRecord.getDeleteId())){
			queryWrapper.lambda().and(i -> i.like(TInspectionRecord::getDeleteId, tInspectionRecord.getDeleteId()));
		}
		if(tInspectionRecord.getDeleteTime() != null){
			queryWrapper.lambda().and(i -> i.like(TInspectionRecord::getDeleteTime, tInspectionRecord.getDeleteTime()));
		}
		if(StringUtils.isNotBlank(tInspectionRecord.getMedicalAdvice())){
			queryWrapper.lambda().and(i -> i.like(TInspectionRecord::getMedicalAdvice, tInspectionRecord.getMedicalAdvice()));
		}
		if(StringUtils.isNotBlank(tInspectionRecord.getOtherCheckAbnormalResults())){
			queryWrapper.lambda().and(i -> i.like(TInspectionRecord::getOtherCheckAbnormalResults, tInspectionRecord.getOtherCheckAbnormalResults()));
		}
		if(StringUtils.isNotBlank(tInspectionRecord.getHandleOpinion())){
			queryWrapper.lambda().and(i -> i.like(TInspectionRecord::getHandleOpinion, tInspectionRecord.getHandleOpinion()));
		}
		if(StringUtils.isNotBlank(tInspectionRecord.getCareerCheckAbnormalResults())){
			queryWrapper.lambda().and(i -> i.like(TInspectionRecord::getCareerCheckAbnormalResults, tInspectionRecord.getCareerCheckAbnormalResults()));
		}
		if(StringUtils.isNotBlank(tInspectionRecord.getCareerIllnessName())){
			queryWrapper.lambda().and(i -> i.like(TInspectionRecord::getCareerIllnessName, tInspectionRecord.getCareerIllnessName()));
		}
		if(StringUtils.isNotBlank(tInspectionRecord.getOrderId())){
			queryWrapper.lambda().and(i -> i.like(TInspectionRecord::getOrderId, tInspectionRecord.getOrderId()));
		}
		if(tInspectionRecord.getInspectionType() != null){
			queryWrapper.lambda().and(i -> i.like(TInspectionRecord::getInspectionType, tInspectionRecord.getInspectionType()));
		}
		if(searchVo!=null){
			if(searchVo.getStartDate()!=null && searchVo.getEndDate()!=null){
				queryWrapper.lambda().and(i -> i.between(TInspectionRecord::getCreateTime, searchVo.getStartDate(),searchVo.getEndDate()));
			}
		}
		queryWrapper.lambda().and(i -> i.eq(TInspectionRecord::getDelFlag, 0));
		return queryWrapper;

}
}
