package com.scmt.healthy.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.scmt.healthy.service.ITPastMedicalHistoryService;
import javax.servlet.http.HttpServletResponse;
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
import com.scmt.healthy.entity.TPastMedicalHistory;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.scmt.core.common.enums.LogType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
/**
 *@author 
 **/
@RestController
@Api(tags =" 既往病史数据接口")
@RequestMapping("/scmt/tPastMedicalHistory")
public class TPastMedicalHistoryController{
	@Autowired
	private ITPastMedicalHistoryService tPastMedicalHistoryService;
	@Autowired
	private SecurityUtil securityUtil;

	/**
	* 功能描述：新增既往病史数据
	* @param tPastMedicalHistory 实体
	* @return 返回新增结果
	*/
	@SystemLog(description = "新增既往病史数据", type = LogType.OPERATION)
	@ApiOperation("新增既往病史数据")
	@PostMapping("addTPastMedicalHistory")
	public Result<Object> addTPastMedicalHistory(@RequestBody TPastMedicalHistory tPastMedicalHistory){
		try {
			tPastMedicalHistory.setCreateId(securityUtil.getCurrUser().getId());
			tPastMedicalHistory.setCreateTime(new Date());
			boolean res = tPastMedicalHistoryService.save(tPastMedicalHistory);
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
	* 功能描述：更新数据
	* @param tPastMedicalHistory 实体
	* @return 返回更新结果
	*/
	@SystemLog(description = "更新既往病史数据", type = LogType.OPERATION)
	@ApiOperation("更新既往病史数据")
	@PostMapping("updateTPastMedicalHistory")
	public Result<Object> updateTPastMedicalHistory(@RequestBody TPastMedicalHistory tPastMedicalHistory){
		if (StringUtils.isBlank(tPastMedicalHistory.getId())) {
			return ResultUtil.error("参数为空，请联系管理员！！");
		}
		try {
			boolean res = tPastMedicalHistoryService.updateById(tPastMedicalHistory);
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
	@ApiOperation("根据主键来删除既往病史数据")
	@SystemLog(description = "根据主键来删除既往病史数据", type = LogType.OPERATION)
	@PostMapping("deleteTPastMedicalHistory")
	public Result<Object> deleteTPastMedicalHistory(@RequestParam String[] ids){
		if (ids == null || ids.length == 0) {
			return ResultUtil.error("参数为空，请联系管理员！！");
		}
		try {
			boolean res = tPastMedicalHistoryService.removeByIds(Arrays.asList(ids));
			if (res) {
				return ResultUtil.data(res, "删除成功");
			} else {
				return ResultUtil.error("删除失败");
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
	@SystemLog(description = "根据主键来获取既往病史数据", type = LogType.OPERATION)
	@ApiOperation("根据主键来获取既往病史数据")
	@GetMapping("getTPastMedicalHistory")
	public Result<Object> getTPastMedicalHistory(@RequestParam(name = "id")String id){
		if (StringUtils.isBlank(id)) {
			return ResultUtil.error("参数为空，请联系管理员！！");
		}
		try {
			TPastMedicalHistory res = tPastMedicalHistoryService.getById(id);
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
	* @param searchVo 需要模糊查询的信息
	* @param pageVo 分页参数
	* @return 返回获取结果
	*/
	@SystemLog(description = "分页查询既往病史数据", type = LogType.OPERATION)
	@ApiOperation("分页查询既往病史数据")
	@GetMapping("queryTPastMedicalHistoryList")
	public Result<Object> queryTPastMedicalHistoryList(TPastMedicalHistory  tPastMedicalHistory, SearchVo searchVo, PageVo pageVo){
		try {
			 IPage<TPastMedicalHistory> result =tPastMedicalHistoryService.queryTPastMedicalHistoryListByPage(tPastMedicalHistory, searchVo, pageVo);
			 return ResultUtil.data(result);
		} catch (Exception e) {
			 e.printStackTrace();
			 return ResultUtil.error("查询异常:" + e.getMessage());
		}
	}
	/**
	* 功能描述：实现查询全部
	* @param searchVo 需要模糊查询的信息
	* @return 返回获取结果
	*/
	@SystemLog(description = "查询既往病史全部数据", type = LogType.OPERATION)
	@ApiOperation("查询既往病史全部数据")
	@GetMapping("queryTPastMedicalHistoryAll")
	public Result<Object> queryTPastMedicalHistoryAll(TPastMedicalHistory  tPastMedicalHistory, SearchVo searchVo){
		try {
			 List<TPastMedicalHistory> result =tPastMedicalHistoryService.queryTPastMedicalHistoryAll(tPastMedicalHistory, searchVo);
			 return ResultUtil.data(result);
		} catch (Exception e) {
			 e.printStackTrace();
			 return ResultUtil.error("查询异常:" + e.getMessage());
		}
	}
	/**
	* 功能描述：导出数据
	* @param response 请求参数
	* @param tPastMedicalHistory 查询参数
	* @return 
	*/
	@SystemLog(description = "导出既往病史数据", type = LogType.OPERATION)
	@ApiOperation("导出既往病史数据")
	@PostMapping("/download")
	public void download(HttpServletResponse response,TPastMedicalHistory  tPastMedicalHistory){
		try {
			tPastMedicalHistoryService.download( tPastMedicalHistory,response);
		} catch (Exception e) {
			 e.printStackTrace();
		}
	}
}