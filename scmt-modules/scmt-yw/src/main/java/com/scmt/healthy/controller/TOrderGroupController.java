package com.scmt.healthy.controller;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.scmt.healthy.entity.*;
import com.scmt.healthy.service.ITComboItemService;
import com.scmt.healthy.service.ITComboService;
import com.scmt.healthy.service.ITOrderGroupItemService;
import com.scmt.healthy.service.ITOrderGroupService;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import com.scmt.core.common.utils.ResultUtil;
import com.scmt.core.common.vo.Result;
import com.scmt.core.common.utils.SecurityUtil;
import com.scmt.core.common.annotation.SystemLog;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.scmt.core.common.enums.LogType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author
 **/
@RestController
@Api(tags = " 订单分组数据接口")
@RequestMapping("/scmt/tOrderGroup")
public class TOrderGroupController {
    @Autowired
    private ITOrderGroupService tOrderGroupService;
    @Autowired
    private SecurityUtil securityUtil;
    @Autowired
    private ITOrderGroupItemService itOrderGroupItemService;

    @Autowired
    private ITComboService tComboService;
    @Autowired
    private ITComboItemService comboItemService;

    /**
     * 功能描述：新增订单分组数据
     *
     * @param tOrderGroup 实体
     * @return 返回新增结果
     */
    @SystemLog(description = "新增订单分组数据", type = LogType.OPERATION)
    @ApiOperation("新增订单分组数据")
    @PostMapping("addTOrderGroup")
    public Result<Object> addTOrderGroup(@RequestBody TOrderGroup tOrderGroup) {
        try {
            tOrderGroup.setDelFlag(0);
            tOrderGroup.setCreateId(securityUtil.getCurrUser().getId());
            tOrderGroup.setCreateTime(new Date());
            boolean res = tOrderGroupService.save(tOrderGroup);
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
     *
     * @param tOrderGroup 实体
     * @return 返回更新结果
     */
    @SystemLog(description = "更新订单分组数据", type = LogType.OPERATION)
    @ApiOperation("更新订单分组数据")
    @PostMapping("updateTOrderGroup")
    public Result<Object> updateTOrderGroup(@RequestBody TOrderGroup tOrderGroup) {
        if (StringUtils.isBlank(tOrderGroup.getId())) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            tOrderGroup.setUpdateId(securityUtil.getCurrUser().getId());
            tOrderGroup.setUpdateTime(new Date());
            boolean res = tOrderGroupService.updateById(tOrderGroup);
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
     *
     * @param ids 主键集合
     * @return 返回删除结果
     */
    @ApiOperation("根据主键来删除订单分组数据")
    @SystemLog(description = "根据主键来删除订单分组数据", type = LogType.OPERATION)
    @PostMapping("deleteTOrderGroup")
    public Result<Object> deleteTOrderGroup(@RequestParam String[] ids) {
        if (ids == null || ids.length == 0) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            boolean res = tOrderGroupService.removeByIds(Arrays.asList(ids));
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
     *
     * @param id 主键
     * @return 返回获取结果
     */
    @SystemLog(description = "根据主键来获取订单分组数据", type = LogType.OPERATION)
    @ApiOperation("根据主键来获取订单分组数据")
    @GetMapping("getTOrderGroup")
    public Result<Object> getTOrderGroup(@RequestParam(name = "id") String id) {
        if (StringUtils.isBlank(id)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            TOrderGroup res = tOrderGroupService.getById(id);
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
     * 功能描述：根据订单id来获取分组数据
     *
     * @param groupOrderId 订单id
     * @return 返回获取结果
     */
    @SystemLog(description = "根据订单id来获取分组数据", type = LogType.OPERATION)
    @ApiOperation("根据订单id来获取分组数据")
    @GetMapping("getTOrderGroupByGroupOrderId")
    public Result<Object> getTOrderGroupByGroupOrderId(@RequestParam(name = "groupOrderId") String groupOrderId) {
        if (StringUtils.isBlank(groupOrderId)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            List<TOrderGroup> res = tOrderGroupService.getTOrderGroupByGroupOrderId(groupOrderId);
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
     * 功能描述：根据单位id来获取分组数据
     *
     * @param groupUnitId 单位id
     * @return 返回获取结果
     */
    @SystemLog(description = "根据单位id来获取分组数据", type = LogType.OPERATION)
    @ApiOperation("根据单位id来获取分组数据")
    @GetMapping("getTOrderGroupByGroupUnitId")
    public Result<Object> getTOrderGroupByGroupUnitId(@RequestParam(name = "groupUnitId") String groupUnitId) {
        if (StringUtils.isBlank(groupUnitId)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            List<TOrderGroup> res = tOrderGroupService.getTOrderGroupByGroupUnitId(groupUnitId);
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
     *
     * @return 返回获取结果
     */
    @SystemLog(description = "查询订单分组数据", type = LogType.OPERATION)
    @ApiOperation("查询订单分组数据")
    @GetMapping("queryTOrderGroupList")
    public Result<Object> queryTOrderGroupList(TOrderGroup tOrderGroup) {
        try {
            List<TOrderGroup> result = tOrderGroupService.queryTOrderGroupList(tOrderGroup);
            for (TOrderGroup orderGroup : result) {
                QueryWrapper<TOrderGroupItem> queryWrapper = new QueryWrapper<TOrderGroupItem>();
                queryWrapper.eq("group_id", orderGroup.getId());
                queryWrapper.eq("del_flag", 0);
                queryWrapper.orderByAsc("order_num").orderByAsc("name").orderByAsc("project_type");
                orderGroup.setProjectData(itOrderGroupItemService.list(queryWrapper));
                //根据分组的套餐id 获取危害因素数据
                if(orderGroup!=null && StringUtils.isNotBlank(orderGroup.getComboId())){
                    String[] comboIds = orderGroup.getComboId().split(",");
                    QueryWrapper<TCombo> comboQueryWrapper = new QueryWrapper<TCombo>();
                    comboQueryWrapper.eq("del_flag", '0');
                    comboQueryWrapper.in("id",comboIds);
                    List<TCombo> tComboList = tComboService.list(comboQueryWrapper);//comboIds
                    if(tComboList!=null && tComboList.size()>0){
                        List<String> hazardFactorCodes = new ArrayList<>();
                        List<String> hazardFactorTexts = new ArrayList<>();
                        for(TCombo tCombo : tComboList){
                            if(tCombo!=null && StringUtils.isNotBlank(tCombo.getHazardFactors()) && StringUtils.isNotBlank(tCombo.getHazardFactorsText())){
                                hazardFactorCodes.add(tCombo.getHazardFactors());
                                hazardFactorTexts.add(tCombo.getHazardFactorsText());
                            }
                        }
                        orderGroup.setHazardFactorCodes(hazardFactorCodes);
                        orderGroup.setHazardFactorTexts(hazardFactorTexts);
                    }
                }
            }
            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 根据订单id 集合来获取分组数据
     * @param orderIds 订单id 集合
     * @return
     */
    @SystemLog(description = "根据订单id集合来获取分组数据", type = LogType.OPERATION)
    @ApiOperation("根据订单id集合来获取分组数据")
    @PostMapping("queryTOrderGroupListByOrderId")
    public Result<Object> queryTOrderGroupListByOrderId(@RequestParam String[] orderIds) {
        try {
            if(orderIds == null ||orderIds.length == 0){
                return ResultUtil.error("参数为空，请联系管理员！！");
            }
            QueryWrapper<TOrderGroup> queryWrapper = new QueryWrapper<TOrderGroup>();
            queryWrapper.in("group_order_id", orderIds);
            queryWrapper.eq("del_flag", 0);
            queryWrapper.orderByAsc("create_time");
            queryWrapper.orderByAsc("name");
            List<TOrderGroup> result = tOrderGroupService.list(queryWrapper);

            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：导出数据
     *
     * @param response    请求参数
     * @param tOrderGroup 查询参数
     * @return
     */
    @SystemLog(description = "导出订单分组数据", type = LogType.OPERATION)
    @ApiOperation("导出订单分组数据")
    @PostMapping("/download")
    public void download(HttpServletResponse response, TOrderGroup tOrderGroup) {
        try {
            tOrderGroupService.download(tOrderGroup, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @ApiOperation("查询订单对应的检查项目和危害因素")
    @GetMapping("queryCheckProjectAndHazardFactors")
    public Result<Object> queryCheckProjectAndHazardFactors(String groupOrderId,String physicalType) {
        try {
            if(physicalType.contains("职业体检") || physicalType.contains("放射体检")){
                Map<String, Object> stringObjectMap = tOrderGroupService.queryCheckProjectAndHazardFactors(groupOrderId);
                return ResultUtil.data(stringObjectMap);
            }else{
                Map<String, Object> stringObjectMap = tOrderGroupService.queryCheckProjectAndHazardFactorsHealthy(groupOrderId);
                return ResultUtil.data(stringObjectMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    @ApiOperation("查询订单对应的检查项目和危害因素")
    @GetMapping("queryCheckProjectAndHazardFactorsList")
    public Result<Object> queryCheckProjectAndHazardFactorsList(String groupOrderId) {
        try {
            List<Map<String, Object>> stringObjectMap = tOrderGroupService.queryCheckProjectAndHazardFactorsList(groupOrderId);
            return ResultUtil.data(stringObjectMap);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    @ApiOperation("查询订单对应的检查项目和危害因素(根据套餐查)")
    @GetMapping("queryCheckProjectAndHazardFactorsComboList")
    public Result<Object> queryCheckProjectAndHazardFactorsComboList(String groupOrderId) {
        try {
            //获取订单下的套餐id
            List<String> comboIdList = new ArrayList<>();
            QueryWrapper<TOrderGroup> tOrderGroupQueryWrapper = new QueryWrapper<>();
            tOrderGroupQueryWrapper.eq("del_flag",0);
            tOrderGroupQueryWrapper.eq("group_order_id",groupOrderId);
            tOrderGroupQueryWrapper.exists("select id from t_group_person where group_id = t_order_group.id");
            List<TOrderGroup> result = tOrderGroupService.list(tOrderGroupQueryWrapper);
            for (TOrderGroup orderGroup : result) {
                String comboIds = orderGroup.getComboId();
                if(comboIds != null && comboIds.trim().length() > 0){
                    if(comboIds.indexOf(",") > -1){
                        String[] strings = comboIds.split(",");
                        for (String string : strings){
                            comboIdList.add(string);
                        }
                    }else{
                        comboIdList.add(comboIds);
                    }
                }
            }
            //查询检查项目(根据套餐id查)
            List<Map<String, Object>> stringObjectMap = null;
            if(comboIdList!=null && comboIdList.size() > 0){
                stringObjectMap = tOrderGroupService.queryCheckProjectAndHazardFactorsComboList(comboIdList);
                //获取套餐下项目
                if(stringObjectMap!=null && stringObjectMap.size() > 0){
                    for(Map<String, Object> map : stringObjectMap){
                        String id = "" + map.get("id");
                        if(id!=null && id.trim().length() > 0){
                            QueryWrapper<TComboItem> queryWrapper = new QueryWrapper<>();
                            queryWrapper.eq("del_flag",0);
                            queryWrapper.eq("combo_id", id);
                            queryWrapper.groupBy("portfolio_project_id");
                            queryWrapper.orderByAsc("order_num").orderByAsc("t_portfolio_project.NAME");
                            List<TComboItem> list = comboItemService.listByComboIds(queryWrapper);
                            String projectDataName = "";
                            for(TComboItem tComboItem : list){
                                if(tComboItem!=null && tComboItem.getName()!=null && tComboItem.getName().trim().length() > 0){
                                    if(projectDataName!=null && projectDataName.trim().length() > 0){
                                        projectDataName += "," + tComboItem.getName();
                                    }else{
                                        projectDataName += tComboItem.getName();
                                    }
                                }
                            }
                            if(projectDataName!=null && projectDataName.trim().length() > 0){
                                map.put("projectDataName",projectDataName);
                            }
                        }
                    }
                }
            }
            return ResultUtil.data(stringObjectMap);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    @ApiOperation("查询订单对应的检查结果")
    @GetMapping("queryCheckResultByOrderId")
    public Result<Object> queryCheckResultByOrderId(String groupOrderId) {
        try {
            List<TGroupPerson> data0 = tOrderGroupService.queryCheckResultByOrderId(groupOrderId);
            Map<String, Map<String, List<TGroupPerson>>> data = data0.stream()
                    .collect(Collectors.groupingBy(TGroupPerson::getWorkStateName,Collectors.groupingBy(TGroupPerson::getHazardFactorsName)));
            return ResultUtil.data(data);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    @ApiOperation("查询订单对应的复查结果和")
    @GetMapping("queryReviewResultByOrderId")
    public Result<Object> queryReviewResultByOrderId(String groupOrderId) {
        try {
            List<TGroupPerson> data0 = tOrderGroupService.queryReviewResultByOrderId(groupOrderId);
            Map<String, Map<String, List<TGroupPerson>>> data = data0.stream()
                    .collect(Collectors.groupingBy(TGroupPerson::getWorkStateName,Collectors.groupingBy(TGroupPerson::getHazardFactorsName)));
            return ResultUtil.data(data);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    @ApiOperation("查询订单对应的检查结果和危害因素")
    @GetMapping("queryCheckResultAndHazardFactorsList")
    public Result<Object> queryCheckResultAndHazardFactorsList(String groupOrderId) {
        try {
            List<Map<String, Object>> stringObjectMap = tOrderGroupService.queryCheckResultAndHazardFactorsList(groupOrderId);
            return ResultUtil.data(stringObjectMap);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    @ApiOperation("查询订单对应的复查结果及建议")
    @GetMapping("queryCheckResultReview")
    public Result<Object> queryCheckResultReview(String groupOrderId) {
        try {
            List<Map<String, Object>> stringObjectMap = tOrderGroupService.queryCheckResultReview(groupOrderId);
            return ResultUtil.data(stringObjectMap);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    @ApiOperation("查询订单对应的复查登记时间")
    @GetMapping("queryRegistDateReview")
    public Result<Object> queryRegistDateReview(String groupOrderId) {
        try {
            List<Map<String, Object>> stringObjectMap = tOrderGroupService.queryRegistDateReview(groupOrderId);
            return ResultUtil.data(stringObjectMap);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    @ApiOperation("查询订单对应的登记时间")
    @GetMapping("queryRegistDate")
    public Result<Object> queryRegistDate(String groupOrderId) {
        try {
            List<Map<String, Object>> stringObjectMap = tOrderGroupService.queryRegistDate(groupOrderId);
            return ResultUtil.data(stringObjectMap);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

}
