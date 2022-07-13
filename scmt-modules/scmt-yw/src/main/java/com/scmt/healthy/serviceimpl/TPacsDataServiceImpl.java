package com.scmt.healthy.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.scmt.healthy.entity.TPacsData;
import com.scmt.healthy.service.ITPacsDataService;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.mapper.TPacsDataMapper;
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
public class TPacsDataServiceImpl extends ServiceImpl<TPacsDataMapper, TPacsData> implements ITPacsDataService {
	@Autowired
	@SuppressWarnings("SpringJavaAutowiringInspection")
	private TPacsDataMapper tPacsDataMapper;

	@Override
	public IPage<TPacsData> queryTPacsDataListByPage(TPacsData  tPacsData, SearchVo searchVo, PageVo pageVo){
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
		Page<TPacsData> pageData = new Page<>(page, limit);

		QueryWrapper<TPacsData> queryWrapper = new QueryWrapper<>();

		queryWrapper.orderByDesc("create_time");
		if (tPacsData !=null) {
			queryWrapper = LikeAllField(tPacsData,searchVo);
		}
		//默认创建时间倒序
		if (pageVo.getSort() != null) {
			if (pageVo.getSort().equals("asc")) {
				queryWrapper.orderByAsc("t_pacs_data." + pageVo.getSort());
			} else {
				queryWrapper.orderByDesc("t_pacs_data." + pageVo.getSort());
			}
		} else {
			queryWrapper.orderByDesc("t_pacs_data.create_time");
		}
		IPage<TPacsData> result = tPacsDataMapper.selectPage(pageData, queryWrapper);
		return  result;
	}
	@Override
	public void download(TPacsData tPacsData, HttpServletResponse response) {
		List<Map<String, Object>> mapList = new ArrayList<>();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		QueryWrapper<TPacsData> queryWrapper = new QueryWrapper<>();
		if (tPacsData !=null) {
			queryWrapper = LikeAllField(tPacsData,null);
		}
		List<TPacsData> list = tPacsDataMapper.selectList(queryWrapper);
		for (TPacsData re : list) {
			Map<String, Object> map = new LinkedHashMap<>();
			map.put("编号", re.getCode());
			map.put("数据", re.getData());
			map.put("监测时间", re.getTime());
			map.put("设备厂商型号", re.getEquipmentManufacturerModel());
			mapList.add(map);
		}
		FileUtil.createExcel(mapList, "exel.xlsx", response);
	}

	/**
	* 功能描述：构建模糊查询
	* @param tPacsData 需要模糊查询的信息
	* @return 返回查询
	*/
	public QueryWrapper<TPacsData>  LikeAllField(TPacsData  tPacsData, SearchVo searchVo) {
		QueryWrapper<TPacsData> queryWrapper = new QueryWrapper<>();
		if(StringUtils.isNotBlank(tPacsData.getId())){
			queryWrapper.and(i -> i.like("t_pacs_data.id", tPacsData.getId()));
		}
		if(StringUtils.isNotBlank(tPacsData.getCode())){
			queryWrapper.and(i -> i.like("t_pacs_data.code", tPacsData.getCode()));
		}
		if(tPacsData.getData() != null){
			queryWrapper.and(i -> i.like("t_pacs_data.data", tPacsData.getData()));
		}
		if(tPacsData.getTime() != null){
			queryWrapper.and(i -> i.like("t_pacs_data.time", tPacsData.getTime()));
		}
		if(StringUtils.isNotBlank(tPacsData.getEquipmentManufacturerModel())){
			queryWrapper.and(i -> i.like("t_pacs_data.equipment_manufacturer_model", tPacsData.getEquipmentManufacturerModel()));
		}
		if(StringUtils.isNotBlank(tPacsData.getType())){
			queryWrapper.and(i -> i.like("t_pacs_data.type", tPacsData.getType()));
		}
		if(StringUtils.isNotBlank(tPacsData.getItem())){
			queryWrapper.and(i -> i.like("t_pacs_data.item", tPacsData.getItem()));
		}
		if(StringUtils.isNotBlank(tPacsData.getPersonName())){
			queryWrapper.and(i -> i.like("t_pacs_data.person_name", tPacsData.getPersonName()));
		}
		if(StringUtils.isNotBlank(tPacsData.getStatus())){
			queryWrapper.and(i -> i.like("t_pacs_data.status", tPacsData.getStatus()));
		}
		if(StringUtils.isNotBlank(tPacsData.getRemark())){
			queryWrapper.and(i -> i.like("t_pacs_data.remark", tPacsData.getRemark()));
		}
		if(tPacsData.getDelFlag() != null){
			queryWrapper.and(i -> i.like("t_pacs_data.del_flag", tPacsData.getDelFlag()));
		}
		if(StringUtils.isNotBlank(tPacsData.getCreateId())){
			queryWrapper.and(i -> i.like("t_pacs_data.create_id", tPacsData.getCreateId()));
		}
		if(tPacsData.getCreateTime() != null){
			queryWrapper.and(i -> i.like("t_pacs_data.create_time", tPacsData.getCreateTime()));
		}
		if(StringUtils.isNotBlank(tPacsData.getUpdateId())){
			queryWrapper.and(i -> i.like("t_pacs_data.update_id", tPacsData.getUpdateId()));
		}
		if(tPacsData.getUpdateTime() != null){
			queryWrapper.and(i -> i.like("t_pacs_data.update_time", tPacsData.getUpdateTime()));
		}
		if(StringUtils.isNotBlank(tPacsData.getDeleteId())){
			queryWrapper.and(i -> i.like("t_pacs_data.delete_id", tPacsData.getDeleteId()));
		}
		if(tPacsData.getDeleteTime() != null){
			queryWrapper.and(i -> i.like("t_pacs_data.delete_time", tPacsData.getDeleteTime()));
		}
		if(searchVo!=null){
			if(searchVo.getStartDate()!=null && searchVo.getEndDate()!=null){
				queryWrapper.lambda().and(i -> i.between(TPacsData::getCreateTime, searchVo.getStartDate(),searchVo.getEndDate()));
			}
		}
		queryWrapper.lambda().and(i -> i.eq(TPacsData::getDelFlag, 0));
		return queryWrapper;
	
}
}