package com.scmt.healthy.controller;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.scmt.healthy.entity.TGroupPerson;
import com.scmt.healthy.entity.TReviewRecord;
import com.scmt.healthy.service.ITGroupPersonService;
import com.scmt.healthy.service.ITInspectionRecordService;

import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.serial.SerialBlob;
import javax.transaction.Transactional;

import com.scmt.healthy.utils.BASE64DecodedMultipartFile;
import org.apache.ibatis.jdbc.Null;
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
import com.scmt.healthy.entity.TInspectionRecord;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.scmt.core.common.enums.LogType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author
 **/
@RestController
@Api(tags = " 总检结论数据接口")
@RequestMapping("/scmt/tInspectionRecord")
public class TInspectionRecordController {
    @Autowired
    private ITInspectionRecordService tInspectionRecordService;
    @Autowired
    private SecurityUtil securityUtil;
    @Autowired
    private ITGroupPersonService tGroupPersonService;

    /**
     * 功能描述：新增总检结论数据
     *
     * @param tInspectionRecord 实体
     * @return 返回新增结果
     */
    @SystemLog(description = "新增总检结论数据", type = LogType.OPERATION)
    @ApiOperation("新增总检结论数据")
    @PostMapping("addTInspectionRecord")
    public Result<Object> addTInspectionRecord(@RequestBody TInspectionRecord tInspectionRecord) {
        try {
            QueryWrapper<TInspectionRecord> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("person_id", tInspectionRecord.getPersonId());
            queryWrapper.last("LIMIT 1");
            queryWrapper.orderByDesc("create_time");
            TInspectionRecord one = tInspectionRecordService.getOne(queryWrapper);
            boolean res;
            if (one == null) {
                tInspectionRecord.setDelFlag(0);
                tInspectionRecord.setCreateId(securityUtil.getCurrUser().getId());
                tInspectionRecord.setCreateTime(new Date());
                tInspectionRecord.setInspectionAutograph(securityUtil.getCurrUser().getAutograph());
                res = tInspectionRecordService.save(tInspectionRecord);
            } else {
                tInspectionRecord.setId(one.getId());
                tInspectionRecord.setUpdateId(securityUtil.getCurrUser().getId());
                tInspectionRecord.setUpdateTime(new Date());
                res = tInspectionRecordService.updateById(tInspectionRecord);
            }
            if (res) {
                TGroupPerson tGroupPerson = new TGroupPerson();
                if(tInspectionRecord != null && tInspectionRecord.getReviewResult() != null && tInspectionRecord.getReviewResult().trim().length() > 0){//有复查结论 更新复查状态
                    tGroupPerson.setReviewStatu(1);
                }else{
                    tGroupPerson.setReviewStatu(0);
                }
                tGroupPerson.setId(tInspectionRecord.getPersonId());
                tGroupPerson.setIsPass(4);
                tGroupPerson.setUpdateId(securityUtil.getCurrUser().getId());
                tGroupPerson.setUpdateTime(new Date());
                tGroupPerson.setAvatar(null);
                tGroupPerson.setDiagnosisDate(tInspectionRecord.getInspectionDate());
                if(tInspectionRecord != null && tInspectionRecord.getConclusionCode() != null){
                    if(tInspectionRecord.getConclusionCode().indexOf(";\n") > -1){
                        String[] conclusionSplit = tInspectionRecord.getConclusionCode().split(";\n");
                        if(conclusionSplit != null && conclusionSplit.length > 0){
                            String checkResultNow = "";
                            for(int i = 0;i < conclusionSplit.length;i ++){
                                if(conclusionSplit[i].indexOf("12001") > -1){
                                    if(checkResultNow == ""){
                                        checkResultNow += ""+0;
                                    }else{
                                        checkResultNow += ","+0;
                                    }
                                }else if(conclusionSplit[i].indexOf("12002") > -1){
                                    if(checkResultNow == ""){
                                        checkResultNow += ""+4;
                                    }else{
                                        checkResultNow += ","+4;
                                    }
                                }else if(conclusionSplit[i].indexOf("12003") > -1){
                                    if(checkResultNow == ""){
                                        checkResultNow += ""+3;
                                    }else{
                                        checkResultNow += ","+3;
                                    }
                                }else if(conclusionSplit[i].indexOf("12004") > -1){
                                    if(checkResultNow == ""){
                                        checkResultNow += ""+2;
                                    }else{
                                        checkResultNow += ","+2;
                                    }
                                }else if(conclusionSplit[i].indexOf("12005") > -1){
                                    if(checkResultNow == ""){
                                        checkResultNow += ""+1;
                                    }else{
                                        checkResultNow += ","+1;
                                    }
                                }
                            }
                            tGroupPerson.setCheckResult(checkResultNow);
                        }
                    }else{
                        if ("12001".equals(tInspectionRecord.getConclusionCode())) {
                            tGroupPerson.setCheckResult(""+0);
                            tGroupPerson.setIsRecheck(0);//未见异常 不复查
                        } else if ("12002".equals(tInspectionRecord.getConclusionCode())) {
                            tGroupPerson.setCheckResult(""+4);
                        } else if ("12003".equals(tInspectionRecord.getConclusionCode())) {
                            tGroupPerson.setCheckResult(""+3);
                        } else if ("12004".equals(tInspectionRecord.getConclusionCode())) {
                            tGroupPerson.setCheckResult(""+2);
                        } else if ("12005".equals(tInspectionRecord.getConclusionCode())) {
                            tGroupPerson.setCheckResult(""+1);
//                            tGroupPerson.setIsRecheck(0);//其他异常 不复查
                        }
                    }
                }
                /*if ("12001".equals(tInspectionRecord.getConclusionCode())) {
                    tGroupPerson.setCheckResult(""+0);
                } else if ("12002".equals(tInspectionRecord.getConclusionCode())) {
                    tGroupPerson.setCheckResult(""+4);
                } else if ("12003".equals(tInspectionRecord.getConclusionCode())) {
                    tGroupPerson.setCheckResult(""+3);
                } else if ("12004".equals(tInspectionRecord.getConclusionCode())) {
                    tGroupPerson.setCheckResult(""+2);
                } else if ("12005".equals(tInspectionRecord.getConclusionCode())) {
                    tGroupPerson.setCheckResult(""+1);
                }*/
                tGroupPersonService.updateById(tGroupPerson);
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
     * 功能描述：新增总检结论数据
     *
     * @param inspectionInfoList 实体
     * @return 返回新增结果
     */
    @SystemLog(description = "新增总检结论数据", type = LogType.OPERATION)
    @ApiOperation("新增总检结论数据")
    @Transactional(rollbackOn = Exception.class)
    @PostMapping("batchAddTInspectionRecord")
    public Result<Object> batchAddTInspectionRecord(@RequestBody List<TInspectionRecord> inspectionInfoList) {
        try {
//            ArrayList<TInspectionRecord> inspectionRecords = new ArrayList<>();
            QueryWrapper<TInspectionRecord> queryWrapper  = new QueryWrapper<>();
            ArrayList<TGroupPerson> personInfos = new ArrayList<>();
            for (TInspectionRecord tInspectionRecord : inspectionInfoList) {
                //总检记录
                tInspectionRecord.setDelFlag(0);
                tInspectionRecord.setCreateId(securityUtil.getCurrUser().getId());
                tInspectionRecord.setCreateTime(new Date());
                tInspectionRecord.setUpdateId(securityUtil.getCurrUser().getId());
                tInspectionRecord.setUpdateTime(new Date());
                tInspectionRecord.setInspectionAutograph(securityUtil.getCurrUser().getAutograph());
                //inspectionRecords.add(tInspectionRecord);
                queryWrapper.eq("person_id", tInspectionRecord.getPersonId());
                tInspectionRecordService.saveOrUpdate(tInspectionRecord, queryWrapper);
                //人员
                TGroupPerson tGroupPerson = new TGroupPerson();
                tGroupPerson.setId(tInspectionRecord.getPersonId());
                tGroupPerson.setIsPass(4);
                tGroupPerson.setUpdateId(securityUtil.getCurrUser().getId());
                tGroupPerson.setUpdateTime(new Date());
                tGroupPerson.setDiagnosisDate(tInspectionRecord.getInspectionDate());
                if(tInspectionRecord != null && tInspectionRecord.getConclusionCode() != null){
                    if(tInspectionRecord.getConclusionCode().indexOf(";\n") > -1){
                        String[] conclusionSplit = tInspectionRecord.getConclusionCode().split(";\n");
                        if(conclusionSplit != null && conclusionSplit.length > 0){
                            String checkResultNow = "";
                            for(int i = 0;i < conclusionSplit.length;i ++){
                                if(conclusionSplit[i].indexOf("12001") > -1){
                                    if(checkResultNow == ""){
                                        checkResultNow += ""+0;
                                    }else{
                                        checkResultNow += ","+0;
                                    }
                                }else if(conclusionSplit[i].indexOf("12002") > -1){
                                    if(checkResultNow == ""){
                                        checkResultNow += ""+4;
                                    }else{
                                        checkResultNow += ","+4;
                                    }
                                }else if(conclusionSplit[i].indexOf("12003") > -1){
                                    if(checkResultNow == ""){
                                        checkResultNow += ""+3;
                                    }else{
                                        checkResultNow += ","+3;
                                    }
                                }else if(conclusionSplit[i].indexOf("12004") > -1){
                                    if(checkResultNow == ""){
                                        checkResultNow += ""+2;
                                    }else{
                                        checkResultNow += ","+2;
                                    }
                                }else if(conclusionSplit[i].indexOf("12005") > -1){
                                    if(checkResultNow == ""){
                                        checkResultNow += ""+1;
                                    }else{
                                        checkResultNow += ","+1;
                                    }
                                }
                            }
                            tGroupPerson.setCheckResult(checkResultNow);
                        }
                    }else{
                        if ("12001".equals(tInspectionRecord.getConclusionCode())) {
                            tGroupPerson.setCheckResult(""+0);
                        } else if ("12002".equals(tInspectionRecord.getConclusionCode())) {
                            tGroupPerson.setCheckResult(""+4);
                        } else if ("12003".equals(tInspectionRecord.getConclusionCode())) {
                            tGroupPerson.setCheckResult(""+3);
                        } else if ("12004".equals(tInspectionRecord.getConclusionCode())) {
                            tGroupPerson.setCheckResult(""+2);
                        } else if ("12005".equals(tInspectionRecord.getConclusionCode())) {
                            tGroupPerson.setCheckResult(""+1);
                        }
                    }
                }
                /*if ("12001".equals(tInspectionRecord.getConclusionCode())) {
                    tGroupPerson.setCheckResult(""+0);
                } else if ("12002".equals(tInspectionRecord.getConclusionCode())) {
                    tGroupPerson.setCheckResult(""+4);
                } else if ("12003".equals(tInspectionRecord.getConclusionCode())) {
                    tGroupPerson.setCheckResult(""+3);
                } else if ("12004".equals(tInspectionRecord.getConclusionCode())) {
                    tGroupPerson.setCheckResult(""+2);
                } else if ("12005".equals(tInspectionRecord.getConclusionCode())) {
                    tGroupPerson.setCheckResult(""+1);
                }*/
                personInfos.add(tGroupPerson);
            }
            //boolean res = tInspectionRecordService.saveOrUpdate(inspectionRecords);
            boolean res1 = tGroupPersonService.updateBatchById(personInfos);
            if (res1) {
                return ResultUtil.data(res1, "保存成功");
            } else {
                return ResultUtil.data(res1, "保存失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("保存异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：更新数据
     *
     * @param tInspectionRecord 实体
     * @return 返回更新结果
     */
    @SystemLog(description = "更新总检结论数据", type = LogType.OPERATION)
    @ApiOperation("更新总检结论数据")
    @PostMapping("updateTInspectionRecord")
    public Result<Object> updateTInspectionRecord(@RequestBody TInspectionRecord tInspectionRecord) {
        if (StringUtils.isBlank(tInspectionRecord.getId())) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            TInspectionRecord inspectionRecord = tInspectionRecordService.getById(tInspectionRecord.getId());
            tInspectionRecord.setInspectionAutograph(inspectionRecord.getInspectionAutograph());
            tInspectionRecord.setUpdateId(securityUtil.getCurrUser().getId());
            tInspectionRecord.setUpdateTime(new Date());
            boolean res = tInspectionRecordService.updateById(tInspectionRecord);
            if (res) {
                TGroupPerson tGroupPerson = new TGroupPerson();
                if(tInspectionRecord != null && tInspectionRecord.getReviewResult() != null && tInspectionRecord.getReviewResult().trim().length() > 0){//有复查结论 更新复查状态
                    tGroupPerson.setReviewStatu(1);
                }else{
                    tGroupPerson.setReviewStatu(0);
                }
                tGroupPerson.setId(tInspectionRecord.getPersonId());
                tGroupPerson.setUpdateId(securityUtil.getCurrUser().getId());
                tGroupPerson.setUpdateTime(new Date());
                tGroupPerson.setDiagnosisDate(tInspectionRecord.getInspectionDate());
                if(tInspectionRecord != null && tInspectionRecord.getConclusionCode() != null){
                    if(tInspectionRecord.getConclusionCode().indexOf(";\n") > -1){
                        String[] conclusionSplit = tInspectionRecord.getConclusionCode().split(";\n");
                        if(conclusionSplit != null && conclusionSplit.length > 0){
                            String checkResultNow = "";
                            for(int i = 0;i < conclusionSplit.length;i ++){
                                if(conclusionSplit[i].indexOf("12001") > -1){
                                    if(checkResultNow == ""){
                                        checkResultNow += ""+0;
                                    }else{
                                        checkResultNow += ","+0;
                                    }
                                }else if(conclusionSplit[i].indexOf("12002") > -1){
                                    if(checkResultNow == ""){
                                        checkResultNow += ""+4;
                                    }else{
                                        checkResultNow += ","+4;
                                    }
                                }else if(conclusionSplit[i].indexOf("12003") > -1){
                                    if(checkResultNow == ""){
                                        checkResultNow += ""+3;
                                    }else{
                                        checkResultNow += ","+3;
                                    }
                                }else if(conclusionSplit[i].indexOf("12004") > -1){
                                    if(checkResultNow == ""){
                                        checkResultNow += ""+2;
                                    }else{
                                        checkResultNow += ","+2;
                                    }
                                }else if(conclusionSplit[i].indexOf("12005") > -1){
                                    if(checkResultNow == ""){
                                        checkResultNow += ""+1;
                                    }else{
                                        checkResultNow += ","+1;
                                    }
                                }
                            }
                            tGroupPerson.setCheckResult(checkResultNow);
                        }
                    }else{
                        if ("12001".equals(tInspectionRecord.getConclusionCode())) {
                            tGroupPerson.setCheckResult(""+0);
                            tGroupPerson.setIsRecheck(0);//未见异常 不复查
                        } else if ("12002".equals(tInspectionRecord.getConclusionCode())) {
                            tGroupPerson.setCheckResult(""+4);
                        } else if ("12003".equals(tInspectionRecord.getConclusionCode())) {
                            tGroupPerson.setCheckResult(""+3);
                        } else if ("12004".equals(tInspectionRecord.getConclusionCode())) {
                            tGroupPerson.setCheckResult(""+2);
                        } else if ("12005".equals(tInspectionRecord.getConclusionCode())) {
                            tGroupPerson.setCheckResult(""+1);
//                            tGroupPerson.setIsRecheck(0);//其他异常 不复查
                        }
                    }
                }
                if(inspectionRecord==null || StringUtils.isBlank(inspectionRecord.getId()) || (tInspectionRecord.getIsRecheck() != null && tInspectionRecord.getIsRecheck() == 1)){
                    tGroupPerson.setIsPass(4);
                }

                tGroupPersonService.updateById(tGroupPerson);
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
     *
     * @param ids 主键集合
     * @return 返回删除结果
     */
    @ApiOperation("根据主键来删除总检结论数据")
    @SystemLog(description = "根据主键来删除总检结论数据", type = LogType.OPERATION)
    @PostMapping("deleteTInspectionRecord")
    public Result<Object> deleteTInspectionRecord(@RequestParam String[] ids) {
        if (ids == null || ids.length == 0) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            boolean res = tInspectionRecordService.removeByIds(Arrays.asList(ids));
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
     *
     * @param id 主键
     * @return 返回获取结果
     */
    @SystemLog(description = "根据主键来获取总检结论数据", type = LogType.OPERATION)
    @ApiOperation("根据主键来获取总检结论数据")
    @GetMapping("getTInspectionRecord")
    public Result<Object> getTInspectionRecord(@RequestParam(name = "id") String id) {
        if (StringUtils.isBlank(id)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            TInspectionRecord res = tInspectionRecordService.getById(id);
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
     * 功能描述：根据人员id来获取总检结论数据
     *
     * @param personId 人员id
     * @return 返回获取结果
     */
    @SystemLog(description = "根据人员id来获取总检结论数据", type = LogType.OPERATION)
    @ApiOperation("根据人员id来获取总检结论数据")
    @GetMapping("getTInspectionRecordByPersonId")
    public Result<Object> getTInspectionRecordByPersonId(@RequestParam(name = "personId") String personId) {
        if (StringUtils.isBlank(personId)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            TInspectionRecord res = tInspectionRecordService.getByPersonId(personId);
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
     *
     * @param searchVo 需要模糊查询的信息
     * @param pageVo   分页参数
     * @return 返回获取结果
     */
    @SystemLog(description = "分页查询总检结论数据", type = LogType.OPERATION)
    @ApiOperation("分页查询总检结论数据")
    @GetMapping("queryTInspectionRecordList")
    public Result<Object> queryTInspectionRecordList(TInspectionRecord tInspectionRecord, SearchVo searchVo, PageVo pageVo) {
        try {
            IPage<TInspectionRecord> result = tInspectionRecordService.queryTInspectionRecordListByPage(tInspectionRecord, searchVo, pageVo);
            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：导出数据
     *
     * @param response          请求参数
     * @param tInspectionRecord 查询参数
     * @return
     */
    @SystemLog(description = "导出总检结论数据", type = LogType.OPERATION)
    @ApiOperation("导出总检结论数据")
    @PostMapping("/download")
    public void download(HttpServletResponse response, TInspectionRecord tInspectionRecord) {
        try {
            tInspectionRecordService.download(tInspectionRecord, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
