package com.scmt.healthy.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.scmt.healthy.entity.TLisData;
import com.scmt.healthy.service.ITLisDataService;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.mapper.TLisDataMapper;
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
public class TLisDataServiceImpl extends ServiceImpl<TLisDataMapper, TLisData> implements ITLisDataService {
	@Autowired
	@SuppressWarnings("SpringJavaAutowiringInspection")
	private TLisDataMapper tLisDataMapper;

	@Override
	public IPage<TLisData> queryTLisDataListByPage(TLisData  tLisData, SearchVo searchVo, PageVo pageVo){
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
		Page<TLisData> pageData = new Page<>(page, limit);
		QueryWrapper<TLisData> queryWrapper = new QueryWrapper<>();
		if (tLisData !=null) {
			queryWrapper = LikeAllField(tLisData,searchVo);
		}
		//默认创建时间倒序
		if (pageVo.getSort() != null) {
			if (pageVo.getSort().equals("asc")) {
				queryWrapper.orderByAsc("t_lis_data." + pageVo.getSort());
			} else {
				queryWrapper.orderByDesc("t_lis_data." + pageVo.getSort());
			}
		} else {
			queryWrapper.orderByDesc("t_lis_data.create_time");
		}
		IPage<TLisData> result = tLisDataMapper.selectPage(pageData, queryWrapper);
		return  result;
	}
	@Override
	public void download(TLisData tLisData, HttpServletResponse response) {
		List<Map<String, Object>> mapList = new ArrayList<>();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		QueryWrapper<TLisData> queryWrapper = new QueryWrapper<>();
		if (tLisData !=null) {
			queryWrapper = LikeAllField(tLisData,null);
		}
		List<TLisData> list = tLisDataMapper.selectList(queryWrapper);
		for (TLisData re : list) {
			Map<String, Object> map = new LinkedHashMap<>();
			map.put("编号", re.getCode());
			map.put("数据", re.getData());
			map.put("监测时间", re.getTime());
			map.put("设备厂商型号", re.getEquipmentManufacturerModel());
			map.put("备注", re.getRemark());
			mapList.add(map);
		}
		FileUtil.createExcel(mapList, "exel.xlsx", response);
	}

	/**
	* 功能描述：构建模糊查询
	* @param tLisData 需要模糊查询的信息
	* @return 返回查询
	*/
	public QueryWrapper<TLisData>  LikeAllField(TLisData  tLisData, SearchVo searchVo) {
		QueryWrapper<TLisData> queryWrapper = new QueryWrapper<>();
		if(StringUtils.isNotBlank(tLisData.getId())){
			queryWrapper.and(i -> i.like("t_lis_data.id", tLisData.getId()));
		}
		if(StringUtils.isNotBlank(tLisData.getCode())){
			queryWrapper.and(i -> i.like("t_lis_data.code", tLisData.getCode()));
		}
		if(tLisData.getData() != null){
			queryWrapper.and(i -> i.like("t_lis_data.data", tLisData.getData()));
		}
		if(tLisData.getTime() != null){
			queryWrapper.and(i -> i.like("t_lis_data.time", tLisData.getTime()));
		}
		if(StringUtils.isNotBlank(tLisData.getEquipmentManufacturerModel())){
			queryWrapper.and(i -> i.like("t_lis_data.equipment_manufacturer_model", tLisData.getEquipmentManufacturerModel()));
		}
		if(StringUtils.isNotBlank(tLisData.getType())){
			queryWrapper.and(i -> i.like("t_lis_data.type", tLisData.getType()));
		}
		if(StringUtils.isNotBlank(tLisData.getItem())){
			queryWrapper.and(i -> i.like("t_lis_data.item", tLisData.getItem()));
		}
		if(StringUtils.isNotBlank(tLisData.getPersonName())){
			queryWrapper.and(i -> i.like("t_lis_data.person_name", tLisData.getPersonName()));
		}
		if(StringUtils.isNotBlank(tLisData.getStatus())){
			queryWrapper.and(i -> i.like("t_lis_data.status", tLisData.getStatus()));
		}
		if(StringUtils.isNotBlank(tLisData.getRemark())){
			queryWrapper.and(i -> i.like("t_lis_data.remark", tLisData.getRemark()));
		}
		if(tLisData.getDelFlag() != null){
			queryWrapper.and(i -> i.like("t_lis_data.del_flag", tLisData.getDelFlag()));
		}
		if(StringUtils.isNotBlank(tLisData.getCreateId())){
			queryWrapper.and(i -> i.like("t_lis_data.create_id", tLisData.getCreateId()));
		}
		if(tLisData.getCreateTime() != null){
			queryWrapper.and(i -> i.like("t_lis_data.create_time", tLisData.getCreateTime()));
		}
		if(StringUtils.isNotBlank(tLisData.getUpdateId())){
			queryWrapper.and(i -> i.like("t_lis_data.update_id", tLisData.getUpdateId()));
		}
		if(tLisData.getUpdateTime() != null){
			queryWrapper.and(i -> i.like("t_lis_data.update_time", tLisData.getUpdateTime()));
		}
		if(StringUtils.isNotBlank(tLisData.getDeleteId())){
			queryWrapper.and(i -> i.like("t_lis_data.delete_id", tLisData.getDeleteId()));
		}
		if(tLisData.getDeleteTime() != null){
			queryWrapper.and(i -> i.like("t_lis_data.delete_time", tLisData.getDeleteTime()));
		}
		if(searchVo!=null){
			if(searchVo.getStartDate()!=null && searchVo.getEndDate()!=null){
				queryWrapper.lambda().and(i -> i.between(TLisData::getCreateTime, searchVo.getStartDate(),searchVo.getEndDate()));
			}
		}
		queryWrapper.lambda().and(i -> i.eq(TLisData::getDelFlag, 0));
		return queryWrapper;
	
}
}