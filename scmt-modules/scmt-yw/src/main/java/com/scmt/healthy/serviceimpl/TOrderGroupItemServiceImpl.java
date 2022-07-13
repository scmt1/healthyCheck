package com.scmt.healthy.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.scmt.healthy.entity.TOrderGroupItem;
import com.scmt.healthy.entity.TOrderGroupItemProject;
import com.scmt.healthy.service.ITOrderGroupItemService;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.mapper.TOrderGroupItemMapper;
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
public class TOrderGroupItemServiceImpl extends ServiceImpl<TOrderGroupItemMapper, TOrderGroupItem> implements ITOrderGroupItemService {
	@Autowired
	@SuppressWarnings("SpringJavaAutowiringInspection")
	private TOrderGroupItemMapper tOrderGroupItemMapper;

	@Override
	public IPage<TOrderGroupItem> queryTOrderGroupItemListByPage(TOrderGroupItem  tOrderGroupItem, SearchVo searchVo, PageVo pageVo){
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
		Page<TOrderGroupItem> pageData = new Page<>(page, limit);
		QueryWrapper<TOrderGroupItem> queryWrapper = new QueryWrapper<>();
		if (tOrderGroupItem !=null) {
			queryWrapper = LikeAllFeild(tOrderGroupItem,searchVo);
		}
		IPage<TOrderGroupItem> result = tOrderGroupItemMapper.selectPage(pageData, queryWrapper);
		return  result;
	}
	@Override
	public void download(TOrderGroupItem tOrderGroupItem, HttpServletResponse response) {
		List<Map<String, Object>> mapList = new ArrayList<>();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		QueryWrapper<TOrderGroupItem> queryWrapper = new QueryWrapper<>();
		if (tOrderGroupItem !=null) {
			queryWrapper = LikeAllFeild(tOrderGroupItem,null);
		}
		List<TOrderGroupItem> list = tOrderGroupItemMapper.selectList(queryWrapper);
		for (TOrderGroupItem re : list) {
			Map<String, Object> map = new LinkedHashMap<>();
			mapList.add(map);
		}
		FileUtil.createExcel(mapList, "exel.xlsx", response);
	}

	@Override
	public List<TOrderGroupItem> queryDataListByGroupId(QueryWrapper<TOrderGroupItem> queryWrapper, String personId) {
		return tOrderGroupItemMapper.queryDataListByGroupId(queryWrapper, personId);
	}

	@Override
	public Integer getAllCheckCount(String personId, String groupId) {
		return tOrderGroupItemMapper.getAllCheckCount(personId, groupId);
	}

	@Override
	public Integer getDepartResultCount(String personId, String groupId) {
		return tOrderGroupItemMapper.getDepartResultCount(personId,groupId);
	}

	@Override
	public List<TOrderGroupItem> listByQueryWrapper(TOrderGroupItem orderGroupItem) {
		return tOrderGroupItemMapper.listByQueryWrapper(orderGroupItem);
	}

	@Override
	public List<TOrderGroupItem> queryOrderGroupItemList(String groupOrderId,String groupId) {
		return tOrderGroupItemMapper.queryOrderGroupItemList(groupOrderId,groupId);
	}

	/**
	* 功能描述：构建模糊查询
	* @param tOrderGroupItem 需要模糊查询的信息
	* @return 返回查询
	*/
	public QueryWrapper<TOrderGroupItem>  LikeAllFeild(TOrderGroupItem  tOrderGroupItem, SearchVo searchVo) {
		QueryWrapper<TOrderGroupItem> queryWrapper = new QueryWrapper<>();
		if(StringUtils.isNotBlank(tOrderGroupItem.getId())){
			queryWrapper.lambda().and(i -> i.like(TOrderGroupItem::getId, tOrderGroupItem.getId()));
		}
		if(StringUtils.isNotBlank(tOrderGroupItem.getName())){
			queryWrapper.lambda().and(i -> i.like(TOrderGroupItem::getName, tOrderGroupItem.getName()));
		}
		if(StringUtils.isNotBlank(tOrderGroupItem.getShortName())){
			queryWrapper.lambda().and(i -> i.like(TOrderGroupItem::getShortName, tOrderGroupItem.getShortName()));
		}
		if(tOrderGroupItem.getOrderNum() != null){
			queryWrapper.lambda().and(i -> i.like(TOrderGroupItem::getOrderNum, tOrderGroupItem.getOrderNum()));
		}
		if(tOrderGroupItem.getSalePrice() != null){
			queryWrapper.lambda().and(i -> i.like(TOrderGroupItem::getSalePrice, tOrderGroupItem.getSalePrice()));
		}
		if(tOrderGroupItem.getDiscount() != null){
			queryWrapper.lambda().and(i -> i.like(TOrderGroupItem::getDiscount, tOrderGroupItem.getDiscount()));
		}
		if(tOrderGroupItem.getDiscountPrice() != null){
			queryWrapper.lambda().and(i -> i.like(TOrderGroupItem::getDiscountPrice, tOrderGroupItem.getDiscountPrice()));
		}
		if(StringUtils.isNotBlank(tOrderGroupItem.getSuitableRange())){
			queryWrapper.lambda().and(i -> i.like(TOrderGroupItem::getSuitableRange, tOrderGroupItem.getSuitableRange()));
		}
		if(StringUtils.isNotBlank(tOrderGroupItem.getIntroduce())){
			queryWrapper.lambda().and(i -> i.like(TOrderGroupItem::getIntroduce, tOrderGroupItem.getIntroduce()));
		}
		if(StringUtils.isNotBlank(tOrderGroupItem.getAddress())){
			queryWrapper.lambda().and(i -> i.like(TOrderGroupItem::getAddress, tOrderGroupItem.getAddress()));
		}
		if(StringUtils.isNotBlank(tOrderGroupItem.getRemark())){
			queryWrapper.lambda().and(i -> i.like(TOrderGroupItem::getRemark, tOrderGroupItem.getRemark()));
		}
		if(tOrderGroupItem.getDelFlag() != null){
			queryWrapper.lambda().and(i -> i.like(TOrderGroupItem::getDelFlag, tOrderGroupItem.getDelFlag()));
		}
		if(StringUtils.isNotBlank(tOrderGroupItem.getCreateId())){
			queryWrapper.lambda().and(i -> i.like(TOrderGroupItem::getCreateId, tOrderGroupItem.getCreateId()));
		}
		if(tOrderGroupItem.getCreateTime() != null){
			queryWrapper.lambda().and(i -> i.like(TOrderGroupItem::getCreateTime, tOrderGroupItem.getCreateTime()));
		}
		if(StringUtils.isNotBlank(tOrderGroupItem.getUpdateId())){
			queryWrapper.lambda().and(i -> i.like(TOrderGroupItem::getUpdateId, tOrderGroupItem.getUpdateId()));
		}
		if(tOrderGroupItem.getUpdateTime() != null){
			queryWrapper.lambda().and(i -> i.like(TOrderGroupItem::getUpdateTime, tOrderGroupItem.getUpdateTime()));
		}
		if(StringUtils.isNotBlank(tOrderGroupItem.getDeleteId())){
			queryWrapper.lambda().and(i -> i.like(TOrderGroupItem::getDeleteId, tOrderGroupItem.getDeleteId()));
		}
		if(tOrderGroupItem.getDeleteTime() != null){
			queryWrapper.lambda().and(i -> i.like(TOrderGroupItem::getDeleteTime, tOrderGroupItem.getDeleteTime()));
		}
		if(StringUtils.isNotBlank(tOrderGroupItem.getDepartmentId())){
			queryWrapper.lambda().and(i -> i.like(TOrderGroupItem::getDepartmentId, tOrderGroupItem.getDepartmentId()));
		}
		if(StringUtils.isNotBlank(tOrderGroupItem.getTemplate())){
			queryWrapper.lambda().and(i -> i.like(TOrderGroupItem::getTemplate, tOrderGroupItem.getTemplate()));
		}
		if(StringUtils.isNotBlank(tOrderGroupItem.getServiceType())){
			queryWrapper.lambda().and(i -> i.like(TOrderGroupItem::getServiceType, tOrderGroupItem.getServiceType()));
		}
		if(StringUtils.isNotBlank(tOrderGroupItem.getSpecimen())){
			queryWrapper.lambda().and(i -> i.like(TOrderGroupItem::getSpecimen, tOrderGroupItem.getSpecimen()));
		}
		if(StringUtils.isNotBlank(tOrderGroupItem.getDiagnostic())){
			queryWrapper.lambda().and(i -> i.like(TOrderGroupItem::getDiagnostic, tOrderGroupItem.getDiagnostic()));
		}
		if(StringUtils.isNotBlank(tOrderGroupItem.getGroupId())){
			queryWrapper.lambda().and(i -> i.like(TOrderGroupItem::getGroupId, tOrderGroupItem.getGroupId()));
		}
		if(StringUtils.isNotBlank(tOrderGroupItem.getPortfolioProjectId())){
			queryWrapper.lambda().and(i -> i.like(TOrderGroupItem::getPortfolioProjectId, tOrderGroupItem.getPortfolioProjectId()));
		}
		if(searchVo!=null){
			if(searchVo.getStartDate()!=null && searchVo.getEndDate()!=null){
				queryWrapper.lambda().and(i -> i.between(TOrderGroupItem::getCreateTime, searchVo.getStartDate(),searchVo.getEndDate()));
			}
		}
		queryWrapper.lambda().and(i -> i.eq(TOrderGroupItem::getDelFlag, 0));
		return queryWrapper;

}
}
