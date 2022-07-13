package com.scmt.healthy.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.scmt.healthy.entity.TGroupUnit;
import com.scmt.healthy.service.ITGroupUnitService;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.mapper.TGroupUnitMapper;
import com.scmt.core.utis.FileUtil;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
/**
 *@author
 **/
@Service
public class TGroupUnitServiceImpl extends ServiceImpl<TGroupUnitMapper, TGroupUnit> implements ITGroupUnitService {
	@Autowired
	@SuppressWarnings("SpringJavaAutowiringInspection")
	private TGroupUnitMapper tGroupUnitMapper;

	@Override
	public IPage<TGroupUnit> queryTGroupUnitListByPage(TGroupUnit  tGroupUnit, SearchVo searchVo, PageVo pageVo){
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
		Page<TGroupUnit> pageData = new Page<>(page, limit);
		QueryWrapper<TGroupUnit> queryWrapper = new QueryWrapper<>();
		if (tGroupUnit !=null) {
			queryWrapper = LikeAllFeild(tGroupUnit,searchVo);
		}
		IPage<TGroupUnit> result = tGroupUnitMapper.selectPage(pageData, queryWrapper);
		return  result;
	}
	@Override
	public void download(TGroupUnit tGroupUnit, HttpServletResponse response) {
		List<Map<String, Object>> mapList = new ArrayList<>();
		QueryWrapper<TGroupUnit> queryWrapper = new QueryWrapper<>();
		if (tGroupUnit !=null) {
			queryWrapper = LikeAllFeild(tGroupUnit,null);
		}
		List<TGroupUnit> list = tGroupUnitMapper.selectList(queryWrapper);
		for (TGroupUnit re : list) {
			Map<String, Object> map = new LinkedHashMap<>();
			map.put("单位名称", re.getName());
			map.put("统一社和信用代码", re.getUscc());
			map.put("法人姓名", re.getLegalPerson());
			map.put("注册资金（万元）", re.getRegCapital());
			map.put("成立日期", re.getEstablishmentDate());
			map.put("行业类型", re.getIndustryCode());
			map.put("企业规模", re.getBusinessScaleCode());
			map.put("经济类型", re.getEconomicTypeCode());
			map.put("工商注册地址", re.getAddress());
			map.put("附件信息", re.getAttachment());
			map.put("行业类型名称", re.getIndustryName());
			map.put("企业规模名称", re.getBusinessScaleName());
			map.put("经济类型名称", re.getEconomicTypeName());
			map.put("所属地区编码", re.getRegionCode());
			map.put("所属地区地址", re.getRegionName());
			map.put("职工人数", re.getEmployeesNum());
			map.put("接触职业病危害因素人数", re.getDangerNum());
			map.put("法人电话", re.getLegalPhone());
			map.put("生产工人数", re.getWorkmanNum());
			map.put("接触职业病危害因素女工人数", re.getWorkmistressNum());
			map.put("单位注册邮编", re.getPostalCode());
			map.put("经营面积", re.getWorkArea());
			map.put("建档日期", re.getFilingDate());
			map.put("检测联系人", re.getLinkMan1());
			map.put("检测联系人职务", re.getPosition1());
			map.put("检测联系电话", re.getLinkPhone1());
			map.put("体检联系人", re.getLinkMan2());
			map.put("体检联系人职务", re.getPosition2());
			map.put("体检联系人电话", re.getLinkPhone2());
			map.put("职业卫生安全负责人", re.getSafetyPrincipal());
			map.put("安全联系人职务", re.getSafePosition());
			map.put("安全联系人电话", re.getSafePhone());
			map.put("隶属关系", re.getSubjeConn());
			map.put("作业场所地址", re.getEnrolAddress());
			map.put("作业场所邮编", re.getEnrolPostalCode());
			map.put("职业卫生管理机构", re.getOccManaOffice());
			mapList.add(map);
		}
		FileUtil.createExcel(mapList, "exel.xlsx", response);
	}

	/**
	* 功能描述：构建模糊查询
	* @param tGroupUnit 需要模糊查询的信息
	* @return 返回查询
	*/
	public QueryWrapper<TGroupUnit>  LikeAllFeild(TGroupUnit  tGroupUnit, SearchVo searchVo) {
		QueryWrapper<TGroupUnit> queryWrapper = new QueryWrapper<>();
		if(StringUtils.isNotBlank(tGroupUnit.getId())){
			queryWrapper.lambda().and(i -> i.like(TGroupUnit::getId, tGroupUnit.getId()));
		}
		if(StringUtils.isNotBlank(tGroupUnit.getName())){
			queryWrapper.lambda().and(i -> i.like(TGroupUnit::getName, tGroupUnit.getName()));
		}
		if(StringUtils.isNotBlank(tGroupUnit.getPhysicalType())){
			queryWrapper.lambda().and(i -> i.like(TGroupUnit::getPhysicalType, tGroupUnit.getPhysicalType()));
		}
		if(StringUtils.isNotBlank(tGroupUnit.getUscc())){
			queryWrapper.lambda().and(i -> i.like(TGroupUnit::getUscc, tGroupUnit.getUscc()));
		}
		if(StringUtils.isNotBlank(tGroupUnit.getLegalPerson())){
			queryWrapper.lambda().and(i -> i.like(TGroupUnit::getLegalPerson, tGroupUnit.getLegalPerson()));
		}
		if(tGroupUnit.getRegCapital() != null){
			queryWrapper.lambda().and(i -> i.like(TGroupUnit::getRegCapital, tGroupUnit.getRegCapital()));
		}
		if(tGroupUnit.getEstablishmentDate() != null){
			queryWrapper.lambda().and(i -> i.like(TGroupUnit::getEstablishmentDate, tGroupUnit.getEstablishmentDate()));
		}

		if(StringUtils.isNotBlank(tGroupUnit.getIndustryCode())){
			queryWrapper.lambda().and(i -> i.like(TGroupUnit::getIndustryCode, tGroupUnit.getIndustryCode()));
		}
		if(StringUtils.isNotBlank(tGroupUnit.getBusinessScaleCode())){
			queryWrapper.lambda().and(i -> i.like(TGroupUnit::getBusinessScaleCode, tGroupUnit.getBusinessScaleCode()));
		}
		if(StringUtils.isNotBlank(tGroupUnit.getEconomicTypeCode())){
			queryWrapper.lambda().and(i -> i.like(TGroupUnit::getEconomicTypeCode, tGroupUnit.getEconomicTypeCode()));
		}

		if(StringUtils.isNotBlank(tGroupUnit.getAddress())){
			queryWrapper.lambda().and(i -> i.like(TGroupUnit::getAddress, tGroupUnit.getAddress()));
		}

		if(StringUtils.isNotBlank(tGroupUnit.getDepartmentId())){
			queryWrapper.lambda().and(i -> i.like(TGroupUnit::getDepartmentId, tGroupUnit.getDepartmentId()));
		}

		if(StringUtils.isNotBlank(tGroupUnit.getCreateId())){
			queryWrapper.lambda().and(i -> i.like(TGroupUnit::getCreateId, tGroupUnit.getCreateId()));
		}
		if(tGroupUnit.getCreateTime() != null){
			queryWrapper.lambda().and(i -> i.like(TGroupUnit::getCreateTime, tGroupUnit.getCreateTime()));
		}
		if(StringUtils.isNotBlank(tGroupUnit.getUpdateId())){
			queryWrapper.lambda().and(i -> i.like(TGroupUnit::getUpdateId, tGroupUnit.getUpdateId()));
		}
		if(tGroupUnit.getUpdateTime() != null){
			queryWrapper.lambda().and(i -> i.like(TGroupUnit::getUpdateTime, tGroupUnit.getUpdateTime()));
		}
		if(StringUtils.isNotBlank(tGroupUnit.getDeleteId())){
			queryWrapper.lambda().and(i -> i.like(TGroupUnit::getDeleteId, tGroupUnit.getDeleteId()));
		}
		if(tGroupUnit.getDeleteTime() != null){
			queryWrapper.lambda().and(i -> i.like(TGroupUnit::getDeleteTime, tGroupUnit.getDeleteTime()));
		}
		if(StringUtils.isNotBlank(tGroupUnit.getLinkMan2())){
			queryWrapper.lambda().and(i -> i.like(TGroupUnit::getLinkMan2, tGroupUnit.getLinkMan2()));
		}
		if(StringUtils.isNotBlank(tGroupUnit.getLinkPhone2())){
			queryWrapper.lambda().and(i -> i.like(TGroupUnit::getLinkPhone2, tGroupUnit.getLinkPhone2()));
		}
		if(searchVo!=null){
			if(searchVo.getStartDate()!=null && searchVo.getEndDate()!=null){
				queryWrapper.lambda().and(i -> i.between(TGroupUnit::getCreateTime, searchVo.getStartDate(),searchVo.getEndDate()));
			}
		}
		queryWrapper.lambda().and(i -> i.eq(TGroupUnit::getDelFlag, 0));
		return queryWrapper;

}
}
