package com.scmt.healthy.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.scmt.core.common.constant.MessageConstant;
import com.scmt.core.common.utils.ResultUtil;
import com.scmt.core.common.utils.SecurityUtil;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.Result;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.entity.TOrderRecord;
import com.scmt.healthy.service.ITGroupPersonService;
import com.scmt.healthy.service.ITOrderRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *@author
 **/
@RestController
@Api(tags =" 预约记录数据接口")
@RequestMapping("/scmt/tOrderRecord")
public class TOrderRecordController{
	@Autowired
	private ITOrderRecordService tOrderRecordService;
	@Autowired
	private SecurityUtil securityUtil;

	@Autowired
	private ITGroupPersonService itGroupPersonService;

	/**
	* 功能描述：新增预约记录数据
	* @param tOrderRecord 实体
	* @return 返回新增结果
	*/
	@ApiOperation("新增预约记录数据")
	@PostMapping("addTOrderRecord")
	public Result<Object> addTOrderRecord(@RequestBody TOrderRecord tOrderRecord){
		try {
			tOrderRecord.setDelFlag(0);
			tOrderRecord.setCreateBy(securityUtil.getCurrUser().getId());
			tOrderRecord.setCreateTime(new Date());
			boolean res = tOrderRecordService.save(tOrderRecord);
			if (res) {
				return ResultUtil.data(res, MessageConstant.SAVE_SUCCESS);
			} else {
				return ResultUtil.error(MessageConstant.SAVE_FAIL);
			}
		} catch (Exception e) {
			 e.printStackTrace();
			return ResultUtil.error(MessageConstant.SAVE_EXCEPTION + e.getMessage());
		}
	}

	/**
	* 功能描述：更新数据
	* @param tOrderRecord 实体
	* @return 返回更新结果
	*/
	@ApiOperation("更新预约记录数据")
	@PostMapping("updateTOrderRecord")
	public Result<Object> updateTOrderRecord(@RequestBody TOrderRecord tOrderRecord){
		if (StringUtils.isBlank(tOrderRecord.getId())) {
			return ResultUtil.error(MessageConstant.PARAMETER_IS_NULL);
		}
		try {
			tOrderRecord.setUpdateBy(securityUtil.getCurrUser().getId());
			tOrderRecord.setUpdateTime(new Date());
			boolean res = tOrderRecordService.updateById(tOrderRecord);
			if (res) {
				return ResultUtil.data(res, MessageConstant.UPDATE_DATA_SUCCESS);
			} else {
				return ResultUtil.error(MessageConstant.UPDATE_DATA_FAIL);
			}
		} catch (Exception e) {
			 e.printStackTrace();
			return ResultUtil.error(MessageConstant.SAVE_EXCEPTION + e.getMessage());
		}
	}

	/**
	* 功能描述：根据主键来删除数据
	* @param ids 主键集合
	* @return 返回删除结果
	*/
	@ApiOperation("根据主键来删除预约记录数据")
	@PostMapping("deleteTOrderRecord")
	public Result<Object> deleteTOrderRecord(@RequestParam String[] ids){
		if (ids == null || ids.length == 0) {
			return ResultUtil.error(MessageConstant.PARAMETER_IS_NULL);
		}
		try {
			boolean res = tOrderRecordService.removeByIds(Arrays.asList(ids));
			if (res) {
				return ResultUtil.data(res, MessageConstant.DELETE_DATA_SUCCESS);
			} else {
				return ResultUtil.error(MessageConstant.DELETE_DATA_FAIL);
			}
		} catch (Exception e) {
			 e.printStackTrace();
			return ResultUtil.error(MessageConstant.DELETE_DATA_EXCEPTION + e.getMessage());
		}
	}

	/**
	* 功能描述：根据主键来获取数据
	* @param id 主键
	* @return 返回获取结果
	*/
	@ApiOperation("根据主键来获取预约记录数据")
	@GetMapping("getTOrderRecord")
	public Result<Object> getTOrderRecord(@RequestParam(name = "id")String id){
		if (StringUtils.isBlank(id)) {
			return ResultUtil.error(MessageConstant.PARAMETER_IS_NULL);
		}
		try {
			TOrderRecord res = tOrderRecordService.getById(id);
			if (res != null) {
				return ResultUtil.data(res, MessageConstant.QUERY_DATA_SUCCESS);
			} else {
				return ResultUtil.data(res,MessageConstant.QUERY_DATA_FAIL);
			}
		} catch (Exception e) {
			 e.printStackTrace();
			return ResultUtil.error(MessageConstant.QUERY_EXCEPTION + e.getMessage());
		}
	}

	/**
	* 功能描述：实现分页查询
	* @param searchVo 需要模糊查询的信息
	* @param pageVo 分页参数
	* @return 返回获取结果
	*/
	@ApiOperation("分页查询预约记录数据")
	@GetMapping("queryTOrderRecordList")
	public Result<Object> queryTOrderRecordList(TOrderRecord  tOrderRecord, SearchVo searchVo, PageVo pageVo){
		try {
			 IPage<TOrderRecord> result =tOrderRecordService.queryTOrderRecordListByPage(tOrderRecord, searchVo, pageVo);
			 return ResultUtil.data(result, MessageConstant.QUERY_DATA_SUCCESS);
		} catch (Exception e) {
			 e.printStackTrace();
			 return ResultUtil.error(MessageConstant.QUERY_EXCEPTION + e.getMessage());
		}
	}
	/**
	* 功能描述：导出数据
	* @param response 请求参数
	* @param tOrderRecord 查询参数
	* @return
	*/
	@ApiOperation("导出预约记录数据")
	@PostMapping("/download")
	public void download(HttpServletResponse response,TOrderRecord  tOrderRecord,SearchVo searchVo, PageVo pageVo){
		try {
			tOrderRecordService.download( tOrderRecord,response,searchVo,pageVo);
		} catch (Exception e) {
			 e.printStackTrace();
		}
	}

	@ApiOperation("根据订单id来获取对应的预约记录")
	@GetMapping("getOrderRecordInfoByOrderId")
	public Result<Object> queryTOrderRecordInfoByOrderId(@RequestParam("orderId") String orderId){
		try {
			if(orderId == null){
				return ResultUtil.error(MessageConstant.PARAMETER_IS_NULL);
			}
			Map<String, Object> result = tOrderRecordService.getOrderRecordInfoByOrderId(orderId);
			if(result != null){
				return ResultUtil.data(result, MessageConstant.QUERY_DATA_SUCCESS);
			}
			return ResultUtil.data(result,"暂无该体检预约记录!");
		} catch (Exception e) {
			e.printStackTrace();
			return ResultUtil.error(MessageConstant.QUERY_EXCEPTION + e.getMessage());
		}
	}

	@ApiOperation("根据手机号来查看用户的预约记录")
	@GetMapping("getOrderRecordInfoListByMobile")
	public Result<Object> queryTOrderRecordListByMobile(String mobile,String isPass){
		try {
			if(mobile  == null){
				return ResultUtil.error(MessageConstant.PARAMETER_IS_NULL);
			}
			List<Map<String,Object>> result = tOrderRecordService.getOrderRecordInfoListByMobile(mobile,isPass);
			if(result != null && result.size() > 0){
				return ResultUtil.data(result, MessageConstant.QUERY_DATA_SUCCESS);
			}
			return ResultUtil.data(result,"暂无该体检预约记录!");
		}catch (Exception e){
			e.printStackTrace();
			return ResultUtil.error(MessageConstant.QUERY_EXCEPTION + e.getMessage());
		}
	}
}
