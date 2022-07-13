package com.scmt.healthy.controller;

import java.util.Arrays;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.scmt.core.common.annotation.SystemLog;
import com.scmt.core.common.enums.LogType;
import com.scmt.core.common.utils.ResultUtil;
import com.scmt.core.common.utils.SecurityUtil;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.Result;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.core.entity.User;
import com.scmt.healthy.entity.TSample;
import com.scmt.healthy.service.ITSampleService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author
 **/
@RestController
@Api(tags = " 生成数据接口")
@RequestMapping("/scmt/tSample")
public class TSampleController {
    @Autowired
    private ITSampleService tSampleService;
    @Autowired
    private SecurityUtil securityUtil;

    /**
     * 功能描述：新增生成数据
     *
     * @param tSample 实体
     * @return 返回新增结果
     */

    @ApiOperation("新增生成数据")
    @PostMapping("addTSample")
    @SystemLog(description = "新增生成数据", type = LogType.OPERATION)
    public Result<Object> addTSample(TSample tSample) {
        try {
            User currUser = securityUtil.getCurrUser();
            tSample.setDelFlag(0);
            tSample.setCreateTime(new Date());
            tSample.setCreateId(currUser.getId());
            tSample.setDepartmentId(currUser.getDepartmentId());
            boolean res = tSampleService.save(tSample);
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
     * 功能描述：根据主键来删除数据
     *
     * @param ids 主键集合
     * @return 返回删除结果
     */
    @ApiOperation("根据主键来删除生成数据")
    @SystemLog(description = "根据主键来删除生成数据", type = LogType.OPERATION)
    @PostMapping("deleteTSample")
    public Result<Object> deleteTSample(@RequestParam String[] ids) {
        if (ids == null || ids.length == 0) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            boolean res = tSampleService.removeByIds(Arrays.asList(ids));
            if (res) {
                return ResultUtil.data(res, "删除成功");
            } else {
                return ResultUtil.error("删除失败");
            }
        } catch (Exception e) {
            return ResultUtil.error("删除异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：根据主键来获取数据
     *
     * @param id 主键
     * @return 返回获取结果
     */
    @ApiOperation("根据主键来获取生成数据")
    @SystemLog(description = "根据主键来获取生成数据", type = LogType.OPERATION)
    @GetMapping("getTSample")
    public Result<Object> getTSample(@RequestParam String id) {
        if (StringUtils.isBlank(id)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            TSample res = tSampleService.getById(id);
            if (res != null) {
                return ResultUtil.data(res, "查询成功");
            } else {
                return ResultUtil.error("查询失败");
            }
        } catch (Exception e) {
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：实现分页查询
     *
     * @param searchVo 需要模糊查询的信息
     * @param searchVo 查询参数
     * @param pageVo   分页参数
     * @return 返回获取结果
     */
    @ApiOperation("分页查询生成数据")
    @SystemLog(description = "分页查询生成数据", type = LogType.OPERATION)
    @GetMapping("queryTSampleList")
    public Result<Object> queryTSampleList(TSample tSample, SearchVo searchVo, PageVo pageVo) {
        try {
            IPage<TSample> tSampleIPage = tSampleService.queryTSampleListByPage(tSample, searchVo, pageVo);
            return ResultUtil.data(tSampleIPage);
        } catch (Exception e) {
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }


    /**
     * 功能描述：查询全部
     *
     * @return 返回获取结果
     */
    @ApiOperation("查询全部")
    @SystemLog(description = "查询全部", type = LogType.OPERATION)
    @GetMapping("queryAllTSampleList")
    public Result<Object> queryAllTSampleList(TSample tSample) {
        try {
            List<TSample> tSampleIPage = tSampleService.queryAllTSampleList(tSample);
            return ResultUtil.data(tSampleIPage);
        } catch (Exception e) {
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }


    /**
     * 功能描述：更新数据
     *
     * @param tSample 实体
     * @return 返回更新结果
     */
    @ApiOperation("更新生成数据")
    @SystemLog(description = "更新生成数据", type = LogType.OPERATION)
    @PostMapping("updateTSample")
    public Result<Boolean> updateTSample(TSample tSample) {
        if (StringUtils.isBlank(tSample.getId())) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            tSample.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            boolean res = tSampleService.updateById(tSample);
            if (res) {
                return ResultUtil.data(res, "保存成功");
            } else {
                return ResultUtil.data(res, "保存失败");
            }
        } catch (Exception e) {
            return ResultUtil.error("保存异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：导出数据
     *
     * @param response 请求参数
     * @param tSample  查询参数
     * @return
     */
    @ApiOperation("导出生成数据")
    @SystemLog(description = "导出生成数据", type = LogType.OPERATION)
    @PostMapping("/download")
    public void download(HttpServletResponse response, TSample tSample) {
        try {
            tSampleService.download(tSample, response);
        } catch (Exception e) {
        }
    }

    /**
     * 功能描述：查询排序
     *
     * @return
     */
    @ApiOperation("查询排序")
    @SystemLog(description = "查询排序", type = LogType.OPERATION)
    @GetMapping("/getOrderNum")
    public Result<Object> getOrderNum() {
        QueryWrapper<TSample> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("order_num");
        queryWrapper.last("limit 1");
        TSample one = tSampleService.getOne(queryWrapper);
        if (one == null) {
            return ResultUtil.data(0);
        } else {
            return ResultUtil.data(one.getOrderNum() + 1);
        }
    }
}
