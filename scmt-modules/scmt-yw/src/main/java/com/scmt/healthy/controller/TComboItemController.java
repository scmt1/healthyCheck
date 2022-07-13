package com.scmt.healthy.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.scmt.healthy.service.ITComboItemService;

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
import com.scmt.healthy.entity.TComboItem;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.scmt.core.common.enums.LogType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author
 **/
@RestController
@Api(tags = " 套餐项目数据接口")
@RequestMapping("/scmt/tComboItem")
public class TComboItemController {
    @Autowired
    private ITComboItemService tComboItemService;
    @Autowired
    private SecurityUtil securityUtil;

    /**
     * 功能描述：新增套餐项目数据
     *
     * @param form 实体
     * @return 返回新增结果
     */
    @SystemLog(description = "新增套餐项目数据", type = LogType.OPERATION)
    @ApiOperation("新增套餐项目数据")
    @PostMapping("addTComboItem")
    public Result<Object> addTComboItem(@RequestBody String form) {
        try {

            if (!StringUtils.isNotBlank(form)) {
                return ResultUtil.data("参数为空，请联系管理员！");
            }

            JSONObject jsonObject = JSON.parseObject(form);
            String formStr = jsonObject.getString("form");
            TComboItem comboItem = JSON.parseObject(formStr, TComboItem.class);
            comboItem.setDepartmentId(securityUtil.getCurrUser().getDepartmentId());
            comboItem.setCreateId(securityUtil.getCurrUser().getId());
            comboItem.setCreateTime(new Date());
            boolean res = tComboItemService.save(comboItem);
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
     * @param form 实体
     * @return 返回更新结果
     */
    @SystemLog(description = "更新套餐项目数据", type = LogType.OPERATION)
    @ApiOperation("更新套餐项目数据")
    @PostMapping("updateTComboItem")
    public Result<Object> updateTComboItem(@RequestBody String form) {
        try {
            if (!StringUtils.isNotBlank(form)) {
                return ResultUtil.data("参数为空，请联系管理员！");
            }

            JSONObject jsonObject = JSON.parseObject(form);
            String formStr = jsonObject.getString("form");
            TComboItem comboItem = JSON.parseObject(formStr, TComboItem.class);

            if (!StringUtils.isNotBlank(comboItem.getId())) {
                return ResultUtil.data("参数为空，请联系管理员！");
            }

            comboItem.setDepartmentId(securityUtil.getCurrUser().getDepartmentId());
            boolean res = tComboItemService.updateById(comboItem);
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
    @ApiOperation("根据主键来删除套餐项目数据")
    @SystemLog(description = "根据主键来删除套餐项目数据", type = LogType.OPERATION)
    @PostMapping("deleteTComboItem")
    public Result<Object> deleteTComboItem(@RequestParam String[] ids) {
        if (ids == null || ids.length == 0) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            boolean res = tComboItemService.removeByIds(Arrays.asList(ids));
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
    @SystemLog(description = "根据主键来获取套餐项目数据", type = LogType.OPERATION)
    @ApiOperation("根据主键来获取套餐项目数据")
    @GetMapping("getTComboItem")
    public Result<Object> getTComboItem(@RequestParam(name = "id") String id) {
        if (StringUtils.isBlank(id)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            TComboItem res = tComboItemService.getById(id);
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
     * @param searchVo 需要模糊查询的信息
     * @param pageVo   分页参数
     * @return 返回获取结果
     */
    @SystemLog(description = "分页查询套餐项目数据", type = LogType.OPERATION)
    @ApiOperation("分页查询套餐项目数据")
    @GetMapping("queryTComboItemList")
    public Result<Object> queryTComboItemList(TComboItem tComboItem, SearchVo searchVo, PageVo pageVo) {
        try {
            IPage<TComboItem> result = tComboItemService.queryTComboItemListByPage(tComboItem, searchVo, pageVo);
            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：导出数据
     *
     * @param response   请求参数
     * @param tComboItem 查询参数
     * @return
     */
    @SystemLog(description = "导出套餐项目数据", type = LogType.OPERATION)
    @ApiOperation("导出套餐项目数据")
    @PostMapping("/download")
    public void download(HttpServletResponse response, TComboItem tComboItem) {
        try {
            tComboItemService.download(tComboItem, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 功能描述：根据套餐id查询套餐项目
     *
     * @param comboId 请求参数
     * @return
     */
    @SystemLog(description = "根据套餐id查询套餐项目", type = LogType.OPERATION)
    @ApiOperation("根据套餐id查询套餐项目")
    @GetMapping("getComboItemByComboId")
    public Result<Object> getComboItemByComboId(String comboId) {
        try {
            if (StringUtils.isBlank(comboId)) {
                return ResultUtil.data(new ArrayList<>());
            }
            String[] split = comboId.split(",");
            QueryWrapper<TComboItem> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("combo_id", Arrays.asList(split));
            queryWrapper.groupBy("portfolio_project_id");
            queryWrapper.orderByAsc("name");
            List<TComboItem> list = tComboItemService.listByComboIds(queryWrapper);
            /*筛掉重复的ALT检查项目*/
            List<TComboItem> list1 = list.stream().filter(ii -> ii.getName().contains("血清ALT")).collect(Collectors.toList());
            List<TComboItem> list2 = list.stream().filter(ii -> ii.getName().contains("肝功")).collect(Collectors.toList());
            if(list1!=null && list2!=null && list1.size()>0 && list2.size()>0){//既有血清ALT又有肝功项目，去掉血清ALT项目
                list = list.stream().filter(ii -> !ii.getName().contains("血清ALT")).collect(Collectors.toList());
            }
            return ResultUtil.data(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询失败！");
        }
    }
}
