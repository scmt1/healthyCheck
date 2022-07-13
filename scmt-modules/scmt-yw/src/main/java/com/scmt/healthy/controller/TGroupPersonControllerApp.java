package com.scmt.healthy.controller;

import cn.hutool.core.io.IoUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.scmt.base.entity.DictData;
import com.scmt.base.service.DictDataService;
import com.scmt.core.common.annotation.SystemLog;
import com.scmt.core.common.enums.LogType;
import com.scmt.core.common.exception.ScmtException;
import com.scmt.core.common.exception.TransactionException;
import com.scmt.core.common.utils.ResultUtil;
import com.scmt.core.common.utils.SecurityUtil;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.Result;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.core.utis.FileUtil;
import com.scmt.healthy.common.SocketConfig;
import com.scmt.healthy.entity.*;
import com.scmt.healthy.service.*;
import com.scmt.healthy.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
import javax.sql.rowset.serial.SerialBlob;
import javax.transaction.Transactional;
import java.io.*;
import java.sql.Blob;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author
 **/
@RestController
@Api(tags = " 团检人员数据接口")
@RequestMapping("/scmt/tGroupPersonApp")
public class TGroupPersonControllerApp {
    @Autowired
    private ITGroupPersonService tGroupPersonService;
    @Autowired
    private ITProTypeService proTypeService;
    @Autowired
    private ITGroupOrderService groupOrderService;
    @Autowired
    private SecurityUtil securityUtil;
    @Autowired
    private ITOrderGroupItemService itOrderGroupItemService;
    @Autowired
    private ITGroupUnitService itGroupUnitService;
    @Autowired
    private ITBarcodeService itBarcodeService;
    @Autowired
    private ITCareerHistoryService itCareerHistoryService;
    @Autowired
    private ITPastMedicalHistoryService itPastMedicalHistoryService;
    @Autowired
    private ITSymptomService itSymptomService;
    @Autowired
    private ITComboService itComboService;
    @Autowired
    private ITReviewProjectService itReviewProjectService;
    @Autowired
    private ITOrderGroupService itOrderGroupService;
    @Autowired
    private ITDepartItemResultService tDepartItemResultService;
    @Autowired
    private DictDataService dictDataService;
    @Autowired
    private ITOrderGroupItemProjectService itOrderGroupItemProjectService;
    @Autowired
    private IRelationBasePortfolioService iRelationBasePortfolioService;
    @Autowired
    private ITBaseProjectService itBaseProjectService;
    @Autowired
    private IRelationPersonProjectCheckService iRelationPersonProjectCheckService;
    @Autowired
    private ITGroupPersonService itGroupPersonService;
    @Autowired
    private ITOrderGroupItemService tOrderGroupItemService;
    @Autowired
    private ITOrderGroupItemProjectService tOrderGroupItemProjectService;
    @Autowired
    private ITDepartResultService tDepartResultService;
    @Autowired
    private ITDepartResultService departResultService;
    @Autowired
    private IRelationPersonProjectCheckService relationPersonProjectCheckService;
    @Autowired
    private ITOrderGroupItemService itemService;
    @Autowired
    private ITReviewProjectService tReviewProjectService;
    @Autowired
    private ITComboItemService tComboItemService;

    @Autowired
    private ITInspectionRecordService tInspectionRecordService;

    /**
     * socket配置
     */
    @Autowired
    public SocketConfig socketConfig;

    /**
     * 功能描述：新增团检人员数据
     *
     * @param tGroupPerson 实体
     * @return 返回新增结果
     */
    @SystemLog(description = "新增团检人员数据", type = LogType.OPERATION)
    @ApiOperation("新增团检人员数据")
    @PostMapping("addTGroupPerson")
    public Result<Object> addTGroupPerson(@RequestBody TGroupPerson tGroupPerson) {
        try {
            //判断当前数据条数是否超过分组人员上限
            Boolean flag = checkCount(tGroupPerson.getGroupId(), 1);
            if (!flag) {
                return ResultUtil.error("当前导入人数超出订单总人数，导入失败");
            }
            //同一个分组下边是否有多个相同的身份证
            QueryWrapper<TGroupPerson> personQueryWrapper = new QueryWrapper<>();

            personQueryWrapper.eq("del_flag", 0);
            if(tGroupPerson.getOrderId()!=null && tGroupPerson.getOrderId().trim().length() > 0){
                personQueryWrapper.eq("order_id", tGroupPerson.getOrderId());
            }
            if(tGroupPerson.getGroupId()!=null && tGroupPerson.getGroupId().trim().length() > 0){
                personQueryWrapper.eq("group_id", tGroupPerson.getGroupId());
            }
            if(tGroupPerson.getIdCard()!=null && tGroupPerson.getIdCard().trim().length() > 0){
                personQueryWrapper.eq("id_card", tGroupPerson.getIdCard());
            }
            if(tGroupPerson.getPhysicalType()!=null && tGroupPerson.getPhysicalType().trim().length() > 0){
                personQueryWrapper.eq("physical_type", tGroupPerson.getPhysicalType());
                if(tGroupPerson.getPhysicalType().indexOf("从业体检") > -1){
                    personQueryWrapper.eq("is_pass", 1);
                }else if (tGroupPerson.getPhysicalType().indexOf("健康体检") > -1){
                    if(tGroupPerson.getOrderId()==null || tGroupPerson.getOrderId().trim().length() == 0){
                        personQueryWrapper.eq("is_pass", 1);
                    }
                }
            }
            if(tGroupPerson.getPersonName()!=null && tGroupPerson.getPersonName().trim().length() > 0){
                personQueryWrapper.eq("person_name", tGroupPerson.getPersonName());
            }
            List<TGroupPerson> list = tGroupPersonService.list(personQueryWrapper);
            if (list.size() > 0) {
                if(tGroupPerson.getIdCard() != null && tGroupPerson.getIdCard().trim().length() > 0){
                    return ResultUtil.error("已导入身份证为" + tGroupPerson.getIdCard() + "的体检人员信息");
                }else{
                    return ResultUtil.error("已导入姓名为" + tGroupPerson.getPersonName() + "的体检人员信息");
                }
            }
            TGroupOrder groupOrder = groupOrderService.getById(tGroupPerson.getOrderId());
            tGroupPerson.setIsPass(1);
            tGroupPerson.setDelFlag(0);
            if ("健康体检".equals(tGroupPerson.getPhysicalType())) {
                tGroupPerson.setIsWzCheck(1);
            } else {
                tGroupPerson.setIsWzCheck(0);
            }
            tGroupPerson.setIsCheck(0);
            tGroupPerson.setIsRecheck(0);
            tGroupPerson.setReportPrintingNum(0);
            tGroupPerson.setTestNum(generatorNum(tGroupPerson.getPhysicalType()));
            tGroupPerson.setCreateId(securityUtil.getCurrUser().getId());
            tGroupPerson.setCreateTime(new Date());
            tGroupPerson.setUnitId(groupOrder.getGroupUnitId());
            tGroupPerson.setDept(groupOrder.getGroupUnitName());
            tGroupPerson.setOldGroupId(tGroupPerson.getGroupId());
            tGroupPerson.setSporadicPhysical(groupOrder.getSporadicPhysical());
            if("健康体检".equals(tGroupPerson.getPhysicalType())){
                tGroupPerson.setWorkStateText(tGroupPerson.getWorkStateText().trim());
            }
            boolean res = tGroupPersonService.save(tGroupPerson);
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
     * 功能描述：更新团检人员状态
     * 更新 isPass
     *
     * @param tGroupPerson 实体
     * @return 返回更新结果
     */
    @SystemLog(description = "更新团检人员状态", type = LogType.OPERATION)
    @ApiOperation("更新团检人员状态")
    @PostMapping("updateTGroupPersonByIsPass")
    public Result<Object> updateTGroupPersonByIsPass(@RequestBody TGroupPerson tGroupPerson) {
        if (StringUtils.isBlank(tGroupPerson.getId())) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            tGroupPerson.setUpdateId(securityUtil.getCurrUser().getId());
            tGroupPerson.setUpdateTime(new Date());
            tGroupPerson.setAvatar(null);
            boolean res = tGroupPersonService.updateById(tGroupPerson);
            if (res) {
                return ResultUtil.data(res, "修改成功");
            } else {
                return ResultUtil.error("修改失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("修改异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：根据主键来修改人员状态
     *
     * @param ids 主键集合
     * @return 返回结果
     */
    @ApiOperation("根据主键来修改人员状态")
    @SystemLog(description = "根据主键来修改人员状态", type = LogType.OPERATION)
    @PostMapping("updateTGroupPersonById")
    public Result<Object> updateTGroupPersonById(@RequestBody String[] ids) {
        if (ids == null) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            List<String> strings = Arrays.asList(ids);
            List<TGroupPerson> re = new ArrayList<>();
            for (String str : strings) {
                TGroupPerson byId = tGroupPersonService.getById(str);
                byId.setIsPass(5);
                re.add(byId);
            }
            boolean res = tGroupPersonService.updateBatchById(re);
            if (res) {
                return ResultUtil.data(res, "审核成功");
            } else {
                return ResultUtil.data(res, "审核失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("审核异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：根据主键来修改人员状态
     *
     * @param ids 主键集合
     * @return 返回结果
     */
    @ApiOperation("根据主键来修改人员状态(退回)")
    @SystemLog(description = "根据主键来修改人员状态(退回)", type = LogType.OPERATION)
    @PostMapping("updateTGroupPersonByIdRetreat")
    public Result<Object> updateTGroupPersonByIdRetreat(@RequestBody String[] ids) {
        if (ids == null) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            List<String> strings = Arrays.asList(ids);
            List<TGroupPerson> re = new ArrayList<>();
            for (String str : strings) {
                TGroupPerson byId = tGroupPersonService.getById(str);
                byId.setIsPass(4);
                re.add(byId);
            }
            boolean res = tGroupPersonService.updateBatchById(re);
            if (res) {
                return ResultUtil.data(res, "退回成功");
            } else {
                return ResultUtil.data(res, "退回失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("退回异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：根据主键来修改人员状态
     *
     * @param ids 主键集合
     * @return 返回结果
     */
    @ApiOperation("根据主键来修改人员状态(打印)")
    @SystemLog(description = "根据主键来修改人员状态(打印)", type = LogType.OPERATION)
    @PostMapping("updateTPrintStateById")
    public Result<Object> updateTPrintStateById(@RequestBody String[] ids) {
        if (ids == null) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            List<String> strings = Arrays.asList(ids);
            List<TGroupPerson> re = new ArrayList<>();
            for (String str : strings) {
                TGroupPerson byId = tGroupPersonService.getById(str);
                byId.setPrintState(1);
                re.add(byId);
            }
            boolean res = tGroupPersonService.updateBatchById(re);
            if (res) {
                return ResultUtil.data(res, "确认成功");
            } else {
                return ResultUtil.data(res, "确认失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("确认异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：更新数据
     *
     * @param tGroupPerson 实体
     * @return 返回更新结果
     */
    @SystemLog(description = "更新团检人员数据", type = LogType.OPERATION)
    @ApiOperation("更新团检人员数据")
    @PostMapping("updateTGroupPerson")
    public Result<Object> updateTGroupPerson(@RequestBody TGroupPerson tGroupPerson) {
        if (StringUtils.isBlank(tGroupPerson.getId())) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            //判断当前数据条数是否超过分组人员上限
            Boolean flag = checkCount(tGroupPerson.getGroupId(), 1);
            if (!flag) {
                return ResultUtil.error("当前导入人数超出订单总人数，导入失败");
            }
//            Date exposureStartDate = tGroupPerson.getExposureStartDate();
//            if (exposureStartDate != null) {
//                //设置年月
//                Calendar cal = Calendar.getInstance();
//                cal.setTime(exposureStartDate);
//
//                //接害工龄日期
//                int year = cal.get(Calendar.YEAR);
//                int month = cal.get(Calendar.MONTH) + 1;
//                //当前日期
//                cal = Calendar.getInstance();
//                int curr_year = cal.get(Calendar.YEAR);
//                int curr_month = cal.get(Calendar.MONTH) + 1;
//
//                //接害年
//                int res_year = curr_year - year;
//                int month_in_year = (curr_year - year) * 12;
//                //接害月
//                int res_month = Math.abs(curr_month - month) + month_in_year;
//                tGroupPerson.setExposureWorkYear(res_year);
//                tGroupPerson.setExposureWorkMonth(res_month);
//            }
            tGroupPerson.setUpdateId(securityUtil.getCurrUser().getId());
            tGroupPerson.setUpdateTime(new Date());
            tGroupPerson.setAvatar(null);
            if("职业体检".equals(tGroupPerson.getPhysicalType())){
                tGroupPerson.setWorkStateText(tGroupPerson.getWorkStateText().trim());
            }
            boolean res = tGroupPersonService.updateById(tGroupPerson);
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
     * 功能描述：更新数据
     *
     * @param tGroupPerson 实体
     * @return 返回更新结果
     */
    @SystemLog(description = "更新团检人员数据(含分组)", type = LogType.OPERATION)
    @ApiOperation("更新团检人员数据(含分组)")
    @PostMapping("updateTGroupPersonAndGroup")
    public Result<Object> updateTGroupPersonAndGroup(@RequestBody TGroupPerson tGroupPerson) {
        if (StringUtils.isBlank(tGroupPerson.getId())) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            //查询原体检人员数据
            TGroupPerson oldGroupPerson = tGroupPersonService.getById(tGroupPerson.getId());

            //若分组变更了，则执行如下方法
            if(!tGroupPerson.getGroupId().equals(oldGroupPerson.getGroupId()) && tGroupPerson.getIsPass() > 1){
                //原始数据的，分组id
                String oldGroupId = oldGroupPerson.getGroupId();
                //需要变更的，分组id
                String newGroupId = tGroupPerson.getGroupId();
                //体检人员id
                String personId = tGroupPerson.getId();

                //订单分组下的，组合项目数据查询(原始)
                QueryWrapper<TOrderGroupItem> itemQueryOldWrapper = new QueryWrapper<>();
                itemQueryOldWrapper.eq("group_id", oldGroupId);
                List<TOrderGroupItem> orderGroupItemOlds = tOrderGroupItemService.list(itemQueryOldWrapper);
                //订单分组下的，组合项目数据查询(变更)
                QueryWrapper<TOrderGroupItem> itemQueryWrapper = new QueryWrapper<>();
                itemQueryWrapper.eq("group_id", newGroupId);
                List<TOrderGroupItem> orderGroupItems = tOrderGroupItemService.list(itemQueryWrapper);

                //遍历修改结果
                for (TOrderGroupItem orderGroupItem : orderGroupItems) {
                    String portfolioProjectId = orderGroupItem.getPortfolioProjectId();
                    for (TOrderGroupItem orderGroupItemOld : orderGroupItemOlds) {
                        String portfolioProjectOldId = orderGroupItemOld.getPortfolioProjectId();
                        if(portfolioProjectOldId.equals(portfolioProjectId)){
                            //修改数据(组合项目结果)
                            QueryWrapper<TDepartResult> departResultQueryWrapper = new QueryWrapper<>();
                            departResultQueryWrapper.eq("person_id", personId);
                            departResultQueryWrapper.eq("group_item_id", orderGroupItemOld.getId());
                            TDepartResult tDepartResult = new TDepartResult();
                            tDepartResult.setGroupItemId(orderGroupItem.getId());
                            tDepartResultService.update(tDepartResult,departResultQueryWrapper);

                            //修改数据(到检确认)
                            QueryWrapper<RelationPersonProjectCheck> relationPersonProjectCheckQueryWrapper = new QueryWrapper<>();
                            relationPersonProjectCheckQueryWrapper.eq("person_id", personId);
                            relationPersonProjectCheckQueryWrapper.eq("order_group_item_id", orderGroupItemOld.getId());
                            RelationPersonProjectCheck relationPersonProjectCheck = new RelationPersonProjectCheck();
                            relationPersonProjectCheck.setOrderGroupItemId(orderGroupItem.getId());
                            relationPersonProjectCheckService.update(relationPersonProjectCheck,relationPersonProjectCheckQueryWrapper);

                            //订单组合项目下的，基础项目数据查询(原始)
                            QueryWrapper<TOrderGroupItemProject> itemProjectOldQueryWrapper = new QueryWrapper<>();
                            itemProjectOldQueryWrapper.eq("t_order_group_item_id", orderGroupItemOld.getId());
                            List<TOrderGroupItemProject> orderGroupItemOldProjects = tOrderGroupItemProjectService.list(itemProjectOldQueryWrapper);
                            //订单组合项目下的，基础项目数据查询(变更)
                            QueryWrapper<TOrderGroupItemProject> itemProjectQueryWrapper = new QueryWrapper<>();
                            itemProjectQueryWrapper.eq("t_order_group_item_id", orderGroupItem.getId());
                            List<TOrderGroupItemProject> orderGroupItemProjects = tOrderGroupItemProjectService.list(itemProjectQueryWrapper);

                            for (TOrderGroupItemProject orderGroupItemProject : orderGroupItemProjects) {
                                for (TOrderGroupItemProject orderGroupItemOldProject : orderGroupItemOldProjects) {
                                    if(orderGroupItemOldProject.getBaseProjectId().equals(orderGroupItemProject.getBaseProjectId())){
                                        //修改数据(基础项目结果)
                                        QueryWrapper<TDepartItemResult> departItemResultQueryWrapper = new QueryWrapper<>();
                                        departItemResultQueryWrapper.eq("person_id", personId);
                                        departItemResultQueryWrapper.eq("order_group_item_id", orderGroupItemOld.getId());
                                        departItemResultQueryWrapper.eq("order_group_item_project_id", orderGroupItemOldProject.getId());
                                        TDepartItemResult tDepartItemResult = new TDepartItemResult();
                                        tDepartItemResult.setOrderGroupItemId(orderGroupItem.getId());
                                        tDepartItemResult.setOrderGroupItemProjectId(orderGroupItemProject.getId());
                                        tDepartItemResultService.update(tDepartItemResult,departItemResultQueryWrapper);
                                    }
                                }
                            }
                        }
                    }
                }
                //修改复查结果(当前体检人员的复查项目移到新的订单分组下)
                QueryWrapper<TReviewProject> tReviewProjectQueryWrapper = new QueryWrapper<>();
                tReviewProjectQueryWrapper.eq("person_id", personId);
                TReviewProject tReviewProject = new TReviewProject();
                tReviewProject.setGroupId(newGroupId);
                tReviewProject.setGroupOrderId(tGroupPerson.getOrderId());
                tReviewProjectService.update(tReviewProject,tReviewProjectQueryWrapper);

                //清除数据(旧订单分组保存的组合项、基础项结果及到检确认数据)
                for (TOrderGroupItem orderGroupItemOld : orderGroupItemOlds) {
                    QueryWrapper<TDepartResult> departQueryWrapper = new QueryWrapper<>();
                    departQueryWrapper.eq("person_id", personId);
                    departQueryWrapper.eq("group_item_id", orderGroupItemOld.getId());
                    //tDepartResultService.remove(departQueryWrapper);
                    TDepartResult tDepartResult = new TDepartResult();
                    tDepartResult.setDelFlag(1);;
                    tDepartResultService.update(tDepartResult,departQueryWrapper);

                    QueryWrapper<RelationPersonProjectCheck> relationPersonProjectCheckQueryWrapper = new QueryWrapper<>();
                    relationPersonProjectCheckQueryWrapper.eq("person_id", personId);
                    relationPersonProjectCheckQueryWrapper.eq("order_group_item_id", orderGroupItemOld.getId());
                    relationPersonProjectCheckService.remove(relationPersonProjectCheckQueryWrapper);

                    QueryWrapper<TDepartItemResult> departItemResultQueryWrapper = new QueryWrapper<>();
                    departItemResultQueryWrapper.eq("person_id", personId);
                    departItemResultQueryWrapper.eq("order_group_item_id", orderGroupItemOld.getId());
                    //tDepartItemResultService.remove(departItemResultQueryWrapper);
                    TDepartItemResult tDepartItemResult = new TDepartItemResult();
                    tDepartItemResult.setDelFlag(1);;
                    tDepartItemResultService.update(tDepartItemResult,departItemResultQueryWrapper);
                }

                //修改数据(体检人员)
                tGroupPerson.setUpdateId(securityUtil.getCurrUser().getId());
                tGroupPerson.setUpdateTime(new Date());
                tGroupPerson.setAvatar(null);
                if("职业体检".equals(tGroupPerson.getPhysicalType())){
                    tGroupPerson.setWorkStateText(tGroupPerson.getWorkStateText().trim());
                }
                boolean res = tGroupPersonService.updateById(tGroupPerson);
                boolean flag = false;
                if(res && oldGroupPerson.getIsWzCheck() == 1){
                    //所有检查项       //查询复查检查项和已检项
                    Integer count = itemService.getAllCheckCount(personId, newGroupId);
                    Integer count1 = itemService.getDepartResultCount(personId, newGroupId);
                    //弃检记录
                    QueryWrapper<RelationPersonProjectCheck> checkQueryWrapper = new QueryWrapper<>();
                    checkQueryWrapper.eq("state", 2);
                    checkQueryWrapper.eq("person_id", personId);
                    int count2 = iRelationPersonProjectCheckService.count(checkQueryWrapper);

                    //没检查完  修改状态为2，到分诊
                    if (count1.intValue() < (count.intValue() - count2)) {
                        //检查人员信息
                        TGroupPerson byId = new TGroupPerson();
                        byId.setId(personId);
                        byId.setIsPass(2);
                        byId.setUpdateId(securityUtil.getCurrUser().getId());
                        byId.setUpdateTime(new Date());
                        tGroupPersonService.updateById(byId);
                        flag = true;
                    }else if(count1.intValue() >= (count.intValue() - count2)){//检查完了  修改状态为已检状态 即>2
                        //检查人员信息
                        TGroupPerson byId = new TGroupPerson();
                        if(tGroupPerson.getIsPass() < 3){//检查状态为未检完时
                            byId.setId(personId);
                            byId.setIsPass(3);
                            byId.setUpdateId(securityUtil.getCurrUser().getId());
                            byId.setUpdateTime(new Date());
                            tGroupPersonService.updateById(byId);
                            flag = true;
                        }
                    }
                    return ResultUtil.data(flag, "保存成功");
                }
                if (res) {
                    return ResultUtil.data(res, "修改成功");
                } else {
                    return ResultUtil.error("修改失败");
                }
            }else{
                //修改数据(体检人员)
                tGroupPerson.setUpdateId(securityUtil.getCurrUser().getId());
                tGroupPerson.setUpdateTime(new Date());
                tGroupPerson.setAvatar(null);
                if("职业体检".equals(tGroupPerson.getPhysicalType())){
                    tGroupPerson.setWorkStateText(tGroupPerson.getWorkStateText().trim());
                }
                boolean res = tGroupPersonService.updateById(tGroupPerson);
                if (res) {
                    return ResultUtil.data(res, "修改成功");
                } else {
                    return ResultUtil.error("修改失败");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("保存异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：更新数据(批量审批)
     *
     * @param tGroupPerson 实体
     * @return 返回更新结果
     */
    @SystemLog(description = "更新团检人员数据(批量审批)", type = LogType.OPERATION)
    @ApiOperation("更新团检人员数据")
    @PostMapping("updateTGroupPersonAll")
    public Result<Object> updateTGroupPersonAll(@RequestBody TGroupPerson tGroupPerson) {
        if (StringUtils.isBlank(tGroupPerson.getId()) && (tGroupPerson.getIds() == null || tGroupPerson.getIds().length == 0)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            if (tGroupPerson.getIds().length > 0) {//批量审核 0 未审核 1 已审核
                boolean res = false;
                for (int i = 0; i < tGroupPerson.getIds().length; i++) {
                    tGroupPerson.setUpdateId(securityUtil.getCurrUser().getId());
                    tGroupPerson.setUpdateTime(new Date());
                    tGroupPerson.setId(tGroupPerson.getIds()[i]);
                    tGroupPerson.setStatu(1);
                    res = tGroupPersonService.updateById(tGroupPerson);
                    if (!res) {
                        return ResultUtil.error("修改失败");
                    }
                }
                if (res) {
                    return ResultUtil.data(res, "修改成功");
                }
            }
            tGroupPerson.setUpdateId(securityUtil.getCurrUser().getId());
            tGroupPerson.setUpdateTime(new Date());
            tGroupPerson.setAvatar(null);
            boolean res = tGroupPersonService.updateById(tGroupPerson);
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

    @SystemLog(description = "更新团检人员数据人脸信息", type = LogType.OPERATION)
    @ApiOperation("更新团检人员数据人脸信息")
    @PostMapping("updateTGroupPersonAvatar")
    public Result<Object> updateTGroupPersonAvatar(@RequestBody TGroupPerson tGroupPerson) {
        if (StringUtils.isBlank(tGroupPerson.getId())) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            tGroupPerson.setUpdateId(securityUtil.getCurrUser().getId());
            tGroupPerson.setUpdateTime(new Date());
            if (tGroupPerson.getAvatar() != null && StringUtils.isNotBlank(tGroupPerson.getAvatar().toString())) {
                MultipartFile imgFile = BASE64DecodedMultipartFile.base64ToMultipart(tGroupPerson.getAvatar().toString());
                Blob blob = new SerialBlob(imgFile.getBytes());
                tGroupPerson.setAvatar(blob);
            }

            boolean res = tGroupPersonService.updateById(tGroupPerson);
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
    @ApiOperation("根据主键来删除团检人员数据")
    @SystemLog(description = "根据主键来删除团检人员数据", type = LogType.OPERATION)
    @PostMapping("deleteTGroupPerson")
    public Result<Object> deleteTGroupPerson(@RequestParam String[] ids) {
        if (ids == null || ids.length == 0) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            List<String> strings = Arrays.asList(ids);
            List<TGroupPerson> re = new ArrayList<>();
            for (String str : strings) {
                TGroupPerson byId = tGroupPersonService.getById(str);
                byId.setDelFlag(1);
                re.add(byId);
            }
            boolean res = tGroupPersonService.updateBatchById(re);
            //boolean res = tGroupPersonService.removeByIds(Arrays.asList(ids));
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
     * @param id
     * @return 返回获取结果
     */
    @SystemLog(description = "根据主键来获取团检人员数据", type = LogType.OPERATION)
    @ApiOperation("根据主键来获取团检人员数据")
    @GetMapping("getTGroupPersonWithLink")
    public Result<Object> getTGroupPersonWithLink(String id) {
        if (StringUtils.isBlank(id)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            Map<String, Object> map = tGroupPersonService.getGroupPersonInfo(id, "");
            if ("职业体检".equals(map.get("physical_type").toString())) {
                //查询职业病和目标禁忌症
                TOrderGroup orderGroup = itOrderGroupService.getById(map.get("group_id").toString());
                if (orderGroup != null) {
                    String comboId = orderGroup.getComboId();
                    String occupationalDiseases = "";
                    String occupationalTaboo = "";
                    String occupationalDiseasesCode = "";
                    String occupationalTabooCode = "";
                    if(comboId != null && StringUtils.isNotBlank(comboId)){
                        if (comboId.contains(",")) {
                            String[] split = comboId.split(",");
                            List<TCombo> tCombos = itComboService.listByIds(Arrays.asList(split));

                            for (TCombo tCombo : tCombos) {
                                if(StringUtils.isNotBlank(tCombo.getOccupationalDiseases()) && !";\n".equals(tCombo.getOccupationalDiseases())){
                                    occupationalDiseases += tCombo.getOccupationalDiseases();
                                }
                                if(StringUtils.isNotBlank(tCombo.getOccupationalTaboo()) && !";\n".equals(tCombo.getOccupationalTaboo())){
                                    occupationalTaboo += tCombo.getOccupationalTaboo();
                                }
                                if(StringUtils.isNotBlank(tCombo.getOccupationalDiseasesCode()) && !";\n".equals(tCombo.getOccupationalDiseasesCode())){
                                    occupationalDiseasesCode += tCombo.getOccupationalDiseasesCode() + ";";
                                }
                                if(StringUtils.isNotBlank(tCombo.getOccupationalTabooCode()) && !";\n".equals(tCombo.getOccupationalTabooCode())){
                                    occupationalTabooCode += tCombo.getOccupationalTabooCode() + ";";
                                }
                            }
                        } else {
                            TCombo byId1 = itComboService.getById(comboId);
                            if (byId1 != null) {
                                if(StringUtils.isNotBlank(byId1.getOccupationalDiseases()) && !";\n".equals(byId1.getOccupationalDiseases())){
                                    occupationalDiseases += byId1.getOccupationalDiseases();
                                }
                                if(StringUtils.isNotBlank(byId1.getOccupationalTaboo()) && !";\n".equals(byId1.getOccupationalTaboo())){
                                    occupationalTaboo += byId1.getOccupationalTaboo();
                                }
                                if(StringUtils.isNotBlank(byId1.getOccupationalDiseasesCode()) && !";\n".equals(byId1.getOccupationalDiseasesCode())){
                                    occupationalDiseasesCode += byId1.getOccupationalDiseasesCode();
                                }
                                if(StringUtils.isNotBlank(byId1.getOccupationalTabooCode()) && !";\n".equals(byId1.getOccupationalTabooCode())){
                                    occupationalTabooCode += byId1.getOccupationalTabooCode();
                                }
                            }
                        }
                    }
                    map.put("occupational_diseases", occupationalDiseases);
                    map.put("occupational_taboo", occupationalTaboo);
                    map.put("occupational_diseases_code", occupationalDiseasesCode);
                    map.put("occupational_taboo_code", occupationalTabooCode);
                }
            }
            return ResultUtil.data(map);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：根据主键来获取数据
     *
     * @param id
     * @return 返回获取结果
     */
    @SystemLog(description = "根据主键来获取从业体检人员数据", type = LogType.OPERATION)
    @ApiOperation("根据主键来获取从业体检人员数据")
    @GetMapping("getTGroupPersonById")
    public Result<Object> getTGroupPersonById(String id, String type) {
        if (StringUtils.isBlank(id)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            Map<String, Object> map = tGroupPersonService.getGroupPersonInfo(id, type);
            return ResultUtil.data(map);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：根据主键来获取数据
     *
     * @param id
     * @return 返回获取结果
     */
    @SystemLog(description = "根据主键来获取团检人员数据", type = LogType.OPERATION)
    @ApiOperation("根据主键来获取团检人员数据")
    @GetMapping("getTGroupPerson")
    public Result<Object> getTGroupPerson(@Param("id") String id) {
        if (StringUtils.isBlank(id)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            TGroupPerson byId = tGroupPersonService.getById(id);
            if (byId != null) {
                return ResultUtil.data(byId);
            } else {
                return ResultUtil.error("查询失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：根据订单id来获取数据
     *
     * @param orderId
     * @return 返回获取结果
     */
    @SystemLog(description = "根据订单id来获取团检人员数据", type = LogType.OPERATION)
    @ApiOperation("根据订单id来获取团检人员数据")
    @GetMapping("getTGroupPersonByOrderId")
    public Result<Object> getTGroupPersonByOrderId(@Param("orderId") String orderId) {
        if (StringUtils.isBlank(orderId)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            List<TGroupPerson> byOrderId = tGroupPersonService.getTGroupPersonByOrderId(orderId);
            if (byOrderId != null) {
                return ResultUtil.data(byOrderId);
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
    @SystemLog(description = "分页查询团检人员数据", type = LogType.OPERATION)
    @ApiOperation("分页查询团检人员数据")
    @GetMapping("queryTGroupPersonList")
    public Result<Object> queryTGroupPersonList(TGroupPerson tGroupPerson, SearchVo searchVo, PageVo pageVo) {
        try {
            IPage<TGroupPerson> result = tGroupPersonService.queryTGroupPersonListByPage(tGroupPerson, searchVo, pageVo);
            return ResultUtil.data(result);
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
    @SystemLog(description = "分页查询体检完与已总检人员数据", type = LogType.OPERATION)
    @ApiOperation("分页查询体检完与已总检人员数据")
    @PostMapping("queryStatisticPersonList")
    public Result<Object> queryStatisticPersonList(TGroupPerson tGroupPerson, SearchVo searchVo, PageVo pageVo) {
        try {
            IPage<TGroupPerson> result = tGroupPersonService.queryStatisticPersonList(tGroupPerson, searchVo, pageVo);
            return ResultUtil.data(result);
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
    @SystemLog(description = "分页查询待审核人员数据", type = LogType.OPERATION)
    @ApiOperation("分页查询待审核人员数据")
    @PostMapping("queryExamineFinishPersonList")
    public Result<Object> queryExamineFinishPersonList(TGroupPerson tGroupPerson, SearchVo searchVo, PageVo pageVo) {
        try {
            IPage<TGroupPerson> result = tGroupPersonService.queryExamineFinishPersonList(tGroupPerson, searchVo, pageVo);
            return ResultUtil.data(result);
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
    @SystemLog(description = "分页查询团检人员信息及未检查项目", type = LogType.OPERATION)
    @ApiOperation("分页查询团检人员信息及未检查项目")
    @PostMapping("queryNoCheckProjectPersonList")
    public Result<Object> queryNoCheckProjectPersonList(TGroupPerson tGroupPerson, SearchVo searchVo, PageVo pageVo) {
        try {
            IPage<TGroupPerson> result = tGroupPersonService.queryNoCheckProjectPersonList(tGroupPerson, searchVo, pageVo);
            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：查询
     *
     * @param orderIdList
     * @return 返回获取结果
     */
    @SystemLog(description = "查询单位团检人员数量", type = LogType.OPERATION)
    @ApiOperation("查询单位团检人员数量")
    @PostMapping("getPersonListNum")
    public Result<Object> getPersonListNum(@RequestParam String[] orderIdList, @RequestParam String physicalType) {
        try {
            TGroupPerson result = tGroupPersonService.getPersonListNum(Arrays.asList(orderIdList), physicalType);
            return ResultUtil.data(result);
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
    @SystemLog(description = "总检分页查询团检人员数据", type = LogType.OPERATION)
    @ApiOperation("总检分页查询团检人员数据")
    @GetMapping("getInspectionTGroupPersonList")
    public Result<Object> getInspectionTGroupPersonList(TGroupPerson tGroupPerson, SearchVo searchVo, PageVo pageVo) {
        try {
            IPage<TGroupPerson> result = tGroupPersonService.getInspectionTGroupPersonList(tGroupPerson, searchVo, pageVo);
//            List<TGroupPerson> person = result.getRecords();
//            for (int i = 0; i < person.size(); i++) {
//                List<TReviewProject> reviewProjects = itReviewProjectService.queryNoCheckReviewProject(person.get(i).getOrderId(), person.get(i).getId());
//                if(reviewProjects.size() == 0){
//                    List<TDepartItemResult> list = tDepartItemResultService.getAbnormalResultList(person.get(i).getId());
//                    if(list.size() == 0){
//                        person.get(i).setIsAllChecked(0);
//                    }else {
//                        person.get(i).setIsAllChecked(1);
//                    }
//                }else {
//                    person.get(i).setIsAllChecked(1);
//                }
//            }
//            result.setRecords(person);
            return ResultUtil.data(result);
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
    @SystemLog(description = "总检分页查询团检人员数据(复查)", type = LogType.OPERATION)
    @ApiOperation("总检分页查询团检人员数据(复查)")
    @GetMapping("getInspectionTGroupPersonReviewList")
    public Result<Object> getInspectionTGroupPersonReviewList(TGroupPerson tGroupPerson, SearchVo searchVo, PageVo pageVo) {
        try {
            IPage<TGroupPerson> result = tGroupPersonService.getInspectionTGroupPersonReviewList(tGroupPerson, searchVo, pageVo);
            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：查询
     *
     * @param groupId
     * @return 返回获取结果
     */
    @SystemLog(description = "通过分组id查询团检人员数量", type = LogType.OPERATION)
    @ApiOperation("通过分组id查询团检人员数量")
    @GetMapping("getPersonNumByGroupId")
    public Result<Object> getPersonNumByGroupId(@RequestParam(name = "groupId") String groupId) {
        try {
            TGroupPerson result = tGroupPersonService.getPersonNumByGroupId(groupId);
            if (result != null) {
                TOrderGroup res = itOrderGroupService.getById(groupId);
                result.setGroupPersonNum(res.getPersonCount());
            }
            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：导出数据
     *
     * @param response     请求参数
     * @param tGroupPerson 查询参数
     * @return
     */
    @SystemLog(description = "导出团检人员数据", type = LogType.OPERATION)
    @ApiOperation("导出团检人员数据")
    @PostMapping("/download")
    public void download(HttpServletResponse response, TGroupPerson tGroupPerson) {
        try {
            tGroupPersonService.download(tGroupPerson, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取行业类别编码 经济类型编码
     *
     * @param typeCode
     * @return
     */
    @SystemLog(description = "获取行业类别编码,经济类型编码", type = LogType.OPERATION)
    @ApiOperation("获取行业类别编码,经济类型编码")
    @GetMapping("getTypeCodeByTProType")
    public Result<Object> getTypeCodeByTProType(String typeCode) {
        try {
            QueryWrapper<TProType> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("del_flag", 0);
            List<TProType> list = proTypeService.list(queryWrapper);
            //递归设置子集
            recursion(list);
            List<TProType> children = list.stream().filter(ii->ii.getTypeCode().equals(typeCode) && ii.getParentId().equals("0")).collect(Collectors.toList());
            return ResultUtil.data(children);
        } catch (Exception e) {
            return ResultUtil.error("查询失败");
        }
    }

    /**
     * 递归寻找子集
     */
    public void recursion(List<TProType> proTypes) {
        for (TProType proType : proTypes) {
            List<TProType> children = proTypes.stream().filter(ii->ii.getParentId().equals(proType.getId())).collect(Collectors.toList());
            if (children.size() > 0) {
                proType.setChildren(children);
            }
        }
    }

    /**
     * 功能描述：导入数据
     * <p>
     * 根据不同的体检类型，导入模板不一样
     *
     * @return
     */
    @ApiOperation("导入数据")
    @PostMapping("/importExcel")
    @Transactional(rollbackOn = Exception.class)
    public Result<Object> importExcel(@RequestParam(value = "file") MultipartFile multipartFile
          , String groupId, String orderId) throws Exception {
        File file = FileUtil.toFile(multipartFile);
        InputStream in = new FileInputStream(file);
        Workbook wb = ImportExeclUtil.chooseWorkbook(file.getName(), in);

        TGroupOrder groupOrder = groupOrderService.getById(orderId);
        if (groupOrder == null || groupOrder.getDelFlag() != 0) {
            return ResultUtil.error("订单信息不存在！");
        }
        TOrderGroup orderGroup = itOrderGroupService.getById(groupId);
        if (orderGroup == null || orderGroup.getDelFlag() != 0) {
            return ResultUtil.error("分组信息不存在！");
        }
        QueryWrapper<TGroupPerson> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id", orderId);
        wrapper.eq("group_id", groupId);
        wrapper.eq("del_flag", 0);
        wrapper.ne("is_pass", 1);
        int count = tGroupPersonService.count(wrapper);
        if (count > 0) {
            return ResultUtil.error("该分组人员已进行体检，无法重新导入，请通过新增添加人员");
        }
        //先删除当前分组下的人员
        QueryWrapper<TGroupPerson> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_id", groupId);
        tGroupPersonService.remove(queryWrapper);

        if ("职业体检".equals(groupOrder.getPhysicalType()) || "放射体检".equals(groupOrder.getPhysicalType())) {
            ImportPersonEntity importEntity = new ImportPersonEntity();
            //读取一个对象的信息
            List<ImportPersonEntity> readData = ImportExeclUtil.readDateListT(wb, importEntity, 2, 0);
            if (readData != null && readData.size() > 0) {
                //判断当前数据条数是否超过分组人员上限
                Boolean flag = checkCount(groupId, readData.size());
                if (!flag) {
                    return ResultUtil.error("当前导入人数超出订单总人数，导入失败");
                }
                String regularExpression = "(^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)|" +
                      "(^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}$)";


                //查询出危害因素、工种、在岗状态的数据，匹配编码
                List<TProType> type_001 = getTypeCodeByTProTypeList("TYPE_001");//危害因素
                List<DictData> dictDataList = dictDataService.findByDictId("1447871138549075969");//在岗状态
                List<DictData> workDataList = dictDataService.findByDictId("1456812385326206976");//工种名称

                for (int i = 0; i < readData.size(); i++) {
                    ImportPersonEntity readDatum = readData.get(i);
                    //判断对象属性值是否都不为空
//                    for (Field f : readDatum.getClass().getDeclaredFields()) {
//                        f.setAccessible(true);
//                        if (!"workName".equals(f.getName())) {
//                            if (f.get(readDatum) == null || f.get(readDatum) == "") {
//                                throw new TransactionException("批量导入失败，数据不能为空！");
//                            }
//                        }
//                    }
                    TGroupPerson person = new TGroupPerson();
                    person.setPersonName(readDatum.getPersonName());
                    //person.setSex(readDatum.getSex());
                    /*if (!StringUtils.isNotBlank(readDatum.getIdCard()) || !readDatum.getIdCard().matches(regularExpression)) {
                        throw new TransactionException("批量导入失败，第 " + (i + 1) + " 行身份证不正确！");
                    }

                    //同一个分组下边是否有多个相同的身份证
                    QueryWrapper<TGroupPerson> personQueryWrapper = new QueryWrapper<>();
                    personQueryWrapper.eq("id_card", readDatum.getIdCard());
                    personQueryWrapper.eq("del_flag", 0);
                    personQueryWrapper.eq("group_id", groupId);
                    personQueryWrapper.eq("order_id", orderId);
                    List<TGroupPerson> list = tGroupPersonService.list(personQueryWrapper);
                    if (list.size() > 0) {
                        throw new TransactionException("已导入身份证为" + readDatum.getIdCard() + "的体检人员信息");
                    }*/

                    person.setTestNum(generatorNum(groupOrder.getPhysicalType()));
                    person.setIdCard(readDatum.getIdCard());
                    /*基数为男 偶数为女*/
                    if(readDatum.getIdCard() != null && StringUtils.isNotBlank(readDatum.getIdCard())){
                        if (Integer.parseInt(readDatum.getIdCard().substring(16, 17)) % 2 == 0) {
                            person.setSex("女");
                        } else {
                            person.setSex("男");
                        }
                    }
                    person.setAge(getAgeForIdcard(readDatum.getIdCard()));
                    person.setBirth(getBirthdayForIdcard(readDatum.getIdCard()));
                    person.setMobile(readDatum.getMobile());

                    DictData dictData = workDataList.stream().filter(e -> e.getTitle().equals(readDatum.getWorkTypeText())).findFirst().orElse(null);
                    if (dictData == null) {
                        throw new TransactionException("工种名称 “" + readDatum.getWorkTypeText() + "” 不符合规范");
                    }
                    //如果工种是其他，其他工种名称必填
                    if (("0014".equals(dictData.getValue()) || "0033".equals(dictData.getValue()) || "999999".equals(dictData.getValue())) && StringUtils.isBlank(readDatum.getWorkName())) {
                        throw new TransactionException("工种名称为其他时，其他工种名称不能为空！");
                    }
                    person.setWorkTypeCode(dictData.getValue());
                    person.setWorkTypeText(readDatum.getWorkTypeText());
                    person.setWorkName(readDatum.getWorkName());
                    dictData = dictDataList.stream().filter(e -> e.getTitle().equals(readDatum.getWorkStateText())).findFirst().orElse(null);
                    if (dictData == null) {
                        throw new TransactionException("在岗状态名称 “" + readDatum.getWorkStateText() + "” 不符合规范");
                    }
                    person.setWorkStateCode(dictData.getValue());
                    person.setWorkStateText(readDatum.getWorkStateText().trim());


                    //匹配危害因素，多个|隔开
                    if (readDatum.getHazardFactorsText().contains("|")) {
                        String[] split = readDatum.getHazardFactorsText().split("\\|");
                        String codes = "";
                        for (String s : split) {
                            String s1 = checkTypeCodeByTypeName(type_001, s);
                            if (StringUtils.isBlank(s1)) {
                                throw new TransactionException("危害因素名称 “" + readDatum.getHazardFactorsText() + "” 不符合规范");
                            }
                            codes += "|" + s1;
                        }
                        codes = codes.substring(1);
                        person.setHazardFactors(codes);
                        person.setHazardFactorsText(readDatum.getHazardFactorsText());
                        //如果危害因素是其他，需要填写其他危害因素
                        if ((codes.contains("110999") || codes.contains("120999") || codes.contains("130999")
                              || codes.contains("140999") || codes.contains("150999") || codes.contains("160999")) && StringUtils.isBlank(readDatum.getOtherHazardFactors())) {
                            throw new TransactionException("危害因素名称为其他时，其他危害因素名称不能为空");
                        }
                        person.setOtherHazardFactors(readDatum.getOtherHazardFactors());
                    } else {
                        String s1 = checkTypeCodeByTypeName(type_001, readDatum.getHazardFactorsText());
                        if (StringUtils.isBlank(s1)) {
                            throw new TransactionException("危害因素名称 “" + readDatum.getHazardFactorsText() + "” 不符合规范");
                        }
                        person.setHazardFactors(s1);
                        person.setHazardFactorsText(readDatum.getHazardFactorsText());
                        if ((s1.contains("110999") || s1.contains("120999") || s1.contains("130999")
                              || s1.contains("140999") || s1.contains("150999") || s1.contains("160999")) && StringUtils.isBlank(readDatum.getOtherHazardFactors())) {
                            throw new TransactionException("危害因素名称为其他时，其他危害因素名称不能为空");
                        }
                        person.setOtherHazardFactors(readDatum.getOtherHazardFactors());
                    }
                    person.setOrderId(orderId);
                    person.setGroupId(groupId);
                    person.setPhysicalType(groupOrder.getPhysicalType());
                    person.setCreateId(securityUtil.getCurrUser().getId());
                    person.setCreateTime(new Date());
                    person.setDelFlag(0);
                    person.setIsPass(1);
                    person.setIsCheck(0);
                    person.setIsRecheck(0);
                    person.setIsWzCheck(0);
                    person.setReportPrintingNum(0);
                    person.setUnitId(groupOrder.getGroupUnitId());
                    person.setDept(groupOrder.getGroupUnitName());
                    person.setJcType("1");
                    person.setSporadicPhysical(groupOrder.getSporadicPhysical());
                    tGroupPersonService.save(person);
                }
                return ResultUtil.data("批量导入成功！");
            } else {
                return ResultUtil.error("批量导入失败，数据为空！");
            }
        } else if ("健康体检".equals(groupOrder.getPhysicalType())) {
            ImportHealthyPersonEntity importEntity = new ImportHealthyPersonEntity();
            //读取一个对象的信息
            List<ImportHealthyPersonEntity> readData = ImportExeclUtil.readDateListT(wb, importEntity, 2, 0);
            if (readData != null && readData.size() > 0) {
                //判断当前数据条数是否超过分组人员上限
                Boolean flag = checkCount(groupId, readData.size());
                if (!flag) {
                    return ResultUtil.error("当前导入人数超出订单总人数，导入失败");
                }
                String regularExpression = "(^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)|" +
                      "(^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}$)";
                for (int i = 0; i < readData.size(); i++) {
                    ImportHealthyPersonEntity readDatum = readData.get(i);
                    //判断对象属性值是否都不为空
                    /*for (Field f : readDatum.getClass().getDeclaredFields()) {
                        f.setAccessible(true);
                        if (f.get(readDatum) == null || f.get(readDatum) == "") {
                            throw new TransactionException("批量导入失败，数据不能为空！");
                        }
                    }*/
                    /*if (!StringUtils.isNotBlank(readDatum.getIdCard()) || !readDatum.getIdCard().matches(regularExpression)) {
                        throw new TransactionException("批量导入失败，第 " + (i + 1) + " 行身份证不正确！");
                    }*/
                    TGroupPerson person = new TGroupPerson();
                    person.setTestNum(generatorNum(groupOrder.getPhysicalType()));
                    person.setIdCard(readDatum.getIdCard());
                    person.setAge(getAgeForIdcard(readDatum.getIdCard()));
                    person.setBirth(getBirthdayForIdcard(readDatum.getIdCard()));
                    person.setMobile(readDatum.getMobile());
                    person.setPersonName(readDatum.getPersonName());
                    /*基数为男 偶数为女*/
                    if(readDatum.getIdCard() != null && StringUtils.isNotBlank(readDatum.getIdCard())){
                        if (Integer.parseInt(readDatum.getIdCard().substring(16, 17)) % 2 == 0) {
                            person.setSex("女");
                        } else {
                            person.setSex("男");
                        }
                    }
                    person.setOrderId(orderId);
                    person.setGroupId(groupId);
                    person.setPhysicalType(groupOrder.getPhysicalType());
                    person.setCreateId(securityUtil.getCurrUser().getId());
                    person.setCreateTime(new Date());
                    person.setDelFlag(0);
                    person.setIsPass(1);
                    person.setIsCheck(0);
                    person.setIsRecheck(0);
                    person.setIsWzCheck(1);
                    person.setReportPrintingNum(0);
                    person.setUnitId(groupOrder.getGroupUnitId());
                    person.setDept(groupOrder.getGroupUnitName());
                    person.setOldGroupId(groupId);
                    tGroupPersonService.save(person);
                }
                return ResultUtil.data("批量导入成功！");
            } else {
                return ResultUtil.error("批量导入失败，数据为空！");
            }
        } else {
            return ResultUtil.error("体检类型不明确！");
        }

    }

    /**
     * 根据身份证的号码算出当前身份证持有者的年龄
     *
     * @param
     * @throws Exception
     */
    public static int getAgeForIdcard(String idcard) {
        try {
            int age = 0;
            if (StringUtils.isBlank(idcard)) {
                return age;
            }
            String birth = "";
            if (idcard.length() == 18) {
                birth = idcard.substring(6, 14);
            } else if (idcard.length() == 15) {
                birth = "19" + idcard.substring(6, 12);
            }
            int year = Integer.valueOf(birth.substring(0, 4));
            int month = Integer.valueOf(birth.substring(4, 6));
            int day = Integer.valueOf(birth.substring(6));
            Calendar cal = Calendar.getInstance();
            age = cal.get(Calendar.YEAR) - year;
            //周岁计算
            if (cal.get(Calendar.MONTH) < (month - 1) || (cal.get(Calendar.MONTH) == (month - 1) && cal.get(Calendar.DATE) < day)) {
                age--;
            }
            return age;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 身份证提取出身日期
     *
     * @param card
     * @return
     * @throws Exception
     */
    public static Date getBirthdayForIdcard(String card) throws Exception {
        Date b = null;
        if (card.length() == 18) {
            String year = card.substring(6).substring(0, 4);// 得到年份
            String yue = card.substring(10).substring(0, 2);// 得到月份
            String ri = card.substring(12).substring(0, 2);// 得到日
            // String day=CardCode.substring(12).substring(0,2);//得到日
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            b = format.parse(year + "-" + yue + "-" + ri);
        } else if (card.length() == 15) {
            String uyear = "19" + card.substring(6, 8);// 年份
            String uyue = card.substring(8, 10);// 月份
            String uri = card.substring(10, 12);// 得到日
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            b = format.parse(uyear + "-" + uyue + "-" + uri);
        }
        return b;
    }

    /**
     * 功能描述：根据当前登录医生的科室 查询对应的体检人员
     * 人 订单 分组 项目 科室
     *
     * @return
     */
    @SystemLog(description = "", type = LogType.OPERATION)
    @ApiOperation("根据当前登录医生的科室，查询对应的体检人员")
    @GetMapping("/getPersonByOfficeId")
    public Result<Object> getPersonByOfficeId(TGroupPerson tGroupPerson, PageVo pageVo, SearchVo searchVo) {
        try {
            //科室id
            List<String> officeId = securityUtil.getDeparmentIds();
            if (tGroupPerson.getIsWzCheck() != null) {
                officeId = null;
            }
            IPage<TGroupPerson> personByOfficeId = tGroupPersonService.getPersonByOfficeId(officeId, tGroupPerson, pageVo, searchVo);
            return ResultUtil.data(personByOfficeId);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：根据当前登录医生的科室 查询对应的体检人员
     * 人 订单 分组 项目 科室
     *
     * @return
     */
    @SystemLog(description = "", type = LogType.OPERATION)
    @ApiOperation("根据当前登录医生的科室，查询对应的复查体检人员")
    @GetMapping("/getPersonReviewerByOfficeId")
    public Result<Object> getPersonReviewerByOfficeId(TGroupPerson tGroupPerson, PageVo pageVo, SearchVo searchVo) {
        try {
            //科室id
            List<String> officeId = securityUtil.getDeparmentIds();
            if (tGroupPerson.getIsWzCheck() != null) {
                officeId = null;
            }
            IPage<TGroupPerson> personByOfficeId = tGroupPersonService.getPersonReviewerByOfficeId(officeId, tGroupPerson, pageVo, searchVo);
            return ResultUtil.data(personByOfficeId);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }



    /**
     * 功能描述：根据id获取团检人员关联信息
     *
     * @param id
     * @return
     */
    @SystemLog(description = "", type = LogType.OPERATION)
    @ApiOperation("根据id获取团检人员关联信息")
    @GetMapping("/getGroupPersonByIdWithLink")
    public Result<Object> getGroupPersonByIdWithLink(String id) {
        if (StringUtils.isBlank(id)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            Map<String, Object> map = tGroupPersonService.getGroupPersonByIdWithLink(id);
            return ResultUtil.data(map);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("导入异常:" + e.getMessage());
        }
    }

    /**
     * 生成体检编号-根据当前年月日+0001开头生成12位不重复的数
     *
     * @return
     */
    public String generatorNum(String type) {
        QueryWrapper<TGroupPerson> queryWrapper = new QueryWrapper<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String currentDay = format.format(new Date());
        queryWrapper.apply(StringUtils.isNotBlank(currentDay), "Date(create_time)=STR_TO_Date('" + currentDay + "','%Y-%m-%d')");
        queryWrapper.eq("physical_type", type);
        queryWrapper.orderByDesc("create_time").orderByDesc("test_num");
        queryWrapper.last("limit 1");
        queryWrapper.select("id,test_num");
        TGroupPerson one = tGroupPersonService.getOne(queryWrapper);
        DateFormat df = new SimpleDateFormat("yyyyMMdd");

        String testNum = "";
        if ("职业体检".equals(type)) {
            testNum = "1";
        } else if ("健康体检".equals(type)) {
            testNum = "2";
        } else if ("从业体检".equals(type)) {
            testNum = "3";
        } else if ("放射体检".equals(type)) {
            testNum = "4";
        } else {
            testNum = "5";
        }
        if (one == null) {
            testNum += df.format(new Date());
            testNum += "0001";
        } else {
            String substring = one.getTestNum().substring(one.getTestNum().length() - 4);
            int i = Integer.valueOf(substring);
            i += 1;
            String code = String.valueOf(i);
            if (code.length() == 1) {
                code = "000" + code;
            } else if (code.length() == 2) {
                code = "00" + code;
            } else if (code.length() == 3) {
                code = "0" + code;
            }
            testNum += df.format(new Date());
            testNum += code;
        }
        return testNum;
    }

    /**
     * 功能描述：实现分页查询
     *
     * @param searchVo 需要模糊查询的信息
     * @param pageVo   分页参数
     * @return 返回获取结果
     */
    @SystemLog(description = "分页查询团检人员数据", type = LogType.OPERATION)
    @ApiOperation("分页查询团检人员数据")
    @GetMapping("getTGroupPersonInspection")
    public Result<Object> getTGroupPersonInspectionList(TGroupPerson tGroupPerson, SearchVo searchVo, PageVo pageVo) {
        try {
            IPage<TGroupPerson> result = tGroupPersonService.getTGroupPersonInspection(tGroupPerson, searchVo, pageVo);
            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }


    @SystemLog(description = "", type = LogType.OPERATION)
    @ApiOperation("根据身份证查询体检人员信息")
    @GetMapping("/getTGroupPersonAndUnit")
    public Result<Object> getTGroupPersonAndUnit(String id) {
        TGroupPerson person = tGroupPersonService.getById(id);
        HashMap<String, Object> hashMap = new HashMap<>();
        if (person != null) {
            String orderId = person.getOrderId();
            Map<String, Object> name = groupOrderService.getComNameByGroupId(person.getGroupId());
            if (name != null) {
                person.setComboName(name.get("name").toString());
                person.setComboId(name.get("id").toString());
            }
            TGroupOrder byId = groupOrderService.getById(orderId);
            if (byId != null) {
                TGroupUnit groupUnit = itGroupUnitService.getById(byId.getGroupUnitId());
                if (groupUnit != null) {
                    person.setUnitName(groupUnit.getName());
                }
            }
//            person.setUnitName(person.getDept());

            if ("职业体检".equals(person.getPhysicalType())) {
                //查询职业病和目标禁忌症
                TOrderGroup orderGroup = itOrderGroupService.getById(person.getGroupId());
                if (orderGroup != null) {
                    String comboId = orderGroup.getComboId();
                    String occupationalDiseases = "";
                    String occupationalTaboo = "";
                    if (comboId.contains(",")) {
                        String[] split = comboId.split(",");
                        List<TCombo> tCombos = itComboService.listByIds(Arrays.asList(split));

                        for (TCombo tCombo : tCombos) {
                            occupationalDiseases += "," + tCombo.getOccupationalDiseases();
                            occupationalTaboo += "," + tCombo.getOccupationalTaboo();
                        }
                        if (StringUtils.isNotBlank(occupationalDiseases)) {
                            occupationalDiseases = occupationalDiseases.substring(1);
                        }
                        if (StringUtils.isNotBlank(occupationalTaboo)) {
                            occupationalTaboo = occupationalTaboo.substring(1);
                        }
                    } else {
                        TCombo byId1 = itComboService.getById(comboId);
                        if (byId1 != null) {
                            occupationalDiseases = byId1.getOccupationalDiseases();
                            occupationalTaboo = byId1.getOccupationalTaboo();
                        }
                    }
                    person.setOccupationalDiseases(occupationalDiseases);
                    person.setOccupationalTaboo(occupationalTaboo);
                }
            }
            hashMap.put("personData", person);
            //关联查询分组项目
            String groupId = person.getGroupId();
            QueryWrapper<TOrderGroupItem> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("group_id", groupId);
            queryWrapper.orderByAsc("project_type");
            queryWrapper.orderByAsc("office_id");
            queryWrapper.orderByAsc("order_num");
            List<TOrderGroupItem> projectData = itOrderGroupItemService.queryDataListByGroupId(queryWrapper, id);

            hashMap.put("projectData", projectData);
            return ResultUtil.data(hashMap);
        } else {
            return ResultUtil.error("未找到当前体检人员信息！");
        }
    }

    private Boolean checkCount(String groupId, int count) {
        TOrderGroup byId = itOrderGroupService.getById(groupId);
        if (byId != null) {
            if (count > byId.getPersonCount()) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    @SystemLog(description = "生成当前人检验科的条形码", type = LogType.OPERATION)
    @ApiOperation("生成当前人检验科的条形码")
    @GetMapping("/generatorBarcode")
    @Transactional(rollbackOn = Exception.class)
    public Result<Object> generatorBarcode(String personId, String testNum, boolean isReviewer,String passStatus,String  projectIds) {
        try {
            TGroupPerson person = tGroupPersonService.getById(personId);
            List<String> listItemId = new ArrayList<>();
            if (person != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                ArrayList<String> list = new ArrayList<>();
                //非复查
                if (!isReviewer) {
                    QueryWrapper<TOrderGroupItem> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("group_id", person.getGroupId());
                    //生成检验科
                    queryWrapper.and(i -> i.eq("office_id", "186"));
                    //只查询检验科的项目来生成条形码
                    List<TOrderGroupItem> itemList = itOrderGroupItemService.list(queryWrapper);
                    for (TOrderGroupItem tOrderGroupItem : itemList) {
                        //2021-12-20 条码生成规则调整  除血常规、return ResultUtil.error("修改失败");外的用名称匹配 共用一个条形码
                        String barcodeStr = "";
                        int type = 1;
                        QueryWrapper<TBarcode> wrapper = new QueryWrapper<>();
                        wrapper.eq("person_id", personId);
                        String codeName = "";
                        if (tOrderGroupItem.getName().contains("血脂") || tOrderGroupItem.getName().contains("血糖")
                              || tOrderGroupItem.getName().contains("肝功") || tOrderGroupItem.getName().contains("肾功")|| tOrderGroupItem.getName().contains("ALT")) {
                            codeName = "生化";
                            wrapper.eq("group_item_id", "99999999999999999999999999999999");
                        } else {
                            wrapper.eq("group_item_id", tOrderGroupItem.getId());
                            codeName = tOrderGroupItem.getName();
                        }
                        wrapper.eq("test_num", testNum);
                        wrapper.orderByDesc("create_time");
                        if (tOrderGroupItem.getPortfolioProjectId().equals("954")) {
                            wrapper.and(i -> i.eq("type", 3));
                        }
                        else{
                            wrapper.and(i -> i.eq("type", 1));
                        }
                        wrapper.last("LIMIT 1");
                        if(socketConfig.getLisCode()){
                            String itemId = codeName.equals("生化") ? "99999999999999999999999999999999" : tOrderGroupItem.getId();
                            if(listItemId!= null &&  listItemId.indexOf(itemId)==-1){
                                listItemId.add(itemId);
                                barcodeStr = testNum.substring(3);
                                String base64Barcode = GoogleBarCodeUtils.generatorBase64Barcode(barcodeStr, person.getPersonName() + "-" + codeName);
                                if (!list.contains(base64Barcode)) {
                                    list.add(base64Barcode);
                                }
                            }
                            continue;

                        }
                        TBarcode one = itBarcodeService.getOne(wrapper);
                        if (one != null) {
                            String base64Barcode = GoogleBarCodeUtils.generatorBase64Barcode(one.getBarcode(), person.getPersonName() + "-" + codeName);
                            if (!list.contains(base64Barcode)) {
                                list.add(base64Barcode);
                            }
                        } else {

                            //判断如果是尿常规，需生成6位数的条形码
                            if (tOrderGroupItem.getPortfolioProjectId().equals("954")) {
                                barcodeStr = genRandomBarcode();
                                type = 3;
                            } else {
                                TBarcode lastBarcode = itBarcodeService.getOneByWhere();
                                if (lastBarcode == null) {
                                    barcodeStr = dateFormat.format(new Date()).substring(2);
                                    barcodeStr += "0001";
                                } else {
                                    String substring = lastBarcode.getBarcode().substring(lastBarcode.getBarcode().length() - 4);
                                    int i = Integer.parseInt(substring);
                                    i += 1;
                                    String code = String.valueOf(i);
                                    if (code.length() == 1) {
                                        code = "000" + code;
                                    } else if (code.length() == 2) {
                                        code = "00" + code;
                                    } else if (code.length() == 3) {
                                        code = "0" + code;
                                    }
                                    barcodeStr = dateFormat.format(new Date()).substring(2);
                                    barcodeStr += code;
                                }
                            }
                            TBarcode barcode = new TBarcode();
                            barcode.setPersonId(personId);
                            barcode.setGroupItemId(codeName.equals("生化") ? "99999999999999999999999999999999" : tOrderGroupItem.getId());
                            barcode.setBarcode(barcodeStr);
                            barcode.setCreateTime(new Date());
                            barcode.setType(type);
                            barcode.setTestNum(testNum);

                            boolean save = itBarcodeService.save(barcode);
                            if (save) {
                                String base64Barcode = GoogleBarCodeUtils.generatorBase64Barcode(barcodeStr, person.getPersonName() + "-" + codeName);
                                if (!list.contains(base64Barcode)) {
                                    list.add(base64Barcode);
                                }
                            } else {
                                throw new ScmtException("条码生成失败！");
                            }
                        }
                    }
                } else {
                    //复查
                    QueryWrapper<TReviewProject> projectQueryWrapper = new QueryWrapper<>();
//                    projectQueryWrapper.eq("test_num", testNum);
                    projectQueryWrapper.eq("person_id", personId);
                    /*if(passStatus.equals("未登记")){
                        projectQueryWrapper.eq("is_pass", 1);
                    }else{
                        projectQueryWrapper.eq("is_pass", 2);
                    }*/
                    projectQueryWrapper.eq("del_flag", 0);
                    //只生成当前导检单里面的检查项目条码
                    List<String> idList = new ArrayList<>();
                    if(projectIds.indexOf(",") > -1){
                        String[] strings = projectIds.split(",");
                        for (String string : strings){
                            idList.add(string);
                        }
                    }else{
                        idList.add(projectIds);
                    }
                    projectQueryWrapper.in("id",idList);
//                    projectQueryWrapper.groupBy("test_num");
                    //生成检验科 肺功能 电测听 心电图 DR CT B超
                    projectQueryWrapper.and(i -> i.eq("office_id", "186").or().eq("office_id","1456834407368364032").or().eq("office_id","202").or().eq("office_id","194").or().eq("office_id","173").or().eq("office_id","207").or().eq("office_id","204"));
                    //只查询检验科的项目来生成条形码
                    List<TReviewProject> reviewProjects = itReviewProjectService.list(projectQueryWrapper);
                    for (TReviewProject reviewProject : reviewProjects) {
                        String barcodeStr = "";
                        int type = 1;
                        QueryWrapper<TBarcode> wrapper = new QueryWrapper<>();
                        wrapper.eq("person_id", personId);

                        String codeName = "";
                        if (reviewProject.getPortfolioProjectName().contains("血脂") || reviewProject.getPortfolioProjectName().contains("血糖")
                              || reviewProject.getPortfolioProjectName().contains("肝功") || reviewProject.getPortfolioProjectName().contains("肾功")|| reviewProject.getPortfolioProjectName().contains("ALT")) {
                            codeName = "生化(复)";
                            wrapper.eq("group_item_id", "99999999999999999999999999999998");
                        } else {
                            wrapper.eq("group_item_id", reviewProject.getId());
                            codeName = reviewProject.getPortfolioProjectName();
                        }
                        wrapper.eq("test_num", testNum);
                        wrapper.orderByDesc("create_time");
                        if (reviewProject.getPortfolioProjectId().equals("954")) {
                            wrapper.and(i -> i.eq("type", 3));
                        }
                        else{
                            wrapper.and(i -> i.eq("type", 1));
                        }
                        wrapper.last("LIMIT 1");
                        TBarcode one = itBarcodeService.getOne(wrapper);
                        if (one != null) {
                            String base64Barcode = GoogleBarCodeUtils.generatorBase64Barcode(one.getBarcode(), person.getPersonName() + "-" + codeName);
                            if (!list.contains(base64Barcode)) {
                                list.add(base64Barcode);
                            }
                        } else {
                            //判断如果是尿常规，需生成6位数的条形码
                            if (reviewProject.getPortfolioProjectId().equals("954")) {
                                barcodeStr = genRandomBarcode();
                                type = 3;
                            } else {
                                String codeStr = dateFormat.format(new Date()).substring(2);
                                TBarcode lastBarcode = itBarcodeService.getOneByWhere();
                                if (lastBarcode == null) {
                                    barcodeStr = dateFormat.format(new Date()).substring(2);
                                    barcodeStr += "0001";
                                } else {
                                    String substring = lastBarcode.getBarcode().substring(codeStr.length(),codeStr.length()+4);
                                    int i = Integer.parseInt(substring);
                                    i += 1;
                                    String code = String.valueOf(i);
                                    if (code.length() == 1) {
                                        code = "000" + code;
                                    } else if (code.length() == 2) {
                                        code = "00" + code;
                                    } else if (code.length() == 3) {
                                        code = "0" + code;
                                    }
                                    barcodeStr = dateFormat.format(new Date()).substring(2);
                                    barcodeStr += code;
                                }
                            }
                            TBarcode barcode = new TBarcode();
                            barcode.setPersonId(personId);
                            barcode.setGroupItemId(codeName.equals("生化(复)") ? "99999999999999999999999999999998" : reviewProject.getId());
                            /*if(!reviewProject.getOfficeId().equals("186")){
                                barcodeStr = barcodeStr + "000";
                            }*/
                            barcode.setBarcode(barcodeStr);
                            barcode.setCreateTime(new Date());
                            barcode.setType(type);
                            barcode.setTestNum(testNum);
                            boolean save = itBarcodeService.save(barcode);
                            if (save) {
                                String base64Barcode = GoogleBarCodeUtils.generatorBase64Barcode(barcodeStr, person.getPersonName() + "-" + codeName);
                                if (!list.contains(base64Barcode)) {
                                    list.add(base64Barcode);
                                }
                            } else {
                                throw new ScmtException("条码生成失败！");
                            }
                        }
                    }
                }
                return ResultUtil.data(list);
            } else {
                return ResultUtil.error("未找到当前体检人员信息！");
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            return ResultUtil.error(e.getMessage());
        }
    }

    @SystemLog(description = "生成当前人检验科的所有条形码", type = LogType.OPERATION)
    @ApiOperation("生成当前人检验科的所有条形码")
    @GetMapping("/getAllGeneratorBarcode")
    @Transactional(rollbackOn = Exception.class)
    public Result<Object> getAllGeneratorBarcode(String personId, String testNum) throws IOException {
        try {
            TGroupPerson person = tGroupPersonService.getById(personId);
            if (person != null) {
                ArrayList<String> list = new ArrayList<>();
                QueryWrapper<TOrderGroupItem> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("group_id", person.getGroupId());
                //生成检验科
                queryWrapper.and(i -> i.eq("office_id", "186"));
                //只查询检验科的项目来生成条形码
                List<TOrderGroupItem> itemList = itOrderGroupItemService.list(queryWrapper);
                for (TOrderGroupItem tOrderGroupItem : itemList) {
                    //2021-12-20 条码生成规则调整  除血常规、return ResultUtil.error("修改失败");外的用名称匹配 共用一个条形码
                    QueryWrapper<TBarcode> wrapper = new QueryWrapper<>();
                    wrapper.eq("person_id", personId);
                    String codeName = "";
                    if (tOrderGroupItem.getName().contains("血脂") || tOrderGroupItem.getName().contains("血糖")
                            || tOrderGroupItem.getName().contains("肝功") || tOrderGroupItem.getName().contains("肾功")|| tOrderGroupItem.getName().contains("ALT")) {
                        codeName = "生化";
                        wrapper.eq("group_item_id", "99999999999999999999999999999999");
                    } else {
                        wrapper.eq("group_item_id", tOrderGroupItem.getId());
                        codeName = tOrderGroupItem.getName();
                    }
                    wrapper.eq("test_num", testNum);
                    wrapper.orderByDesc("create_time");
                    if (tOrderGroupItem.getPortfolioProjectId().equals("954")) {
                        wrapper.and(i -> i.eq("type", 3));
                    }
                    else{
                        wrapper.and(i -> i.eq("type", 1));
                    }
                    wrapper.last("LIMIT 1");
                    TBarcode one = itBarcodeService.getOne(wrapper);
                    if (one != null) {
                        String base64Barcode = GoogleBarCodeUtils.generatorBase64Barcode(one.getBarcode(), person.getPersonName() + "-" + codeName);
                        if (!list.contains(base64Barcode)) {
                            list.add(base64Barcode);
                        }
                    }
                    //复查
                    QueryWrapper<TReviewProject> projectQueryWrapper = new QueryWrapper<>();
                    projectQueryWrapper.eq("person_id", personId);
                    projectQueryWrapper.eq("del_flag", 0);

                    //生成检验科 肺功能 电测听 心电图 DR CT B超
                    projectQueryWrapper.and(i -> i.eq("office_id", "186").or().eq("office_id","1456834407368364032").or().eq("office_id","202").or().eq("office_id","194").or().eq("office_id","173").or().eq("office_id","207").or().eq("office_id","204"));
                    //只查询检验科的项目来生成条形码
                    List<TReviewProject> reviewProjects = itReviewProjectService.list(projectQueryWrapper);
                    if(reviewProjects.size() > 0) {
                        for (TReviewProject reviewProject : reviewProjects) {
                            QueryWrapper<TBarcode> wrapper1 = new QueryWrapper<>();
                            wrapper1.eq("person_id", personId);

                            String codeName1 = "";
                            if (reviewProject.getPortfolioProjectName().contains("血脂") || reviewProject.getPortfolioProjectName().contains("血糖")
                                    || reviewProject.getPortfolioProjectName().contains("肝功") || reviewProject.getPortfolioProjectName().contains("肾功") || reviewProject.getPortfolioProjectName().contains("ALT")) {
                                codeName1 = "生化";
                                wrapper1.eq("group_item_id", "99999999999999999999999999999999");
                            } else {
                                wrapper1.eq("group_item_id", reviewProject.getId());
                                codeName1 = reviewProject.getPortfolioProjectName();
                            }
                            wrapper1.eq("test_num", testNum);
                            wrapper1.orderByDesc("create_time");
                            if (reviewProject.getPortfolioProjectId().equals("954")) {
                                wrapper1.and(i -> i.eq("type", 3));
                            } else {
                                wrapper1.and(i -> i.eq("type", 1));
                            }
                            wrapper1.last("LIMIT 1");
                            TBarcode one1 = itBarcodeService.getOne(wrapper1);
                            if (one1 != null) {
                                String base64Barcode = GoogleBarCodeUtils.generatorBase64Barcode(one1.getBarcode(), person.getPersonName() + "-" + codeName1);
                                if (!list.contains(base64Barcode)) {
                                    list.add(base64Barcode);
                                }
                            }
                        }
                    }
                }
                return ResultUtil.data(list);
            } else {
                return ResultUtil.error("未找到当前体检人员信息！");
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            return ResultUtil.error(e.getMessage());
        }
    }

    @SystemLog(description = "生成当前人体检编号的条形码", type = LogType.OPERATION)
    @ApiOperation("生成当前人体检编号的条形码")
    @GetMapping("/testNumGeneratorBarcode")
    @Transactional(rollbackOn = Exception.class)
    public Result<Object> testNumGeneratorBarcode(String personId, String testNum) throws IOException {
        TGroupPerson person = tGroupPersonService.getById(personId);
        if (person != null) {
            String testNumCode = "";
            QueryWrapper<TBarcode> wrapper = new QueryWrapper<>();
            wrapper.eq("person_id", personId);
            wrapper.eq("barcode", testNum);
            wrapper.eq("type", 2);
            wrapper.eq("test_num", testNum);
            wrapper.last("LIMIT 1");
            wrapper.orderByDesc("create_time");
            TBarcode one = itBarcodeService.getOne(wrapper);
            if (one != null) {
                testNumCode = (GoogleTestNumBarCodeUtils.generatorBase64Barcode(one.getBarcode(), testNum));
            } else {
                TBarcode barcode = new TBarcode();
                barcode.setPersonId(personId);
                barcode.setBarcode(testNum);
                barcode.setCreateTime(new Date());
                barcode.setType(2);
                barcode.setTestNum(testNum);
                boolean save = itBarcodeService.save(barcode);
                if (save) {
                    testNumCode = (GoogleTestNumBarCodeUtils.generatorBase64Barcode(testNum, testNum));
                } else {
                    throw new ScmtException("条码生成失败！");
                }
            }
            return ResultUtil.data(testNumCode.split(",")[1]);
        } else {
            return ResultUtil.error("未找到当前体检人员信息！");
        }
    }

    @SystemLog(description = "获取当前人体检编号的条形码", type = LogType.OPERATION)
    @ApiOperation("获取当前人体检编号的条形码")
    @GetMapping("/getTestNumGeneratorBarcode")
    @Transactional(rollbackOn = Exception.class)
    public Result<Object> getTestNumGeneratorBarcode(String personId, String testNum) throws IOException {
        String testNumCode = "";
        QueryWrapper<TBarcode> wrapper = new QueryWrapper<>();
        wrapper.eq("person_id", personId);
        wrapper.eq("type", 2);
        wrapper.eq("test_num", testNum);
        TBarcode one = itBarcodeService.getOne(wrapper);
        if (one != null) {
            testNumCode = (GoogleTestNumBarCodeUtils.generatorBase64Barcode(one.getBarcode(), testNum));
        } else {
            return ResultUtil.error("未找到当前体检人员信息！");
        }
        return ResultUtil.data(testNumCode);
    }

    @SystemLog(description = "", type = LogType.OPERATION)
    @ApiOperation("问诊科查询体检人员信息")
    @GetMapping("/getTGroupPersonInfo")
    public Result<Object> getTGroupPersonInfo(String id) {
        TGroupPerson person = tGroupPersonService.getById(id);
        if (person != null) {
            TGroupUnit groupUnit = itGroupUnitService.getById(person.getUnitId());
            if (groupUnit != null) {
                person.setUnitName(groupUnit.getName());
            }
            if ("职业体检".equals(person.getPhysicalType())) {
                //查询职业病和目标禁忌症
                TOrderGroup orderGroup = itOrderGroupService.getById(person.getGroupId());
                if (orderGroup != null) {
                    String comboId = orderGroup.getComboId();
                    String occupationalDiseases = "";
                    String occupationalTaboo = "";
                    if (comboId.contains(",")) {
                        String[] split = comboId.split(",");
                        List<TCombo> tCombos = itComboService.listByIds(Arrays.asList(split));

                        for (TCombo tCombo : tCombos) {
                            occupationalDiseases += "," + tCombo.getOccupationalDiseases();
                            occupationalTaboo += "," + tCombo.getOccupationalTaboo();
                        }
                        if (StringUtils.isNotBlank(occupationalDiseases)) {
                            occupationalDiseases = occupationalDiseases.substring(1);
                        }
                        if (StringUtils.isNotBlank(occupationalTaboo)) {
                            occupationalTaboo = occupationalTaboo.substring(1);
                        }
                    } else {
                        TCombo byId1 = itComboService.getById(comboId);
                        if (byId1 != null) {
                            occupationalDiseases = byId1.getOccupationalDiseases();
                            occupationalTaboo = byId1.getOccupationalTaboo();
                        }
                    }
                    person.setEiaFactors(orderGroup.getEiaFactors());
                    person.setOccupationalDiseases(occupationalDiseases);
                    person.setOccupationalTaboo(occupationalTaboo);
                }
            }
            QueryWrapper<TCareerHistory> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("person_id", id);
            List<TCareerHistory> list = itCareerHistoryService.list(queryWrapper);
            person.setCareerHistoryData(list);

            QueryWrapper<TPastMedicalHistory> historyQueryWrapper = new QueryWrapper<>();
            historyQueryWrapper.eq("person_id", id);
            person.setPastMedicalHistoryData(itPastMedicalHistoryService.list(historyQueryWrapper));

            QueryWrapper<TSymptom> symptomQueryWrapper = new QueryWrapper<>();
            symptomQueryWrapper.eq("person_id", id);
            person.setSymptomData(itSymptomService.list(symptomQueryWrapper));

            return ResultUtil.data(person);
        } else {
            return ResultUtil.error("未找到当前体检人员信息！");
        }
    }

    @SystemLog(description = "", type = LogType.OPERATION)
    @ApiOperation("问诊科查询体检人员信息")
    @GetMapping("/getTGroupPersonInfoById")
    public Result<Object> getTGroupPersonInfoById(String id) {
        TGroupPerson person = tGroupPersonService.getById(id);
        if (person != null) {
            String orderId = person.getOrderId();
            TGroupOrder byId = groupOrderService.getById(orderId);
            if (byId != null) {
                TGroupUnit groupUnit = itGroupUnitService.getById(byId.getGroupUnitId());
                if (groupUnit != null) {
                    person.setUnitName(groupUnit.getName());
                }
                person.setPhysicalType(byId.getPhysicalType());
            }

            QueryWrapper<TPastMedicalHistory> historyQueryWrapper = new QueryWrapper<>();
            historyQueryWrapper.eq("person_id", id);
            person.setPastMedicalHistoryData(itPastMedicalHistoryService.list(historyQueryWrapper));
            return ResultUtil.data(person);
        } else {
            return ResultUtil.error("未找到当前体检人员信息！");
        }
    }

    @SystemLog(description = "", type = LogType.OPERATION)
    @ApiOperation("查询体检人员问诊信息")
    @GetMapping("/getTGroupPersonWZInfo")
    public Result<Object> getTGroupPersonWZInfo(String id, SearchVo searchVo) {
        TGroupPerson person = tGroupPersonService.getById(id);
        if (person != null) {
            String orderId = person.getOrderId();
            TGroupOrder byId = groupOrderService.getById(orderId);
            if (byId != null) {
                TGroupUnit groupUnit = itGroupUnitService.getById(byId.getGroupUnitId());
                if (groupUnit != null) {
                    person.setUnitName(groupUnit.getName());
                }
                person.setPhysicalType(byId.getPhysicalType());
            }
            QueryWrapper<TCareerHistory> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("person_id", id);
            if (searchVo != null) {
                if (searchVo.getStartDate() != null && searchVo.getEndDate() != null) {
                    queryWrapper.lambda().and(i -> i.between(TCareerHistory::getCreateTime, searchVo.getStartDate(), searchVo.getEndDate()));
                }
            }
            List<TCareerHistory> list = itCareerHistoryService.list(queryWrapper);
            person.setCareerHistoryData(list);

            QueryWrapper<TPastMedicalHistory> historyQueryWrapper = new QueryWrapper<>();
            historyQueryWrapper.eq("person_id", id);
            if (searchVo != null) {
                if (searchVo.getStartDate() != null && searchVo.getEndDate() != null) {
                    historyQueryWrapper.lambda().and(i -> i.between(TPastMedicalHistory::getCreateTime, searchVo.getStartDate(), searchVo.getEndDate()));
                }
            }
            person.setPastMedicalHistoryData(itPastMedicalHistoryService.list(historyQueryWrapper));

            QueryWrapper<TSymptom> symptomQueryWrapper = new QueryWrapper<>();
            symptomQueryWrapper.eq("person_id", id);
            if (searchVo != null) {
                if (searchVo.getStartDate() != null && searchVo.getEndDate() != null) {
                    symptomQueryWrapper.lambda().and(i -> i.between(TSymptom::getCreateTime, searchVo.getStartDate(), searchVo.getEndDate()));
                }
            }
            person.setSymptomData(itSymptomService.list(symptomQueryWrapper));
            return ResultUtil.data(person);
        } else {
            return ResultUtil.error("未找到当前体检人员信息！");
        }
    }


    @SystemLog(description = "问诊科更新团检人员数据", type = LogType.OPERATION)
    @ApiOperation("问诊科更新团检人员数据")
    @PostMapping("updateTGroupPersonByInquiry")
    public Result<Object> updateTGroupPersonByInquiry(@RequestBody TGroupPerson tGroupPerson) {
        if (StringUtils.isBlank(tGroupPerson.getId())) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            TGroupPerson byId = tGroupPersonService.getById(tGroupPerson.getId());
            tGroupPerson.setUpdateId(securityUtil.getCurrUser().getId());
            tGroupPerson.setUpdateTime(new Date());

            if (byId.getIsPass() < 3) {
                //判断其他科室是否都检查完了
                Integer count = itOrderGroupItemService.getAllCheckCount(byId.getId(), byId.getGroupId());
                Integer count1 = itOrderGroupItemService.getDepartResultCount(byId.getId(), byId.getGroupId());

                //弃检项目
                QueryWrapper<RelationPersonProjectCheck> checkQueryWrapper = new QueryWrapper<>();
                checkQueryWrapper.eq("state", 2);
                checkQueryWrapper.eq("person_id", byId.getId());
                int count2 = iRelationPersonProjectCheckService.count(checkQueryWrapper);
                if (count1.intValue() >= (count.intValue() - count2)) {
                    tGroupPerson.setIsPass(3);
                }
            }
            tGroupPerson.setIsWzCheck(1);
            tGroupPerson.setAvatar(null);

            tGroupPerson.setWzCheckDoctor(securityUtil.getCurrUser().getNickname());
            tGroupPerson.setWzCheckTime(new Date());
            tGroupPerson.setWzCheckAutograph(securityUtil.getCurrUser().getAutograph());
            boolean res = tGroupPersonService.updateById(tGroupPerson);
            if (res) {
                QueryWrapper<TCareerHistory> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("person_id", tGroupPerson.getId());
                itCareerHistoryService.remove(queryWrapper);
                if (tGroupPerson.getCareerHistoryData() != null && tGroupPerson.getCareerHistoryData().size() > 0) {
                    tGroupPerson.getCareerHistoryData().forEach(i -> i.setCreateId(securityUtil.getCurrUser().getId()));
                    tGroupPerson.getCareerHistoryData().forEach(i -> i.setCreateName(securityUtil.getCurrUser().getNickname()));
                    itCareerHistoryService.saveBatch(tGroupPerson.getCareerHistoryData());
                }

                QueryWrapper<TPastMedicalHistory> pastMedicalHistoryQueryWrapper = new QueryWrapper<>();
                pastMedicalHistoryQueryWrapper.eq("person_id", tGroupPerson.getId());
                itPastMedicalHistoryService.remove(pastMedicalHistoryQueryWrapper);
                if (tGroupPerson.getPastMedicalHistoryData() != null && tGroupPerson.getPastMedicalHistoryData().size() > 0) {
                    tGroupPerson.getPastMedicalHistoryData().forEach(i -> i.setCreateId(securityUtil.getCurrUser().getId()));
                    itPastMedicalHistoryService.saveBatch(tGroupPerson.getPastMedicalHistoryData());
                }

                QueryWrapper<TSymptom> symptomQueryWrapper = new QueryWrapper<>();
                symptomQueryWrapper.eq("person_id", tGroupPerson.getId());
                itSymptomService.remove(symptomQueryWrapper);
                if (tGroupPerson.getSymptomData() != null && tGroupPerson.getSymptomData().size() > 0) {
                    tGroupPerson.getSymptomData().forEach(i -> i.setCreateId(securityUtil.getCurrUser().getId()));
                    tGroupPerson.getSymptomData().forEach(i -> i.setCreateName(securityUtil.getCurrUser().getNickname()));
                    itSymptomService.saveBatch(tGroupPerson.getSymptomData());
                }
                return ResultUtil.data(res, "修改成功");
            } else {
                return ResultUtil.error("修改失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("保存异常:" + e.getMessage());
        }
    }

    @SystemLog(description = "问诊科更新从业人员数据", type = LogType.OPERATION)
    @ApiOperation("问诊科更新从业人员数据")
    @PostMapping("updatePracticePersonByInquiry")
    public Result<Object> updatePracticePersonByInquiry(@RequestBody TGroupPerson tGroupPerson) {
        if (StringUtils.isBlank(tGroupPerson.getId())) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            TGroupPerson byId = tGroupPersonService.getById(tGroupPerson.getId());
            tGroupPerson.setUpdateId(securityUtil.getCurrUser().getId());
            tGroupPerson.setUpdateTime(new Date());
            if (byId.getIsPass() < 3) {
                //判断其他科室是否都检查完了
                Integer count = itOrderGroupItemService.getAllCheckCount(byId.getId(), byId.getGroupId());
                Integer count1 = itOrderGroupItemService.getDepartResultCount(byId.getId(), byId.getGroupId());

                //弃检项目
                QueryWrapper<RelationPersonProjectCheck> checkQueryWrapper = new QueryWrapper<>();
                checkQueryWrapper.eq("state", 2);
                checkQueryWrapper.eq("person_id", byId.getId());
                int count2 = iRelationPersonProjectCheckService.count(checkQueryWrapper);
                if (count1.intValue() >= (count.intValue() - count2)) {
                    tGroupPerson.setIsPass(3);
                }
            }

            tGroupPerson.setIsWzCheck(1);
            tGroupPerson.setAvatar(null);

            tGroupPerson.setWzCheckDoctor(securityUtil.getCurrUser().getNickname());
            tGroupPerson.setWzCheckTime(new Date());
            tGroupPerson.setWzCheckAutograph(securityUtil.getCurrUser().getAutograph());
            boolean res = tGroupPersonService.updateById(tGroupPerson);
            if (res) {
                QueryWrapper<TPastMedicalHistory> pastMedicalHistoryQueryWrapper = new QueryWrapper<>();
                pastMedicalHistoryQueryWrapper.eq("person_id", tGroupPerson.getId());
                itPastMedicalHistoryService.remove(pastMedicalHistoryQueryWrapper);
                if (tGroupPerson.getPastMedicalHistoryData() != null && tGroupPerson.getPastMedicalHistoryData().size() > 0) {
                    tGroupPerson.getPastMedicalHistoryData().forEach(i -> i.setCreateId(securityUtil.getCurrUser().getId()));
                    itPastMedicalHistoryService.saveBatch(tGroupPerson.getPastMedicalHistoryData());
                }
                return ResultUtil.data(res, "修改成功");
            } else {
                return ResultUtil.error("修改失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("保存异常:" + e.getMessage());
        }
    }

    @ApiOperation("修改复查表人员的登记状态")
    @PostMapping("updateTGroupPersonReviewerById")
    public Result<Object> updateTGroupPersonReviewerById(@RequestBody TReviewProject tReviewProject) {
        if (StringUtils.isBlank(tReviewProject.getId())) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            TReviewProject byId = itReviewProjectService.getById(tReviewProject.getId());
            if (byId != null) {
                QueryWrapper<TReviewProject> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("person_id", byId.getPersonId());
                TReviewProject project = new TReviewProject();
                project.setIsPass(10);
                project.setRegistDate(new Date());
                boolean update = itReviewProjectService.update(project, queryWrapper);
                if (update) {
                    return ResultUtil.data(update, "修改失败");
                } else {
                    return ResultUtil.error("登记失败");
                }
            } else {
                return ResultUtil.error("登记失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("修改异常:" + e.getMessage());
        }
    }


    @ApiOperation("分页查询复查人员数据")
    @GetMapping("getTGroupPersonReviewerList")
    public Result<Object> getTGroupPersonReviewerList(TReviewProject tReviewProject, SearchVo searchVo, PageVo pageVo) {
        try {
            IPage<TReviewProject> result = itReviewProjectService.getTGroupPersonReviewer(tReviewProject, searchVo, pageVo);
            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    @ApiOperation("根据人员id查询对应的人员信息")
    @GetMapping("/getTGroupPersonReviewerById")
    public Result<Object> getTGroupPersonReviewerById(String id,String passStatus) {
        TGroupPerson person = itReviewProjectService.getTGroupPersonReviewerById(id);
        HashMap<String, Object> hashMap = new HashMap<>();
        if (person != null) {
//            TReviewProject reviewProject = itReviewProjectService.getById(id);
//            person.setTestNum(reviewProject.getTestNum());
            String orderId = person.getOrderId();
            TGroupOrder byId = groupOrderService.getById(orderId);
            if (byId != null) {
                TGroupUnit groupUnit = itGroupUnitService.getById(byId.getGroupUnitId());
                if (groupUnit != null) {
                    person.setUnitName(groupUnit.getName());
                }
            }
            if ("职业体检".equals(person.getPhysicalType())) {
                //查询职业病和目标禁忌症
                TOrderGroup orderGroup = itOrderGroupService.getById(person.getGroupId());
                if (orderGroup != null) {
                    String comboId = orderGroup.getComboId();
                    String occupationalDiseases = "";
                    String occupationalTaboo = "";
                    if (comboId.contains(",")) {
                        String[] split = comboId.split(",");
                        List<TCombo> tCombos = itComboService.listByIds(Arrays.asList(split));

                        for (TCombo tCombo : tCombos) {
                            occupationalDiseases += "," + tCombo.getOccupationalDiseases();
                            occupationalTaboo += "," + tCombo.getOccupationalTaboo();
                        }
                        if (StringUtils.isNotBlank(occupationalDiseases)) {
                            occupationalDiseases = occupationalDiseases.substring(1);
                        }
                        if (StringUtils.isNotBlank(occupationalTaboo)) {
                            occupationalTaboo = occupationalTaboo.substring(1);
                        }
                    } else {
                        TCombo byId1 = itComboService.getById(comboId);
                        if (byId1 != null) {
                            occupationalDiseases = byId1.getOccupationalDiseases();
                            occupationalTaboo = byId1.getOccupationalTaboo();
                        }
                    }
                    person.setOccupationalDiseases(occupationalDiseases);
                    person.setOccupationalTaboo(occupationalTaboo);
                }
            }
            hashMap.put("personData", person);
            //关联查询分组项目
            QueryWrapper<TReviewProject> tReviewProjectQueryWrapper = new QueryWrapper<>();
            tReviewProjectQueryWrapper.eq("t_review_project.person_id", person.getId());
            tReviewProjectQueryWrapper.eq("t_review_project.del_flag", 0);
            if(passStatus.equals("未登记")){
                tReviewProjectQueryWrapper.eq("t_review_project.is_pass", 1);
            }else{
                tReviewProjectQueryWrapper.eq("t_review_project.is_pass", 2);
            }
//            tReviewProjectQueryWrapper.groupBy("t_review_project.portfolio_project_id");
            List<TReviewProject> list = itReviewProjectService.queryDataListByPersonId(tReviewProjectQueryWrapper);
            list.forEach(i -> i.setName(i.getPortfolioProjectName()));
            hashMap.put("projectData", list);
            return ResultUtil.data(hashMap);
        } else {
            return ResultUtil.error("未找到当前体检人员信息！");
        }
    }

    @ApiOperation("修改复查表人员的登记状态")
    @PostMapping("updateTGroupPersonReviewerByIsPass")
    public Result<Object> updateTGroupPersonReviewerByIsPass(@RequestBody TReviewProject tReviewProject) {
        if (StringUtils.isBlank(tReviewProject.getId())) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            TReviewProject byId = itReviewProjectService.getById(tReviewProject.getId());
            if (byId != null) {
                QueryWrapper<TReviewProject> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("person_id", byId.getPersonId());
                TReviewProject project = new TReviewProject();
                project.setIsPass(2);
                project.setRegistDate(new Date());
                boolean update = itReviewProjectService.update(project, queryWrapper);
                if (update) {
                    //更新人员复查状态
                    TGroupPerson byIdNew = itGroupPersonService.getById(tReviewProject.getPersonId());
                    if (byIdNew != null) {
                        byIdNew.setIsRecheck(1);
                        byIdNew.setUpdateTime(new Date());
                        byIdNew.setUpdateId(securityUtil.getCurrUser().getId());
                        //更新体检状态(复查登记后 状态更新到分诊环节 进行项目复查)
                        byIdNew.setIsPass(4);
                        byIdNew.setStatu(0);
                        byIdNew.setPrintState(0);
                        itGroupPersonService.updateById(byIdNew);
                    }else{
                        return ResultUtil.error("修改失败");
                    }
                    return ResultUtil.data(update, "修改成功");
                } else {
                    return ResultUtil.error("登记失败");
                }
            } else {
                return ResultUtil.error("登记失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("修改异常:" + e.getMessage());
        }
    }

    /**
     * 生成尿常规的6为随机数，当日不重复即可
     *
     * @return
     */
    private String genRandomBarcode() {
        String sixstr = RandomUtil.getSixstr();
        int number = itBarcodeService.checkBarcodeExists(sixstr);
        if (number > 0) {
            return genRandomBarcode();
        } else {
            return sixstr;
        }
    }


    /**
     * 是否能继续导入
     *
     * @params orderId
     * @params groupId
     */
    @ApiOperation("是否能继续导入")
    @GetMapping("isContinuImport")
    public Result<Object> isContinuImport(String orderId, String groupId) {
        if (StringUtils.isBlank(orderId) || StringUtils.isBlank(groupId)) {
            return ResultUtil.error("参数为空，请联系管理员！");
        }
        try {
            QueryWrapper<TGroupPerson> wrapper = new QueryWrapper<>();
            wrapper.eq("order_id", orderId);
            wrapper.eq("group_id", groupId);
            wrapper.eq("del_flag", 0);
            wrapper.ne("is_pass", 1);
            int count = tGroupPersonService.count(wrapper);
            if (count > 0) {
                return ResultUtil.data(false);
            } else {
                return ResultUtil.data(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常异常:" + e.getMessage());
        }
    }

    @ApiOperation("查询当前人员的检查项目")
    @GetMapping("queryProjectDataByGroupIdAndPersonId")
    public Result<Object> queryProjectDataByGroupIdAndPersonId(String groupId, String personId,Boolean isReviewer) {
        if (StringUtils.isBlank(groupId)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            if(isReviewer != null && isReviewer){
                //分组项目
                List<TOrderGroupItem> items = new ArrayList<>();
                //复查
                QueryWrapper<TReviewProject> reviewWrapper = new QueryWrapper<>();
                reviewWrapper.eq("person_id", personId);
                reviewWrapper.eq("group_id", groupId);
                reviewWrapper.eq("del_flag", 0);
                reviewWrapper.orderByAsc("office_id");
                reviewWrapper.orderByAsc("order_num");
                List<TReviewProject> list = itReviewProjectService.list(reviewWrapper);
                if (list.size() > 0) {
                    for (TReviewProject t : list) {
                        TOrderGroupItem orderGroupItem = new TOrderGroupItem();
                        orderGroupItem.setId(t.getId());
                        orderGroupItem.setName(t.getPortfolioProjectName());
                        orderGroupItem.setPortfolioProjectId(t.getPortfolioProjectId());
                        orderGroupItem.setGroupId(t.getGroupId());
                        orderGroupItem.setGroupOrderId(t.getGroupOrderId());
                        orderGroupItem.setOfficeId(t.getOfficeId());
                        orderGroupItem.setOfficeName(t.getOfficeName());
                        orderGroupItem.setTestNum(t.getTestNum());
                        items.add(orderGroupItem);
                    }
                }
                return ResultUtil.data(items);
            }else{
                QueryWrapper<TOrderGroupItem> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("group_id", groupId);
                queryWrapper.eq("del_flag", 0);
                queryWrapper.orderByAsc("project_type");
                queryWrapper.orderByAsc("office_id");
                queryWrapper.orderByAsc("order_num");
                //分组项目
                List<TOrderGroupItem> items = itOrderGroupItemService.list(queryWrapper);
                //复查
                QueryWrapper<TReviewProject> reviewWrapper = new QueryWrapper<>();
                reviewWrapper.eq("person_id", personId);
                reviewWrapper.eq("group_id", groupId);
                reviewWrapper.eq("del_flag", 0);
                reviewWrapper.orderByAsc("office_id");
                reviewWrapper.orderByAsc("order_num");
                List<TReviewProject> list = itReviewProjectService.list(reviewWrapper);
                if (list.size() > 0) {
                    for (TReviewProject t : list) {
                        TOrderGroupItem orderGroupItem = new TOrderGroupItem();
                        orderGroupItem.setId(t.getId());
                        orderGroupItem.setName(t.getPortfolioProjectName());
                        orderGroupItem.setPortfolioProjectId(t.getPortfolioProjectId());
                        orderGroupItem.setGroupId(t.getGroupId());
                        orderGroupItem.setGroupOrderId(t.getGroupOrderId());
                        orderGroupItem.setOfficeId(t.getOfficeId());
                        orderGroupItem.setOfficeName(t.getOfficeName());
                        orderGroupItem.setTestNum(t.getTestNum());
                        items.add(orderGroupItem);
                    }
                }
                return ResultUtil.data(items);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }


    @ApiOperation("获取行业类别编码,经济类型编码")
    public List<TProType> getTypeCodeByTProTypeList(String typeCode) {
        try {
            QueryWrapper<TProType> queryWrapper = new QueryWrapper<>();

//            queryWrapper.eq("parent_id", "0");
            queryWrapper.eq("del_flag", 0);
            List<TProType> list = proTypeService.list(queryWrapper);

            //递归设置子集
            recursion(list);
            List<TProType> children = list.stream().filter(ii->ii.getTypeCode().equals(typeCode)).collect(Collectors.toList());

            return children;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public String checkTypeCodeByTypeName(List<TProType> list, String typeName) {
        for (TProType tProType : list) {
            if (tProType.getTypeName().equals(typeName)) {
                return tProType.getTypeCode();
            } else {
                if (tProType.getChildren() != null && tProType.getChildren().size() > 0) {
                    String s = checkTypeCodeByTypeName(tProType.getChildren(), typeName);
                    if (StringUtils.isNotBlank(s)) {
                        return s;
                    }
                }
            }
        }
        return "";
    }

    @ApiOperation("保存或更新健康体检人员信息和体检项目信息")
    @PostMapping("/saveOrUpdatePersonInfo")
    @Transactional(rollbackOn = Exception.class)
    public Result<Object> saveOrUpdatePersonInfo(@RequestBody TGroupPerson tGroupPerson) {
        List<TOrderGroupItem> tOrderGroupItems = null;
        if (StringUtils.isBlank(tGroupPerson.getId())) {
            tGroupPerson.setIsWzCheck(1);//默认设置问诊科已检
            tGroupPerson.setIsPass(1);
            tGroupPerson.setDelFlag(0);
            tGroupPerson.setCreateTime(new Date());
            tGroupPerson.setCreateId(securityUtil.getCurrUser().getId());
            tGroupPerson.setTestNum(generatorNum(tGroupPerson.getPhysicalType()));
        } else {
            tGroupPerson.setUpdateTime(new Date());
            tGroupPerson.setUpdateId(securityUtil.getCurrUser().getId());
            QueryWrapper<TOrderGroupItem> groupItemQueryWrapper = new QueryWrapper<>();
            groupItemQueryWrapper.eq("group_order_id", tGroupPerson.getOrderId());
            groupItemQueryWrapper.eq("group_id", tGroupPerson.getGroupId());
            tOrderGroupItems = itOrderGroupItemService.list(groupItemQueryWrapper);
            //返回每个list的第一个值，
            List<String> itemIds = itOrderGroupItemService.listObjs(groupItemQueryWrapper, new Function<Object, String>() {
                @Override
                public String apply(Object o) {
                    return o.toString();
                }
            });

            //如果当前的分组id和原分组id不一样，则可以删除，否则不能删除
//            if (StringUtils.isNotBlank(tGroupPerson.getGroupId()) && !tGroupPerson.getGroupId().equals(tGroupPerson.getOldGroupId())) {
//                if (itemIds != null && itemIds.size() > 0) {
//                    //删除分组项目子项目
//                    QueryWrapper<TOrderGroupItemProject> groupItemProjectQueryWrapper = new QueryWrapper<>();
//                    groupItemProjectQueryWrapper.in("t_order_group_item_id", itemIds);
//                    itOrderGroupItemProjectService.remove(groupItemProjectQueryWrapper);
//                }
//                //删除分组项目
//                itOrderGroupItemService.remove(groupItemQueryWrapper);
//            }
        }
        List<String> groupItemProjetIds = new ArrayList<>();
        //拿出所有组合项目id
        if(tOrderGroupItems!=null && tOrderGroupItems.size() > 0){
            groupItemProjetIds = tOrderGroupItems.stream().map(TOrderGroupItem:: getPortfolioProjectId).collect(Collectors.toList());
        }
        tGroupPerson.setAvatar(null);
        if(StringUtils.isBlank(tGroupPerson.getOrderId()) ){
            String groupId = UUID.randomUUID().toString().replaceAll("-", "");
            tGroupPerson.setGroupId(groupId);
            tGroupPerson.setOrderId(groupId);
        }
        if(StringUtils.isBlank(tGroupPerson.getGroupId()) ){
            String groupId = UUID.randomUUID().toString().replaceAll("-", "");
            tGroupPerson.setGroupId(groupId);
        }
        tGroupPersonService.saveOrUpdate(tGroupPerson);
        //添加项目
        List<TOrderGroupItem> projectData = tGroupPerson.getProjectData();


        //所有组合项id
        List<String> finalGroupItemProjetIds = groupItemProjetIds;
        projectData.forEach(i -> {
            if(!finalGroupItemProjetIds.contains(i.getPortfolioProjectId())){
                i.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                i.setCreateTime(new Date());
                i.setCreateId(securityUtil.getCurrUser().getId());
                i.setDelFlag(0);
                i.setGroupId(tGroupPerson.getGroupId());
                i.setGroupOrderId(tGroupPerson.getOrderId());
                boolean save1 = itOrderGroupItemService.save(i);
                if (save1) {
                    //保存分组项目的子项目
                    ArrayList<String> list = iRelationBasePortfolioService.queryBaseProjectIdList(i.getPortfolioProjectId());
                    if (list != null && list.size() > 0) {
                        List<TBaseProject> tBaseProjects = itBaseProjectService.listByIds(list);
                        ArrayList<TOrderGroupItemProject> projectArrayList = new ArrayList<>();
                        for (TBaseProject tBaseProject : tBaseProjects) {
                            TOrderGroupItemProject tOrderGroupItemProject = new TOrderGroupItemProject();
                            tOrderGroupItemProject.setTOrderGroupItemId(i.getId());
                            tOrderGroupItemProject.setCode(tBaseProject.getCode());
                            tOrderGroupItemProject.setName(tBaseProject.getName());
                            tOrderGroupItemProject.setShortName(tBaseProject.getShortName());
                            tOrderGroupItemProject.setOrderNum(tBaseProject.getOrderNum());
                            tOrderGroupItemProject.setOfficeId(tBaseProject.getOfficeId());
                            tOrderGroupItemProject.setUnitCode(tBaseProject.getUnitCode());
                            tOrderGroupItemProject.setUnitName(tBaseProject.getUnitName());
                            tOrderGroupItemProject.setDefaultValue(tBaseProject.getDefaultValue());
                            tOrderGroupItemProject.setResultType(tBaseProject.getResultType());
                            tOrderGroupItemProject.setInConclusion(tBaseProject.getInConclusion());
                            tOrderGroupItemProject.setInReport(tBaseProject.getInReport());
                            tOrderGroupItemProject.setRelationCode(tBaseProject.getRelationCode());
                            tOrderGroupItemProject.setGroupOrderId(i.getGroupOrderId());
                            tOrderGroupItemProject.setBaseProjectId(tBaseProject.getId());
                            projectArrayList.add(tOrderGroupItemProject);
                        }
                        itOrderGroupItemProjectService.saveBatch(projectArrayList);
                    }
                }
            }
        });
        return ResultUtil.data("保存成功");
    }

    @ApiOperation("根据订单导出当前单位所有分组人员数据")
    @PostMapping("/exportPersonDataByUnitId")
    public void exportPersonDataByUnitId(String orderId, HttpServletResponse response) {
        List<TGroupPerson> list = tGroupPersonService.queryPersonDataListByOrderId(orderId);
        QueryWrapper<TOrderGroup> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_order_id", orderId);
        queryWrapper.select("id, name");
        List<TOrderGroup> groupList = itOrderGroupService.list(queryWrapper);

        XSSFWorkbook wb = new XSSFWorkbook();
        for (TOrderGroup tOrderGroup : groupList) {
            XSSFSheet sheet = wb.createSheet(tOrderGroup.getName().replace("[", "【").replace("]", "】"));
            sheet.setDefaultColumnWidth(20);
            XSSFRow row = sheet.createRow(0);
            List<TGroupPerson> personList = list.stream().filter(i -> tOrderGroup.getId().equals(i.getGroupId())).collect(Collectors.toList());
            XSSFCell cell = row.createCell(0);
            cell.setCellValue("序号");
            cell.setCellStyle(FileUtil.setHeadCellStyle(wb));
            cell = row.createCell(1);
            cell.setCellValue("体检编码");
            cell.setCellStyle(FileUtil.setHeadCellStyle(wb));
            cell = row.createCell(2);
            cell.setCellValue("姓名");
            cell.setCellStyle(FileUtil.setHeadCellStyle(wb));
            cell = row.createCell(3);
            cell.setCellValue("电话");
            cell.setCellStyle(FileUtil.setHeadCellStyle(wb));
            cell = row.createCell(4);
            cell.setCellValue("性别");
            cell.setCellStyle(FileUtil.setHeadCellStyle(wb));
            cell = row.createCell(5);
            cell.setCellValue("年龄");
            cell.setCellStyle(FileUtil.setHeadCellStyle(wb));
            cell = row.createCell(6);
            cell.setCellValue("工作单位");
            cell.setCellStyle(FileUtil.setHeadCellStyle(wb));
            cell = row.createCell(7);
            cell.setCellValue("体检时间");
            cell.setCellStyle(FileUtil.setHeadCellStyle(wb));
            cell = row.createCell(8);
            cell.setCellValue("在岗状态");
            cell.setCellStyle(FileUtil.setHeadCellStyle(wb));
            if (personList.size() > 0) {
                int index = 1;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                for (int i = 0; i < personList.size(); i++) {
                    TGroupPerson tGroupPerson = personList.get(i);
                    row = sheet.createRow(index);
                    cell = row.createCell(0);
                    cell.setCellValue(i + 1);
                    cell.setCellStyle(FileUtil.setCellStyle(wb));
                    cell = row.createCell(1);
                    cell.setCellValue(tGroupPerson.getTestNum());
                    cell.setCellStyle(FileUtil.setCellStyle(wb));
                    cell = row.createCell(2);
                    cell.setCellValue(tGroupPerson.getPersonName());
                    cell.setCellStyle(FileUtil.setCellStyle(wb));
                    cell = row.createCell(3);
                    cell.setCellValue(tGroupPerson.getMobile());
                    cell.setCellStyle(FileUtil.setCellStyle(wb));
                    cell = row.createCell(4);
                    cell.setCellValue(tGroupPerson.getSex());
                    cell.setCellStyle(FileUtil.setCellStyle(wb));
                    cell = row.createCell(5);
                    cell.setCellValue(tGroupPerson.getAge());
                    cell.setCellStyle(FileUtil.setCellStyle(wb));
                    cell = row.createCell(6);
                    cell.setCellValue(tGroupPerson.getDept());
                    cell.setCellStyle(FileUtil.setCellStyle(wb));
                    cell = row.createCell(7);
                    cell.setCellValue(simpleDateFormat.format(tGroupPerson.getRegistDate()));
                    cell.setCellStyle(FileUtil.setCellStyle(wb));
                    cell = row.createCell(8);
                    cell.setCellValue(tGroupPerson.getWorkStateText());
                    cell.setCellStyle(FileUtil.setCellStyle(wb));
                    index += 1;
                }
            }
        }
        try {
            String fileName = "人员数据";
            response.setHeader("content-Type", "application/ms-excel");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ";filename*=utf-8''" + fileName);
            OutputStream out = response.getOutputStream();
            wb.write(out);
            IoUtil.close(out);
        } catch (Exception var15) {
            var15.printStackTrace();
        }
    }

    /**
     * 功能描述：导入数据
     * <p>
     * 根据不同的体检类型，导入模板不一样
     *
     * @return
     */
    @ApiOperation("导入数据")
    @PostMapping("/importExcelBatch")
    @Transactional(rollbackOn = Exception.class)
    public Result<Object> importExcelBatch(@RequestBody Map map  )  {
        try {
            if(map == null || !map.containsKey("orderId")||map.get("orderId")==null || !map.containsKey("personInfoList")||map.get("personInfoList")==null){
                return ResultUtil.error("参数有误请联系管理员！");
            }
            String orderId = map.get("orderId").toString();

            List<ImportPersonEntity> readData = JSON.parseArray(JSON.toJSONString(map.get("personInfoList")),ImportPersonEntity.class);
            TGroupOrder groupOrder = groupOrderService.getById(orderId);
            if (groupOrder == null || groupOrder.getDelFlag() != 0) {
                return ResultUtil.error("订单信息不存在！");
            }
            List<ImportPersonEntity> resList = new ArrayList();
            String regularExpression = "(^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)|" +
                    "(^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}$)";
            if ("职业体检".equals(groupOrder.getPhysicalType()) || "放射体检".equals(groupOrder.getPhysicalType())) {
                //读取一个对象的信息
                if (readData != null && readData.size() > 0) {

                    //查询出危害因素、工种、在岗状态的数据，匹配编码
                    List<TProType> type_001 = getTypeCodeByTProTypeList("TYPE_001");//危害因素
                    List<DictData> dictDataList = dictDataService.findByDictId("1447871138549075969");//在岗状态
                    List<DictData> workDataList = dictDataService.findByDictId("1456812385326206976");//工种名称
                    List<TCombo> comboList = itComboService.list();
                    //保存订单分组信息
                    List<RelationBasePortfolio> relationBasePortfolioList = iRelationBasePortfolioService.list();
                    List<TBaseProject> tBaseProjectList = itBaseProjectService.list();
                    for (int i = 0; i < readData.size(); i++) {
                        StringBuilder errorTxt = new StringBuilder();
                        ImportPersonEntity readDatum = readData.get(i);
                        List<TCombo> itemComboList = new ArrayList<>();

                        //判断对象属性值是否都不为空
                        TGroupPerson person = new TGroupPerson();
                        if(StringUtils.isBlank(readDatum.getPersonName())){
                            errorTxt.append("人员姓名为空\n") ;
                        }
                        else{
                            person.setPersonName(readDatum.getPersonName());
                        }
                        if(StringUtils.isNotBlank(readDatum.getIdCard())){
                            if (  !readDatum.getIdCard().matches(regularExpression)) {
                                errorTxt.append("身份证格式不正确\n") ;
                            }
                            else{

                                person.setIdCard(readDatum.getIdCard());
                                /*基数为男 偶数为女*/
                                if(readDatum.getIdCard() != null && StringUtils.isNotBlank(readDatum.getIdCard())){
                                    if (Integer.parseInt(readDatum.getIdCard().substring(16, 17)) % 2 == 0) {
                                        person.setSex("女");
                                    } else {
                                        person.setSex("男");
                                    }
                                }
                                person.setAge(getAgeForIdcard(readDatum.getIdCard()));
                                person.setBirth(getBirthdayForIdcard(readDatum.getIdCard()));
                            }
                        }

                        if(StringUtils.isBlank(readDatum.getWorkStateText())){
                            errorTxt.append("在岗状态名称为空\n") ;
                        }
                        else {
                            DictData dictData = dictDataList.stream().filter(e -> e.getTitle().equals(readDatum.getWorkStateText().trim())).findFirst().orElse(null);
                            if (dictData == null) {
                                errorTxt.append("在岗状态名称 “" + readDatum.getWorkStateText() + "” 不符合规范\n") ;
                            }
                            person.setWorkStateCode(dictData.getValue());
                            person.setWorkStateText(readDatum.getWorkStateText().trim());
                        }
                        if("放射体检".equals(groupOrder.getPhysicalType())){
                            readDatum.setHazardFactorsText("放射工作");
                        }
                        if(StringUtils.isBlank(readDatum.getHazardFactorsText())){
                            errorTxt.append("危害因素为空\n") ;
                        }
                        else{
                            //匹配危害因素，多个|隔开
                            String[] split = readDatum.getHazardFactorsText().split("\\|");
                            String codes = "";
                            Boolean isTrueHazardFactors = true;
                            for (String s : split) {
                                String s1 = checkTypeCodeByTypeName(type_001, s);
                                if (StringUtils.isBlank(s1) && "职业体检".equals(groupOrder.getPhysicalType())) {
                                    errorTxt.append("危害因素名称 “").append(readDatum.getHazardFactorsText()).append("” 不符合规范\n");
                                }
                                else{
                                    if(StringUtils.isNotBlank(readDatum.getWorkStateText()) && StringUtils.isNotBlank(s1)){
                                        TCombo combo = comboList.stream().filter(e -> e.getHazardFactors().equals(s1) && e.getCareerStage().equals(readDatum.getWorkStateText().trim())).findFirst().orElse(null);
                                        if(combo==null){
                                            errorTxt.append("危害因素名称 “").append(readDatum.getHazardFactorsText()).append("” 没有套餐请改为其他危害因素或者在岗状态\n");
                                            isTrueHazardFactors = false;
                                        }
                                        else{
                                            itemComboList.add(combo);
                                        }
                                    }
                                    else if(StringUtils.isNotBlank(readDatum.getWorkStateText())){
                                        TCombo combo = comboList.stream().filter(e -> e.getHazardFactorsText().equals(readDatum.getHazardFactorsText()) && e.getCareerStage().equals(readDatum.getWorkStateText().trim())).findFirst().orElse(null);
                                        if(combo==null){
                                            errorTxt.append("危害因素名称 “").append(readDatum.getHazardFactorsText()).append("” 没有套餐请改为其他危害因素或者在岗状态\n");
                                            isTrueHazardFactors = false;
                                        }
                                        else{
                                            itemComboList.add(combo);
                                        }
                                    }
                                }
                                codes += "|" + s1;
                            }
                            if(isTrueHazardFactors){
                                codes = codes.substring(1);
                                person.setHazardFactors(codes);
                                person.setHazardFactorsText(readDatum.getHazardFactorsText());
                                if("放射体检".equals(groupOrder.getPhysicalType())){
                                    person.setHazardFactorsText("放射工作");
                                    person.setHazardFactors("160999");
                                }
                                //如果危害因素是其他，需要填写其他危害因素
                                if ((codes.contains("110999") || codes.contains("120999") || codes.contains("130999")
                                        || codes.contains("140999") || codes.contains("150999") || codes.contains("160999")) && StringUtils.isBlank(readDatum.getOtherHazardFactors())) {
                                    errorTxt.append("危害因素名称为其他时，其他危害因素名称不能为空\n") ;
                                }
                                person.setOtherHazardFactors(readDatum.getOtherHazardFactors());
                            }
                            else{
                                itemComboList = new ArrayList<>();
                            }

                        }

                        String groupId = UUID.randomUUID().toString().replaceAll("-", "");

                        if(StringUtils.isNotBlank(person.getHazardFactors()) && itemComboList.size()>0){
                            String groupName = readDatum.getHazardFactorsText().replaceAll("\\|","、") + "[" + readDatum.getWorkStateText() + "]";
                            QueryWrapper<TOrderGroup> groupWrapper = new QueryWrapper<>();
                            groupWrapper.eq("name", groupName);
                            groupWrapper.eq("del_flag", 0);
                            groupWrapper.eq("group_order_id", orderId);
                            List<TOrderGroup> list = itOrderGroupService.list(groupWrapper);
                            if(list!=null && list.size()>0){
                                groupId = list.get(0).getId();
                            }
                            //保存分组
                            else{
                                List<String> courseIds=  itemComboList.stream().map(TCombo::getId).collect(Collectors.toList());
                                String comboId = courseIds.stream().collect(Collectors.joining(","));
                                TOrderGroup orderGroup = new TOrderGroup();
                                orderGroup.setGroupOrderId(orderId);
                                orderGroup.setId(groupId);
                                orderGroup.setCreateId(securityUtil.getCurrUser().getId());
                                orderGroup.setCreateTime(new Date());
                                orderGroup.setDelFlag(0);
                                orderGroup.setComboId(comboId);
                                orderGroup.setDiscount(100);
                                orderGroup.setAddDiscount(100);
                                orderGroup.setPersonCount(100);
                                orderGroup.setName(groupName);
                                boolean save = itOrderGroupService.save(orderGroup);
                                if(save){
                                    //保存分组项目
                                    QueryWrapper<TComboItem> queryWrapper = new QueryWrapper<>();
                                    queryWrapper.in("combo_id", courseIds);
                                    queryWrapper.groupBy("portfolio_project_id");
                                    queryWrapper.orderByAsc("name");
                                    List<TComboItem> comboItemList= tComboItemService.listByComboIds(queryWrapper);
                                    if(comboItemList != null && comboItemList.size()>0){
                                        for (int j = 0; j < comboItemList.size(); j++) {
                                            TComboItem tComboItem = comboItemList.get(j);
                                            if(tComboItem!=null && StringUtils.isNotBlank(tComboItem.getId())){
                                                TOrderGroupItem projectDatum  = new TOrderGroupItem();
                                                //复制属性
                                                BeanUtils.copyProperties(tComboItem,projectDatum);
                                                projectDatum.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                                                projectDatum.setCreateId(securityUtil.getCurrUser().getId());
                                                projectDatum.setCreateTime(new Date());
                                                projectDatum.setDelFlag(0);
                                                projectDatum.setGroupId(groupId);
                                                projectDatum.setGroupOrderId(orderId);
                                                projectDatum.setStatus(0);
                                                //保存分组项目
                                                boolean save1 = itOrderGroupItemService.save(projectDatum);
                                                if (save1) {
                                                    //保存分组项目的子项目
                                                    List<RelationBasePortfolio> collect = relationBasePortfolioList.stream()
                                                            .filter(item -> item.getPortfolioProjectId().equals(projectDatum.getPortfolioProjectId()))
                                                            .collect(Collectors.toList());
                                                    ArrayList<TOrderGroupItemProject> projectArrayList = new ArrayList<>();
                                                    for (RelationBasePortfolio relationBasePortfolio : collect) {
                                                        TBaseProject tBaseProject = tBaseProjectList.stream().filter(item -> item.getId().equals(relationBasePortfolio.getBaseProjectId())).findFirst().orElse(null);
                                                        if(tBaseProject != null) {
                                                            List<TOrderGroupItemProject> collect1 =
                                                                    projectArrayList.stream().filter(item -> item.getTOrderGroupItemId().equals(projectDatum.getId())
                                                                            && item.getBaseProjectId().equals(tBaseProject.getId())).collect(Collectors.toList());
                                                            if (collect1.size() > 0) {
                                                                continue;
                                                            }
                                                            TOrderGroupItemProject tOrderGroupItemProject = new TOrderGroupItemProject();
                                                            tOrderGroupItemProject.setTOrderGroupItemId(projectDatum.getId());
                                                            tOrderGroupItemProject.setCode(tBaseProject.getCode());
                                                            tOrderGroupItemProject.setName(tBaseProject.getName());
                                                            tOrderGroupItemProject.setShortName(tBaseProject.getShortName());
                                                            tOrderGroupItemProject.setOrderNum(tBaseProject.getOrderNum());
                                                            tOrderGroupItemProject.setOfficeId(tBaseProject.getOfficeId());
                                                            tOrderGroupItemProject.setUnitCode(tBaseProject.getUnitCode());
                                                            tOrderGroupItemProject.setUnitName(tBaseProject.getUnitName());
                                                            tOrderGroupItemProject.setDefaultValue(tBaseProject.getDefaultValue());
                                                            tOrderGroupItemProject.setResultType(tBaseProject.getResultType());
                                                            tOrderGroupItemProject.setInConclusion(tBaseProject.getInConclusion());
                                                            tOrderGroupItemProject.setInReport(tBaseProject.getInReport());
                                                            tOrderGroupItemProject.setRelationCode(tBaseProject.getRelationCode());
                                                            tOrderGroupItemProject.setGroupOrderId(orderId);
                                                            tOrderGroupItemProject.setBaseProjectId(tBaseProject.getId());
                                                            projectArrayList.add(tOrderGroupItemProject);
                                                        }
                                                    }
                                                    itOrderGroupItemProjectService.saveBatch(projectArrayList);
                                                }

                                            }
                                        }
                                    }
                                }
                            }

                        }
                        //同一个分组下边是否有多个相同的身份证
                        QueryWrapper<TGroupPerson> personQueryWrapper = new QueryWrapper<>();
                        personQueryWrapper.eq("id_card", readDatum.getIdCard());
                        personQueryWrapper.eq("del_flag", 0);
                        personQueryWrapper.eq("group_id", groupId);
                        personQueryWrapper.eq("order_id", orderId);
                        int  count = tGroupPersonService.count(personQueryWrapper);
                        if (count > 0) {
                            errorTxt.append("已导入身份证为").append(readDatum.getIdCard()).append("的体检人员信息\n");
                        }
                        person.setMobile(readDatum.getMobile());
                        if(StringUtils.isBlank(readDatum.getWorkTypeText())){
                            errorTxt.append("工种代码名称为空\n") ;
                        }
                        else{
                            DictData dictData = workDataList.stream().filter(e -> e.getTitle().equals(readDatum.getWorkTypeText())).findFirst().orElse(null);
                            if (dictData == null) {
                                errorTxt.append("工种名称 “" + readDatum.getWorkTypeText() + "” 不符合规范\n") ;
                            }
                            else{
                                //如果工种是其他，其他工种名称必填
                                if (("0014".equals(dictData.getValue()) || "0033".equals(dictData.getValue()) || "999999".equals(dictData.getValue())) && StringUtils.isBlank(readDatum.getWorkName())) {
                                    errorTxt.append("工种名称为其他时，其他工种名称不能为空！\n") ;
                                }
                                person.setWorkTypeCode(dictData.getValue());
                                person.setWorkTypeText(readDatum.getWorkTypeText());
                                person.setWorkName(readDatum.getWorkName());
                            }

                        }
                        //有错误信息不保存
                        if(StringUtils.isNotBlank(errorTxt)){
                            readDatum.setErrorTxt(errorTxt.toString());
                            resList.add(readDatum);
                            continue;
                        }
                        person.setTestNum(generatorNum(groupOrder.getPhysicalType()));
                        person.setOrderId(orderId);
                        person.setGroupId(groupId);
                        person.setPhysicalType(groupOrder.getPhysicalType());
                        person.setCreateId(securityUtil.getCurrUser().getId());
                        person.setCreateTime(new Date());
                        person.setDelFlag(0);
                        person.setIsPass(1);
                        person.setIsCheck(0);
                        person.setIsRecheck(0);
                        person.setIsWzCheck(0);
                        person.setReportPrintingNum(0);
                        person.setUnitId(groupOrder.getGroupUnitId());
                        person.setDept(groupOrder.getGroupUnitName());
                        person.setJcType("1");
                        person.setSporadicPhysical(groupOrder.getSporadicPhysical());
                        tGroupPersonService.save(person);
                    }

                    return ResultUtil.data(resList);
                } else {
                    return ResultUtil.error("批量导入失败，数据为空！");
                }
            } else if ("健康体检".equals(groupOrder.getPhysicalType())) {
                //读取一个对象的信息
                if (readData != null && readData.size() > 0) {
                    for (int i = 0; i < readData.size(); i++) {
                        ImportPersonEntity readDatum = readData.get(i);
                        //判断对象属性值是否都不为空
                        String groupId = UUID.randomUUID().toString().replaceAll("-", "");
                        TGroupPerson person = new TGroupPerson();
                        StringBuilder errorTxt = new StringBuilder();
                        if(StringUtils.isBlank(readDatum.getPersonName())){
                            errorTxt.append( "人员姓名为空\n");
                        }
                        else{
                            person.setPersonName(readDatum.getPersonName());
                        }
                        if(StringUtils.isNotBlank(readDatum.getIdCard())){
                            if (  !readDatum.getIdCard().matches(regularExpression)) {
                                errorTxt .append( "身份证格式不正确\n");
                            }
                            else{

                                person.setIdCard(readDatum.getIdCard());
                                /*基数为男 偶数为女*/
                                if(readDatum.getIdCard() != null && StringUtils.isNotBlank(readDatum.getIdCard())){
                                    if (Integer.parseInt(readDatum.getIdCard().substring(16, 17)) % 2 == 0) {
                                        person.setSex("女");
                                    } else {
                                        person.setSex("男");
                                    }
                                }
                                person.setAge(getAgeForIdcard(readDatum.getIdCard()));
                                person.setBirth(getBirthdayForIdcard(readDatum.getIdCard()));
                            }
                        }

                        person.setTestNum(generatorNum(groupOrder.getPhysicalType()));
                        person.setMobile(readDatum.getMobile());
                        person.setPersonName(readDatum.getPersonName());
                        /*基数为男 偶数为女*/
                        if(readDatum.getIdCard() != null && StringUtils.isNotBlank(readDatum.getIdCard())){
                            if (Integer.parseInt(readDatum.getIdCard().substring(16, 17)) % 2 == 0) {
                                person.setSex("女");
                            } else {
                                person.setSex("男");
                            }
                        }
                        if(map.containsKey("groupId") && map.get("groupId")!=null && StringUtils.isNotBlank(map.get("groupId").toString())){
                            String groupIdStr = map.get("groupId").toString();
                            if(StringUtils.isNotBlank(groupIdStr)){
                                groupId = groupIdStr;
                            }
                        }
                        //健康体检默认分了男女
                        else if(StringUtils.isNotBlank(person.getSex()) ) {
                            String groupName = person.getSex();
                            QueryWrapper<TOrderGroup> groupWrapper = new QueryWrapper<>();
                            groupWrapper.eq("name", groupName);
                            groupWrapper.eq("del_flag", 0);
                            groupWrapper.eq("group_order_id", orderId);
                            List<TOrderGroup> list = itOrderGroupService.list(groupWrapper);
                            if (list != null && list.size() > 0) {
                                groupId = list.get(0).getId();
                            }
                        }
                        //同一个分组下边是否有多个相同的身份证
                        QueryWrapper<TGroupPerson> personQueryWrapper = new QueryWrapper<>();
                        personQueryWrapper.eq("id_card", readDatum.getIdCard());
                        personQueryWrapper.eq("del_flag", 0);
                        personQueryWrapper.eq("group_id", groupId);
                        personQueryWrapper.eq("order_id", orderId);
                        int  count = tGroupPersonService.count(personQueryWrapper);
                        if (count > 0) {
                            errorTxt.append("已导入身份证为").append(readDatum.getIdCard()).append("的体检人员信息\n");
                        }
                        //有错误信息不保存
                        if(StringUtils.isNotBlank(errorTxt)){
                            readDatum.setErrorTxt(errorTxt.toString());
                            resList.add(readDatum);
                            continue;
                        }
                        person.setOrderId(orderId);
                        person.setGroupId(groupId);
                        person.setPhysicalType(groupOrder.getPhysicalType());
                        person.setCreateId(securityUtil.getCurrUser().getId());
                        person.setCreateTime(new Date());
                        person.setDelFlag(0);
                        person.setIsPass(1);
                        person.setIsCheck(0);
                        person.setIsRecheck(0);
                        person.setIsWzCheck(1);
                        person.setReportPrintingNum(0);
                        person.setUnitId(groupOrder.getGroupUnitId());
                        person.setDept(groupOrder.getGroupUnitName());
                        person.setOldGroupId(groupId);
                        tGroupPersonService.save(person);
                    }
                    return ResultUtil.data(resList);
                } else {
                    return ResultUtil.error("批量导入失败，数据为空！");
                }
            } else {
                return ResultUtil.error("体检类型不明确！");
            }
        }
        catch (Exception e){
            e.printStackTrace();
            //手工回滚异常
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultUtil.error("导入失败！"+e.getMessage());
        }
    }

    /**
     * 功能描述：查询用户的体检报告(人员信息、检查项目)
     *
     * @param pageVo   分页参数
     * @return 返回获取结果
     */
    @SystemLog(description = "查询用户的体检报告(人员信息、检查项目)", type = LogType.OPERATION)
    @ApiOperation("查询用户的体检报告(人员信息、检查项目)")
    @GetMapping("queryTGroupPersonAndResultList")
    public Result<Object> queryTGroupPersonAndResultList(TGroupPerson tGroupPerson, PageVo pageVo) {
        try {
            IPage<TGroupPerson> result = tGroupPersonService.queryTGroupPersonAndResultList(tGroupPerson, pageVo);
            for(TGroupPerson resultItem: result.getRecords()){
                QueryWrapper<TDepartResult> tDepartResultQueryWrapper = new QueryWrapper<>();
                tDepartResultQueryWrapper.eq("del_flag",0);
                tDepartResultQueryWrapper.eq("person_id",resultItem.getId());
                List<TDepartResult> list = tDepartResultService.list(tDepartResultQueryWrapper);
                String groupItemNames = "";
                for(TDepartResult tDepartResult : list){
                    if(groupItemNames != null && groupItemNames.trim().length() > 0){
                        groupItemNames += "," + tDepartResult.getGroupItemName();
                    }else{
                        groupItemNames += tDepartResult.getGroupItemName();
                    }
                }
                resultItem.setGroupItemNames(groupItemNames);
            }
            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：查询用户的体检报告(人员信息、检查项目)
     *
     * @param pageVo   分页参数
     * @return 返回获取结果
     */
    @SystemLog(description = "查询用户的个人预约信息(人员信息、检查项目)", type = LogType.OPERATION)
    @ApiOperation("查询用户的个人预约信息(人员信息、检查项目)")
    @GetMapping("queryTGroupPersonAppList")
    public Result<Object> queryTGroupPersonAppList(TGroupPerson tGroupPerson, PageVo pageVo) {
        try {
            IPage<TGroupPerson> result = tGroupPersonService.queryTGroupPersonAndResultAppList(tGroupPerson, pageVo);
            for(TGroupPerson resultItem: result.getRecords()){
                QueryWrapper<TOrderGroupItem> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("group_id", resultItem.getGroupId());
                queryWrapper.eq("del_flag", 0);
                queryWrapper.orderByAsc("project_type");
                queryWrapper.orderByAsc("office_id");
                queryWrapper.orderByAsc("order_num");
                //分组项目
                List<TOrderGroupItem> list = itOrderGroupItemService.list(queryWrapper);
                String groupItemNames = "";
                for(TOrderGroupItem tOrderGroupItem : list){
                    if(groupItemNames != null && groupItemNames.trim().length() > 0){
                        groupItemNames += "," + tOrderGroupItem.getName();
                    }else{
                        groupItemNames += tOrderGroupItem.getName();
                    }
                }
                resultItem.setGroupItemNames(groupItemNames);
            }
            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：查询用户的体检报告(总检记录、组合项结果)
     *
     * @return 返回获取结果
     */
    @SystemLog(description = "查询用户的体检报告(总检记录、组合项结果)", type = LogType.OPERATION)
    @ApiOperation("查询用户的体检报告(总检记录、组合项结果)")
    @PostMapping("getTInspectRecordAndGroupItemResult")
    public Result<Object> getTInspectRecordAndGroupItemResult(@RequestBody TGroupPerson tGroupPerson) {
        try {
            HashMap<String, Object> result = new HashMap<>();
            TInspectionRecord inspectionRecords = tInspectionRecordService.getByPersonId(tGroupPerson.getId());
            QueryWrapper<TDepartResult> tDepartResultQueryWrapper = new QueryWrapper<>();
            tDepartResultQueryWrapper.eq("person_id",tGroupPerson.getId());
            tDepartResultQueryWrapper.eq("del_flag",0);
            tDepartResultQueryWrapper.orderByDesc("create_date");
            tDepartResultQueryWrapper.groupBy("group_item_id");
            List<TDepartResult> tDepartResults = departResultService.list(tDepartResultQueryWrapper);
            result.put("inspectData", inspectionRecords);
            result.put("departResults", tDepartResults);
            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：查询用户的体检报告(组合项、基础项 详细检查结果)
     *
     * @return 返回获取结果
     */
    @SystemLog(description = "查询用户的体检报告(组合项、基础项 详细检查结果)", type = LogType.OPERATION)
    @ApiOperation("查询用户的体检报告(组合项、基础项 详细检查结果)")
    @PostMapping("getDepartResultAndItemResult")
    public Result<Object> getDepartResultAndItemResult(@RequestBody TDepartResult tDepartResult) {
        try {
            HashMap<String, Object> result = new HashMap<>();
            QueryWrapper<TDepartResult> tDepartResultQueryWrapper = new QueryWrapper<>();
            tDepartResultQueryWrapper.eq("person_id",tDepartResult.getPersonId());
            tDepartResultQueryWrapper.eq("group_item_id",tDepartResult.getGroupItemId());
            tDepartResultQueryWrapper.eq("del_flag",0);
            tDepartResultQueryWrapper.groupBy("group_item_id");
            TDepartResult tDepartResultData = departResultService.getOne(tDepartResultQueryWrapper);
            QueryWrapper<TDepartItemResult> tDepartItemResultQueryWrapper = new QueryWrapper<>();
            tDepartItemResultQueryWrapper.eq("person_id",tDepartResult.getPersonId());
            tDepartItemResultQueryWrapper.eq("order_group_item_id",tDepartResult.getGroupItemId());
            tDepartItemResultQueryWrapper.eq("del_flag",0);
            tDepartItemResultQueryWrapper.groupBy("order_group_item_project_id");
            tDepartItemResultQueryWrapper.orderByAsc("order_num");
            List<TDepartItemResult> tDepartItemResults = tDepartItemResultService.list(tDepartItemResultQueryWrapper);
            result.put("departResult", tDepartResultData);
            result.put("departItemResult", tDepartItemResults);
            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }


    @ApiOperation("通过app端保存或更新健康体检人员信息和体检项目信息")
    @PostMapping("/saveOrUpdatePersonInfoByApp")
    @Transactional(rollbackOn = Exception.class)
    public Result<Object> saveOrUpdatePersonInfoByApp(@RequestBody TGroupPerson tGroupPerson) {
        List<TOrderGroupItem> tOrderGroupItems = null;
        if (StringUtils.isBlank(tGroupPerson.getId())) {
            tGroupPerson.setIsWzCheck(1);//默认设置问诊科已检
            tGroupPerson.setIsPass(1);
            tGroupPerson.setDelFlag(0);
            tGroupPerson.setCreateTime(new Date());
            tGroupPerson.setCreateId(securityUtil.getCurrUser().getId());
            tGroupPerson.setTestNum(generatorNum(tGroupPerson.getPhysicalType()));
        } else {
            tGroupPerson.setUpdateTime(new Date());
            tGroupPerson.setUpdateId(securityUtil.getCurrUser().getId());
            QueryWrapper<TOrderGroupItem> groupItemQueryWrapper = new QueryWrapper<>();
            groupItemQueryWrapper.eq("group_order_id", tGroupPerson.getOrderId());
            groupItemQueryWrapper.eq("group_id", tGroupPerson.getGroupId());
            tOrderGroupItems = itOrderGroupItemService.list(groupItemQueryWrapper);
            //返回每个list的第一个值，
            List<String> itemIds = itOrderGroupItemService.listObjs(groupItemQueryWrapper, new Function<Object, String>() {
                @Override
                public String apply(Object o) {
                    return o.toString();
                }
            });

            //如果当前的分组id和原分组id不一样，则可以删除，否则不能删除
//            if (StringUtils.isNotBlank(tGroupPerson.getGroupId()) && !tGroupPerson.getGroupId().equals(tGroupPerson.getOldGroupId())) {
//                if (itemIds != null && itemIds.size() > 0) {
//                    //删除分组项目子项目
//                    QueryWrapper<TOrderGroupItemProject> groupItemProjectQueryWrapper = new QueryWrapper<>();
//                    groupItemProjectQueryWrapper.in("t_order_group_item_id", itemIds);
//                    itOrderGroupItemProjectService.remove(groupItemProjectQueryWrapper);
//                }
//                //删除分组项目
//                itOrderGroupItemService.remove(groupItemQueryWrapper);
//            }
        }
        List<String> groupItemProjetIds = new ArrayList<>();
        //拿出所有组合项目id
        if(tOrderGroupItems!=null && tOrderGroupItems.size() > 0){
            groupItemProjetIds = tOrderGroupItems.stream().map(TOrderGroupItem:: getPortfolioProjectId).collect(Collectors.toList());
        }
        tGroupPerson.setAvatar(null);
        if(StringUtils.isBlank(tGroupPerson.getOrderId()) ){
            String groupId = UUID.randomUUID().toString().replaceAll("-", "");
            tGroupPerson.setGroupId(groupId);
            tGroupPerson.setOrderId(groupId);
        }
        if(StringUtils.isBlank(tGroupPerson.getGroupId()) ){
            String groupId = UUID.randomUUID().toString().replaceAll("-", "");
            tGroupPerson.setGroupId(groupId);
        }
        tGroupPersonService.saveOrUpdate(tGroupPerson);
        //添加项目
       // List<TOrderGroupItem> projectData = tGroupPerson.getProjectData();
        Set<TOrderGroupItem> projectData= (Set<TOrderGroupItem>) tGroupPerson.getProjectData();
        for (TOrderGroupItem projectDatum : projectData) {
            System.out.println(projectDatum);
        }
        //所有组合项id
        List<String> finalGroupItemProjetIds = groupItemProjetIds;
        projectData.forEach(i -> {
            if(!finalGroupItemProjetIds.contains(i.getPortfolioProjectId())){
                i.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                i.setCreateTime(new Date());
                i.setCreateId(securityUtil.getCurrUser().getId());
                i.setDelFlag(0);
                i.setGroupId(tGroupPerson.getGroupId());
                i.setGroupOrderId(tGroupPerson.getOrderId());
                boolean save1 = itOrderGroupItemService.save(i);
                if (save1) {
                    //保存分组项目的子项目
                    ArrayList<String> list = iRelationBasePortfolioService.queryBaseProjectIdList(i.getPortfolioProjectId());
                    if (list != null && list.size() > 0) {
                        List<TBaseProject> tBaseProjects = itBaseProjectService.listByIds(list);
                        ArrayList<TOrderGroupItemProject> projectArrayList = new ArrayList<>();
                        for (TBaseProject tBaseProject : tBaseProjects) {
                            TOrderGroupItemProject tOrderGroupItemProject = new TOrderGroupItemProject();
                            tOrderGroupItemProject.setTOrderGroupItemId(i.getId());
                            tOrderGroupItemProject.setCode(tBaseProject.getCode());
                            tOrderGroupItemProject.setName(tBaseProject.getName());
                            tOrderGroupItemProject.setShortName(tBaseProject.getShortName());
                            tOrderGroupItemProject.setOrderNum(tBaseProject.getOrderNum());
                            tOrderGroupItemProject.setOfficeId(tBaseProject.getOfficeId());
                            tOrderGroupItemProject.setUnitCode(tBaseProject.getUnitCode());
                            tOrderGroupItemProject.setUnitName(tBaseProject.getUnitName());
                            tOrderGroupItemProject.setDefaultValue(tBaseProject.getDefaultValue());
                            tOrderGroupItemProject.setResultType(tBaseProject.getResultType());
                            tOrderGroupItemProject.setInConclusion(tBaseProject.getInConclusion());
                            tOrderGroupItemProject.setInReport(tBaseProject.getInReport());
                            tOrderGroupItemProject.setRelationCode(tBaseProject.getRelationCode());
                            tOrderGroupItemProject.setGroupOrderId(i.getGroupOrderId());
                            tOrderGroupItemProject.setBaseProjectId(tBaseProject.getId());
                            projectArrayList.add(tOrderGroupItemProject);
                        }
                        itOrderGroupItemProjectService.saveBatch(projectArrayList);
                    }
                }
            }
        });
        return ResultUtil.data("保存成功");
    }

    @ApiOperation("通过app端查询已经总检的人数")
    @GetMapping("/findPersonCountByApp")
    @Transactional(rollbackOn = Exception.class)
    public Result<Object> findPersonCountByApp(Integer isPass) {
       // TGroupPerson tGroupPerson=new TGroupPerson();
        QueryWrapper<TGroupPerson> queryWrapper=new QueryWrapper<TGroupPerson>();
        queryWrapper.gt("t_group_person.is_pass",isPass);
        int count = itGroupPersonService.count(queryWrapper);
        return ResultUtil.data(count);
    }
}
