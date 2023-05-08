package com.scmt.healthy.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.scmt.healthy.entity.TDepartItemResult;
import com.scmt.healthy.entity.TDepartResult;
import com.scmt.healthy.entity.TReviewProject;
import com.scmt.healthy.entity.TTemplate;
import com.scmt.healthy.service.ITTemplateService;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.mapper.TTemplateMapper;
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
public class TTemplateServiceImpl extends ServiceImpl<TTemplateMapper, TTemplate> implements ITTemplateService {
	@Autowired
	@SuppressWarnings("SpringJavaAutowiringInspection")
	private TTemplateMapper tTemplateMapper;

	@Override
	public TTemplate getTemplateById(String id) {
		QueryWrapper<TTemplate> queryWrapper = new QueryWrapper<>();
		queryWrapper.and(i -> i.like("t_template.id",id));
		List<TTemplate> list = tTemplateMapper.selectByMyWrapper(queryWrapper);
		if(list.size()>0){
			return list.get(0);
		}
		return null;
	}

	@Override
	public IPage<TTemplate> queryTTemplateListByPage(TTemplate  tTemplate, SearchVo searchVo, PageVo pageVo){
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

		Page<TTemplate> pageData = new Page<>(page, limit);
		QueryWrapper<TTemplate> queryWrapper = new QueryWrapper<>();
		if (tTemplate !=null) {
			queryWrapper = LikeAllFeild(tTemplate,searchVo);
		}
		if(pageVo.getSort()!=null){
			if(pageVo.getOrder().equals("asc")){
				queryWrapper.orderByAsc(pageVo.getSort());
			}
			else{
				queryWrapper.orderByDesc(pageVo.getSort());
			}
		}
		else{
			queryWrapper.orderByDesc("create_time");
		}
		IPage<TTemplate> result = tTemplateMapper.selectByMyWrapper(queryWrapper,pageData);
		return  result;
	}
	@Override
	public void download(TTemplate tTemplate, HttpServletResponse response) {
		List<Map<String, Object>> mapList = new ArrayList<>();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		QueryWrapper<TTemplate> queryWrapper = new QueryWrapper<>();
		if (tTemplate !=null) {
			queryWrapper = LikeAllFeild(tTemplate,null);
		}
		List<TTemplate> list = tTemplateMapper.selectByMyWrapper(queryWrapper);
		for (TTemplate re : list) {
			Map<String, Object> map = new LinkedHashMap<>();
			map.put("组合项目id", re.getBaseProjectId());
			map.put("状态", re.getStatus());
			map.put("模板内容", re.getContent());
			map.put("排序", re.getOrderNum());
			mapList.add(map);
		}
		FileUtil.createExcel(mapList, "exel.xlsx", response);
	}

	@Override
	public List<TTemplate> queryAllTTemplateList(TTemplate tTemplate) {
		QueryWrapper<TTemplate> queryWrapper = new QueryWrapper<>();
		if (tTemplate !=null) {
			queryWrapper = LikeAllFeild(tTemplate,null);
		}
		List<TTemplate> list = tTemplateMapper.selectByMyWrapper(queryWrapper);
		return list;
	}

	@Override
	public List<TDepartResult> getDepartResultList(String personId,String groupId) {
		List<TDepartResult> list = tTemplateMapper.getDepartResultList(personId,groupId);
		return list;
	}

	@Override
	public List<TDepartItemResult> getDepartItemResultListByPersonIds(List<String> personIds,List<String> groupIds) {
		List<TDepartItemResult> list = tTemplateMapper.getDepartItemResultListByPersonIds(personIds,groupIds);
		return list;
	}

	@Override
	public List<TDepartResult> getDepartResultListByPersonIds(List<String> personIds,List<String> groupIds) {
		List<TDepartResult> list = tTemplateMapper.getDepartResultListByPersonIds(personIds,groupIds);
		return list;
	}

	@Override
	public List<TDepartItemResult> getDepartItemResultList(String personId,String groupId) {
		List<TDepartItemResult> list = tTemplateMapper.getDepartItemResultList(personId,groupId);
		return list;
	}

	@Override
	public List<TDepartItemResult> getDepartItemResultListByReviewPersonIds(List<String> personIds,List<String> groupIds) {
		List<String> personIdsByReviewPersonIds = tTemplateMapper.getPersonIdsByReviewPersonIds(personIds);
		List<TDepartItemResult> list = tTemplateMapper.getDepartItemResultListByPersonIds(personIdsByReviewPersonIds,groupIds);
		return list;
	}

	@Override
	public List<TDepartResult> getDepartResultListByReviewPersonIds(List<String> personIds,List<String> groupIds) {
		List<String> personIdsByReviewPersonIds = tTemplateMapper.getPersonIdsByReviewPersonIds(personIds);
		List<TDepartResult> list = tTemplateMapper.getDepartResultListByPersonIds(personIdsByReviewPersonIds,groupIds);
		return list;
	}
	@Override
	public List<String> getPersonIdsByReviewPersonIds (List<String> personIds){
		List<String> list = tTemplateMapper.getPersonIdsByReviewPersonIds(personIds);
		return list;
	}
	/**
	* 功能描述：构建模糊查询
	* @param tTemplate 需要模糊查询的信息
	* @return 返回查询
	*/
	public QueryWrapper<TTemplate>  LikeAllFeild(TTemplate  tTemplate, SearchVo searchVo) {
		QueryWrapper<TTemplate> queryWrapper = new QueryWrapper<>();
		if(StringUtils.isNotBlank(tTemplate.getId())){
			queryWrapper.and(i -> i.like("t_template.id", tTemplate.getId()));
		}
		if(StringUtils.isNotBlank(tTemplate.getBaseProjectId())){
			queryWrapper.and(i -> i.like("t_template.base_project_id", tTemplate.getBaseProjectId()));
		}
		if(StringUtils.isNotBlank(tTemplate.getType())){
			queryWrapper.and(i -> i.like("t_template.type", tTemplate.getType()));
		}
		if(StringUtils.isNotBlank(tTemplate.getReportType())){
			queryWrapper.and(i -> i.like("t_template.report_type", tTemplate.getReportType()));
		}
		if(StringUtils.isNotBlank(tTemplate.getStatus())){
			queryWrapper.and(i -> i.like("t_template.status", tTemplate.getStatus()));
		}
		if(tTemplate.getContent() != null){
			queryWrapper.and(i -> i.like("t_template.content", tTemplate.getContent()));
		}
		if(StringUtils.isNotBlank(tTemplate.getContentName())){
			queryWrapper.and(i -> i.like("t_template.content_name", tTemplate.getContentName()));
		}
		if(tTemplate.getOrderNum() != null){
			queryWrapper.and(i -> i.like("t_template.order_num", tTemplate.getOrderNum()));
		}
		if(StringUtils.isNotBlank(tTemplate.getCreateId())){
			queryWrapper.and(i -> i.like("t_template.create_id", tTemplate.getCreateId()));
		}
		if(tTemplate.getCreateTime() != null){
			queryWrapper.and(i -> i.like("t_template.create_time", tTemplate.getCreateTime()));
		}
		if(StringUtils.isNotBlank(tTemplate.getUpdateId())){
			queryWrapper.and(i -> i.like("t_template.update_id", tTemplate.getUpdateId()));
		}
		if(tTemplate.getUpdateTime() != null){
			queryWrapper.and(i -> i.like("t_template.update_time", tTemplate.getUpdateTime()));
		}
		if(StringUtils.isNotBlank(tTemplate.getDeleteId())){
			queryWrapper.and(i -> i.like("t_template.delete_id", tTemplate.getDeleteId()));
		}
		if(tTemplate.getDeleteTime() != null){
			queryWrapper.and(i -> i.like("t_template.delete_time", tTemplate.getDeleteTime()));
		}
		if(searchVo!=null){
			if(searchVo.getStartDate()!=null && searchVo.getEndDate()!=null){
				queryWrapper.and(i -> i.between("t_template.create_time", searchVo.getStartDate(),searchVo.getEndDate()));
			}
		}
		queryWrapper.and(i -> i.eq("t_template.del_flag", 0));
		return queryWrapper;
	
	}
}