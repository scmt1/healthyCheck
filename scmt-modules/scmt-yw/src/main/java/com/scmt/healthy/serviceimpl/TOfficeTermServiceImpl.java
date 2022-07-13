package com.scmt.healthy.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.scmt.healthy.entity.TOfficeTerm;
import com.scmt.healthy.entity.TSectionOffice;
import com.scmt.healthy.service.ITOfficeTermService;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.mapper.TOfficeTermMapper;
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
public class TOfficeTermServiceImpl extends ServiceImpl<TOfficeTermMapper, TOfficeTerm> implements ITOfficeTermService {
	@Autowired
	@SuppressWarnings("SpringJavaAutowiringInspection")
	private TOfficeTermMapper tOfficeTermMapper;

	@Override
	public IPage<TOfficeTerm> queryTOfficeTermListByPage(TOfficeTerm  tOfficeTerm, SearchVo searchVo, PageVo pageVo){
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
		Page<TOfficeTerm> pageData = new Page<>(page, limit);
//		QueryWrapper<TOfficeTerm> queryWrapper = new QueryWrapper<>();
//		if (tOfficeTerm !=null) {
//			queryWrapper = LikeAllFeild(tOfficeTerm,searchVo);
//		}
//		IPage<TOfficeTerm> result = tOfficeTermMapper.selectPage(pageData, queryWrapper);
		IPage<TOfficeTerm> result = tOfficeTermMapper.selectTOfficeTermPageList(tOfficeTerm, searchVo, pageData);
		return  result;
	}

	@Override
	public void download(TOfficeTerm tOfficeTerm, HttpServletResponse response) {
		List<Map<String, Object>> mapList = new ArrayList<>();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		QueryWrapper<TOfficeTerm> queryWrapper = new QueryWrapper<>();
		if (tOfficeTerm !=null) {
			queryWrapper = LikeAllFeild(tOfficeTerm,null);
		}
		List<TOfficeTerm> list = tOfficeTermMapper.selectList(queryWrapper);
		for (TOfficeTerm re : list) {
			Map<String, Object> map = new LinkedHashMap<>();
			map.put("科室Id", re.getOfficeId());
			map.put("状态", re.getStatus());
			map.put("术语内容", re.getContent());
			map.put("排序", re.getOrderNum());
			mapList.add(map);
		}
		FileUtil.createExcel(mapList, "exel.xlsx", response);
	}

	@Override
	public List<TOfficeTerm> queryAllOfficeTermData(TOfficeTerm tOfficeTerm) {
		QueryWrapper<TOfficeTerm> queryWrapper = new QueryWrapper<>();
		if (tOfficeTerm != null) {
			queryWrapper = LikeAllFeild(tOfficeTerm, null);
		}
		return tOfficeTermMapper.selectList(queryWrapper);
	}

	/**
	* 功能描述：构建模糊查询
	* @param tOfficeTerm 需要模糊查询的信息
	* @return 返回查询
	*/
	public QueryWrapper<TOfficeTerm>  LikeAllFeild(TOfficeTerm  tOfficeTerm, SearchVo searchVo) {
		QueryWrapper<TOfficeTerm> queryWrapper = new QueryWrapper<>();
		if(StringUtils.isNotBlank(tOfficeTerm.getId())){
			queryWrapper.lambda().and(i -> i.like(TOfficeTerm::getId, tOfficeTerm.getId()));
		}
		if(StringUtils.isNotBlank(tOfficeTerm.getType())){
			queryWrapper.lambda().and(i -> i.like(TOfficeTerm::getType, tOfficeTerm.getType()));
		}
		if(StringUtils.isNotBlank(tOfficeTerm.getStatus())){
			queryWrapper.lambda().and(i -> i.like(TOfficeTerm::getStatus, tOfficeTerm.getStatus()));
		}
		if(StringUtils.isNotBlank(tOfficeTerm.getContent())){
			queryWrapper.lambda().and(i -> i.like(TOfficeTerm::getContent, tOfficeTerm.getContent()));
		}
		if(tOfficeTerm.getOrderNum() != null){
			queryWrapper.lambda().and(i -> i.like(TOfficeTerm::getOrderNum, tOfficeTerm.getOrderNum()));
		}
		if(tOfficeTerm.getDelFlag() != null){
			queryWrapper.lambda().and(i -> i.like(TOfficeTerm::getDelFlag, tOfficeTerm.getDelFlag()));
		}
		if(StringUtils.isNotBlank(tOfficeTerm.getCreateId())){
			queryWrapper.lambda().and(i -> i.like(TOfficeTerm::getCreateId, tOfficeTerm.getCreateId()));
		}
		if(tOfficeTerm.getCreateTime() != null){
			queryWrapper.lambda().and(i -> i.like(TOfficeTerm::getCreateTime, tOfficeTerm.getCreateTime()));
		}
		if(StringUtils.isNotBlank(tOfficeTerm.getUpdateId())){
			queryWrapper.lambda().and(i -> i.like(TOfficeTerm::getUpdateId, tOfficeTerm.getUpdateId()));
		}
		if(tOfficeTerm.getUpdateTime() != null){
			queryWrapper.lambda().and(i -> i.like(TOfficeTerm::getUpdateTime, tOfficeTerm.getUpdateTime()));
		}
		if(StringUtils.isNotBlank(tOfficeTerm.getDeleteId())){
			queryWrapper.lambda().and(i -> i.like(TOfficeTerm::getDeleteId, tOfficeTerm.getDeleteId()));
		}
		if(tOfficeTerm.getDeleteTime() != null){
			queryWrapper.lambda().and(i -> i.like(TOfficeTerm::getDeleteTime, tOfficeTerm.getDeleteTime()));
		}
		if(searchVo!=null){
			if(searchVo.getStartDate()!=null && searchVo.getEndDate()!=null){
				queryWrapper.lambda().and(i -> i.between(TOfficeTerm::getCreateTime, searchVo.getStartDate(),searchVo.getEndDate()));
			}
		}
		if(StringUtils.isNotBlank(tOfficeTerm.getOfficeId())){
			queryWrapper.lambda().and(i -> i.eq(TOfficeTerm::getOfficeId, tOfficeTerm.getOfficeId()));
		}
		queryWrapper.lambda().and(i -> i.eq(TOfficeTerm::getDelFlag, 0));
		return queryWrapper;

}
}
