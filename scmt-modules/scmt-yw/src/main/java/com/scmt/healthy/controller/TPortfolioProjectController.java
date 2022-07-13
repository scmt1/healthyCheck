package com.scmt.healthy.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import cn.hutool.db.Page;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.scmt.core.common.annotation.SystemLog;
import com.scmt.core.common.enums.LogType;
import com.scmt.core.common.utils.SecurityUtil;
import com.scmt.core.entity.User;
import com.scmt.healthy.entity.RelationBasePortfolio;
import com.scmt.healthy.entity.TBaseProject;
import com.scmt.healthy.entity.TComboItem;
import com.scmt.healthy.service.IRelationBasePortfolioService;
import com.scmt.healthy.service.ITBaseProjectService;
import com.scmt.healthy.service.ITComboItemService;
import com.scmt.healthy.service.ITPortfolioProjectService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.scmt.core.common.utils.ResultUtil;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.Result;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.entity.TPortfolioProject;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author
 **/
@RestController
@Api(tags = " 组合项目数据接口")
@RequestMapping("/scmt/tPortfolioProject")
public class TPortfolioProjectController {
    @Autowired
    private ITPortfolioProjectService tPortfolioProjectService;
    @Autowired
    private SecurityUtil securityUtil;
    @Autowired
    private IRelationBasePortfolioService iRelationBasePortfolioService;
    @Autowired
    private ITBaseProjectService itBaseProjectService;
    @Autowired
    private ITComboItemService itComboItemService;

    /**
     * 功能描述：新增组合项目数据
     *
     * @param tPortfolioProject 实体
     * @return 返回新增结果
     */
    @SystemLog(description = "新增组合项目数据", type = LogType.OPERATION)
    @ApiOperation("新增组合项目数据")
    @PostMapping("addTPortfolioProject")
    public Result<Object> addTPortfolioProject(@RequestBody TPortfolioProject tPortfolioProject) {
        try {
            User currUser = securityUtil.getCurrUser();
            tPortfolioProject.setDelFlag(0);
            tPortfolioProject.setCreateTime(new Date());
            tPortfolioProject.setCreateId(currUser.getId());
            tPortfolioProject.setDepartmentId(currUser.getDepartmentId());
            boolean res = tPortfolioProjectService.save(tPortfolioProject);
            if (res) {
                if (tPortfolioProject.getProjectList() != null && tPortfolioProject.getProjectList().size() > 0) {
                    ArrayList<RelationBasePortfolio> relationBasePortfolios = new ArrayList<>();
                    for (TBaseProject tBaseProject : tPortfolioProject.getProjectList()) {
                        if (StringUtils.isBlank(tBaseProject.getResultType())) {
                            return ResultUtil.error(tBaseProject.getName() + "结果类型或者单位为空，绑定失败！！");
                        }
                        if ("数值".equals(tBaseProject.getResultType()) && StringUtils.isBlank(tBaseProject.getUnitCode())) {
                            return ResultUtil.error(tBaseProject.getName() + "项目单位为空,绑定失败！");
                        }
                        RelationBasePortfolio portfolio = new RelationBasePortfolio();
                        portfolio.setBaseProjectId(tBaseProject.getId());
                        portfolio.setPortfolioProjectId(tPortfolioProject.getId());
                        relationBasePortfolios.add(portfolio);
                    }
                    iRelationBasePortfolioService.saveBatch(relationBasePortfolios);
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
     * 功能描述：根据主键来删除数据
     *
     * @param ids 主键集合
     * @return 返回删除结果
     */
    @ApiOperation("根据主键来删除组合项目数据")
    @SystemLog(description = "根据主键来删除组合项目数据", type = LogType.OPERATION)
    @PostMapping("deleteTPortfolioProject")
    public Result<Object> deleteTPortfolioProject(@RequestParam String[] ids) {
        if (ids == null || ids.length == 0) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            QueryWrapper<TComboItem> tComboItemQueryWrapper = new QueryWrapper<>();
            tComboItemQueryWrapper.in("portfolio_project_id", Arrays.asList(ids));
            List<TComboItem> list = itComboItemService.list(tComboItemQueryWrapper);
            if (list.size() > 0) {
                return ResultUtil.error("删除失败，该组合项目已被套餐绑定！");
            }
            boolean res = tPortfolioProjectService.removeByIds(Arrays.asList(ids));
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
    @SystemLog(description = "根据主键来获取组合项目数据", type = LogType.OPERATION)
    @ApiOperation("根据主键来获取组合项目数据")
    @GetMapping("getTPortfolioProject")
    public Result<Object> getTPortfolioProject(@RequestParam(name = "id") String id) {
        if (StringUtils.isBlank(id)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            TPortfolioProject res = tPortfolioProjectService.getById(id);
            if (res != null) {
                ArrayList<String> idList = iRelationBasePortfolioService.queryBaseProjectIdList(id);
                if (idList != null && idList.size() > 0) {
                    res.setProjectList(itBaseProjectService.listByIds(idList));
                }
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
     * @return 返回获取结果
     */
    @SystemLog(description = "分页查询组合项目数据", type = LogType.OPERATION)
    @ApiOperation("分页查询组合项目数据")
    @GetMapping("queryTPortfolioProjectList")
    public Result<Object> queryTPortfolioProjectList(TPortfolioProject tPortfolioProject, SearchVo searchVo, PageVo pageVo) {
        try {
            IPage<TPortfolioProject> listByPage = tPortfolioProjectService.queryTPortfolioProjectListByPage(tPortfolioProject, searchVo, pageVo);
            return ResultUtil.data(listByPage);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    @SystemLog(description = "根据当前科室id查询", type = LogType.OPERATION)
    @ApiOperation("根据当前科室id查询")
    @GetMapping("queryTPortfolioProjectListByOfficeId")
    public Result<Object> queryTPortfolioProjectListByOfficeId(TPortfolioProject tPortfolioProject) {
        try {
            List<TPortfolioProject> result = tPortfolioProjectService.queryTPortfolioProjectListByOfficeId(tPortfolioProject);
            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    @SystemLog(description = "查询所有的组合项目", type = LogType.OPERATION)
    @ApiOperation("查询所有的组合项目")
    @GetMapping("queryAllPortfolioProjectList")
    public Result<Object> queryAllPortfolioProjectList(TPortfolioProject tPortfolioProject) {
        try {
            List<TPortfolioProject> result = tPortfolioProjectService.queryPortfolioProjectList(tPortfolioProject);
            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：更新数据
     *
     * @param tPortfolioProject 实体
     * @return 返回更新结果
     */
    @SystemLog(description = "更新组合项目数据", type = LogType.OPERATION)
    @ApiOperation("更新组合项目数据")
    @PostMapping("updateTPortfolioProject")
    public Result<Object> updateTPortfolioProject(@RequestBody TPortfolioProject tPortfolioProject) {
        if (StringUtils.isBlank(tPortfolioProject.getId())) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            tPortfolioProject.setUpdateTime(new Date());
            tPortfolioProject.setUpdateId(securityUtil.getCurrUser().getId());
            boolean res = tPortfolioProjectService.updateById(tPortfolioProject);
            if (res) {
                QueryWrapper<RelationBasePortfolio> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("portfolio_project_id", tPortfolioProject.getId());
//                iRelationBasePortfolioService.remove(queryWrapper);
                if (tPortfolioProject.getProjectList() != null && tPortfolioProject.getProjectList().size() > 0) {
                    ArrayList<RelationBasePortfolio> relationBasePortfolios = new ArrayList<>();
                    for (TBaseProject tBaseProject : tPortfolioProject.getProjectList()) {
                        if (StringUtils.isBlank(tBaseProject.getResultType())) {
                            return ResultUtil.error(tBaseProject.getName() + "结果类型或者单位为空，绑定失败！！");
                        }
                        if ("数值".equals(tBaseProject.getResultType()) && StringUtils.isBlank(tBaseProject.getUnitCode())) {
                            return ResultUtil.error(tBaseProject.getName() + "项目单位为空,绑定失败！");
                        }
                        RelationBasePortfolio portfolio = new RelationBasePortfolio();
                        portfolio.setBaseProjectId(tBaseProject.getId());
                        portfolio.setPortfolioProjectId(tPortfolioProject.getId());
                        relationBasePortfolios.add(portfolio);
                    }
                    iRelationBasePortfolioService.remove(queryWrapper);
                    iRelationBasePortfolioService.saveBatch(relationBasePortfolios);
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
     * 功能描述：导出数据
     *
     * @param response          请求参数
     * @param tPortfolioProject 查询参数
     * @return
     */
    @SystemLog(description = "导出组合项目数据", type = LogType.OPERATION)
    @ApiOperation("导出组合项目数据")
    @PostMapping("/download")
    public void download(HttpServletResponse response, TPortfolioProject tPortfolioProject) {
        try {
            tPortfolioProjectService.download(tPortfolioProject, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
