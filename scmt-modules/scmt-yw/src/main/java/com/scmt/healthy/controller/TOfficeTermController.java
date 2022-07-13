package com.scmt.healthy.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.scmt.healthy.entity.TSectionOffice;
import com.scmt.healthy.entity.TTemplate;
import com.scmt.healthy.service.ITOfficeTermService;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import com.scmt.core.common.utils.ResultUtil;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.Result;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.core.common.utils.SecurityUtil;
import com.scmt.core.common.annotation.SystemLog;
import com.scmt.healthy.entity.TOfficeTerm;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.scmt.core.common.enums.LogType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author
 **/
@RestController
@Api(tags = " 术语数据接口")
@RequestMapping("/scmt/tOfficeTerm")
public class TOfficeTermController {
    @Autowired
    private ITOfficeTermService tOfficeTermService;
    @Autowired
    private SecurityUtil securityUtil;

    /**
     * 功能描述：新增术语数据
     *
     * @param tOfficeTerm 实体
     * @return 返回新增结果
     */
    @SystemLog(description = "新增术语数据", type = LogType.OPERATION)
    @ApiOperation("新增术语数据")
    @PostMapping("addTOfficeTerm")
    public Result<Object> addTOfficeTerm(@RequestBody TOfficeTerm tOfficeTerm) {
        try {
            //查询术语内容是否重复
            QueryWrapper<TOfficeTerm> queryWrapper = new QueryWrapper<>();
            queryWrapper.and(i -> i.eq("t_office_term.office_id", tOfficeTerm.getOfficeId()));//科室
            queryWrapper.and(i -> i.eq("t_office_term.content", tOfficeTerm.getContent()));//术语内容
            queryWrapper.and(i -> i.eq("t_office_term.type", tOfficeTerm.getType()));//术语类型
            queryWrapper.and(i -> i.eq("t_office_term.inspect_type", tOfficeTerm.getInspectType()));//体检类型
            if(StringUtils.isNotBlank(tOfficeTerm.getHazardFactorsText())){
                queryWrapper.and(i -> i.eq("t_office_term.hazard_factors_text", tOfficeTerm.getHazardFactorsText()));//危害因素
            }else{
                queryWrapper.and(i -> i.eq("hazard_factors_text", "").or().isNull("hazard_factors_text"));//危害因素
            }
            if(StringUtils.isNotBlank(tOfficeTerm.getWorkStateText())){
                queryWrapper.and(i -> i.eq("t_office_term.work_state_text", tOfficeTerm.getWorkStateText()));//在岗状态
            }else{
                queryWrapper.and(i -> i.eq("t_office_term.work_state_text", "").or().isNull("t_office_term.work_state_text"));//在岗状态
            }
            queryWrapper.and(i -> i.eq("t_office_term.del_flag", 0));
            int count = tOfficeTermService.count(queryWrapper);
            if (count > 0) {
                return ResultUtil.error("保存失败:数据重复（当前科室下已有生效术语内容）");
            }
            tOfficeTerm.setDelFlag(0);
            tOfficeTerm.setCreateId(securityUtil.getCurrUser().getId());
            tOfficeTerm.setCreateTime(new Date());
            boolean res = tOfficeTermService.save(tOfficeTerm);
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
     * 功能描述：查询当前部门下全部科室数据
     *
     * @param tOfficeTerm 需要模糊查询的信息
     * @return 返回获取结果
     */
    @ApiOperation("查询当前部门下全部科室数据")
    @GetMapping("queryAllOfficeTermData")
    public Result<Object> queryAllOfficeTermData(TOfficeTerm tOfficeTerm) {
        try {
            List<TOfficeTerm> list = tOfficeTermService.queryAllOfficeTermData(tOfficeTerm);
            return ResultUtil.data(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：更新数据
     *
     * @param tOfficeTerm 实体
     * @return 返回更新结果
     */
    @SystemLog(description = "更新术语数据", type = LogType.OPERATION)
    @ApiOperation("更新术语数据")
    @PostMapping("updateTOfficeTerm")
    public Result<Object> updateTOfficeTerm(@RequestBody TOfficeTerm tOfficeTerm) {
        if (StringUtils.isBlank(tOfficeTerm.getId())) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        //查询术语内容是否重复
        QueryWrapper<TOfficeTerm> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(i -> i.eq("t_office_term.office_id", tOfficeTerm.getOfficeId()));//科室
        queryWrapper.and(i -> i.eq("t_office_term.content", tOfficeTerm.getContent()));//术语内容
        queryWrapper.and(i -> i.eq("t_office_term.type", tOfficeTerm.getType()));//术语类型
        queryWrapper.and(i -> i.eq("t_office_term.inspect_type", tOfficeTerm.getInspectType()));//体检类型
        if(StringUtils.isNotBlank(tOfficeTerm.getHazardFactorsText())){
            queryWrapper.and(i -> i.eq("t_office_term.hazard_factors_text", tOfficeTerm.getHazardFactorsText()));//危害因素
        }else{
            queryWrapper.and(i -> i.eq("hazard_factors_text", "").or().isNull("hazard_factors_text"));//危害因素
        }
        if(StringUtils.isNotBlank(tOfficeTerm.getWorkStateText())){
            queryWrapper.and(i -> i.eq("t_office_term.work_state_text", tOfficeTerm.getWorkStateText()));//在岗状态
        }else{
            queryWrapper.and(i -> i.eq("t_office_term.work_state_text", "").or().isNull("t_office_term.work_state_text"));//在岗状态
        }
        queryWrapper.and(i -> i.eq("t_office_term.del_flag", 0));
        int count = tOfficeTermService.count(queryWrapper);
        if (count > 0) {
            if(count == 1){
                TOfficeTerm officeTerm = tOfficeTermService.getOne(queryWrapper);
                if(!officeTerm.getId().equals(tOfficeTerm.getId())){
                    return ResultUtil.error("保存失败:数据重复（当前科室下已有生效术语内容）");
                }
            }else{
                return ResultUtil.error("保存失败:数据重复（当前科室下已有生效术语内容）");
            }
		}
        try {
            tOfficeTerm.setUpdateId(securityUtil.getCurrUser().getId());
            tOfficeTerm.setUpdateTime(new Date());
            boolean res = tOfficeTermService.updateById(tOfficeTerm);
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
    @ApiOperation("根据主键来删除术语数据")
    @SystemLog(description = "根据主键来删除术语数据", type = LogType.OPERATION)
    @PostMapping("deleteTOfficeTerm")
    public Result<Object> deleteTOfficeTerm(@RequestParam String[] ids) {
        if (ids == null || ids.length == 0) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            boolean res = tOfficeTermService.removeByIds(Arrays.asList(ids));
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
    @SystemLog(description = "根据主键来获取术语数据", type = LogType.OPERATION)
    @ApiOperation("根据主键来获取术语数据")
    @GetMapping("getTOfficeTerm")
    public Result<Object> getTOfficeTerm(@RequestParam(name = "id") String id) {
        if (StringUtils.isBlank(id)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            TOfficeTerm res = tOfficeTermService.getById(id);
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
     * @param tOfficeTerm 术语
     * @param searchVo    需要模糊查询的信息
     * @param pageVo      分页参数
     * @return 返回获取结果
     */
    @SystemLog(description = "分页查询术语数据", type = LogType.OPERATION)
    @ApiOperation("分页查询术语数据")
    @GetMapping("queryTOfficeTermList")
    public Result<Object> queryTOfficeTermList(TOfficeTerm tOfficeTerm, SearchVo searchVo, PageVo pageVo) {
        try {
            IPage<TOfficeTerm> result = tOfficeTermService.queryTOfficeTermListByPage(tOfficeTerm, searchVo, pageVo);
            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：导出数据
     *
     * @param response    请求参数
     * @param tOfficeTerm 查询参数
     * @return
     */
    @SystemLog(description = "导出术语数据", type = LogType.OPERATION)
    @ApiOperation("导出术语数据")
    @PostMapping("/download")
    public void download(HttpServletResponse response, TOfficeTerm tOfficeTerm) {
        try {
            tOfficeTermService.download(tOfficeTerm, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 功能描述：根据officeid  查询所有术语
     *
     * @return
     */
    @SystemLog(description = "导出术语数据", type = LogType.OPERATION)
    @ApiOperation("导出术语数据")
    @GetMapping("/queryAllByOfficeId")
    public Result<Object> queryAllByOfficeId(String content, String type, String officeId, String hazardFactors, String inspectType, String workState) {
        try {
            QueryWrapper<TOfficeTerm> queryWrapper = new QueryWrapper<>();
            if(StringUtils.isNotBlank(officeId)){
                queryWrapper.eq("office_id", officeId);
            }else{
                //权限获取科室
                if (securityUtil.getDeparmentIds() != null) {
                    queryWrapper.in("office_id", securityUtil.getDeparmentIds());
                }
            }
            queryWrapper.eq("status", 0);
            queryWrapper.eq("type", type);
            queryWrapper.eq("del_flag", 0);

            System.out.println(hazardFactors);
            if (StringUtils.isNotBlank(hazardFactors)) {
                String[] split = hazardFactors.split("\\|");
                queryWrapper.and(i->i.in("hazard_factors_text", Arrays.asList(split)).or().eq("hazard_factors_text", "").or().isNull("hazard_factors_text"));            }else{
                queryWrapper.lambda().and(i -> i.eq(TOfficeTerm::getHazardFactorsText, "").or().isNull(true, TOfficeTerm::getHazardFactorsText));
            }

            if (StringUtils.isNotBlank(inspectType)) {
                queryWrapper.lambda().and(i -> i.like(TOfficeTerm::getInspectType, inspectType));
            }
            if (StringUtils.isNotBlank(workState)) {
                queryWrapper.lambda().and(i -> i.like(TOfficeTerm::getWorkStateText, workState).or().eq(TOfficeTerm::getWorkStateText, "").or().isNull(true, TOfficeTerm::getWorkStateText));
            }else{
                queryWrapper.lambda().and(i -> i.eq(TOfficeTerm::getWorkStateText, "").or().isNull(true, TOfficeTerm::getWorkStateText));
            }
            if (StringUtils.isNotBlank(content)) {
                queryWrapper.lambda().and(i -> i.like(TOfficeTerm::getContent, content));
            }
            queryWrapper.groupBy("content");
            queryWrapper.orderByAsc("office_id");
            queryWrapper.orderByAsc("order_num");
            List<TOfficeTerm> list = tOfficeTermService.list(queryWrapper);
            return ResultUtil.data(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常" + e.getMessage());
        }
    }
}
