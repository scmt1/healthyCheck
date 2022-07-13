package com.scmt.healthy.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.scmt.healthy.entity.TGroupPerson;
import com.scmt.healthy.mapper.TProTypeMapper;
import com.scmt.healthy.entity.TCombo;
import com.scmt.healthy.service.ITComboService;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.mapper.TComboMapper;
import com.scmt.core.utis.FileUtil;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.text.SimpleDateFormat;

/**
 *@author
 **/
@Service
public class TComboServiceImpl extends ServiceImpl<TComboMapper, TCombo> implements ITComboService {
	@Autowired
	@SuppressWarnings("SpringJavaAutowiringInspection")
	private TComboMapper tComboMapper;

	@Override
	public IPage<TCombo> queryTComboListByPage(TCombo  tCombo, SearchVo searchVo, PageVo pageVo){
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
		Page<TCombo> pageData = new Page<>(page, limit);
		QueryWrapper<TCombo> queryWrapper = new QueryWrapper<>();
		if (tCombo !=null) {
			queryWrapper = LikeAllFeild(tCombo,searchVo);
		}
		//queryWrapper.orderByDesc("create_time");
		queryWrapper.orderByAsc("order_num");//名称排序
		IPage<TCombo> result = tComboMapper.selectPage(pageData, queryWrapper);
		return  result;
	}
	@Override
	public void download(TCombo tCombo, HttpServletResponse response) {
		List<Map<String, Object>> mapList = new ArrayList<>();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		QueryWrapper<TCombo> queryWrapper = new QueryWrapper<>();
		if (tCombo !=null) {
			queryWrapper = LikeAllFeild(tCombo,null);
		}
		List<TCombo> list = tComboMapper.selectList(queryWrapper);
		for (TCombo re : list) {
			Map<String, Object> map = new LinkedHashMap<>();
			map.put("套餐名称", re.getName());
			map.put("封面图片地址", re.getUrl());
			map.put("套餐类别", re.getType());
			map.put("简拼", re.getSimpleSpell());
			map.put("适合性别", re.getFitSex());
			map.put("排序", re.getOrderNum());
			map.put("套餐介绍", re.getRemark());
			mapList.add(map);
		}
		FileUtil.createExcel(mapList, "exel.xlsx", response);
	}

	@Override
	public IPage<TCombo> queryTComboAndItemList(TCombo tCombo, PageVo pageVo) {
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
		Page<TCombo> pageData = new Page<>(page, limit);
		QueryWrapper<TCombo> queryWrapper = new QueryWrapper<>();
		if (tCombo !=null) {
//			queryWrapper = LikeAllFeild(tCombo,null);
			queryWrapper.eq("t_combo.del_flag",0);
			if(tCombo.getType() != null && tCombo.getType().trim().length() > 0){
				queryWrapper.eq("t_combo.type",tCombo.getType());
			}
			if(tCombo.getFitSex() != null && tCombo.getFitSex().trim().length() > 0){
				queryWrapper.eq("t_combo.fit_sex",tCombo.getFitSex());
			}
			if(tCombo.getCareerStage() != null && tCombo.getCareerStage().trim().length() > 0){
				queryWrapper.eq("t_combo.career_stage",tCombo.getCareerStage());
			}
			if(tCombo.getName() != null && tCombo.getName().trim().length() > 0){
				queryWrapper.like("t_combo.name",tCombo.getName());
			}
			queryWrapper.groupBy("t_combo.id");
			queryWrapper.orderByDesc("t_combo.create_time");
		}
		IPage<TCombo> result = tComboMapper.queryTComboAndItemList(queryWrapper, pageData);
		return result;
	}

	@Override
	public  List<TCombo>  getItemById(String id) {

		List<TCombo>  tCombo=tComboMapper.tComboMapper(id);
		return tCombo;
	}

	@Override
	public   List<TCombo> getTComboById(String[] ids) {
		List<TCombo> list=new ArrayList<TCombo>();
		//List<String> itemIdlist=new ArrayList<>();
		Set<String>set=new HashSet<>();
		Integer sumprice=0;
		List<String> comboIds = new ArrayList<>();
		for (String id : ids) {
			comboIds.add(id);
			TCombo tCombo=tComboMapper.getTComboById(id);
			list.add(tCombo);

			List<TCombo> tComboItems =tComboMapper.getTComboItem(id);
			for (TCombo tComboItem : tComboItems) {
				String itemId = tComboItem.getId();
				set.add(itemId);
			}
		}
		/*QueryWrapper<TCombo> queryWrapper = new QueryWrapper<>();
		queryWrapper.in("",comboIds);
		tComboMapper.selectList(queryWrapper);*/

		for (String itemId: set) {
			Integer price=tComboMapper.findItemPrice(itemId);
			sumprice=price+sumprice;
		}

		for (TCombo tCombo : list) {
			tCombo.setSumPrice(sumprice);
		}
		return list;
	}

	@Override
	public TCombo getTComboByPersonId(String personId,String hazardFactors,String content) {
		return tComboMapper.getTComboByPersonId(personId,hazardFactors,content);
	}

	@Override
	public List<TCombo> gethazardFactorsByGroupId(String groupId) {
		return tComboMapper.gethazardFactorsByGroupId(groupId);
	}

	/**
	* 功能描述：构建模糊查询
	* @param tCombo 需要模糊查询的信息
	* @return 返回查询
	*/
	public QueryWrapper<TCombo>  LikeAllFeild(TCombo  tCombo, SearchVo searchVo) {
		QueryWrapper<TCombo> queryWrapper = new QueryWrapper<>();
		if(StringUtils.isNotBlank(tCombo.getId())){
			queryWrapper.lambda().and(i -> i.like(TCombo::getId, tCombo.getId()));
		}
		if(StringUtils.isNotBlank(tCombo.getName())){
			queryWrapper.lambda().and(i -> i.like(TCombo::getName, tCombo.getName()));
		}
		if(StringUtils.isNotBlank(tCombo.getCareerStage())){
			queryWrapper.lambda().and(i -> i.eq(TCombo::getCareerStage, tCombo.getCareerStage()));
		}
		if(StringUtils.isNotBlank(tCombo.getUrl())){
			queryWrapper.lambda().and(i -> i.eq(TCombo::getUrl, tCombo.getUrl()));
		}
		if(StringUtils.isNotBlank(tCombo.getType())){
			queryWrapper.lambda().and(i -> i.eq(TCombo::getType, tCombo.getType()));
		}
		if(StringUtils.isNotBlank(tCombo.getSimpleSpell())){
			queryWrapper.lambda().and(i -> i.like(TCombo::getSimpleSpell, tCombo.getSimpleSpell()));
		}
		if(StringUtils.isNotBlank(tCombo.getFitSex())){
			queryWrapper.lambda().and(i -> i.like(TCombo::getFitSex, tCombo.getFitSex()));
		}
		if(tCombo.getOrderNum() != null){
			queryWrapper.lambda().and(i -> i.like(TCombo::getOrderNum, tCombo.getOrderNum()));
		}
		if(StringUtils.isNotBlank(tCombo.getRemark())){
			queryWrapper.lambda().and(i -> i.like(TCombo::getRemark, tCombo.getRemark()));
		}
		if(tCombo.getDelFlag() != null){
			queryWrapper.lambda().and(i -> i.like(TCombo::getDelFlag, tCombo.getDelFlag()));
		}
		if(StringUtils.isNotBlank(tCombo.getCreateId())){
			queryWrapper.lambda().and(i -> i.like(TCombo::getCreateId, tCombo.getCreateId()));
		}
		if(tCombo.getCreateTime() != null){
			queryWrapper.lambda().and(i -> i.like(TCombo::getCreateTime, tCombo.getCreateTime()));
		}
		if(StringUtils.isNotBlank(tCombo.getUpdateId())){
			queryWrapper.lambda().and(i -> i.like(TCombo::getUpdateId, tCombo.getUpdateId()));
		}
		if(tCombo.getUpdateTime() != null){
			queryWrapper.lambda().and(i -> i.like(TCombo::getUpdateTime, tCombo.getUpdateTime()));
		}
		if(StringUtils.isNotBlank(tCombo.getDeleteId())){
			queryWrapper.lambda().and(i -> i.like(TCombo::getDeleteId, tCombo.getDeleteId()));
		}
		if(tCombo.getDeleteTime() != null){
			queryWrapper.lambda().and(i -> i.like(TCombo::getDeleteTime, tCombo.getDeleteTime()));
		}
		if(searchVo!=null){
			if(searchVo.getStartDate()!=null && searchVo.getEndDate()!=null){
				queryWrapper.lambda().and(i -> i.between(TCombo::getCreateTime, searchVo.getStartDate(),searchVo.getEndDate()));
			}
		}
		queryWrapper.lambda().and(i -> i.eq(TCombo::getDelFlag, 0));
		return queryWrapper;

}
}
