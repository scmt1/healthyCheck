package com.scmt.healthy.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.scmt.core.common.annotation.SystemLog;
import com.scmt.core.common.enums.LogType;
import com.scmt.core.common.utils.ResultUtil;
import com.scmt.core.common.utils.SecurityUtil;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.Result;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.entity.*;
import com.scmt.healthy.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.*;

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

    @Autowired
    private ITReviewPersonService itReviewPersonService;

    @Autowired
    private ITdTjBadrsnsService iTdTjBadrsnsService;

    @Autowired
    private ITPositivePersonService tPositivePersonService;

    @Autowired
    private ITDiseaseDiagnosisService tDiseaseDiagnosisService;

    /**
     * 功能描述：新增总检结论数据
     *
     * @param map 实体
     * @return 返回新增结果
     */
//    @SystemLog(description = "新增总检结论数据", type = LogType.OPERATION)
    @ApiOperation("新增总检结论数据")
    @PostMapping("addTInspectionRecord")
    @Transactional(rollbackOn = Exception.class)
    public Result<Object> addTInspectionRecord(@RequestBody Map<String ,Object> map) {
        TInspectionRecord tInspectionRecord = JSON.parseObject(JSON.toJSONString(map), TInspectionRecord.class);;
        try {
            QueryWrapper<TInspectionRecord> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("person_id", tInspectionRecord.getPersonId());
            queryWrapper.last("LIMIT 1");
            queryWrapper.orderByDesc("create_time");
            TInspectionRecord one = tInspectionRecordService.getOne(queryWrapper);
            boolean res;
            if (one == null) {
                tInspectionRecord.setDelFlag(0);

                if(StringUtils.isBlank(tInspectionRecord.getCreateId())){
                    tInspectionRecord.setCreateId(securityUtil.getCurrUser().getId());
                }
                tInspectionRecord.setCreateTime(new Date());
                if(tInspectionRecord.getInspectionAutograph()==null || StringUtils.isBlank(tInspectionRecord.getInspectionAutograph().toString())){
                    tInspectionRecord.setInspectionAutograph(securityUtil.getCurrUser().getAutograph());
                }

                res = tInspectionRecordService.save(tInspectionRecord);
            } else {
                tInspectionRecord.setId(one.getId());
                tInspectionRecord.setUpdateId(securityUtil.getCurrUser().getId());
                tInspectionRecord.setUpdateTime(new Date());
                res = tInspectionRecordService.updateById(tInspectionRecord);
            }
            if (res) {
                //疾病诊断添加或更新
                if(tInspectionRecord!=null && tInspectionRecord.getTDiseaseDiagnosis()!=null){
                    TDiseaseDiagnosis tDiseaseDiagnosis = tInspectionRecord.getTDiseaseDiagnosis();
                    if(tDiseaseDiagnosis!=null){
                        QueryWrapper<TDiseaseDiagnosis> tDiseaseDiagnosisQueryWrapper = new QueryWrapper<>();
                        tDiseaseDiagnosisQueryWrapper.eq("person_id", tInspectionRecord.getPersonId());
                        tDiseaseDiagnosisQueryWrapper.last("LIMIT 1");
                        tDiseaseDiagnosisQueryWrapper.orderByDesc("create_time");
                        TDiseaseDiagnosis tDiseaseDiagnosisOne = tDiseaseDiagnosisService.getOne(tDiseaseDiagnosisQueryWrapper);
                        boolean resTD;
                        if(tDiseaseDiagnosisOne == null){
                            tDiseaseDiagnosis.setCreateTime(new Date());
                            tDiseaseDiagnosis.setCreateId(securityUtil.getCurrUser().getId());
                        }else{
                            tDiseaseDiagnosis.setUpdateId(securityUtil.getCurrUser().getId());
                            tDiseaseDiagnosis.setUpdateTime(new Date());
                        }
                        tDiseaseDiagnosis.setDelFlag(0);
                        tDiseaseDiagnosis.setPersonId(tInspectionRecord.getPersonId());
                        resTD = tDiseaseDiagnosisService.saveOrUpdate(tDiseaseDiagnosis);
                        if(!resTD){
                            //手动回滚
                            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                            return ResultUtil.error("保存异常:" + "疾病诊断更新失败！");
                        }
                    }
                }

                //更新复查人员表 体检状态
                TReviewPerson tReviewPerson = itReviewPersonService.getById(tInspectionRecord.getPersonId());
                Boolean isUpdate = false;
                if(tReviewPerson==null){
                    isUpdate = true;
                }

                if(isUpdate){
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
                    boolean b = tGroupPersonService.updateById(tGroupPerson);
                    if(!b){
                        //手动回滚
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return ResultUtil.error("保存异常:" + "更新人员信息时异常，请联系管理员");
                    }
                }else{
                    TReviewPerson tReviewPerson1 = new TReviewPerson();
                    tReviewPerson1.setId(tInspectionRecord.getPersonId());
                    tReviewPerson1.setIsPass(4);
                    tReviewPerson1.setUpdateId(securityUtil.getCurrUser().getId());
                    tReviewPerson1.setUpdateTime(new Date());
                    tReviewPerson1.setDiagnosisDate(tInspectionRecord.getInspectionDate());
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
                                tReviewPerson1.setCheckResult(checkResultNow);
                            }
                        }else{
                            if ("12001".equals(tInspectionRecord.getConclusionCode())) {
                                tReviewPerson1.setCheckResult(""+0);
                                tReviewPerson1.setIsRecheck(0);//未见异常 不复查
                            } else if ("12002".equals(tInspectionRecord.getConclusionCode())) {
                                tReviewPerson1.setCheckResult(""+4);
                            } else if ("12003".equals(tInspectionRecord.getConclusionCode())) {
                                tReviewPerson1.setCheckResult(""+3);
                            } else if ("12004".equals(tInspectionRecord.getConclusionCode())) {
                                tReviewPerson1.setCheckResult(""+2);
                            } else if ("12005".equals(tInspectionRecord.getConclusionCode())) {
                                tReviewPerson1.setCheckResult(""+1);
//                            tGroupPerson.setIsRecheck(0);//其他异常 不复查
                            }
                        }
                    }
                    boolean b = itReviewPersonService.updateById(tReviewPerson1);
                    if(!b){
                        //手动回滚
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return ResultUtil.error("保存异常:" + "更新复查人员信息时异常，请联系管理员");
                    }
                }
                if(tInspectionRecord.getBairns()!=null && tInspectionRecord.getBairns().size()>0){
//                    boolean b = iTdTjBadrsnsService.saveBatch(tInspectionRecord.getBairns());
//                    if(!b){
//                        //手动回滚
//                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//                        return ResultUtil.error("保存异常:" + "保存危害因素结论时异常，请联系管理员");
//                    }
                    //先删除(逻辑删除+删除时间)
                    QueryWrapper<TdTjBadrsns> queryWrapperTD = new QueryWrapper<>();
                    queryWrapperTD.eq("FK_BHK_ID",tInspectionRecord.getPersonId());
                    queryWrapperTD.eq("del_flag",0);
                    List<TdTjBadrsns> tdTjBadrsnsList = iTdTjBadrsnsService.list(queryWrapperTD);
                    boolean b = true;
                    if(tdTjBadrsnsList!=null && tdTjBadrsnsList.size()>0){
                        for (int i = 0; i < tdTjBadrsnsList.size(); i++) {
                            TdTjBadrsns tdTjBadrsnsOne = tdTjBadrsnsList.get(i);
                            tdTjBadrsnsOne.setDeleteTime(new Date());
                            b = iTdTjBadrsnsService.updateById(tdTjBadrsnsOne);
                            if(b){
                                b = iTdTjBadrsnsService.removeById(tdTjBadrsnsOne.getId());
                            }
                            if(!b){
                                break;
                            }
                        }
                    }
                    if(b){
                        //后添加(存在则修改)
                        for(TdTjBadrsns tdTjBadrsns : tInspectionRecord.getBairns()){
                            tdTjBadrsns.setDelFlag(0);
                        }
                        b = iTdTjBadrsnsService.saveOrUpdateBatch(tInspectionRecord.getBairns());
                        if(!b){
                            //手动回滚
                            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                            return ResultUtil.error("保存异常:" + "保存危害因素结论时异常，请联系管理员");
                        }
                    }
                    else{
                        return ResultUtil.error("保存异常:" + "删除危害因素结论时异常，请联系管理员");
                    }

                }
                if (tInspectionRecord.getPositiveResultData() != null && tInspectionRecord.getPositiveResultData().size() > 0) {
                    //先删除
                    QueryWrapper<TPositivePerson> queryWrapperTp = new QueryWrapper<>();
                    queryWrapper.eq("person_id",tInspectionRecord.getPersonId());
                    int count = tPositivePersonService.count(queryWrapperTp);
                    boolean b = true;
                    if(count>0){
                        b = tPositivePersonService.remove(queryWrapperTp);
                    }
                    if(b){
                        //后添加
                        for (int i = 0; i <tInspectionRecord.getPositiveResultData().size() ; i++) {
                            TPositivePerson tPositivePerson = tInspectionRecord.getPositiveResultData().get(i);
                            if (tPositivePerson!=null){
                                tPositivePerson.setOrderNum(i);
                                tPositivePerson.setUpdateTime(new Date());
                            }
                        }
                        b = tPositivePersonService.saveBatch(tInspectionRecord.getPositiveResultData());

                        if(!b){
                            //手动回滚
                            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                            return ResultUtil.error("保存异常:" + "保存阳性结果异常，请联系管理员");
                        }
                    }
                    else{
                        return ResultUtil.error("保存异常:" + "删除阳性结果异常，请联系管理员");
                    }
//                    for (int i = 0; i <tInspectionRecord.getPositiveResultData().size() ; i++) {
//                        TPositivePerson tPositivePerson = tInspectionRecord.getPositiveResultData().get(i);
//                        if (tPositivePerson!=null){
//                            tPositivePerson.setOrderNum(i);
//                            tPositivePerson.setCreateTime(new Date());
//                        }
//                    }
//                    boolean b = tPositivePersonService.saveBatch(tInspectionRecord.getPositiveResultData());
//                    if(!b){
//                        //手动回滚
//                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//                        return ResultUtil.error("保存异常:" + "保存阳性结果建议异常，请联系管理员");
//                    }
                }
                return ResultUtil.data(res, "保存成功");
            } else {
                return ResultUtil.error("保存失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            //手动回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
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
                if(StringUtils.isBlank(tInspectionRecord.getCreateId())){
                    tInspectionRecord.setCreateId(securityUtil.getCurrUser().getId());
                }
                tInspectionRecord.setCreateTime(new Date());
                tInspectionRecord.setUpdateId(securityUtil.getCurrUser().getId());
                tInspectionRecord.setUpdateTime(new Date());
                if(tInspectionRecord.getInspectionAutograph()==null|| StringUtils.isBlank(tInspectionRecord.getInspectionAutograph().toString())){
                    tInspectionRecord.setInspectionAutograph(securityUtil.getCurrUser().getAutograph());
                }
                //inspectionRecords.add(tInspectionRecord);
                queryWrapper.eq("person_id", tInspectionRecord.getPersonId());
                boolean b= tInspectionRecordService.saveOrUpdate(tInspectionRecord, queryWrapper);
                if(!b){
                    //手动回滚
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return ResultUtil.data(b, "保存失败:保存总检表信息失败");

                }
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
                //手动回滚
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
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
     * @param map 实体
     * @return 返回更新结果
     */
    @SystemLog(description = "更新总检结论数据", type = LogType.OPERATION)
    @ApiOperation("更新总检结论数据")
    @PostMapping("updateTInspectionRecord")
    public Result<Object> updateTInspectionRecord(@RequestBody Map<String ,Object> map) {
        TInspectionRecord tInspectionRecord = JSON.parseObject(JSON.toJSONString(map), TInspectionRecord.class);;
        if (StringUtils.isBlank(tInspectionRecord.getId())) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            TInspectionRecord inspectionRecord = tInspectionRecordService.getById(tInspectionRecord.getId());
            if(tInspectionRecord.getInspectionAutograph() == null|| StringUtils.isBlank(tInspectionRecord.getInspectionAutograph().toString())){
                tInspectionRecord.setInspectionAutograph(inspectionRecord.getInspectionAutograph());
            }

            tInspectionRecord.setUpdateId(securityUtil.getCurrUser().getId());
            tInspectionRecord.setUpdateTime(new Date());
            boolean res = tInspectionRecordService.updateById(tInspectionRecord);
            if (res) {
                //疾病诊断添加或更新
                if(tInspectionRecord!=null && tInspectionRecord.getTDiseaseDiagnosis()!=null){
                    TDiseaseDiagnosis tDiseaseDiagnosis = tInspectionRecord.getTDiseaseDiagnosis();
                    if(tDiseaseDiagnosis!=null){
                        QueryWrapper<TDiseaseDiagnosis> tDiseaseDiagnosisQueryWrapper = new QueryWrapper<>();
                        tDiseaseDiagnosisQueryWrapper.eq("person_id", tInspectionRecord.getPersonId());
                        tDiseaseDiagnosisQueryWrapper.last("LIMIT 1");
                        tDiseaseDiagnosisQueryWrapper.orderByDesc("create_time");
                        TDiseaseDiagnosis tDiseaseDiagnosisOne = tDiseaseDiagnosisService.getOne(tDiseaseDiagnosisQueryWrapper);
                        if(tDiseaseDiagnosisOne == null){
                            tDiseaseDiagnosis.setCreateTime(new Date());
                            tDiseaseDiagnosis.setCreateId(securityUtil.getCurrUser().getId());
                        }else{
                            tDiseaseDiagnosis.setUpdateId(securityUtil.getCurrUser().getId());
                            tDiseaseDiagnosis.setUpdateTime(new Date());
                        }
                        tDiseaseDiagnosis.setDelFlag(0);
                        boolean resTD = tDiseaseDiagnosisService.saveOrUpdate(tDiseaseDiagnosis);
                        if(!resTD){
                            //手动回滚
                            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                            return ResultUtil.error("保存异常:" + "疾病诊断更新失败！");
                        }
                    }
                }

                //更新复查人员表 体检状态
                TReviewPerson tReviewPerson = itReviewPersonService.getById(tInspectionRecord.getPersonId());
                Boolean isUpdate = false;
                if(tReviewPerson==null){
                    isUpdate = true;
                }

                if(isUpdate){
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

                    boolean b = tGroupPersonService.updateById(tGroupPerson);
                    if(!b){
                        //手动回滚
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return ResultUtil.error("保存异常:" + "更新人员信息时异常，请联系管理员");
                    }
                }else{
                    TReviewPerson tReviewPerson1 = new TReviewPerson();
                    tReviewPerson1.setId(tInspectionRecord.getPersonId());
                    tReviewPerson1.setUpdateId(securityUtil.getCurrUser().getId());
                    tReviewPerson1.setUpdateTime(new Date());
                    tReviewPerson1.setDiagnosisDate(tInspectionRecord.getInspectionDate());
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
                                tReviewPerson1.setCheckResult(checkResultNow);
                            }
                        }else{
                            if ("12001".equals(tInspectionRecord.getConclusionCode())) {
                                tReviewPerson1.setCheckResult(""+0);
                                tReviewPerson1.setIsRecheck(0);//未见异常 不复查
                            } else if ("12002".equals(tInspectionRecord.getConclusionCode())) {
                                tReviewPerson1.setCheckResult(""+4);
                            } else if ("12003".equals(tInspectionRecord.getConclusionCode())) {
                                tReviewPerson1.setCheckResult(""+3);
                            } else if ("12004".equals(tInspectionRecord.getConclusionCode())) {
                                tReviewPerson1.setCheckResult(""+2);
                            } else if ("12005".equals(tInspectionRecord.getConclusionCode())) {
                                tReviewPerson1.setCheckResult(""+1);
//                            tGroupPerson.setIsRecheck(0);//其他异常 不复查
                            }
                        }
                    }
                    if((tReviewPerson.getIsPass()==3)|| inspectionRecord==null || StringUtils.isBlank(inspectionRecord.getId()) || (tInspectionRecord.getIsRecheck() != null && tInspectionRecord.getIsRecheck() == 1)){
                        tReviewPerson1.setIsPass(4);
                    }

                    boolean b = itReviewPersonService.updateById(tReviewPerson1);
                    if(!b){
                        //手动回滚
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return ResultUtil.error("保存异常:" + "更新复查人员信息时异常，请联系管理员");
                    }
                }
                if(tInspectionRecord.getBairns()!=null && tInspectionRecord.getBairns().size()>0){
                    //先删除(逻辑删除+删除时间)
                    QueryWrapper<TdTjBadrsns> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("FK_BHK_ID",tInspectionRecord.getPersonId());
                    queryWrapper.eq("del_flag",0);
                    List<TdTjBadrsns> tdTjBadrsnsList = iTdTjBadrsnsService.list(queryWrapper);
                    boolean b = true;
                    if(tdTjBadrsnsList!=null && tdTjBadrsnsList.size()>0){
                        for (int i = 0; i < tdTjBadrsnsList.size(); i++) {
                            TdTjBadrsns tdTjBadrsnsOne = tdTjBadrsnsList.get(i);
                            tdTjBadrsnsOne.setDeleteTime(new Date());
                            b = iTdTjBadrsnsService.updateById(tdTjBadrsnsOne);
                            if(b){
                                b = iTdTjBadrsnsService.removeById(tdTjBadrsnsOne.getId());
                            }
                           if(!b){
                               break;
                           }
                        }
                    }
                    if(b){
                        //后添加(存在则修改)
                        for(TdTjBadrsns tdTjBadrsns : tInspectionRecord.getBairns()){
                            tdTjBadrsns.setDelFlag(0);
                        }
                        b = iTdTjBadrsnsService.saveOrUpdateBatch(tInspectionRecord.getBairns());
                        if(!b){
                            //手动回滚
                            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                            return ResultUtil.error("保存异常:" + "保存危害因素结论时异常，请联系管理员");
                        }
                    }
                    else{
                        return ResultUtil.error("保存异常:" + "删除危害因素结论时异常，请联系管理员");
                    }


                }

                if(tInspectionRecord.getPositiveResultData()!=null && tInspectionRecord.getPositiveResultData().size()>0){
                    //先删除
                    QueryWrapper<TPositivePerson> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("person_id",tInspectionRecord.getPersonId());
                    int count = tPositivePersonService.count(queryWrapper);
                    boolean b = true;
                    if(count>0){
                        b = tPositivePersonService.remove(queryWrapper);
                    }
                    if(b){
                        //后添加
                        for (int i = 0; i <tInspectionRecord.getPositiveResultData().size() ; i++) {
                            TPositivePerson tPositivePerson = tInspectionRecord.getPositiveResultData().get(i);
                            if (tPositivePerson!=null){
                                tPositivePerson.setOrderNum(i);
                                tPositivePerson.setUpdateTime(new Date());
                            }
                        }
                        b = tPositivePersonService.saveBatch(tInspectionRecord.getPositiveResultData());

                        if(!b){
                            //手动回滚
                            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                            return ResultUtil.error("保存异常:" + "保存阳性结果异常，请联系管理员");
                        }
                    }
                    else{
                        return ResultUtil.error("保存异常:" + "删除阳性结果异常，请联系管理员");
                    }


                }


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
                if(res.getInspectionAutograph()!=null){
                    //字节转字符串
                    byte[] blob = (byte[]) res.getInspectionAutograph();
                    if(blob!=null){
                        String avatarNow = new String(blob);
                        if(avatarNow.indexOf("/dcm") > -1){
                            res.setInspectionAutograph(avatarNow);
                        }
                    }
                }
                //危害因素结论关联查询
                QueryWrapper<TdTjBadrsns> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("FK_BHK_ID",res.getPersonId());
                List<TdTjBadrsns> list = iTdTjBadrsnsService.list(queryWrapper);
                res.setBairns(list);
                //危害因素结论关联查询
                QueryWrapper<TDiseaseDiagnosis> tDiseaseDiagnosisQueryWrapper = new QueryWrapper<>();
                tDiseaseDiagnosisQueryWrapper.eq("person_id",res.getPersonId());
                tDiseaseDiagnosisQueryWrapper.last("LIMIT 1");
                tDiseaseDiagnosisQueryWrapper.orderByDesc("create_time");
                tDiseaseDiagnosisQueryWrapper.ne("del_flag",1);
                TDiseaseDiagnosis tDiseaseDiagnoses = tDiseaseDiagnosisService.getOne(tDiseaseDiagnosisQueryWrapper);
                res.setTDiseaseDiagnosis(tDiseaseDiagnoses);
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

    /**
     * 功能描述：取消总检
     *
     * @param personId 实体
     * @return 返回更新结果
     */
    @SystemLog(description = "更新总检结论数据", type = LogType.OPERATION)
    @ApiOperation("更新总检结论数据")
    @PostMapping("CancelGeneralInspection")
    public Result<Object> CancelGeneralInspection(@RequestParam(name = "personId")  String personId) {
        if(StringUtils.isBlank(personId)){
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            TReviewPerson tReviewPerson = itReviewPersonService.getById(personId);
            Boolean isUpdate = false;
            if(tReviewPerson == null){
                isUpdate = true;
            }
            if (isUpdate){
                    QueryWrapper<TGroupPerson> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("id",personId);
                    TGroupPerson tGroupPerson = new TGroupPerson();
                    tGroupPerson.setIsPass(3);
                    boolean update = tGroupPersonService.update(tGroupPerson,queryWrapper);
                    if (update){
                        return ResultUtil.data(update, "修改成功");
                    }else {
                        return ResultUtil.data(update, "修改失败");
                    }
            }else {
                QueryWrapper<TReviewPerson> queryWrapper1 = new QueryWrapper<>();
                queryWrapper1.eq("id",personId);
                TReviewPerson tReviewPerson1 = new TReviewPerson();
                tReviewPerson1.setIsPass(3);
                boolean update = itReviewPersonService.update(tReviewPerson1,queryWrapper1);
                if (update){
                    return ResultUtil.data(update, "修改成功");
                }else {
                    return ResultUtil.data(update, "修改失败");
                }
            }

        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.error("修改异常:" + e.getMessage());
        }
    }

}
