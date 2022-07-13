package com.scmt.healthy.controller;

import java.util.Arrays;
import java.sql.Timestamp;
import java.util.Date;
import javax.servlet.http.HttpServletResponse;

import com.scmt.core.common.utils.ResultUtil;
import com.scmt.core.common.utils.SecurityUtil;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.Result;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.core.entity.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.scmt.healthy.entity.RelationProjectRules;
import com.scmt.healthy.service.IRelationProjectRulesService;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author
 **/
@RestController
@Api(tags = " 项目规则数据接口")
@RequestMapping("/scmt/relationProjectRules")
public class RelationProjectRulesController {
    @Autowired
    private IRelationProjectRulesService relationProjectRulesService;
    @Autowired
    private SecurityUtil securityUtil;

    /**
     * 功能描述：新增项目规则数据
     *
     * @param relationProjectRules 实体
     * @return 返回新增结果
     */
    @ApiOperation("新增项目规则数据")
    @PostMapping("addRelationProjectRules")
    public Result<Object> addRelationProjectRules(@RequestBody RelationProjectRules relationProjectRules) {
        try {
            User currUser = securityUtil.getCurrUser();
            relationProjectRules.setCreateId(currUser.getId());
            relationProjectRules.setCreateTime(new Date());
            relationProjectRules.setDepartmentId(currUser.getDepartmentId());
            boolean res = relationProjectRulesService.save(relationProjectRules);
            if (res) {
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
     *
     * @param ids 主键集合
     * @return 返回删除结果
     */
    @ApiOperation("根据主键来删除项目规则数据")
    @PostMapping("deleteRelationProjectRules")
    public Result<Object> deleteRelationProjectRules(@RequestParam String[] ids) {
        if (ids == null || ids.length == 0) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            boolean res = relationProjectRulesService.removeByIds(Arrays.asList(ids));
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
    @ApiOperation("根据主键来获取项目规则数据")
    @GetMapping("getRelationProjectRules")
    public Result<Object> getRelationProjectRules(@RequestParam(name = "id") String id) {
        if (StringUtils.isBlank(id)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            RelationProjectRules res = relationProjectRulesService.getById(id);
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
     *
     * @param relationProjectRules 需要模糊查询的信息
     * @param searchVo             查询参数
     * @param pageVo               分页参数
     * @return 返回获取结果
     */
    @ApiOperation("分页查询项目规则数据")
    @GetMapping("queryRelationProjectRulesList")
    public Result<Object> queryRelationProjectRulesList(RelationProjectRules relationProjectRules, SearchVo searchVo, PageVo pageVo) {
        Long time = System.currentTimeMillis();
        try {
            return relationProjectRulesService.queryRelationProjectRulesListByPage(relationProjectRules, searchVo, pageVo);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：更新数据
     *
     * @param relationProjectRules 实体
     * @return 返回更新结果
     */
    @ApiOperation("更新项目规则数据")
    @PostMapping("updateRelationProjectRules")
    public Result<Object> updateRelationProjectRules(@RequestBody RelationProjectRules relationProjectRules) {
        if (StringUtils.isBlank(relationProjectRules.getId())) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            boolean res = relationProjectRulesService.updateById(relationProjectRules);
            if (res) {
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
     *
     * @param response             请求参数
     * @param relationProjectRules 查询参数
     * @return
     */
    @ApiOperation("导出项目规则数据")
    @PostMapping("/download")
    public void download(HttpServletResponse response, RelationProjectRules relationProjectRules) {
        try {
            relationProjectRulesService.download(relationProjectRules, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
