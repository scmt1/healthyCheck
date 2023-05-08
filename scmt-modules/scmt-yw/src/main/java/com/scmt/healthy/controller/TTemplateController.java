package com.scmt.healthy.controller;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import cn.hutool.core.io.resource.InputStreamResource;
import cn.hutool.core.thread.ThreadFactoryBuilder;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.deepoove.poi.data.TextRenderData;
import com.deepoove.poi.data.style.Style;
import com.scmt.core.common.utils.IpInfoUtil;
import com.scmt.healthy.common.SocketConfig;
import com.scmt.healthy.entity.*;
import com.scmt.healthy.reporting.Reporting;
import com.scmt.healthy.service.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.scmt.healthy.serviceimpl.TdTjBadrsnsServiceImpl;
import com.scmt.healthy.utils.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import com.scmt.core.common.utils.ResultUtil;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.Result;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.core.common.utils.SecurityUtil;
import com.scmt.core.common.annotation.SystemLog;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.scmt.core.common.enums.LogType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author
 **/
@RestController
@Api(tags = " 模板信息数据接口")
@RequestMapping("/scmt/tTemplate")
public class TTemplateController {
	@Autowired
	private ITTemplateService tTemplateService;
	@Autowired
	private SecurityUtil securityUtil;
	@Autowired(required = false)
	private HttpServletRequest request;
	@Autowired
	private IpInfoUtil ipInfoUtil;
	@Autowired
	private ITGroupPersonService tGroupPersonService;
	@Autowired
	private ITOrderGroupService orderGroupService;
	@Autowired
	private ITPastMedicalHistoryService tPastMedicalHistoryService;
	@Autowired
	private IRelationPersonProjectCheckService relationPersonProjectCheckService;
	@Autowired
	private ITInspectionRecordService tInspectionRecordService;
	@Autowired
	private ITReviewProjectService tReviewProjectService;
	@Autowired
	private ITCareerHistoryService tCareerHistoryService;

	@Autowired
	private ITSymptomService tSymptomService;

	@Autowired
	private ITGroupUnitService tGroupUnitService;
	@Autowired
	private ITGroupOrderService itGroupOrderService;
	@Autowired
	private ITOrderGroupService tOrderGroupService;
	@Autowired
	private ITComboItemService itComboItemService;
	@Autowired
	private ITOrderGroupItemService tOrderGroupItemService;


	@Autowired
	private ITDepartResultService tDepartResultService;
	@Autowired
	private TInterrogationService interrogationService;
	private File file;

	@Autowired
	private TTestRecordService tTestRecordService;

	@Autowired
	private ITReviewPersonService itReviewPersonService;

	@Autowired
	private ITPositivePersonService tPositivePersonService;

	@Autowired
	private TdTjBadrsnsServiceImpl tdTjBadrsnsService;
	/**
	 * socket配置
	 */
	@Autowired
	public SocketConfig socketConfig;

	/**
	 * 功能描述：新增模板信息数据
	 *
	 * @param file 实体
	 * @return 返回新增结果
	 */
	@SystemLog(description = "新增模板信息数据", type = LogType.OPERATION)
	@ApiOperation("新增模板信息数据")
	@PostMapping("addTTemplate")
	public Result<Object> addTTemplate(@RequestParam(value = "file", required = false) MultipartFile file, @RequestParam String submissionData) {
		if (file == null || StringUtils.isBlank(submissionData)) {
			return ResultUtil.error("保存失败:参数为空");
		}
		try {
			TTemplate tTemplate = JSONObject.parseObject(submissionData, TTemplate.class);
			//查询是否重复
			if (tTemplate != null) {
				//查询是否重复
				/*QueryWrapper<TTemplate> queryWrapper = new QueryWrapper<>();
				if (StringUtils.isNotBlank(tTemplate.getType()) &&  tTemplate.getType().equals("分项报告") && StringUtils.isNotBlank(tTemplate.getBaseProjectId())) {
					queryWrapper.and(i -> i.eq("t_template.base_project_id", tTemplate.getBaseProjectId()));
					queryWrapper.and(i -> i.like("t_template.report_type", tTemplate.getReportType()));
					queryWrapper.and(i -> i.eq("t_template.status", 1));
					queryWrapper.and(i -> i.eq("t_template.del_flag", 0));
					int count = tTemplateService.count(queryWrapper);
					if (count > 0) {
						return ResultUtil.error("保存失败:数据重复（当前类型下已有生效的模板文件或当前组合项目下已有生效的模板文件）");
					}
				} else {
					return ResultUtil.error("保存失败:模板类型为空");
				}*/
				tTemplate.setDelFlag(0);
				tTemplate.setCreateId(securityUtil.getCurrUser().getId());
				tTemplate.setCreateTime(new Date());
				if (file.getOriginalFilename().indexOf(".docx") >= 0) {
					String html = DocUtil.uploadFile(file);
					tTemplate.setContent(html);
				} else if (file.getOriginalFilename().indexOf(".doc") >= 0) {
					String html = DocUtil.uploadFile(file);
					tTemplate.setContent(html);
				} else {
					byte[] data = file.getBytes();//获取文件中的数据
					String blobString = new String(data);
					tTemplate.setContent(blobString);
				}

				boolean res = tTemplateService.save(tTemplate);
				if (res) {
					return ResultUtil.data(res, "保存成功");
				} else {
					return ResultUtil.data(res, "保存失败");
				}
			} else {
				return ResultUtil.data(false, "保存失败");
			}

		} catch (Exception e) {
			e.printStackTrace();
			return ResultUtil.error("保存异常:" + e.getMessage());
		}
	}

	/**
	 * 功能描述：更新数据
	 *
	 * @param submissionData 实体
	 * @return 返回更新结果
	 */
	@SystemLog(description = "更新模板信息数据", type = LogType.OPERATION)
	@ApiOperation("更新模板信息数据")
	@PostMapping("updateTTemplate")
	public Result<Object> updateTTemplate(@RequestParam(value = "file", required = false) MultipartFile file, @RequestParam String submissionData) {
		if (StringUtils.isBlank(submissionData)) {
			return ResultUtil.error("保存失败:参数为空");
		}
		try {
			TTemplate tTemplate = JSONObject.parseObject(submissionData, TTemplate.class);
			if (tTemplate != null) {
				//查询是否重复
				/*QueryWrapper<TTemplate> queryWrapper = new QueryWrapper<>();
				if (StringUtils.isNotBlank(tTemplate.getType()) &&  tTemplate.getType().equals("分项报告") && StringUtils.isNotBlank(tTemplate.getBaseProjectId())) {
					queryWrapper.and(i -> i.eq("t_template.base_project_id", tTemplate.getBaseProjectId()));
					queryWrapper.and(i -> i.like("t_template.report_type", tTemplate.getReportType()));
					queryWrapper.and(i -> i.eq("t_template.status", 1));
					queryWrapper.and(i -> i.eq("t_template.del_flag", 0));
					queryWrapper.and(i -> i.ne("t_template.id", tTemplate.getId()));
					int count = tTemplateService.count(queryWrapper);
					if (count > 0) {
						return ResultUtil.error("保存失败:数据重复（当前类型下已有生效的模板文件或当前组合项目下已有生效的模板文件）");
					}
				} else {
					return ResultUtil.error("保存失败:模板类型为空");
				}*/
				if (file != null) {
					TTemplate tTemplateOld = tTemplateService.getById(tTemplate.getId());
					//删除之前的
					if (StringUtils.isNotBlank(tTemplateOld.getContent())) {
						DocUtil.deleteFile(tTemplateOld.getContent());
					}
					if (file.getOriginalFilename().indexOf(".docx") >= 0) {
						String html = DocUtil.uploadFile(file);
						tTemplate.setContent(html);
					} else if (file.getOriginalFilename().indexOf(".doc") >= 0) {
						String html = DocUtil.uploadFile(file);
						tTemplate.setContent(html);
					} else {
						//获取文件中的数据
						byte[] data = file.getBytes();
						String blobString = new String(data);
						tTemplate.setContent(blobString);
					}
				}
				tTemplate.setUpdateId(securityUtil.getCurrUser().getId());
				tTemplate.setUpdateTime(new Date());
				boolean res = tTemplateService.updateById(tTemplate);
				if (res) {
					return ResultUtil.data(true, "修改成功");
				} else {
					return ResultUtil.error("修改失败");
				}
			} else {
				return ResultUtil.data(false, "修改失败");
			}


		} catch (Exception e) {
			e.printStackTrace();
			return ResultUtil.error("保存异常:" + e.getMessage());
		}
	}

	/**
	 * 功能描述：根据主键来删除数据
	 *
	 * @param ids 主键集合
	 * @return 返回删除结果
	 */
	@ApiOperation("根据主键来删除模板信息数据")
	@SystemLog(description = "根据主键来删除模板信息数据", type = LogType.OPERATION)
	@PostMapping("deleteTTemplate")
	public Result<Object> deleteTTemplate(@RequestParam String[] ids) {
		if (ids == null || ids.length == 0) {
			return ResultUtil.error("参数为空，请联系管理员！！");
		}
		try {
			for (int i = 0; i < ids.length; i++) {
				String id = ids[i];
				if (StringUtils.isNotBlank(id)) {
					TTemplate tTemplateOld = tTemplateService.getById(id);
					//删除之前的文件
					if (StringUtils.isNotBlank(tTemplateOld.getContent())) {
						DocUtil.deleteFile(tTemplateOld.getContent());
					}
					boolean res = tTemplateService.removeById(id);
					if (!res) {
						return ResultUtil.error("删除失败");
					}
				}

			}
			return ResultUtil.data(true, "删除成功");

		} catch (Exception e) {
			e.printStackTrace();
			return ResultUtil.error("删除异常:" + e.getMessage());
		}
	}

	/**
	 * 功能描述：根据主键来获取数据
	 *
	 * @param id 主键
	 * @return 返回获取结果
	 */
	@SystemLog(description = "根据主键来获取模板信息数据", type = LogType.OPERATION)
	@ApiOperation("根据主键来获取模板信息数据")
	@GetMapping("getTTemplate")
	public Result<Object> getTTemplate(@RequestParam(name = "id") String id) {
		if (StringUtils.isBlank(id)) {
			return ResultUtil.error("参数为空，请联系管理员！！");
		}
		try {
			TTemplate res = tTemplateService.getTemplateById(id);
			if (res != null) {
				return ResultUtil.data(res, "查询成功");
			} else {
				return ResultUtil.error("查询失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultUtil.error("查询异常:" + e.getMessage());
		}
	}

	/**
	 * 功能描述：实现分页查询
	 *
	 * @param searchVo 需要模糊查询的信息
	 * @param pageVo   分页参数
	 * @return 返回获取结果
	 */
	@SystemLog(description = "分页查询模板信息数据", type = LogType.OPERATION)
	@ApiOperation("分页查询模板信息数据")
	@GetMapping("queryTTemplateList")
	public Result<Object> queryTTemplateList(TTemplate tTemplate, SearchVo searchVo, PageVo pageVo) {
		try {
			IPage<TTemplate> result = tTemplateService.queryTTemplateListByPage(tTemplate, searchVo, pageVo);
			return ResultUtil.data(result);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultUtil.error("查询异常:" + e.getMessage());
		}
	}

	/**
	 * 功能描述：导出数据
	 *
	 * @param response  请求参数
	 * @param tTemplate 查询参数
	 * @return
	 */
	@SystemLog(description = "导出模板信息数据", type = LogType.OPERATION)
	@ApiOperation("导出模板信息数据")
	@PostMapping("/download")
	public void download(HttpServletResponse response, TTemplate tTemplate) {
		try {
			tTemplateService.download(tTemplate, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 功能描述：实现分页查询
	 *
	 * @param tTemplate 需要模糊查询的信息
	 * @return 返回获取结果
	 */
	@SystemLog(description = "模糊查询所有模板信息数据", type = LogType.OPERATION)
	@ApiOperation("模糊查询所有模板信息数据")
	@GetMapping("queryAllTTemplateList")
	public Result<Object> queryAllTTemplateList(TTemplate tTemplate) {
		try {
			List<TTemplate> result = tTemplateService.queryAllTTemplateList(tTemplate);
			return ResultUtil.data(result);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultUtil.error("查询异常:" + e.getMessage());
		}
	}

	/**
	 * 功能描述：预览模板信息数据
	 *
	 * @param response  请求参数
	 * @param tTemplate 查询参数
	 * @return
	 */
	@SystemLog(description = "预览模板信息数据", type = LogType.OPERATION)
	@ApiOperation("预览模板信息数据")
	@PostMapping("/getTemplatePreview")
	public Result<Object> getTemplatePreview(HttpServletResponse response, TTemplate tTemplate) {
		try {
			TTemplate res = tTemplateService.getById(tTemplate.getId());
			String template = res.getContent();
			Map<String, Object> variables = new HashMap<>();
			//设置日期格式
			SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日");
			variables.put("date", df.format(new Date()));
			String html = DocUtil.WordToPDF(template);
			if (StringUtils.isBlank(html)) {
				return ResultUtil.error("预览模板信息异常:" + "转换文件失败");
			}
			return ResultUtil.data(html);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultUtil.error("预览模板信息异常:" + e.getMessage());
		}
	}

	/**
	 * 功能描述：预览模板信息数据
	 *
	 * @param tTemplate    查询参数
	 * @return
	 */
//	@SystemLog(description = "预览模板信息数据", type = LogType.OPERATION)
	@ApiOperation("预览模板信息数据")
	@PostMapping("/getTemplatePreviewData")
	public Result<Object> getTemplatePreviewData(@RequestBody TTemplate tTemplate) {
		try {
			if(tTemplate== null || (StringUtils.isBlank(tTemplate.getId()) && StringUtils.isBlank(tTemplate.getBaseProjectId()) )){
				return ResultUtil.error("查询模板信息失败:" + "参数为空");
			}
			String templateData = tTemplate.getTemplateData();
			TTemplate res = new TTemplate();
			String userId = securityUtil.getCurrUser().getId();
			QueryWrapper<TTemplate> queryWrapper = new QueryWrapper<>();
			queryWrapper.and(i -> i.eq("t_template.status", 1));
			queryWrapper.and(i -> i.eq("t_template.del_flag", 0));
			if (StringUtils.isNotBlank(tTemplate.getId())) {
				queryWrapper.and(i -> i.eq("t_template.id", tTemplate.getId()));
			}
			if (StringUtils.isNotBlank(tTemplate.getBaseProjectId())) {
				queryWrapper.and(i -> i.eq("t_template.base_project_id", tTemplate.getBaseProjectId()));
			}
			List<TTemplate> list = tTemplateService.list(queryWrapper);
			if (list.size() > 0) {
				res = list.get(0);
			} else {
				return ResultUtil.error("查询模板信息失败:" + "当前项目没有模板");
			}
			if (res == null || StringUtils.isBlank(res.getContent())) {
				return ResultUtil.error("查询模板信息失败:" + "当前项目没有模板文件，请上传");
			}
			String template = res.getContent();
			Map<String, Object> variables = new HashMap<>();
			//设置日期格式
			SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日");
			variables.put("date", df.format(new Date()));
			if (StringUtils.isNotBlank(templateData)) {
				Map<String, Object> jsonToMap = JSONUtil.parseJSON2Map(templateData);
				variables.putAll(jsonToMap);
			}
			//获取请求ip
			String ip = ipInfoUtil.getIpAddr(request);
			String html = "";
			if (template.indexOf(".docx") >= 0) {
				html = DocUtil.WordDOCXToPDFWithData(template, variables, userId, ip);
			} else if (template.indexOf(".doc") >= 0) {
				html = DocUtil.WordDOCToPDFWithData(template, variables, userId);
			}

			if (StringUtils.isBlank(html)) {
				return ResultUtil.error("预览模板信息异常:" + "转换文件失败");
			}
			return ResultUtil.data(html.trim().toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResultUtil.error("预览模板信息异常:" + e.getMessage());
		}
	}

	@SystemLog(description = "获取委托协议相关数据(检查的单位、订单、人员、项目信息)", type = LogType.OPERATION)
	@ApiOperation("获取委托协议相关数据(检查的单位、订单、人员、项目信息)")
	@GetMapping("getEntrustData")
	public Result<Object> getEntrustData(TGroupOrder tGroupOrder) {
		try {
			//订单信息查询
			QueryWrapper<TGroupOrder> tGroupOrderQueryWrapper = new QueryWrapper<>();
			tGroupOrderQueryWrapper.eq("id",tGroupOrder.getId());
			tGroupOrderQueryWrapper.eq("del_flag",0);
			TGroupOrder orderData = itGroupOrderService.getOne(tGroupOrderQueryWrapper);
			//单位信息查询
			TGroupUnit unitData = tGroupUnitService.getById(orderData.getGroupUnitId());
			//单位检测信息查询
			TTestRecord tTestRecord = new TTestRecord();
			tTestRecord.setUnitId(unitData.getId());
			SearchVo searchVo = new SearchVo();
			List<TTestRecord> tTestRecords = tTestRecordService.queryTTestRecordList(tTestRecord,searchVo);
			//人员信息查询
			QueryWrapper<TGroupPerson> tGroupPersonQueryWrapper = new QueryWrapper<>();
			tGroupPersonQueryWrapper.eq("order_id",tGroupOrder.getId());
			tGroupPersonQueryWrapper.eq("del_flag",0);
			List<TGroupPerson> personData = tGroupPersonService.list(tGroupPersonQueryWrapper);
			//项目信息查询
			QueryWrapper<TOrderGroup> tOrderGroupQueryWrapper = new QueryWrapper<>();
			tOrderGroupQueryWrapper.eq("group_order_id",tGroupOrder.getId());
			tOrderGroupQueryWrapper.eq("del_flag",0);
			List<TOrderGroup> goods = tOrderGroupService.list(tOrderGroupQueryWrapper);
			for(TOrderGroup tOrderGroup : goods){
				QueryWrapper<TOrderGroupItem> tOrderGroupItemQueryWrapper = new QueryWrapper<>();
				tOrderGroupItemQueryWrapper.eq("group_id",tOrderGroup.getId());
				tOrderGroupItemQueryWrapper.eq("del_flag",0);
				tOrderGroupItemQueryWrapper.orderByAsc("order_num").orderByAsc("name");
				List<TOrderGroupItem> tOrderGroupItems = tOrderGroupItemService.list(tOrderGroupItemQueryWrapper);
				String projects = "";
				Integer prices = 0;
				for(TOrderGroupItem tOrderGroupItem : tOrderGroupItems){
					if(projects.trim().length()>0){
						projects += "、" + tOrderGroupItem.getName();
					}else{
						projects += tOrderGroupItem.getName();
					}
					if(tOrderGroupItem.getSalePrice()!=null){
						prices += tOrderGroupItem.getSalePrice().intValue();
					}
				}
				tOrderGroup.setProjects(projects);
				tOrderGroup.setPrices(prices);
			}
			HashMap<String, Object> stringObjectHashMap = new HashMap<>();
			stringObjectHashMap.put("orderData", orderData);
			stringObjectHashMap.put("unitData", unitData);
			stringObjectHashMap.put("personData", personData);
			stringObjectHashMap.put("goods", goods);
			stringObjectHashMap.put("tTestRecords", tTestRecords);
			return ResultUtil.data(stringObjectHashMap);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultUtil.error("查询异常:" + e.getMessage());
		}
	}


	/**
	 * 功能描述：预览模板信息数据(健康证)
	 *
	 * @param tTemplate    查询参数
	 * @return
	 */
//	@SystemLog(description = "预览模板信息数据(健康证)", type = LogType.OPERATION)
	@ApiOperation("预览模板信息数据(健康证)")
	@PostMapping("/getTemplatePreviewHealthyData")
	public Result<Object> getTemplatePreviewHealthyData(@RequestBody TTemplate tTemplate) {
		try {
			if(tTemplate== null || (StringUtils.isBlank(tTemplate.getId()) && StringUtils.isBlank(tTemplate.getBaseProjectId()) )){
				return ResultUtil.error("查询模板信息失败:" + "参数为空");
			}
			TTemplate res = null;
			String templateData = tTemplate.getTemplateData();
			String userId = securityUtil.getCurrUser().getId();
			QueryWrapper<TTemplate> queryWrapper = new QueryWrapper<>();
			queryWrapper.and(i -> i.eq("t_template.status", 1));
			queryWrapper.and(i -> i.eq("t_template.del_flag", 0));

			if (StringUtils.isNotBlank(tTemplate.getId())) {
				queryWrapper.and(i -> i.eq("t_template.id", tTemplate.getId()));
			}
			if (StringUtils.isNotBlank(tTemplate.getBaseProjectId())) {
				queryWrapper.and(i -> i.eq("t_template.base_project_id", tTemplate.getBaseProjectId()));
			}
			List<TTemplate> list = tTemplateService.list(queryWrapper);
			if (list.size() > 0) {
				res = list.get(0);
			} else {
				return ResultUtil.error("查询模板信息失败:" + "当前项目没有模板");
			}
			if (res == null || StringUtils.isBlank(res.getContent())) {
				return ResultUtil.error("查询模板信息失败:" + "当前项目没有模板文件，请上传");
			}
			String template = res.getContent();
			Map<String, Object> variables = new HashMap<>();
			//设置日期格式
			SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日");
			variables.put("date", df.format(new Date()));
			if (StringUtils.isNotBlank(templateData)) {
				Map<String, Object> jsonToMap = JSONUtil.parseJSON2Map(templateData);
				variables.putAll(jsonToMap);
			}
			//获取请求ip
			String ip = ipInfoUtil.getIpAddr(request);
			String html = "";
			if (template.indexOf(".docx") >= 0) {
				html = DocUtil.WordDOCXToPDFWithHealthyData(template, variables, userId, ip);
			} else if (template.indexOf(".doc") >= 0) {
				html = DocUtil.WordDOCToPDFWithData(template, variables, userId);
			}

			if (StringUtils.isBlank(html)) {
				return ResultUtil.error("预览模板信息异常:" + "转换文件失败");
			}
			return ResultUtil.data(html.trim().toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResultUtil.error("预览模板信息异常:" + e.getMessage());
		}
	}

	/**
	 * 功能描述：导出数据
	 *
	 * @param response  请求参数
	 * @param tTemplate 查询参数
	 * @return
	 */
	@SystemLog(description = "导出模板信息数据", type = LogType.OPERATION)
	@ApiOperation("导出模板信息数据")
	@PostMapping("/downloadTemplate")
	public void downloadTemplate(HttpServletResponse response, TTemplate tTemplate, String templateData) {
		try {
			TTemplate res = tTemplateService.getById(tTemplate.getId());
			String template = res.getContent();
			Map<String, Object> variables = new HashMap<>();
			if (StringUtils.isNotBlank(templateData)) {
				Map jsonToMap = JSONObject.parseObject(templateData);
				variables.putAll(jsonToMap);
			}

			if (template.indexOf(".docx") >= 0) {
				DocUtil.getBuild(template, variables, response);
			} else if (template.indexOf(".doc") >= 0) {
				DocUtil.getBuild(template, variables, response);
			}
		} catch (IOException e) {

			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 功能描述：导出总检报告
	 *
	 * @param response  请求参数
	 * @return
	 */
	@SystemLog(description = "导出总检报告", type = LogType.OPERATION)
	@ApiOperation("导出总检报告")
	@PostMapping("/exportTemplate")
	public void exportTemplate(HttpServletResponse response, String url) {
		try {
			String classPath = DocUtil.getClassPath();
			String file = classPath.split(":")[0] + ":" + DocUtil.basePath + url.split("/tempFileUrl/tempfile/wordTemplate/")[1];
			//获取word路径
			String[] splitContent = file.split("/");
			String nowPathStart = file.replaceAll(splitContent[splitContent.length - 1],"");
			String fileName = "";
			if(file.indexOf(".pdf") > -1){
				String outPathWord = nowPathStart + splitContent[splitContent.length - 1].replaceAll(".pdf",".docx");
				fileName = outPathWord;
//				fileName = file.replaceAll(".pdf", ".docx");
			}else if(file.indexOf(".Pdf") > -1){
				String outPathWord = nowPathStart + splitContent[splitContent.length - 1].replaceAll(".Pdf",".docx");
				fileName = outPathWord;
//				fileName = file.replaceAll(".Pdf", ".docx");
			}
			File fileOut = new File(fileName);
			if(!fileOut.exists()){
				return;
			}
			FileInputStream fis = new FileInputStream(fileName);
			BufferedInputStream bis = new BufferedInputStream(fis);
			OutputStream outputStream = response.getOutputStream();
			byte[] buffer = new byte[1024];
			int i = bis.read(buffer);
			while (i != -1) {
				outputStream.write(buffer, 0, i);

				i = bis.read(buffer);
			}
			//导出到文件
			response.setHeader("Content-disposition",
					"attachment;filename=" + URLEncoder.encode("模板" + ".docx", StandardCharsets.UTF_8.name()));
			// 定义输出类型
			response.setContentType("application/msword");
			bis.close();
			fis.close();
			outputStream.close();
		} catch (IOException e) {

			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 功能描述：导出总检报告
	 *
	 * @param response  请求参数
	 * @return
	 */
	@SystemLog(description = "导出总检报告", type = LogType.OPERATION)
	@ApiOperation("导出总检报告")
	@PostMapping("/exportTemplatePdf")
	public void exportTemplatePdf(HttpServletResponse response, String url) {
		try {
			String classPath = DocUtil.getClassPath();
			String fileName = "";
			if(url.indexOf(":\\") > -1){
				fileName = classPath.split(":")[0] + url.split(":")[1];
			}else{
				String file = classPath.split(":")[0] + ":" + DocUtil.basePath + url.split("/tempfile/wordTemplate/")[1];
				//获取word路径
				String[] splitContent = file.split("/");
				String nowPathStart = file.replaceAll(splitContent[splitContent.length - 1],"");
				String outPathWord = nowPathStart + splitContent[splitContent.length - 1];
				fileName = outPathWord;
			}
			File fileOut = new File(fileName);
			if(!fileOut.exists()){
				return;
			}
			FileInputStream fis = new FileInputStream(fileName);
			BufferedInputStream bis = new BufferedInputStream(fis);
			OutputStream outputStream = response.getOutputStream();
			byte[] buffer = new byte[1024];
			int i = bis.read(buffer);
			while (i != -1) {
				outputStream.write(buffer, 0, i);

				i = bis.read(buffer);
			}
			//导出到文件
			response.setHeader("Content-disposition",
					"attachment;filename=" + URLEncoder.encode("模板" + ".Pdf", StandardCharsets.UTF_8.name()));
			// 定义输出类型
			response.setContentType("application/pdf");
			bis.close();
			fis.close();
			outputStream.close();
		} catch (IOException e) {

			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 功能描述：导出总检报告
	 *
	 * @param response  请求参数
	 * @return
	 */
	@SystemLog(description = "pdf压缩并导出", type = LogType.OPERATION)
	@ApiOperation("pdf压缩并导出")
	@PostMapping("/exportTemplatePdfZip")
	public void exportTemplatePdfZip(HttpServletRequest request,HttpServletResponse response, String url,String name) {
		try {
			String classPath = DocUtil.getClassPath();
			String fileName = "";
			if(url.indexOf(":\\") > -1){
				fileName = classPath.split(":")[0] + url.split(":")[1];
			}else{
				String file = classPath.split(":")[0] + ":" + DocUtil.basePath + url.split("/tempfile/wordTemplate/")[1];
				//获取word路径
				String[] splitContent = file.split("/");
				String nowPathStart = file.replaceAll(splitContent[splitContent.length - 1],"");
				String outPathWord = nowPathStart + splitContent[splitContent.length - 1];
				fileName = outPathWord;
			}
			/*导出文件*/
			List<Map<String, Object>> fileList = new ArrayList<>();
			String zipFileName="";
			url = fileName;
			File file = new File(url);
			String[] splitName = url.split("-")[0].split("/");
			String personName = splitName[splitName.length-1];
			if (url != null && StringUtils.isNotBlank(url)){// 判断文件是否存在
				file = new File(url);// 获取模板文件
				if (file.exists()) {
					// 取得文件名。
					String filename = file.getName();

					// 以流的形式下载文件。
					InputStream fis = new BufferedInputStream(new FileInputStream(url));
					byte[] buffer = new byte[fis.available()];
					fis.read(buffer);
					fis.close();
					if(filename.contains(".pdf")){
						filename = personName + ".pdf";  //重命名
					}else if(filename.contains(".zip")){
						filename = personName + ".zip";
					}
					Map<String, Object> fileMap = new HashMap<String, Object>();
					fileMap.put("fileName", filename);
					fileMap.put("pdf", buffer);
					fileList.add(fileMap);
				}
			}

			//zip压缩包名称
			zipFileName = name + ".zip";
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			zipFileName = URLEncoder.encode(zipFileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename=" + zipFileName);
			try (OutputStream os = response.getOutputStream()) {
				TemplateUtil.toPDFZip(fileList, os);
			}
		} catch (IOException e) {

			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 功能描述：导出总检报告
	 *
	 * @param map
	 * @param request
	 * @param response
	 * @return
	 */
	@SystemLog(description = "批量生成pdf并压缩导出", type = LogType.OPERATION)
	@ApiOperation("批量生成pdf并压缩导出")
	@PostMapping("/exportTemplatePdfBatch")
	public Result<Object> exportTemplatePdfBatch(@RequestBody Map map,HttpServletRequest request,HttpServletResponse response) {
		String res = "";
		try {
			String name = "";
			/*生成文件*/
			List<String> urls = new ArrayList<>();
			if(map ==null || map.size()==0 || !map.containsKey("data")){
				return ResultUtil.error("生成体检报告异常:参数为空" );
			}
			try{
				ArrayList batchData = (ArrayList)  map.get("data");
				//查询所有模板
				List<TTemplate> list = tTemplateService.list();
				String userId = securityUtil.getCurrUser().getId();
				//获取请求ip
				String ip = ipInfoUtil.getIpAddr(request);
				if(map.get("name") != null){
					ip = ip + map.get("name");
					name = "" + map.get("name");
				}
				urls =TemplateUtil.templateBatchHandDataPdf(batchData,ip,userId,list);
			}catch (Exception e){
				e.printStackTrace();
				return ResultUtil.error("生成体检报告异常:" + e.getMessage());
			}

			/*导出文件*/
			String classPath = DocUtil.getClassPath().split(":")[0];
			List<Map<String, Object>> fileList = new ArrayList<>();
			String zipFileName="";
			for (String url : urls) {
				url = classPath+":" + UploadFileUtils.deletePath + url;
				File file = null;
				String[] splitName = url.split("-")[0].split("/");
				String personName = splitName[splitName.length-1];
				if (url != null && StringUtils.isNotBlank(url)){// 判断文件是否存在
					file = new File(url);// 获取模板文件
					if (file.exists()) {
						// 取得文件名。
						String filename = file.getName();

						// 以流的形式下载文件。
						InputStream fis = new BufferedInputStream(new FileInputStream(url));
						byte[] buffer = new byte[fis.available()];
						fis.read(buffer);
						fis.close();
						if(filename.contains(".Pdf")){
							filename = personName + ".pdf";  //重命名
						}else if(filename.contains(".zip")){
							filename = personName + ".zip";
						}
						Map<String, Object> fileMap = new HashMap<String, Object>();
						fileMap.put("fileName", filename);
						fileMap.put("pdf", buffer);
						fileList.add(fileMap);
					}
				}
			}

			//zip压缩包名称
			zipFileName = name + ".zip";
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			zipFileName = URLEncoder.encode(zipFileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename=" + zipFileName);
			try (OutputStream os = response.getOutputStream()) {
				TemplateUtil.toPDFZip(fileList, os);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return ResultUtil.error("导出报告文件异常:" + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return ResultUtil.error("导出报告文件异常:" + e.getMessage());
		}
		return  ResultUtil.data(res);
	}

	/**
	 * 生成体检报告
	 * @param pdfPaths  pdf路径集合
	 * @return
	 */
	@ApiOperation("生成体检报告")
	@PostMapping("/generatePhysicalExaminationReport")
	public Result<Object>  generatePhysicalExaminationReport(@RequestParam String[] pdfPaths,@RequestParam String personId){
		try {
			if(pdfPaths==null||pdfPaths.length==0||StringUtils.isBlank(personId)){
				return ResultUtil.error("生成体检报告异常:参数为空" );
			}
			for (int i = 0; i < pdfPaths.length; i++) {
				if(StringUtils.isBlank(pdfPaths[i])){
					return ResultUtil.error("生成体检报告异常:路径参数为空" );
				}
			}
			String path = "";
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
			String DataStr = format.format(new Date());
			String classPath = DocUtil.getClassPath();
			path =classPath.split(":")[0]+":" +DocUtil.basePath + "pdf/report/"+personId+"-" + DataStr + ".pdf";
            String result = "/tempfile/wordTemplate/pdf/report/"+personId+"-" + DataStr + ".pdf";
            PdfUtil.MergePdf(pdfPaths,path);
			return ResultUtil.data(result);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultUtil.error("生成体检报告异常:" + e.getMessage());
		}
	}
	/**
	 * 根据人员Id查询体检报告结果数据
	 * @param personId  人员Id
	 * @return
	 */
	@ApiOperation("根据人员Id查询体检报告结果数据")
	@GetMapping("/generateReportByPersonId")
	public Result<Object>  generateReportByPersonId(@RequestParam String personId){
		try {
			Map<String, Object> result = new HashMap<>();
			if(StringUtils.isBlank(personId)){
				return ResultUtil.error("查询体检报告结果数据异常:人员Id为空" );
			}
			final int[] index = {0};
			//第一步查询人员信息
			Map<String, Object> mapPerson = tGroupPersonService.getGroupPersonInfo(personId, "");
			if(mapPerson==null ||mapPerson.size()==0){
				return ResultUtil.error("查询体检报告结果数据异常:查询人员信息失败" );
			}
			final Exception[] ex = new Exception[1];
			//体检类型
			String physicalType = mapPerson.get("physical_type").toString();
			//通过线程池创建线程
//			ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
//					.setNamePrefix("generateReportByPersonId").build();
//			ExecutorService singleThreadPool = new ThreadPoolExecutor(1, 10,
//					0L, TimeUnit.MILLISECONDS,
//					new LinkedBlockingQueue<Runnable>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());
//			final Boolean[] isThread = {true};
//			singleThreadPool.execute(() -> {
//				try {
//					if("职业体检".equals(physicalType)) {
						//查询职业病、目标禁忌症、危害因素
						TOrderGroup orderGroup = orderGroupService.getById(mapPerson.get("group_id").toString());
						result.put("orderGroup", orderGroup);
//					}
//					index[0]++;getGroupByOfficeId
//				} catch (Exception e) {
//					e.printStackTrace();
//					ex[0] = e;
//					isThread[0] = false;
//				}
//			});
			//第二步查询组合项目结果
//			singleThreadPool.execute(() -> {
//				try {
					List<TDepartResult> departResults = tTemplateService.getDepartResultList( personId,mapPerson.get("group_id").toString());
					result.put("departResults", departResults);
//					index[0]++;
//				} catch (Exception e) {
//					e.printStackTrace();
//					ex[0] = e;
//					isThread[0] = false;
//				}
//			});
			//第三部查询分项项目结果
//			singleThreadPool.execute(() -> {
//				try {
					List<TDepartItemResult> departItemResults = tTemplateService.getDepartItemResultList( personId,mapPerson.get("group_id").toString());
					result.put("departItemResults", departItemResults);
//					index[0]++;
//				} catch (Exception e) {
//					e.printStackTrace();
//					ex[0] = e;
//					isThread[0] = false;
//				}
//			});
//			//既往病史数据
//			singleThreadPool.execute(() -> {
//				try {
					QueryWrapper<TPastMedicalHistory> queryWrapper = new QueryWrapper<>();
					queryWrapper.and(i -> i.eq("t_past_medical_history.person_id", personId));
					List<TPastMedicalHistory> pastMedicalHistory = tPastMedicalHistoryService.list(queryWrapper);
					mapPerson.put("pastMedicalHistory",pastMedicalHistory);
//					index[0]++;
//				} catch (Exception e) {
//					e.printStackTrace();
//					ex[0] = e;
//					isThread[0] = false;
//				}
//			});
			//弃检项目
//			singleThreadPool.execute(() -> {
//				try {
					QueryWrapper<RelationPersonProjectCheck> queryWrapperRelationPersonProjectCheck = new QueryWrapper<>();
					queryWrapperRelationPersonProjectCheck.and(i -> i.eq("relation_person_project_check.person_id", personId));
					queryWrapperRelationPersonProjectCheck.and(i -> i.eq("relation_person_project_check.state", 2));
					List<RelationPersonProjectCheck> personProjectCheck = relationPersonProjectCheckService.list(queryWrapperRelationPersonProjectCheck);
					mapPerson.put("personProjectCheck",personProjectCheck);
//					index[0]++;
//				} catch (Exception e) {
//					e.printStackTrace();
//					ex[0] = e;
//					isThread[0] = false;
//				}
//			});

//			//条码
//			singleThreadPool.execute(() -> {
//				try {
					String testNum = mapPerson.get("test_num").toString();
					String testNumCode = (GoogleTestNumBarCodeUtils.generatorBase64Barcode(testNum, testNum));
					mapPerson.put("testNumCode",testNumCode.split(",")[1]);
					index[0]++;
//				} catch (Exception e) {
//					e.printStackTrace();
//					ex[0] = e;
//					isThread[0] = false;
//				}
//			});
//			//总检结果
//			singleThreadPool.execute(() -> {
//				try {
					TInspectionRecord inspectionRecord = tInspectionRecordService.getByPersonId(personId);
					mapPerson.put("inspectionRecord",inspectionRecord);
					index[0]++;
//				} catch (Exception e) {
//					e.printStackTrace();
//					ex[0] = e;
//					isThread[0] = false;
//				}
//			});
//
//			//复检项目
//			singleThreadPool.execute(() -> {
//				try {
					QueryWrapper<TReviewProject> queryWrapperReviewRecord = new QueryWrapper<>();
					queryWrapperReviewRecord.lambda().and(i -> i.eq(TReviewProject::getPersonId, personId));
					queryWrapperReviewRecord.lambda().and(i -> i.eq(TReviewProject::getDelFlag, 0));
//					queryWrapperReviewRecord.lambda().and(i -> i.eq(TReviewProject::getIsPass, 1));
					List<TReviewProject> reviewProjectsList =tReviewProjectService.list(queryWrapperReviewRecord);
					/*QueryWrapper<TDepartResult> queryWrapperReviewRecord = new QueryWrapper<>();
					queryWrapperReviewRecord.lambda().and(i -> i.eq(TDepartResult::getPersonId, personId));
					queryWrapperReviewRecord.lambda().and(i -> i.eq(TDepartResult::getDelFlag, 0));
					queryWrapperReviewRecord.lambda().and(i -> i.like(TDepartResult::getGroupItemName, "(复)"));
					queryWrapperReviewRecord.orderByAsc("check_date");
					List<TDepartResult> reviewProjectsList = tDepartResultService.list(queryWrapperReviewRecord);*/
					mapPerson.put("reviewProjectsList",reviewProjectsList);
//					index[0]++;
//				} catch (Exception e) {
//					e.printStackTrace();
//					ex[0] = e;
//					isThread[0] = false;
//				}
//			});


			//查询人员的职业史数据
//			singleThreadPool.execute(() -> {
//				try {
					if(!"从业体检".equals(physicalType)) {
						QueryWrapper<TCareerHistory> queryWrapperCareerHistory = new QueryWrapper<>();
						queryWrapperCareerHistory.lambda().and(i -> i.eq(TCareerHistory::getPersonId, personId));
						List<TCareerHistory> careerHistory =tCareerHistoryService.list(queryWrapperCareerHistory);
						mapPerson.put("careerHistory",careerHistory);

						//症状查询
						QueryWrapper<TSymptom> queryWrapperSymptom = new QueryWrapper<>();
						queryWrapperSymptom.and(i -> i.eq("t_symptom.person_id", personId));
						List<TSymptom> symptom =tSymptomService.list(queryWrapperSymptom);
						mapPerson.put("symptom",symptom);
					}
//					index[0]++;
//				} catch (Exception e) {
//					e.printStackTrace();
//					ex[0] = e;
//					isThread[0] = false;
//				}
//			});
//			while (isThread[0]){
//				if(index[0] == 9){
					result.put("mapPerson",mapPerson);

//			if(isThread[0]){	break;
//	}
//}
//			singleThreadPool.shutdown();
				return ResultUtil.data(result);
//			}
//			return ResultUtil.error(ex[0].getMessage());

		} catch (Exception e) {
			e.printStackTrace();
			return ResultUtil.error("查询体检报告结果数据异常:" + e.getMessage());
		}
	}


	@ApiOperation("生成体检报告(先合并word 再 转pdf)")
	@PostMapping("/generateWordReport")
	public Result<Object> generateWordReport(@RequestBody Map map){
 		String res = "";
 		if(map ==null || map.size()==0 || !map.containsKey("wordData")|| !map.containsKey("personId")){
			return ResultUtil.error("生成体检报告异常:参数为空" );
		}
		try {
			String personId = map.get("personId").toString();
			ArrayList wordData =  (ArrayList)   map.get("wordData");
			//查询所有模板
			List<TTemplate> list = tTemplateService.list();
			//获取请求ip
			String ip = ipInfoUtil.getIpAddr(request);
			res =TemplateUtil. templateHandData(personId,ip,wordData,list);
		}catch (Exception e){
			e.printStackTrace();
			return ResultUtil.error("生成体检报告异常:" + e.getMessage());
		}
 		return  ResultUtil.data(res);
	}

	@ApiOperation("生成健康证(先合并word 再 转pdf)")
	@PostMapping("/generateWordReportHealthy")
	public Result<Object> generateWordReportHealthy(@RequestBody Map map){
		String res = "";
		if(map ==null || map.size()==0 || !map.containsKey("wordData")|| !map.containsKey("wordName")){
			return ResultUtil.error("生成体检报告异常:参数为空" );
		}
		try {
			String wordName = map.get("wordName").toString();
			ArrayList wordData =  (ArrayList)   map.get("wordData");
			//查询所有模板
			List<TTemplate> list = tTemplateService.list();
			//获取请求ip
			String ip = ipInfoUtil.getIpAddr(request);
			res =TemplateUtil. templateHandDataHealthy(wordName,ip,wordData,list);
		}catch (Exception e){
			e.printStackTrace();
			return ResultUtil.error("生成体检报告异常:" + e.getMessage());
		}
		return  ResultUtil.data(res);
	}

	@ApiOperation("根据主键来获取模板信息数据")
	@PostMapping("generateReportByPersonIds")
	public Result<Object> generateReportByPersonIds(@RequestBody String[] ids) {
		try {
			List<Map<String, Object>>results  = new ArrayList<>();
			if(ids == null || ids.length == 0){
				return ResultUtil.error("查询体检报告结果数据异常:人员Id为空" );
			}
			//第一步查询人员信息
			List <Map<String, Object>> mapPersons = tGroupPersonService.getGroupPersonInfoByIds(Arrays.asList(ids));

			if(mapPersons==null ||mapPersons.size()==0){
				return ResultUtil.error("查询体检报告结果数据异常:查询人员信息失败" );
			}
			List<String> groupIds = new ArrayList<>();
			mapPersons.stream().forEach(m -> {
				if (m.containsKey("group_id")) {
					//没得才添加
					if(!groupIds.contains(m.get("group_id").toString())){
						groupIds.add(m.get("group_id").toString());
					}
				}
			});
			//查询职业病、目标禁忌症、危害因素（订单）
			List<TOrderGroup> orderGroup = orderGroupService.listByIds (groupIds);
			//查询组合项检查结果
			List<TDepartResult> departResults = tTemplateService.getDepartResultListByPersonIds( Arrays.asList(ids),groupIds);
			for(TDepartResult tDepartResult : departResults){
				if(tDepartResult!=null && tDepartResult.getCheckSign()!=null){
					//字节转字符串
					byte[] blob = (byte[]) tDepartResult.getCheckSign();
					if(blob!=null){
						String avatarNow = new String(blob);
						if(avatarNow.indexOf("/dcm") > -1){
							tDepartResult.setCheckSignPath(avatarNow);
						}
					}
				}
			}
			//查询基础项目检查结果
			List<TDepartItemResult> departItemResults = tTemplateService.getDepartItemResultListByPersonIds( Arrays.asList(ids),groupIds);
			//症状查询
			QueryWrapper<TPastMedicalHistory> queryWrapper = new QueryWrapper<>();
			queryWrapper.and(i -> i.in("t_past_medical_history.person_id", Arrays.asList(ids)));
			List<TPastMedicalHistory> pastMedicalHistory = tPastMedicalHistoryService.list(queryWrapper);
			//查询弃检项目
			QueryWrapper<RelationPersonProjectCheck> queryWrapperRelationPersonProjectCheck = new QueryWrapper<>();
			queryWrapperRelationPersonProjectCheck.and(i -> i.in("relation_person_project_check.person_id", Arrays.asList(ids)));
			queryWrapperRelationPersonProjectCheck.and(i -> i.eq("relation_person_project_check.state", 2));
			List<RelationPersonProjectCheck> personProjectCheck = relationPersonProjectCheckService.list(queryWrapperRelationPersonProjectCheck);
			//查询总检结果
			QueryWrapper<TInspectionRecord> queryWrapperInspectionRecord = new QueryWrapper<>();
			queryWrapperInspectionRecord.lambda().and(i -> i.in(TInspectionRecord::getPersonId, Arrays.asList(ids)));
			queryWrapperInspectionRecord.lambda().and(i -> i.eq(TInspectionRecord::getDelFlag, 0));
			List<TInspectionRecord> inspectionRecord = tInspectionRecordService.list(queryWrapperInspectionRecord);
			for(TInspectionRecord tInspectionRecord : inspectionRecord){
				if(tInspectionRecord!=null && tInspectionRecord.getInspectionAutograph()!=null){
					//字节转字符串
					byte[] blob = (byte[]) tInspectionRecord.getInspectionAutograph();
					if(blob!=null){
						String avatarNow = new String(blob);
						if(avatarNow.indexOf("/dcm") > -1){
							tInspectionRecord.setInspectionAutograph(avatarNow);
						}
					}
				}
			}
			//查询复检项目
			QueryWrapper<TReviewProject> queryWrapperReviewRecord = new QueryWrapper<>();
			queryWrapperReviewRecord.lambda().and(i -> i.in(TReviewProject::getPersonId, Arrays.asList(ids)));
			queryWrapperReviewRecord.lambda().and(i -> i.eq(TReviewProject::getDelFlag, 0));
//			queryWrapperReviewRecord.lambda().and(i -> i.eq(TReviewProject::getIsPass, 1));
			List<TReviewProject> reviewProjectsList =tReviewProjectService.list(queryWrapperReviewRecord);
			/*QueryWrapper<TDepartResult> queryWrapperReviewRecord = new QueryWrapper<>();
			queryWrapperReviewRecord.lambda().and(i -> i.in(TDepartResult::getPersonId, Arrays.asList(ids)));
			queryWrapperReviewRecord.lambda().and(i -> i.eq(TDepartResult::getDelFlag, 0));
			queryWrapperReviewRecord.lambda().and(i -> i.like(TDepartResult::getGroupItemName, "(复)"));
			queryWrapperReviewRecord.orderByAsc("check_date");
			List<TDepartResult> reviewProjectsList = tDepartResultService.list(queryWrapperReviewRecord);*/

			//查询职业史
			QueryWrapper<TCareerHistory> queryWrapperCareerHistory = new QueryWrapper<>();
			queryWrapperCareerHistory.lambda().and(i -> i.in(TCareerHistory::getPersonId, Arrays.asList(ids)));
			List<TCareerHistory> careerHistory =tCareerHistoryService.list(queryWrapperCareerHistory);

			//症状查询
			QueryWrapper<TSymptom> queryWrapperSymptom = new QueryWrapper<>();
			queryWrapperSymptom.and(i -> i.in("t_symptom.person_id", Arrays.asList(ids)));
			List<TSymptom> symptom =tSymptomService.list(queryWrapperSymptom);

			//问诊查询
			QueryWrapper<TInterrogation> tInterrogationQueryWrapper = new QueryWrapper<>();
			tInterrogationQueryWrapper.eq("del_flag",0);
			tInterrogationQueryWrapper.in("person_id",ids);
			tInterrogationQueryWrapper.orderByDesc("create_time");
			List<TInterrogation> tInterrogationList = interrogationService.list(tInterrogationQueryWrapper);

			//查询阳性结果(目前仅健康和从业)
			QueryWrapper<TPositivePerson> tPositivePersonQueryWrapper = new QueryWrapper<>();
			tPositivePersonQueryWrapper.in("person_id",ids);
			tPositivePersonQueryWrapper.orderByAsc("order_num");
			List<TPositivePerson> tPositivePeople = tPositivePersonService.list(tPositivePersonQueryWrapper);

			//查询危害因素结论（目前仅职业和健康）
//			QueryWrapper<TdTjBadrsns> tdTjBadrsnsQueryWrapper = new QueryWrapper<>();
//			tdTjBadrsnsQueryWrapper.in("FK_BHK_ID",ids);
//			tdTjBadrsnsQueryWrapper.orderByAsc("EXAM_CONCLUSION_CODE");
//			List<TdTjBadrsns> tdTjBadrsns = tdTjBadrsnsService.list(tdTjBadrsnsQueryWrapper);
			List<TdTjBadrsns> tdTjBadrsns = tdTjBadrsnsService.selectListByIds(Arrays.asList(ids));

			for (Map<String, Object> mapPerson:mapPersons) {
				if(mapPerson!=null  && mapPerson.size()>0 && mapPerson.containsKey("id") ){

					if(mapPerson.get("avatar")!=null){
						//字节转字符串
						byte[] blob = (byte[]) mapPerson.get("avatar");
						if(blob!=null){
							String avatarNow = new String(blob);
							if(avatarNow.indexOf("/dcm") > -1){
								mapPerson.put("avatar",avatarNow);
							}
						}
					}
					Map<String, Object> result = new HashMap<>();
					//人员id
//					String personId = mapPersons.get(0).get("id").toString();
					String personId = mapPerson.get("id").toString();
					//获取人员问诊信息
					TInterrogation tInterrogation = tInterrogationList.stream().filter(ii -> personId.contains(ii.getPersonId())).findFirst().orElse(null);
					if(tInterrogation!=null){
						mapPerson.put("work_year",tInterrogation.getWorkYear());
						mapPerson.put("work_month",tInterrogation.getWorkMonth());
						mapPerson.put("exposure_work_year",tInterrogation.getExposureWorkYear());
						mapPerson.put("exposure_work_month",tInterrogation.getExposureWorkMonth());
						mapPerson.put("exposure_start_date",tInterrogation.getExposureStartDate());
						mapPerson.put("nation",tInterrogation.getNation());
						mapPerson.put("check_num",tInterrogation.getCheckNum());
						mapPerson.put("disease_name",tInterrogation.getDiseaseName());
						mapPerson.put("is_cured",tInterrogation.getIsCured());
						mapPerson.put("menarche",tInterrogation.getMenarche());
						mapPerson.put("period",tInterrogation.getPeriod());
						mapPerson.put("cycle",tInterrogation.getCycle());
						mapPerson.put("last_menstruation",tInterrogation.getLastMenstruation());
						mapPerson.put("existing_children",tInterrogation.getExistingChildren());
						mapPerson.put("abortion",tInterrogation.getAbortion());
						mapPerson.put("premature",tInterrogation.getPremature());
						mapPerson.put("death",tInterrogation.getDeath());
						mapPerson.put("abnormal_fetus",tInterrogation.getAbnormalFetus());
						mapPerson.put("smoke_state",tInterrogation.getSmokeState());
						mapPerson.put("package_every_day",tInterrogation.getPackageEveryDay());
						mapPerson.put("smoke_year",tInterrogation.getSmokeYear());
						mapPerson.put("drink_state",tInterrogation.getDrinkState());
						mapPerson.put("ml_every_day",tInterrogation.getMlEveryDay());
						mapPerson.put("drink_year",tInterrogation.getDrinkYear());
						mapPerson.put("other_info",tInterrogation.getOtherInfo());
						mapPerson.put("symptom",tInterrogation.getSymptom());
						mapPerson.put("education",tInterrogation.getEducation());
						mapPerson.put("family_address",tInterrogation.getFamilyAddress());
						mapPerson.put("menstrual_history",tInterrogation.getMenstrualHistory());
						mapPerson.put("menstrual_info",tInterrogation.getMenstrualInfo());
						mapPerson.put("allergies",tInterrogation.getAllergies());
						mapPerson.put("allergies_info",tInterrogation.getAllergiesInfo());
						mapPerson.put("birthplace_code",tInterrogation.getBirthplaceCode());
						mapPerson.put("birthplace_name",tInterrogation.getBirthplaceName());
						mapPerson.put("family_history",tInterrogation.getFamilyHistory());
						mapPerson.put("past_medical_history_other_info",tInterrogation.getPastMedicalHistoryOtherInfo());
						mapPerson.put("wz_check_doctor",tInterrogation.getWzCheckDoctor());
						mapPerson.put("wz_check_time",tInterrogation.getWzCheckTime());
						mapPerson.put("wz_check_autograph",tInterrogation.getWzCheckAutograph());

						mapPerson.put("marriage_date",tInterrogation.getMarriageDate());//婚姻史-结婚日期
						mapPerson.put("spouse_radiation_situation",tInterrogation.getSpouseRadiationSituation());//婚姻史-配偶接触放射线情况
						mapPerson.put("spouse_health_situation",tInterrogation.getSpouseHealthSituation());//婚姻史-配偶接触放射线情况
						mapPerson.put("pregnancy_count",tInterrogation.getPregnancyCount());//孕次
						mapPerson.put("live_birth",tInterrogation.getLiveBirth());//活产
						mapPerson.put("abortion_small",tInterrogation.getAbortionSmall());//自然流产
						mapPerson.put("multiparous",tInterrogation.getMultiparous());//多胎
						mapPerson.put("ectopic_pregnancy",tInterrogation.getEctopicPregnancy());//异位妊娠
						mapPerson.put("boys",tInterrogation.getBoys());//现有男孩
						mapPerson.put("boys_birth",tInterrogation.getBoysBirth());//现有男孩-出生日期
						mapPerson.put("girls",tInterrogation.getGirls());//现有女孩
						mapPerson.put("girls_birth",tInterrogation.getGirlsBirth());//现有女孩-出生日期
						mapPerson.put("infertility_reason",tInterrogation.getInfertilityReason());//不孕不育原因
						mapPerson.put("childrens_health",tInterrogation.getChildrensHealth());//子女健康情况

						mapPerson.put("quit_somking",tInterrogation.getQuitSomking());//戒烟年数
						mapPerson.put("job",tInterrogation.getJob());//职务/职称
						mapPerson.put("zip_code",tInterrogation.getZipCode());//邮政编码
					}
					if(mapPerson.get("wz_check_autograph")!=null){
						//字节转字符串
						byte[] blob = (byte[]) mapPerson.get("wz_check_autograph");
						if(blob!=null){
							String avatarNow = new String(blob);
							if(avatarNow.indexOf("/dcm") > -1){
								mapPerson.put("wz_check_autograph",avatarNow);
							}
						}
					}

					//订单Id
					String groupId = mapPersons.get(0).get("group_id").toString();
					result.put("orderGroup", orderGroup.stream().filter(ii -> groupId.contains(ii.getId())).findFirst().orElse(null));
					result.put("departResults", departResults.stream().filter(ii -> personId.contains(ii.getPersonId())).collect(Collectors.toList()));
					result.put("departItemResults", departItemResults.stream().filter(ii -> personId.contains(ii.getPersonId())).collect(Collectors.toList()));
					if(mapPerson.get("physical_type")!=null && (mapPerson.get("physical_type").toString().equals("健康体检") || mapPerson.get("physical_type").toString().equals("从业体检"))){
						mapPerson.put("tPositivePeople", tPositivePeople.stream().filter(ii -> personId.contains(ii.getPersonId())).collect(Collectors.toList()));
					}
					if(mapPerson.get("hazard_factors_text")!=null && mapPerson.get("hazard_factors")!=null && mapPerson.get("physical_type")!=null && (mapPerson.get("physical_type").toString().equals("职业体检") || mapPerson.get("physical_type").toString().equals("放射体检"))){

						String hazardFactorsNow = mapPerson.get("hazard_factors").toString();
						String hazardFactorsTextNow = mapPerson.get("hazard_factors_text").toString();
						String[] hazardFactorsNowArr = new String[0];
						String[] hazardFactorsTextNowArr = new String[0];
						if(StringUtils.isNotBlank(hazardFactorsNow)){
							hazardFactorsNowArr = hazardFactorsNow.split("\\|");
						}
						if(StringUtils.isNotBlank(hazardFactorsTextNow)){
							if(hazardFactorsTextNow.indexOf("\\|") > -1){
								hazardFactorsTextNowArr = hazardFactorsTextNow.split("\\|");
							}else if(hazardFactorsNowArr!=null && hazardFactorsNowArr.length>0){
								hazardFactorsTextNowArr = (hazardFactorsTextNow.replaceAll("\\|","、")).split("、");
							}else{
								hazardFactorsTextNowArr =hazardFactorsTextNow.split("\\|");
							}
						}
						if(tdTjBadrsns!=null && tdTjBadrsns.size()>0){
							List<TdTjBadrsns> tdTjBadrsnsList = tdTjBadrsns.stream().filter(ii -> personId.contains(ii.getFkBhkId())).collect(Collectors.toList());
							List<TdTjBadrsns> tdTjBadrsnsListNow = new ArrayList<>();
							for(TdTjBadrsns tdTjBadrsnsOne : tdTjBadrsnsList){
								if(tdTjBadrsnsOne!=null && StringUtils.isNotBlank(tdTjBadrsnsOne.getBadrsnCode()) && hazardFactorsNowArr!=null && hazardFactorsNowArr.length>0){
									int num = 0;
									for(String s : hazardFactorsNowArr){
										if(StringUtils.isNotBlank(s) && tdTjBadrsnsOne.getBadrsnCode().equals(s) && hazardFactorsTextNowArr!=null && hazardFactorsTextNowArr.length>0 && hazardFactorsTextNowArr[num]!=null && StringUtils.isNotBlank(hazardFactorsTextNowArr[num])){
											tdTjBadrsnsOne.setTypeName(hazardFactorsTextNowArr[num]);
											tdTjBadrsnsListNow.add(tdTjBadrsnsOne);
										}
										num ++;
									}
								}
							}
							mapPerson.put("tdTjBadrsns", tdTjBadrsnsListNow);
						}
						else{
							mapPerson.put("tdTjBadrsns", new ArrayList<>());
						}
					}
					mapPerson.put("symptom",symptom.stream().filter(ii -> personId.contains(ii.getPersonId())).collect(Collectors.toList()));
					mapPerson.put("careerHistory",careerHistory.stream().filter(ii -> personId.contains(ii.getPersonId())).collect(Collectors.toList()));
					mapPerson.put("reviewProjectsList",reviewProjectsList.stream().filter(ii -> personId.contains(ii.getPersonId())).collect(Collectors.toList()));
					mapPerson.put("personProjectCheck",personProjectCheck.stream().filter(ii -> personId.contains(ii.getPersonId())).collect(Collectors.toList()));
					mapPerson.put("pastMedicalHistory",pastMedicalHistory.stream().filter(ii -> personId.contains(ii.getPersonId())).collect(Collectors.toList()));
					mapPerson.put("inspectionRecord",inspectionRecord.stream().filter(ii -> personId.contains(ii.getPersonId())).findFirst().orElse(null));
					result.put("mapPerson",mapPerson);
					String testNum = mapPerson.get("test_num").toString();
					String testNumCode = (GoogleTestNumBarCodeUtils.generatorBase64Barcode(testNum, testNum));
					mapPerson.put("testNumCode",testNumCode.split(",")[1]);
					results.add(result);
				}
			}

			return ResultUtil.data(results);

		} catch (Exception e) {
			e.printStackTrace();
			return ResultUtil.error("查询体检报告结果数据异常:" + e.getMessage());
		}
	}

	@ApiOperation("根据主键来获取模板信息数据")
	@PostMapping("generateReportByPersonIdsTypeStatus")
	public Result<Object> generateReportByPersonIdsTypeStatus(@RequestBody String[] ids) {
		try {
			List<Map<String, Object>>results  = new ArrayList<>();
			if(ids == null || ids.length == 0){
				return ResultUtil.error("查询体检报告结果数据异常:人员Id为空" );
			}
			List<String> personIds = new ArrayList<>() ;
			//是否查询第一次
			if(socketConfig.getInitialMerger()){
				personIds = tTemplateService.getPersonIdsByReviewPersonIds( Arrays.asList(ids));
			}
			else{
				personIds = Arrays.asList(ids);
			}
			List<String> finalPersonIds = personIds;

			//第一步查询人员信息
			List <Map<String, Object>> mapPersons = tGroupPersonService.getGroupPersonInfoByIdsTypeStatus(Arrays.asList(ids));
			if(mapPersons==null ||mapPersons.size()==0){
				return ResultUtil.error("查询体检报告结果数据异常:查询人员信息失败" );
			}
			List<String> groupIds = new ArrayList<>();
			mapPersons.stream().forEach(m -> {
				if (m.containsKey("group_id")) {
					//没得才添加
					if(!groupIds.contains(m.get("group_id").toString())){
						groupIds.add(m.get("group_id").toString());
					}
				}
			});
			//查询职业病、目标禁忌症、危害因素（订单）
			List<TOrderGroup> orderGroup = orderGroupService.listByIds (groupIds);
			//查询组合项检查结果
			List<TDepartResult> departResults = new ArrayList<>();
			//是否查询第一次
			if(socketConfig.getInitialMerger()){
				departResults = tTemplateService.getDepartResultListByReviewPersonIds( Arrays.asList(ids),groupIds);
			}
			else{
				departResults = tTemplateService.getDepartResultListByPersonIds( Arrays.asList(ids),groupIds);
			}


			for(TDepartResult tDepartResult : departResults){
				if(tDepartResult!=null && tDepartResult.getCheckSign()!=null){
					//字节转字符串
					byte[] blob = (byte[]) tDepartResult.getCheckSign();
					if(blob!=null){
						String avatarNow = new String(blob);
						if(avatarNow.indexOf("/dcm") > -1){
							tDepartResult.setCheckSignPath(avatarNow);
						}
					}
				}
			}
			//查询基础项目检查结果
			List<TDepartItemResult> departItemResults = new ArrayList<>();
			//是否查询第一次
			if(socketConfig.getInitialMerger()){
				departItemResults = tTemplateService.getDepartItemResultListByReviewPersonIds( Arrays.asList(ids),groupIds);
			}
			else{
				departItemResults = tTemplateService.getDepartItemResultListByPersonIds( Arrays.asList(ids),groupIds);
			}

			//查询弃检项目
			QueryWrapper<RelationPersonProjectCheck> queryWrapperRelationPersonProjectCheck = new QueryWrapper<>();
			queryWrapperRelationPersonProjectCheck.and(i -> i.in("relation_person_project_check.person_id", Arrays.asList(ids)));
			queryWrapperRelationPersonProjectCheck.and(i -> i.eq("relation_person_project_check.state", 2));
			List<RelationPersonProjectCheck> personProjectCheck = relationPersonProjectCheckService.list(queryWrapperRelationPersonProjectCheck);
			//查询总检结果
			QueryWrapper<TInspectionRecord> queryWrapperInspectionRecord = new QueryWrapper<>();
			queryWrapperInspectionRecord.lambda().and(i -> i.in(TInspectionRecord::getPersonId, finalPersonIds));
			queryWrapperInspectionRecord.lambda().and(i -> i.eq(TInspectionRecord::getDelFlag, 0));
			List<TInspectionRecord> inspectionRecord = tInspectionRecordService.list(queryWrapperInspectionRecord);
			for(TInspectionRecord tInspectionRecord : inspectionRecord){
				if(tInspectionRecord!=null && tInspectionRecord.getInspectionAutograph()!=null){
					//字节转字符串
					byte[] blob = (byte[]) tInspectionRecord.getInspectionAutograph();
					if(blob!=null){
						String avatarNow = new String(blob);
						if(avatarNow.indexOf("/dcm") > -1){
							tInspectionRecord.setInspectionAutograph(avatarNow);
						}
					}
				}
			}


			//查询复检项目
			QueryWrapper<TReviewProject> queryWrapperReviewRecord = new QueryWrapper<>();
			queryWrapperReviewRecord.lambda().and(i -> i.in(TReviewProject::getPersonId, Arrays.asList(ids)));
			queryWrapperReviewRecord.lambda().and(i -> i.eq(TReviewProject::getDelFlag, 0));
//			queryWrapperReviewRecord.lambda().and(i -> i.eq(TReviewProject::getIsPass, 1));
			List<TReviewProject> reviewProjectsList =tReviewProjectService.list(queryWrapperReviewRecord);
			/*QueryWrapper<TDepartResult> queryWrapperReviewRecord = new QueryWrapper<>();
			queryWrapperReviewRecord.lambda().and(i -> i.in(TDepartResult::getPersonId, Arrays.asList(ids)));
			queryWrapperReviewRecord.lambda().and(i -> i.eq(TDepartResult::getDelFlag, 0));
			queryWrapperReviewRecord.lambda().and(i -> i.like(TDepartResult::getGroupItemName, "(复)"));
			queryWrapperReviewRecord.orderByAsc("check_date");
			List<TDepartResult> reviewProjectsList = tDepartResultService.list(queryWrapperReviewRecord);*/

			//获取第一次体检人员的id
			QueryWrapper<TReviewPerson> tReviewPersonQueryWrapper = new QueryWrapper<>();
			tReviewPersonQueryWrapper.eq("del_flag",0);
			tReviewPersonQueryWrapper.in("id",ids);
			tReviewPersonQueryWrapper.select("first_person_id");
			tReviewPersonQueryWrapper.groupBy("first_person_id");
			List<TReviewPerson> tReviewPersonList = itReviewPersonService.list(tReviewPersonQueryWrapper);
			List<String> stringList = tReviewPersonList.stream().map(TReviewPerson::getFirstPersonId).collect(Collectors.toList());
			//问诊查询
			QueryWrapper<TInterrogation> tInterrogationQueryWrapper = new QueryWrapper<>();
			tInterrogationQueryWrapper.eq("del_flag",0);
//			tInterrogationQueryWrapper.in("person_id",ids);
			tInterrogationQueryWrapper.in("person_id",stringList);
			tInterrogationQueryWrapper.orderByDesc("create_time");
			List<TInterrogation> tInterrogationList = interrogationService.list(tInterrogationQueryWrapper);

			//危害因素结论
			List<TdTjBadrsns> tdTjBadrsns = tdTjBadrsnsService.selectListByIds(finalPersonIds);

			for (Map<String, Object> mapPerson:mapPersons) {
				if(mapPerson!=null  && mapPerson.size()>0 && mapPerson.containsKey("id") ){

					if(mapPerson.get("avatar")!=null){
						//字节转字符串
						byte[] blob = (byte[]) mapPerson.get("avatar");
						if(blob!=null){
							String avatarNow = new String(blob);
							if(avatarNow.indexOf("/dcm") > -1){
								mapPerson.put("avatar",avatarNow);
							}
						}
					}
					Map<String, Object> result = new HashMap<>();
					//人员id
//					String personId = mapPersons.get(0).get("id").toString();
					String personId = mapPerson.get("id").toString();
					String firstPersonId = mapPerson.get("first_person_id").toString();
					//获取人员问诊信息
					TInterrogation tInterrogation = tInterrogationList.stream().filter(ii -> firstPersonId.contains(ii.getPersonId())).findFirst().orElse(null);
					if(tInterrogation!=null){
						mapPerson.put("work_year",tInterrogation.getWorkYear());
						mapPerson.put("work_month",tInterrogation.getWorkMonth());
						mapPerson.put("exposure_work_year",tInterrogation.getExposureWorkYear());
						mapPerson.put("exposure_work_month",tInterrogation.getExposureWorkMonth());
						mapPerson.put("exposure_start_date",tInterrogation.getExposureStartDate());
						mapPerson.put("nation",tInterrogation.getNation());
						mapPerson.put("check_num",tInterrogation.getCheckNum());
						mapPerson.put("disease_name",tInterrogation.getDiseaseName());
						mapPerson.put("is_cured",tInterrogation.getIsCured());
						mapPerson.put("menarche",tInterrogation.getMenarche());
						mapPerson.put("period",tInterrogation.getPeriod());
						mapPerson.put("cycle",tInterrogation.getCycle());
						mapPerson.put("last_menstruation",tInterrogation.getLastMenstruation());
						mapPerson.put("existing_children",tInterrogation.getExistingChildren());
						mapPerson.put("abortion",tInterrogation.getAbortion());
						mapPerson.put("premature",tInterrogation.getPremature());
						mapPerson.put("death",tInterrogation.getDeath());
						mapPerson.put("abnormal_fetus",tInterrogation.getAbnormalFetus());
						mapPerson.put("smoke_state",tInterrogation.getSmokeState());
						mapPerson.put("package_every_day",tInterrogation.getPackageEveryDay());
						mapPerson.put("smoke_year",tInterrogation.getSmokeYear());
						mapPerson.put("drink_state",tInterrogation.getDrinkState());
						mapPerson.put("ml_every_day",tInterrogation.getMlEveryDay());
						mapPerson.put("drink_year",tInterrogation.getDrinkYear());
						mapPerson.put("other_info",tInterrogation.getOtherInfo());
						mapPerson.put("symptom",tInterrogation.getSymptom());
						mapPerson.put("education",tInterrogation.getEducation());
						mapPerson.put("family_address",tInterrogation.getFamilyAddress());
						mapPerson.put("menstrual_history",tInterrogation.getMenstrualHistory());
						mapPerson.put("menstrual_info",tInterrogation.getMenstrualInfo());
						mapPerson.put("allergies",tInterrogation.getAllergies());
						mapPerson.put("allergies_info",tInterrogation.getAllergiesInfo());
						mapPerson.put("birthplace_code",tInterrogation.getBirthplaceCode());
						mapPerson.put("birthplace_name",tInterrogation.getBirthplaceName());
						mapPerson.put("family_history",tInterrogation.getFamilyHistory());
						mapPerson.put("past_medical_history_other_info",tInterrogation.getPastMedicalHistoryOtherInfo());
						mapPerson.put("wz_check_doctor",tInterrogation.getWzCheckDoctor());
						mapPerson.put("wz_check_time",tInterrogation.getWzCheckTime());
						mapPerson.put("wz_check_autograph",tInterrogation.getWzCheckAutograph());

						mapPerson.put("marriage_date",tInterrogation.getMarriageDate());//婚姻史-结婚日期
						mapPerson.put("spouse_radiation_situation",tInterrogation.getSpouseRadiationSituation());//婚姻史-配偶接触放射线情况
						mapPerson.put("spouse_health_situation",tInterrogation.getSpouseHealthSituation());//婚姻史-配偶接触放射线情况
						mapPerson.put("pregnancy_count",tInterrogation.getPregnancyCount());//孕次
						mapPerson.put("live_birth",tInterrogation.getLiveBirth());//活产
						mapPerson.put("abortion_small",tInterrogation.getAbortionSmall());//自然流产
						mapPerson.put("multiparous",tInterrogation.getMultiparous());//多胎
						mapPerson.put("ectopic_pregnancy",tInterrogation.getEctopicPregnancy());//异位妊娠
						mapPerson.put("boys",tInterrogation.getBoys());//现有男孩
						mapPerson.put("boys_birth",tInterrogation.getBoysBirth());//现有男孩-出生日期
						mapPerson.put("girls",tInterrogation.getGirls());//现有女孩
						mapPerson.put("girls_birth",tInterrogation.getGirlsBirth());//现有女孩-出生日期
						mapPerson.put("infertility_reason",tInterrogation.getInfertilityReason());//不孕不育原因
						mapPerson.put("childrens_health",tInterrogation.getChildrensHealth());//子女健康情况

						mapPerson.put("quit_somking",tInterrogation.getQuitSomking());//戒烟年数
						mapPerson.put("job",tInterrogation.getJob());//职务/职称
						mapPerson.put("zip_code",tInterrogation.getZipCode());//邮政编码
					}
					if(mapPerson.get("wz_check_autograph")!=null){
						//字节转字符串
						byte[] blob = (byte[]) mapPerson.get("wz_check_autograph");
						if(blob!=null){
							String avatarNow = new String(blob);
							if(avatarNow.indexOf("/dcm") > -1){
								mapPerson.put("wz_check_autograph",avatarNow);
							}
						}
					}

					//订单Id
					String groupId = mapPersons.get(0).get("group_id").toString();
					result.put("orderGroup", orderGroup.stream().filter(ii -> groupId.contains(ii.getId())).findFirst().orElse(null));
					if(socketConfig.getInitialMerger()){
						result.put("departResults", departResults.stream().filter(ii -> personId.contains(ii.getPersonId())||firstPersonId.contains(ii.getPersonId())).collect(Collectors.toList()));
						result.put("departItemResults", departItemResults.stream().filter(ii -> personId.contains(ii.getPersonId())||firstPersonId.contains(ii.getPersonId())).collect(Collectors.toList()));
						mapPerson.put("inspectionRecordFirst",inspectionRecord.stream().filter(ii -> firstPersonId.contains(ii.getPersonId())).findFirst().orElse(null));
					}
					else{
						result.put("departResults", departResults.stream().filter(ii -> personId.contains(ii.getPersonId())).collect(Collectors.toList()));
						result.put("departItemResults", departItemResults.stream().filter(ii -> personId.contains(ii.getPersonId())).collect(Collectors.toList()));
					}

					List<TReviewProject> reviewProjects = reviewProjectsList.stream().filter(ii -> personId.contains(ii.getPersonId())).collect(Collectors.toList());
					mapPerson.put("reviewProjectsList",reviewProjects);
					mapPerson.put("personProjectCheck",personProjectCheck.stream().filter(ii -> personId.contains(ii.getPersonId())).collect(Collectors.toList()));
					mapPerson.put("inspectionRecord",inspectionRecord.stream().filter(ii -> personId.contains(ii.getPersonId())).findFirst().orElse(null));
					if(mapPerson.get("hazard_factors_text")!=null && mapPerson.get("hazard_factors")!=null && mapPerson.get("physical_type")!=null && (mapPerson.get("physical_type").toString().equals("职业体检") || mapPerson.get("physical_type").toString().equals("放射体检"))){

						String hazardFactorsNow = mapPerson.get("hazard_factors").toString();
						String hazardFactorsTextNow = mapPerson.get("hazard_factors_text").toString();
						String[] hazardFactorsNowArr = new String[0];
						String[] hazardFactorsTextNowArr = new String[0];
						if(StringUtils.isNotBlank(hazardFactorsNow)){
							hazardFactorsNowArr = hazardFactorsNow.split("\\|");
						}
						if(StringUtils.isNotBlank(hazardFactorsTextNow)){
							if(hazardFactorsTextNow.indexOf("\\|") > -1){
								hazardFactorsTextNowArr = hazardFactorsTextNow.split("\\|");
							}else if(hazardFactorsNowArr!=null && hazardFactorsNowArr.length>0){
								hazardFactorsTextNowArr = (hazardFactorsTextNow.replaceAll("\\|","、")).split("、");
							}else{
								hazardFactorsTextNowArr =hazardFactorsTextNow.split("\\|");
							}
						}
						if(tdTjBadrsns!=null && tdTjBadrsns.size()>0){
							List<TdTjBadrsns> tdTjBadrsnsList = tdTjBadrsns.stream().filter(ii -> personId.contains(ii.getFkBhkId())).collect(Collectors.toList());
							List<TdTjBadrsns> tdTjBadrsnsListNow = new ArrayList<>();
							for(TdTjBadrsns tdTjBadrsnsOne : tdTjBadrsnsList){
								if(tdTjBadrsnsOne!=null && StringUtils.isNotBlank(tdTjBadrsnsOne.getBadrsnCode()) && hazardFactorsNowArr!=null && hazardFactorsNowArr.length>0){
									int num = 0;
									for(String s : hazardFactorsNowArr){
										if(StringUtils.isNotBlank(s) && tdTjBadrsnsOne.getBadrsnCode().equals(s) && hazardFactorsTextNowArr!=null && hazardFactorsTextNowArr.length>0 && hazardFactorsTextNowArr[num]!=null && StringUtils.isNotBlank(hazardFactorsTextNowArr[num])){
											tdTjBadrsnsOne.setTypeName(hazardFactorsTextNowArr[num]);
											tdTjBadrsnsListNow.add(tdTjBadrsnsOne);
										}
										num ++;
									}
								}
							}
							mapPerson.put("tdTjBadrsns", tdTjBadrsnsListNow);
							if(socketConfig.getInitialMerger()){
								List<TdTjBadrsns> tdTjBadrsnsListFirst = tdTjBadrsns.stream().filter(ii -> firstPersonId.contains(ii.getFkBhkId())).collect(Collectors.toList());
								List<TdTjBadrsns> tdTjBadrsnsListNowFirst = new ArrayList<>();
								for(TdTjBadrsns tdTjBadrsnsOne : tdTjBadrsnsListFirst){
									if(tdTjBadrsnsOne!=null && StringUtils.isNotBlank(tdTjBadrsnsOne.getBadrsnCode()) && hazardFactorsNowArr!=null && hazardFactorsNowArr.length>0){
										int num = 0;
										for(String s : hazardFactorsNowArr){
											if(StringUtils.isNotBlank(s) && tdTjBadrsnsOne.getBadrsnCode().equals(s) && hazardFactorsTextNowArr!=null && hazardFactorsTextNowArr.length>0 && hazardFactorsTextNowArr[num]!=null && StringUtils.isNotBlank(hazardFactorsTextNowArr[num])){
												tdTjBadrsnsOne.setTypeName(hazardFactorsTextNowArr[num]);
												tdTjBadrsnsListNowFirst.add(tdTjBadrsnsOne);
											}
											num ++;
										}
									}
								}
								mapPerson.put("tdTjBadrsnsFirst", tdTjBadrsnsListFirst);
							}
						}


					}
					String testNum = mapPerson.get("test_num").toString();
					String testNumCode = (GoogleTestNumBarCodeUtils.generatorBase64Barcode(testNum, testNum));
					mapPerson.put("testNumCode",testNumCode.split(",")[1]);
					mapPerson.put("nowRegistDate",reviewProjects.get(0).getRegistDate());//当前最新一次体检日期
					result.put("mapPerson",mapPerson);
					results.add(result);
				}
			}

			return ResultUtil.data(results);

		} catch (Exception e) {
			e.printStackTrace();
			return ResultUtil.error("查询体检报告结果数据异常:" + e.getMessage());
		}
	}

	@ApiOperation("根据主键来获取模板信息数据")
	@PostMapping("generateReportByPersonIdsFC")
	public Result<Object> generateReportByPersonIdsFC(@RequestBody String[] ids) {
		try {
			List<Map<String, Object>>results  = new ArrayList<>();
			if(ids == null || ids.length == 0){
				return ResultUtil.error("查询体检报告结果数据异常:人员Id为空" );
			}
			//第一步查询人员信息
			List <Map<String, Object>> mapPersons = tGroupPersonService.getGroupPersonInfoByIds(Arrays.asList(ids));
			if(mapPersons==null ||mapPersons.size()==0){
				return ResultUtil.error("查询体检报告结果数据异常:查询人员信息失败" );
			}
			List<String> groupIds = new ArrayList<>();
			mapPersons.stream().forEach(m -> {
				if (m.containsKey("group_id")) {
					//没得才添加
					if(!groupIds.contains(m.get("group_id").toString())){
						groupIds.add(m.get("group_id").toString());
					}
				}
			});
			//查询组合项检查结果
			List<TDepartResult> departResults = tTemplateService.getDepartResultListByPersonIds( Arrays.asList(ids),groupIds);
			for(TDepartResult tDepartResult : departResults){
				if(tDepartResult!=null && tDepartResult.getCheckSign()!=null){
					//字节转字符串
					byte[] blob = (byte[]) tDepartResult.getCheckSign();
					if(blob!=null){
						String avatarNow = new String(blob);
						if(avatarNow.indexOf("/dcm") > -1){
							tDepartResult.setCheckSignPath(avatarNow);
						}
					}
				}
			}
			//查询基础项目检查结果
			List<TDepartItemResult> departItemResults = tTemplateService.getDepartItemResultListByPersonIds( Arrays.asList(ids),groupIds);
			//查询总检结果
			QueryWrapper<TInspectionRecord> queryWrapperInspectionRecord = new QueryWrapper<>();
			queryWrapperInspectionRecord.lambda().and(i -> i.in(TInspectionRecord::getPersonId, Arrays.asList(ids)));
			queryWrapperInspectionRecord.lambda().and(i -> i.eq(TInspectionRecord::getDelFlag, 0));
			List<TInspectionRecord> inspectionRecord = tInspectionRecordService.list(queryWrapperInspectionRecord);
			//查询复检项目
			QueryWrapper<TReviewProject> queryWrapperReviewRecord = new QueryWrapper<>();
			queryWrapperReviewRecord.orderByDesc("regist_date");//降序排序
			queryWrapperReviewRecord.lambda().and(i -> i.in(TReviewProject::getPersonId, Arrays.asList(ids)));
			queryWrapperReviewRecord.lambda().and(i -> i.eq(TReviewProject::getDelFlag, 0));
			List<TReviewProject> reviewProjectsList =tReviewProjectService.list(queryWrapperReviewRecord);

			//问诊查询
			QueryWrapper<TInterrogation> tInterrogationQueryWrapper = new QueryWrapper<>();
			tInterrogationQueryWrapper.eq("del_flag",0);
			tInterrogationQueryWrapper.in("person_id",ids);
			tInterrogationQueryWrapper.orderByDesc("create_time");
			List<TInterrogation> tInterrogationList = interrogationService.list(tInterrogationQueryWrapper);

			for (Map<String, Object> mapPerson:mapPersons) {
				if(mapPerson!=null  && mapPerson.size()>0 && mapPerson.containsKey("id") ){
					if(mapPerson.get("avatar")!=null){
						//字节转字符串
						byte[] blob = (byte[]) mapPerson.get("avatar");
						if(blob!=null){
							String avatarNow = new String(blob);
							if(avatarNow.indexOf("/dcm") > -1){
								mapPerson.put("avatar",avatarNow);
							}
						}
					}

					Map<String, Object> result = new HashMap<>();
					//人员id
//					String personId = mapPersons.get(0).get("id").toString();
					String personId = mapPerson.get("id").toString();
					//获取人员问诊信息
					TInterrogation tInterrogation = tInterrogationList.stream().filter(ii -> personId.contains(ii.getPersonId())).findFirst().orElse(null);
					if(tInterrogation!=null){
						mapPerson.put("work_year",tInterrogation.getWorkYear());
						mapPerson.put("work_month",tInterrogation.getWorkMonth());
						mapPerson.put("exposure_work_year",tInterrogation.getExposureWorkYear());
						mapPerson.put("exposure_work_month",tInterrogation.getExposureWorkMonth());
						mapPerson.put("exposure_start_date",tInterrogation.getExposureStartDate());
						mapPerson.put("nation",tInterrogation.getNation());
						mapPerson.put("check_num",tInterrogation.getCheckNum());
						mapPerson.put("disease_name",tInterrogation.getDiseaseName());
						mapPerson.put("is_cured",tInterrogation.getIsCured());
						mapPerson.put("menarche",tInterrogation.getMenarche());
						mapPerson.put("period",tInterrogation.getPeriod());
						mapPerson.put("cycle",tInterrogation.getCycle());
						mapPerson.put("last_menstruation",tInterrogation.getLastMenstruation());
						mapPerson.put("existing_children",tInterrogation.getExistingChildren());
						mapPerson.put("abortion",tInterrogation.getAbortion());
						mapPerson.put("premature",tInterrogation.getPremature());
						mapPerson.put("death",tInterrogation.getDeath());
						mapPerson.put("abnormal_fetus",tInterrogation.getAbnormalFetus());
						mapPerson.put("smoke_state",tInterrogation.getSmokeState());
						mapPerson.put("package_every_day",tInterrogation.getPackageEveryDay());
						mapPerson.put("smoke_year",tInterrogation.getSmokeYear());
						mapPerson.put("drink_state",tInterrogation.getDrinkState());
						mapPerson.put("ml_every_day",tInterrogation.getMlEveryDay());
						mapPerson.put("drink_year",tInterrogation.getDrinkYear());
						mapPerson.put("other_info",tInterrogation.getOtherInfo());
						mapPerson.put("symptom",tInterrogation.getSymptom());
						mapPerson.put("education",tInterrogation.getEducation());
						mapPerson.put("family_address",tInterrogation.getFamilyAddress());
						mapPerson.put("menstrual_history",tInterrogation.getMenstrualHistory());
						mapPerson.put("menstrual_info",tInterrogation.getMenstrualInfo());
						mapPerson.put("allergies",tInterrogation.getAllergies());
						mapPerson.put("allergies_info",tInterrogation.getAllergiesInfo());
						mapPerson.put("birthplace_code",tInterrogation.getBirthplaceCode());
						mapPerson.put("birthplace_name",tInterrogation.getBirthplaceName());
						mapPerson.put("family_history",tInterrogation.getFamilyHistory());
						mapPerson.put("past_medical_history_other_info",tInterrogation.getPastMedicalHistoryOtherInfo());
						mapPerson.put("wz_check_doctor",tInterrogation.getWzCheckDoctor());
						mapPerson.put("wz_check_time",tInterrogation.getWzCheckTime());
						mapPerson.put("wz_check_autograph",tInterrogation.getWzCheckAutograph());

						mapPerson.put("marriage_date",tInterrogation.getMarriageDate());//婚姻史-结婚日期
						mapPerson.put("spouse_radiation_situation",tInterrogation.getSpouseRadiationSituation());//婚姻史-配偶接触放射线情况
						mapPerson.put("spouse_health_situation",tInterrogation.getSpouseHealthSituation());//婚姻史-配偶接触放射线情况
						mapPerson.put("pregnancy_count",tInterrogation.getPregnancyCount());//孕次
						mapPerson.put("live_birth",tInterrogation.getLiveBirth());//活产
						mapPerson.put("abortion_small",tInterrogation.getAbortionSmall());//自然流产
						mapPerson.put("multiparous",tInterrogation.getMultiparous());//多胎
						mapPerson.put("ectopic_pregnancy",tInterrogation.getEctopicPregnancy());//异位妊娠
						mapPerson.put("boys",tInterrogation.getBoys());//现有男孩
						mapPerson.put("boys_birth",tInterrogation.getBoysBirth());//现有男孩-出生日期
						mapPerson.put("girls",tInterrogation.getGirls());//现有女孩
						mapPerson.put("girls_birth",tInterrogation.getGirlsBirth());//现有女孩-出生日期
						mapPerson.put("infertility_reason",tInterrogation.getInfertilityReason());//不孕不育原因
						mapPerson.put("childrens_health",tInterrogation.getChildrensHealth());//子女健康情况

						mapPerson.put("quit_somking",tInterrogation.getQuitSomking());//戒烟年数
						mapPerson.put("job",tInterrogation.getJob());//职务/职称
						mapPerson.put("zip_code",tInterrogation.getZipCode());//邮政编码
					}
					if(mapPerson.get("wz_check_autograph")!=null){
						//字节转字符串
						byte[] blob = (byte[]) mapPerson.get("wz_check_autograph");
						if(blob!=null){
							String avatarNow = new String(blob);
							if(avatarNow.indexOf("/dcm") > -1){
								mapPerson.put("wz_check_autograph",avatarNow);
							}
						}
					}

					//订单Id
					String groupId = mapPersons.get(0).get("group_id").toString();
					result.put("departResults", departResults.stream().filter(ii -> personId.contains(ii.getPersonId())).collect(Collectors.toList()));
					result.put("departItemResults", departItemResults.stream().filter(ii -> personId.contains(ii.getPersonId())).collect(Collectors.toList()));
					List<TReviewProject> reviewProjects = reviewProjectsList.stream().filter(ii -> personId.contains(ii.getPersonId())).collect(Collectors.toList());
					mapPerson.put("reviewProjectsList",reviewProjects);
					mapPerson.put("inspectionRecord",inspectionRecord.stream().filter(ii -> personId.contains(ii.getPersonId())).findFirst().orElse(null));
					result.put("mapPerson",mapPerson);
					String testNum = mapPerson.get("test_num").toString();
					String testNumCode = (GoogleTestNumBarCodeUtils.generatorBase64Barcode(testNum, testNum));
					mapPerson.put("testNumCode",testNumCode.split(",")[1]);
					mapPerson.put("nowRegistDate",reviewProjects.get(0).getRegistDate());//当前最新一次体检日期
					results.add(result);
				}
			}

			return ResultUtil.data(results);

		} catch (Exception e) {
			e.printStackTrace();
			return ResultUtil.error("查询体检报告结果数据异常:" + e.getMessage());
		}
	}

	@ApiOperation("根据主键来获取模板信息数据")
	@PostMapping("generateReportByPersonIdsNotice")
	public Result<Object> generateReportByPersonIdsNotice(@RequestBody String[] ids) {
		try {
			List<Map<String, Object>>results  = new ArrayList<>();
			if(ids == null || ids.length == 0){
				return ResultUtil.error("查询体检报告结果数据异常:人员Id为空" );
			}
			//第一步查询人员信息
			List <Map<String, Object>> mapPersons = tGroupPersonService.getGroupPersonInfoByIds(Arrays.asList(ids));
			List<TInspectionRecord> groupPersonOrderNumByIds = tGroupPersonService.getGroupPersonOrderNumByIds();
			List<TInspectionRecord> collect = new ArrayList<>();
			for (int i = 0; i < Arrays.asList(ids).size(); i++) {
				 collect = groupPersonOrderNumByIds.stream().filter(aa -> aa.getPersonId().equals(Arrays.asList(ids).get(0))).collect(Collectors.toList());
			}
			if(mapPersons==null ||mapPersons.size()==0){
				return ResultUtil.error("查询体检报告结果数据异常:查询人员信息失败" );
			}
			List<String> groupIds = new ArrayList<>();
			mapPersons.stream().forEach(m -> {
				if (m.containsKey("group_id")) {
					//没得才添加
					if(!groupIds.contains(m.get("group_id").toString())){
						groupIds.add(m.get("group_id").toString());
					}
				}
			});
			List<TOrderGroup> orderGroup = orderGroupService.listByIds (groupIds);
			//查询总检结果
			QueryWrapper<TInspectionRecord> queryWrapperInspectionRecord = new QueryWrapper<>();
			queryWrapperInspectionRecord.lambda().and(i -> i.in(TInspectionRecord::getPersonId, Arrays.asList(ids)));
			queryWrapperInspectionRecord.lambda().and(i -> i.eq(TInspectionRecord::getDelFlag, 0));
			List<TInspectionRecord> inspectionRecord = tInspectionRecordService.list(queryWrapperInspectionRecord);

			for (Map<String, Object> mapPerson:mapPersons) {
				if(mapPerson!=null  && mapPerson.size()>0 && mapPerson.containsKey("id") ){
					Map<String, Object> result = new HashMap<>();
					//人员id
//					String personId = mapPersons.get(0).get("id").toString();
					String personId = mapPerson.get("id").toString();
					if (collect!=null && collect.size()>0){
					   mapPerson.put("number",collect.get(0).getRowno());
					}
					//订单Id
					String groupId = mapPersons.get(0).get("group_id").toString();
					result.put("orderGroup", orderGroup.stream().filter(ii -> groupId.contains(ii.getId())).findFirst().orElse(null));
					mapPerson.put("inspectionRecord",inspectionRecord.stream().filter(ii -> personId.contains(ii.getPersonId())).findFirst().orElse(null));
					result.put("mapPerson",mapPerson);
					results.add(result);
				}
			}

			return ResultUtil.data(results);

		} catch (Exception e) {
			e.printStackTrace();
			return ResultUtil.error("查询体检报告结果数据异常:" + e.getMessage());
		}
	}

	@ApiOperation("根据主键来获取模板信息数据")
	@PostMapping("generateReportByPersonIdsTJJL")
	public Result<Object> generateReportByPersonIdsTJJL(@RequestBody String[] ids) {
		try {
			List<Map<String, Object>>results  = new ArrayList<>();
			if(ids == null || ids.length == 0){
				return ResultUtil.error("查询体检报告结果数据异常:人员Id为空" );
			}
			//第一步查询人员信息
			List <Map<String, Object>> mapPersons = tGroupPersonService.getGroupPersonInfoByIds(Arrays.asList(ids));
			if(mapPersons==null ||mapPersons.size()==0){
				return ResultUtil.error("查询体检报告结果数据异常:查询人员信息失败" );
			}
			List<String> groupIds = new ArrayList<>();
			mapPersons.stream().forEach(m -> {
				if (m.containsKey("group_id")) {
					//没得才添加
					if(!groupIds.contains(m.get("group_id").toString())){
						groupIds.add(m.get("group_id").toString());
					}
				}
			});
			List<TOrderGroup> orderGroup = orderGroupService.listByIds (groupIds);
			//查询总检结果
			QueryWrapper<TInspectionRecord> queryWrapperInspectionRecord = new QueryWrapper<>();
			queryWrapperInspectionRecord.lambda().and(i -> i.in(TInspectionRecord::getPersonId, Arrays.asList(ids)));
			queryWrapperInspectionRecord.lambda().and(i -> i.eq(TInspectionRecord::getDelFlag, 0));
			List<TInspectionRecord> inspectionRecord = tInspectionRecordService.list(queryWrapperInspectionRecord);
			for(TInspectionRecord tInspectionRecord : inspectionRecord){
				if(tInspectionRecord!=null && tInspectionRecord.getInspectionAutograph()!=null){
					//字节转字符串
					byte[] blob = (byte[]) tInspectionRecord.getInspectionAutograph();
					if(blob!=null){
						String avatarNow = new String(blob);
						if(avatarNow.indexOf("/dcm") > -1){
							tInspectionRecord.setInspectionAutograph(avatarNow);
						}
					}
				}
			}

			//问诊查询
			QueryWrapper<TInterrogation> tInterrogationQueryWrapper = new QueryWrapper<>();
			tInterrogationQueryWrapper.eq("del_flag",0);
			tInterrogationQueryWrapper.in("person_id",ids);
			tInterrogationQueryWrapper.orderByDesc("create_time");
			List<TInterrogation> tInterrogationList = interrogationService.list(tInterrogationQueryWrapper);

			for (Map<String, Object> mapPerson:mapPersons) {
				if(mapPerson!=null  && mapPerson.size()>0 && mapPerson.containsKey("id") ){
					if(mapPerson.get("avatar")!=null){
						//字节转字符串
						byte[] blob = (byte[]) mapPerson.get("avatar");
						if(blob!=null){
							String avatarNow = new String(blob);
							if(avatarNow.indexOf("/dcm") > -1){
								mapPerson.put("avatar",avatarNow);
							}
						}
					}

					Map<String, Object> result = new HashMap<>();
					//人员id
//					String personId = mapPersons.get(0).get("id").toString();
					String personId = mapPerson.get("id").toString();
					//获取人员问诊信息
					TInterrogation tInterrogation = tInterrogationList.stream().filter(ii -> personId.contains(ii.getPersonId())).findFirst().orElse(null);
					if(tInterrogation!=null){
						mapPerson.put("work_year",tInterrogation.getWorkYear());
						mapPerson.put("work_month",tInterrogation.getWorkMonth());
						mapPerson.put("exposure_work_year",tInterrogation.getExposureWorkYear());
						mapPerson.put("exposure_work_month",tInterrogation.getExposureWorkMonth());
						mapPerson.put("exposure_start_date",tInterrogation.getExposureStartDate());
						mapPerson.put("nation",tInterrogation.getNation());
						mapPerson.put("check_num",tInterrogation.getCheckNum());
						mapPerson.put("disease_name",tInterrogation.getDiseaseName());
						mapPerson.put("is_cured",tInterrogation.getIsCured());
						mapPerson.put("menarche",tInterrogation.getMenarche());
						mapPerson.put("period",tInterrogation.getPeriod());
						mapPerson.put("cycle",tInterrogation.getCycle());
						mapPerson.put("last_menstruation",tInterrogation.getLastMenstruation());
						mapPerson.put("existing_children",tInterrogation.getExistingChildren());
						mapPerson.put("abortion",tInterrogation.getAbortion());
						mapPerson.put("premature",tInterrogation.getPremature());
						mapPerson.put("death",tInterrogation.getDeath());
						mapPerson.put("abnormal_fetus",tInterrogation.getAbnormalFetus());
						mapPerson.put("smoke_state",tInterrogation.getSmokeState());
						mapPerson.put("package_every_day",tInterrogation.getPackageEveryDay());
						mapPerson.put("smoke_year",tInterrogation.getSmokeYear());
						mapPerson.put("drink_state",tInterrogation.getDrinkState());
						mapPerson.put("ml_every_day",tInterrogation.getMlEveryDay());
						mapPerson.put("drink_year",tInterrogation.getDrinkYear());
						mapPerson.put("other_info",tInterrogation.getOtherInfo());
						mapPerson.put("symptom",tInterrogation.getSymptom());
						mapPerson.put("education",tInterrogation.getEducation());
						mapPerson.put("family_address",tInterrogation.getFamilyAddress());
						mapPerson.put("menstrual_history",tInterrogation.getMenstrualHistory());
						mapPerson.put("menstrual_info",tInterrogation.getMenstrualInfo());
						mapPerson.put("allergies",tInterrogation.getAllergies());
						mapPerson.put("allergies_info",tInterrogation.getAllergiesInfo());
						mapPerson.put("birthplace_code",tInterrogation.getBirthplaceCode());
						mapPerson.put("birthplace_name",tInterrogation.getBirthplaceName());
						mapPerson.put("family_history",tInterrogation.getFamilyHistory());
						mapPerson.put("past_medical_history_other_info",tInterrogation.getPastMedicalHistoryOtherInfo());
						mapPerson.put("wz_check_doctor",tInterrogation.getWzCheckDoctor());
						mapPerson.put("wz_check_time",tInterrogation.getWzCheckTime());
						mapPerson.put("wz_check_autograph",tInterrogation.getWzCheckAutograph());

						mapPerson.put("marriage_date",tInterrogation.getMarriageDate());//婚姻史-结婚日期
						mapPerson.put("spouse_radiation_situation",tInterrogation.getSpouseRadiationSituation());//婚姻史-配偶接触放射线情况
						mapPerson.put("spouse_health_situation",tInterrogation.getSpouseHealthSituation());//婚姻史-配偶接触放射线情况
						mapPerson.put("pregnancy_count",tInterrogation.getPregnancyCount());//孕次
						mapPerson.put("live_birth",tInterrogation.getLiveBirth());//活产
						mapPerson.put("abortion_small",tInterrogation.getAbortionSmall());//自然流产
						mapPerson.put("multiparous",tInterrogation.getMultiparous());//多胎
						mapPerson.put("ectopic_pregnancy",tInterrogation.getEctopicPregnancy());//异位妊娠
						mapPerson.put("boys",tInterrogation.getBoys());//现有男孩
						mapPerson.put("boys_birth",tInterrogation.getBoysBirth());//现有男孩-出生日期
						mapPerson.put("girls",tInterrogation.getGirls());//现有女孩
						mapPerson.put("girls_birth",tInterrogation.getGirlsBirth());//现有女孩-出生日期
						mapPerson.put("infertility_reason",tInterrogation.getInfertilityReason());//不孕不育原因
						mapPerson.put("childrens_health",tInterrogation.getChildrensHealth());//子女健康情况

						mapPerson.put("quit_somking",tInterrogation.getQuitSomking());//戒烟年数
						mapPerson.put("job",tInterrogation.getJob());//职务/职称
						mapPerson.put("zip_code",tInterrogation.getZipCode());//邮政编码
					}
					if(mapPerson.get("wz_check_autograph")!=null){
						//字节转字符串
						byte[] blob = (byte[]) mapPerson.get("wz_check_autograph");
						if(blob!=null){
							String avatarNow = new String(blob);
							if(avatarNow.indexOf("/dcm") > -1){
								mapPerson.put("wz_check_autograph",avatarNow);
							}
						}
					}

					//订单Id
					String groupId = mapPersons.get(0).get("group_id").toString();
					result.put("orderGroup", orderGroup.stream().filter(ii -> groupId.contains(ii.getId())).findFirst().orElse(null));
					mapPerson.put("inspectionRecord",inspectionRecord.stream().filter(ii -> personId.contains(ii.getPersonId())).findFirst().orElse(null));
					result.put("mapPerson",mapPerson);
					results.add(result);
				}
			}

			return ResultUtil.data(results);

		} catch (Exception e) {
			e.printStackTrace();
			return ResultUtil.error("查询体检报告结果数据异常:" + e.getMessage());
		}
	}

	@ApiOperation("根据主键来获取模板信息数据")
	@PostMapping("generateReportByPersonIdsfirstPage")
	public Result<Object> generateReportByPersonIdsfirstPage(@RequestBody String[] ids) {
		try {
			List<Map<String, Object>>results  = new ArrayList<>();
			if(ids == null || ids.length == 0){
				return ResultUtil.error("查询体检报告结果数据异常:人员Id为空" );
			}
			//第一步查询人员信息
			List <Map<String, Object>> mapPersons = tGroupPersonService.getGroupPersonInfoByIds(Arrays.asList(ids));
			if(mapPersons==null ||mapPersons.size()==0){
				return ResultUtil.error("查询体检报告结果数据异常:查询人员信息失败" );
			}
			List<String> groupIds = new ArrayList<>();
			mapPersons.stream().forEach(m -> {
				if (m.containsKey("group_id")) {
					//没得才添加
					if(!groupIds.contains(m.get("group_id").toString())){
						groupIds.add(m.get("group_id").toString());
					}
				}
			});
			List<TOrderGroup> orderGroup = orderGroupService.listByIds (groupIds);

			for (Map<String, Object> mapPerson:mapPersons) {
				if(mapPerson!=null  && mapPerson.size()>0 && mapPerson.containsKey("id") ){
					Map<String, Object> result = new HashMap<>();
					//订单Id
					String groupId = mapPersons.get(0).get("group_id").toString();
					result.put("orderGroup", orderGroup.stream().filter(ii -> groupId.contains(ii.getId())).findFirst().orElse(null));
					result.put("mapPerson",mapPerson);
					results.add(result);
				}
			}

			return ResultUtil.data(results);

		} catch (Exception e) {
			e.printStackTrace();
			return ResultUtil.error("查询体检报告结果数据异常:" + e.getMessage());
		}
	}


	@ApiOperation("批量生成体检报告(先合并word 再 转pdf)")
	@PostMapping("/getReportWordBatch")
	public Result<Object> getReportWordBatch(@RequestBody Map map){
		List<String> res = new ArrayList<>();
		if(map ==null || map.size()==0 || !map.containsKey("data")){
			return ResultUtil.error("生成体检报告异常:参数为空" );
		}
		try {
			ArrayList batchData = (ArrayList)  map.get("data");
			//查询所有模板
			List<TTemplate> list = tTemplateService.list();
			String userId = securityUtil.getCurrUser().getId();
			//获取请求ip
			String ip = ipInfoUtil.getIpAddr(request);
			if(map.get("name") != null){
				ip = ip + map.get("name");
			}
			res =TemplateUtil.templateBatchHandData(batchData,ip,userId,list);
		}catch (Exception e){
			e.printStackTrace();
			return ResultUtil.error("生成体检报告异常:" + e.getMessage());
		}
		return  ResultUtil.data(res);
	}

}
