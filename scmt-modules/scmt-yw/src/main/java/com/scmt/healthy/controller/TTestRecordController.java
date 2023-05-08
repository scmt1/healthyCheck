package com.scmt.healthy.controller;

import cn.hutool.core.io.IoUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.scmt.base.entity.DictData;
import com.scmt.core.common.annotation.SystemLog;
import com.scmt.core.common.enums.LogType;
import com.scmt.core.common.exception.ScmtException;
import com.scmt.core.common.exception.TransactionException;
import com.scmt.core.common.utils.ResultUtil;
import com.scmt.core.common.utils.SecurityUtil;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.Result;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.core.entity.User;
import com.scmt.core.utis.FileUtil;
import com.scmt.healthy.common.SocketConfig;
import com.scmt.healthy.entity.*;
import com.scmt.healthy.service.*;
import com.scmt.healthy.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FileUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author
 **/
@RestController
@Api(tags = " 危害因素检测记录数据接口")
@RequestMapping("/scmt/tTestRecord")
public class TTestRecordController {
    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private TTestRecordService tTestRecordService;

    /**
     * socket配置
     */
    @Autowired
    public SocketConfig socketConfig;

    /**
     * 功能描述：新增tTestRecord数据
     *
     * @param tTestRecord 实体
     * @return 返回新增结果
     */
    @SystemLog(description = "新增tTestRecord数据", type = LogType.OPERATION)
    @ApiOperation("新增tTestRecord数据")
    @PostMapping("addtTestRecord")
    public Result<Object> addTTestRecord(@RequestBody TTestRecord tTestRecord) {
        try {
            tTestRecord.setDelFlag(0);
            tTestRecord.setCreateId(securityUtil.getCurrUser().getId());
            tTestRecord.setCreateTime(new Date());
            boolean res = tTestRecordService.save(tTestRecord);
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
     * @param tTestRecord 实体
     * @return 返回更新结果
     */
    @SystemLog(description = "更新tTestRecord数据", type = LogType.OPERATION)
    @ApiOperation("更新tTestRecord数据")
    @PostMapping("updateTTestRecord")
    public Result<Object> updateTTestRecord(@RequestBody TTestRecord tTestRecord) {
        if (StringUtils.isBlank(tTestRecord.getId())) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            tTestRecord.setUpdateId(securityUtil.getCurrUser().getId());
            tTestRecord.setUpdateTime(new Date());
            boolean res = tTestRecordService.updateById(tTestRecord);
            if (res) {
                return ResultUtil.data(res, "修改成功");
            } else {
                return ResultUtil.error( "修改失败");
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
    @ApiOperation("根据主键来删除tTestRecord数据")
    @SystemLog(description = "根据主键来删除tTestRecord数据", type = LogType.OPERATION)
    @PostMapping("deleteTTestRecord")
    public Result<Object> deleteTTestRecord(@RequestParam String[] ids) {
        if (ids == null || ids.length == 0) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            boolean res = tTestRecordService.removeByIds(Arrays.asList(ids));
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
     * 功能描述：实现分页查询
     *
     * @param searchVo 需要模糊查询的信息
     * @param pageVo   分页参数
     * @return 返回获取结果
     */
    @SystemLog(description = "分页查询tTestRecord数据", type = LogType.OPERATION)
    @ApiOperation("分页查询tTestRecord数据")
    @GetMapping("queryTTestRecordList")
    public Result<Object> queryTTestRecordList(TTestRecord tTestRecord, SearchVo searchVo, PageVo pageVo) {
        try {
            IPage<TTestRecord> result = tTestRecordService.queryTTestRecordListByPage(tTestRecord, searchVo, pageVo);
            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：查询
     *
     * @param searchVo 需要模糊查询的信息
     * @return 返回获取结果
     */
    @SystemLog(description = "分询tTestRecord数据", type = LogType.OPERATION)
    @ApiOperation("查询tTestRecord数据")
    @GetMapping("queryTTestRecordListAll")
    public Result<Object> queryTTestRecordListAll(TTestRecord tTestRecord, SearchVo searchVo) {
        try {
            List<TTestRecord> result = tTestRecordService.queryTTestRecordList(tTestRecord, searchVo);
            return ResultUtil.data(result);
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
    @SystemLog(description = "根据主键来获取tTestRecord数据", type = LogType.OPERATION)
    @ApiOperation("根据主键来获取tTestRecord数据")
    @GetMapping("getTTestRecord")
    public Result<Object> getTTestRecord(@RequestParam(name = "id") String id) {
        if (StringUtils.isBlank(id)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            TTestRecord res = tTestRecordService.getById(id);
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

}
