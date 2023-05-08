package com.scmt.healthy.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.scmt.core.common.utils.ResultUtil;
import com.scmt.core.common.utils.SecurityUtil;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.Result;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.entity.TDiseaseDiagnosis;
import com.scmt.healthy.service.ITDiseaseDiagnosisService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Date;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author dengjie
 * @since 2023-03-09
 */
@RestController
@RequestMapping("/scmt/tDiseaseDiagnosis")
public class TDiseaseDiagnosisController {
    @Autowired
    private ITDiseaseDiagnosisService tDiseaseDiagnosisService;
    @Autowired
    private SecurityUtil securityUtil;

    /**
     * 功能描述：新增TDiseaseDiagnosis数据
     * @param tDiseaseDiagnosis 实体
     * @return 返回新增结果
     */
    @ApiOperation("新增TDiseaseDiagnosis数据")
    @PostMapping("addTDiseaseDiagnosis")
    public Result<Object> addTDiseaseDiagnosis(@RequestBody TDiseaseDiagnosis tDiseaseDiagnosis){
        try {
            tDiseaseDiagnosis.setDelFlag(0);
            tDiseaseDiagnosis.setCreateId(securityUtil.getCurrUser().getId());
            tDiseaseDiagnosis.setCreateTime(new Date());
            boolean res = tDiseaseDiagnosisService.save(tDiseaseDiagnosis);
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
     * @param tDiseaseDiagnosis 实体
     * @return 返回更新结果
     */
    @ApiOperation("更新TDiseaseDiagnosis数据")
    @PostMapping("updateTDiseaseDiagnosis")
    public Result<Object> updateTDiseaseDiagnosis(@RequestBody TDiseaseDiagnosis tDiseaseDiagnosis){
        if (StringUtils.isBlank(tDiseaseDiagnosis.getId())) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            tDiseaseDiagnosis.setUpdateId(securityUtil.getCurrUser().getId());
            tDiseaseDiagnosis.setUpdateTime(new Date());
            boolean res = tDiseaseDiagnosisService.updateById(tDiseaseDiagnosis);
            if (res) {
                return ResultUtil.data(res, "修改成功");
            } else {
                return ResultUtil.data(res, "修改失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("保存异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：根据主键来删除数据
     * @param ids 主键集合
     * @return 返回删除结果
     */
    @ApiOperation("根据主键来删除TDiseaseDiagnosis数据")
    @PostMapping("deleteTDiseaseDiagnosis")
    public Result<Object> deleteTDiseaseDiagnosis(@RequestParam String[] ids){
        if (ids == null || ids.length == 0) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            boolean res = tDiseaseDiagnosisService.removeByIds(Arrays.asList(ids));
            if (res) {
                return ResultUtil.data(res, "删除成功");
            } else {
                return ResultUtil.data(res, "删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("删除异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：根据主键来获取数据
     * @param id 主键
     * @return 返回获取结果
     */
    @ApiOperation("根据主键来获取TDiseaseDiagnosis数据")
    @GetMapping("getTDiseaseDiagnosis")
    public Result<Object> getTDiseaseDiagnosis(@RequestParam(name = "id")String id){
        if (StringUtils.isBlank(id)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            TDiseaseDiagnosis res = tDiseaseDiagnosisService.getById(id);
            if (res != null) {
                return ResultUtil.data(res, "查询成功");
            } else {
                return ResultUtil.data(res, "查询失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：实现分页查询
     * @param searchVo 需要模糊查询的信息
     * @param pageVo 分页参数
     * @return 返回获取结果
     */
    @ApiOperation("分页查询TDiseaseDiagnosis数据")
    @GetMapping("queryTDiseaseDiagnosisList")
    public Result<Object> queryTDiseaseDiagnosisList(TDiseaseDiagnosis  tDiseaseDiagnosis, SearchVo searchVo, PageVo pageVo){
        try {
            IPage<TDiseaseDiagnosis> result =tDiseaseDiagnosisService.queryTDiseaseDiagnosisListByPage(tDiseaseDiagnosis, searchVo, pageVo);
            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }
    /**
     * 功能描述：导出数据
     * @param response 请求参数
     * @param tDiseaseDiagnosis 查询参数
     * @return
     */
    @ApiOperation("导出TDiseaseDiagnosis数据")
    @PostMapping("/download")
    public void download(HttpServletResponse response, TDiseaseDiagnosis  tDiseaseDiagnosis){
        try {
            tDiseaseDiagnosisService.download( tDiseaseDiagnosis,response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
