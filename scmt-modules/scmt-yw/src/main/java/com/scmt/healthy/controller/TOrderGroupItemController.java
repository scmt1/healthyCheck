package com.scmt.healthy.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.scmt.healthy.entity.TDepartResult;
import com.scmt.healthy.entity.TGroupUnit;
import com.scmt.healthy.entity.TOrderGroupItemProject;
import com.scmt.healthy.service.ITDepartResultService;
import com.scmt.healthy.service.ITOrderGroupItemProjectService;
import com.scmt.healthy.service.ITOrderGroupItemService;

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
import com.scmt.healthy.entity.TOrderGroupItem;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.scmt.core.common.enums.LogType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author
 **/
@RestController
@Api(tags = " 分组项目数据接口")
@RequestMapping("/scmt/tOrderGroupItem")
public class TOrderGroupItemController {
    @Autowired
    private ITOrderGroupItemService tOrderGroupItemService;
    @Autowired
    private ITDepartResultService departResultService;
    @Autowired
    private ITOrderGroupItemProjectService tOrderGroupItemProjectService;
    @Autowired
    private SecurityUtil securityUtil;

    /**
     * 功能描述：新增分组项目数据
     *
     * @param tOrderGroupItem 实体
     * @return 返回新增结果
     */
    @SystemLog(description = "新增分组项目数据", type = LogType.OPERATION)
    @ApiOperation("新增分组项目数据")
    @PostMapping("addTOrderGroupItem")
    public Result<Object> addTOrderGroupItem(@RequestBody TOrderGroupItem tOrderGroupItem) {
        try {
            tOrderGroupItem.setDelFlag(0);
            tOrderGroupItem.setCreateId(securityUtil.getCurrUser().getId());
            tOrderGroupItem.setCreateTime(new Date());
            boolean res = tOrderGroupItemService.save(tOrderGroupItem);
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
     * @param tOrderGroupItem 实体
     * @return 返回更新结果
     */
    @SystemLog(description = "更新分组项目数据", type = LogType.OPERATION)
    @ApiOperation("更新分组项目数据")
    @PostMapping("updateTOrderGroupItem")
    public Result<Object> updateTOrderGroupItem(@RequestBody TOrderGroupItem tOrderGroupItem) {
        if (StringUtils.isBlank(tOrderGroupItem.getId())) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            tOrderGroupItem.setUpdateId(securityUtil.getCurrUser().getId());
            tOrderGroupItem.setUpdateTime(new Date());
            boolean res = tOrderGroupItemService.updateById(tOrderGroupItem);
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
    @ApiOperation("根据主键来删除分组项目数据")
    @SystemLog(description = "根据主键来删除分组项目数据", type = LogType.OPERATION)
    @PostMapping("deleteTOrderGroupItem")
    public Result<Object> deleteTOrderGroupItem(@RequestParam String[] ids) {
        if (ids == null || ids.length == 0) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            boolean res = tOrderGroupItemService.removeByIds(Arrays.asList(ids));
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
    @SystemLog(description = "根据主键来获取分组项目数据", type = LogType.OPERATION)
    @ApiOperation("根据主键来获取分组项目数据")
    @GetMapping("getTOrderGroupItem")
    public Result<Object> getTOrderGroupItem(@RequestParam(name = "id") String id) {
        if (StringUtils.isBlank(id)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            TOrderGroupItem res = tOrderGroupItemService.getById(id);
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
     * 功能描述：根据主键来获取数据
     *
     * @param id 主键
     * @return 返回获取结果
     */
    @SystemLog(description = "根据主键来删除分组项目数据", type = LogType.OPERATION)
    @ApiOperation("根据主键来来删除分组项目数据")
    @GetMapping("deleteGroupItem")
    public Result<Object> deleteGroupItem(@RequestParam(name = "id") String id) {
        if (StringUtils.isBlank(id)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            TOrderGroupItem res = tOrderGroupItemService.getById(id);
            if (res != null) {
                QueryWrapper<TDepartResult> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("del_flag", 0);
                queryWrapper.eq("group_item_id", id);
                Integer count =  departResultService.count(queryWrapper);
                //List<TDepartResult> departResult = departResultService.list(queryWrapper);
                if(count > 0){
                    res.setItemStatus(2);
                    return ResultUtil.data(res, "该项目已有检查结果，不能删除");
                }else{
                    res.setItemStatus(1);
                    QueryWrapper<TOrderGroupItem> itemWrapper = new QueryWrapper<>();
                    itemWrapper.eq("del_flag", 0);
                    itemWrapper.eq("id", id);
                    TOrderGroupItem tOrderGroupItem = new TOrderGroupItem();
                    tOrderGroupItem.setDelFlag(1);
                    Boolean flag = tOrderGroupItemService.update(tOrderGroupItem,itemWrapper);
                    if(flag){
                        QueryWrapper<TOrderGroupItemProject> itemProjectWrapper = new QueryWrapper<>();
                        itemProjectWrapper.eq("del_flag", 0);
                        itemProjectWrapper.eq("t_order_group_item_id", id);
                        TOrderGroupItemProject tOrderGroupItemProject = new TOrderGroupItemProject();
                        tOrderGroupItemProject.setDelFlag(1);
                        tOrderGroupItemProjectService.update(tOrderGroupItemProject,itemProjectWrapper);
                    }
                    return ResultUtil.data(res, "删除成功");
                }
            } else {
                res.setItemStatus(0);
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
    @SystemLog(description = "分页查询分组项目数据", type = LogType.OPERATION)
    @ApiOperation("分页查询分组项目数据")
    @GetMapping("queryTOrderGroupItemList")
    public Result<Object> queryTOrderGroupItemList(TOrderGroupItem tOrderGroupItem) {
        try {
            QueryWrapper<TOrderGroupItem> itemQueryWrapper = new QueryWrapper<>();
            itemQueryWrapper.eq("group_id", tOrderGroupItem.getGroupId());
            itemQueryWrapper.eq("del_flag",0);
            itemQueryWrapper.orderByAsc("order_num").orderByAsc("name");
            List<TOrderGroupItem> list = tOrderGroupItemService.list(itemQueryWrapper);
            return ResultUtil.data(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：导出数据
     *
     * @param response        请求参数
     * @param tOrderGroupItem 查询参数
     * @return
     */
    @SystemLog(description = "导出分组项目数据", type = LogType.OPERATION)
    @ApiOperation("导出分组项目数据")
    @PostMapping("/download")
    public void download(HttpServletResponse response, TOrderGroupItem tOrderGroupItem) {
        try {
            tOrderGroupItemService.download(tOrderGroupItem, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 功能描述：查询未体检项目数据
     *
     * @param groupOrderId 订单id
     * @return 返回获取结果
     */
    @SystemLog(description = "查询订单体检项目数据", type = LogType.OPERATION)
    @ApiOperation("查询订单体检项目数据")
    @GetMapping("queryOrderGroupItemList")
    public Result<Object> queryOrderGroupItemProjectList(@RequestParam(name = "groupOrderId") String groupOrderId, @RequestParam(name = "groupId") String groupId) {
        try {
            List<TOrderGroupItem> result = tOrderGroupItemService.queryOrderGroupItemList(groupOrderId, groupId);
            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }
}
