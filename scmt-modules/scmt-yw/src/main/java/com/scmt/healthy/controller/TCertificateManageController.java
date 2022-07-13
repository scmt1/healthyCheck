package com.scmt.healthy.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.scmt.healthy.entity.TGroupPerson;
import com.scmt.healthy.entity.TUnitReport;
import com.scmt.healthy.service.ITCertificateManageService;
import javax.servlet.http.HttpServletResponse;

import com.scmt.healthy.service.ITGroupPersonService;
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
import com.scmt.healthy.entity.TCertificateManage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.scmt.core.common.enums.LogType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
/**
 *@author
 **/
@RestController
@Api(tags =" com.scmt.healthy数据接口")
@RequestMapping("/scmt/tCertificateManage")
public class TCertificateManageController{
	@Autowired
	private ITCertificateManageService tCertificateManageService;
	@Autowired
	private SecurityUtil securityUtil;
	@Autowired
	private ITGroupPersonService tGroupPersonService;

	/**
	* 功能描述：新增com.scmt.healthy数据
	* @param tCertificateManage 实体
	* @return 返回新增结果
	*/
	@SystemLog(description = "新增com.scmt.healthy数据", type = LogType.OPERATION)
	@ApiOperation("新增com.scmt.healthy数据")
	@PostMapping("addTCertificateManage")
	public Result<Object> addTCertificateManage(@RequestBody TCertificateManage tCertificateManage){
		try {
			TGroupPerson tGroupPerson = tGroupPersonService.getById(tCertificateManage.getPersonId());
			tCertificateManage.setDelFlag(0);
			tCertificateManage.setCreateId(securityUtil.getCurrUser().getId());
			tCertificateManage.setCreateTime(new Date());
			tCertificateManage.setHeadImg(tGroupPerson.getAvatar());
			boolean res = tCertificateManageService.save(tCertificateManage);
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
	* @param tCertificateManage 实体
	* @return 返回更新结果
	*/
	@SystemLog(description = "更新com.scmt.healthy数据", type = LogType.OPERATION)
	@ApiOperation("更新com.scmt.healthy数据")
	@PostMapping("updateTCertificateManage")
	public Result<Object> updateTCertificateManage(@RequestBody TCertificateManage tCertificateManage){
		if (StringUtils.isBlank(tCertificateManage.getId())) {
			return ResultUtil.error("参数为空，请联系管理员！！");
		}
		try {
			tCertificateManage.setUpdateId(securityUtil.getCurrUser().getId());
			tCertificateManage.setUpdateTime(new Date());
			boolean res = tCertificateManageService.updateById(tCertificateManage);
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
	@ApiOperation("根据主键来删除com.scmt.healthy数据")
	@SystemLog(description = "根据主键来删除com.scmt.healthy数据", type = LogType.OPERATION)
	@PostMapping("deleteTCertificateManage")
	public Result<Object> deleteTCertificateManage(@RequestParam String[] ids){
		if (ids == null || ids.length == 0) {
			return ResultUtil.error("参数为空，请联系管理员！！");
		}
		try {
			boolean res = tCertificateManageService.removeByIds(Arrays.asList(ids));
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
	@SystemLog(description = "根据主键来获取com.scmt.healthy数据", type = LogType.OPERATION)
	@ApiOperation("根据主键来获取com.scmt.healthy数据")
	@GetMapping("getTCertificateManage")
	public Result<Object> getTCertificateManage(@RequestParam(name = "id")String id){
		if (StringUtils.isBlank(id)) {
			return ResultUtil.error("参数为空，请联系管理员！！");
		}
		try {
			TCertificateManage res = tCertificateManageService.getById(id);
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
	@SystemLog(description = "分页查询com.scmt.healthy数据", type = LogType.OPERATION)
	@ApiOperation("分页查询com.scmt.healthy数据")
	@GetMapping("queryTCertificateManageList")
	public Result<Object> queryTCertificateManageList(TCertificateManage  tCertificateManage, SearchVo searchVo, PageVo pageVo){
		try {
			 IPage<TCertificateManage> result =tCertificateManageService.queryTCertificateManageListByPage(tCertificateManage, searchVo, pageVo);
			 return ResultUtil.data(result);
		} catch (Exception e) {
			 e.printStackTrace();
			 return ResultUtil.error("查询异常:" + e.getMessage());
		}
	}

	/**
	 * 功能描述：实现查询全部
	 * @param tCertificateManage 查询参数
	 * @param searchVo 需要模糊查询的信息
	 * @return 返回获取结果
	 */
	@SystemLog(description = "查询tCertificateManage数据", type = LogType.OPERATION)
	@ApiOperation("查询tCertificateManage数据")
	@GetMapping("queryTCertificateManageAll")
	public Result<Object> queryTCertificateManageAll(TCertificateManage tCertificateManage, SearchVo searchVo){
		try {
			List<TCertificateManage> result =tCertificateManageService.queryTCertificateManageByNotPage(tCertificateManage, searchVo);
			return ResultUtil.data(result);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultUtil.error("查询异常:" + e.getMessage());
		}
	}

	/**
	* 功能描述：导出数据
	* @param response 请求参数
	* @param tCertificateManage 查询参数
	* @return
	*/
	@SystemLog(description = "导出com.scmt.healthy数据", type = LogType.OPERATION)
	@ApiOperation("导出com.scmt.healthy数据")
	@PostMapping("/download")
	public void download(HttpServletResponse response,TCertificateManage  tCertificateManage){
		try {
			tCertificateManageService.download( tCertificateManage,response);
		} catch (Exception e) {
			 e.printStackTrace();
		}
	}
}
