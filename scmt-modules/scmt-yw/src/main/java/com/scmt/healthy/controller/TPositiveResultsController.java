package com.scmt.healthy.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.scmt.core.common.utils.ResultUtil;
import com.scmt.core.common.utils.SecurityUtil;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.Result;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.entity.TPositivePerson;
import com.scmt.healthy.entity.TPositiveResults;
import com.scmt.healthy.entity.TPositiveResultsRule;
import com.scmt.healthy.entity.TPositiveRule;
import com.scmt.healthy.service.ITPositivePersonService;
import com.scmt.healthy.service.ITPositiveResultsRuleService;
import com.scmt.healthy.service.ITPositiveResultsService;
import com.scmt.healthy.service.ITPositiveRuleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 *@author 
 **/
@RestController
@Api(tags =" com.scmt.healthy数据接口")
@RequestMapping("/scmt/tPositiveResultList")
public class TPositiveResultsController{
	@Autowired
	private ITPositiveResultsService tPositiveResultsService;

	@Autowired
	private ITPositiveResultsRuleService tPositiveResults;

	@Autowired
	private ITPositiveRuleService itPositiveRuleService;

	@Autowired
	private ITPositivePersonService itPositivePersonService;

	@Autowired
	private SecurityUtil securityUtil;

	/**
	* 功能描述：新增com.scmt.healthy数据
	* @param tPositiveResults 实体
	* @return 返回新增结果
	*/
	@ApiOperation("新增数据")
	@PostMapping("addTPositiveResults")
	public Result<Object> addTPositiveResults(@RequestBody TPositiveResults tPositiveResults){
		try {
			tPositiveResults.setCreateId(securityUtil.getCurrUser().getId());
			tPositiveResults.setCreateTime(new Date());
			tPositiveResults.setCreatedUserName(securityUtil.getCurrUser().getId());
			boolean res = tPositiveResultsService.save(tPositiveResults);
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
	* @param tPositiveResults 实体
	* @return 返回更新结果
	*/
	@ApiOperation("更新com.scmt.healthy数据")
	@PostMapping("updateTPositiveResults")
	public Result<Object> updateTPositiveResults(@RequestBody TPositiveResults tPositiveResults){
		if (StringUtils.isBlank(tPositiveResults.getId())) {
			return ResultUtil.error("参数为空，请联系管理员！！");
		}
		try {
			tPositiveResults.setUpdateTime(new Date());
			tPositiveResults.setUpdateId(securityUtil.getCurrUser().getId());
			boolean res = tPositiveResultsService.updateById(tPositiveResults);
			if (res) {
				return ResultUtil.data(res, "修改成功");
			} else {
				return ResultUtil.data(res, "修改失败");
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
	@PostMapping("deleteTPositiveResults")
	public Result<Object> deleteTPositiveResults(@RequestParam String[] ids){
		if (ids == null || ids.length == 0) {
			return ResultUtil.error("参数为空，请联系管理员！！");
		}
		try {
			boolean res = tPositiveResultsService.removeByIds(Arrays.asList(ids));
			if (res) {
				return ResultUtil.data(res, "删除成功");
			} else {
				return ResultUtil.data(res, "删除失败");
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
	@ApiOperation("根据主键来获取com.scmt.healthy数据")
	@GetMapping("getTPositiveResults")
	public Result<Object> getTPositiveResults(@RequestParam(name = "id")String id){
		if (StringUtils.isBlank(id)) {
			return ResultUtil.error("参数为空，请联系管理员！！");
		}
		try {
			TPositiveResults res = tPositiveResultsService.getById(id);
			if (res != null) {
				return ResultUtil.data(res, "查询成功");
			} else {
				return ResultUtil.data(res, "查询失败");
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
	@ApiOperation("分页查询com.scmt.healthy数据")
	@GetMapping("queryTPositiveResultsList")
	public Result<Object> queryTPositiveResultsList(TPositiveResults  tPositiveResults, SearchVo searchVo, PageVo pageVo){
		try {
			 IPage<TPositiveResults> result =tPositiveResultsService.querytPositiveResultList(tPositiveResults, searchVo, pageVo);
			 return ResultUtil.data(result);
		} catch (Exception e) {
			 e.printStackTrace();
			 return ResultUtil.error("查询异常:" + e.getMessage());
		}
	}
	/**
	 * 功能描述：新增com.scmt.healthy数据
	 * @param tPositiveResultsRule 实体
	 * @return 返回新增结果
	 */
	@ApiOperation("新增com.scmt.healthy数据")
	@PostMapping("addPositiveResultRuleList")
	public Result<Object> addPositiveResultRuleList(@RequestBody TPositiveResultsRule tPositiveResultsRule) {
		try {
			boolean res = tPositiveResults.save(tPositiveResultsRule);
			if (res) {
				TPositiveRule tPositiveRule = new TPositiveRule();
				List<TPositiveRule> unitList = tPositiveResultsRule.getRulData();
				if (unitList != null && unitList.size() > 0) {
					for (int i = 0; i < unitList.size(); i++) {
						TPositiveRule basicUnit = unitList.get(i);
						basicUnit.setPositiveResultsId(tPositiveResultsRule.getId());
						itPositiveRuleService.save(basicUnit);
					}
				}
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
	 * @param tPositiveResultsRule 实体
	 * @return 返回更新结果
	 */
	@ApiOperation("更新com.scmt.healthy数据")
	@PostMapping("updatePositiveResultRuleList")
	public Result<Object> updateTPositiveResults(@RequestBody TPositiveResultsRule tPositiveResultsRule){
		if (StringUtils.isBlank(tPositiveResultsRule.getId())) {
			return ResultUtil.error("参数为空，请联系管理员！！");
		}
		try {
			boolean res = tPositiveResults.updateById(tPositiveResultsRule);
			List<TPositiveRule> unitList = tPositiveResultsRule.getRulData();
			//更新和删除
			for (int i = 0; i < unitList.size(); i++) {
				TPositiveRule basicUnit = unitList.get(i);
				basicUnit.setPositiveResultsId(tPositiveResultsRule.getId());
				itPositiveRuleService.saveOrUpdate(basicUnit);
			}

			if (res) {
				return ResultUtil.data(res, "修改成功");
			} else {
				return ResultUtil.data(res, "修改失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultUtil.error("保存异常:" + e.getMessage());
		}
	}

	/**
	 * 功能描述：根据主键来获取数据
	 * @param id 主键
	 * @return 返回获取结果
	 */
	@ApiOperation("根据主键来获取com.scmt.healthy数据")
	@GetMapping("getPositiveResultRuleId")
	public Result<Object> getPositiveResultRuleId(@RequestParam(name = "positiveId")String id){
		if (StringUtils.isBlank(id)) {
			return ResultUtil.error("参数为空，请联系管理员！！");
		}
		try {
			List<TPositiveResultsRule> res = tPositiveResults.getByPositiveId(id);
			if (res != null) {
				return ResultUtil.data(res, "查询成功");
			} else {
				return ResultUtil.data(res, "查询失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultUtil.error("查询异常:" + e.getMessage());
		}
	}

	/**
	 * 功能描述：根据主键来获取数据
	 * @param id 主键
	 * @return 返回获取结果
	 */
	@ApiOperation("根据主键来获取com.scmt.healthy数据")
	@GetMapping("getPositiveResultId")
	public Result<Object> getPositiveResultId(@RequestParam(name = "id")String id){
		if (StringUtils.isBlank(id)) {
			return ResultUtil.error("参数为空，请联系管理员！！");
		}
		try {
			TPositiveResultsRule res = tPositiveResults.getById(id);
			List<TPositiveRule> unitList = itPositiveRuleService.queryBasicUnitListByArchiveId(id);
			res.setRulData(unitList);
			if (res != null) {
				return ResultUtil.data(res, "查询成功");
			} else {
				return ResultUtil.data(res, "查询失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultUtil.error("查询异常:" + e.getMessage());
		}
	}

	/**
	 * 功能描述：根据主键来删除数据
	 * @param ids 主键集合
	 * @return 返回删除结果
	 */
	@ApiOperation("根据主键来删除com.scmt.healthy数据")
	@PostMapping("deletePositiveResultRuleList")
	public Result<Object> deletePositiveResultRuleList(@RequestParam String[] ids){
		if (ids == null || ids.length == 0) {
			return ResultUtil.error("参数为空，请联系管理员！！");
		}
		try {
			boolean res = tPositiveResults.removeByIds(Arrays.asList(ids));
			if (res) {
				return ResultUtil.data(res, "删除成功");
			} else {
				return ResultUtil.data(res, "删除失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultUtil.error("删除异常:" + e.getMessage());
		}
	}

	/**
	 * 功能描述：根据主键来删除数据
	 * @param ids 主键集合
	 * @return 返回删除结果
	 */
	@ApiOperation("根据主键来删除com.scmt.healthy数据")
	@PostMapping("deletePositiveResultRule")
	public Result<Object> deletePositiveResultRule(@RequestParam String ids){
		if (ids == null) {
			return ResultUtil.error("参数为空，请联系管理员！！");
		}
		try {
			boolean res = itPositiveRuleService.removeById(ids);
			if (res) {
				return ResultUtil.data(res, "删除成功");
			} else {
				return ResultUtil.data(res, "删除失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultUtil.error("删除异常:" + e.getMessage());
		}
	}

	/**
	 * 功能描述：根据异常结果匹配阳性规则
	 * @return 返回获取结果
	 */
	@ApiOperation("根据异常结果匹配阳性规则")
	@GetMapping("queryPositiveResultExaminationList")
	public Result<Object> queryPositiveResultExaminationList(@RequestParam(name = "ProjectName")String orderGroupItemProjectName){/*
		if (orderGroupItemProjectName == null || orderGroupItemProjectName.length == 0) {
			return ResultUtil.error("参数为空，请联系管理员！！");
		}*/
		try {
			QueryWrapper<TPositiveResults> queryWrapper = new QueryWrapper();
				List<TPositiveResults> list = tPositiveResultsService.list(queryWrapper);
				if (list != null) {
					return ResultUtil.data(list, "查询成功");
				} else {
					return ResultUtil.data(list, "查询失败");
				}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultUtil.error("查询异常:" + e.getMessage());
		}
	}

	/**
	 * 功能描述：新增com.scmt.healthy数据
	 * @param tPositivePerson 实体
	 * @return 返回新增结果
	 */
	@ApiOperation("新增com.scmt.healthy数据")
	@PostMapping("addPositivePerson")
	public Result<Object> addPositivePerson(@RequestBody TPositivePerson tPositivePerson){
		try {
				List<TPositiveResults> unitList = tPositivePerson.getPositivePersonData();
				if (unitList != null && unitList.size() > 0) {
					for (int i = 0; i < unitList.size(); i++) {
						TPositiveResults basicUnit = unitList.get(2);
						TPositivePerson account = JSONObject.parseObject(String.valueOf(basicUnit),TPositivePerson.class);
						account.setPositiveName(basicUnit.getName());
						account.setPositiveSuggestion(basicUnit.getHealthAdvice());
						account.setHeavy(basicUnit.getDegree());
						boolean save = itPositivePersonService.save(account);
					}

				}
			return ResultUtil.data("保存成功");
		} catch (Exception e) {
			e.printStackTrace();
			return ResultUtil.error("保存异常:" + e.getMessage());
		}
	}


	/**
	 * 功能描述：实现查询
	 * @param searchVo 需要模糊查询的信息
	 * @return 返回获取结果
	 */
	@ApiOperation("查询com.scmt.healthy数据")
	@GetMapping("getPositiveResultList")
	public Result<Object> getPositiveResultList(TPositiveResults  tPositiveResults, SearchVo searchVo){
		try {
			List<TPositiveResults> result =tPositiveResultsService.getPositiveResultList(tPositiveResults, searchVo);
			return ResultUtil.data(result);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultUtil.error("查询异常:" + e.getMessage());
		}
	}


}