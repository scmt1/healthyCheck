package com.scmt.healthy.controller;

import java.util.Arrays;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.scmt.core.common.redis.RedisTemplateHelper;
import com.scmt.core.common.utils.ResultUtil;
import com.scmt.core.common.utils.SecurityUtil;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.Result;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.core.entity.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.scmt.healthy.entity.RelationProjectReference;
import com.scmt.healthy.service.IRelationProjectReferenceService;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author
 **/
@RestController
@Api(tags = " 项目参考值数据接口")
@RequestMapping("/scmt/relationProjectReference")
public class RelationProjectReferenceController {
    @Autowired
    private IRelationProjectReferenceService relationProjectReferenceService;
    @Autowired
    private SecurityUtil securityUtil;
    @Autowired
    private RedisTemplateHelper redisTemplate;
    /**
     * 功能描述：新增项目参考值数据
     *
     * @param relationProjectReference 实体
     * @return 返回新增结果
     */
    @ApiOperation("新增项目参考值数据")
    @PostMapping("addRelationProjectReference")
    public Result<Object> addRelationProjectReference(@RequestBody RelationProjectReference relationProjectReference) {
        try {
            User currUser = securityUtil.getCurrUser();
            relationProjectReference.setCreateId(currUser.getId());
            relationProjectReference.setCreateTime(new Date());
            relationProjectReference.setDepartmentId(currUser.getDepartmentId());
            boolean res = relationProjectReferenceService.save(relationProjectReference);
            if (res) {
                redisTemplate.delete("permission::relationProjectReference:" + securityUtil.getCurrUser().getId());
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
    @ApiOperation("根据主键来删除项目参考值数据")
    @PostMapping("deleteRelationProjectReference")
    public Result<Object> deleteRelationProjectReference(@RequestParam String[] ids) {
        if (ids == null || ids.length == 0) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            boolean res = relationProjectReferenceService.removeByIds(Arrays.asList(ids));
            if (res) {
                redisTemplate.delete("permission::relationProjectReference:" + securityUtil.getCurrUser().getId());
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
    @ApiOperation("根据主键来获取项目参考值数据")
    @GetMapping("getRelationProjectReference")
    public Result<Object> getRelationProjectReference(@RequestParam String id) {
        if (StringUtils.isBlank(id)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            RelationProjectReference res = relationProjectReferenceService.getById(id);
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
     * @param relationProjectReference 需要模糊查询的信息
     * @return 返回获取结果
     */
    @ApiOperation("分页查询项目参考值数据")
    @GetMapping("queryRelationProjectReferenceList")
    public Result<Object> queryRelationProjectReferenceList(RelationProjectReference relationProjectReference) {
        try {
            List<RelationProjectReference> list = relationProjectReferenceService.queryRelationProjectReferenceList(relationProjectReference);
            return ResultUtil.data(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：更新数据
     *
     * @param relationProjectReference 实体
     * @return 返回更新结果
     */
    @ApiOperation("更新项目参考值数据")
    @PostMapping("updateRelationProjectReference")
    public Result<Object> updateRelationProjectReference(@RequestBody RelationProjectReference relationProjectReference) {
        if (StringUtils.isBlank(relationProjectReference.getId())) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            boolean res = relationProjectReferenceService.updateById(relationProjectReference);
            if (res) {
                redisTemplate.delete("permission::relationProjectReference:" + securityUtil.getCurrUser().getId());
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
     * @param response                 请求参数
     * @param relationProjectReference 查询参数
     * @return
     */
    @ApiOperation("导出项目参考值数据")
    @PostMapping("/download")
    public void download(HttpServletResponse response, RelationProjectReference relationProjectReference) {
        try {
            relationProjectReferenceService.download(relationProjectReference, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
