package com.scmt.healthy.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.scmt.core.common.utils.ResultUtil;
import com.scmt.core.common.utils.SecurityUtil;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.Result;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.core.entity.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.scmt.healthy.entity.TSectionOffice;
import com.scmt.healthy.service.ITSectionOfficeService;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author
 **/
@RestController
@Api(tags = " 科室管理数据接口")
@RequestMapping("/scmt/tSectionOffice")
public class TSectionOfficeController {
    @Autowired
    private ITSectionOfficeService tSectionOfficeService;
    @Autowired
    private SecurityUtil securityUtil;

    /**
     * 功能描述：新增科室管理数据
     *
     * @param tSectionOffice 实体
     * @return 返回新增结果
     */
    @ApiOperation("新增科室管理数据")
    @PostMapping("addTSectionOffice")
    public Result<Object> addTSectionOffice(@RequestBody TSectionOffice tSectionOffice) {
        try {
            User currUser = securityUtil.getCurrUser();
            tSectionOffice.setDelFlag(0);
            tSectionOffice.setCreateTime(new Date());
            tSectionOffice.setCreateId(currUser.getId());
            tSectionOffice.setDepartmentId(currUser.getDepartmentId());

            QueryWrapper<TSectionOffice> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("department_id", currUser.getDepartmentId());
            queryWrapper.eq("section_code", tSectionOffice.getSectionCode().trim());
            if (tSectionOfficeService.getOne(queryWrapper) != null) {
                return ResultUtil.error("科室编码不能重复！");
            }
            queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("department_id", currUser.getDepartmentId());
            queryWrapper.eq("section_name", tSectionOffice.getSectionName().trim());
            if (tSectionOfficeService.getOne(queryWrapper) != null) {
                return ResultUtil.error("科室名称不能重复！");
            }
            boolean res = tSectionOfficeService.save(tSectionOffice);
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
    @ApiOperation("根据主键来删除科室管理数据")
    @PostMapping("deleteTSectionOffice")
    public Result<Object> deleteTSectionOffice(@RequestParam String[] ids) {
        if (ids == null || ids.length == 0) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            boolean res = tSectionOfficeService.removeByIds(Arrays.asList(ids));
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
    @ApiOperation("根据主键来获取科室管理数据")
    @GetMapping("getTSectionOffice")
    public Result<Object> getTSectionOffice(@RequestParam(name = "id") String id) {
        if (StringUtils.isBlank(id)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            TSectionOffice res = tSectionOfficeService.getById(id);
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
     * @param tSectionOffice 需要模糊查询的信息
     * @param searchVo       查询参数
     * @param pageVo         分页参数
     * @return 返回获取结果
     */
    @ApiOperation("分页查询科室管理数据")
    @GetMapping("queryTSectionOfficeList")
    public Result<Object> queryTSectionOfficeList(TSectionOffice tSectionOffice, SearchVo searchVo, PageVo pageVo) {
        try {
            return tSectionOfficeService.queryTSectionOfficeListByPage(tSectionOffice, searchVo, pageVo);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：查询当前部门下全部科室数据
     *
     * @param tSectionOffice 需要模糊查询的信息
     * @return 返回获取结果
     */
    @ApiOperation("查询当前部门下全部科室数据")
    @GetMapping("queryAllSectionOfficeData")
    public Result<Object> queryAllSectionOfficeData(TSectionOffice tSectionOffice) {
        try {
            List<TSectionOffice> list = tSectionOfficeService.queryAllSectionOfficeData(tSectionOffice);
            return ResultUtil.data(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：更新数据
     *
     * @param tSectionOffice 实体
     * @return 返回更新结果
     */
    @ApiOperation("更新科室管理数据")
    @PostMapping("updateTSectionOffice")
    public Result<Object> updateTSectionOffice(@RequestBody TSectionOffice tSectionOffice) {
        if (StringUtils.isBlank(tSectionOffice.getId())) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            User currUser = securityUtil.getCurrUser();
            tSectionOffice.setUpdateTime(new Date());
            tSectionOffice.setUpdateId(currUser.getId());

            QueryWrapper<TSectionOffice> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("department_id", currUser.getDepartmentId());
            queryWrapper.eq("section_code", tSectionOffice.getSectionCode().trim());
            TSectionOffice one = tSectionOfficeService.getOne(queryWrapper);
            if (one != null && !one.getId().equals(tSectionOffice.getId())) {
                return ResultUtil.error("科室编码不能重复！");
            }
            queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("department_id", currUser.getDepartmentId());
            queryWrapper.eq("section_name", tSectionOffice.getSectionName().trim());
            one = tSectionOfficeService.getOne(queryWrapper);
            if (one != null && !one.getId().equals(tSectionOffice.getId())) {
                return ResultUtil.error("科室名称不能重复！");
            }
            boolean res = tSectionOfficeService.updateById(tSectionOffice);
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
     * @param response       请求参数
     * @param tSectionOffice 查询参数
     * @return
     */
    @ApiOperation("导出科室管理数据")
    @PostMapping("/download")
    public void download(HttpServletResponse response, TSectionOffice tSectionOffice) {
        Long time = System.currentTimeMillis();
        try {
            tSectionOfficeService.download(tSectionOffice, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 功能描述：获取所有科室
     *
     * @return
     */
    @ApiOperation("获取所有科室")
    @GetMapping("/getAllSectionOffice")
    public Result<Object> getAllSectionOffice() {
        try {
            QueryWrapper<TSectionOffice> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("del_flag", 0);
            List<TSectionOffice> list = tSectionOfficeService.list(queryWrapper);
            return ResultUtil.data(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询数据失败");
        }
    }

}
