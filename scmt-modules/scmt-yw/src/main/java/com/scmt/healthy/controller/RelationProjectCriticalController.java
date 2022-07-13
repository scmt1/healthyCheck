package com.scmt.healthy.controller;

import java.util.Arrays;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.scmt.core.common.redis.RedisTemplateHelper;
import com.scmt.core.common.utils.ResultUtil;
import com.scmt.core.common.utils.SecurityUtil;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.Result;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.core.entity.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.scmt.healthy.entity.RelationProjectCritical;
import com.scmt.healthy.service.IRelationProjectCriticalService;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
/**
 *@author
 **/
@RestController
@Api(tags =" 项目危急值数据接口")
@RequestMapping("/scmt/relationProjectCritical")
public class RelationProjectCriticalController{
	@Autowired
	private IRelationProjectCriticalService relationProjectCriticalService;
	@Autowired
	private SecurityUtil securityUtil;
	@Autowired
	private RedisTemplateHelper redisTemplate;
	/**
	* 功能描述：新增项目危急值数据
	* @param relationProjectCritical 实体
	* @return 返回新增结果
	*/
	@ApiOperation("新增项目危急值数据")
	@PostMapping("addRelationProjectCritical")
	public Result<Object> addRelationProjectCritical(@RequestBody RelationProjectCritical relationProjectCritical){
		try {
			User currUser = securityUtil.getCurrUser();
			relationProjectCritical.setCreateId(currUser.getId());
			relationProjectCritical.setCreateTime(new Date());
			relationProjectCritical.setDepartmentId(currUser.getDepartmentId());
			boolean res = relationProjectCriticalService.save(relationProjectCritical);
			if (res) {
				redisTemplate.delete("permission::relationProjectCritical:" + securityUtil.getCurrUser().getId());
				return ResultUtil.data(res, "保存成功");
			} else {
				return ResultUtil.error("保存失败");
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
	@ApiOperation("根据主键来删除项目危急值数据")
	@PostMapping("deleteRelationProjectCritical")
	public Result<Object> deleteRelationProjectCritical(@RequestParam String[] ids){
		if (ids == null || ids.length == 0) {
			return ResultUtil.error("参数为空，请联系管理员！！");
		}
		try {
			boolean res = relationProjectCriticalService.removeByIds(Arrays.asList(ids));
			if (res) {
				redisTemplate.delete("permission::relationProjectCritical:" + securityUtil.getCurrUser().getId());
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
	* 功能描述：根据主键来获取数据
	* @param id 主键
	* @return 返回获取结果
	*/
	@ApiOperation("根据主键来获取项目危急值数据")
	@GetMapping("getRelationProjectCritical")
	public Result<Object> getRelationProjectCritical(@RequestParam String id){
		if (StringUtils.isBlank(id)) {
			return ResultUtil.error("参数为空，请联系管理员！！");
		}
		try {
			RelationProjectCritical res = relationProjectCriticalService.getById(id);
			if (res != null) {
				return ResultUtil.data(res, "查询成功");
			} else {
				return ResultUtil.error("查询失败:暂无数据");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultUtil.error("查询异常:" + e.getMessage());
		}
	}

	/**
	* 功能描述：实现分页查询
	* @param relationProjectCritical 需要模糊查询的信息
	* @return 返回获取结果
	*/
	@ApiOperation("分页查询项目危急值数据")
	@GetMapping("queryRelationProjectCriticalList")
	public Result<Object> queryRelationProjectCriticalList(RelationProjectCritical  relationProjectCritical){
		try {
			List<RelationProjectCritical> list = relationProjectCriticalService.queryRelationProjectCriticalList(relationProjectCritical);
			return ResultUtil.data(list);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultUtil.error("查询异常:" + e.getMessage());
		}
	}
	/**
	* 功能描述：更新数据
	* @param relationProjectCritical 实体
	* @return 返回更新结果
	*/
	@ApiOperation("更新项目危急值数据")
	@PostMapping("updateRelationProjectCritical")
	public Result<Object> updateRelationProjectCritical(@RequestBody RelationProjectCritical relationProjectCritical){
		if (StringUtils.isBlank(relationProjectCritical.getId())) {
			return ResultUtil.error("参数为空，请联系管理员！！");
		}
		try {
			boolean res = relationProjectCriticalService.updateById(relationProjectCritical);
			if (res) {
				redisTemplate.delete("permission::relationProjectCritical:" + securityUtil.getCurrUser().getId());
				return ResultUtil.data(res, "保存成功");
			} else {
				return ResultUtil.error("保存失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultUtil.error("保存异常:" + e.getMessage());
		}
	}

	/**
	* 功能描述：导出数据
	* @param response 请求参数
	* @param relationProjectCritical 查询参数
	* @return
	*/
	@ApiOperation("导出项目危急值数据")
	@PostMapping("/download")
	public void download(HttpServletResponse response,RelationProjectCritical  relationProjectCritical){
		try {
			relationProjectCriticalService.download( relationProjectCritical,response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
