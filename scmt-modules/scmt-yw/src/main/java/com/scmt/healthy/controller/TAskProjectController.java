package com.scmt.healthy.controller;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.scmt.core.common.annotation.SystemLog;
import com.scmt.core.common.enums.LogType;
import com.scmt.core.common.utils.ResultUtil;
import com.scmt.core.common.utils.SecurityUtil;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.Result;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.entity.TAskProject;
import com.scmt.healthy.entity.TAskProject;
import com.scmt.healthy.service.ITAskProjectService;
import com.scmt.healthy.service.ITAskProjectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author
 **/
@RestController
@Api(tags = "问诊科目数据接口")
@RequestMapping("/scmt/tAskProject")
public class TAskProjectController {
    @Autowired
    private ITAskProjectService itAskProjectService;
    @Autowired
    private SecurityUtil securityUtil;

    /**
     * 功能描述：新增问诊科目
     *
     * @param tAskProject 实体
     * @return 返回新增结果
     */
    @SystemLog(description = "新增问诊科目数据", type = LogType.OPERATION)
    @ApiOperation("新增问诊科目数据")
    @PostMapping("addTAskProject")
    public Result<Object> addTAskProject(@RequestBody TAskProject tAskProject) {
        try {
            tAskProject.setCreateId(securityUtil.getCurrUser().getId());
            tAskProject.setCreateTime(new Date());
            tAskProject.setDelFlag(0);
            boolean save = itAskProjectService.save(tAskProject);
            if (save) {
                return ResultUtil.data("保存成功");
            } else {
                return ResultUtil.error("保存失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("保存异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：更新数据
     *
     * @param tAskProject 实体
     * @return 返回更新结果
     */
    @SystemLog(description = "更新问诊科目数据", type = LogType.OPERATION)
    @ApiOperation("更新问诊科目数据")
    @PostMapping("updateTAskProject")
    public Result<Object> updateTAskProject(@RequestBody TAskProject tAskProject) {
        if (StringUtils.isBlank(tAskProject.getId())) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            tAskProject.setUpdateId(securityUtil.getCurrUser().getId());
            tAskProject.setUpdateTime(new Date());
            boolean save = itAskProjectService.updateById(tAskProject);
            if (save) {
                return ResultUtil.data("保存成功");
            } else {
                return ResultUtil.error("保存失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("修改异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：根据主键来删除数据
     *
     * @param ids 主键集合
     * @return 返回删除结果
     */
    @ApiOperation("根据主键来删除问诊科目数据")
    @SystemLog(description = "根据主键来删除问诊科目数据", type = LogType.OPERATION)
    @PostMapping("deleteTAskProject")
    public Result<Object> deleteTAskProject(@RequestParam String[] ids) {
        if (ids == null || ids.length == 0) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            boolean res = itAskProjectService.removeByIds(Arrays.asList(ids));
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
    @SystemLog(description = "根据主键来获取问诊科目数据", type = LogType.OPERATION)
    @ApiOperation("根据主键来获取问诊科目数据")
    @GetMapping("getTAskProject")
    public Result<Object> getTAskProject(@RequestParam(name = "id") String id) {
        if (StringUtils.isBlank(id)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            TAskProject res = itAskProjectService.getById(id);
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
     * 功能描述：根据危害因素获取
     *
     * @param hazardFactorId 主键
     * @return 返回获取结果
     */
    @SystemLog(description = "根据危害因素获取问诊科目数据", type = LogType.OPERATION)
    @ApiOperation("根据危害因素获取问诊科目数据")
    @GetMapping("getTAskProjectByHazardId")
    public Result<Object> getTAskProjectByHazardId(String hazardFactorId) {
        if (StringUtils.isBlank(hazardFactorId)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            QueryWrapper<TAskProject> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("hazard_factor_id", hazardFactorId);
            queryWrapper.eq("del_flag", 0);
            TAskProject one = itAskProjectService.getOne(queryWrapper);
            return ResultUtil.data(one);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：根据个人危害因素匹配问诊科目
     *
     * @param hazardFactorIds 主键
     * @return 返回获取结果
     */
    @SystemLog(description = "根据个人危害因素匹配问诊科目", type = LogType.OPERATION)
    @ApiOperation("根据个人危害因素匹配问诊科目")
    @GetMapping("getAskProjectByFactor")
    public Result<Object> getAskProjectByFactor(String hazardFactorIds) {
        if (StringUtils.isBlank(hazardFactorIds)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            String[] split = hazardFactorIds.split("\\|");
            List<String> strings = Arrays.asList(split);

            List<TAskProject> data = new ArrayList<>();

            QueryWrapper<TAskProject> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("del_flag", 0);
            List<TAskProject> list = itAskProjectService.list(queryWrapper);

            for (String str : strings) {
                TAskProject tAskProject = list.stream().filter(i -> str.equals(i.getHazardFactorId())).findFirst().orElse(null);
                if (tAskProject != null) {
                    data.add(tAskProject);
                }
            }
            return ResultUtil.data(data);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }
}
