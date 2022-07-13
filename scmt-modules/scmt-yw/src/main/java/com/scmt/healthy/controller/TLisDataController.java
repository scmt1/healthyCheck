package com.scmt.healthy.controller;

import java.util.Arrays;
import java.util.Date;
import com.scmt.healthy.service.ITLisDataService;
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
import com.scmt.healthy.entity.TLisData;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.scmt.core.common.enums.LogType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
/**
 *@author 
 **/
@RestController
@Api(tags =" tLisData数据接口")
@RequestMapping("/scmt/tLisData")
public class TLisDataController{
	@Autowired
	private ITLisDataService tLisDataService;
	@Autowired
	private SecurityUtil securityUtil;

	/**
	* 功能描述：新增tLisData数据
	* @param tLisData 实体
	* @return 返回新增结果
	*/
	@SystemLog(description = "新增tLisData数据", type = LogType.OPERATION)
	@ApiOperation("新增tLisData数据")
	@PostMapping("addTLisData")
	public Result<Object> addTLisData(@RequestBody TLisData tLisData){
		try {
			tLisData.setDelFlag(0);
			tLisData.setCreateId(securityUtil.getCurrUser().getId());
			tLisData.setCreateTime(new Date());
			boolean res = tLisDataService.save(tLisData);
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
	* @param tLisData 实体
	* @return 返回更新结果
	*/
	@SystemLog(description = "更新tLisData数据", type = LogType.OPERATION)
	@ApiOperation("更新tLisData数据")
	@PostMapping("updateTLisData")
	public Result<Object> updateTLisData(@RequestBody TLisData tLisData){
		if (StringUtils.isBlank(tLisData.getId())) {
			return ResultUtil.error("参数为空，请联系管理员！！");
		}
		try {
			tLisData.setUpdateId(securityUtil.getCurrUser().getId());
			tLisData.setUpdateTime(new Date());
			boolean res = tLisDataService.updateById(tLisData);
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
	@ApiOperation("根据主键来删除tLisData数据")
	@SystemLog(description = "根据主键来删除tLisData数据", type = LogType.OPERATION)
	@PostMapping("deleteTLisData")
	public Result<Object> deleteTLisData(@RequestParam String[] ids){
		if (ids == null || ids.length == 0) {
			return ResultUtil.error("参数为空，请联系管理员！！");
		}
		try {
			boolean res = tLisDataService.removeByIds(Arrays.asList(ids));
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
	@SystemLog(description = "根据主键来获取tLisData数据", type = LogType.OPERATION)
	@ApiOperation("根据主键来获取tLisData数据")
	@GetMapping("getTLisData")
	public Result<Object> getTLisData(@RequestParam(name = "id")String id){
		if (StringUtils.isBlank(id)) {
			return ResultUtil.error("参数为空，请联系管理员！！");
		}
		try {
			TLisData res = tLisDataService.getById(id);
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
	@SystemLog(description = "分页查询tLisData数据", type = LogType.OPERATION)
	@ApiOperation("分页查询tLisData数据")
	@GetMapping("queryTLisDataList")
	public Result<Object> queryTLisDataList(TLisData  tLisData, SearchVo searchVo, PageVo pageVo){
		try {
			 IPage<TLisData> result =tLisDataService.queryTLisDataListByPage(tLisData, searchVo, pageVo);
			 return ResultUtil.data(result);
		} catch (Exception e) {
			 e.printStackTrace();
			 return ResultUtil.error("查询异常:" + e.getMessage());
		}
	}
	/**
	* 功能描述：导出数据
	* @param response 请求参数
	* @param tLisData 查询参数
	* @return 
	*/
	@SystemLog(description = "导出tLisData数据", type = LogType.OPERATION)
	@ApiOperation("导出tLisData数据")
	@PostMapping("/download")
	public void download(HttpServletResponse response,TLisData  tLisData){
		try {
			tLisDataService.download( tLisData,response);
		} catch (Exception e) {
			 e.printStackTrace();
		}
	}
}