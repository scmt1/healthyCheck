package com.scmt.healthy.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.scmt.core.common.utils.ResultUtil;
import com.scmt.core.common.utils.SecurityUtil;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.Result;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.core.entity.User;
import com.scmt.healthy.entity.RelationBasePortfolio;
import com.scmt.healthy.service.IRelationBasePortfolioService;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.scmt.healthy.entity.TBaseProject;
import com.scmt.healthy.service.ITBaseProjectService;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author
 **/
@RestController
@Api(tags = " 体检项目数据接口")
@RequestMapping("/scmt/tBaseProject")
public class TBaseProjectController {
    @Autowired
    private ITBaseProjectService tBaseProjectService;
    @Autowired
    private SecurityUtil securityUtil;
    @Autowired
    private IRelationBasePortfolioService iRelationBasePortfolioService;

    /**
     * 功能描述：新增体检项目数据
     *
     * @param tBaseProject 实体
     * @return 返回新增结果
     */
    @ApiOperation("新增体检项目数据")
    @PostMapping("addTBaseProject")
    public Result<Object> addTBaseProject(@RequestBody TBaseProject tBaseProject) {
        try {
            User currUser = securityUtil.getCurrUser();
            tBaseProject.setCreateTime(new Date());
            tBaseProject.setCreateId(currUser.getId());
            tBaseProject.setDepartmentId(currUser.getDepartmentId());
            boolean res = tBaseProjectService.save(tBaseProject);
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
    @ApiOperation("根据主键来删除体检项目数据")
    @PostMapping("deleteTBaseProject")
    public Result<Object> deleteTBaseProject(@RequestParam String[] ids) {
        if (ids == null || ids.length == 0) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            QueryWrapper<RelationBasePortfolio> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("base_project_id", Arrays.asList(ids));
            List<RelationBasePortfolio> list = iRelationBasePortfolioService.list(queryWrapper);
            if(list.size() > 0) {
                return ResultUtil.error("删除失败，该基础项目已被组合项目绑定！");
            }
            boolean res = tBaseProjectService.removeByIds(Arrays.asList(ids));
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
    @ApiOperation("根据主键来获取体检项目数据")
    @GetMapping("getTBaseProject")
    public Result<Object> getTBaseProject(@RequestParam String id) {
        if (StringUtils.isBlank(id)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            TBaseProject res = tBaseProjectService.getById(id);
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
     * 功能描述：查询所有体检项目数据
     *
     * @return 返回获取结果
     */
    @ApiOperation("查询所有体检项目数据")
    @GetMapping("queryAllTBaseProject")
    public Result<Object> queryAllTBaseProject(@Param("officeId") String officeId) {
        try {
            return tBaseProjectService.queryAllTBaseProject(officeId);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：实现分页查询
     *
     * @param tBaseProject 需要模糊查询的信息
     * @param searchVo     查询参数
     * @param pageVo       分页参数
     * @return 返回获取结果
     */
    @ApiOperation("分页查询体检项目数据")
    @GetMapping("queryTBaseProjectList")
    public Result<Object> queryTBaseProjectList(TBaseProject tBaseProject, SearchVo searchVo, PageVo pageVo) {
        try {
            return tBaseProjectService.queryTBaseProjectListByPage(tBaseProject, searchVo, pageVo);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：更新数据
     *
     * @param tBaseProject 实体
     * @return 返回更新结果
     */
    @ApiOperation("更新体检项目数据")
    @PostMapping("updateTBaseProject")
    public Result<Object> updateTBaseProject(@RequestBody TBaseProject tBaseProject) {
        if (StringUtils.isBlank(tBaseProject.getId())) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            tBaseProject.setUpdateTime(new Date());
            tBaseProject.setUpdateId(securityUtil.getCurrUser().getId());
            boolean res = tBaseProjectService.updateById(tBaseProject);
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
     * @param response     请求参数
     * @param tBaseProject 查询参数
     * @return
     */
    @ApiOperation("导出体检项目数据")
    @PostMapping("/download")
    public void download(HttpServletResponse response, TBaseProject tBaseProject) {
        try {
            tBaseProjectService.download(tBaseProject, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
