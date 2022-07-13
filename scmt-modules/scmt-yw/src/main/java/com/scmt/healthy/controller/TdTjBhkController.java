package com.scmt.healthy.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.scmt.core.common.annotation.SystemLog;
import com.scmt.core.common.enums.LogType;
import com.scmt.core.common.utils.ResultUtil;
import com.scmt.core.common.utils.SecurityUtil;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.Result;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.entity.TdTjBhk;
import com.scmt.healthy.service.ITdTjBhkService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author
 **/
@RestController
@Api(tags = " 网报列表数据接口")
@RequestMapping("/scmt/tdTjBhk")
public class TdTjBhkController {
    @Autowired
    private ITdTjBhkService tdTjBhkService;
    @Autowired
    private SecurityUtil securityUtil;

    /**
     * 功能描述：新增网报列表数据
     *
     * @param tdTjBhk 实体
     * @return 返回新增结果
     */
    @ApiOperation("新增网报列表数据")
    @PostMapping("addTdTjBhk")
    public Result<Object> addTdTjBhk(@RequestBody TdTjBhk tdTjBhk) {
        try {
//			tdTjBhk.setDelFlag(0);
//			tdTjBhk.setCreateId(securityUtil.getCurrUser().getId());
//			tdTjBhk.setCreateTime(new Date());
            boolean res = tdTjBhkService.save(tdTjBhk);
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
     * @param tdTjBhk 实体
     * @return 返回更新结果
     */
    @ApiOperation("更新网报列表数据")
    @PostMapping("updateTdTjBhk")
    public Result<Object> updateTdTjBhk(@RequestBody TdTjBhk tdTjBhk) {
        if (StringUtils.isBlank(tdTjBhk.getId())) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
//			tdTjBhk.setUpdateId(securityUtil.getCurrUser().getId());
//			tdTjBhk.setUpdateTime(new Date());
            boolean res = tdTjBhkService.updateById(tdTjBhk);
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
    @ApiOperation("根据主键来删除网报列表数据")
    @PostMapping("deleteTdTjBhk")
    public Result<Object> deleteTdTjBhk(@RequestParam String[] ids) {
        if (ids == null || ids.length == 0) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            boolean res = tdTjBhkService.removeByIds(Arrays.asList(ids));
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
    @ApiOperation("根据主键来获取网报列表数据")
    @GetMapping("getTdTjBhk")
    public Result<Object> getTdTjBhk(@RequestParam(name = "id") String id) {
        if (StringUtils.isBlank(id)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            TdTjBhk res = tdTjBhkService.getById(id);
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
    @ApiOperation("分页查询网报列表数据")
    @GetMapping("queryTdTjBhkList")
    public Result<Object> queryTdTjBhkList(TdTjBhk tdTjBhk, SearchVo searchVo, PageVo pageVo) {
        try {
            IPage<TdTjBhk> result = tdTjBhkService.queryTdTjBhkListByPage(tdTjBhk, searchVo, pageVo);
            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：导出数据
     *
     * @param response 请求参数
     * @param tdTjBhk  查询参数
     * @return
     */
    @ApiOperation("导出网报列表数据")
    @PostMapping("/download")
    public void download(HttpServletResponse response, TdTjBhk tdTjBhk) {
        try {
            tdTjBhkService.download(tdTjBhk, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @ApiOperation("查询中间表的企业信息")
    @GetMapping("queryCompanyList")
    public Result<Object> queryCompanyList() {
        try {
            List<Map<String, Object>> result = tdTjBhkService.queryCompanyList();
            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }
}