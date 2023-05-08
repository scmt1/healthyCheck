package com.scmt.healthy.controller;

import java.util.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.scmt.healthy.entity.*;
import com.scmt.healthy.service.*;

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
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.scmt.core.common.enums.LogType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author
 **/
@RestController
@Api(tags = " tUnitReport数据接口")
@RequestMapping("/scmt/tUnitReport")
public class TUnitReportController {
    @Autowired
    private ITUnitReportService tUnitReportService;
    @Autowired
    private SecurityUtil securityUtil;
    @Autowired
    private ITReviewProjectService itReviewProjectService;
    @Autowired
    private TInterrogationService interrogationService;

    @Autowired
    private ITReviewPersonService itReviewPersonService;
    @Autowired
    private ITGroupUnitService tGroupUnitService;
    @Autowired
    private ITGroupOrderService tGroupOrderService;

    /**
     * 功能描述：新增tUnitReport数据
     *
     * @param tUnitReport 实体
     * @return 返回新增结果
     */
    @SystemLog(description = "新增tUnitReport数据", type = LogType.OPERATION)
    @ApiOperation("新增tUnitReport数据")
    @PostMapping("addTUnitReport")
    public Result<Object> addTUnitReport(@RequestBody TUnitReport tUnitReport) {
        try {
            tUnitReport.setDelFlag(0);
            tUnitReport.setCreateId(securityUtil.getCurrUser().getId());
            tUnitReport.setCreateTime(new Date());
            boolean res = tUnitReportService.save(tUnitReport);
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
     * @param tUnitReport 实体
     * @return 返回更新结果
     */
    @SystemLog(description = "更新tUnitReport数据", type = LogType.OPERATION)
    @ApiOperation("更新tUnitReport数据")
    @PostMapping("updateTUnitReport")
    public Result<Object> updateTUnitReport(@RequestBody TUnitReport tUnitReport) {
        if (StringUtils.isBlank(tUnitReport.getId())) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            tUnitReport.setUpdateId(securityUtil.getCurrUser().getId());
            tUnitReport.setUpdateTime(new Date());
            boolean res = tUnitReportService.updateById(tUnitReport);
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
    @ApiOperation("根据主键来删除tUnitReport数据")
    @SystemLog(description = "根据主键来删除tUnitReport数据", type = LogType.OPERATION)
    @PostMapping("deleteTUnitReport")
    public Result<Object> deleteTUnitReport(@RequestParam String[] ids) {
        if (ids == null || ids.length == 0) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            boolean res = tUnitReportService.removeByIds(Arrays.asList(ids));
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
    @SystemLog(description = "根据主键来获取tUnitReport数据", type = LogType.OPERATION)
    @ApiOperation("根据主键来获取tUnitReport数据")
    @GetMapping("getTUnitReport")
    public Result<Object> getTUnitReport(@RequestParam(name = "id") String id) {
        if (StringUtils.isBlank(id)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            TUnitReport res = tUnitReportService.getById(id);
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


    @ApiOperation("根据主键来获取tUnitReport数据")
    @GetMapping("getTUnitReportByOrderId")
    public Result<Object> getTUnitReportByOrderId(@RequestParam(name = "orderId") String orderId) {
        if (StringUtils.isBlank(orderId)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            TUnitReport res = tUnitReportService.getTUnitReportByOrderId(orderId);
            if (res != null) {
                return ResultUtil.data(res, "查询成功");
            } else {
                return ResultUtil.data(res,"查询失败");
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
    @SystemLog(description = "分页查询tUnitReport数据", type = LogType.OPERATION)
    @ApiOperation("分页查询tUnitReport数据")
    @GetMapping("queryTUnitReportList")
    public Result<Object> queryTUnitReportList(TUnitReport tUnitReport, SearchVo searchVo, PageVo pageVo) {
        try {
            IPage<TUnitReport> result = tUnitReportService.queryTUnitReportListByPage(tUnitReport, searchVo, pageVo);
            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：实现查询全部
     *
     * @param tUnitReport 查询参数
     * @param searchVo    需要模糊查询的信息
     * @return 返回获取结果
     */
    @SystemLog(description = "查询tUnitReport数据", type = LogType.OPERATION)
    @ApiOperation("查询tUnitReport数据")
    @GetMapping("queryTUnitReportAll")
    public Result<Object> queryTUnitReportAll(TUnitReport tUnitReport, SearchVo searchVo) {
        try {
            List<TUnitReport> result = tUnitReportService.queryTUnitReportListByNotPage(tUnitReport, searchVo);
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
     * @param tUnitReport 查询参数
     * @return
     */
    @SystemLog(description = "导出tUnitReport数据", type = LogType.OPERATION)
    @ApiOperation("导出tUnitReport数据")
    @PostMapping("/download")
    public void download(HttpServletResponse response, TUnitReport tUnitReport) {
        try {
            tUnitReportService.download(tUnitReport, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @ApiOperation("检查人数统计表")
    @GetMapping("checkThePeopleStatisticsTable")
    public Result<Object> checkThePeopleStatisticsTable(String orderId) {
        try {
            List<TUnitReport> result = tUnitReportService.checkThePeopleStatisticsTable(orderId);
            List<TUnitReport> resultFinish = tUnitReportService.checkThePeopleStatisticsTableFinish(orderId);
            //查询复查人员信息
            List<TGroupPerson> reviewData = itReviewProjectService.queryReviewPersonData(orderId);
            //查询复查结果信息
            List<TGroupPerson> tReviewPersonList = itReviewProjectService.queryReviewResultData(orderId);
            //其他人员检查信息
            List<TGroupPerson> personList = itReviewProjectService.queryAllPersonData(orderId);
            //单位信息
            TGroupUnit unitInfo = new TGroupUnit();
            if(StringUtils.isNotBlank(orderId)){
                TGroupOrder orderInfo = tGroupOrderService.getById(orderId);
                if(orderInfo!=null && StringUtils.isNotBlank(orderInfo.getGroupUnitId())){
                    unitInfo = tGroupUnitService.getById(orderInfo.getGroupUnitId());
                }
            }
            //复查后异常人员统计
            /*问诊信息*/
            /*for (TGroupPerson person : personList) {
                if(person!=null && person.getId()!=null && person.getId().trim().length()>0){
                    //问诊查询
                    QueryWrapper<TInterrogation> tInterrogationQueryWrapper = new QueryWrapper<>();
                    tInterrogationQueryWrapper.eq("del_flag",0);
                    tInterrogationQueryWrapper.eq("person_id",person.getId());
                    List<TInterrogation> tInterrogationList = interrogationService.list(tInterrogationQueryWrapper);
                    if(tInterrogationList!=null & tInterrogationList.size()>0){
                        TInterrogation tInterrogation = tInterrogationList.get(0);
                        if(tInterrogation!=null){
                            person.setWorkYear(tInterrogation.getWorkYear());
                            person.setWorkMonth(tInterrogation.getWorkMonth());
                            person.setExposureWorkYear(tInterrogation.getExposureWorkYear());
                            person.setExposureWorkMonth(tInterrogation.getExposureWorkMonth());
                            person.setExposureStartDate(tInterrogation.getExposureStartDate());
                            person.setNation(tInterrogation.getNation());
                            person.setCheckNum(tInterrogation.getCheckNum());
                            person.setDiseaseName(tInterrogation.getDiseaseName());
                            person.setIsCured(tInterrogation.getIsCured());
                            person.setMenarche(tInterrogation.getMenarche());
                            person.setPeriod(tInterrogation.getPeriod());
                            person.setCycle(tInterrogation.getCycle());
                            person.setLastMenstruation(tInterrogation.getLastMenstruation());
                            person.setExistingChildren(tInterrogation.getExistingChildren());
                            person.setAbortion(tInterrogation.getAbortion());
                            person.setPremature(tInterrogation.getPremature());
                            person.setDeath(tInterrogation.getDeath());
                            person.setAbnormalFetus(tInterrogation.getAbnormalFetus());
                            person.setSmokeState(tInterrogation.getSmokeState());
                            person.setPackageEveryDay(tInterrogation.getPackageEveryDay());
                            person.setSmokeYear(tInterrogation.getSmokeYear());
                            person.setDrinkState(tInterrogation.getDrinkState());
                            person.setMlEveryDay(tInterrogation.getMlEveryDay());
                            person.setDrinkYear(tInterrogation.getDrinkYear());
                            person.setOtherInfo(tInterrogation.getOtherInfo());
                            person.setSymptom(tInterrogation.getSymptom());
                            person.setEducation(tInterrogation.getEducation());
                            person.setFamilyAddress(tInterrogation.getFamilyAddress());
                            person.setMenstrualHistory(tInterrogation.getMenstrualHistory());
                            person.setMenstrualInfo(tInterrogation.getMenstrualInfo());
                            person.setAllergies(tInterrogation.getAllergies());
                            person.setAllergiesInfo(tInterrogation.getAllergiesInfo());
                            person.setBirthplaceCode(tInterrogation.getBirthplaceCode());
                            person.setBirthplaceName(tInterrogation.getBirthplaceName());
                            person.setFamilyHistory(tInterrogation.getFamilyHistory());
                            person.setPastMedicalHistoryOtherInfo(tInterrogation.getPastMedicalHistoryOtherInfo());
                            person.setWzCheckDoctor(tInterrogation.getWzCheckDoctor());
                            person.setWzCheckTime(tInterrogation.getWzCheckTime());
                            person.setWzCheckAutograph(tInterrogation.getWzCheckAutograph());
                        }
                    }
                }
            }*/
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("result1", result);
            hashMap.put("resultFinish", resultFinish);
            hashMap.put("result2", reviewData);
            hashMap.put("result3", personList);
            hashMap.put("result4", tReviewPersonList);
            hashMap.put("result5", unitInfo);
            return ResultUtil.data(hashMap);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }
}