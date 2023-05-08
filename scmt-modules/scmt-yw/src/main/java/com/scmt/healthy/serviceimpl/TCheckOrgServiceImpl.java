package com.scmt.healthy.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scmt.core.common.utils.SecurityUtil;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.core.utis.FileUtil;
import com.scmt.healthy.entity.TCheckOrg;
import com.scmt.healthy.entity.TCombo;
import com.scmt.healthy.mapper.TCheckOrgMapper;
import com.scmt.healthy.mapper.TComboMapper;
import com.scmt.healthy.service.ITCheckOrgService;
import com.scmt.healthy.utils.BASE64DecodedMultipartFile;
import com.scmt.healthy.utils.DocUtil;
import com.scmt.healthy.utils.UploadFileUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *@author
 **/
@Service
public class TCheckOrgServiceImpl extends ServiceImpl<TCheckOrgMapper, TCheckOrg> implements ITCheckOrgService {


	@Resource
	@SuppressWarnings("SpringJavaAutowiringInspection")
	private TCheckOrgMapper tCheckOrgMapper;

	@Resource
	private TComboMapper tComboMapper;

	@Resource
	private SecurityUtil securityUtil;

	@Override
	public IPage<TCheckOrg> queryTCheckOrgListByPage(TCheckOrg  tCheckOrg, SearchVo searchVo, PageVo pageVo){
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
		Page<TCheckOrg> pageData = new Page<>(page, limit);
		QueryWrapper<TCheckOrg> queryWrapper = new QueryWrapper<>();
		if (tCheckOrg !=null) {
			queryWrapper = LikeAllField(tCheckOrg,searchVo);
		}
		queryWrapper.select().orderByDesc("update_time");
		IPage<TCheckOrg> result = tCheckOrgMapper.selectPage(pageData,queryWrapper);
		return  result;
	}

	/**
	 * 联表分页查询体检机构及套餐信息
	 * @param tCheckOrg
	 * @return
	 */
	@Override
	public IPage<TCheckOrg> getOrgAndComboInfoByPage(TCheckOrg tCheckOrg, SearchVo searchVo, PageVo pageVo) {
		int page = 1;
		int limit = 1000;
		if (pageVo != null) {
			if (pageVo.getPageNumber() != 0) {
				page = pageVo.getPageNumber();
			}
			if (pageVo.getPageSize() != 0) {
				limit = pageVo.getPageSize();
			}
		}
		Page<TCheckOrg> pageData = new Page<>(page, limit);
		QueryWrapper<TCheckOrg> queryWrapper = new QueryWrapper<>();
		if (tCheckOrg !=null) {
			if("全部类型".equals(tCheckOrg.getType())){
				tCheckOrg.setType(null);
			}
			if("全部地区".equals(tCheckOrg.getAddress())){
				tCheckOrg.setAddress(null);
			}
			queryWrapper = LikeAllField(tCheckOrg,searchVo);
			if(StringUtils.isNotBlank(tCheckOrg.getCombosType())){
				queryWrapper.and(i -> i.eq("t_combo.type", tCheckOrg.getCombosType()));
			}
			if(StringUtils.isNotBlank(tCheckOrg.getCombosName())){
				queryWrapper.and(i -> i.like("t_combo.name", tCheckOrg.getCombosName()));
			}
			if(tCheckOrg.getIsLevel() != null && "等级优先".equals(tCheckOrg.getIsLevel())){
				queryWrapper.last("ORDER BY FIELD(SUBSTRING(`level`,1,1),'一','二','三') DESC,t_check_org.name ASC");
			}
		}
		IPage<TCheckOrg> result = tCheckOrgMapper.selectOrgAndCombo(queryWrapper,pageData);
		//设置每一个套餐的总价
		List<TCheckOrg> list = result.getRecords();
		for (TCheckOrg checkOrg:list) {
			List<TCombo> combos = checkOrg.getTCombos();
			for (TCombo combo:combos) {
				Integer price = tComboMapper.selectTComboPriceById(combo.getId());
				combo.setPrice(price);
			}
		}
		return  result;
	}

	/**
	 * 根据id查询体检机构及套餐信息
	 * @param tCheckOrg
	 * @return
	 */
	@Override
	public TCheckOrg getOrgAndComboInfo(TCheckOrg tCheckOrg) {

		QueryWrapper<TCheckOrg> queryWrapper = new QueryWrapper<>();
		if (tCheckOrg !=null) {
			queryWrapper.and(i -> i.eq("t_check_org.id", tCheckOrg.getId()));
		}
		TCheckOrg result = tCheckOrgMapper.selectOrgAndCombo(queryWrapper);
		return  result;
	}
	@Override
	public void download(TCheckOrg tCheckOrg, HttpServletResponse response) {
		List<Map<String, Object>> mapList = new ArrayList<>();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		QueryWrapper<TCheckOrg> queryWrapper = new QueryWrapper<>();
		if (tCheckOrg !=null) {
			queryWrapper = LikeAllField(tCheckOrg,null);
		}
		List<TCheckOrg> list = tCheckOrgMapper.selectList(queryWrapper);
		for (TCheckOrg re : list) {
			Map<String, Object> map = new LinkedHashMap<>();
			map.put("机构名称", re.getName());
			map.put("机构简介", re.getIntroduction());
			map.put("机构地址", re.getAddress());
			map.put("电话", re.getPhone());
			map.put("营业时间", re.getBusinessHours());
			map.put("图片", re.getAvatar());
			map.put("位置", re.getPosition());
			map.put("到院须知", re.getNotice());
			mapList.add(map);
		}
		FileUtil.createExcel(mapList, "exel.xlsx", response);
	}

	/**
	* 功能描述：构建模糊查询
	* @param tCheckOrg 需要模糊查询的信息
	* @return 返回查询
	*/
	public QueryWrapper<TCheckOrg>  LikeAllField(TCheckOrg  tCheckOrg, SearchVo searchVo) {
		QueryWrapper<TCheckOrg> queryWrapper = new QueryWrapper<>();
		if(StringUtils.isNotBlank(tCheckOrg.getId())){
			queryWrapper.and(i -> i.eq("t_check_org.id", tCheckOrg.getId()));
		}
		if(StringUtils.isNotBlank(tCheckOrg.getName())){
			queryWrapper.and(i -> i.like("t_check_org.name", tCheckOrg.getName()));
		}
		if(StringUtils.isNotBlank(tCheckOrg.getIntroduction())){
			queryWrapper.and(i -> i.like("t_check_org.introduction", tCheckOrg.getIntroduction()));
		}
		if(StringUtils.isNotBlank(tCheckOrg.getAddress())){
			queryWrapper.and(i -> i.like("t_check_org.address", tCheckOrg.getAddress()));
		}
		if(StringUtils.isNotBlank(tCheckOrg.getPhone())){
			queryWrapper.and(i -> i.like("t_check_org.phone", tCheckOrg.getPhone()));
		}
		if(StringUtils.isNotBlank(tCheckOrg.getBusinessHours())){
			queryWrapper.and(i -> i.like("t_check_org.business_hours", tCheckOrg.getBusinessHours()));
		}
		if(StringUtils.isNotBlank(tCheckOrg.getLevel())){
			queryWrapper.and(i -> i.like("t_check_org.level", tCheckOrg.getLevel()));
		}
		if(StringUtils.isNotBlank(tCheckOrg.getTags())){
			queryWrapper.and(i -> i.like("t_check_org.tags", tCheckOrg.getTags()));
		}
		if(StringUtils.isNotBlank(tCheckOrg.getPosition())){
			queryWrapper.and(i -> i.like("t_check_org.position", tCheckOrg.getPosition()));
		}
		if(StringUtils.isNotBlank(tCheckOrg.getNotice())){
			queryWrapper.and(i -> i.like("t_check_org.notice", tCheckOrg.getNotice()));
		}
		if(StringUtils.isNotBlank(tCheckOrg.getCreateBy())){
			queryWrapper.and(i -> i.like("t_check_org.create_by", tCheckOrg.getCreateBy()));
		}
		if(tCheckOrg.getCreateTime() != null){
			queryWrapper.and(i -> i.like("t_check_org.create_time", tCheckOrg.getCreateTime()));
		}
		if(StringUtils.isNotBlank(tCheckOrg.getUpdateBy())){
			queryWrapper.and(i -> i.like("t_check_org.update_by", tCheckOrg.getUpdateBy()));
		}
		if(tCheckOrg.getUpdateTime() != null){
			queryWrapper.and(i -> i.like("t_check_org.update_time", tCheckOrg.getUpdateTime()));
		}
		if(tCheckOrg.getDelFlag() != null){
			queryWrapper.and(i -> i.like("t_check_org.del_flag", tCheckOrg.getDelFlag()));
		}
		if(StringUtils.isNotBlank(tCheckOrg.getType())){
			queryWrapper.and(i -> i.like("t_check_org.type", tCheckOrg.getType()));
		}
		if(StringUtils.isNotBlank(tCheckOrg.getCode())){
			queryWrapper.and(i -> i.like("t_check_org.code", tCheckOrg.getCode()));
		}
		if(StringUtils.isNotBlank(tCheckOrg.getStatus())){
			queryWrapper.and(i -> i.like("t_check_org.status", tCheckOrg.getStatus()));
		}
		if(StringUtils.isNotBlank(tCheckOrg.getCheckType())){
			queryWrapper.and(i -> i.like("t_check_org.check_type", tCheckOrg.getCheckType()));
		}
		if(searchVo!=null){
			if(searchVo.getStartDate()!=null && searchVo.getEndDate()!=null){
				queryWrapper.lambda().and(i -> i.between(TCheckOrg::getCreateTime, searchVo.getStartDate(),searchVo.getEndDate()));
			}
		}
//		queryWrapper.lambda().and(i -> i.eq(TCheckOrg::getDelFlag, 0));
		queryWrapper.and(i -> i.eq("t_check_org.del_flag", 0));
		return queryWrapper;

}

	/**
	 * 模糊查询所有的体检机构信息
	 * @param tCheckOrg
	 * @param searchVo
	 * @return
	 */
	@Override
	public List<TCheckOrg> getAllCheckOrg(TCheckOrg tCheckOrg,SearchVo searchVo) {
		QueryWrapper<TCheckOrg> queryWrapper = LikeAllField(tCheckOrg, searchVo);
		return tCheckOrgMapper.selectList(queryWrapper);
	}

	/**
	 * 检查是否出现同名的体检机构
	 * @param tCheckOrg
	 * @return
	 */
	@Override
	public Boolean checkOrgName(TCheckOrg tCheckOrg){
		QueryWrapper<TCheckOrg> queryWrapper = new QueryWrapper<>();
		if(tCheckOrg.getId() != null){
			queryWrapper.ne("id",tCheckOrg.getId());
		}
		queryWrapper.eq("name",tCheckOrg.getName());
		return tCheckOrgMapper.selectCount(queryWrapper) > 0;
	}

	/**
	 * 保存操作或者更新操作时处理上传的图片
	 * @return
	 */
	@Override
	public Boolean handleCheckOrgImg(TCheckOrg tCheckOrg,String checkImage){
		if(checkImage != null && StringUtils.isNotBlank(checkImage)){
			String header = "data:image/jpeg;base64,";
			String image = checkImage;
			//判断image是否包含指定类型子串
			if(checkImage.contains("data:image/png;base64,")){
				image = checkImage.replaceAll("data:image/png;base64,", "");
			}
			if(checkImage.contains("data:image/jpeg;base64,")){
				image = checkImage.replaceAll("data:image/jpeg;base64,", "");
			}
			String img = image;
			//判断image是否含有指定字符
			//头像上传
			if(image.indexOf(";") == -1){
				//清空avatar
				tCheckOrg.setAvatar("");
				//判断image是否是base64格式的字符串
				if(Base64.isBase64(image)){
					//拼接完整的base64形式的图片字符串
					String imgBase64 = header + image;
					MultipartFile imgFile = BASE64DecodedMultipartFile.base64ToMultipart(imgBase64.toString());
					String classPath = null;
					try {
						classPath = DocUtil.getClassPath().split(":")[0];
						//时间戳
						SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
						String DataStr = format.format(new Date());
						String name = imgFile.getOriginalFilename();
						File file1 = new File(classPath + ":" + UploadFileUtils.basePath + "org/img/" + DataStr + "/" + name);
						//存在则删除
						if (file1.isFile() && file1.exists()) {
							file1.delete();
							file1 = new File(classPath + ":" + UploadFileUtils.basePath + "org/img/" + DataStr + "/" + name);
						}
						FileUtils.writeByteArrayToFile(file1, imgFile.getBytes());
						String url = "/tempFileUrl/tempfile/org/img/" + DataStr + "/" + name;
						tCheckOrg.setAvatar(url);
						return true;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				tCheckOrg.setAvatar(img);
				return true;
			}
			//背景图片上传
			String[] images = image.split(";");
			//清空images
			tCheckOrg.setImages("");
			for (String checkImg:images) {
				//判断索引位置上的checkImg是否为base64字符串
				if(Base64.isBase64(checkImg) && checkImg != ""){
					//拼接完整的base64形式的图片字符串
					String imgBase64 = header + checkImg;
					MultipartFile imgFile = BASE64DecodedMultipartFile.base64ToMultipart(imgBase64.toString());
					String classPath = null;
					try {
						classPath = DocUtil.getClassPath().split(":")[0];
						//时间戳
						SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
						String DataStr = format.format(new Date());
						String name = imgFile.getOriginalFilename();
						File file1 = new File(classPath + ":" + UploadFileUtils.basePath + "org/img/" + DataStr + "/" + name);
						//存在则删除
						if (file1.isFile() && file1.exists()) {
							file1.delete();
							file1 = new File(classPath + ":" + UploadFileUtils.basePath + "org/img/" + DataStr + "/" + name);
						}
						FileUtils.writeByteArrayToFile(file1, imgFile.getBytes());
						String url = "/tempFileUrl/tempfile/org/img/" + DataStr + "/" + name;
						tCheckOrg.setImages(tCheckOrg.getImages() + url + ";");
						continue;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				tCheckOrg.setImages(tCheckOrg.getImages()+checkImg+";");
			}
			return true;
		}
		return false;
	}
}
