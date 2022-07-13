package com.scmt.healthy.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.scmt.healthy.entity.*;
import com.scmt.healthy.service.*;
import org.apache.ibatis.annotations.Param;
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
/**
 *@author
 **/
@RestController
@Api(tags =" 复查记录数据接口")
@RequestMapping("/scmt/tReviewRecord")
public class TReviewRecordController{
	@Autowired
	private ITReviewRecordService tReviewRecordService;
	@Autowired
	private SecurityUtil securityUtil;
	@Autowired
	private ITReviewProjectService tReviewProjectService;
	@Autowired
	private ITGroupPersonService tGroupPersonService;
	@Autowired
	private ITOrderGroupItemService tOrderGroupItemService;
	@Autowired
	private ITGroupOrderService groupOrderService;
	/**
	* 功能描述：新增复查记录数据
	* @param reviewList 实体
	* @return 返回新增结果
	*/
	@SystemLog(description = "新增复查记录数据", type = LogType.OPERATION)
	@ApiOperation("新增复查记录数据")
	@PostMapping("addTReviewRecord")
	@Transactional(rollbackOn = Exception.class)
	public Result<Object> addTReviewRecord(@RequestBody List<TReviewRecord> reviewList){
		try {
			ArrayList<TReviewRecord> reviewRecords = new ArrayList<>();
			String personId = "";
			for(TReviewRecord tReviewRecord:reviewList){
				personId = tReviewRecord.getPersonId();
				tReviewRecord.setDelFlag(0);
				tReviewRecord.setState(0);
				tReviewRecord.setCreateId(securityUtil.getCurrUser().getId());
				tReviewRecord.setCreateTime(new Date());
				reviewRecords.add(tReviewRecord);
				//复查项目
				TGroupPerson person = tGroupPersonService.getById(personId);
				//是否存有当前人的复检记录
				QueryWrapper<TReviewProject> reviewProjectQueryWrapper = new QueryWrapper<>();
				reviewProjectQueryWrapper.eq("group_id", person.getGroupId());
				reviewProjectQueryWrapper.eq("person_id", person.getId());
				reviewProjectQueryWrapper.eq("del_flag", 0);
				reviewProjectQueryWrapper.last("limit 1");
				TReviewProject one = tReviewProjectService.getOne(reviewProjectQueryWrapper);
				//编号
				String testNum = "";
				if (one != null) {
					testNum = one.getTestNum();
				} else {
					testNum = generatorNum();
				}
				TReviewProject tReviewProject;
				tReviewProject = tReviewRecord.getReviewProject();
				tReviewProject.setCreateId(securityUtil.getCurrUser().getId());
				tReviewProject.setPortfolioProjectName(tReviewRecord.getCheckProjectName() + "(复)");
				//取服务类型
				tReviewProject.setServiceType(tReviewRecord.getReviewProject().getServiceType());
				tReviewProject.setCreateTime(new Date());
				tReviewProject.setPersonName(person.getPersonName() + "(复)");
				tReviewProject.setGroupOrderId(tReviewRecord.getGroupOrderId());
				tReviewProject.setGroupId(person.getGroupId());
				tReviewProject.setProjectType(1);
				tReviewProject.setIsPass(1);
				tReviewProject.setTestNum(testNum);
				tReviewProjectService.save(tReviewProject);
			}
			boolean res = tReviewRecordService.saveBatch(reviewRecords);
			if (res) {
				return ResultUtil.data(res, "保存成功");
			} else {
				return ResultUtil.data(res, "保存失败");
			}
		} catch (Exception e) {
			 e.printStackTrace();
			return ResultUtil.error("保存异常:" + e.getMessage());
		}
	}

	/**
	 * 功能描述：新增复查记录数据
	 * @param personId 体检人员id
	 * @return 返回新增结果
	 */
	@SystemLog(description = "查询人员复查记录数据", type = LogType.OPERATION)
	@ApiOperation("查询人员复查记录数据")
	@GetMapping("getCheckProjectByPersonId")
	public Result<Object> getCheckProjectByPersonId(@Param("personId") String personId){
		try {
			List<TReviewRecord> result = tReviewRecordService.getCheckProjectByPersonId(personId);
			return ResultUtil.data(result);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultUtil.error("查询异常:" + e.getMessage());
		}
	}

	/**
	* 功能描述：更新数据
	* @param tReviewRecord 实体
	* @return 返回更新结果
	*/
	@SystemLog(description = "更新复查记录数据", type = LogType.OPERATION)
	@ApiOperation("更新复查记录数据")
	@PostMapping("updateTReviewRecord")
	public Result<Object> updateTReviewRecord(@RequestBody TReviewRecord tReviewRecord){
		if (StringUtils.isBlank(tReviewRecord.getId())) {
			return ResultUtil.error("参数为空，请联系管理员！！");
		}
		try {
			tReviewRecord.setUpdateId(securityUtil.getCurrUser().getId());
			tReviewRecord.setUpdateTime(new Date());
			boolean res = tReviewRecordService.updateById(tReviewRecord);
			if (res) {
				return ResultUtil.data(res, "修改成功");
			} else {
				return ResultUtil.error("修改失败");
			}
		} catch (Exception e) {
			 e.printStackTrace();
			return ResultUtil.error("保存异常:" + e.getMessage());
		}
	}

	/**
	* 功能描述：根据主键来删除数据
	* @param ids 主键集合
	* @return 返回删除结果
	*/
	@ApiOperation("根据主键来删除复查记录数据")
	@SystemLog(description = "根据主键来删除复查记录数据", type = LogType.OPERATION)
	@PostMapping("deleteTReviewRecord")
	public Result<Object> deleteTReviewRecord(@RequestParam String[] ids){
		if (ids == null || ids.length == 0) {
			return ResultUtil.error("参数为空，请联系管理员！！");
		}
		try {
			boolean res = tReviewRecordService.removeByIds(Arrays.asList(ids));
			if (res) {
				return ResultUtil.data(res, "删除成功");
			} else {
				return ResultUtil.error( "删除失败");
			}
		} catch (Exception e) {
			 e.printStackTrace();
			return ResultUtil.error("删除异常:" + e.getMessage());
		}
	}

	/**
	 * 功能描述：根据主键来修改复查项目状态
	 * @param ids 主键集合
	 * @return 返回结果
	 */
	@ApiOperation("根据主键来修改复查项目状态")
	@SystemLog(description = "根据主键来修改复查项目状态", type = LogType.OPERATION)
	@PostMapping("updateTReviewRecordById")
	public Result<Object> updateTReviewRecordById(@RequestBody String[] ids){
		if (ids == null) {
			return ResultUtil.error("参数为空，请联系管理员！！");
		}
		try {
			List<String> strings = Arrays.asList(ids);
			List<TReviewRecord> re = new ArrayList<>();
			for (String str:strings) {
				TReviewRecord byId = tReviewRecordService.getById(str);
				byId.setState(1);
				re.add(byId);
			}
			boolean res = tReviewRecordService.updateBatchById(re);
			if (res) {
				return ResultUtil.data(res, "审核成功");
			} else {
				return ResultUtil.data(res, "审核失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultUtil.error("删除异常:" + e.getMessage());
		}
	}

	/**
	 * 功能描述：根据主键来修改复查项目状态
	 * @param ids 主键集合
	 * @return 返回结果
	 */
	@ApiOperation("根据主键来修改复查项目状态")
	@SystemLog(description = "根据主键来修改复查项目状态", type = LogType.OPERATION)
	@PostMapping("approveTReviewRecordById")
	public Result<Object> approveTReviewRecordById(@RequestBody String[] ids){
		if (ids == null) {
			return ResultUtil.error("参数为空，请联系管理员！！");
		}
		try {
			List<String> strings = Arrays.asList(ids);
			List<TReviewRecord> re = new ArrayList<>();
			for (String str:strings) {
				TReviewRecord byId = tReviewRecordService.getById(str);
				byId.setState(2);
				re.add(byId);
			}
			boolean res = tReviewRecordService.updateBatchById(re);
			if (res) {
				return ResultUtil.data(res, "取消审核成功");
			} else {
				return ResultUtil.data(res, "取消审核失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultUtil.error("删除异常:" + e.getMessage());
		}
	}

	/**
	* 功能描述：根据主键来获取数据
	* @param id 主键
	* @return 返回获取结果
	*/
	@SystemLog(description = "根据主键来获取复查记录数据", type = LogType.OPERATION)
	@ApiOperation("根据主键来获取复查记录数据")
	@GetMapping("getTReviewRecord")
	public Result<Object> getTReviewRecord(@RequestParam(name = "id")String id){
		if (StringUtils.isBlank(id)) {
			return ResultUtil.error("参数为空，请联系管理员！！");
		}
		try {
			TReviewRecord res = tReviewRecordService.getById(id);
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
	* 功能描述：模糊查询复查记录数据
	* @param tReviewProject 需要模糊查询的信息
	* @return 返回获取结果
	*/
	@SystemLog(description = "模糊查询复查记录数据", type = LogType.OPERATION)
	@ApiOperation("模糊查询复查记录数据")
	@GetMapping("queryTReviewRecordList")
	public Result<Object> queryTReviewRecordList(TReviewProject  tReviewProject){
		try {
			QueryWrapper<TReviewProject> queryWrapper = new QueryWrapper<>();
			queryWrapper.eq("person_id",tReviewProject.getPersonId());
			List<TReviewProject> list = tReviewProjectService.list(queryWrapper);
			return ResultUtil.data(list);
		} catch (Exception e) {
			 e.printStackTrace();
			 return ResultUtil.error("查询异常:" + e.getMessage());
		}
	}





	/**
	* 功能描述：实现查询所有
	* @param searchVo 需要模糊查询的信息
	* @return 返回获取结果
	*/
	@SystemLog(description = "查询复查记录所有数据", type = LogType.OPERATION)
	@ApiOperation("查询复查记录所有数据")
	@GetMapping("queryTReviewRecordAll")
	public Result<Object> queryTReviewRecordAll(TReviewRecord  tReviewRecord, SearchVo searchVo){
		try {
			 List<TReviewRecord> result =tReviewRecordService.queryTReviewRecordList(tReviewRecord, searchVo);
			 return ResultUtil.data(result);
		} catch (Exception e) {
			 e.printStackTrace();
			 return ResultUtil.error("查询异常:" + e.getMessage());
		}
	}
	/**
	* 功能描述：导出数据
	* @param response 请求参数
	* @param tReviewRecord 查询参数
	* @return
	*/
	@SystemLog(description = "导出复查记录数据", type = LogType.OPERATION)
	@ApiOperation("导出复查记录数据")
	@PostMapping("/download")
	public void download(HttpServletResponse response,TReviewRecord  tReviewRecord){
		try {
			tReviewRecordService.download( tReviewRecord,response);
		} catch (Exception e) {
			 e.printStackTrace();
		}
	}

	/**
	 * 生成体检编号
	 *
	 * @return
	 */
	public String generatorNum() {
		QueryWrapper<TReviewProject> queryWrapper = new QueryWrapper<>();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String currentDay = format.format(new Date());
		queryWrapper.apply(StringUtils.isNotBlank(currentDay), "Date(create_time)=STR_TO_Date('" + currentDay + "','%Y-%m-%d')");
		queryWrapper.orderByDesc("test_num");
		queryWrapper.last("limit 1");
		TReviewProject one = tReviewProjectService.getOne(queryWrapper);
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		String testNum = "";
		if (one == null) {
			testNum = df.format(new Date());
			testNum += "0001";
		} else {
			String substring = one.getTestNum().substring(one.getTestNum().length() - 4);
			int i = Integer.valueOf(substring);
			i += 1;
			String code = String.valueOf(i);
			if (code.length() == 1) {
				code = "000" + code;
			} else if (code.length() == 2) {
				code = "00" + code;
			} else if (code.length() == 3) {
				code = "0" + code;
			}
			testNum = df.format(new Date());
			testNum += code;
		}
		return testNum;
	}

}
