package com.scmt.healthy.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.scmt.healthy.service.ITCareerHistoryService;
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
import com.scmt.healthy.entity.TCareerHistory;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.scmt.core.common.enums.LogType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
/**
 *@author
 **/
@RestController
@Api(tags =" 职业史数据接口")
@RequestMapping("/scmt/tCareerHistory")
public class TCareerHistoryController{
	@Autowired
	private ITCareerHistoryService tCareerHistoryService;
	@Autowired
	private SecurityUtil securityUtil;

	/**
	* 功能描述：新增职业史数据
	* @param tCareerHistory 实体
	* @return 返回新增结果
	*/
	@SystemLog(description = "新增职业史数据", type = LogType.OPERATION)
	@ApiOperation("新增职业史数据")
	@PostMapping("addTCareerHistory")
	public Result<Object> addTCareerHistory(@RequestBody TCareerHistory tCareerHistory){
		try {
			tCareerHistory.setCreateId(securityUtil.getCurrUser().getId());
			tCareerHistory.setCreateTime(new Date());
			boolean res = tCareerHistoryService.save(tCareerHistory);
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
	* @param tCareerHistory 实体
	* @return 返回更新结果
	*/
	@SystemLog(description = "更新职业史数据", type = LogType.OPERATION)
	@ApiOperation("更新职业史数据")
	@PostMapping("updateTCareerHistory")
	public Result<Object> updateTCareerHistory(@RequestBody TCareerHistory tCareerHistory){
		if (StringUtils.isBlank(tCareerHistory.getId())) {
			return ResultUtil.error("参数为空，请联系管理员！！");
		}
		try {
			boolean res = tCareerHistoryService.updateById(tCareerHistory);
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
	@ApiOperation("根据主键来删除职业史数据")
	@SystemLog(description = "根据主键来删除职业史数据", type = LogType.OPERATION)
	@PostMapping("deleteTCareerHistory")
	public Result<Object> deleteTCareerHistory(@RequestParam String[] ids){
		if (ids == null || ids.length == 0) {
			return ResultUtil.error("参数为空，请联系管理员！！");
		}
		try {
			boolean res = tCareerHistoryService.removeByIds(Arrays.asList(ids));
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
	@SystemLog(description = "根据主键来获取职业史数据", type = LogType.OPERATION)
	@ApiOperation("根据主键来获取职业史数据")
	@GetMapping("getTCareerHistory")
	public Result<Object> getTCareerHistory(@RequestParam(name = "id")String id){
		if (StringUtils.isBlank(id)) {
			return ResultUtil.error("参数为空，请联系管理员！！");
		}
		try {
			TCareerHistory res = tCareerHistoryService.getById(id);
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
	@SystemLog(description = "分页查询职业史数据", type = LogType.OPERATION)
	@ApiOperation("分页查询职业史数据")
	@GetMapping("queryTCareerHistoryList")
	public Result<Object> queryTCareerHistoryList(TCareerHistory  tCareerHistory, SearchVo searchVo, PageVo pageVo){
		try {
			 IPage<TCareerHistory> result =tCareerHistoryService.queryTCareerHistoryListByPage(tCareerHistory, searchVo, pageVo);
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
	@SystemLog(description = "查询全部职业史数据", type = LogType.OPERATION)
	@ApiOperation("查询全部职业史数据")
	@GetMapping("queryTCareerHistoryAll")
	public Result<Object> queryTCareerHistoryAll(TCareerHistory  tCareerHistory, SearchVo searchVo){
		try {
			List<TCareerHistory> result =tCareerHistoryService.queryTCareerHistoryAll(tCareerHistory,searchVo);
			return ResultUtil.data(result);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultUtil.error("查询异常:" + e.getMessage());
		}
	}
	/**
	* 功能描述：导出数据
	* @param response 请求参数
	* @param tCareerHistory 查询参数
	* @return
	*/
	@SystemLog(description = "导出职业史数据", type = LogType.OPERATION)
	@ApiOperation("导出职业史数据")
	@PostMapping("/download")
	public void download(HttpServletResponse response,TCareerHistory  tCareerHistory){
		try {
			tCareerHistoryService.download( tCareerHistory,response);
		} catch (Exception e) {
			 e.printStackTrace();
		}
	}
}
