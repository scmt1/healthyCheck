package com.scmt.healthy.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.scmt.core.common.annotation.SystemLog;
import com.scmt.core.common.constant.MessageConstant;
import com.scmt.core.common.enums.LogType;
import com.scmt.core.common.utils.ResultUtil;
import com.scmt.core.common.utils.SecurityUtil;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.Result;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.entity.TOrderSetting;
import com.scmt.healthy.service.ITOrderSettingService;
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
@Api(tags =" 预约设置数据接口")
@RequestMapping("/scmt/orderSetting")
public class TOrderSettingController{
	@Autowired
	private ITOrderSettingService tOrderSettingService;
	@Autowired
	private SecurityUtil securityUtil;

	/**
	* 功能描述：新增预约设置数据
	* @param tOrderSetting 实体
	* @return 返回新增结果
	*/
	@ApiOperation("新增预约设置数据")
	@PostMapping("addTOrderSetting")
	public Result<Object> addTOrderSetting(@RequestBody TOrderSetting tOrderSetting){
		try {
			//如果体检机构id为空
			if(tOrderSetting.getCheckOrgId() == null){
				return ResultUtil.error(MessageConstant.PARAMETER_IS_NULL);
			}
			//如果预约设置容量为空
			if(tOrderSetting.getNumber() == null){
				tOrderSetting.setNumber(50);
			}
			tOrderSetting.setReservations(0);
			tOrderSetting.setDelFlag(0);
			tOrderSetting.setCreateBy(securityUtil.getCurrUser().getId());
			tOrderSetting.setCreateTime(new Date());
			boolean res = tOrderSettingService.save(tOrderSetting);
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
	* 功能描述：根据主键来删除数据
	* @param ids 主键集合
	* @return 返回删除结果
	*/
	@ApiOperation("根据主键来删除预约设置数据")
	@PostMapping("deleteTOrderSetting")
	public Result<Object> deleteTOrderSetting(@RequestParam String[] ids){
		if (ids == null || ids.length == 0) {
			return ResultUtil.error(MessageConstant.PARAMETER_IS_NULL);
		}
		try {
			boolean res = tOrderSettingService.removeByIds(Arrays.asList(ids));
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
	@ApiOperation("根据主键来获取预约设置数据")
	@GetMapping("getTOrderSetting")
	public Result<Object> getTOrderSetting(@RequestParam(name = "id")String id){
		if (StringUtils.isBlank(id)) {
			return ResultUtil.error(MessageConstant.PARAMETER_IS_NULL);
		}
		try {
			TOrderSetting res = tOrderSettingService.getById(id);
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
	@ApiOperation("分页查询预约设置数据")
	@GetMapping("queryTOrderSettingList")
	public Result<Object> queryTOrderSettingList(TOrderSetting  tOrderSetting, SearchVo searchVo, PageVo pageVo){
		try {
			 IPage<TOrderSetting> result =tOrderSettingService.queryTOrderSettingListByPage(tOrderSetting, searchVo, pageVo);
			 return ResultUtil.data(result);
		} catch (Exception e) {
			 e.printStackTrace();
			 return ResultUtil.error("查询异常:" + e.getMessage());
		}
	}
	/**
	* 功能描述：导出数据
	* @param response 请求参数
	* @param tOrderSetting 查询参数
	* @return
	*/
	@ApiOperation("导出预约设置数据")
	@PostMapping("/download")
	public void download(HttpServletResponse response,TOrderSetting  tOrderSetting){
		try {
			tOrderSettingService.download( tOrderSetting,response);
		} catch (Exception e) {
			 e.printStackTrace();
		}
	}

	/**
	 * 获取excel数据，批量导入数据
	 * @param tOrderSettings
	 * @return
	 */
	@SystemLog(description = "获取excel导入的数据", type = LogType.OPERATION)
	@ApiOperation("获取excel导入的数据")
	@RequestMapping(value = "/importExcel",method = RequestMethod.POST)
	public Result uploadExcel(@RequestBody List<TOrderSetting> tOrderSettings) {
		try {
			boolean res = tOrderSettingService.saveOrUpdateBatchInfo(tOrderSettings);
			if (res) {
				return ResultUtil.data(res,MessageConstant.IMPORT_DATA_SUCCESS);
			}
			return ResultUtil.error(MessageConstant.IMPORT_DATA_FAIL);
		}catch (Exception e){
			e.printStackTrace();
			return ResultUtil.error(MessageConstant.IMPORT_EXCEPTION + e.getMessage());
		}
	}

	@SystemLog(description = "根据体检机构,时间获取对应体检机构的预约设置", type = LogType.OPERATION)
	@ApiOperation("根据体检机构和体检类型获取对应体检机构的预约设置")
	@RequestMapping(value = "getOrderSettingInfo",method = RequestMethod.GET)
	public Result<Object> getOrderSettingInfo(TOrderSetting tOrderSetting,String dateTime){
		try {
			List<Map<String, Object>> list = tOrderSettingService.findOrderSettingInfoByOrg(tOrderSetting,dateTime);
			if (list != null && list.size() > 0){
				return ResultUtil.data(list,MessageConstant.QUERY_DATA_SUCCESS);
			}
			return ResultUtil.data(list,MessageConstant.QUERY_DATA_FAIL);
		}catch (Exception e){
			e.printStackTrace();
			return ResultUtil.error(MessageConstant.QUERY_EXCEPTION + e.getMessage());
		}
	}
	@ApiOperation("根据体检机构id和当前时间获取对应时间区间的可预约设置信息")
	@RequestMapping(value = "getOrderSettingInfoByOrderDate",method = RequestMethod.GET)
	public Result<Object> getOrderSettingByOrg(TOrderSetting tOrderSetting){
		if(tOrderSetting == null){
			return ResultUtil.error(MessageConstant.PARAMETER_IS_NULL);
		}
		try {
			List<Map<String,Object>> list = tOrderSettingService.findOrderSettingInfoByOrgId(tOrderSetting);
			if(list != null && list.size() > 0){
				return ResultUtil.data(list,MessageConstant.QUERY_DATA_SUCCESS);
			}
			return ResultUtil.data(list,MessageConstant.QUERY_DATA_FAIL);
		}catch (Exception e){
			return ResultUtil.error(MessageConstant.QUERY_EXCEPTION + e.getMessage());
		}
	}

	/**
	 * 功能描述：更新数据
	 * @param tOrderSetting 实体
	 * @return 返回更新结果
	 */
	@ApiOperation("更新预约设置数据")
	@PostMapping("updateOrderSettingInfo")
	public Result<Object> updateTOrderSetting(@RequestBody TOrderSetting tOrderSetting){
		if (StringUtils.isBlank(tOrderSetting.getId()) && StringUtils.isNotBlank(tOrderSetting.getCheckOrgId())) {
			return ResultUtil.error(MessageConstant.PARAMETER_IS_NULL);
		}
		if(tOrderSetting.getReservations() > tOrderSetting.getNumber()){
			return ResultUtil.error("可预约人数已超出容量");
		}
		try {
			boolean res = tOrderSettingService.updateOrderSettingInfo(tOrderSetting);
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

}
