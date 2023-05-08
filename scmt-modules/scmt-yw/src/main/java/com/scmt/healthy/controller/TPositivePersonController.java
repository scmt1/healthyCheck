package com.scmt.healthy.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.scmt.core.common.utils.ResultUtil;
import com.scmt.core.common.utils.SecurityUtil;
import com.scmt.core.common.vo.Result;
import com.scmt.healthy.entity.TDepartItemResult;
import com.scmt.healthy.entity.TPositivePerson;
import com.scmt.healthy.entity.TPositiveResults;
import com.scmt.healthy.entity.TPositiveResultsRule;
import com.scmt.healthy.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;


/**
 *@author 
 **/
@RestController
@Api(tags =" com.scmt.healthy数据接口")
@RequestMapping("/scmt/tPositivePerson")
public class TPositivePersonController{
	@Autowired
	private ITPositivePersonService tPositivePersonService;
	@Autowired
	private SecurityUtil securityUtil;

	@Autowired
	private ITDepartItemResultService itDepartItemResultService;

	@Autowired
	private ITPositiveRuleService itPositiveRuleService;

	@Autowired
	private ITPositiveResultsService itPositiveResultsService;

	@Autowired
	private ITPositiveResultsRuleService itPositiveResultsRuleService;


	/**
	* 功能描述：新增com.scmt.healthy数据
	* @param tPositivePerson 实体
	* @return 返回新增结果
	*/
	@ApiOperation("新增com.scmt.healthy数据")
	@PostMapping("addTPositivePerson")
	public Result<Object> addTPositivePerson(@RequestBody TPositivePerson tPositivePerson){
		try {
			tPositivePerson.setCreateId(securityUtil.getCurrUser().getId());
			boolean res = tPositivePersonService.save(tPositivePerson);
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
	* @param tPositivePerson 实体
	* @return 返回更新结果
	*/
	@ApiOperation("更新com.scmt.healthy数据")
	@PostMapping("updateTPositivePerson")
	public Result<Object> updateTPositivePerson(@RequestBody TPositivePerson tPositivePerson){
		if (StringUtils.isBlank(tPositivePerson.getId())) {
			return ResultUtil.error("参数为空，请联系管理员！！");
		}
		try {
			tPositivePerson.setUpdateId(securityUtil.getCurrUser().getId());
			boolean res = tPositivePersonService.updateById(tPositivePerson);
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
	@PostMapping("deleteTPositivePerson")
	public Result<Object> deleteTPositivePerson(@RequestParam String[] ids){
		if (ids == null || ids.length == 0) {
			return ResultUtil.error("参数为空，请联系管理员！！");
		}
		try {
			boolean res = tPositivePersonService.removeByIds(Arrays.asList(ids));
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
	@GetMapping("getTPositivePerson")
	public Result<Object> getTPositivePerson(@RequestParam(name = "id")String id){
		if (StringUtils.isBlank(id)) {
			return ResultUtil.error("参数为空，请联系管理员！！");
		}
		try {
			TPositivePerson res = tPositivePersonService.getById(id);
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
	 * @param personId 主键
	 * @return 返回获取结果
	 */
	@ApiOperation("根据主键来获取com.scmt.healthy数据")
	@GetMapping("getTPositivePersonId")
	public Result<Object> getTPositivePersonId(@RequestParam(name = "personId")String personId,@RequestParam(name = "physicalType")String physicalType){
		if (StringUtils.isBlank(physicalType)){
			return ResultUtil.error("参数为空，请联系管理员！！");
		}
		if (StringUtils.isBlank(personId)) {
			return ResultUtil.error("参数为空，请联系管理员！！");
		}
		try {
			List<TPositivePerson> res = tPositivePersonService.getByPersonId(personId);
			if (res == null || res.size() == 0) {
				res = new ArrayList<>();
				QueryWrapper<TDepartItemResult> queryWrapperResult = new QueryWrapper();
				queryWrapperResult.eq("person_id", personId);
				queryWrapperResult.eq("positive", 1);
				List<TDepartItemResult> listResult = itDepartItemResultService.list(queryWrapperResult);

				List<TPositiveResults> listPositive = itPositiveResultsService.list();
				List<TPositiveResultsRule> list = itPositiveResultsRuleService.getAllPositiceRuleList();
				//筛选出所有的  满足所有  的阳性规则
				List<TPositiveResultsRule> listAll = list.stream().filter(aa -> aa.getJudgmentCondition().equals("满足全部")).collect(Collectors.toList());
				//筛选出所有的  满足一个  的阳性规则
				List<TPositiveResultsRule> listOne = list.stream().filter(aa -> aa.getJudgmentCondition().equals("满足一个")).collect(Collectors.toList());
				//满足所有的阳性规则根据 规则id 分组
				Map<String, List<TPositiveResultsRule>> mapAll = listAll.stream().collect(Collectors.groupingBy(TPositiveResultsRule::getPositiveId));
				//满足一个的阳性规则根据 规则id 分组
				Map<String, List<TPositiveResultsRule>> mapOne = listOne.stream().collect(Collectors.groupingBy(TPositiveResultsRule::getPositiveId));
				if (listPositive != null && listResult != null && listPositive.size() > 0 && listResult.size() > 0) {
					if(mapAll!=null && mapAll.size()>0){
						Set<String> keys=mapAll.keySet();
						for (String key : keys) {
							List<TPositiveResultsRule> tPositiveResultsRules = mapAll.get(key);
							if(tPositiveResultsRules!=null && tPositiveResultsRules.size()>0){
								Boolean isMatch= true;//是否匹配上
								//匹配上的阳性结果集合
								List<TDepartItemResult> results = new ArrayList<>();
								//匹配上的阳性结果id集合
								List<String> resultIds = new ArrayList<>();
								/**
								 * 循环匹配阳性规则中的项目名称
								 */
								for (int i = 0; i < tPositiveResultsRules.size(); i++) {
									TPositiveResultsRule projectRules = tPositiveResultsRules.get(i);
									List<TDepartItemResult> tDepartItemResultStream = listResult.stream().filter(aa -> aa.getOrderGroupItemProjectName().equals(projectRules.getProjectNames())).collect(Collectors.toList());
									if(tDepartItemResultStream==null||tDepartItemResultStream.size()==0){
										//项目名称未匹配上
										isMatch = false;
										break;
									}
									isMatch = getTPositiveResults(tDepartItemResultStream.get(0).getResult(), projectRules);
									if(!isMatch){
										break;
									}
									results.add(tDepartItemResultStream.get(0));
									resultIds.add(tDepartItemResultStream.get(0).getId());
								}
								/**
								 * 如果阳性规则中的项目名称匹配的上就验证 阳性结果规则
								 */
								if(isMatch && resultIds!=null && resultIds.size()>0 ){
									List<TPositiveResults> collect = listPositive.stream().filter(aa -> aa.getId().equals(key)).collect(Collectors.toList());
									if(collect!=null && collect.size()>0){
										//添加阳性结果建议返回值
										TPositivePerson tPositivePerson = new TPositivePerson();
										tPositivePerson.setId(UUID.randomUUID().toString().replaceAll("-", ""));
										tPositivePerson.setPersonId(personId);
										tPositivePerson.setPositiveName(collect.get(0).getName());
										if (physicalType=="健康体检"){
											tPositivePerson.setPositiveSuggestion(collect.get(0).getHealthAdvice());
										}else if (physicalType == "职业体检"||physicalType == "放射体检"){
											tPositivePerson.setPositiveSuggestion(collect.get(0).getAdvise());
										}else {
											tPositivePerson.setPositiveSuggestion(collect.get(0).getHealthAdvice());
										}
										tPositivePerson.setConclusionType("主要健康问题");
										tPositivePerson.setHeavy("D");
										res.add(tPositivePerson);

										//删除已经匹配成功的阳性结果
										listResult.removeIf(bb->resultIds.contains(bb.getId()));
									}


								}

							}

						}
					}
					if(mapOne!=null && mapOne.size()>0){
						Set<String> keys=mapOne.keySet();
						for (String key : keys) {
							List<TPositiveResultsRule> tPositiveResultsRules = mapOne.get(key);
							if(tPositiveResultsRules!=null && tPositiveResultsRules.size()>0){
								Boolean isMatch= true;//是否匹配上
								//匹配上的阳性结果集合
								List<TDepartItemResult> results = new ArrayList<>();
								//匹配上的阳性结果id集合
								List<String> resultIds = new ArrayList<>();
								/**
								 * 循环匹配阳性规则中的项目名称
								 */
								for (int i = 0; i < tPositiveResultsRules.size(); i++) {
									TPositiveResultsRule projectRules = tPositiveResultsRules.get(i);
									List<TDepartItemResult> tDepartItemResultStream = listResult.stream().filter(aa -> aa.getOrderGroupItemProjectName().equals(projectRules.getProjectNames())).collect(Collectors.toList());
									if(tDepartItemResultStream==null||tDepartItemResultStream.size()==0){
										//项目名称未匹配上
										isMatch =false;
										continue;
									}
									isMatch = getTPositiveResults(tDepartItemResultStream.get(0).getResult(), projectRules);
									if(isMatch){
										results.add(tDepartItemResultStream.get(0));
										resultIds.add(tDepartItemResultStream.get(0).getId());
										break;
									}

								}
								/**
								 * 如果阳性规则中的项目名称匹配的上就验证 阳性结果规则
								 */
								if(isMatch && resultIds!=null && resultIds.size()>0 ){
									List<TPositiveResults> collect = listPositive.stream().filter(aa -> aa.getId().equals(key)).collect(Collectors.toList());
									if(collect!=null && collect.size()>0){
										//添加阳性结果建议返回值
										TPositivePerson tPositivePerson = new TPositivePerson();
										tPositivePerson.setId(UUID.randomUUID().toString().replaceAll("-", ""));
										tPositivePerson.setPersonId(personId);
										tPositivePerson.setPositiveName(collect.get(0).getName());
										if (physicalType=="健康体检"){
											tPositivePerson.setPositiveSuggestion(collect.get(0).getHealthAdvice());
										}else if (physicalType == "职业体检"||physicalType == "放射体检"){
											tPositivePerson.setPositiveSuggestion(collect.get(0).getAdvise());
										}else {
											tPositivePerson.setPositiveSuggestion(collect.get(0).getHealthAdvice());
										}
										tPositivePerson.setConclusionType("主要健康问题");
										tPositivePerson.setHeavy("D");
										res.add(tPositivePerson);

										//删除已经匹配成功的阳性结果
										listResult.removeIf(bb->resultIds.contains(bb.getId()));
									}


								}

							}

						}
					}

					for (int i = 0; i < listResult.size(); i++) {
						TDepartItemResult tDepartItemResult = listResult.get(i);

						String orderGroupItemProjectName = tDepartItemResult.getOrderGroupItemProjectName().split("\\[")[0];
						String crisisDegree = tDepartItemResult.getCrisisDegree();
						List<TPositiveResults> collect = new ArrayList<>();
						if (StringUtils.isNotBlank(crisisDegree)) {
							if (crisisDegree.contains("高于")) {
								collect = listPositive.stream().filter(
										aa -> StringUtils.isNotBlank(aa.getName()) && (aa.getName().contains(orderGroupItemProjectName + "升高")
												|| aa.getName().contains(orderGroupItemProjectName + "增高")
												|| aa.getName().contains(orderGroupItemProjectName + "增多")
												|| aa.getName().contains(orderGroupItemProjectName + "增加")
												|| aa.getName().contains(orderGroupItemProjectName + "增大")
												|| aa.getName().contains(orderGroupItemProjectName + "偏高"))
								).collect(Collectors.toList());
								if (collect.size()>1){
									collect = collect.stream().filter(
											aa -> StringUtils.isNotBlank(aa.getName()) && (aa.getName().equals(orderGroupItemProjectName + "升高")
													|| aa.getName().equals(orderGroupItemProjectName + "增高")
													|| aa.getName().equals(orderGroupItemProjectName + "增多")
													|| aa.getName().equals(orderGroupItemProjectName + "增加")
													|| aa.getName().equals(orderGroupItemProjectName + "增大")
													|| aa.getName().equals(orderGroupItemProjectName + "偏高"))
									).collect(Collectors.toList());
								}
							} else if (crisisDegree.contains("低于")) {
								collect = listPositive.stream().filter(
										aa -> StringUtils.isNotBlank(aa.getName()) && (aa.getName().contains(orderGroupItemProjectName + "降低")
												|| aa.getName().contains(orderGroupItemProjectName + "减少")
												|| aa.getName().contains(orderGroupItemProjectName + "偏低"))
								).collect(Collectors.toList());
								if (collect.size()>1){
									collect = collect.stream().filter(
											aa -> StringUtils.isNotBlank(aa.getName()) && (aa.getName().equals(orderGroupItemProjectName + "降低")
													|| aa.getName().equals(orderGroupItemProjectName + "减少")
													|| aa.getName().equals(orderGroupItemProjectName + "偏低"))
									).collect(Collectors.toList());
								}
							} else {
								collect = listPositive.stream().filter(
										aa -> StringUtils.isNotBlank(aa.getName()) && aa.getName().contains(orderGroupItemProjectName)
								).collect(Collectors.toList());
								if (collect.size()>1){
									collect = collect.stream().filter(
											aa -> StringUtils.isNotBlank(aa.getName()) && aa.getName().equals(orderGroupItemProjectName)
									).collect(Collectors.toList());
								}
							}
						}
						if (collect != null && collect.size() > 0) {
							TPositivePerson tPositivePerson = new TPositivePerson();
							tPositivePerson.setId(UUID.randomUUID().toString().replaceAll("-", ""));
							tPositivePerson.setPersonId(personId);
							tPositivePerson.setPositiveName(collect.get(0).getName());
							if (physicalType=="健康体检"){
								tPositivePerson.setPositiveSuggestion(collect.get(0).getHealthAdvice());
							}else if (physicalType == "职业体检"||physicalType == "放射体检"){
								tPositivePerson.setPositiveSuggestion(collect.get(0).getAdvise());
							}else {
								tPositivePerson.setPositiveSuggestion(collect.get(0).getHealthAdvice());
							}
							tPositivePerson.setConclusionType("主要健康问题");
							tPositivePerson.setHeavy("D");
							res.add(tPositivePerson);
						}
					}
				}
			}
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
	 * 匹配阳性结果规则 （判断规则）
	 * @return
	 */
	private Boolean getTPositiveResults(String result,TPositiveResultsRule positive) {
		Boolean  res = false;
		if(positive !=null && StringUtils.isNotBlank(positive.getTypes()) && StringUtils.isNotBlank(result) && StringUtils.isNotBlank(positive.getRegularValues())){
			String types = positive.getTypes();
			if (types.equals("EQ")){
				res =positive.getRegularValues().equals(result);
			}else if (types.equals("LIKE")){
				res =result.contains(positive.getRegularValues());
			}else if (types.equals("NEQ")){
				res =!positive.getRegularValues().equals(result);

			}else if (types.equals("NLIKE")){
				res =!result.contains(positive.getRegularValues());

			}else if (types.equals("GTE")){
				res = Double.parseDouble(positive.getRegularValues()) <= Double.parseDouble(result);
			}else if (types.equals("GT")){
				res = Double.parseDouble(positive.getRegularValues()) < Double.parseDouble(result);

			}else if (types.equals("LT")){
				res = Double.parseDouble(positive.getRegularValues()) > Double.parseDouble(result);

			}else if (types.equals("LTE")){
				res = Double.parseDouble(positive.getRegularValues()) >= Double.parseDouble(result);
			}
		}
		return res;
	}
}