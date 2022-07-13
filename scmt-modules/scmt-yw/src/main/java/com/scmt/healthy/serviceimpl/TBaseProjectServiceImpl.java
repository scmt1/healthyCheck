package com.scmt.healthy.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scmt.core.common.utils.ResultUtil;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.Result;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.core.utis.FileUtil;
import com.scmt.healthy.entity.TBaseProject;
import com.scmt.healthy.mapper.TBaseProjectMapper;
import com.scmt.healthy.service.ITBaseProjectService;
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
public class TBaseProjectServiceImpl extends ServiceImpl<TBaseProjectMapper, TBaseProject> implements ITBaseProjectService {
	@Autowired
	@SuppressWarnings("SpringJavaAutowiringInspection")
	private TBaseProjectMapper tBaseProjectMapper;

	@Override
	public Result<Object> queryTBaseProjectListByPage(TBaseProject  tBaseProject, SearchVo searchVo, PageVo pageVo){
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
		Page<TBaseProject> pageData = new Page<>(page, limit);
		QueryWrapper<TBaseProject> queryWrapper = new QueryWrapper<>();
		if (tBaseProject !=null) {
			queryWrapper = LikeAllFeild(tBaseProject,searchVo);
		}
		if (pageVo.getSort() != null) {
		    if (pageVo.getSort().equals("asc")) {
		        queryWrapper.orderByAsc("t_base_project."+pageVo.getSort());
		    } else {
		        queryWrapper.orderByDesc("t_base_project."+pageVo.getSort());
		    }
		} else {
		    queryWrapper.orderByDesc("t_base_project.create_time");
		}
		IPage<TBaseProject> result = tBaseProjectMapper.selectPage(pageData, queryWrapper);
		return  ResultUtil.data(result);
	}
	@Override
	public void download(TBaseProject tBaseProject, HttpServletResponse response) {
		List<Map<String, Object>> mapList = new ArrayList<>();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		QueryWrapper<TBaseProject> queryWrapper = new QueryWrapper<>();
		if (tBaseProject !=null) {
			queryWrapper = LikeAllFeild(tBaseProject,null);
		}
		List<TBaseProject> list = tBaseProjectMapper.selectList(queryWrapper);
		for (TBaseProject re : list) {
			Map<String, Object> map = new LinkedHashMap<>();
			map.put("项目代码", re.getCode());
			map.put("名称", re.getName());
			map.put("简称", re.getShortName());
			map.put("排序", re.getOrderNum());
			map.put("科室id", re.getOfficeId());
			map.put("项目单位", re.getUnitName());
			map.put("默认值", re.getDefaultValue());
			map.put("结果类型", re.getResultType());
			map.put("是否进入小结", re.getInConclusion());
			map.put("是否进入报告", re.getInReport());
			map.put("LIS关联码", re.getRelationCode());
			mapList.add(map);
		}
		FileUtil.createExcel(mapList, "exel.xlsx", response);
	}

	@Override
	public Result<Object> queryAllTBaseProject(String officeId) {
		List<TBaseProject> list = tBaseProjectMapper.queryAllTBaseProject(officeId);
		return ResultUtil.data(list);
	}

	/**
	* 功能描述：构建模糊查询
	* @param tBaseProject 需要模糊查询的信息
	* @return 返回查询
	*/
	public QueryWrapper<TBaseProject>  LikeAllFeild(TBaseProject  tBaseProject, SearchVo searchVo) {
		QueryWrapper<TBaseProject> queryWrapper = new QueryWrapper<>();
		if(tBaseProject.getId() != null){
			queryWrapper.and(i -> i.like("t_base_project.id", tBaseProject.getId()));
		}
		if(StringUtils.isNotBlank(tBaseProject.getCode())){
			queryWrapper.and(i -> i.like("t_base_project.code", tBaseProject.getCode()));
		}
		if(StringUtils.isNotBlank(tBaseProject.getName())){
			queryWrapper.and(i -> i.like("t_base_project.name", tBaseProject.getName())
					.or().like("t_base_project.short_name", tBaseProject.getName()));
		}

		if(StringUtils.isNotBlank(tBaseProject.getOfficeId())){
			queryWrapper.and(i -> i.like("t_base_project.office_id", tBaseProject.getOfficeId()));
		}
		if(StringUtils.isNotBlank(tBaseProject.getUnitCode())){
			queryWrapper.and(i -> i.like("t_base_project.unit_code", tBaseProject.getUnitCode()));
		}
		if(StringUtils.isNotBlank(tBaseProject.getDefaultValue())){
			queryWrapper.and(i -> i.like("t_base_project.default_value", tBaseProject.getDefaultValue()));
		}
		if(StringUtils.isNotBlank(tBaseProject.getResultType())){
			queryWrapper.and(i -> i.like("t_base_project.result_type", tBaseProject.getResultType()));
		}
		if(StringUtils.isNotBlank(tBaseProject.getInConclusion())){
			queryWrapper.and(i -> i.like("t_base_project.in_conclusion", tBaseProject.getInConclusion()));
		}
		if(StringUtils.isNotBlank(tBaseProject.getInReport())){
			queryWrapper.and(i -> i.like("t_base_project.in_report", tBaseProject.getInReport()));
		}
		if(StringUtils.isNotBlank(tBaseProject.getRelationCode())){
			queryWrapper.and(i -> i.like("t_base_project.relation_code", tBaseProject.getRelationCode()));
		}
		if(tBaseProject.getCreateId() != null){
			queryWrapper.and(i -> i.like("t_base_project.create_id", tBaseProject.getCreateId()));
		}
		if(tBaseProject.getCreateTime() != null){
			queryWrapper.and(i -> i.like("t_base_project.create_time", tBaseProject.getCreateTime()));
		}
		if(tBaseProject.getUpdateId() != null){
			queryWrapper.and(i -> i.like("t_base_project.update_id", tBaseProject.getUpdateId()));
		}
		if(tBaseProject.getUpdateTime() != null){
			queryWrapper.and(i -> i.like("t_base_project.update_time", tBaseProject.getUpdateTime()));
		}
		if(tBaseProject.getDeleteId() != null){
			queryWrapper.and(i -> i.like("t_base_project.delete_id", tBaseProject.getDeleteId()));
		}
		if(tBaseProject.getDeleteTime() != null){
			queryWrapper.and(i -> i.like("t_base_project.delete_time", tBaseProject.getDeleteTime()));
		}
		if(tBaseProject.getDepartmentId() != null){
			queryWrapper.and(i -> i.like("t_base_project.department_id", tBaseProject.getDepartmentId()));
		}
		if(searchVo!=null){
			if(StringUtils.isNotBlank(searchVo.getStartDate()) && StringUtils.isNotBlank(searchVo.getEndDate())){
				queryWrapper.and(i -> i.between("t_base_project.create_time", searchVo.getStartDate(),searchVo.getEndDate()));
			}
		}
		if(StringUtils.isNotBlank(tBaseProject.getKeyword())) {
			tBaseProject.setKeyword(tBaseProject.getKeyword().trim());
			queryWrapper.and(i -> i.like("t_base_project.code", tBaseProject.getKeyword())
					.or().like("t_base_project.name", tBaseProject.getKeyword())
					.or().like("t_base_project.short_name",tBaseProject.getKeyword()));
		}
		queryWrapper.and(i -> i.eq("t_base_project.del_flag", 0));
		return queryWrapper;

}
}
